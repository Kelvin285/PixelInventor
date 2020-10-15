using Microsoft.Xna.Framework;

//This is a c# simplified version of the JOML (Java Opengl Math Library) Quaternionf class
namespace Inignoto.Math
{
    public class Quaternionf
    {
        private Quaternion rotation;
        public Quaternionf()
        {
            rotation = new Quaternion();
        }

        public Quaternionf(Quaternion rot)
        {
            rotation = new Quaternion(rot.X, rot.Y, rot.Z, rot.W);
        }

        public Quaternionf(float axisX, float axisY, float axisZ)
        {
            rotation = new Quaternion();
            this.RotateXYZ(axisX, axisY, axisZ);
        }

        public Quaternionf(float x, float y, float z, float w)
        {
            rotation = new Quaternion(x, y, z, w);
        }

        public Quaternion Rotation { get => rotation; set => rotation = value; }

        public Quaternionf Identity()
        {
            rotation.X = 0;
            rotation.Y = 0;
            rotation.Z = 0;
            rotation.W = 1;
            return this;
        }

        public Vector3f ToEulerAngles()
        {
            
            Vector3f pitchYawRoll = new Vector3f();

            float yaw = (float)System.Math.Atan2(2.0 * (Y * Z + W * X), W * W - X * X - Y * Y + Z * Z);
            float pitch = (float)System.Math.Asin(-2.0 * (X * Z - W * Y));
            float roll = (float)System.Math.Atan2(2.0 * (X * Y + W * Z), W * W + X * X - Y * Y - Z * Z);

            pitchYawRoll.X = pitch;
            pitchYawRoll.Y = yaw;
            pitchYawRoll.Z = roll;

            return pitchYawRoll;
        }

        public Quaternionf Normalize()
        {
            this.rotation.Normalize();
            return this;
        }

