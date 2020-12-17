using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Generator;
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
        public TileData RIVER_EARTH;

        public SurfaceBiome(TileData EARTH, TileData GRASS, TileData STONE, TileData RIVER_EARTH)
        {
            this.EARTH = EARTH;
            this.GRASS = GRASS;
            this.STONE = STONE;
            this.RIVER_EARTH = RIVER_EARTH;
        }

        public float GetRiverHeight(float x, float z)
        {

            float expand = 2;
            float rivers = MathF.Abs(ChunkGenerator.noise.GetSimplex((x / 2.0f) / expand, (z / 2.0f) / expand)) * expand;
            return rivers;
        }

        public virtual float GetHeightAt(float x, float z)
        {
            return 0;
        }

        public float GetBiomeHeight(float x, float z)
        {
            float height = GetHeightAt(x, z);
            float river = GetRiverHeight(x, z);
            if (river < 0.2f)
            {
                height = 0;
            }
            return height;
        }

        public virtual TileData GetOverlayAt(int x, int y, int z, int height)
        {
            return (y == height && height > 7) ? GRASS : TileRegistry.AIR.DefaultData;
        }

        public TileData GetVoxelOverlay(int x, int y, int z, int height, float river)
        {
            if (river <= 0.3f && height <= 7)
            {
                return TileRegistry.AIR.DefaultData;
            }
            return GetOverlayAt(x, y, z, height);
        }

        public virtual TileData GetVoxelAt(int x, int y, int z, int height)
        {
            TileData tile = TileRegistry.AIR.DefaultData;

            if (y <= height)
            {
                tile = EARTH;
                if (height <= 7)
                {
                    return RIVER_EARTH;
                }
                if (y <= height - 4) tile = STONE;
            }
            return tile;
        }
    }
}
