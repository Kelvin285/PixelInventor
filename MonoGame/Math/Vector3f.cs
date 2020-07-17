using Microsoft.Xna.Framework;

namespace Inignoto.Math
{
    class Vector3f
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
            this.vector = vec;
        }

        public Vector3f(Vector3f vec)
        {
            this.vector = new Vector3(vec.getX(), vec.getY(), vec.getZ());
        }

        public Vector3 getVector()
        {
            return vector;
        }

        public float getX()
        {
            return vector.X;
        }

        public float getY()
        {
            return vector.Y;
        }

        public float getZ()
        {
            return vector.Z;
        }

        public void set(float x, float y, float z)
        {
            vector.X = x;
            vector.Y = y;
            vector.Z = z;
        }

        public void set(Vector3 vec)
        {
            vector.X = vec.X;
            vector.Y = vec.Y;
            vector.Z = vec.Z;
        }

        public void set(Vector3f vec)
        {
            set(vec.getVector());
        }

        public void setX(float x)
        {
            vector.X = x;
        }

        public void setY(float y)
        {
            vector.Y = y;
        }

        public void setZ(float z)
        {
            vector.Z = z;
        }

        public void add(float x, float y, float z)
        {
            vector.X += x;
            vector.Y += y;
            vector.Z += z;
        }

        public void add(Vector3 vec)
        {
            add(vec.X, vec.Y, vec.Z);
        }

        public void add(Vector3f vec)
        {
            add(vec.vector);
        }

        public void sub(float x, float y, float z)
        {
            vector.X -= x;
            vector.Y -= y;
            vector.Z -= z;
        }

        public void sub(Vector3 vec)
        {
            sub(vec.X, vec.Y, vec.Z);
        }

        public void sub(Vector3f vec)
        {
            sub(vec.vector);
        }

        public void rotate(Quaternion quat)
        {
            Quaternion vec = new Quaternion(vector, 1);
            vec = Quaternion.Add(quat, vec);
           
            vector.X = vec.X;
            vector.Y = vec.Y;
            vector.Z = vec.Z;
        }

        public void rotate(Quaternionf quat)
        {
            rotate(quat.getRotation());
        }
    }
}
