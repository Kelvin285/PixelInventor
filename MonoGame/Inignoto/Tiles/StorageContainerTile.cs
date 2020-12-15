using Inignoto.Entities;
using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using Microsoft.Xna.Framework.Audio;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.Tiles
{
    public class StorageContainerTile : Tile
    {
        public StorageContainerTile(string name, SoundEffect[] sound, bool solid = true, int hits = 1) : base(name, sound, solid, hits)
        {

        }

        public override TileData GetStateForBlockPlacement(int x, int y, int z, Chunk chunk, TileFace face, Entity placer = null)
        {

            if (placer != null)
            {
                double dir = ((placer.look.Y) % 360);
                if (dir < 0) dir = 360 + dir;

                int DIR = 0;

                if (dir >= 90 - 45 && dir <= 90 + 45)
                {
                    DIR = 1;
                }

                if (dir >= 180 - 45 && dir <= 180 + 45)
                {
                    DIR = 2;
                }

                if (dir >= 270 - 45 && dir <= 270 + 45)
                {
                    DIR = 3;
                }

                if (DIR == 0) face = TileFace.BACK;
                if (DIR == 1) face = TileFace.RIGHT;
                if (DIR == 2) face = TileFace.FRONT;
                if (DIR == 3) face = TileFace.LEFT;
            }

            switch (face)
            {
                case TileFace.RIGHT:
                    return stateHolder.data[1];
                case TileFace.FRONT:
                    return stateHolder.data[0];
                case TileFace.LEFT:
                    return stateHolder.data[3];
                case TileFace.BACK:
                    return stateHolder.data[2];
            }

            return DefaultData;
        }

    }
        
}
