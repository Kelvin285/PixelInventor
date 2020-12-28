using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using Inignoto.World.Generator;
using Inignoto.World.Structures;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes.Surface.Grasslands
{
    public class DarkFlatsBiome : SurfaceBiome
    {
        public DarkFlatsBiome() : base(TileRegistry.DIRT.DefaultData, TileRegistry.PURPLE_GRASS.DefaultData, TileRegistry.SMOOTH_STONE.DefaultData, TileRegistry.SAND.DefaultData)
        {
            
        }

        public override TileData GetOverlayAt(int x, int y, int z, int height)
        {
            if (y == height)
            {
                return GRASS;
            }
            return TileRegistry.AIR.DefaultData;
        }

        public override float GetHeightAt(float x, float z)
        {
            return ChunkGenerator.noise.GetSimplex(x, z) * 5.0f + 10;
        }
        public override void TryPlaceStructure(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, TileData voxel, TileData overlay, Chunk chunk, FastNoise noise, double n)
        {
            double N = noise.GetWhiteNoise(x * 1000, y * 1000, z * 1000) * 1000;
            if (n <= 10 && overlay == TileRegistry.PURPLE_GRASS.DefaultData)
            {
                chunk.SetVoxel(chunk_x, chunk_y + 1, chunk_z, TileRegistry.ALCYONEUM.DefaultData);
            }
            if (N <= 10 && N > 0 && overlay != TileRegistry.AIR.DefaultData)
            {
                chunk.SetVoxel(chunk_x, chunk_y + 1, chunk_z, TileRegistry.ROCK_PILE.stateHolder.data[new Random().Next(TileRegistry.ROCK_PILE.stateHolder.data.Count)]);
            }
            if (n <= 5 && overlay == TileRegistry.PURPLE_GRASS.DefaultData)
            {
                chunk.SetVoxel(chunk_x, chunk_y + 1, chunk_z, TileRegistry.CANDENTIS.DefaultData);
            }

            if (N < 10)
            {
                if (chunk.GetVoxel(chunk_x, chunk_y, chunk_z) == TileRegistry.AIR.DefaultData)
                {
                    if (chunk.GetVoxel(chunk_x, chunk_y - 1, chunk_z) == TileRegistry.DIRT.DefaultData)
                    {
                        int hit = 0;
                        int height = (int)(N / 2 + 5);
                        for (int i = 0; i < height; i++)
                        {
                            if (chunk.GetVoxel(chunk_x, chunk_y + i, chunk_z) == TileRegistry.AIR.DefaultData)
                            {
                                chunk.SetVoxel(chunk_x, chunk_y + i, chunk_z, TileRegistry.MOON_STEM.DefaultData);
                                hit = i + 1;
                            } else
                            {
                                hit = i;
                                break;
                            }
                        }
                        if (hit > 1)
                        {
                            chunk.SetVoxel(chunk_x, chunk_y + hit - 1, chunk_z, TileRegistry.GLOW_BERRY.DefaultData);
                            chunk.SetVoxel(chunk_x, chunk_y + hit - 2, chunk_z, TileRegistry.MOON_LEAF.DefaultData);

                        }
                    }
                }
                
            }
        }
    }
}
