using Inignoto.Utilities;
using Inignoto.Tiles.Data;
using Inignoto.Tiles;
using System;
using Inignoto.Graphics.Mesh;
using Inignoto.Graphics.World;
using Inignoto.Math;
using Microsoft.Xna.Framework;
using System.Collections.Generic;
using Inignoto.Graphics.Gui;
using System.Transactions;
using System.IO;
using static Inignoto.World.World;
using System.Threading.Tasks;
using System.Runtime.Serialization.Formatters.Binary;

namespace Inignoto.World.Chunks
{
    public class Chunk : IComparable<Chunk>
    {

        public List<short> sunlightBfsQueue;
        public List<short> sunlightRemovalBfsQueue;
        public List<short> redRemovalBfsQueue;
        public List<short> greenRemovalBfsQueue;
        public List<short> blueRemovalBfsQueue;
        public List<short> redBfsQueue;
        public List<short> greenBfsQueue;
        public List<short> blueBfsQueue;

        public struct Voxel
        {
            public TileData voxel { get => GetVoxel(); set => SetVoxel(value); }
            public TileData overlay { get => GetOverlayVoxel(); set => SetOverlayVoxel(value); }

            private int true_voxel;
            private int true_overlay;
            private short true_light;

            public uint light { get => GetLight(); set => SetLight(value); }
            public byte sunlight { get => GetSunlight(); set => SetSunlight(value); }
            public byte mining_time;
            public Voxel(TileData voxel)
            {
                true_voxel = voxel.index;
                true_overlay = TileRegistry.AIR.DefaultData.index;
                true_light = 0x000f;
                mining_time = 0;
            }

            public uint GetLight()
            {
                return (uint)true_light >> 4;
            }

            public byte GetSunlight()
            {
                return (byte)(true_light & 0xf);
            }

            public void SetLight(uint light)
            {
                uint sunlight = GetSunlight();
                true_light = (short)((light << 4) | sunlight);
            }

            public void SetSunlight(byte sunlight)
            {
                true_light = (short)((true_light & 0xfff0) | sunlight);
            }

            public TileData GetVoxel()
            {
                return TileDataHolder.REGISTRY[true_voxel];
            }

            public void SetVoxel(TileData voxel)
            {
                true_voxel = voxel.index;
            }

            public TileData GetOverlayVoxel()
            {
                return TileDataHolder.REGISTRY[true_overlay];
            }

            public void SetOverlayVoxel(TileData voxel)
            {
                true_overlay = voxel.index;
            }
            
        }


        public Voxel[] voxels;

        public readonly Vector3 cpos;
        private World world;
        private ChunkManager chunkManager;

        private bool rebuilding;
        private bool generated;

        public bool mesh_fixed = false;

        public Mesh lastMesh;
        public Mesh lastWaterMesh;
        public Mesh lastTransparencyMesh;
        public Mesh lastCustomMesh;

        public Mesh mesh;

        public Mesh waterMesh;

        public Mesh transparencyMesh;

        public Mesh customMesh;

        public bool modified = false;
        public bool LightRebuild { get; private set; }

        public bool NotEmpty;

