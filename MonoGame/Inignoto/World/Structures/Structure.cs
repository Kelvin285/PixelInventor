using Inignoto.Imported;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Structures
{
    public abstract class Structure
    {
        public virtual void TryPlace(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise, double n, int orientation = 0)
        {

        }
    }
}
