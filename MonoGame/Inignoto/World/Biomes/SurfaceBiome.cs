using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes
{
    public abstract class SurfaceBiome : Biome
    {
        public TileData EARTH;
        public TileData GRASS;
        public TileData STONE;

        public SurfaceBiome(TileData EARTH, TileData GRASS, TileData STONE)
        {
            this.EARTH = EARTH;
            this.GRASS = GRASS;
            this.STONE = STONE;
        }

        public virtual float GetHeightAt(float x, float z)
        {
            return 0;
        }

        public virtual TileData GetOverlayAt(int x, int y, int z, int height)
        {
            return y == height ? GRASS : TileManager.AIR.DefaultData;
        }

        public virtual TileData GetVoxelAt(int x, int y, int z, int height)
        {
            TileData tile = TileManager.AIR.DefaultData;
            if (y <= height)
            {
                tile = EARTH;
                if (y <= height - 4) tile = STONE;
            }
            return tile;
        }
    }
}
