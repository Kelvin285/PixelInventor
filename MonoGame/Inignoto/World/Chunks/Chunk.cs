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

namespace Inignoto.World.Chunks
{
    public class Chunk : IComparable<Chunk>
    {
        public static Vector3[] vectors = new Vector3[Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
        public struct Voxel
        {
            public TileData voxel { get => GetVoxel(); set => SetVoxel(value); }
            public TileData overlay { get => GetOverlayVoxel(); set => SetOverlayVoxel(value); }

            private int true_voxel;
            private int true_overlay;
            private uint true_light;

            public uint light { get => GetLight(); set => SetLight(value); }
            public byte sunlight { get => GetSunlight(); set => SetSunlight(value); }
            public int mining_time;
            public Voxel(TileData voxel)
            {
                true_voxel = voxel.index;
                true_overlay = TileRegistry.AIR.DefaultData.index;
                true_light = 0x000f;
                mining_time = 0;
            }

            public uint GetLight()
            {
                return true_light >> 4;
            }

            public byte GetSunlight()
            {
                return (byte)(true_light & 0xf);
            }

            public void SetLight(uint light)
            {
                uint sunlight = GetSunlight();
                true_light = (light << 4) | sunlight;
            }

            public void SetSunlight(byte sunlight)
            {
                true_light = true_light & 0xfff0;
                true_light |= sunlight;
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

        public readonly Voxel[] voxels;

        public List<Vector3> buildVoxels = new List<Vector3>();

        public readonly Vector3 cpos;
        private World world;
        private ChunkManager chunkManager;

        private bool rebuilding;
        private bool generated;

        public bool mesh_fixed = false;

        public Mesh[] mesh = new Mesh[2];

        public Mesh[] waterMesh = new Mesh[2];

        public Mesh[] transparencyMesh = new Mesh[2];

        public int meshIndex = 0;
        
        public bool modified = false;
        public bool LightRebuild { get; private set; }

        public bool NotEmpty;

        public List<int> sunlightBfsQueue;
        public List<int> sunlightRemovalBfsQueue;
        public List<int> redRemovalBfsQueue;
        public List<int> greenRemovalBfsQueue;
        public List<int> blueRemovalBfsQueue;
        public List<int> redBfsQueue;
        public List<int> greenBfsQueue;
        public List<int> blueBfsQueue;

        private static bool vec = false;
        public Chunk(int x, int y, int z, World world)
        {
            this.world = world;
            voxels = new Voxel[Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
            for (int i = 0; i < voxels.Length; i++)
            {
                voxels[i] = new Voxel(TileRegistry.AIR.DefaultData);
            }

            if (!vec)
            {
                vec = true;
                for (int xx = 0; xx < Constants.CHUNK_SIZE; xx++)
                {
                    for (int yy = 0; yy < Constants.CHUNK_SIZE; yy++)
                    {
                        for (int zz = 0; zz < Constants.CHUNK_SIZE; zz++)
                        {
                            Vector3 v = new Vector3(xx, yy, zz);
                            vectors[GetIndexFor(xx, yy, zz)] = v;
                        }
                    }
                }
            }

            chunkManager = world.GetChunkManager();


            sunlightBfsQueue = new List<int>(voxels.Length);
            sunlightRemovalBfsQueue = new List<int>(voxels.Length);
            redRemovalBfsQueue = new List<int>(voxels.Length);
            greenRemovalBfsQueue = new List<int>(voxels.Length);
            blueRemovalBfsQueue = new List<int>(voxels.Length);
            redBfsQueue = new List<int>(voxels.Length);
            greenBfsQueue = new List<int>(voxels.Length);
            blueBfsQueue = new List<int>(voxels.Length);
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

            List<string> str = new List<string>();
            for(int i = 0; i < voxels.Length; i++)
            {
                str.Add(voxels[i].voxel.index + "_" + voxels[i].overlay.index+"_"+voxels[i].light+"_"+voxels[i].sunlight);
            }

            File.WriteAllLinesAsync(FileUtils.GetResourcePath(file), str);
        }

        public bool Load()
        {
            modified = false;
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
            string[] data = File.ReadAllLinesAsync(FileUtils.GetResourcePath(file)).Result;
            for (int i = 0; i < voxels.Length; i++)
            {
                voxels[i].light = 0;
                voxels[i].sunlight = 15;
            }
            int index = 0;
            foreach (string s in data)
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
            return true;
        }

        public void BuildMesh()
        {
            UpdateLights(LightRebuild);

            if (NotEmpty == false) return;

            if (mesh[meshIndex] != null)
            {
                mesh[meshIndex].Dispose();
            }

            if (waterMesh[meshIndex] != null)
            {
                waterMesh[meshIndex].Dispose();
            }

            if (transparencyMesh[meshIndex] != null)
            {
                transparencyMesh[meshIndex].Dispose();
            }

            lock (mesh) mesh[meshIndex] = ChunkBuilder.BuildMeshForChunk(Inignoto.game.GraphicsDevice, this);
            if (mesh[meshIndex] != null)
                mesh[meshIndex].SetPosition(new Vector3(GetX() * Constants.CHUNK_SIZE, GetY() * Constants.CHUNK_SIZE, GetZ() * Constants.CHUNK_SIZE));

            if (meshIndex == 1) meshIndex = 0;
            else meshIndex = 1;

            FinishRebuilding();
            
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
            //lock(redBfsQueue)
            {
                //lock(greenBfsQueue)
                {
                    //lock(blueBfsQueue)
                    {
                        //lock(sunlightBfsQueue)
                        {
                            //lock(redRemovalBfsQueue)
                            {
                                //lock(greenRemovalBfsQueue)
                                {
                                    //lock(blueRemovalBfsQueue)
                                    {
                                        //lock(sunlightRemovalBfsQueue)
                                        {
                                            sunlightBfsQueue.Clear();
                                            sunlightRemovalBfsQueue.Clear();
                                            redBfsQueue.Clear();
                                            redRemovalBfsQueue.Clear();
                                            greenBfsQueue.Clear();
                                            greenRemovalBfsQueue.Clear();
                                            blueBfsQueue.Clear();
                                            blueRemovalBfsQueue.Clear();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < 2; i++)
            {
                if (mesh[i] != null)
                {
                    mesh[i].Dispose();
                }

                if (transparencyMesh[i] != null)
                {
                    transparencyMesh[i].Dispose();
                }

                if (waterMesh[i] != null)
                {
                    waterMesh[i].Dispose();
                }
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
                lock (sunlightRemovalBfsQueue)
                {
                    lock (blueRemovalBfsQueue)
                    {
                        lock (greenRemovalBfsQueue)
                        {
                            lock (redRemovalBfsQueue)
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
                            }
                        }
                    }
                }

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
                        lock (redBfsQueue)
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
                        lock (greenBfsQueue) {
                            if (!greenBfsQueue.Contains(GetIndexFor(x, y, z)))
                                greenBfsQueue.Add(GetIndexFor(x, y, z));
                        }
                }
                
                if (b > -1)
                {
                    voxels[GetIndexFor(x, y, z)].light &= 0b000011111111;
                    voxels[GetIndexFor(x, y, z)].light |= (uint)B;
                    if (update)
                        lock (blueBfsQueue)
                        {
                            if (!blueBfsQueue.Contains(GetIndexFor(x, y, z)))
                                blueBfsQueue.Add(GetIndexFor(x, y, z));
                        }
                }

                if (sun > -1)
                {
                    voxels[GetIndexFor(x, y, z)].sunlight = (byte)SUN;

                    if (update)
                        //lock(sunlightBfsQueue) 
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

        public void UpdateLights(bool canRerender = true)
        {
            LightRebuild = false;
            bool rerender = false;
            bool cu = false, cd = false, cl = false, cr = false, cb = false, cf = false;
            
            lock(sunlightRemovalBfsQueue)
            while (sunlightRemovalBfsQueue.Count > 0)
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
                } else if (neighborLevel >= lightLevel)
                {
                    int index = GetIndexFor(x - 1, y, z);
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
                    int index = GetIndexFor(x + 1, y, z);
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
                    int index = GetIndexFor(x, y + 1, z);
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
                    int index = GetIndexFor(x, y - 1, z);
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
                    int index = GetIndexFor(x, y, z + 1);
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
                    int index = GetIndexFor(x, y, z - 1);
                            if (!sunlightBfsQueue.Contains(index))
                                sunlightBfsQueue.Add(index);
                }
            }
            lock(redRemovalBfsQueue)
            while (redRemovalBfsQueue.Count > 0)
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
                    int index = GetIndexFor(x - 1, y, z);
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
                    int index = GetIndexFor(x + 1, y, z);
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
                    int index = GetIndexFor(x, y + 1, z);
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
                    int index = GetIndexFor(x, y - 1, z);
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
                    int index = GetIndexFor(x, y, z + 1);
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
                    int index = GetIndexFor(x, y, z - 1);
                            if (!redBfsQueue.Contains(index))
                                redBfsQueue.Add(index);
                }
            }
            lock(greenRemovalBfsQueue)
            while (greenRemovalBfsQueue.Count > 0)
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
                    int index = GetIndexFor(x - 1, y, z);
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
                    int index = GetIndexFor(x + 1, y, z);
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
                    int index = GetIndexFor(x, y + 1, z);
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
                    int index = GetIndexFor(x, y - 1, z);
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
                    int index = GetIndexFor(x, y, z + 1);
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
                    int index = GetIndexFor(x, y, z - 1);
                            if (!greenBfsQueue.Contains(index))
                                greenBfsQueue.Add(index);
                }
            }
            lock(blueRemovalBfsQueue)
            while (blueRemovalBfsQueue.Count > 0)
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
                    int index = GetIndexFor(x - 1, y, z);
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
                    int index = GetIndexFor(x + 1, y, z);
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
                    int index = GetIndexFor(x, y + 1, z);
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
                    int index = GetIndexFor(x, y - 1, z);
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
                    int index = GetIndexFor(x, y, z + 1);
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
                    int index = GetIndexFor(x, y, z - 1);
                            if (!blueBfsQueue.Contains(index))
                                blueBfsQueue.Add(index);
                }
            }


            //////////ADD LIGHTS
            lock (sunlightBfsQueue)
                while (sunlightBfsQueue.Count > 0)
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
            lock (redBfsQueue)
                while (redBfsQueue.Count > 0)
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
            lock (greenBfsQueue)
                while (greenBfsQueue.Count > 0)
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
            lock (blueBfsQueue)
                while (blueBfsQueue.Count > 0)
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

                if (IsVisible(x, y, z))
                {
                    buildVoxels.Add(vectors[GetIndexFor(x, y, z)]);
                } else
                {
                    if (buildVoxels.Contains(vectors[GetIndexFor(x, y, z)]))
                    {
                        buildVoxels.Remove(vectors[GetIndexFor(x, y, z)]);
                    }
                }

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

        public int GetIndexFor(int x, int y, int z)
        {
            return x + y * Constants.CHUNK_SIZE + z * (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
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

        bool extendedRebuild = false;
        bool justFinishedBuilding = false;
        public void MarkForRebuild(bool queue = false, bool extend = true)
        {
            if (justFinishedBuilding)
            {
                for (int x = 0; x < Constants.CHUNK_SIZE; x++)
                {
                    for (int y = 0; y < Constants.CHUNK_SIZE; y++)
                    {
                        for (int z = 0; z < Constants.CHUNK_SIZE; z++)
                        {
                            if (IsVisible(x, y, z))
                            {
                                buildVoxels.Add(vectors[GetIndexFor(x, y, z)]);
                            }
                        }
                    }
                }
            }
            extendedRebuild = extend;
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
            justFinishedBuilding = true;
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
    }
}
