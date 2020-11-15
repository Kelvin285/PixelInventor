using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Utilities
{
    public class Constants
    {
        public static readonly int CHUNK_SIZE = 16;
        public static readonly int ACTIVE_CHUNK_DISTANCE = 5;
        public static readonly int CHUNK_LIGHT_DISTANCE = 3;

        public static readonly Vector4 COLOR_WHITE = new Vector4(1, 1, 1, 1);
    }
}
