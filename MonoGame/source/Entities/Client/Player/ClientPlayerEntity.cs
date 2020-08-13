using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Audio;
using Inignoto.Client;
using Inignoto.Entities.Player;
using Inignoto.Graphics.Gui;
using Inignoto.Inventory;
using Inignoto.Items;
using Inignoto.Math;
using Inignoto.Tiles;
using Inignoto.World;
using Inignoto.World.RaytraceResult;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using static Inignoto.Tiles.Tile;
using static Inignoto.World.World;

namespace Inignoto.Entities.Client.Player
{
    public class ClientPlayerEntity : PlayerEntity
    {
        private double CameraShakeTime = 0;
        private float CameraShakeIntensity = 0;

        private double LastTappedSpace = 0;
        private bool pressedSpace = false;

        private GameSound wind_instance;
        private GameSound fabric_wind_instance;

        public ClientPlayerEntity(World.World world, Vector3f position) : base(world, position)
        {
            wind_instance = new GameSound(SoundEffects.ambient_wind.CreateInstance(), SoundType.AMBIENT);
            fabric_wind_instance = new GameSound(SoundEffects.ambient_fabric_in_wind.CreateInstance(), SoundType.AMBIENT);
        }

        public override void Update(GameTime time)
        {
            base.Update(time);

            Camera camera = Inignoto.game.camera;

            if (health > 0)
                UpdateCamera();
                        
            switch (gamemode)
            {
                case Gamemode.SURVIVAL:
                    {
                        NormalMotion(time);
                        break;
                    }
                case Gamemode.FREECAM:
                    {
                        FreecamMotion(time);
                        break;
                    }
                case Gamemode.SANDBOX:
                    {
                        NormalMotion(time);
                        break;
                    }

            }

        }

        private bool justPressedC = false;

