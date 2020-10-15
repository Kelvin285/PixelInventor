using Inignoto.Math;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Graphics.Models
{
    public class KeyTransformation
    {
        public Vector3f position = new Vector3f();
        public Quaternionf rotation = new Quaternionf();
        public Vector3f scale = new Vector3f(1, 1, 1);
        public Vector3f size = new Vector3f();
        public Vector2 uv = new Vector2(0, 0);
        public Vector3f origin = new Vector3f();

        public bool visible = true;
        public bool locked = true;

        public Vector3f look = new Vector3f(0, 0, 1);
        public Vector3f axisAngles = new Vector3f(0, 0, 0);

        public void Translate(Vector3f translation)
        {
            position.Add(translation);
        }

        public void Rotate(Vector3f rotation)
        {
            Rotate(rotation, new Vector3f(position).Add(new Vector3f(origin).Rotate(this.rotation.Rotation)));
        }

        public void Rotate(Quaternion rot, Vector3f origin)
        {
            this.rotation.Rotation = Quaternion.Concatenate(this.rotation.Rotation, rot);
            
            position = Raytracing.RotateAround(position, origin, new Quaternionf(rot));

        }

        public void Rotate(Vector3f rotation, Vector3f origin)
        {
            Quaternion xRot = Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), rotation.X);
            Quaternion yRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), rotation.Y);
            Quaternion zRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), rotation.Z);

            Quaternion rot = Quaternion.Concatenate(Quaternion.Concatenate(xRot, yRot), zRot);

            this.rotation.Rotation = Quaternion.Concatenate(this.rotation.Rotation, rot);
            axisAngles.Add(rotation);

            position = Raytracing.RotateAround(position, origin, new Quaternionf(rot));

        }

        public void RotateEuler(Vector3f rotation, Vector3f origin)
        {
            Quaternion rot = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);

            this.rotation.Rotation = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);

            position = Raytracing.RotateAround(position, origin, new Quaternionf(rot));

        }

        public void SetRotation(Vector3f rotation)
        {
            Rotate(new Vector3f(rotation).Mul(-1));
            axisAngles.Set(0, 0, 0);
            this.rotation.Identity();
            Rotate(rotation);
        }

        public KeyTransformation Copy()
        {
            KeyTransformation p = new KeyTransformation
            {
                position = new Vector3f(position),
                rotation = new Quaternionf(rotation.Rotation),
                scale = new Vector3f(scale),
                size = new Vector3f(size),
                uv = new Vector2(uv.X, uv.Y),
                origin = new Vector3f(origin)
            };
            return p;
        }
    }
}
