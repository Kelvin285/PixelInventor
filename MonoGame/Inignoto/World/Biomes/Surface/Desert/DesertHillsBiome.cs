using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Generator;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes.Surface.Grasslands
{
    public class DesertHillsBiome : SurfaceBiome
    {
        public DesertHillsBiome() : base(TileManager.SAND.DefaultData, TileManager.AIR.DefaultData, TileManager.STONE.DefaultData)
        {
            
        }
        public override float GetHeightAt(float x, float z)
        {
            float noise = ChunkGenerator.noise.GetSimplex(x, z);
            noise *= 10.0f;
            noise = MathF.Abs(1.0f - (noise * noise));
            noise *= 5.0f;

            return noise + 10;
        }
    }
}