        public void DoInputControls(GameTime time)
        {

            float friction = 0.2f;
            float moveLerp = 0.1f;

            Camera camera = Inignoto.game.camera;

            bool flyMovement = false;

            if (Crouching && Flying)
            {
                velocity.Y = -0.15f;
                flyMovement = true;
            }
            
            if (GameSettings.Settings.JUMP.IsPressed())
            {
                if (OnGround)
                {
                    velocity.Y = 0.15f;
                    Jumping = true;
                }

                if (Flying)
                {
                    velocity.Y = 0.15f;
                    flyMovement = true;
                    if (Crouching) flyMovement = false;
                }

                if (!pressedSpace)
                {

                    if (gamemode == Gamemode.SANDBOX && LastTappedSpace > 0)
                    {
                        Flying = !Flying;
                    }

                    if (LastTappedSpace == 0)
                    {
                        LastTappedSpace = Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds;
                    }

                    pressedSpace = true;
                }

            } else
            {
                pressedSpace = false;
            }
            if (OnGround) Flying = false;

            if (Flying && !flyMovement)
            {
                velocity.Y = 0;
            }

            if (Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds > LastTappedSpace + 250)
            {
                LastTappedSpace = 0;
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

            Walking = walking;

            Running = Keyboard.GetState().IsKeyDown(Keys.LeftShift);
            Crouching = Keyboard.GetState().IsKeyDown(Keys.LeftControl);

            if (walking)
            {
                float speed = 450;
                if (Crouching || Crawling)
                {
                    speed = 750;
                }
                if (Running)
                {
                    speed = 350;
                }
                

                if (Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds > WalkCycle + speed)
                {
                    WalkCycle = Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds;
                    PlayStepSound(SoundType.PLAYERS);
                }
            }
            
            if (Keyboard.GetState().IsKeyDown(Keys.C))
            {
                if (!justPressedC) Crawling = !Crawling;
                if (BlockAboveHead) Crawling = true;
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
                    velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(0, velocity.Y, 0), friction * 0.1f));
                }
            } else if (trueMotion.Vector.Length() > 0)
            {
                trueMotion.Div(trueMotion.Vector.Length());
                trueMotion.Mul(GetMovementSpeed());
                velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(trueMotion.X, velocity.Y, trueMotion.Z), moveLerp));
            }
            DoItemActions(time);
        }

        public void DoItemActions(GameTime time)
        {
            if (Hud.openGui != null) return;
            Camera camera = Inignoto.game.camera;
            TileRaytraceResult result = camera.highlightedTile;

            if (result != null)
            {
                if (UseTimer == 0)
                {
                    if (GameSettings.Settings.ATTACK.IsPressed())
                    {
                        Tile tile = TileManager.GetTile(world.GetVoxel(result.pos).tile_id);
                        bool add = false;
                        ItemStack it = new ItemStack(tile, 1);
                        for (int i = 0; i < 10; i++)
                        {
                            if (inventory.hotbar[i] == null)
                            {
                                inventory.hotbar[i] = it;
                                add = true;
                                break;
                            }
                            int n = inventory.TryAddToStack(inventory.hotbar[i], it, out it, PhysicalInventory.SlotType.NORMAL);
                            if (n != -1)
                            {
                                add = true;
                                break;
                            }
                        }
                        if (!add)
                        {
                            for (int i = 0; i < 30; i++)
                            {
                                if (inventory.inventory[i] == null)
                                {
                                    inventory.inventory[i] = it;
                                    add = true;
                                    break;
                                }
                                int n = inventory.TryAddToStack(inventory.inventory[i], it, out it, PhysicalInventory.SlotType.NORMAL);
                                if (n != -1)
                                {
                                    add = true;
                                    break;
                                }
                            }
                        }

                        SoundEffect[] sounds = tile.step_sound;
                        if (sounds != null)
                        {
                            SoundEffect effect = sounds[world.random.Next(sounds.Length)];
                            GameSound sound = new GameSound(effect.CreateInstance(), SoundType.PLAYERS);
                            sound.Play();
                            Inignoto.game.SoundsToDispose.Add(sound);
                        }

                        world.SetVoxel(result.pos, TileManager.AIR.DefaultData);



                        UseTimer = 10;
                    }
                }

                if (GameSettings.Settings.USE.IsPressed())
                {
                    if (inventory.hotbar[inventory.selected] != null)
                    {
                        inventory.hotbar[inventory.selected].item.TryUse(this, time);
                    }
                } else
                {
                    if (inventory.hotbar[inventory.selected] != null)
                    {
                        inventory.hotbar[inventory.selected].item.TryStopUsing(this, time);
                    }
                }

            }
            
        }

        public void NormalMotion(GameTime time)
        {

            if (!OnGround && !Flying)
            {
                if (velocity.Y > 0)
                {
                    if (GameSettings.Settings.JUMP.IsPressed())
                    {
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.0075f);
                    } else
                    {
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.01f);
                    }
                } else
                {
                    if (GameSettings.Settings.SNEAK.IsPressed())
                    {
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.0125f);
                    }
                    else
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.01f);
                }

                float d = System.Math.Abs(FallStart - position.Y);

                if (d < 2) d = 0;

                float wv = d;
                if (wv > 1000) wv = 1000;
                wv /= 1000;
                wind_instance.Volume = wv;

                float fv = d;
                if (fv > 25) fv = 25;
                fv /= 25;
                fabric_wind_instance.Volume = fv;
                

                if (wind_instance.State != SoundState.Playing)
                {
                    wind_instance.IsLooped = true;
                    wind_instance.Play();
                }
                if (fabric_wind_instance.State != SoundState.Playing)
                {
                    fabric_wind_instance.IsLooped = true;
                    fabric_wind_instance.Play();
                }
            } else
            {
                fabric_wind_instance.Stop();
                wind_instance.Stop();
            }

            if (health <= 0) return;

            if (health < 100)
            {
                health += 0.01f;
            } else
            {
                health = 100;
            }


            DoInputControls(time);

            
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
                                if (!LastOnGround) PlayStepSound(SoundType.PLAYERS, -1);
                                position.Y = result.hit.Y;
                                velocity.Y = 0;
                                OnGround = true;
                                break;
                            }

                        }

                    }
                }
            }

            BlockAboveHead = false;
            for (float sx = 0; sx < 2; sx++)
            {
                for (float sz = 0; sz < 2; sz++)
                {
                    Vector3f collision_p1 = new Vector3f(position).Add(size.X * sx, size.Y - 0.1f, size.Z * sz);
                    Vector3f collision_p2 = new Vector3f(position).Add(size.X * sx, size.Y + velocity.Y + 0.1f, size.Z * sz);

                    TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                        {
                            if (velocity.Y > 0)
                            {
                                velocity.Y = 0.0f;
                                position.Y = result.hit.Y - size.Y - 0.1f;
                            }
                            BlockAboveHead = true;
                                break;
                        }

                    }

                }
            }
            
            if (TileManager.GetTile(world.GetVoxel(new TilePos(position.X + 0.5f, position.Y + size.Y, position.Z + 0.5f)).tile_id).BlocksMovement())
            {
                BlockAboveHead = true;
            }

            for (float sz = 0; sz < 2; sz++)
            {
                Vector3f collision_p1 = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * sz);
                Vector3f collision_p2 = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * sz).Add(velocity.X, 0, 0).Add(size.X * 0.52f * velocity.X / System.Math.Abs(velocity.X), 0, 0);
                for (float h = OnGround ? StepHeight : 0; h < size.Y - 0.2f; h += 0.1f)
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
                for (float h = OnGround ? StepHeight : 0; h < size.Y - 0.2f; h += 0.1f)
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

        public void FreecamMotion(GameTime time)
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

            if (Inignoto.game.mouse_captured)
            {
                if (mousePos.X != lastMousePos.X)
                {
                    float rot = -(mousePos.X - lastMousePos.X);
                    look.Y += rot * GameSettings.Settings.MOUSE_SENSITIVITY;
                }
                if (mousePos.Y != lastMousePos.Y)
                {
                    float rot = -(mousePos.Y - lastMousePos.Y);
                    look.X += rot * GameSettings.Settings.MOUSE_SENSITIVITY;
                }

                if (look.X >= 85) look.X = 85;
                if (look.X <= -85) look.X = -85;
            }

            camera.rotation.Set(look.X, look.Y, look.Z);


            Vector3f cpos = new Vector3f();
            cpos.Set(position.X + size.X * 0.5f, position.Y + GetEyeHeight(), position.Z + size.Z * 0.5f);
            if (Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds < CameraShakeTime)
            {
                Random random = new Random();
                double x = random.NextDouble() - 0.5;
                double y = random.NextDouble() - 0.5;
                double z = random.NextDouble() - 0.5;
                x *= 2;
                y *= 2;
                z *= 2;
                cpos.Add(new Vector3((float)x * CameraShakeIntensity, (float)y * CameraShakeIntensity, (float)z * CameraShakeIntensity));
            }
            camera.position.Set(Vector3.Lerp(camera.position.Vector, cpos.Vector, 0.5f));
        }

        

        public override void Render(GraphicsDevice device, BasicEffect effect)
        {

        }

        public override void DamageEntity(float damage)
        {
            base.DamageEntity(damage);
            
            if (damage > 0)
            {
                float d = damage > 100 ? 100 : damage;

                SoundEffects.player_hit.Play(d / 100.0f, 0.0f, 0.0f);

                CameraShakeIntensity = d / 1000;


                CameraShakeTime = Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds + 250;
            }
        }
       

        public override float GetMovementSpeed()
        {
            if (Flying && !Crouching)
            {
                if (Running) return 0.2f;
                return 0.1f;
            }
            return base.GetMovementSpeed();
        }

        public override void PlayStepSound(SoundType soundType, float y_offset = 0)
        {
            TilePos pos = GetTilePos();
            if (y_offset != 0)
            {
                pos = new TilePos(pos.x, (float)System.Math.Round(pos.y + y_offset), pos.z);
            }
            SoundEffect[] sounds = TileManager.GetTile(world.GetVoxel(pos).tile_id).step_sound;
            if (sounds != null)
            {
                SoundEffect effect = sounds[world.random.Next(sounds.Length)];
                GameSound sound = new GameSound(effect.CreateInstance(), soundType);
                sound.Volume = 0.5f;
                if (Running) sound.Volume = 0.75f;
                if (Crouching) sound.Volume = 0.25f;
                if (Crawling) sound.Volume = 0.1f;
                sound.Play();
                SoundsToDispose.Add(sound);
            }
        }

    }
}
