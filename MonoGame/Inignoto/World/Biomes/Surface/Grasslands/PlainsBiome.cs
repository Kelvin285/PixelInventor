using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Generator;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes.Surface.Grasslands
{
    public class PlainsBiome : SurfaceBiome
    {
        public PlainsBiome() : base(TileManager.DIRT.DefaultData, TileManager.GRASS.DefaultData, TileManager.SMOOTH_STONE.DefaultData)
        {
            
        }
        public override float GetHeightAt(float x, float z)
        {
            return ChunkGenerator.noise.GetSimplex(x, z) * 5.0f + 10;
        }
    }
}
