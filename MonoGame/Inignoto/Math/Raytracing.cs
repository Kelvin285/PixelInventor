using Microsoft.Xna.Framework;
namespace Inignoto.Math
{
    public class Raytracing
    {
        public struct RayBox
        {
            public Vector3f Min;
            public Vector3f Max;

            public RayBox(Vector3f Min, Vector3f Max)
            {
                this.Min = Min;
                this.Max = Max;
            }
        }

        public struct RayIntersection
        {
            public Vector2 lambda;
            public Vector3f normal;
            public Vector2 texStart;
            public Vector2 texEnd;
            public Vector2 texCurrent;
            public RayIntersection(Vector2 lambda, Vector3f normal, Vector2 texStart, Vector2 texEnd, Vector2 texCurrent)
            {
                this.lambda = lambda;
                this.normal = normal;
                this.texStart = texStart;
                this.texEnd = texEnd;
                this.texCurrent = texCurrent;
            }
        }

        public static Vector3f RotateAround(Vector3f a, Vector3f b, Quaternionf theta)
        {
            float x = a.X - b.X;
            float y = a.Y - b.Y;
            float z = a.Z - b.Z;
            Vector3f vec = new Vector3f(x, y, z);
            vec.Rotate(theta);
            vec.X += b.X;
            vec.Y += b.Y;
            vec.Z += b.Z;
            return vec;
        }

        public static Vector3f RotateDir(Vector3f a, Quaternionf theta)
        {
            Vector3f vec = new Vector3f(a);
            vec.Rotate(theta);
            return vec;
        }

        public static RayIntersection IntersectBox(Vector3f origin, Vector3f dir, RayBox box, Quaternionf rotation)
        {
            Vector3f center = new Vector3f(box.Min).Add(box.Max).Div(2.0f);
            Vector3f o2 = RotateAround(origin, center, rotation);
            Vector3f d2 = RotateDir(dir, rotation);
            return IntersectBox(o2, d2, box);
        }

        public static bool DoesCollisionOccur(Vector3f origin, Vector3f dir, RayBox box, Quaternionf rotation)
        {
            RayIntersection i = IntersectBox(origin, dir, box, rotation);
            Vector2 l = i.lambda;
            if (l.X > 0.0 && l.X < l.Y)
            {
                return true;
            }
            return false;
        }

        public static Vector3f Min(Vector3f a, Vector3f b)
        {
            return new Vector3f(a.X < b.X ? a.X : b.X, a.Y < b.Y ? a.Y : b.Y, a.Z < b.Z ? a.Z : b.Z);
        }

        public static Vector3f Max(Vector3f a, Vector3f b)
        {
            return new Vector3f(a.X > b.X ? a.X : b.X, a.Y > b.Y ? a.Y : b.Y, a.Z > b.Z ? a.Z : b.Z);
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

        public static RayIntersection IntersectBox(Vector3f origin, Vector3f dir, RayBox b)
        {
            const float bias = 0.005f;
            Vector3f tMin = new Vector3f(b.Min).Sub(origin).Div(dir);
            Vector3f tMax = new Vector3f(b.Max).Sub(origin).Div(dir);
            Vector3f t1 = Min(tMin, tMax);
            Vector3f t2 = Max(tMin, tMax);
            float tNear = Max(Max(t1.X, t1.Y), t1.Z);
            float tFar = Min(Min(t2.X, t2.Y), t2.Z);

            Vector3f hitnear = new Vector3f(origin).Add(new Vector3f(dir).Mul(tNear));
            Vector3f normal = new Vector3f(0.0f, 0.0f, 0.0f);

            if (hitnear.X >= b.Min.X - bias && hitnear.X <= b.Min.X + bias) normal = new Vector3f(-1, 0, 0);
            if (hitnear.Y >= b.Min.Y - bias && hitnear.Y <= b.Min.Y + bias) normal = new Vector3f(0, -1, 0);
            if (hitnear.Z >= b.Min.Z - bias && hitnear.Z <= b.Min.Z + bias) normal = new Vector3f(0, 0, -1);
            if (hitnear.X >= b.Max.X - bias && hitnear.X <= b.Max.X + bias) normal = new Vector3f(1, 0, 0);
            if (hitnear.Y >= b.Max.Y - bias && hitnear.Y <= b.Max.Y - bias) normal = new Vector3f(0, 1, 0);
            if (hitnear.Z >= b.Max.Z - bias && hitnear.Z <= b.Max.Z + bias) normal = new Vector3f(0, 0, 1);

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
