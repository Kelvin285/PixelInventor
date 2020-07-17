using Microsoft.Xna.Framework;

namespace Inignoto.Math
{
    class Quaternionf
    {
        private Quaternion rotation;
        public Quaternionf()
        {
            rotation = new Quaternion();
        }

        public Quaternionf(float axisX, float axisY, float axisZ)
        {
            rotation = new Quaternion();
            this.rotate(axisX, axisY, axisZ);
        }

        public void rotateX(float rotation)
        {
            Quaternion rot = Quaternion.CreateFromAxisAngle(Vector3.UnitX, rotation);
            this.rotation = rot * this.rotation;
        }

        public void rotateY(float rotation)
        {
            Quaternion rot = Quaternion.CreateFromAxisAngle(Vector3.UnitY, rotation);
            this.rotation = rot * this.rotation;
        }

        public void rotateZ(float rotation)
        {
            Quaternion rot = Quaternion.CreateFromAxisAngle(Vector3.UnitZ, rotation);
            this.rotation = rot * this.rotation;
        }

        public void rotate(float x, float y, float z)
        {
            rotateX(x);
            rotateY(y);
            rotateZ(z);
        }

        public Quaternion getRotation()
        {
            return rotation;
        }
    }
}
