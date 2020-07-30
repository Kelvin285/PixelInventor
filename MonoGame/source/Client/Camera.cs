using Microsoft.Xna.Framework;
using Inignoto.Math;
using System;
using Microsoft.Xna.Framework.Input;

namespace Inignoto.Client
{
    class Camera
    {
        public Vector3f position;
        public Vector3f rotation;

        public Camera()
        {
            position = new Vector3f(0, 0, 0);
            rotation = new Vector3f(0, 0, 0);
        }

        public Vector3f Forward
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll(rotation.Y * (float)System.Math.PI / 180, rotation.X * (float)System.Math.PI / 180, rotation.Z * (float)System.Math.PI / 180));
        }

        public Vector3f ForwardMotionVector
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll(rotation.Y * (float)System.Math.PI / 180, 0, 0));
        }

        public Vector3f RightMotionVector
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll((rotation.Y - 90) * (float)System.Math.PI / 180, 0, 0));
        }

        public Vector3f UpMotionVector
        {
            get => new Vector3f(0, 1, 0);
        }

        public Vector3f Right
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll((rotation.Y - 90) * (float)System.Math.PI / 180, rotation.X * (float)System.Math.PI / 180, rotation.Z * (float)System.Math.PI / 180));
        }

        public Vector3f Up
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll((rotation.Y) * (float)System.Math.PI / 180, (rotation.X + 90) * (float)System.Math.PI / 180, rotation.Z * (float)System.Math.PI / 180));
        }

        public Matrix ViewMatrix { get => Matrix.CreateLookAt(position.Vector, Forward.Vector + position.Vector,
                         new Vector3(0f, 1f, 0f));
        }// Y up}

        public void Update(GameTime gameTime)
        {
            
            Point mousePos = Inignoto.game.mousePos;
            Point lastMousePos = Inignoto.game.lastMousePos;

            if (mousePos.X != lastMousePos.X)
            {
                float rot = -(mousePos.X - lastMousePos.X);
                rotation.Y += rot * GameSettings.Settings.MOUSE_SENSITIVITY;
            }
            if (mousePos.Y != lastMousePos.Y)
            {
                float rot = -(mousePos.Y - lastMousePos.Y);
                rotation.X += rot * GameSettings.Settings.MOUSE_SENSITIVITY;
            }
            
            if (Keyboard.GetState().IsKeyDown(Keys.Space))
            {
                position.Y += 0.1f;
            }
            if (Keyboard.GetState().IsKeyDown(Keys.LeftControl))
            {
                position.Y -= 0.1f;
            }

            if (Keyboard.GetState().IsKeyDown(Keys.W))
            {
                position.Add(ForwardMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f));
            }

            if (Keyboard.GetState().IsKeyDown(Keys.S))
            {
                position.Add(ForwardMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f) * -1f);
            }

            if (Keyboard.GetState().IsKeyDown(Keys.A))
            {
                position.Add(RightMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f) * -1f);
            }

            if (Keyboard.GetState().IsKeyDown(Keys.D))
            {
                position.Add(RightMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f));
            }

            if (rotation.X >= 85) rotation.X = 85;
            if (rotation.X <= -85) rotation.X = -85;
        }
    }
}
