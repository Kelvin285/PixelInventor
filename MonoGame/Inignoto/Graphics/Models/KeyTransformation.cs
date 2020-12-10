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
        public Vector3 position = new Vector3();
        public Quaternion rotation = new Quaternion();
        public Vector3 scale = new Vector3(1, 1, 1);
        public Vector3 size = new Vector3();
        public Vector2 uv = new Vector2(0, 0);
        public Vector3 origin = new Vector3();

        public bool visible = true;
        public bool locked = true;

        public Vector3 look = new Vector3(0, 0, 1);
        public Vector3 axisAngles = new Vector3(0, 0, 0);

        public void Translate(Vector3 translation)
        {
            position += translation;
        }

        public void Rotate(Vector3 rotation)
        {
            Rotate(Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z), position + (Matrix.CreateTranslation(origin) * Matrix.CreateFromQuaternion(this.rotation)).Translation);
        }

        public void Rotate(Quaternion rot, Vector3 origin)
        {
            rotation = Quaternion.Concatenate(rotation, rot);
            
            position = Raytracing.RotateAround(position, origin, new Quaternion(rot.X, rot.Y, rot.Z, rot.W));

        }

        public void Rotate(Vector3 rotation, Vector3 origin)
        {
            Quaternion xRot = Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), rotation.X);
            Quaternion yRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), rotation.Y);
            Quaternion zRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), rotation.Z);

            Quaternion rot = Quaternion.Concatenate(Quaternion.Concatenate(xRot, yRot), zRot);

            this.rotation = Quaternion.Concatenate(this.rotation, rot);
            axisAngles += rotation;

            position = Raytracing.RotateAround(position, origin, new Quaternion(rot.X, rot.Y, rot.Z, rot.W));

        }

        public void RotateEuler(Vector3 rotation, Vector3 origin)
        {
            Quaternion rot = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);

            this.rotation = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);

            position = Raytracing.RotateAround(position, origin, new Quaternion(rot.X, rot.Y, rot.Z, rot.W));

        }

        public void SetRotation(Vector3 rotation)
        {
            Rotate(new Vector3(rotation.X, rotation.Y, rotation.Z) * -1);
            axisAngles *= 0;
            this.rotation = Quaternion.Identity;
            Rotate(rotation);
        }

        public KeyTransformation Copy()
        {
            KeyTransformation p = new KeyTransformation
            {
                position = new Vector3(position.X, position.Y, position.Z),
                rotation = new Quaternion(rotation.X, rotation.Y, rotation.Z, rotation.W),
                scale = new Vector3(scale.X, scale.Y, scale.Z),
                size = new Vector3(size.X, size.Y, size.Z),
                uv = new Vector2(uv.X, uv.Y),
                origin = new Vector3(origin.X, origin.Y, origin.Z)
            };
            return p;
        }
    }
}