        public Chunk(int x, int y, int z, World world)
        {
            this.world = world;
            voxels = new Voxel[Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
            for (int i = 0; i < voxels.Length; i++)
            {
                voxels[i] = new Voxel(TileRegistry.AIR.DefaultData);
            }

            chunkManager = world.GetChunkManager();


            sunlightBfsQueue = new List<short>(voxels.Length);
            sunlightRemovalBfsQueue = new List<short>(voxels.Length);
            redRemovalBfsQueue = new List<short>(voxels.Length);
            greenRemovalBfsQueue = new List<short>(voxels.Length);
            blueRemovalBfsQueue = new List<short>(voxels.Length);
            redBfsQueue = new List<short>(voxels.Length);
            greenBfsQueue = new List<short>(voxels.Length);
            blueBfsQueue = new List<short>(voxels.Length);
            cpos = new Vector3(x, y, z);
        }

        public void Save()
        {
            if (!modified) return;
            ResourcePath directory = new ResourcePath("Chunks", "", "Worlds/" + world.name);
            ResourcePath file = new ResourcePath("Chunks", "Chunk_"+GetX()+"_"+GetY()+"_"+GetZ()+".chunk", "Worlds/" + world.name);
            if (!Directory.Exists(FileUtils.GetResourcePath(directory)))
            {
                Directory.CreateDirectory(FileUtils.GetResourcePath(directory));
            }
            if (File.Exists(FileUtils.GetResourcePath(file)))
            {
                File.Delete(FileUtils.GetResourcePath(file));
            }

            FileStream s = File.Open(FileUtils.GetResourcePath(file), FileMode.OpenOrCreate);
            BinaryWriter writer = new BinaryWriter(s);
            writer.Write((short)2);


            SortedSet<int> idSetVox = new SortedSet<int>();
            SortedSet<int> idSetOv = new SortedSet<int>();

            for (int i = 0; i < voxels.Length; i++)
            {
                idSetVox.Add(voxels[i].voxel.index);
                idSetOv.Add(voxels[i].overlay.index);
            }
            Dictionary<int, int> idMapVox = new Dictionary<int, int>();
            Dictionary<int, int> idMapOv = new Dictionary<int, int>();
            {
                int i = 0;
                foreach (int id in idSetVox)
                {
                    idMapVox.Add(id, i++);
                }
                i = 0;
                foreach (int id in idSetOv)
                {
                    idMapOv.Add(id, i++);
                }
            }
            int maxValues = System.Math.Max(idSetVox.Count, idSetOv.Count);
            int entrySize = maxValues <= 16 ? 0 : maxValues <= 256 ? 1 : maxValues <= 65536 ? 2 : 4;
            writer.Write((byte)entrySize);
            for (int i = 0; i < voxels.Length; i++)
            {
                int vox_index = idMapVox[voxels[i].voxel.index];
                int ov_index = idMapOv[voxels[i].overlay.index];

                if (entrySize == 0)
                {
                    writer.Write((byte)(vox_index | ov_index << 4));
                }
                else if (entrySize == 1)
                {
                    writer.Write((byte)vox_index);
                    writer.Write((byte)ov_index);
                }
                else if (entrySize == 2)
                {
                    writer.Write((short)vox_index);
                    writer.Write((short)ov_index);
                }
                else
                {
                    writer.Write(vox_index);
                    writer.Write(ov_index);
                }

                if (i % 2 == 0)
                {
                    writer.Write((byte)(voxels[i].sunlight << 4 | voxels[i + 1].sunlight));
                }
            }
            writer.Write((short)idMapVox.Count);
            foreach (int key in idMapVox.Keys)
            {
                writer.Write(key);
            }
            writer.Write((short)idMapOv.Count);
            foreach (int key in idMapOv.Keys)
            {
                writer.Write(key);
            }



        }


        public bool Load()
        {
            modified = false;

            int save_version = 1; // 1 is the original (old) save file

            ResourcePath directory = new ResourcePath("Chunks", "", "Worlds/" + world.name);
            ResourcePath file = new ResourcePath("Chunks", "Chunk_" + GetX() + "_" + GetY() + "_" + GetZ() + ".chunk", "Worlds/" + world.name);
            if (!Directory.Exists(FileUtils.GetResourcePath(directory)))
            {
                return false;
            }
            if (!File.Exists(FileUtils.GetResourcePath(file)))
            {
                return false;
            }

            FileStream st = File.Open(FileUtils.GetResourcePath(file), FileMode.Open);
            BinaryReader reader = new BinaryReader(st);
            if (reader.ReadInt16() == 2)
            {
                save_version = 2;
            }

            int index = 0; // The variable used to iterate through the voxel grid

            if (save_version == 2)
            {
                int[] voxIndex = new int[voxels.Length];
                int[] ovIndex = new int[voxels.Length];
                byte entrySize = reader.ReadByte();
                for (int i = 0; i < voxels.Length; i++)
                {
                    if (entrySize == 0)
                    {
                        byte r = reader.ReadByte();
                        voxIndex[i] = (int)(r & 0b1111);
                        ovIndex[i] = (int)(r >> 4);
                    } else if (entrySize == 1)
                    {
                        voxIndex[i] = reader.ReadByte();
                        ovIndex[i] = reader.ReadByte();
                    } else if (entrySize == 2)
                    {
                        voxIndex[i] = reader.ReadInt16();
                        ovIndex[i] = reader.ReadInt16();
                    } else
                    {
                        voxIndex[i] = reader.ReadInt32();
                        ovIndex[i] = reader.ReadInt32();
                    }

                    if (i % 2 == 0)
                    {
                        byte r = reader.ReadByte();
                        voxels[i].sunlight = (byte)(r >> 4);
                        voxels[i + 1].sunlight = (byte)(r & 0b1111);
                    }
                }

                int idMapVoxCount = reader.ReadInt16();
                List<int> vox_ids = new();
                List<int> ov_ids = new();

                for (int i = 0; i < idMapVoxCount; i++)
                {
                    int value = reader.ReadInt32();
                    vox_ids.Add(value);
                }

                int idMapOvCount = reader.ReadInt16();

                for (int i = 0; i < idMapOvCount; i++)
                {
                    int value = reader.ReadInt32();
                    ov_ids.Add(value);
                }

                for (int x = 0; x < Constants.CHUNK_SIZE; x++)
                {
                    for (int y = 0; y < Constants.CHUNK_SIZE; y++)
                    {
                        for (int z = 0; z < Constants.CHUNK_SIZE; z++)
                        {
                            int i = GetIndexFor(x, y, z);
                            int voxel = vox_ids[voxIndex[i]];
                            int overlay = ov_ids[ovIndex[i]];
                            SetVoxel(x, y, z, TileDataHolder.REGISTRY[voxel]);
                            SetOverlayVoxel(x, y, z, TileDataHolder.REGISTRY[overlay]);
                            voxels[i].mining_time = 0;
                        }
                    }
                }
            }

            if (save_version == 1)
            {
                for (int i = 0; i < voxels.Length; i++)
                {
                    voxels[i].light = 0;
                    voxels[i].sunlight = 15;
                    voxels[i].mining_time = 0;
                }
                //This method will load in data from old chunk save files (plaintext)

                string[] DATA = File.ReadAllLinesAsync(FileUtils.GetResourcePath(file)).Result;

                foreach (string s in DATA)
                {

                    string[] voxel = s.Split("_");
                    int tile = int.Parse(voxel[0]);
                    int overlay = int.Parse(voxel[1]);

                    int x = index % Constants.CHUNK_SIZE;
                    int y = ((index - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (index - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    SetVoxel(x, y, z, TileDataHolder.REGISTRY[tile]);
                    SetOverlayVoxel(x, y, z, TileDataHolder.REGISTRY[overlay]);
                    if (voxel.Length > 2)
                    {
                        if (uint.TryParse(voxel[2], out uint r1))
                            voxels[index].light = uint.Parse(voxel[2]);
                        if (uint.TryParse(voxel[2], out uint r2))
                            voxels[index].sunlight = byte.Parse(voxel[3]);
                    }


                    index++;
                }
            }

            return true;
        }

        public void BuildMesh(bool lights = true)
        {
            lock (this)
            {
                UpdateLights();

                if (NotEmpty == false) return;

                if (lastMesh != null)
                {
                    lastMesh.Dispose();
                }

                if (mesh != null)
                {
                    lastMesh = mesh;
                }

                if (lastWaterMesh != null)
                {
                    lastWaterMesh.Dispose();
                }

                if (waterMesh != null)
                {
                    lastWaterMesh = waterMesh;
                }

                if (lastTransparencyMesh != null)
                {
                    lastTransparencyMesh.Dispose();
                }

                if (transparencyMesh != null)
                {
                    lastTransparencyMesh = transparencyMesh;
                }

                if (lastCustomMesh != null)
                {
                    lastCustomMesh.Dispose();
                }

                if (customMesh != null)
                {
                    lastCustomMesh = customMesh;
                }

                mesh = ChunkBuilder.BuildMeshForChunk(Inignoto.game.GraphicsDevice, this);
                if (mesh != null)
                    mesh.SetPosition(new Vector3(GetX() * Constants.CHUNK_SIZE, GetY() * Constants.CHUNK_SIZE, GetZ() * Constants.CHUNK_SIZE));

                FinishRebuilding();
            }
            
            
        }


        public bool ReadyToFix()
        {
            if (mesh_fixed == false)
            {
                Chunk[] c = new Chunk[6];
                if ((c[0] = GetAdjacentChunk(-1, 0, 0)) != null)
                {
                    if ((c[1] = GetAdjacentChunk(1, 0, 0)) != null)
                    {
                        if ((c[2] = GetAdjacentChunk(0, -1, 0)) != null)
                        {
                            if ((c[3] = GetAdjacentChunk(0, 1, 0)) != null)
                            {
                                if ((c[4] = GetAdjacentChunk(0, 0, -1)) != null)
                                {
                                    if ((c[5] = GetAdjacentChunk(0, 0, 1)) != null)
                                    {
                                        for (int i = 0; i < 6; i++)
                                        {
                                            if (c[i].NeedsToGenerate() == true)
                                            {
                                                return false;
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

        public bool Disposed = false;

        public void Dispose()
        {
            Disposed = true;
            
            sunlightBfsQueue.Clear();
            sunlightRemovalBfsQueue.Clear();
            redBfsQueue.Clear();
            redRemovalBfsQueue.Clear();
            greenBfsQueue.Clear();
            greenRemovalBfsQueue.Clear();
            blueBfsQueue.Clear();
            blueRemovalBfsQueue.Clear();

            if (lastMesh != null)
            {
                lastMesh.Dispose();
            }

            if (lastWaterMesh != null)
            {
                lastWaterMesh.Dispose();
            }

            if (lastTransparencyMesh != null)
            {
                lastTransparencyMesh.Dispose();
            }

            if (lastCustomMesh != null)
            {
                lastCustomMesh.Dispose();
            }

            if (mesh != null)
            {
                mesh.Dispose();
            }

            if (transparencyMesh != null)
            {
                transparencyMesh.Dispose();
            }

            if (waterMesh != null)
            {
                waterMesh.Dispose();
            }

            if (customMesh != null)
            {
                customMesh.Dispose();
            }

            Save();
        }

        public int GetRedLight(int x, int y, int z)
        {
            return GetLight(x, y, z) & 0b1111;
        }

        public int GetGreenLight(int x, int y, int z)
        {
            return (GetLight(x, y, z) >> 4) & 0b1111;
        }

        public int GetBlueLight(int x, int y, int z)
        {
            return (GetLight(x, y, z) >> 8) & 0b1111;
        }

        public int GetLight(int x, int y, int z)
        {
            if (IsInsideChunk(x, y, z))
            {
                return (int)voxels[GetIndexFor(x, y, z)].light;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.GetLight(X, Y, Z);
            }

            return 0;
        }

        public int GetSunlight(int x, int y, int z)
        {
            if (IsInsideChunk(x, y, z))
            {
                return voxels[GetIndexFor(x, y, z)].sunlight;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.GetSunlight(X, Y, Z);
            }

            return 0;
        }


        public void RemoveLight(int x, int y, int z, bool red, bool green, bool blue, bool sun)
        {
            TileData data = GetVoxel(x, y, z);
            Tile tile = TileRegistry.GetTile(data.tile_id);
            if (IsInsideChunk(x, y, z))
            {
                if (tile.light_red > 0) {
                    SetRedLight(x, y, z, tile.light_red);
                    }
                else
                if (red)
                    if (!redRemovalBfsQueue.Contains(GetIndexFor(x, y, z)))
                        redRemovalBfsQueue.Add(GetIndexFor(x, y, z));

                if (tile.light_green > 0)
                {
                    SetGreenLight(x, y, z, tile.light_green);
                }
                else
                if (green)
                    if (!greenRemovalBfsQueue.Contains(GetIndexFor(x, y, z)))
                        greenRemovalBfsQueue.Add(GetIndexFor(x, y, z));

                if (tile.light_blue > 0)
                {
                    SetBlueLight(x, y, z, tile.light_blue);
                }
                else
                if (blue)
                    if (!blueRemovalBfsQueue.Contains(GetIndexFor(x, y, z)))
                        blueRemovalBfsQueue.Add(GetIndexFor(x, y, z));

                if (sun)
                    if (!sunlightRemovalBfsQueue.Contains(GetIndexFor(x, y, z)))
                        sunlightRemovalBfsQueue.Add(GetIndexFor(x, y, z));
                

                return;
            }
            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                chunk.RemoveLight(X, Y, Z, red, green, blue, sun);
            }

        }

        public void SetRedLight(int x, int y, int z, int r, bool update = true)
        {
            SetLight(x, y, z, r, -1, -1, -1, update);
        }

        public void SetGreenLight(int x, int y, int z, int g, bool update = true)
        {
            SetLight(x, y, z, -1, g, -1, -1, update);
        }

        public void SetBlueLight(int x, int y, int z, int b, bool update = true)
        {
            SetLight(x, y, z, -1, -1, b, -1, update);
        }

        public void SetSunlight(int x, int y, int z, int sun, bool update = true)
        {
            SetLight(x, y, z, -1, -1, -1, sun, update);
        }

        public int GetRGB(int r, int g, int b)
        {
            int RGB = 0;

            int R = System.Math.Max(System.Math.Min(15, r), 0);
            int G = System.Math.Max(System.Math.Min(15, g), 0) << 4;
            int B = System.Math.Max(System.Math.Min(15, b), 0) << 8;

            RGB |= R;
            RGB |= G;
            RGB |= B;

            return RGB;
        }

        public bool IsVisible(int x, int y, int z)
        {
            Tile tile = GetTile(x, y, z);
            if (GetVoxel(x, y, z).model != null)
            {
                return true;
            }
            if (GetTile(x - 1, y, z).IsOpaque())
            {
                if (GetTile(x + 1, y, z).IsOpaque())
                {
                    if (GetTile(x, y - 1, z).IsOpaque())
                    {
                        if (GetTile(x, y + 1, z).IsOpaque())
                        {
                            if (GetTile(x, y, z - 1).IsOpaque())
                            {
                                if (GetTile(x, y, z + 1).IsOpaque())
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }

        public Tile GetTile(int x, int y, int z)
        {
            return TileRegistry.GetTile(GetVoxel(x, y, z).tile_id);
        }
        public void SetLight(int x, int y, int z, int r, int g, int b, int sun, bool update = true)
        {
            if (NeedsToGenerate()) update = false;
            if (IsInsideChunk(x, y, z))
            {
                Tile tile = TileRegistry.GetTile(GetVoxel(x, y, z).tile_id);

                if (tile.tinted)
                {
                    if (r > tile.RedTint) r = tile.RedTint - (int)MathF.Max(GetRedLight(x, y, z) - r, 0);
                    if (g > tile.GreenTint) g = tile.GreenTint - (int)MathF.Max(GetGreenLight(x, y, z) - r, 0);
                    if (b > tile.BlueTint) b = tile.BlueTint - (int)MathF.Max(GetBlueLight(x, y, z) - r, 0);
                }
                

                int R = System.Math.Max(System.Math.Min(15, r), 0);
                int G = System.Math.Max(System.Math.Min(15, g), 0) << 4;
                int B = System.Math.Max(System.Math.Min(15, b), 0) << 8;
                int SUN = System.Math.Max(System.Math.Min(15, sun), 0);

                if (r > -1)
                {
                    voxels[GetIndexFor(x, y, z)].light &= 0b111111110000;
                    voxels[GetIndexFor(x, y, z)].light |= (uint)R;
                    if (update)
                        {
                            if (!redBfsQueue.Contains(GetIndexFor(x, y, z)))
                            redBfsQueue.Add(GetIndexFor(x, y, z));
                        }
                }
                if (g > -1)
                {
                    voxels[GetIndexFor(x, y, z)].light &= 0b111100001111;
                    voxels[GetIndexFor(x, y, z)].light |= (uint)G;
                    if (update)
                        {
                            if (!greenBfsQueue.Contains(GetIndexFor(x, y, z)))
                                greenBfsQueue.Add(GetIndexFor(x, y, z));
                        }
                }
                
                if (b > -1)
                {
                    voxels[GetIndexFor(x, y, z)].light &= 0b000011111111;
                    voxels[GetIndexFor(x, y, z)].light |= (uint)B;
                    if (update)
                        {
                            if (!blueBfsQueue.Contains(GetIndexFor(x, y, z)))
                                blueBfsQueue.Add(GetIndexFor(x, y, z));
                        }
                }

                if (sun > -1)
                {
                    voxels[GetIndexFor(x, y, z)].sunlight = (byte)SUN;

                    if (update)
                        {

                            if (!sunlightBfsQueue.Contains(GetIndexFor(x, y, z)))
                                sunlightBfsQueue.Add(GetIndexFor(x, y, z));
                        }
                }
                return;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                chunk.SetLight(X, Y, Z, r, g, b, sun);
            }
        }

        public void PropogateLights(int x, int y, int z, bool red, bool green, bool blue, bool sun)
        {
            if (sun)
                if (GetSunlight(x, y + 1, z) > 0)
                {
                    int s = GetSunlight(x, y + 1, z);
                    SetLight(x, y, z, -1, -1, -1, s == 15 ? s : s - 1);
                }
                else
                {
                    if (GetSunlight(x, y - 1, z) > 0)
                    {
                        SetLight(x, y, z, -1, -1, -1, GetSunlight(x, y - 1, z) - 1);
                    }
                    else
                    {
                        if (GetSunlight(x - 1, y, z) > 0)
                        {
                            SetLight(x, y, z, -1, -1, -1, GetSunlight(x - 1, y, z) - 1);
                        }
                        else
                        {
                            if (GetSunlight(x + 1, y, z) > 0)
                            {
                                SetLight(x, y, z, -1, -1, -1, GetSunlight(x + 1, y, z) - 1);
                            }
                            else
                            {
                                if (GetSunlight(x, y, z - 1) > 0)
                                {
                                    SetLight(x, y, z, -1, -1, -1, GetSunlight(x, y, z - 1) - 1);
                                }
                                else
                                {
                                    if (GetSunlight(x, y, z + 1) > 0)
                                    {
                                        SetLight(x, y, z, -1, -1, -1, GetSunlight(x, y, z + 1) - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            if (red)
                if (GetRedLight(x, y + 1, z) > 0)
                {
                    SetRedLight(x, y, z, GetRedLight(x, y + 1, z) - 1);
                }
                else
                {
                    if (GetRedLight(x, y - 1, z) > 0)
                    {
                        SetRedLight(x, y, z, GetRedLight(x, y - 1, z) - 1);
                    }
                    else
                    {
                        if (GetRedLight(x - 1, y, z) > 0)
                        {
                            SetRedLight(x, y, z, GetRedLight(x - 1, y, z) - 1);
                        }
                        else
                        {
                            if (GetRedLight(x + 1, y, z) > 0)
                            {
                                SetRedLight(x, y, z, GetRedLight(x + 1, y, z) - 1);
                            }
                            else
                            {
                                if (GetRedLight(x, y, z - 1) > 0)
                                {
                                    SetRedLight(x, y, z, GetRedLight(x, y, z - 1) - 1);
                                }
                                else
                                {
                                    if (GetRedLight(x, y, z + 1) > 0)
                                    {
                                        SetRedLight(x, y, z, GetRedLight(x, y, z + 1) - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            if (green)
                if (GetGreenLight(x, y + 1, z) > 0)
                {
                    SetGreenLight(x, y, z, GetGreenLight(x, y + 1, z) - 1);
                }
                else
                {
                    if (GetGreenLight(x, y - 1, z) > 0)
                    {
                        SetGreenLight(x, y, z, GetGreenLight(x, y - 1, z) - 1);
                    }
                    else
                    {
                        if (GetGreenLight(x - 1, y, z) > 0)
                        {
                            SetGreenLight(x, y, z, GetGreenLight(x - 1, y, z) - 1);
                        }
                        else
                        {
                            if (GetGreenLight(x + 1, y, z) > 0)
                            {
                                SetGreenLight(x, y, z, GetGreenLight(x + 1, y, z) - 1);
                            }
                            else
                            {
                                if (GetGreenLight(x, y, z - 1) > 0)
                                {
                                    SetGreenLight(x, y, z, GetGreenLight(x, y, z - 1) - 1);
                                }
                                else
                                {
                                    if (GetGreenLight(x, y, z + 1) > 0)
                                    {
                                        SetGreenLight(x, y, z, GetGreenLight(x, y, z + 1) - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            if (blue)
                if (GetBlueLight(x, y + 1, z) > 0)
                {
                    SetBlueLight(x, y, z, GetBlueLight(x, y + 1, z) - 1);
                }
                else
                {
                    if (GetBlueLight(x, y - 1, z) > 0)
                    {
                        SetBlueLight(x, y, z, GetBlueLight(x, y - 1, z) - 1);
                    }
                    else
                    {
                        if (GetBlueLight(x - 1, y, z) > 0)
                        {
                            SetBlueLight(x, y, z, GetBlueLight(x - 1, y, z) - 1);
                        }
                        else
                        {
                            if (GetBlueLight(x + 1, y, z) > 0)
                            {
                                SetBlueLight(x, y, z, GetBlueLight(x + 1, y, z) - 1);
                            }
                            else
                            {
                                if (GetBlueLight(x, y, z - 1) > 0)
                                {
                                    SetBlueLight(x, y, z, GetBlueLight(x, y, z - 1) - 1);
                                }
                                else
                                {
                                    if (GetBlueLight(x, y, z + 1) > 0)
                                    {
                                        SetBlueLight(x, y, z, GetBlueLight(x, y, z + 1) - 1);
                                    }
                                }
                            }
                        }
                    }
                }


        }

        public void Tick(float distance)
        {
            for (int i = 0; i < 10; i++)
            {
                int I = world.random.Next(voxels.Length);
                int x = I % Constants.CHUNK_SIZE;
                int y = ((I - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                int z = (I - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                if (world.random.Next(100) <= 2)
                {
                    voxels[I].voxel.Tick(x, y, z, this);
                    voxels[I].mining_time--;
                }
                
            }
        }

        private void MarkForLightBuild()
        {
            LightRebuild = true;
        }

        public TileData GetOverlayVoxel(int x, int y, int z)
        {
            if (IsInsideChunk(x, y, z))
            {
                TileData data = voxels[GetIndexFor(x, y, z)].overlay;
                if (data == null) return TileRegistry.AIR.DefaultData;
                return data;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.GetOverlayVoxel(X, Y, Z);
            }

            return TileRegistry.AIR.DefaultData;
        }

        public bool SetOverlayVoxel(int x, int y, int z, TileData voxel)
        {
            if (IsInsideChunk(x, y, z))
            {
                voxels[GetIndexFor(x, y, z)].overlay = voxel;
                if (!NeedsToGenerate())
                modified = true;
                return true;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.SetOverlayVoxel(X, Y, Z, voxel);
            }
            return false;
        }

        public TileData GetVoxel(int x, int y, int z)
        {
            if (IsInsideChunk(x, y, z))
            {
                TileData data = voxels[GetIndexFor(x, y, z)].voxel;
                if (data == null) return TileRegistry.AIR.DefaultData;
                return data;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z, out int CX, out int CY, out int CZ);

            if (chunk != null)
            {
                return chunk.GetVoxel(X, Y, Z);
            }

            return TileRegistry.AIR.DefaultData;
        }

        public bool SetVoxel(int x, int y, int z, TileData voxel)
        {

            if (IsInsideChunk(x, y, z))
            {
                TileData last = voxels[GetIndexFor(x, y, z)].voxel;

                voxels[GetIndexFor(x, y, z)].voxel = voxel;

                if (last != null)
                    last.UpdateLightWhenRemoved(this, x, y, z);
                
                if (voxel != null)
                    voxel.UpdateLightWhenPlaced(this, x, y, z);

                if (voxel == TileRegistry.AIR.DefaultData)
                {
                    SetOverlayVoxel(x, y, z, TileRegistry.AIR.DefaultData);
                } else
                {
                    NotEmpty = true;
                }
                if (!NeedsToGenerate())
                    modified = true;
                return true;
            }


            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z, out int CX, out int CY, out int CZ);

            if (chunk != null)
            {
                if (chunk.NeedsToGenerate())
                {
                    StructureChunk schunk = chunkManager.GetOrCreateStructureChunk(CX, CY, CZ);
                    schunk.SetTile(X, Y, Z, voxel);
                    return true;
                }
                return chunk.SetVoxel(X, Y, Z, voxel);
            } else
            {

                StructureChunk schunk = chunkManager.GetOrCreateStructureChunk(CX, CY, CZ);
                schunk.SetTile(X, Y, Z, voxel);
            }
            return false;
        }

        public short GetIndexFor(int x, int y, int z)
        {
            return (short)(x + y * Constants.CHUNK_SIZE + z * (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE));
        }

        public bool IsInsideChunk(int x, int y, int z)
        {
            return x >= 0 && y >= 0 && z >= 0 && x < Constants.CHUNK_SIZE && y < Constants.CHUNK_SIZE && z < Constants.CHUNK_SIZE;
        }

        public Chunk GetAdjacentChunk(int x, int y, int z)
        {
            int X = GetX() + x;
            int Y = GetY() + y;
            int Z = GetZ() + z;
            return chunkManager.TryGetChunk(X, Y, Z);
        }
        public Chunk GetAdjacentChunkForLocation(int xx, int yy, int zz, out int x, out int y, out int z)
        {
            return GetAdjacentChunkForLocation(xx, yy, zz, out x, out y, out z, out int CX, out int CY, out int CZ);
        }
        public Chunk GetAdjacentChunkForLocation(int xx, int yy, int zz, out int x, out int y, out int z, out int CX, out int CY, out int CZ)
        {
            int X = xx >= 0 ? xx / Constants.CHUNK_SIZE : xx / Constants.CHUNK_SIZE - 1;
            int Y = yy >= 0 ? yy / Constants.CHUNK_SIZE : yy / Constants.CHUNK_SIZE - 1;
            int Z = zz >= 0 ? zz / Constants.CHUNK_SIZE : zz / Constants.CHUNK_SIZE - 1;
            X += GetX();
            Y += GetY();
            Z += GetZ();
            x = xx % Constants.CHUNK_SIZE;
            y = yy % Constants.CHUNK_SIZE;
            z = zz % Constants.CHUNK_SIZE;

            if (x < 0) x += Constants.CHUNK_SIZE;
            if (y < 0) y += Constants.CHUNK_SIZE;
            if (z < 0) z += Constants.CHUNK_SIZE;

            CX = X;
            CY = Y;
            CZ = Z;
            return chunkManager.TryGetChunk(X, Y, Z);
        }

        public World GetWorld()
        {
            return world;
        }

        public int GetX()
        {
            return (int)cpos.X;
        }

        public int GetY()
        {
            return (int)cpos.Y;
        }

        public int GetZ()
        {
            return (int)cpos.Z;
        }

        public void MarkForRebuild(bool queue = false)
        {
            rebuilding = true;
        }

        public void FinishRebuilding()
        {
            rebuilding = false;
        }

        public bool NeedsToRebuild()
        {
            return rebuilding;
        }


        public bool NeedsToGenerate()
        {
            return !generated;
        }

        public void SetGenerated(bool gen = true)
        {
            generated = gen;
        }
        public int CompareTo(Chunk obj)
        {
            if (obj == null) return 1;
            try
            {
                Vector3 pos = obj.chunkManager.current_xyz + new Vector3(0, 4, 0);
                float dist1 = Vector3.Distance(cpos, pos);
                float dist2 = Vector3.Distance(obj.cpos, pos);
                if (dist1 == dist2) return 0;
                if (dist1 < dist2) return -1;
            } catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return 0;
            }
            return 1;
        }


        public void UpdateLights(bool canRerender = true)
        {
            try
            {

                LightRebuild = false;
                bool rerender = false;
                bool cu = false, cd = false, cl = false, cr = false, cb = false, cf = false;

                for (int i = 0; i < voxels.Length && sunlightRemovalBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = sunlightRemovalBfsQueue[0];
                    sunlightRemovalBfsQueue.RemoveAt(0);

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                    int lightLevel = GetSunlight(x, y, z);
                    SetLight(x, y, z, -1, -1, -1, 0, false);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int neighborLevel = GetSunlight(x - 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x - 1, y, z, false, false, false, true);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x - 1, y, z);
                        if (!sunlightBfsQueue.Contains(index))
                            sunlightBfsQueue.Add(index);
                    }
                    neighborLevel = GetSunlight(x + 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x + 1, y, z, false, false, false, true);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x + 1, y, z);
                        if (!sunlightBfsQueue.Contains(index))
                            sunlightBfsQueue.Add(index);
                    }
                    neighborLevel = GetSunlight(x, y + 1, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y + 1, z, false, false, false, true);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y + 1, z);
                        if (!sunlightBfsQueue.Contains(index))
                            sunlightBfsQueue.Add(index);
                    }
                    neighborLevel = GetSunlight(x, y - 1, z);
                    if (neighborLevel != 0 && (neighborLevel < lightLevel || lightLevel == 15))
                    {
                        RemoveLight(x, y - 1, z, false, false, false, true);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y - 1, z);
                        if (!sunlightBfsQueue.Contains(index))
                            sunlightBfsQueue.Add(index);
                    }
                    neighborLevel = GetSunlight(x, y, z + 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z + 1, false, false, false, true);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z + 1);
                        if (!sunlightBfsQueue.Contains(index))
                            sunlightBfsQueue.Add(index);
                    }
                    neighborLevel = GetSunlight(x, y, z - 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z - 1, false, false, false, true);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z - 1);
                        if (!sunlightBfsQueue.Contains(index))
                            sunlightBfsQueue.Add(index);
                    }
                }
                for (int i = 0; i < voxels.Length && redRemovalBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = redRemovalBfsQueue[0];
                    redRemovalBfsQueue.RemoveAt(0);

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                    int lightLevel = GetRedLight(x, y, z);
                    SetLight(x, y, z, 0, -1, -1, -1, false);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int neighborLevel = GetRedLight(x - 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x - 1, y, z, true, false, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x - 1, y, z);
                        if (!redBfsQueue.Contains(index))
                            redBfsQueue.Add(index);
                    }
                    neighborLevel = GetRedLight(x + 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x + 1, y, z, true, false, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x + 1, y, z);
                        if (!redBfsQueue.Contains(index))
                            redBfsQueue.Add(index);
                    }
                    neighborLevel = GetRedLight(x, y + 1, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y + 1, z, true, false, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y + 1, z);
                        if (!redBfsQueue.Contains(index))
                            redBfsQueue.Add(index);
                    }
                    neighborLevel = GetRedLight(x, y - 1, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y - 1, z, true, false, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y - 1, z);
                        if (!redBfsQueue.Contains(index))
                            redBfsQueue.Add(index);
                    }
                    neighborLevel = GetRedLight(x, y, z + 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z + 1, true, false, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z + 1);
                        if (!redBfsQueue.Contains(index))
                            redBfsQueue.Add(index);
                    }
                    neighborLevel = GetRedLight(x, y, z - 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z - 1, true, false, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z - 1);
                        if (!redBfsQueue.Contains(index))
                            redBfsQueue.Add(index);
                    }
                }
                for (int i = 0; i < voxels.Length && greenRemovalBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = greenRemovalBfsQueue[0];
                    greenRemovalBfsQueue.RemoveAt(0);

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                    int lightLevel = GetGreenLight(x, y, z);
                    SetLight(x, y, z, -1, 0, -1, -1, false);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int neighborLevel = GetGreenLight(x - 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x - 1, y, z, false, true, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x - 1, y, z);
                        if (!greenBfsQueue.Contains(index))
                            greenBfsQueue.Add(index);
                    }
                    neighborLevel = GetGreenLight(x + 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x + 1, y, z, false, true, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x + 1, y, z);
                        if (!greenBfsQueue.Contains(index))
                            greenBfsQueue.Add(index);
                    }
                    neighborLevel = GetGreenLight(x, y + 1, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y + 1, z, false, true, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y + 1, z);
                        if (!greenBfsQueue.Contains(index))
                            greenBfsQueue.Add(index);
                    }
                    neighborLevel = GetGreenLight(x, y - 1, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y - 1, z, false, true, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y - 1, z);
                        if (!greenBfsQueue.Contains(index))
                            greenBfsQueue.Add(index);
                    }
                    neighborLevel = GetGreenLight(x, y, z + 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z + 1, false, true, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z + 1);
                        if (!greenBfsQueue.Contains(index))
                            greenBfsQueue.Add(index);
                    }
                    neighborLevel = GetGreenLight(x, y, z - 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z - 1, false, true, false, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z - 1);
                        if (!greenBfsQueue.Contains(index))
                            greenBfsQueue.Add(index);
                    }
                }
                for (int i = 0; i < voxels.Length && blueRemovalBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = blueRemovalBfsQueue[0];
                    blueRemovalBfsQueue.RemoveAt(0);

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                    int lightLevel = GetBlueLight(x, y, z);
                    SetLight(x, y, z, -1, -1, 0, -1, false);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int neighborLevel = GetBlueLight(x - 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x - 1, y, z, false, false, true, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x - 1, y, z);
                        if (!blueBfsQueue.Contains(index))
                            blueBfsQueue.Add(index);
                    }
                    neighborLevel = GetBlueLight(x + 1, y, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x + 1, y, z, false, false, true, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x + 1, y, z);
                        if (!blueBfsQueue.Contains(index))
                            blueBfsQueue.Add(index);
                    }
                    neighborLevel = GetBlueLight(x, y + 1, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y + 1, z, false, false, true, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y + 1, z);
                        if (!blueBfsQueue.Contains(index))
                            blueBfsQueue.Add(index);
                    }
                    neighborLevel = GetBlueLight(x, y - 1, z);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y - 1, z, false, false, true, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y - 1, z);
                        if (!blueBfsQueue.Contains(index))
                            blueBfsQueue.Add(index);
                    }
                    neighborLevel = GetBlueLight(x, y, z + 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z + 1, false, false, true, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z + 1);
                        if (!blueBfsQueue.Contains(index))
                            blueBfsQueue.Add(index);
                    }
                    neighborLevel = GetBlueLight(x, y, z - 1);
                    if (neighborLevel != 0 && neighborLevel < lightLevel)
                    {
                        RemoveLight(x, y, z - 1, false, false, true, false);
                    }
                    else if (neighborLevel >= lightLevel)
                    {
                        short index = GetIndexFor(x, y, z - 1);
                        if (!blueBfsQueue.Contains(index))
                            blueBfsQueue.Add(index);
                    }
                }


                //////////ADD LIGHTS
                for (int i = 0; i < voxels.Length && sunlightBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = sunlightBfsQueue[0];
                    sunlightBfsQueue.RemoveAt(0);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetSunlight(x, y, z);

                    if (!TileRegistry.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, -1, -1, -1, lightLevel == 15 ? lightLevel : lightLevel - 1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x, y + 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y + 1, z, -1, -1, -1, lightLevel - 1);
                    }
                }
                for (int i = 0; i < voxels.Length && redBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = redBfsQueue[0];
                    redBfsQueue.RemoveAt(0);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetRedLight(x, y, z);

                    if (!TileRegistry.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x, y + 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y + 1, z, lightLevel - 1, -1, -1, -1);
                    }
                }
                for (int i = 0; i < voxels.Length && greenBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = greenBfsQueue[0];
                    greenBfsQueue.RemoveAt(0);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetGreenLight(x, y, z);

                    if (!TileRegistry.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x, y + 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y + 1, z, -1, lightLevel - 1, -1, -1);
                    }
                }
                for (int i = 0; i < voxels.Length && blueBfsQueue.Count > 0; i++)
                {
                    rerender = true;
                    int node = blueBfsQueue[0];
                    blueBfsQueue.RemoveAt(0);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 < 0 || x + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 < 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 < 0 || y + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 < 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 < 0 || z + 1 > Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 < 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetBlueLight(x, y, z);

                    if (!TileRegistry.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileRegistry.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y + 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y + 1, z, -1, -1, lightLevel - 1, -1);
                    }
                }

                if (rerender)
                {
                    MarkForRebuild();
                    if (cu)
                    {
                        Chunk chunk = world.chunkManager.TryGetChunk(GetX(), GetY() + 1, GetZ());
                        if (chunk != null)
                            chunk.MarkForLightBuild();
                    }
                    if (cd)
                    {
                        Chunk chunk = world.chunkManager.TryGetChunk(GetX(), GetY() - 1, GetZ());
                        if (chunk != null)
                            chunk.MarkForLightBuild();
                    }
                    if (cl)
                    {
                        Chunk chunk = world.chunkManager.TryGetChunk(GetX() - 1, GetY(), GetZ());
                        if (chunk != null)
                            chunk.MarkForLightBuild();
                    }
                    if (cr)
                    {
                        Chunk chunk = world.chunkManager.TryGetChunk(GetX() + 1, GetY(), GetZ());
                        if (chunk != null)
                            chunk.MarkForLightBuild();
                    }
                    if (cf)
                    {
                        Chunk chunk = world.chunkManager.TryGetChunk(GetX(), GetY(), GetZ() + 1);
                        if (chunk != null)
                            chunk.MarkForLightBuild();
                    }
                    if (cb)
                    {
                        Chunk chunk = world.chunkManager.TryGetChunk(GetX(), GetY(), GetZ() - 1);
                        if (chunk != null)
                            chunk.MarkForLightBuild();
                    }
                }
            } catch (InvalidOperationException e) {
            }
        }

    }
}
