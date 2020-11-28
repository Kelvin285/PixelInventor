using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Tiles
{
    public class PlantTile : PileTile
    {

        public Tile[] placeOn;
        public PlantTile(string name, Tile[] placeOn) : base(name)
        {
            this.placeOn = placeOn;
            SetReplaceable(true);
        }

        public override TileData GetStateForBlockPlacement(int x, int y, int z, Chunk chunk, TileFace face)
        {
            return stateHolder.data[(int)(chunk.GetWorld().DayTime) % stateHolder.data.Count];
        }

        public override bool CanPlace(int x, int y, int z, Chunk chunk)
        {
            for (int i = 0; i < placeOn.Length; i++)
            {
                if (chunk.GetVoxel(x, y - 1, z).tile_id == placeOn[i].DefaultData.tile_id) return true;
            }
            return false;
        }
    }
}
