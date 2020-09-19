﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Audio;
using Inignoto.Client;
using Inignoto.Effects;
using Inignoto.Entities.Player;
using Inignoto.GameSettings;
using Inignoto.Graphics.Gui;
using Inignoto.Graphics.Models;
using Inignoto.Graphics.Textures;
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

        public int Perspective { get; private set; }

        private float WalkCycleSpeed = 0;

        private float RenderEyeHeight = 0;

        private float forward = 0;
        private float right = 0;

        public ClientPlayerEntity(World.World world, Vector3f position) : base(world, position)
        {
            wind_instance = new GameSound(SoundEffects.ambient_wind.CreateInstance(), SoundType.AMBIENT);
            fabric_wind_instance = new GameSound(SoundEffects.ambient_fabric_in_wind.CreateInstance(), SoundType.AMBIENT);
            Perspective = 1;
            RenderEyeHeight = GetEyeHeight();
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
            LastOnGround = OnGround;
        }

        private bool justPressedC = false;

        private float OffGroundTimer = 0.0f;

        public void DoInputControls(GameTime time)
        {
            float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;

            if (!OnGround)
            {
                OffGroundTimer = 1.0f;
            } else
            {
                OffGroundTimer = MathHelper.Max(OffGroundTimer - delta, 0.0f);
            }
            
            float friction = 0.2f;
            float moveLerp = (OffGroundTimer == 0) ? 0.9f : 0.25f;

            Camera camera = Inignoto.game.camera;

            bool flyMovement = false;

            if (Crouching && Flying)
            {
                velocity.Y = -0.15f * delta;
                flyMovement = true;
            }

            if (Settings.PERSPECTIVE_SWITCH.IsJustPressed())
            {
                Perspective++;
                Perspective %= 3;
            }
            
            if (GameSettings.Settings.JUMP.IsPressed())
            {
                if (OnGround)
                {
                    velocity.Y = 0.15f * delta;
                    Jumping = true;
                }

                if (Flying)
                {
                    velocity.Y = 0.15f * delta;
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

            if (Settings.FORWARD.IsPressed() && !Settings.BACKWARD.IsPressed())
            {
                Vector3 motion = (ForwardMotionVector.Vector * new Vector3(GetMovementSpeed(time), 0.0f, GetMovementSpeed(time)));
                trueMotion.Add(motion);
                walking = true;
                forward = GetMovementSpeed(time);
            }

            if (Settings.BACKWARD.IsPressed() && !Settings.FORWARD.IsPressed())
            {
                Vector3 motion = (ForwardMotionVector.Vector * new Vector3(GetMovementSpeed(time), 0.0f, GetMovementSpeed(time)) * -1f);
                trueMotion.Add(motion);
                walking = true;
                forward = -GetMovementSpeed(time);
            }

            if (Settings.LEFT.IsPressed() && !Settings.RIGHT.IsPressed())
            {
                Vector3 motion = (RightMotionVector.Vector * new Vector3(GetMovementSpeed(time), 0.0f, GetMovementSpeed(time)) * -1f);
                trueMotion.Add(motion);
                walking = true;
                right = -GetMovementSpeed(time);
            }

            if (Settings.RIGHT.IsPressed() && !Settings.LEFT.IsPressed())
            {
                Vector3 motion = (RightMotionVector.Vector * new Vector3(GetMovementSpeed(time), 0.0f, GetMovementSpeed(time)));
                trueMotion.Add(motion);
                walking = true;
                right = GetMovementSpeed(time);
            }

            Walking = walking;

            Running = Keyboard.GetState().IsKeyDown(Keys.LeftShift);
            Crouching = Keyboard.GetState().IsKeyDown(Keys.LeftControl);

            if (walking)
            {
                float speed = 450;
                if (Crouching)
                {
                    speed = 450;
                }
                
                if (Running)
                {
                    speed = 350;
                }

                if (Crawling)
                {
                    speed = 1000;
                }


                if (Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds > WalkCycle + speed)
                {
                    WalkCycle = Inignoto.game.world.gameTime.TotalGameTime.TotalMilliseconds;
                    PlayStepSound(SoundType.PLAYERS);
                }

                WalkCycleSpeed = speed / 1000.0f;
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
                    velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(0, velocity.Y, 0), friction * delta));
                } else
                {
                    velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(0, velocity.Y, 0), friction * 0.001f * delta));
                }
            } else if (trueMotion.Vector.Length() > 0)
            {
                trueMotion.Div(trueMotion.Vector.Length());
                trueMotion.Mul(GetMovementSpeed(time));
                velocity.Set(Vector3.Lerp(velocity.Vector, new Vector3(trueMotion.X, velocity.Y, trueMotion.Z), moveLerp * delta));
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
                        

                        SoundEffect[] sounds = tile.step_sound;
                        if (sounds != null)
                        {
                            SoundEffect effect = sounds[world.random.Next(sounds.Length)];
                            GameSound sound = new GameSound(effect.CreateInstance(), SoundType.PLAYERS);
                            sound.Play();
                            Inignoto.game.SoundsToDispose.Add(sound);
                        }

                        world.entities.Add(new ItemEntity(world, new Vector3f(result.pos.x + 0.5f, result.pos.y + 0.5f, result.pos.z + 0.5f), new ItemStack(TileManager.GetTile(result.data.tile_id))));

                        world.SetVoxel(result.pos, TileManager.AIR.DefaultData);



                        UseTimer = 10;
                    }
                }

                if (GameSettings.Settings.USE.IsPressed())
                {
                    if (Inventory.hotbar[Inventory.selected] != null)
                    {
                        Inventory.hotbar[Inventory.selected].item.TryUse(this, time);
                    }
                }
                else
                {
                    if (Inventory.hotbar[Inventory.selected] != null)
                    {
                        Inventory.hotbar[Inventory.selected].item.TryStopUsing(this, time);
                    }
                }

            }

        }

        public void NormalMotion(GameTime time)
        {
            float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;
            if (!OnGround && !Flying)
            {
                if (velocity.Y > 0)
                {
                    if (GameSettings.Settings.JUMP.IsPressed())
                    {
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.0075f * delta);
                    } else
                    {
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.01f * delta);
                    }
                } else
                {
                    if (GameSettings.Settings.SNEAK.IsPressed())
                    {
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.0125f * delta);
                    }
                    else
                        velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.01f * delta);
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

           
            DoBlockCollisions();

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
                position.Add(camera.ForwardMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f) * -0.75f);
            }

            if (Keyboard.GetState().IsKeyDown(Keys.A))
            {
                position.Add(camera.RightMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f) * -0.75f);
            }

            if (Keyboard.GetState().IsKeyDown(Keys.D))
            {
                position.Add(camera.RightMotionVector.Vector * new Vector3(0.1f, 0.0f, 0.1f) * 0.75f);
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
            
            Vector3f cpos = new Vector3f();
            cpos.Set(position.X + size.X * 0.5f, position.Y + RenderEyeHeight, position.Z + size.Z * 0.5f);
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

            cpos.Add(ForwardLook.Mul(-(Perspective - 1) * 4.0f));

            TileRaytraceResult result = world.RayTraceTiles(GetEyePositionForRender(), cpos, TileRayTraceType.BLOCK);

            cpos.Sub(ForwardLook.Mul(-(Perspective - 1) * 0.5f));

            if (result != null)
            {
                Vector3f dir = new Vector3f(cpos).Sub(GetEyePositionForRender());
                if (dir.Vector.Length() != 0)
                    dir.Div(dir.Vector.Length());
                cpos = GetEyePosition().Add(dir.Mul(result.intersection.lambda.X - 0.5f));
            }
            
            camera.position.Set(Vector3.Lerp(camera.position.Vector, cpos.Vector, 1f));
            camera.rotation.Set(look.X, look.Y, look.Z);
            if (Perspective == 0)
            {   
                camera.rotation.Set(-look.X, look.Y + 180, -look.Z + lean + rLean * 30);
            }

            SetModelTransform(position);
            RenderEyeHeight = MathHelper.Lerp(RenderEyeHeight, GetEyeHeight(), 0.25f);
            
            
        }

        public virtual Vector3f GetEyePositionForRender()
        {
            return new Vector3f(position).Add(size.X * 0.5f, RenderEyeHeight, size.Z * 0.5f);
        }

        public void SetModelTransform(Vector3f position = null, Vector3f look = null)
        {
            if (position == null) position = this.position;
            if (look == null) look = this.look;
            if (model != null)
            {
                float sine = (float)System.Math.Sin(look.Y * (3.14 / 180.0));
                float cos = (float)System.Math.Cos(look.Y * (3.14 / 180.0));
                model.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                model.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);

                shirt_model.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                shirt_model.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);

                pants_model.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                pants_model.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);

                shoes_model.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                shoes_model.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);

                eyes_model.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                eyes_model.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);

                hair_model.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                hair_model.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);
            }
        }

        public GameModel model;
        public GameModel shirt_model;
        public GameModel pants_model;
        public GameModel eyes_model;
        public GameModel shoes_model;
        public GameModel hair_model;

        public Texture2D texture;
        public Texture2D shirt;
        public Texture2D pants;
        public Texture2D shoes;
        public Texture2D eyes;
        public Texture2D hair;

        public List<Keyframe> idle;
        public List<Keyframe> walk;
        public List<Keyframe> jump;
        public List<Keyframe> run;
        public List<Keyframe> fall;
        public List<Keyframe> sneaking;
        public List<Keyframe> sneak_idle;
        public List<Keyframe> crawling;
        public List<Keyframe> crawl_idle;


        public bool shirt_visible = true;
        public bool long_sleeves = false;
        public bool pants_visible = true;
        public bool long_pants = true;
        public bool shoes_cover = true;

        public GameModel pick_model;
        public Texture2D pick_texture;

        public override void Render(GraphicsDevice device, GameEffect effect, GameTime time, bool showModel = false)
        {
            if (model == null)
            {
                idle = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/idle.anim", "assets"));
                walk = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/walk.anim", "assets"));
                jump = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/jump.anim", "assets"));
                run = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/run.anim", "assets"));
                fall = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/fall.anim", "assets"));
                sneaking = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/sneaking.anim", "assets"));
                sneak_idle = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/sneak_idle.anim", "assets"));
                crawling = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/crawling.anim", "assets"));
                crawl_idle = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/entity/player/crawl_idle.anim", "assets"));


                texture = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/skin/skin1.png", "assets"));
                shirt = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/shirt/shirt1.png", "assets"));
                pants = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/pants/pants1.png", "assets"));
                shoes = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/shoes/shoes1.png", "assets"));
                eyes = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/eyes/eyes1.png", "assets"));
                hair = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/hair/hair1.png", "assets"));

                model = GameModel.LoadModel(new Utilities.ResourcePath("Inignoto", "models/entity/player/player.model", "assets"), texture);
                shirt_model = GameModel.LoadModel(new Utilities.ResourcePath("Inignoto", "models/entity/player/player.model", "assets"), shirt);
                pants_model = GameModel.LoadModel(new Utilities.ResourcePath("Inignoto", "models/entity/player/player.model", "assets"), pants);
                eyes_model = GameModel.LoadModel(new Utilities.ResourcePath("Inignoto", "models/entity/player/eyes.model", "assets"), eyes);
                shoes_model = GameModel.LoadModel(new Utilities.ResourcePath("Inignoto", "models/entity/player/player.model", "assets"), shoes);
                hair_model = GameModel.LoadModel(new Utilities.ResourcePath("Inignoto", "models/entity/player/hair1.model", "assets"), hair);

                pick_texture = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/items/iron_pickaxe.png", "assets"));
                pick_model = GameModel.LoadModel(new Utilities.ResourcePath("Inignoto", "models/item/iron_pickaxe.model", "assets"), pick_texture);
                pick_model.timeline = GameModel.LoadAnimation(new Utilities.ResourcePath("Inignoto", "models/item/iron_pickaxe.anim", "assets"));
                pick_model.editMode = GameModel.EditMode.MODEL;
                //pick_model.Play(0);

                model.editMode = GameModel.EditMode.ANIMATION;
                model.timeline = idle;
                model.Play(0);

                shirt_model.editMode = GameModel.EditMode.ANIMATION;
                shirt_model.timeline = idle;
                shirt_model.Play(0);

                pants_model.editMode = GameModel.EditMode.ANIMATION;
                pants_model.timeline = idle;
                pants_model.Play(0);

                eyes_model.editMode = GameModel.EditMode.ANIMATION;
                eyes_model.timeline = idle;
                eyes_model.Play(0);

                shoes_model.editMode = GameModel.EditMode.ANIMATION;
                shoes_model.timeline = idle;
                shoes_model.Play(0);

                hair_model.editMode = GameModel.EditMode.ANIMATION;
                hair_model.timeline = idle;
                hair_model.Play(0);
            }

            if (shoes_model != null)
                foreach (Part part in shoes_model.Parts)
                {
                    if (!part.name.Contains("Foot"))
                    {
                        part.visible = false;
                    }
                }

            if (hair_model != null)
                foreach (Part part in hair_model.Parts)
                {
                    if (!part.name.Contains("Head"))
                    {
                        part.visible = false;
                    }
                }

            if (eyes_model != null)
                foreach (Part part in pants_model.Parts)
                {
                    if (!part.name.Contains("Head"))
                    {
                        part.visible = false;
                    }
                }

            if (pants_model != null)
                foreach (Part part in pants_model.Parts)
                {
                    part.visible = false;
                    if (part.name.Contains("Leg"))
                    {
                        part.visible = true;
                        if (!long_pants)
                        {
                            if (part.name.Contains("Leg_Lower"))
                            {
                                part.visible = false;
                            }
                        }
                    }
                }

            if (shirt_model != null)
            foreach (Part part in shirt_model.Parts)
            {
                    if (!(part.name.Contains("Body") || part.name.Contains("Arm")))
                    {
                        part.visible = false;
                    }
                    if (part.name.Contains("Arm") && part.name.Contains("Arm_Lower"))
                    {
                        part.visible = long_sleeves;
                    }
            }

                foreach (Part part in model.Parts)
            {
                if (part.name.Contains("Body") || part.name.Contains("Arm"))
                {
                    if (shirt_visible)
                    {
                        part.visible = false;
                        if (!long_sleeves)
                        {
                            if (part.name.Contains("Arm_Lower"))
                            {
                                part.visible = true;
                            }
                        }
                    }
                }
                if (part.name.Contains("Leg"))
                {
                    if (pants_visible)
                    {
                        part.visible = false;
                        if (!long_pants)
                        {
                            if (part.name.Contains("Leg_Lower")) part.visible = true;
                        }
                    }
                }
                if (shoes_cover && part.name.Contains("Foot"))
                {
                    part.visible = false;
                }
            }

            if (Walking)
            {
                if (Crawling)
                {
                    model.animationSpeed = 0.25f / 15.0f;
                    shirt_model.animationSpeed = 0.25f / 15.0f;
                    pants_model.animationSpeed = 0.25f / 15.0f;
                    eyes_model.animationSpeed = 0.25f / 15.0f;
                    shoes_model.animationSpeed = 0.25f / 15.0f;
                    hair_model.animationSpeed = 0.25f / 15.0f;
                    TryPlayAnimation(crawling);
                }
                else
                 if (Crouching)
                {
                    model.animationSpeed = 1.1f / 15.0f;
                    shirt_model.animationSpeed = 1.1f / 15.0f;
                    pants_model.animationSpeed = 1.1f / 15.0f;
                    eyes_model.animationSpeed = 1.1f / 15.0f;
                    shoes_model.animationSpeed = 1.1f / 15.0f;
                    hair_model.animationSpeed = 1.1f / 15.0f;
                    TryPlayAnimation(sneaking);
                } else
                {
                    if (Running)
                    {
                        model.animationSpeed = (1.5f / 15.0f) * 2.0f;
                        shirt_model.animationSpeed = (1.5f / 15.0f) * 2.0f;
                        pants_model.animationSpeed = (1.5f / 15.0f) * 2.0f;
                        eyes_model.animationSpeed = (1.5f / 15.0f) * 2.0f;
                        shoes_model.animationSpeed = (1.5f / 15.0f) * 2.0f;
                        hair_model.animationSpeed = (1.5f / 15.0f) * 2.0f;
                        TryPlayAnimation(run);
                    }
                    else
                    {
                        model.animationSpeed = 1.1f / 15.0f;
                        shirt_model.animationSpeed = 1.1f / 15.0f;
                        pants_model.animationSpeed = 1.1f / 15.0f;
                        eyes_model.animationSpeed = 1.1f / 15.0f;
                        shoes_model.animationSpeed = 1.1f / 15.0f;
                        hair_model.animationSpeed = 1.1f / 15.0f;
                        TryPlayAnimation(walk);
                    }
                }

                
            } else
            {
                model.animationSpeed = 1.0f / 60.0f;
                shirt_model.animationSpeed = 1.0f / 60.0f;
                pants_model.animationSpeed = 1.0f / 60.0f;
                eyes_model.animationSpeed = 1.0f / 60.0f;
                shoes_model.animationSpeed = 1.0f / 60.0f;
                hair_model.animationSpeed = 1.0f / 60.0f;
                if (Crawling)
                {
                    TryPlayAnimation(crawl_idle);
                }
                else
                if (Crouching)
                {
                    TryPlayAnimation(sneak_idle);
                } else
                {
                    TryPlayAnimation(idle);
                }
            }

            if (!OnGround)
            {
                TryPlayAnimation(fall);
            }

            if (model != null && Perspective != 1 || model != null && showModel)
            {
                if (shirt_model != null)
                {
                    shirt_model.currentTime = model.currentTime;
                    shirt_model.scale.Set(2.0f, 2.0f, 2.0f);
                    shirt_model.Render(device, effect, time);
                }
                if (pants_model != null)
                {
                    pants_model.currentTime = model.currentTime;
                    pants_model.scale.Set(2.0f, 2.0f, 2.0f);
                    pants_model.Render(device, effect, time);
                }
                if (shoes_model != null)
                {
                    shoes_model.currentTime = model.currentTime;
                    shoes_model.scale.Set(2.0f, 2.0f, 2.0f);
                    shoes_model.Render(device, effect, time);
                }

                eyes_model.currentTime = model.currentTime;
                hair_model.currentTime = model.currentTime;

                model.scale.Set(2.0f, 2.0f, 2.0f);
                model.Render(device, effect, time);

                if (eyes_model != null)
                {
                    eyes_model.scale.Set(2.0f, 2.0f, 2.0f);
                    eyes_model.Render(device, effect, time);
                }

                if (hair_model != null)
                {
                    hair_model.scale.Set(2.0f, 2.0f, 2.0f);
                    hair_model.Render(device, effect, time);
                }

                ItemStack stack = Inventory.hotbar[Inventory.selected];
                if (stack != null)
                {
                    if (stack.item.Model != null)
                    {
                        Vector3f renderPos = null;
                        Quaternionf renderRot = null;
                        foreach (Part part in model.Parts)
                        {
                            if (part.name.Contains("Right_Hand"))
                            {
                                renderPos = part.RenderPosition;
                                renderRot = part.RenderRotation;
                            }
                        }

                        stack.item.Model.scale = new Vector3f(1.0f, 1.0f, 1.0f);
                        stack.item.Model.translation = renderPos;

                        Vector3f euler = renderRot.ToEulerAngles();

                        Vector3f e2 = new Vector3f(euler.Z, euler.X, euler.Y);

                        stack.item.Model.rotation = new Vector3f(0, 90f * 3.14f / 180.0f, 90f * 3.14f / 180.0f).Add(new Vector3f(model.rotation)).Add(e2);

                        stack.item.Model.Render(device, effect, time);
                    }
                }
            }
            SetHeadRotation(-look.X, 0);

            lean = MathHelper.Lerp(lean, lastYaw - look.Y, 0.1f);
            fLean = MathHelper.Lerp(fLean, forward, 0.15f);
            rLean = MathHelper.Lerp(rLean, right, 0.15f);

            forward = 0;
            right = 0;

            SetLean(lean + rLean * 30);
            SetForwardLean(fLean * 45);

            lastYaw = look.Y;

            
        }

        private float rLean;
        private float fLean;

        private float lastYaw = 0;
        private float lean = 0;

        public void SetForwardLean(float lean)
        {
            model.rotation.X = lean * (float)System.Math.PI / 180.0f;
            eyes_model.rotation.X = lean * (float)System.Math.PI / 180.0f;
            shirt_model.rotation.X = lean * (float)System.Math.PI / 180.0f;
            pants_model.rotation.X = lean * (float)System.Math.PI / 180.0f;
            shoes_model.rotation.X = lean * (float)System.Math.PI / 180.0f;
            hair_model.rotation.X = lean * (float)System.Math.PI / 180.0f;
        }

        public void SetLean(float lean)
        {
            model.rotation.Z = lean * (float)System.Math.PI / 180.0f;
            eyes_model.rotation.Z = lean * (float)System.Math.PI / 180.0f;
            shirt_model.rotation.Z = lean * (float)System.Math.PI / 180.0f;
            pants_model.rotation.Z = lean * (float)System.Math.PI / 180.0f;
            shoes_model.rotation.Z = lean * (float)System.Math.PI / 180.0f;
            hair_model.rotation.Z = lean * (float)System.Math.PI / 180.0f;
        }

        public void SetHeadRotation(float pitch, float yaw)
        {
            model.extra_rotations["Neck"] = new Vector3(pitch * (float)System.Math.PI / 180.0f, yaw * (float)System.Math.PI / 180.0f, 0);
            eyes_model.extra_rotations["Neck"] = new Vector3(pitch * (float)System.Math.PI / 180.0f, yaw * (float)System.Math.PI / 180.0f, 0);
            shirt_model.extra_rotations["Neck"] = new Vector3(pitch * (float)System.Math.PI / 180.0f, yaw * (float)System.Math.PI / 180.0f, 0);
            pants_model.extra_rotations["Neck"] = new Vector3(pitch * (float)System.Math.PI / 180.0f, yaw * (float)System.Math.PI / 180.0f, 0);
            shoes_model.extra_rotations["Neck"] = new Vector3(pitch * (float)System.Math.PI / 180.0f, yaw * (float)System.Math.PI / 180.0f, 0);
            hair_model.extra_rotations["Neck"] = new Vector3(pitch * (float)System.Math.PI / 180.0f, yaw * (float)System.Math.PI / 180.0f, 0);
        }

        public void TryPlayAnimation(List<Keyframe> animation)
        {
            if (model != null)
            {
                if (model.timeline != animation)
                {
                    model.timeline = animation;
                    model.Play(0);
                    shirt_model.timeline = animation;
                    shirt_model.Play(0);
                    pants_model.timeline = animation;
                    pants_model.Play(0);
                    eyes_model.timeline = animation;
                    eyes_model.Play(0);
                    shoes_model.timeline = animation;
                    shoes_model.Play(0);
                    hair_model.timeline = animation;
                    hair_model.Play(0);
                }
            }
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
       

        public override float GetMovementSpeed(GameTime time)
        {
            if (Flying && !Crouching)
            {
                if (Running) return 0.2f * (float)time.ElapsedGameTime.TotalSeconds * 60;
                return 0.1f * (float)time.ElapsedGameTime.TotalSeconds * 60;
            }
            return base.GetMovementSpeed(time);
        }

        public override void PlayStepSound(SoundType soundType, float y_offset = 0)
        {
            TilePos pos = GetTilePos();
            if (y_offset != 0)
            {
                pos = new TilePos(pos.x, (float)System.Math.Round(pos.y + y_offset), pos.z);
            }
            if (velocity.Y >= -0.01f)
            {
                pos.y -= 1;
            }
            SoundEffect[] sounds = TileManager.GetTile(world.GetVoxel(pos).tile_id).step_sound;
            if (sounds != null)
            {
                SoundEffect effect = sounds[world.random.Next(sounds.Length)];
                GameSound sound = new GameSound(effect.CreateInstance(), soundType);
                sound.Volume = 0.5f;
                if (Running) sound.Volume = 0.75f;
                if (Crouching) sound.Volume = 0.35f;
                if (Crawling) sound.Volume = 0.2f;
                sound.Play();
                SoundsToDispose.Add(sound);
            }
        }

    }
}
