using Inignoto.World.Chunks;
using Inignoto.GameSettings;
using Inignoto.Utilities;
using Inignoto.Tiles;
using Inignoto.Imported;

namespace Inignoto.World.Generator
{
    class ChunkGenerator
    {
        public readonly FastNoise noise;
        public ChunkGenerator()
        {
            noise = new FastNoise();
        }
        public void GenerateChunk(Chunks.Chunk chunk)
        {
            for (int chunk_x = 0; chunk_x < Constants.CHUNK_SIZE; chunk_x++)
            {
                for (int chunk_z = 0; chunk_z < Constants.CHUNK_SIZE; chunk_z++)
                {
                    int x = chunk_x + chunk.GetX() * Constants.CHUNK_SIZE;
                    int z = chunk_z + chunk.GetZ() * Constants.CHUNK_SIZE;

                    float height = noise.GetPerlinFractal(x, z) * 10;

                    int voxel_height = (int)height;

                    for (int chunk_y = 0; chunk_y < Constants.CHUNK_SIZE; chunk_y++)
                    {
                        int y = chunk_y + chunk.GetY() * Constants.CHUNK_SIZE;

                        if (y < voxel_height)
                        {
                            if (y < voxel_height - 1)
                            {
                                chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileManager.DIRT.DefaultData);
                            } else
                            {
                                chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileManager.GRASS.DefaultData);
                            }
                        } else
                        {
                            chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileManager.AIR.DefaultData);
                        }
                    }
                }
            }
            chunk.MarkForRebuild();
        }
    }
}
