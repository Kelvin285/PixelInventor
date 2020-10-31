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

namespace Inignoto.World.Chunks
{
    public class Chunk :  IComparable<Chunk>
    {
        private readonly TileData[] voxels;
        private readonly int[] light;
        private readonly int[] sunlight;

        private readonly int x, y, z;
        public Vector3 cpos { get; private set; }
        private readonly World world;
        private readonly ChunkManager chunkManager;

        private bool rebuilding;
        private bool generated;

        public Mesh mesh;
        public Mesh secondMesh;

        public Mesh waterMesh;
        public Mesh secondWaterMesh;

        public Mesh transparencyMesh;
        public Mesh secondTransparencyMesh;

        public bool transparentRebuild = false;

        private int solid_voxels = 0;

        private bool lightRebuild = false;

        public bool Full => solid_voxels >= Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE;

        public List<int> sunlightBfsQueue;
        public List<int> sunlightRemovalBfsQueue;
        public List<int> redRemovalBfsQueue;
        public List<int> greenRemovalBfsQueue;
        public List<int> blueRemovalBfsQueue;
        public List<int> redBfsQueue;
        public List<int> greenBfsQueue;
        public List<int> blueBfsQueue;
        public Chunk(int x, int y, int z, World world)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
            voxels = new TileData[Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
            light = new int[voxels.Length];
            sunlight = new int[voxels.Length];
            chunkManager = world.GetChunkManager();

            sunlightBfsQueue = new List<int>();
            sunlightRemovalBfsQueue = new List<int>();
            redRemovalBfsQueue = new List<int>();
            greenRemovalBfsQueue = new List<int>();
            blueRemovalBfsQueue = new List<int>();
            redBfsQueue = new List<int>();
            greenBfsQueue = new List<int>();
            blueBfsQueue = new List<int>();
            cpos = new Vector3(x, y, z);
        }

