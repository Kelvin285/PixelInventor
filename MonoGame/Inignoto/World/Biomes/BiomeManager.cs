using Inignoto.World.Biomes.Surface.Grasslands;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes
{
    public class BiomeManager
    {
        public static List<Biome> REGISTRY = new List<Biome>();
        public static List<SurfaceBiome> SURFACE_REGISTRY = new List<SurfaceBiome>();

        public static List<List<SurfaceBiome>> SURFACE_CATEGORIES = new List<List<SurfaceBiome>>();
        public static List<SurfaceBiome> GRASSLAND = new List<SurfaceBiome>();
        public static List<SurfaceBiome> DESERT = new List<SurfaceBiome>();
        public static List<SurfaceBiome> SNOW = new List<SurfaceBiome>();

        public static PlainsBiome PLAINS;
        public static HillsBiome HILLS;
        public static DesertPlainsBiome DESERT_PLAINS;
        public static DesertHillsBiome DESERT_HILLS;
        public static SnowPlainsBiome SNOW_PLAINS;
        public static SnowHillsBiome SNOW_HILLS;

        public static void RegisterBiomes()
        {
            RegisterSurfaceBiome(PLAINS = new PlainsBiome());
            RegisterSurfaceBiome(HILLS = new HillsBiome());

            RegisterSurfaceBiome(DESERT_PLAINS = new DesertPlainsBiome());
            RegisterSurfaceBiome(DESERT_HILLS = new DesertHillsBiome());

            RegisterSurfaceBiome(SNOW_PLAINS = new SnowPlainsBiome());
            RegisterSurfaceBiome(SNOW_HILLS = new SnowHillsBiome());

            GRASSLAND.Add(PLAINS);
            GRASSLAND.Add(HILLS);
            SURFACE_CATEGORIES.Add(GRASSLAND);

            DESERT.Add(DESERT_PLAINS);
            DESERT.Add(DESERT_HILLS);
            SURFACE_CATEGORIES.Add(DESERT);

            SNOW.Add(SNOW_PLAINS);
            SNOW.Add(SNOW_HILLS);
            SURFACE_CATEGORIES.Add(SNOW);
        }

        public static void RegisterBiome(Biome biome)
        {
            REGISTRY.Add(biome);
        }
        public static void RegisterSurfaceBiome(SurfaceBiome biome)
        {
            SURFACE_REGISTRY.Add(biome);
            REGISTRY.Add(biome);
        }
    }
}
