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
    }
}
