using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Audio;
using Inignoto.Client;
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

        private int perspective = 1;

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

            if (Settings.PERSPECTIVE_SWITCH.IsJustPressed())
            {
                perspective++;
                perspective %= 3;
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

            cpos.Add(ForwardLook.Mul(-(perspective - 1) * 4.0f));

            TileRaytraceResult result = world.RayTraceTiles(GetEyePosition(), cpos, TileRayTraceType.BLOCK);

            cpos.Sub(ForwardLook.Mul(-(perspective - 1) * 0.5f));

            if (result != null)
            {
                Vector3f dir = new Vector3f(cpos).Sub(GetEyePosition());
                if (dir.Vector.Length() != 0)
                    dir.Div(dir.Vector.Length());
                cpos = GetEyePosition().Add(dir.Mul(result.intersection.lambda.X - 0.5f));
            }
            
            camera.position.Set(Vector3.Lerp(camera.position.Vector, cpos.Vector, 1f));
            camera.rotation.Set(look.X, look.Y, look.Z);
            if (perspective == 0)
            {
                camera.rotation.Set(-look.X, look.Y + 180, -look.Z);
            }

            SetModelTransform(position);
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

                shirt.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                shirt.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);

                pants.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                pants.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);

                shoes.translation = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * 0.5f);
                shoes.rotation.Y = (180 + look.Y) * ((float)System.Math.PI / 180.0f);
            }
        }

        public GameModel model;
        public GameModel shirt;
        public GameModel pants;
        public GameModel shoes;
        public Texture2D texture;

        public Texture2D shirtTexture;
        public Texture2D pantsTexture;
        public Texture2D shoesTexture;

        public override void Render(GraphicsDevice device, BasicEffect effect, bool showModel = false)
        {
            if (model == null)
            {
                texture = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/skin/skin1.png", "assets"));
                model = GameModel.LoadModel(new Utilities.ResourcePath("inignoto", "models/test.model", "assets"), texture);

                shirtTexture = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/shirt/shirt1.png", "assets"));
                shirt = GameModel.LoadModel(new Utilities.ResourcePath("inignoto", "models/shirt.model", "assets"), shirtTexture);

                pantsTexture = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/pants/pants1.png", "assets"));
                pants = GameModel.LoadModel(new Utilities.ResourcePath("inignoto", "models/pants.model", "assets"), pantsTexture);

                shoesTexture = Textures.LoadTexture(new Utilities.ResourcePath("Inignoto", "textures/entity/player/shoes/shoes1.png", "assets"));
                shoes = GameModel.LoadModel(new Utilities.ResourcePath("inignoto", "models/shoes.model", "assets"), shoesTexture);
            }
            if (model != null && perspective != 1 || model != null && showModel)
            {
                model.scale.Set(2.0f, 2.0f, 2.0f);
                model.Render(device, effect);

                shoes.scale.Set(2.0f, 2.0f, 2.0f);
                shoes.Render(device, effect);

                pants.scale.Set(2.0f, 2.0f, 2.0f);
                pants.Render(device, effect);

                shirt.scale.Set(2.0f, 2.0f, 2.0f);
                shirt.Render(device, effect);
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
                if (Crouching) sound.Volume = 0.25f;
                if (Crawling) sound.Volume = 0.1f;
                sound.Play();
                SoundsToDispose.Add(sound);
            }
        }

    }
}
