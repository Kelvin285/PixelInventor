using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Structures.DesertStructures
{
    public class CactusStructure : Structure
    {
        public override void TryPlace(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise, double n)
        {
            if (chunk.GetVoxel(chunk_x, chunk_y - 1, chunk_z) == TileManager.SAND.DefaultData)
            {
                if (chunk.GetVoxel(chunk_x, chunk_y, chunk_z) == TileManager.AIR.DefaultData)
                {
                    if (n <= 2 && n > 0)
                    {
                        int height = (int)(MathF.Abs(MathF.Max(0, (float)n)) / 2 + 4);
                        for (int i = 0; i < height; i++)
                        {
                            if (chunk.GetVoxel(chunk_x, chunk_y + i, chunk_z) == TileManager.AIR.DefaultData)
                            {
                                if (i == height - 1)
                                {
                                    chunk.SetVoxel(chunk_x, chunk_y + i, chunk_z, TileManager.CACTUS.stateHolder.data[5]);
                                } else
                                {
                                    chunk.SetVoxel(chunk_x, chunk_y + i, chunk_z, TileManager.CACTUS.DefaultData);
                                }
                            }
                        }
                    }
                }
                
            }
        }
    }
}
