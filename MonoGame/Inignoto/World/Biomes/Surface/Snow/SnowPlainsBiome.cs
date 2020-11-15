using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Generator;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes.Surface.Grasslands
{
    public class SnowPlainsBiome : SurfaceBiome
    {
        public SnowPlainsBiome() : base(TileManager.SNOW.DefaultData, TileManager.AIR.DefaultData, TileManager.ICE.DefaultData)
        {
            
        }
        public override float GetHeightAt(float x, float z)
        {
            return ChunkGenerator.noise.GetSimplex(x, z) * 5.0f + 10;
        }
    }
}
