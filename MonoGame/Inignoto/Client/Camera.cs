using Microsoft.Xna.Framework;
using Inignoto.Math;
using System;
using Microsoft.Xna.Framework.Input;
using static Inignoto.World.World;
using Inignoto.Entities.Client.Player;
using Inignoto.World.RaytraceResult;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.GameSettings;

namespace Inignoto.Client
{
    public class Camera
    {
        public Vector3 position;
        public Vector3 rotation;

        public TileRaytraceResult highlightedTile;

        public BoundingFrustum frustum;

        public Camera()
        {
            position = new Vector3(0, 0, 0);
            rotation = new Vector3(0, 0, 0);

            frustum = new BoundingFrustum(ViewMatrix * Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f));
            
        }

        public Vector3 Forward
        {
            get => (Matrix.CreateTranslation(Vector3.Forward) * Matrix.CreateFromQuaternion(Quaternion.CreateFromYawPitchRoll(rotation.Y * (float)System.Math.PI / 180, rotation.X * (float)System.Math.PI / 180, rotation.Z * (float)System.Math.PI / 180))).Translation;
        }

        public Vector3 ForwardMotionVector
        {
            get => (Matrix.CreateTranslation(Vector3.Forward) * Matrix.CreateFromQuaternion(Quaternion.CreateFromYawPitchRoll(rotation.Y * (float)System.Math.PI / 180, 0, 0))).Translation;
        }

        public Vector3 RightMotionVector
        {
            get => (Matrix.CreateTranslation(Vector3.Forward) * Matrix.CreateFromQuaternion(Quaternion.CreateFromYawPitchRoll((rotation.Y - 90) * (float)System.Math.PI / 180, 0, 0))).Translation;
        }

        public Vector3 UpMotionVector
        {
            get => Vector3.Up;
        }

        public Vector3 Right
        {
            get => (Matrix.CreateTranslation(Vector3.Forward) * Matrix.CreateFromQuaternion(Quaternion.CreateFromYawPitchRoll((rotation.Y - 90) * (float)System.Math.PI / 180, rotation.X * (float)System.Math.PI / 180, rotation.Z * (float)System.Math.PI / 180))).Translation;
        }

        public Vector3 Up
        {
            get => (Matrix.CreateTranslation(Vector3.Forward) * Matrix.CreateFromQuaternion(Quaternion.CreateFromYawPitchRoll((rotation.Y) * (float)System.Math.PI / 180, (rotation.X + 90) * (float)System.Math.PI / 180, rotation.Z * (float)System.Math.PI / 180))).Translation;
        }

        public Matrix ViewMatrix { get => Matrix.CreateLookAt(position, Forward + position,
                         new Vector3(0f, 1f, 0f));
        }// Y up}

        public void Update(GameTime gameTime)
        {
            World.World world = Inignoto.game.world;
            ClientPlayerEntity player = Inignoto.game.player;

            Vector3 eyePosition = player.GetEyePosition();

            highlightedTile = world.RayTraceTiles(eyePosition, eyePosition + (Forward * player.ReachDistance), Tiles.Tile.TileRayTraceType.BLOCK, false);
            
            frustum.Matrix = ViewMatrix * Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);
        }
    }
}
