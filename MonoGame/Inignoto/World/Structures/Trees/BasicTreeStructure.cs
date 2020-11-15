using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Structures.Trees
{
    public class BasicTreeStructure : Structure
    {
        public override void TryPlace(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise)
        {
            int n = (int)(MathF.Abs(noise.GetWhiteNoise(x, 0, z, 0)) * 10 * 100);
            if (n <= 5)
            {
                int height = chunk.GetWorld().random.Next(5, 10);
                for (int i = 0; i < height; i++)
                {
                    chunk.SetVoxel(chunk_x, chunk_y + i, chunk_z, TileManager.LOG.DefaultData);
                }
            }
        }
    }
}
