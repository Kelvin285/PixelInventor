using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.World.Structures.Trees
{
    public class BasicTreeStructure : LoadedStructure
    {

        public TileData ground;
        public BasicTreeStructure(string structure, TilePos offset, TileData ground) : base(structure, offset)
        {
            this.ground = ground;
        }

        public override void TryPlace(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise)
        {
            int n = (int)(MathF.Abs(noise.GetWhiteNoise(x, 0, z, 0)) * 10 * 100);
            if (n <= 5)
            {
                TilePos current = new TilePos(0, 0, 0);
                if (chunk.GetVoxel(chunk_x, chunk_y, chunk_z) == ground)
                foreach (TilePos pos in tiles.Keys)
                {
                    current.x = pos.x + offset.x;
                    current.y = pos.y + offset.y;
                    current.z = pos.z + offset.z;
                    if (tiles[pos][0] != TileManager.AIR.DefaultData.index)
                    {
                        chunk.SetVoxel(chunk_x + current.x, chunk_y + current.y, chunk_z + current.z, Tiles.Data.TileDataHolder.REGISTRY[tiles[pos][0]]);
                        chunk.SetOverlayVoxel(chunk_x + current.x, chunk_y + current.y, chunk_z + current.z, Tiles.Data.TileDataHolder.REGISTRY[tiles[pos][1]]);
                    }
                    
                }
            }
        }
    }
}
