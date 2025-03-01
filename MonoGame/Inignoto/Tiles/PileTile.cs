﻿using Inignoto.Entities;
using Inignoto.Tiles.Data;
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
            SetReplaceable();
            SetTransparent();
        }

        public override bool CanPlace(int x, int y, int z, Chunk chunk)
        {
            return TileRegistry.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).solid && TileRegistry.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).FullSpace;
        }

        public override TileData GetStateForBlockPlacement(int x, int y, int z, Chunk chunk, TileFace face, Entity placer = null)
        {
            return stateHolder.data[(int)(chunk.GetWorld().DayTime) % stateHolder.data.Count];
        }
    }
}
