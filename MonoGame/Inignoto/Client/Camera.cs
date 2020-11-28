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
        public Vector3f position;
        public Vector3f rotation;

        public TileRaytraceResult highlightedTile;

        public BoundingFrustum frustum;

        public Camera()
        {
            position = new Vector3f(0, 0, 0);
            rotation = new Vector3f(0, 0, 0);

            frustum = new BoundingFrustum(ViewMatrix * Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f));
            
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
            World.World world = Inignoto.game.world;
            ClientPlayerEntity player = Inignoto.game.player;

            Vector3f eyePosition = player.GetEyePosition();

            highlightedTile = world.RayTraceTiles(eyePosition, new Vector3f(eyePosition).Add(Forward.Mul(player.ReachDistance)), Tiles.Tile.TileRayTraceType.BLOCK, false);
            
            frustum.Matrix = ViewMatrix * Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);
        }
    }
}
