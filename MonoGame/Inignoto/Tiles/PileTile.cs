using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Tiles
{
    public class PileTile : Tile
    {
        public PileTile(string name) : base(name, null, false, 1)
        {
            BlockLight(false, false, false, false);
        }

        public override bool CanPlace(int x, int y, int z, Chunk chunk)
        {
            return TileRegistry.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).solid && TileRegistry.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).FullSpace;
        }
    }
}
