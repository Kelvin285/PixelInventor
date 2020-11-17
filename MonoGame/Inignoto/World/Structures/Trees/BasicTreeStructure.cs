using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.World.Structures.Trees
{
    public class BasicTreeStructure : LoadedStructure
    {

        public BasicTreeStructure(string structure, TilePos offset) : base(structure, offset)
        {

        }

        public override void TryPlace(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise)
        {
            int n = (int)(MathF.Abs(noise.GetWhiteNoise(x, 0, z, 0)) * 10 * 100);
            if (n <= 5)
            {
                TilePos current = new TilePos(0, 0, 0);
                foreach (TilePos pos in tiles.Keys)
                {
                    current.x = pos.x + offset.x;
                    current.y = pos.y + offset.y;
                    current.z = pos.z + offset.z;
                    chunk.SetVoxel(x + current.x, y + current.y, z + current.z, Tiles.Data.TileDataHolder.REGISTRY[tiles[pos][0]]);
                    chunk.SetOverlayVoxel(x + current.x, y + current.y, z + current.z, Tiles.Data.TileDataHolder.REGISTRY[tiles[pos][1]]);
                }
            }
        }
    }
}
