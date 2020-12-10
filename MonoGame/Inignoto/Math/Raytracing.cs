using Microsoft.Xna.Framework;
using System;

namespace Inignoto.Math
{
    public class Raytracing
    {
        public struct RayBox
        {
            public Vector3 Min;
            public Vector3 Max;

            public RayBox(Vector3 Min, Vector3 Max)
            {
                this.Min = Min;
                this.Max = Max;
            }
        }

        public struct RayIntersection
        {
            public Vector2 lambda;
            public Vector3 normal;
            public Vector2 texStart;
            public Vector2 texEnd;
            public Vector2 texCurrent;
            public RayIntersection(Vector2 lambda, Vector3 normal, Vector2 texStart, Vector2 texEnd, Vector2 texCurrent)
            {
                this.lambda = lambda;
                this.normal = normal;
                this.texStart = texStart;
                this.texEnd = texEnd;
                this.texCurrent = texCurrent;
                if (normal.X == 0 && normal.Y == 0 && normal.Z == 0 || normal.Length() == 0)
                {
                    this.normal.Y = 1;
                }
            }
        }

        public static Vector3 RotateAround(Vector3 a, Vector3 b, Quaternion theta)
        {
            float x = a.X - b.X;
            float y = a.Y - b.Y;
            float z = a.Z - b.Z;
            Matrix matrix = Matrix.CreateTranslation(x, y, z) * Matrix.CreateFromQuaternion(theta);
            Vector3 vec = matrix.Translation;
            vec.X += b.X;
            vec.Y += b.Y;
            vec.Z += b.Z;
            return vec;
        }

        public static Vector3 RotateDir(Vector3 a, Quaternion theta)
        {
            Matrix matrix = Matrix.CreateTranslation(a) * Matrix.CreateFromQuaternion(theta);
            Vector3 vec = matrix.Translation;
            return vec;
        }

        public static RayIntersection IntersectBox(Vector3 origin, Vector3 dir, RayBox box, Quaternion rotation)
        {
            Vector3 center = (box.Min + box.Max) / 2.0f;
            Vector3 o2 = RotateAround(origin, center, rotation);
            Vector3 d2 = RotateDir(dir, rotation);
            return IntersectBox(o2, d2, box);
        }

        public static bool DoesCollisionOccur(Vector3 origin, Vector3 dir, RayBox box, Quaternion rotation)
        {
            RayIntersection i = IntersectBox(origin, dir, box, rotation);
            Vector2 l = i.lambda;
            if (l.X > 0.0 && l.X < l.Y)
            {
                return true;
            }
            return false;
        }

        public static Vector3 Min(Vector3 a, Vector3 b)
        {
            return new Vector3(a.X < b.X ? a.X : b.X, a.Y < b.Y ? a.Y : b.Y, a.Z < b.Z ? a.Z : b.Z);
        }

        public static Vector3 Max(Vector3 a, Vector3 b)
        {
            return new Vector3(a.X > b.X ? a.X : b.X, a.Y > b.Y ? a.Y : b.Y, a.Z > b.Z ? a.Z : b.Z);
        }

        public static Vector2 Min(Vector2 a, Vector2 b)
        {
            return new Vector2(a.X < b.X ? a.X : b.X, a.Y < b.Y ? a.Y : b.Y);
        }

        public static Vector2 Max(Vector2 a, Vector2 b)
        {
            return new Vector2(a.X > b.X ? a.X : b.X, a.Y > b.Y ? a.Y : b.Y);
        }

        public static float Max(float a, float b)
        {
            return a > b ? a : b;
        }

        public static float Min(float a, float b)
        {
            return a < b ? a : b;
        }

        public static RayIntersection IntersectBox(Vector3 origin, Vector3 dir, RayBox b)
        {
            const float bias = 0.0005f;
            Vector3 tMin = (b.Min - origin) / dir;
            Vector3 tMax = (b.Max - origin) / dir;
            Vector3 t1 = Min(tMin, tMax);
            Vector3 t2 = Max(tMin, tMax);
            float tNear = Max(Max(t1.X, t1.Y), t1.Z);
            float tFar = Min(Min(t2.X, t2.Y), t2.Z);

            Vector3 hitnear = origin + (dir * tNear);
            Vector3 normal = new Vector3(0.0f, 0.0f, 0.0f);

            if (hitnear.X >= b.Min.X - bias && hitnear.X <= b.Min.X + bias) normal = new Vector3(-1, 0, 0);
            if (hitnear.Y >= b.Min.Y - bias && hitnear.Y <= b.Min.Y + bias) normal = new Vector3(0, -1, 0);
            if (hitnear.Z >= b.Min.Z - bias && hitnear.Z <= b.Min.Z + bias) normal = new Vector3(0, 0, -1);
            if (hitnear.X >= b.Max.X - bias && hitnear.X <= b.Max.X + bias) normal = new Vector3(1, 0, 0);
            if (hitnear.Y >= b.Max.Y - bias && hitnear.Y <= b.Max.Y - bias) normal = new Vector3(0, 1, 0);
            if (hitnear.Z >= b.Max.Z - bias && hitnear.Z <= b.Max.Z + bias) normal = new Vector3(0, 0, 1);

            Vector2 texStart = new Vector2(0, 0);
            Vector2 texEnd = new Vector2(0, 0);
            Vector2 texCurrent = new Vector2(0, 0);

            if (normal.X == -1)
            {
                texStart = new Vector2(b.Min.Z, b.Min.Y);
                texEnd = new Vector2(b.Max.Z, b.Max.Y);
                texCurrent = new Vector2(hitnear.Z, hitnear.Y);
            }
            //bottom
            if (normal.Y == -1)
            {
                texStart = new Vector2(b.Min.X, b.Min.Z);
                texEnd = new Vector2(b.Max.X, b.Max.Z);
                texCurrent = new Vector2(hitnear.X, hitnear.Z);
            }
            //front
            if (normal.Z == -1)
            {
                texStart = new Vector2(b.Min.X, b.Min.Y);
                texEnd = new Vector2(b.Max.X, b.Max.Y);
                texCurrent = new Vector2(hitnear.X, hitnear.Y);
            }
            //right
            if (normal.X == 1)
            {
                texStart = new Vector2(b.Min.Z, b.Min.Y);
                texEnd = new Vector2(b.Max.Z, b.Max.Y);
                texCurrent = new Vector2(hitnear.Z, hitnear.Y);
            }
            //top
            if (normal.Y == 1)
            {
                texStart = new Vector2(b.Min.X, b.Min.Z);
                texEnd = new Vector2(b.Max.X, b.Max.Z);
                texCurrent = new Vector2(hitnear.X, hitnear.Z);
            }
            //back
            if (normal.Z == 1)
            {
                texStart = new Vector2(b.Min.X, b.Min.Y);
                texEnd = new Vector2(b.Max.X, b.Max.Y);
                texCurrent = new Vector2(hitnear.X, hitnear.Y);
            }

            return new RayIntersection(new Vector2(tNear, tFar), normal, texStart, texEnd, texCurrent);
        }
    }
}
