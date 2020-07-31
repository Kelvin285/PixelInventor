using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Client;
using Inignoto.Entities.Player;
using Inignoto.Math;
using Inignoto.Tiles;
using Inignoto.World;
using Inignoto.World.RaytraceResult;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using static Inignoto.Tiles.Tile;
using static Inignoto.World.World;

namespace Inignoto.Entities.Client.Player
{
    public class ClientPlayerEntity : PlayerEntity
    {
        public ClientPlayerEntity(World.World world, Vector3f position) : base(world, position)
        {
        }

        public override void Update(GameTime time)
        {
            base.Update(time);
            UpdateCamera();
            switch (gamemode)
            {
                case Gamemode.SURVIVAL:
                    {
                        NormalMotion();
                        break;
                    }
                case Gamemode.FREECAM:
                    {
                        FreecamMotion();
                        break;
                    }
                case Gamemode.SANDBOX:
                    {
                        NormalMotion();
                        break;
                    }

            }

        }

        private bool justPressedC = false;

        public void DoInputControls()
        {

            float friction = 0.2f;
            float moveLerp = 0.1f;

            Camera camera = Inignoto.game.camera;
            
            if (Keyboard.GetState().IsKeyDown(Keys.Space) && OnGround)
            {
                velocity.Y = 0.15f;
                Jumping = true;
            }

            bool walking = false;
            Vector3f trueMotion = new Vector3f();

            if (Keyboard.GetState().IsKeyDown(Keys.W))
            {
                Vector3 motion = (camera.ForwardMotionVector.Vector * new Vector3(GetMovementSpeed(), 0.0f, GetMovementSpeed()));
                trueMotion.Add(motion);
                walking = true;
            }

            if (Keyboard.GetState().IsKeyDown(Keys.S))
            {
                Vector3 motion = (camera.ForwardMotionVector.Vector * new Vector3(GetMovementSpeed(), 0.0f, GetMovementSpeed()) * -1f);
                trueMotion.Add(motion);
                walking = true;
            }

            if (Keyboard.GetState().IsKeyDown(Keys.A))
            {
                Vector3 motion = (camera.RightMotionVector.Vector * new Vector3(GetMovementSpeed(), 0.0f, GetMovementSpeed()) * -1f);
                trueMotion.Add(motion);
                walking = true;
            }

            if (Keyboard.GetState().IsKeyDown(Keys.D))
            {
                Vector3 motion = (camera.RightMotionVector.Vector * new Vector3(GetMovementSpeed(), 0.0f, GetMovementSpeed()));
                trueMotion.Add(motion);
                walking = true;
            }

            Running = Keyboard.GetState().IsKeyDown(Keys.LeftShift);
            Crouching = Keyboard.GetState().IsKeyDown(Keys.LeftControl);
            
            if (Keyboard.GetState().IsKeyDown(Keys.C))
            {
                if (!justPressedC) Crawling = !Crawling;
                justPressedC = true;
            }
            else justPressedC = false;

            if (!walking)
            {
                if (OnGround)
                {
                    velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(0, velocity.Y, 0), friction));
                } else
                {
                    velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(0, velocity.Y, 0), friction * 0.25f));
                }
            } else if (trueMotion.Vector.Length() > 0)
            {
                trueMotion.Div(trueMotion.Vector.Length());
                trueMotion.Mul(GetMovementSpeed());
                velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(trueMotion.X, velocity.Y, trueMotion.Z), moveLerp));
            }
            DoItemActions();
        }

        public void DoItemActions()
        {
            Camera camera = Inignoto.game.camera;
            TileRaytraceResult result = camera.highlightedTile;

            if (result != null)
            {
                if (UseTimer == 0)
                {
                    if (Mouse.GetState().LeftButton == ButtonState.Pressed)
                    {
                        world.SetVoxel(result.pos, TileManager.AIR.DefaultData);
                        UseTimer = 10;
                    }
                }

                if (Mouse.GetState().RightButton == ButtonState.Pressed)
                {
                    if (PlaceTimer == 0)
                    {
                        Vector3f normal = result.intersection.normal;
                        
                        TilePos pos = new World.World.TilePos(result.pos.x + normal.X, result.pos.y + normal.Y, result.pos.z + normal.Z);
                        if (TileManager.GetTile(world.GetVoxel(pos).tile_id).IsReplaceable())
                        {
                            bool intersects = false;
                            foreach (Entity e in world.entities)
                            {
                                Rectangle b1 = new Rectangle(pos.x * 16, pos.y * 16, 16, 16);
                                Rectangle b2 = new Rectangle(pos.x * 16, pos.z * 16, 16, 16);

                                Rectangle p1 = new Rectangle((int)(e.position.X * 16) + 1, (int)(e.position.Y * 16) + 1, (int)(e.size.X * 16) - 2, (int)(e.size.Y * 16) - 2);
                                Rectangle p2 = new Rectangle((int)(e.position.X * 16) + 1, (int)(e.position.Z * 16) + 1, (int)(e.size.X * 16) - 2, (int)(e.size.Z * 16) - 2);

                                if (b1.Intersects(p1) && b2.Intersects(p2))
                                {
                                    intersects = true;
                                    break;
                                }
                            }
                            if (!intersects)
                            {
                                world.SetVoxel(pos, TileManager.DIRT.DefaultData);
                                PlaceTimer = 10;
                            }
                        }
                    }
                }
                else PlaceTimer = 0;

            }
            
        }

        public void NormalMotion()
        {

            if (!OnGround)
            {
                velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.01f);
            }

            DoInputControls();

            
            OnGround = false;


            for (float sx = 0; sx < 2; sx++)
            {
                for (float sz = 0; sz < 2; sz++)
                {
                    TileRaytraceResult ground_result = world.RayTraceTiles(new Vector3f(position).Add(size.X * sx, size.Y, size.Z * sz), new Vector3f(position).Add(size.X * sx, 0, size.Z * sz).Sub(0, -0.01f, 0), Tiles.Tile.TileRayTraceType.BLOCK);
                    if (ground_result != null)
                    {
                        OnGround = true;
                        if (velocity.Y < 0)
                            velocity.Y = 0;
                    }
                    TileRaytraceResult near_ground = world.RayTraceTiles(new Vector3f(position).Add(size.X * sx, size.Y, size.Z * sz), new Vector3f(position).Add(size.X * sx, -1.1f, size.Z * sz), Tiles.Tile.TileRayTraceType.BLOCK);
                    if (near_ground != null)
                    {
                        NearGround = true;
                    }

                    TileRaytraceResult step_result = world.RayTraceTiles(new Vector3f(position).Add(size.X * sx, 0, size.Z * sz), new Vector3f(position).Add(size.X * sx, StepHeight, size.Z * sz), Tiles.Tile.TileRayTraceType.BLOCK);
                    if (step_result != null && NearGround)
                    {
                        position.Y += StepHeight;
                    }
                }
            }


            if (velocity.Y < 0)
            {
                for (float sx = 0; sx < 2; sx++)
                {
                    for (float sz = 0; sz < 2; sz++)
                    {
                        Vector3f collision_p1 = new Vector3f(position).Add(size.X * sx, 0, size.Z * sz);
                        Vector3f collision_p2 = new Vector3f(position).Add(size.X * sx, velocity.Y, size.Z * sz);

                        TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                        if (result != null)
                        {
                            if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                            {
                                position.Y = result.hit.Y;
                                velocity.Y = 0;
                                OnGround = true;
                                break;
                            }

                        }

                    }
                }
            }
                


            if (velocity.Y > 0)
            for (float sx = 0; sx < 2; sx++)
            {
                for (float sz = 0; sz < 2; sz++)
                {
                    Vector3f collision_p1 = new Vector3f(position).Add(size.X * sx, size.Y, size.Z * sz);
                    Vector3f collision_p2 = new Vector3f(position).Add(size.X * sx, size.Y + velocity.Y, size.Z * sz);

                    TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                        {
                            velocity.Y = 0;
                                break;
                        }

                    }

                }
            }

            for (float sz = 0; sz < 2; sz++)
            {
                Vector3f collision_p1 = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * sz);
                Vector3f collision_p2 = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * sz).Add(velocity.X, 0, 0).Add(size.X * 0.52f * velocity.X / System.Math.Abs(velocity.X), 0, 0);
                for (float h = StepHeight; h < 2 - 0.2f; h += 0.1f)
                {
                    collision_p1.Y = position.Y + h;
                    collision_p2.Y = position.Y + h;
                    TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                        {
                            velocity.X = 0;
                            break;
                        }

                    }
                }
            }

            for (float sx = 0; sx < 2; sx++)
            {
                Vector3f collision_p1 = new Vector3f(position).Add(size.X * sx, 0, size.Z * 0.5f);
                Vector3f collision_p2 = new Vector3f(position).Add(size.X * sx, 0, size.Z * 0.5f).Add(0, 0, velocity.Z).Add(0, 0, size.Z * 0.52f * velocity.Z / System.Math.Abs(velocity.Z));
                for (float h = StepHeight; h < 2 - 0.2f; h += 0.1f)
                {
                    collision_p1.Y = position.Y + h;
                    collision_p2.Y = position.Y + h;
                    TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                        {
                            velocity.Z = 0;
                            break;
                        }

                    }
                }
            }

            if (Crouching && NearGround && !Jumping)
            {
                bool flagX = false;
                bool flagZ = false;
                {
                    Vector3f p1 = new Vector3f(position).Add(0, StepHeight, 0);
                    Vector3f p2 = new Vector3f(position).Add(size.X, StepHeight, size.Z);
                    TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                    if (result == null)
                    {
                        result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result != null)
                        {
                            if (velocity.X > 0)
                            {
                                flagX = true;
                            }
                            if (velocity.Z > 0)
                            {
                                flagZ = true;
                            }
                        }
                    }
                }

                {
                    Vector3f p1 = new Vector3f(position).Add(0, StepHeight, 0);
                    Vector3f p2 = new Vector3f(position).Add(size.X, StepHeight, size.Z);
                    TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result == null)
                        {
                            if (velocity.X < 0)
                            {
                                flagX = true;
                            }
                            if (velocity.Z < 0)
                            {
                                flagZ = true;
                            }
                        }
                    }
                }

                {
                    Vector3f p1 = new Vector3f(position).Add(size.X, StepHeight, 0);
                    Vector3f p2 = new Vector3f(position).Add(0, StepHeight, size.Z);
                    TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result == null)
                        {
                            if (velocity.X > 0)
                            {
                                flagX = true;
                            }
                            if (velocity.Z < 0)
                            {
                                flagZ = true;
                            }
                        }
                    }
                }

                {
                    Vector3f p1 = new Vector3f(position).Add(0, StepHeight, size.Z);
                    Vector3f p2 = new Vector3f(position).Add(size.X, StepHeight, 0);
                    TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result == null)
                        {
                            if (velocity.X < 0)
                            {
                                flagX = true;
                            }
                            if (velocity.Z > 0)
                            {
                                flagZ = true;
                            }
                        }
                    }
                }

                if (!flagZ)
                    if (velocity.Z > 0)
                    {
                        Vector3f p1 = new Vector3f(position).Add(0, StepHeight, size.Z * 0.1f);
                        Vector3f p2 = new Vector3f(position).Add(size.X / 2.0f, StepHeight, size.Z * 0.1f);
                        Vector3f p3 = new Vector3f(position).Add(size.X, StepHeight, size.Z * 0.1f);
                        Vector3f p4 = new Vector3f(position).Add(size.X / 2.0f, StepHeight, size.Z);
                        TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result == null)
                        {
                            result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                            if (result == null)
                            {
                                result = world.RayTraceTiles(new Vector3f(p3), new Vector3f(p3).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                if (result == null)
                                {
                                    result = world.RayTraceTiles(new Vector3f(p4), new Vector3f(p4).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                    if (result == null)
                                    {
                                        //								position.Z -= velocity.Z;
                                        velocity.Z *= -0.1f;
                                    }
                                }
                            }
                        }
                    }
                if (!flagZ)
                    if (velocity.Z < 0)
                    {
                        Vector3f p1 = new Vector3f(position).Add(0, StepHeight, size.Z * 0.9f);
                        Vector3f p2 = new Vector3f(position).Add(size.X / 2.0f, StepHeight, size.Z * 0.9f);
                        Vector3f p3 = new Vector3f(position).Add(size.X, StepHeight, size.Z * 0.9f);
                        Vector3f p4 = new Vector3f(position).Add(size.X / 2.0f, StepHeight, 0);
                        TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result == null)
                        {
                            result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                            if (result == null)
                            {
                                result = world.RayTraceTiles(new Vector3f(p3), new Vector3f(p3).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                if (result == null)
                                {
                                    result = world.RayTraceTiles(new Vector3f(p4), new Vector3f(p4).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                    if (result == null)
                                    {
                                        //								position.Z -= velocity.Z;
                                        velocity.Z *= -0.1f;
                                    }
                                }
                            }
                        }
                    }
                if (!flagX)
                    if (velocity.X > 0)
                    {
                        Vector3f p1 = new Vector3f(position).Add(size.X * 0.1f, StepHeight, 0);
                        Vector3f p2 = new Vector3f(position).Add(size.X * 0.1f, StepHeight, size.Z / 2.0f);
                        Vector3f p3 = new Vector3f(position).Add(size.X * 0.1f, StepHeight, size.Z);
                        Vector3f p4 = new Vector3f(position).Add(size.X, StepHeight, size.Z / 2.0f);
                        TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result == null)
                        {
                            result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                            if (result == null)
                            {
                                result = world.RayTraceTiles(new Vector3f(p3), new Vector3f(p3).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                if (result == null)
                                {
                                    result = world.RayTraceTiles(new Vector3f(p4), new Vector3f(p4).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                    if (result == null)
                                    {
                                        //								position.X -= velocity.X;
                                        velocity.X *= -0.1f;
                                    }
                                }
                            }
                        }
                    }
                if (!flagX)
                    if (velocity.X < 0)
                    {
                        Vector3f p1 = new Vector3f(position).Add(size.X * 0.9f, StepHeight, 0);
                        Vector3f p2 = new Vector3f(position).Add(size.X * 0.9f, StepHeight, size.Z / 2.0f);
                        Vector3f p3 = new Vector3f(position).Add(size.X * 0.9f, StepHeight, size.Z);
                        Vector3f p4 = new Vector3f(position).Add(0, StepHeight, size.Z / 2.0f);
                        TileRaytraceResult result = world.RayTraceTiles(new Vector3f(p1), new Vector3f(p1).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                        if (result == null)
                        {
                            result = world.RayTraceTiles(new Vector3f(p2), new Vector3f(p2).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                            if (result == null)
                            {
                                result = world.RayTraceTiles(new Vector3f(p3), new Vector3f(p3).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                if (result == null)
                                {
                                    result = world.RayTraceTiles(new Vector3f(p4), new Vector3f(p4).Add(0, -StepHeight * 2, 0), TileRayTraceType.BLOCK);
                                    if (result == null)
                                    {
                                        //								position.X -= velocity.X;
                                        velocity.X *= -0.1f;
                                    }
                                }
                            }
                        }
                    }
            }


            position.Add(velocity);
        }

        public void FreecamMotion()
        {
            Camera camera = Inignoto.game.camera;
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
                position.Add(camera.ForwardMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f));
            }

            if (Keyboard.GetState().IsKeyDown(Keys.S))
            {
                position.Add(camera.ForwardMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f) * -1f);
            }

            if (Keyboard.GetState().IsKeyDown(Keys.A))
            {
                position.Add(camera.RightMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f) * -1f);
            }

            if (Keyboard.GetState().IsKeyDown(Keys.D))
            {
                position.Add(camera.RightMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f));
            }
        }

        public void UpdateCamera()
        {
            Camera camera = Inignoto.game.camera;
            Point mousePos = Inignoto.game.mousePos;
            Point lastMousePos = Inignoto.game.lastMousePos;

            if (mousePos.X != lastMousePos.X)
            {
                float rot = -(mousePos.X - lastMousePos.X);
                camera.rotation.Y += rot * GameSettings.Settings.MOUSE_SENSITIVITY;
            }
            if (mousePos.Y != lastMousePos.Y)
            {
                float rot = -(mousePos.Y - lastMousePos.Y);
                camera.rotation.X += rot * GameSettings.Settings.MOUSE_SENSITIVITY;
            }

            if (camera.rotation.X >= 85) camera.rotation.X = 85;
            if (camera.rotation.X <= -85) camera.rotation.X = -85;

            camera.position.Set(position.X + size.X * 0.5f, position.Y + GetEyeHeight(), position.Z + size.Z * 0.5f);
        }



        public override void Render(GraphicsDevice device, BasicEffect effect)
        {

        }
    }
}
