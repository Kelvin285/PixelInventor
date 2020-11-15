using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Generator;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes.Surface.Grasslands
{
    public class SnowHillsBiome : SurfaceBiome
    {
        public SnowHillsBiome() : base(TileManager.SNOW.DefaultData, TileManager.AIR.DefaultData, TileManager.ICE.DefaultData)
        {
            
        }
        public override float GetHeightAt(float x, float z)
        {
            float noise = ChunkGenerator.noise.GetSimplex(x, z) * 5.0f;
            if (noise > 0) noise *= 10;
            return noise + 10;
        }
    }
}
