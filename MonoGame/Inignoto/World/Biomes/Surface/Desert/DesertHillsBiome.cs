﻿using Inignoto.Imported;
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
    public class DesertHillsBiome : SurfaceBiome
    {
        public DesertHillsBiome() : base(TileRegistry.SAND.DefaultData, TileRegistry.AIR.DefaultData, TileRegistry.STONE.DefaultData, TileRegistry.STONE.DefaultData)
        {
            
        }
        public override float GetHeightAt(float x, float z)
        {
            float noise = ChunkGenerator.noise.GetSimplex(x, z);
            //noise *= 10.0f;
            noise = MathF.Abs(1.0f - (noise * noise));
            noise *= 10.0f;

            return noise + 10;
        }
        public override void TryPlaceStructure(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, TileData voxel, TileData overlay, Chunk chunk, FastNoise noise, double n)
        {
            StructureRegistry.CACTUS.TryPlace(x, y, z, chunk_x, chunk_y, chunk_z, chunk, noise, n);
        }
    }
}
