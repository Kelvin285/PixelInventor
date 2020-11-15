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

        public static float Fma(float a, float b, float c)
        {
            return a * b + c;
        }

        public static double Fma(double a, double b, double c)
        {
            return a * b + c;
        }

        public static float Sin(float angle)
        {
            return (float)System.Math.Sin(angle);
        }

        public static float Cos(float angle)
        {
            return (float)System.Math.Cos(angle);
        }

        public static float CosFromSin(double sin, double angle)
        {
            double cos = System.Math.Sqrt(1.0 - sin * sin);
            double a = angle + System.Math.PI / 2.0;
            double b = a - (int)(a / (System.Math.PI * 2)) * System.Math.PI * 2;
            if (b < 0.0) b = System.Math.PI * 2 + b;
            if (b >= System.Math.PI) return (float)-cos;
            return (float)cos;
        }

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
