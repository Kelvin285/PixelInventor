using Inignoto.Utilities;
using Inignoto.Tiles.Data;
using Inignoto.Tiles;
using System;
using Inignoto.Graphics.Mesh;
using Inignoto.Graphics.World;
using Inignoto.Math;
using Microsoft.Xna.Framework;

namespace Inignoto.World.Chunks
{
    public class Chunk
    {
        private readonly TileData[] voxels;
        private readonly int[] light;
        private readonly int[] sunlight;

        private readonly int x, y, z;
        private readonly World world;
        private readonly ChunkManager chunkManager;
        private bool rebuilding;
        private bool generated;

        public Mesh mesh;
        public Mesh secondMesh;

        public Mesh waterMesh;
        public Mesh secondWaterMesh;

        public bool transparentRebuild = false;

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
        }

        public void BuildMesh()
        {
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
                return light[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
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
                return sunlight[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.GetSunlight(X, Y, Z);
            }

            return 0;
        }

        public void SetLight(int x, int y, int z, int r, int g, int b, int sun)
        {
            if (IsInsideChunk(x, y, z))
            {
                int R = System.Math.Max(System.Math.Min(15, r), 0);
                int G = System.Math.Max(System.Math.Min(15, g), 0) << 4;
                int B = System.Math.Max(System.Math.Min(15, b), 0) << 8;
                int SUN = System.Math.Max(System.Math.Min(15, sun), 0);

                if (r > -1)
                {
                    light[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] &= 0b111111110000;
                    light[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] |= R;
                }
                if (g > -1)
                {
                    light[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] &= 0b111100001111;
                    light[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] |= G;
                }
                
                if (b > -1)
                {
                    light[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] &= 0b000011111111;
                    light[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] |= B;
                }
                
                if (sun > -1)
                sunlight[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] = SUN;
                return;
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                chunk.SetLight(X, Y, Z, r, g, b, sun);
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
                voxels[GetIndexFor(x, y, z)] = voxel;
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

        public void MarkForRebuild()
        {
            MarkForRebuild(Tile.TileRayTraceType.BLOCK);
            //MarkForRebuild(Tile.TileRayTraceType.FLUID);
        }
        public void MarkForRebuild(Tile.TileRayTraceType raytraceType)
        {
            rebuilding = true;
            //world.GetChunkManager().chunksToRerender.Add(this);
            this.BuildMesh();
        }

        public void FinishRebuilding()
        {
            rebuilding = false;

            Chunk chunk = chunkManager.TryGetChunk(GetX() - 1, GetY(), GetZ());
            if (chunk != null && transparentRebuild) chunkManager.QueueForRerender(chunk);
            chunk = chunkManager.TryGetChunk(GetX() + 1, GetY(), GetZ());
            if (chunk != null && transparentRebuild) chunkManager.QueueForRerender(chunk);
            chunk = chunkManager.TryGetChunk(GetX(), GetY() - 1, GetZ());
            if (chunk != null && transparentRebuild) chunkManager.QueueForRerender(chunk);
            chunk = chunkManager.TryGetChunk(GetX(), GetY() + 1, GetZ());
            if (chunk != null && transparentRebuild) chunkManager.QueueForRerender(chunk);
            chunk = chunkManager.TryGetChunk(GetX(), GetY(), GetZ() - 1);
            if (chunk != null && transparentRebuild) chunkManager.QueueForRerender(chunk);
            chunk = chunkManager.TryGetChunk(GetX(), GetY(), GetZ() + 1);
            if (chunk != null && transparentRebuild) chunkManager.QueueForRerender(chunk);
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

    }
}
