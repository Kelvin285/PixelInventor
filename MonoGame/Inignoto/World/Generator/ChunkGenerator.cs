using Inignoto.World.Chunks;
using Inignoto.GameSettings;
using Inignoto.Utilities;
using Inignoto.Tiles;
using Inignoto.Imported;
using Microsoft.Xna.Framework;
using System.Runtime.CompilerServices;
using System.CodeDom;
using System;
using Inignoto.Graphics.World;
using System.Diagnostics;
using Inignoto.World.Biomes;
using Inignoto.Tiles.Data;
using System.Collections.Generic;
using Inignoto.Math;

namespace Inignoto.World.Generator
{
    public class ChunkGenerator
    {
        public static readonly FastNoise noise = new FastNoise();
        
        public void GenerateChunk(Chunks.Chunk chunk)
        {
            if (chunk.Load())
            {
                chunk.MarkForRebuild();
                return;
            }

            StructureChunk schunk = null;
            if(chunk.GetWorld().chunkManager.HasStructureChunk(chunk.GetX(), chunk.GetY(), chunk.GetZ()))
                chunk.GetWorld().chunkManager.GetOrCreateStructureChunk(chunk.GetX(), chunk.GetY(), chunk.GetZ());

            for (int chunk_x = 0; chunk_x < Constants.CHUNK_SIZE; chunk_x++)
            {
                for (int chunk_z = 0; chunk_z < Constants.CHUNK_SIZE; chunk_z++)
                {
                    
                    int x = chunk_x + chunk.GetX() * Constants.CHUNK_SIZE;
                    int z = chunk_z + chunk.GetZ() * Constants.CHUNK_SIZE;

                    float[] heights = GetHeight(x, z, chunk.GetWorld().radius, chunk.GetWorld().properties.infinite);
                    float height = heights[0];
                    float river_height = heights[1];
                    SurfaceBiome biome = GetSurfaceBiome(x, z);

                    int voxel_height = (int)height;

                    double n = (MathF.Abs(noise.GetWhiteNoise(x * 1000, z * 1000, 0, 0)) * 10 * 100);

                    for (int chunk_y = 0; chunk_y < Constants.CHUNK_SIZE; chunk_y++)
                    {
                        int y = chunk_y + chunk.GetY() * Constants.CHUNK_SIZE;

                        TileData data = biome.GetVoxelAt(x, y, z, voxel_height);
                        TileData overlay = biome.GetVoxelOverlay(x, y, z, voxel_height, river_height);
                        
                        if (chunk.GetVoxel(chunk_x, chunk_y, chunk_z) == TileRegistry.AIR.DefaultData)
                        {
                            if (data == TileRegistry.AIR.DefaultData)
                            {
                                chunk.SetLight(chunk_x, chunk_y, chunk_z, 0, 0, 0, 15);
                            }
                            else
                            {
                                Tile tile = TileRegistry.GetTile(data.tile_id);
                                chunk.SetLight(chunk_x, chunk_y, chunk_z, tile.light_red, tile.light_green, tile.light_blue, tile.allows_sunlight ? -1 : 0);
                            }

                            chunk.SetVoxel(chunk_x, chunk_y, chunk_z, data);
                            chunk.SetOverlayVoxel(chunk_x, chunk_y, chunk_z, overlay);
                        }
                        
                        biome.TryPlaceStructure(x, y, z, chunk_x, chunk_y, chunk_z, data, overlay, chunk, noise, n);

                        if (y <= 5 && y > height)
                        {
                            if (TileRegistry.GetTile(chunk.GetVoxel(chunk_x, chunk_y, chunk_z).tile_id).IsReplaceable())
                                chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileRegistry.WATER.DefaultData);
                            chunk.SetOverlayVoxel(chunk_x, chunk_y - 1, chunk_z, TileRegistry.AIR.DefaultData);
                        }
                        if (schunk != null)
                        {
                            chunk.SetVoxel(chunk_x, chunk_y, chunk_z, schunk.GetTile(chunk_x, chunk_y, chunk_z));
                        }
                    }
                }
            }
            if (schunk != null)
            {
                chunk.GetWorld().chunkManager.RemoveStructureChunk(chunk.GetX(), chunk.GetY(), chunk.GetZ());
            }
            chunk.MarkForRebuild();
        }
         
