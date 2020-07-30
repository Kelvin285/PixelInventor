using Inignoto.Utilities;
using Inignoto.Tiles.Data;
using Inignoto.Tiles;
using System;
using Inignoto.Graphics.Mesh;

namespace Inignoto.World.Chunks
{
    class Chunk
    {
        private readonly TileData[] voxels;

        private readonly int x, y, z;
        private readonly World world;
        private readonly ChunkManager chunkManager;
        private bool rebuilding;
        private bool generated;

        public Mesh mesh;

        public Chunk(int x, int y, int z, World world)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
            voxels = new TileData[Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
            chunkManager = world.GetChunkManager();
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
                Y--;
            }
            if (y < 0)
            {
                y += Constants.CHUNK_SIZE;
                Y++;
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
            rebuilding = true;
            world.GetChunkManager().chunksToRerender.Add(this);
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
    }
}
