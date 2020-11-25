using Inignoto.Imported;
using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Biomes
{
    public abstract class Biome
    {
        public virtual void TryPlaceStructure(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, TileData voxel, TileData overlay, Chunk chunk, FastNoise noise, double n)
        {

        }
    }
}
