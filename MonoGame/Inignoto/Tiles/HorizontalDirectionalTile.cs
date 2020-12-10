using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using Microsoft.Xna.Framework.Audio;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.Tiles
{
    public class HorizontalDirectionalTile : Tile
    {
        public HorizontalDirectionalTile(string name, SoundEffect[] sound, bool solid = true, int hits = 1) : base(name, sound, solid, hits)
        {

        }

        public override TileData GetStateForBlockPlacement(int x, int y, int z, Chunk chunk, TileFace face)
        {
            switch (face)
            {
                case TileFace.RIGHT:
                    return stateHolder.data[1];
                case TileFace.FRONT:
                    return stateHolder.data[2];
                case TileFace.LEFT:
                    return stateHolder.data[3];
                case TileFace.BACK:
                    return stateHolder.data[0];
            }

            return DefaultData;
        }

    }
        
}