        public void BuildMesh()
        {
            UpdateLights();
            secondMesh = ChunkBuilder.BuildMeshForChunk(Inignoto.game.GraphicsDevice, this);
            if (secondMesh != null)
                secondMesh.SetPosition(new Microsoft.Xna.Framework.Vector3(GetX() * Constants.CHUNK_SIZE, GetY() * Constants.CHUNK_SIZE, GetZ() * Constants.CHUNK_SIZE));

            FinishRebuilding();
            if (mesh != null)
            {
                mesh.Dispose();
            }

            if (waterMesh != null)
            {
                waterMesh.Dispose();
            }
            
            if (transparencyMesh != null)
            {
                transparencyMesh.Dispose();
            }
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
                return light[GetIndexFor(x, y, z)];
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
                return sunlight[GetIndexFor(x, y, z)];
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
            if (IsInsideChunk(x, y, z))
            {

                lock(redRemovalBfsQueue)
                if (red) redRemovalBfsQueue.Add(GetIndexFor(x, y, z));
                lock(greenRemovalBfsQueue)
                if (green) greenRemovalBfsQueue.Add(GetIndexFor(x, y, z));
                lock(blueRemovalBfsQueue)
                if (blue) blueRemovalBfsQueue.Add(GetIndexFor(x, y, z));
                lock(sunlightRemovalBfsQueue)
                if (sun) sunlightRemovalBfsQueue.Add(GetIndexFor(x, y, z));

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

        public void SetLight(int x, int y, int z, int r, int g, int b, int sun, bool update = true)
        {

            if (update)
            {
                //RemoveLight(x, y, z, r == 0, g == 0, b == 0, sun == 0);
            }
            if (IsInsideChunk(x, y, z))
            {
                Tile tile = TileManager.GetTile(GetVoxel(x, y, z).tile_id);

                if (tile.tinted)
                {
                    r = (int)MathF.Max(r, sun);
                    g = (int)MathF.Max(g, sun);
                    b = (int)MathF.Max(b, sun);
                    sun = 0;
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
                    light[GetIndexFor(x, y, z)] &= 0b111111110000;
                    light[GetIndexFor(x, y, z)] |= R;
                    if (update)
                        lock(redBfsQueue)
                        redBfsQueue.Add(GetIndexFor(x, y, z));
                }
                if (g > -1)
                {
                    light[GetIndexFor(x, y, z)] &= 0b111100001111;
                    light[GetIndexFor(x, y, z)] |= G;
                    if (update)
                        lock(greenBfsQueue)
                        greenBfsQueue.Add(GetIndexFor(x, y, z));
                }
                
                if (b > -1)
                {
                    light[GetIndexFor(x, y, z)] &= 0b000011111111;
                    light[GetIndexFor(x, y, z)] |= B;
                    if (update)
                        lock(blueBfsQueue)
                        blueBfsQueue.Add(GetIndexFor(x, y, z));
                }

                if (sun > -1)
                {
                    sunlight[GetIndexFor(x, y, z)] = SUN;

                    if (update)
                        lock(sunlightBfsQueue)
                        sunlightBfsQueue.Add(GetIndexFor(x, y, z));
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
            if (distance <= Constants.CHUNK_LIGHT_DISTANCE)
            UpdateLights();
            if (lightRebuild)
            {
                lightRebuild = false;
                BuildMesh();
            }
        }

        private void MarkForLightBuild()
        {
            lightRebuild = true;
        }
        public void UpdateLights()
        {
            bool rerender = false;
            bool cu = false, cd = false, cl = false, cr = false, cb = false, cf = false;
            
            lock(sunlightRemovalBfsQueue)
            while (sunlightRemovalBfsQueue.Count > 0)
            {
                rerender = true;
                int node = sunlightRemovalBfsQueue[0];
                sunlightRemovalBfsQueue.Remove(node);

                int x = node % Constants.CHUNK_SIZE;
                int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                int lightLevel = GetSunlight(x, y, z);
                SetLight(x, y, z, -1, -1, -1, 0, false);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
                        else cf = true;
                    }

                    int neighborLevel = GetSunlight(x - 1, y, z);
                if (neighborLevel != 0 && neighborLevel < lightLevel)
                {
                    RemoveLight(x - 1, y, z, false, false, false, true);
                } else if (neighborLevel >= lightLevel)
                {
                    int index = GetIndexFor(x - 1, y, z);
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
                    sunlightBfsQueue.Add(index);
                }
            }
            lock(redRemovalBfsQueue)
            while (redRemovalBfsQueue.Count > 0)
            {
                rerender = true;
                int node = redRemovalBfsQueue[0];
                redRemovalBfsQueue.Remove(node);

                int x = node % Constants.CHUNK_SIZE;
                int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                int lightLevel = GetRedLight(x, y, z);
                SetLight(x, y, z, 0, -1, -1, -1, false);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
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
                    redBfsQueue.Add(index);
                }
            }
            lock(greenRemovalBfsQueue)
            while (greenRemovalBfsQueue.Count > 0)
            {
                rerender = true;
                int node = greenRemovalBfsQueue[0];
                greenRemovalBfsQueue.Remove(node);

                int x = node % Constants.CHUNK_SIZE;
                int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                int lightLevel = GetGreenLight(x, y, z);
                SetLight(x, y, z, -1, 0, -1, -1, false);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
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
                    greenBfsQueue.Add(index);
                }
            }
            lock(blueRemovalBfsQueue)
            while (blueRemovalBfsQueue.Count > 0)
            {
                rerender = true;
                int node = blueRemovalBfsQueue[0];
                blueRemovalBfsQueue.Remove(node);

                int x = node % Constants.CHUNK_SIZE;
                int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                int lightLevel = GetBlueLight(x, y, z);
                SetLight(x, y, z, -1, -1, 0, -1, false);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
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
                    blueBfsQueue.Add(index);
                }
            }


            //////////ADD LIGHTS
            lock (sunlightBfsQueue)
                while (sunlightBfsQueue.Count > 0)
                {
                    rerender = true;
                    int node = sunlightBfsQueue[0];
                    sunlightBfsQueue.Remove(node);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetSunlight(x, y, z);

                    if (!TileManager.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, -1, -1, -1, lightLevel - 1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetSunlight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, -1, -1, -1, lightLevel == 15 ? lightLevel : lightLevel - 1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
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
                    redBfsQueue.Remove(node);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetRedLight(x, y, z);

                    if (!TileManager.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetRedLight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, lightLevel - 1, -1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
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
                    greenBfsQueue.Remove(node);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetGreenLight(x, y, z);

                    if (!TileManager.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetGreenLight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, -1, lightLevel - 1, -1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
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
                    blueBfsQueue.Remove(node);

                    // x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE

                    int x = node % Constants.CHUNK_SIZE;
                    int y = ((node - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                    int z = (node - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);

                    if (x - 1 <= 0 || x + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (x - 1 <= 0) cl = true;
                        else cr = true;
                    }
                    if (y - 1 <= 0 || y + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (y - 1 <= 0) cd = true;
                        else
                            cu = true;
                    }
                    if (z - 1 <= 0 || z + 1 >= Constants.CHUNK_SIZE - 1)
                    {
                        if (z - 1 <= 0) cb = true;
                        else cf = true;
                    }

                    int lightLevel = GetBlueLight(x, y, z);

                    if (!TileManager.GetTile(GetVoxel(x - 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x - 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x - 1, y, z, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x + 1, y, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x + 1, y, z) + 2 <= lightLevel)
                    {
                        SetLight(x + 1, y, z, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z - 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y, z - 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z - 1, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y, z + 1).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y, z + 1) + 2 <= lightLevel)
                    {
                        SetLight(x, y, z + 1, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y - 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y - 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y - 1, z, -1, -1, lightLevel - 1, -1);
                    }
                    if (!TileManager.GetTile(GetVoxel(x, y + 1, z).tile_id).IsOpaqueAndNotGlowing() &&
                        GetBlueLight(x, y + 1, z) + 2 <= lightLevel)
                    {
                        SetLight(x, y + 1, z, -1, -1, lightLevel - 1, -1);
                    }
                }



            if (rerender)
            {
                MarkForRebuild();
            }
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

        public TileData GetVoxel(int x, int y, int z)
        {
            if (IsInsideChunk(x, y, z))
            {
                TileData data = voxels[GetIndexFor(x, y, z)];
                if (data == null) return TileManager.AIR.DefaultData;
                return data;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.GetVoxel(X, Y, Z);
            }

            return TileManager.AIR.DefaultData;
        }

        public bool SetVoxel(int x, int y, int z, TileData voxel)
        {
            if (IsInsideChunk(x, y, z))
            {
                if (TileManager.GetTile(voxel.tile_id) == TileManager.WATER)
                {
                    this.transparentRebuild = true;
                }
                if (voxels[GetIndexFor(x, y, z)] == null)
                {
                    solid_voxels += TileManager.GetTile(voxel.tile_id).TakesUpEntireSpace() ? 1 : 0;
                } else
                {
                    if (!TileManager.GetTile(voxels[GetIndexFor(x, y, z)].tile_id).TakesUpEntireSpace() &&
                                        TileManager.GetTile(voxel.tile_id).TakesUpEntireSpace())
                    {
                        solid_voxels++;
                    }
                    else
                    {
                        if (TileManager.GetTile(voxel.tile_id).TakesUpEntireSpace() != TileManager.GetTile(voxels[GetIndexFor(x, y, z)].tile_id).TakesUpEntireSpace())
                        {
                            solid_voxels--;
                        }
                    }
                }
                TileData last = voxels[GetIndexFor(x, y, z)];

                
                voxels[GetIndexFor(x, y, z)] = voxel;
                if (last != null)
                    last.UpdateLightWhenRemoved(this, x, y, z);
                
                if (voxel != null)
                    voxel.UpdateLightWhenPlaced(this, x, y, z);
                return true;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.SetVoxel(X, Y, Z, voxel);
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
            int X = GetX();
            int Y = GetY();
            int Z = GetZ();
            x = xx;
            y = yy;
            z = zz;
            if (xx >= Constants.CHUNK_SIZE)
            {
                x -= Constants.CHUNK_SIZE;
                X++;
            }
            if (x < 0)
            {
                x += Constants.CHUNK_SIZE;
                X--;
            }
            if (yy >= Constants.CHUNK_SIZE)
            {
                y -= Constants.CHUNK_SIZE;
                Y++;
            }
            if (y < 0)
            {
                y += Constants.CHUNK_SIZE;
                Y--;
            }
            if (zz >= Constants.CHUNK_SIZE)
            {
                z -= Constants.CHUNK_SIZE;
                Z++;
            }
            if (z < 0)
            {
                z += Constants.CHUNK_SIZE;
                Z--;
            }
            
            return chunkManager.TryGetChunk(X, Y, Z);
        }

        public TileData[] GetVoxels()
        {
            return voxels;
        }

        public World GetWorld()
        {
            return world;
        }

        public int GetX()
        {
            return x;
        }

        public int GetY()
        {
            return y;
        }

        public int GetZ()
        {
            return z;
        }

        bool extendedRebuild = false;
        public void MarkForRebuild(bool queue = false, bool extend = true)
        {
            if (queue)
            {
                chunkManager.QueueForRerender(this);
            } else
            {
                extendedRebuild = extend;
                rebuilding = true;
            }
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

        public void SetGenerated()
        {
            generated = true;
        }

        public int CompareTo(Chunk obj)
        {
            if (Inignoto.game.world.chunkManager == null) return 0;
            Vector3 pos = Inignoto.game.world.chunkManager.current_xyz;
            float dist1 = Vector3.Distance(cpos, pos);
            float dist2 = Vector3.Distance(obj.cpos, pos);
            if (dist1 == dist2) return 0;
            if (dist1 < dist2) return -1;
            return 1;
        }
    }
}