        public float[] GetHeight(float x, float z, float radius, bool infinite)
        {
            if (infinite)
            {
                return GetPolarHeight(x, z, true);
            }
            float diameter = radius * 2;
            float length = diameter * 2;

            if (x >= 0 && z >= 0 && x <= length && z <= length)
            {
                if (z < diameter)
                {
                    float lat = (x / length) * MathHelper.TwoPi * radius;
                    float lon = (z / diameter) * radius;
                    return GetPolarHeight(lat, lon);
                }
                else
                {
                    if (IMathHelper.Distance2(x, z, radius, diameter + radius) <= radius)
                    {
                        float dist = (float)IMathHelper.Distance2(x, z, radius, diameter + radius);
                        float lon = -(radius - dist);

                        float atan = (float)System.Math.Atan2(z - (diameter + radius), x - radius);
                        if (atan < 0) atan += MathHelper.TwoPi;

                        float lat = atan * radius;
                        return GetPolarHeight(lat, lon);
                    }
                    else if (IMathHelper.Distance2(x, z, diameter + radius, diameter + radius) <= radius)
                    {
                        float dist = (float)IMathHelper.Distance2(x, z, diameter + radius, diameter + radius);
                        float lon = (radius - dist) + radius;

                        float atan = (float)System.Math.Atan2(z - (diameter + radius), x - (diameter + radius));
                        if (atan < 0) atan += MathHelper.TwoPi;

                        float lat = atan * radius;
                        return GetPolarHeight(lat, lon);
                    }
                }
            }

            return new float[] { -100, -100 };
        }

        public float[] GetPolarHeight(float X, float Z, bool infinite = false)
        {
            if (!infinite)
            Z *= 2.0f;
            float height = 0;
            float riverheight = 0;
            float i = 0;

            int count = 5;
            for (int x = -count; x < count + 1; x++)
            {
                for (int z = -count; z < count + 1; z++)
                {
                    SurfaceBiome biome = GetSurfaceBiome(x + X, z + Z);

                    float biomeheight = biome.GetBiomeHeight(x + X, z + Z);

                    height += biomeheight;
                    riverheight += biome.GetRiverHeight(x + X, z + Z);

                    i++;
                }
            }

            height /= i;
            riverheight /= i;
            return new float[] { height, riverheight };
        }

        public List<SurfaceBiome> GetSurfaceCategory(float x, float y)
        {
            float category_size = 5.0f;

            float cell = MathF.Abs(noise.GetCellular((x) / category_size, y / category_size));
            cell = MathF.Max(0, MathF.Min(1, cell));
            int category = (int)(cell * (BiomeRegistry.SURFACE_CATEGORIES.Count));
            category = (int)MathF.Max(0, MathF.Min(BiomeRegistry.SURFACE_CATEGORIES.Count - 1, category));
            return BiomeRegistry.SURFACE_CATEGORIES[category];
        }
        public SurfaceBiome GetSurfaceBiome(float x, float y)
        {
            List<SurfaceBiome> BIOMES = GetSurfaceCategory(x, y);
            float biome_size = 3.0f;

            float cell = MathF.Abs(noise.GetCellular((x) / biome_size, y / biome_size));
            cell = MathF.Max(0, MathF.Min(1, cell));
            int biome = (int)(cell * (BIOMES.Count));
            biome = (int)MathF.Max(0, MathF.Min(BIOMES.Count - 1, biome));
            return BIOMES[biome];
        }
    }
}
