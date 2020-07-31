using Microsoft.Xna.Framework;

namespace Inignoto.Math
{
    public class Vector3f
    {
        private Vector3 vector;

        public Vector3f()
        {
            this.vector = new Vector3();
        }


        public Vector3f(float x, float y, float z)
        {
            vector = new Vector3(x, y, z);
        }

        public Vector3f(Vector3 vec)
        {
            this.vector = new Vector3(vec.X, vec.Y, vec.Z);
        }

        public Vector3f(Vector3f vec)
        {
            this.vector = new Vector3(vec.X, vec.Y, vec.Z);
        }

        public Vector3 Vector => vector;

        public float X { get => vector.X; set => vector.X = value; }

        public float Y { get => vector.Y; set => vector.Y = value; }

        public float Z { get => vector.Z; set => vector.Z = value; }


        public void Set(float x, float y, float z)
        {
            vector.X = x;
            vector.Y = y;
            vector.Z = z;
        }

        public void Set(Vector3 vec)
        {
            vector.X = vec.X;
            vector.Y = vec.Y;
            vector.Z = vec.Z;
        }

        public void Set(Vector3f vec)
        {
            Set(vec.Vector);
        }

        public void SetX(float x)
        {
            vector.X = x;
        }

        public void SetY(float y)
        {
            vector.Y = y;
        }

        public void SetZ(float z)
        {
            vector.Z = z;
        }

        public Vector3f Add(float x, float y, float z)
        {
            vector.X += x;
            vector.Y += y;
            vector.Z += z;
            return this;
        }

        public Vector3f Add(Vector3 vec)
        {
            return Add(vec.X, vec.Y, vec.Z);
        }

        public Vector3f Add(Vector3f vec)
        {
            return Add(vec.vector);
        }

        public Vector3f Sub(float x, float y, float z)
        {
            vector.X -= x;
            vector.Y -= y;
            vector.Z -= z;
            return this;
        }

        public Vector3f Sub(Vector3 vec)
        {
           return Sub(vec.X, vec.Y, vec.Z);
        }

        public Vector3f Sub(Vector3f vec)
        {
            return Sub(vec.vector);
        }

        public Vector3f Mul(float f)
        {
            vector *= f;
            return this;
        }

        public Vector3f Mul(float x, float y, float z)
        {
            vector.X *= x;
            vector.Y *= y;
            vector.Z *= z;
            return this;
        }

        public Vector3f Mul(Vector3f vec)
        {
            vector *= vec.vector;
            return this;
        }

        public Vector3f Mul(Vector3 vec)
        {
            vector *= vec;
            return this;
        }

        public Vector3f Div(float val)
        {
            vector /= val;
            return this;
        }

        public Vector3f Div(float x, float y, float z)
        {
            vector.X /= x;
            vector.Y /= y;
            vector.Z /= z;
            return this;
        }

        public Vector3f Div(Vector3f vec)
        {
            vector /= vec.vector;
            return this;
        }

        public Vector3f Div(Vector3 vec)
        {
            vector /= vec;
            return this;
        }

        public Vector3f Rotate(Quaternionf quat)
        {
            return Rotate(quat.Rotation);
        }

        public Vector3f Rotate(Quaternion quat)
        {

            vector = Vector3.Transform(vector, Matrix.CreateFromQuaternion(quat));
            
            return this;
        }

        public float DistanceTo(Vector3f vec)
        {
            return Vector3.Distance(vec.vector, vector);
        }

        public float DistanceTo(Vector3 vec)
        {
            return Vector3.Distance(vector, vec);
        }

        public Vector2 XY => new Vector2(X, Y);
        public Vector2 YX => new Vector2(Y, X);
        public Vector2 XZ => new Vector2(X, Z);
        public Vector2 ZX => new Vector2(Z, X);
        public Vector2 YZ => new Vector2(Y, Z);
        public Vector2 ZY => new Vector2(Z, Y);
        public Vector3 XYZ => new Vector3(X, Y, Z);
        public Vector3 XZY => new Vector3(X, Z, Y);
        public Vector3 ZYX => new Vector3(Z, Y, X);
        public Vector3 ZXY => new Vector3(Z, X, Y);
        public Vector3 YXZ => new Vector3(Y, X, Z);
        public Vector3 YZX => new Vector3(Y, Z, X);
    }
}
