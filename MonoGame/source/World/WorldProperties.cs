using Inignoto.World.Generator;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.World
{
    class WorldProperties
    {
        public readonly ChunkGenerator generator;
        public WorldProperties()
        {
            generator = new ChunkGenerator();
        }
    }
}
