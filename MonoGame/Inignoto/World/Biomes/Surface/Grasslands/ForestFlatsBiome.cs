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
    public class ForestFlatsBiome : SurfaceBiome
    {
        public ForestFlatsBiome() : base(TileRegistry.DIRT.DefaultData, TileRegistry.GRASS.DefaultData, TileRegistry.SMOOTH_STONE.DefaultData)
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
            if (n <= 100 && overlay == TileRegistry.GRASS.DefaultData)
            {
                chunk.SetVoxel(chunk_x, chunk_y + 1, chunk_z, TileRegistry.TALL_GRASS.DefaultData);
            }
            if (N <= 10 && N > 0 && overlay != TileRegistry.AIR.DefaultData)
            {
                chunk.SetVoxel(chunk_x, chunk_y + 1, chunk_z, TileRegistry.ROCK_PILE.stateHolder.data[new Random().Next(TileRegistry.ROCK_PILE.stateHolder.data.Count)]);
            }
            if (n <= 5 && overlay == TileRegistry.GRASS.DefaultData)
            {
                chunk.SetVoxel(chunk_x, chunk_y + 1, chunk_z, TileRegistry.CANDENTIS.DefaultData);
            }

            if (N < 10)
            {
                StructureRegistry.REDWOOD_TREE.TryPlace(x, y, z, chunk_x, chunk_y, chunk_z, chunk, noise, n);
            } else
            {
                StructureRegistry.OAK_TREE.TryPlace(x, y, z, chunk_x, chunk_y, chunk_z, chunk, noise, n);
            }
        }
    }
}