        public Quaternionf RotateXYZ(float angleX, float angleY, float angleZ)
        {
            Quaternion x = Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), angleX * 3.14f / 180.0f);
            Quaternion y = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), angleY * 3.14f / 180.0f);
            Quaternion z = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), angleZ * 3.14f / 180.0f);
            Rotation = Quaternion.Concatenate(x, Quaternion.Concatenate(y, z));
            return this;
        }


        public Quaternionf Slerp(Quaternionf target, float alpha)
        {
            float cosom = IMathHelper.Fma(rotation.X, target.rotation.X, IMathHelper.Fma(rotation.Y, target.rotation.Y, IMathHelper.Fma(rotation.Z, target.rotation.Z, rotation.W * target.rotation.W)));
            float absCosom = System.Math.Abs(cosom);
            float scale0, scale1;if (1.0f - absCosom > 1E-6f)
            {
                float sinSqr = 1.0f - absCosom * absCosom;
                float sinom = 1.0f / (float)System.Math.Sqrt(sinSqr);
                float omega = (float)System.Math.Atan2(sinSqr * sinom, absCosom);
                scale0 = IMathHelper.Sin((1.0f - alpha) * omega) * sinom;
                scale1 = IMathHelper.Sin(alpha * omega) * sinom;
            } else
            {
                scale0 = 1.0f - alpha;
                scale1 = alpha;
            }
            scale1 = cosom >= 0.0f ? scale1 : -scale1;
            rotation.X = IMathHelper.Fma(scale0, rotation.X, scale1 * target.rotation.X);
            rotation.Y = IMathHelper.Fma(scale0, rotation.Y, scale1 * target.rotation.Y);
            rotation.Z = IMathHelper.Fma(scale0, rotation.Z, scale1 * target.rotation.Z);
            rotation.W = IMathHelper.Fma(scale0, rotation.W, scale1 * target.rotation.W);
            return this;
        }

        public Quaternionf Scale(float factor)
        {
            float sqrt = (float)System.Math.Sqrt(factor);
            rotation.X = sqrt * rotation.X;
            rotation.Y = sqrt * rotation.Y;
            rotation.Z = sqrt * rotation.Z;
            rotation.W = sqrt * rotation.W;
            return this;
        }

        public Quaternionf Nlerp(Quaternionf q, float factor)
        {
            float cosom = IMathHelper.Fma(rotation.X, rotation.X, IMathHelper.Fma(rotation.Y, rotation.Y, IMathHelper.Fma(rotation.Z, rotation.Z, rotation.W * rotation.W)));
            float scale0 = 1.0f - factor;
            float scale1 = (cosom >= 0.0f) ? factor : -factor;
            rotation.X = IMathHelper.Fma(scale0, rotation.X, scale1 * rotation.X);
            rotation.Y = IMathHelper.Fma(scale0, rotation.Y, scale1 * rotation.Y);
            rotation.Z = IMathHelper.Fma(scale0, rotation.Z, scale1 * rotation.Z);
            rotation.W = IMathHelper.Fma(scale0, rotation.W, scale1 * rotation.W);
            return this;
        }

        public Quaternionf RotateX(float angle)
        {
            float sin = IMathHelper.Sin(angle * 0.5f);
            float cos = IMathHelper.CosFromSin(sin, angle * 0.5f);
            rotation.X = rotation.W * sin + rotation.X * cos;
            rotation.Y = rotation.Y * cos + rotation.Z * sin;
            rotation.Z = rotation.Z * cos - rotation.Y * sin;
            rotation.W = rotation.W * cos - rotation.X * sin;
            return this;
        }

        public Quaternionf RotateY(float angle)
        {
            float sin = IMathHelper.Sin(angle * 0.5f);
            float cos = IMathHelper.CosFromSin(sin, angle * 0.5f);
            rotation.X = rotation.X * cos - rotation.Z * sin;
            rotation.Y = rotation.W * sin + rotation.Y * cos;
            rotation.Z = rotation.X * sin + rotation.Z * cos;
            rotation.W = rotation.W * cos - rotation.Y * sin;
            return this;
        }

        public Quaternionf RotateZ(float angle)
        {
            float sin = IMathHelper.Sin(angle * 0.5f);
            float cos = IMathHelper.CosFromSin(sin, angle * 0.5f);
            rotation.X = rotation.X * cos + rotation.Y * sin;
            rotation.Y = rotation.Y * cos - rotation.X * sin;
            rotation.Z = rotation.W * sin + rotation.Z * cos;
            rotation.W = rotation.W * cos - rotation.Z * sin;
            return this;
        }

        public Quaternionf RotateLocalX(float angle)
        {
            float sin = IMathHelper.Sin(angle * 0.5f);
            float cos = IMathHelper.CosFromSin(sin, angle * 0.5f);
            rotation.X = cos * rotation.X + sin * rotation.W;
            rotation.Y = cos * rotation.Y - sin * rotation.Z;
            rotation.Z = cos * rotation.Z + sin * rotation.Y;
            rotation.W = cos * rotation.W - sin * rotation.X;
            return this;
        }

        public Quaternionf RotateLocalY(float angle)
        {
            float sin = IMathHelper.Sin(angle * 0.5f);
            float cos = IMathHelper.CosFromSin(sin, angle * 0.5f);
            rotation.X = cos * rotation.X + sin * rotation.Z;
            rotation.Y = cos * rotation.Y + sin * rotation.W;
            rotation.Z = cos * rotation.Z - sin * rotation.X;
            rotation.W = cos * rotation.W - sin * rotation.Y;
            return this;
        }

        public Quaternionf RotateLocalZ(float angle)
        {
            float sin = IMathHelper.Sin(angle * 0.5f);
            float cos = IMathHelper.CosFromSin(sin, angle * 0.5f);
            rotation.X = cos * rotation.X - sin * rotation.Y;
            rotation.Y = cos * rotation.Y + sin * rotation.X;
            rotation.Z = cos * rotation.Z + sin * rotation.W;
            rotation.W = cos * rotation.W - sin * rotation.Z;
            return this;
        }

        public float X => rotation.X;

        public float Y => rotation.Y;

        public float Z => rotation.Z;

        public float W => rotation.W;
    }
}
