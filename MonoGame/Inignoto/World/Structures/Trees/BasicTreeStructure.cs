using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.World.Structures.Trees
{
    public class BasicTreeStructure : Structure
    {
        public virtual void Grow(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise, double n, int orientation = 0)
        {

        }
    }
}
