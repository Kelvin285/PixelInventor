using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Math
{
    public class IMathHelper
    {
        public static double Distance2(double x1, double y1, double x2, double y2)
        {
            return System.Math.Sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        }

        public static double Distance3(double x1, double y1, double z1, double x2, double y2, double z2)
        {
            return System.Math.Sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
        }
    }
}
