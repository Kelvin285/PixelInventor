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
    public class HillsBiome : SurfaceBiome
    {
        public HillsBiome() : base(TileManager.DIRT.DefaultData, TileManager.GRASS.DefaultData, TileManager.SMOOTH_STONE.DefaultData)
        {
            
        }
        public override float GetHeightAt(float x, float z)
        {
            float noise = ChunkGenerator.noise.GetSimplex(x, z) * 5.0f;
            if (noise > 0) noise *= 10;
            return noise + 10;
        }

        public override void TryPlaceStructure(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, TileData voxel, TileData overlay, Chunk chunk, FastNoise noise)
        {
            if (overlay == TileManager.GRASS.DefaultData)
            {
                StructureManager.OAK_TREE1.TryPlace(x, y, z, chunk_x, chunk_y, chunk_z, chunk, noise);
            }
        }
    }
}
