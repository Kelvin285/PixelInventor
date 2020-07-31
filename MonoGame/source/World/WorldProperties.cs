using Inignoto.World.Generator;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.World
{
    public class WorldProperties
    {
        public readonly ChunkGenerator generator;
        public readonly float gravity;

        public WorldProperties()
        {
            generator = new ChunkGenerator();
            gravity = 9.81f;
        }
    }
}
