using Inignoto.Utilities;

namespace Inignoto.World.Chunk
{
    class Chunk
    {
        private readonly int[] voxels;

        private readonly int x, y, z;
        private readonly World world;
        private readonly ChunkManager chunkManager;

        public Chunk(int x, int y, int z, World world)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
            this.voxels = new int[Constants.CHUNK_SIZE ^ 3];
            this.chunkManager = world.GetChunkManager();
        }

        public int GetVoxel(int x, int y, int z)
        {
            if (IsInsideChunk(x, y, z))
            {
                return voxels[GetIndexFor(x, y, z)];
            }

            Chunk chunk = GetAdjacentChunkForLocation(x, y, z, out int X, out int Y, out int Z);

            if (chunk != null)
            {
                return chunk.GetVoxel(X, Y, Z);
            }

            return 0;
        }

        public bool SetVoxel(int x, int y, int z, int voxel)
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
            return x + y * Constants.CHUNK_SIZE + z * (Constants.CHUNK_SIZE ^ 2);
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

        public int[] GetVoxels()
        {
            return voxels;
        }

        public World GetWorld()
        {
            return this.world;
        }

        public int GetX()
        {
            return this.x;
        }

        public int GetY()
        {
            return this.y;
        }

        public int GetZ()
        {
            return this.z;
        }
    }
}
