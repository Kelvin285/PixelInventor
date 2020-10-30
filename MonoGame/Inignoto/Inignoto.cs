using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System.IO;
using Inignoto.Utilities;
using Inignoto.Graphics.Textures;
using Inignoto.Math;
using Inignoto.Graphics.Mesh;
using Inignoto.Client;
using Inignoto.Graphics.World;
using Inignoto.World.Chunks;
using System.Threading;
using System;
using Inignoto.GameSettings;
using Inignoto.Entities.Client.Player;
using Inignoto.Graphics.Gui;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Media;
using Inignoto.Tiles;
using System.Collections.Generic;
using Inignoto.Audio;
using Inignoto.Items;
using System.Windows.Forms;
using System.Diagnostics;
using System.Runtime.InteropServices;
using static Inignoto.VulkanMain;

namespace Inignoto
{
    
    /// <summary>
    /// This is the main type for your game.
    /// </summary>
    public class Inignoto : Game
    {
        public static Inignoto game;
        private GraphicsDeviceManager graphics;
        private SpriteBatch spriteBatch;

        public Point mousePos;
        public Point lastMousePos;

        public bool mouse_captured = false;

        private Matrix projectionMatrix;
        public Camera camera;

        public List<GameSound> SoundsToDispose = new List<GameSound>();

        public World.World world;
        public ClientPlayerEntity player;

        public Thread world_thread;
        public Thread rerender_thread;
        public bool running = true;

        public Hud hud;

        public long currentFrame = 0;
        public long lastFrame = 0;

        public VulkanMain vulkan;
        public bool VULKAN_ENABLED = false;

        public Rectangle ClientBounds;

        public readonly int target_width = 1920;
        public readonly int target_height = 1080;

        private Matrix content_scale;

        public Inignoto()
        {

            graphics = new GraphicsDeviceManager(this);
            graphics.GraphicsProfile = GraphicsProfile.HiDef;
            graphics.PreferMultiSampling = false;

            
            float scaleX = graphics.PreferredBackBufferWidth / target_width;
            float scaleY = graphics.PreferredBackBufferHeight / target_height;


            Content.RootDirectory = "Content";
            running = true;
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {


            // TODO: Add your initialization logic here
            base.Initialize();

            Window.Title = "Inignoto";
            Window.AllowUserResizing = true;

            camera = new Camera();
            
            projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);
            
            

            //GameResources.effect.Alpha = 1f;
            //GameResources.effect.VertexColorEnabled = true;
            //GameResources.effect.TextureEnabled = true;

            //GameResources.effect.LightingEnabled = false;
            
            Vector3f a = new Vector3f(0, 20, 0);
            Vector3f b = new Vector3f(0, -20, 0);
            Vector3f c = new Vector3f(20, -20, 0);
            Vector3f d = new Vector3f(20, 20, 0);

            GameResources.LoadResources();

            ThreadStart world_thread_start = new ThreadStart(UpdateWorldGeneration);
            world_thread = new Thread(world_thread_start);
            world_thread.IsBackground = true;
            world_thread.Start();

            ThreadStart rerender_thread_start = new ThreadStart(FixWorldChunkBorders);
            rerender_thread = new Thread(rerender_thread_start);
            rerender_thread.IsBackground = true;
            rerender_thread.Start();

            TryStartVulkan();
            if (VULKAN_ENABLED)
            {
                Exit();
            }
        }

        public static void UpdateWorldGeneration()
        {
            while (Inignoto.game.running)
            {
                Inignoto.game.world.UpdateChunkGeneration();
                Thread.Sleep(5);
            }
        }

        public static void FixWorldChunkBorders()
        {
            while (Inignoto.game.running)
            {                
                Inignoto.game.world.FixChunkBorders();
                Thread.Sleep(5);
            }
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);

            // TODO: use this.Content to load your game content here
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// game-specific content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
            GameResources.Dispose();
        }

        protected override void OnExiting(object sender, EventArgs args)
        {
            running = false;
            Settings.SaveSettings();
            GameResources.Dispose();
            base.OnExiting(sender, args);
        }


        bool space = false;
        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {

            if (graphics.SynchronizeWithVerticalRetrace != Settings.VSYNC)
            graphics.SynchronizeWithVerticalRetrace = Settings.VSYNC;
            
            if (!VULKAN_ENABLED)
            {
                ClientBounds = Window.ClientBounds;
            } else
            {
                camera.position = player.position;
            }
            for (int i = 0; i < SoundsToDispose.Count; i++)
            {
                GameSound sound = SoundsToDispose[i];
                if (sound != null)
                    if (sound.State != SoundState.Playing)
                    {
                        sound.Dispose();
                        SoundsToDispose.Remove(sound);
                    }
            }
            SoundEffect.MasterVolume = Settings.MASTER_VOLUME / 100.0f;
            int width = ClientBounds.Right - ClientBounds.Left;
            int height = ClientBounds.Top - ClientBounds.Bottom;

            projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);

            if (VULKAN_ENABLED)
            {
                Vec3f mp = VulkanMain.GetMousePos();
                lastMousePos = new Point(mousePos.X, mousePos.Y);
                mousePos = new Point((int)mp.x, (int)mp.y);
            } else
            {
                mousePos = Mouse.GetState().Position;
            }
            

            UpdateInput(gameTime);

            camera.Update(gameTime);

            
            if (VULKAN_ENABLED) {
                VulkanMain.UpdateCamera();
            }


            world.Update(camera.position.Vector, gameTime);

            base.Update(gameTime);

            lastMousePos = new Point(mousePos.X, mousePos.Y);


            if (!IsActive) mouse_captured = false;
            else
                mouse_captured = Hud.openGui == null;

            if (mouse_captured)
            {
                if (!VULKAN_ENABLED)
                {
                    Mouse.SetPosition(width / 2, -height / 2);
                    mousePos = new Point(width / 2, -height / 2);
                    lastMousePos = new Point(width / 2, -height / 2);
                }
                
                IsMouseVisible = false;
            } else
            {
                try
                {
                    IsMouseVisible = true;
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                }
            }
            lastFrame = currentFrame;
            currentFrame++;
        }

        private void UpdateInput(GameTime gameTime)
        {
            
            InputSetting.Update();

            int width = ClientBounds.Right - ClientBounds.Left;
            int height = ClientBounds.Bottom - ClientBounds.Top;
            hud.Update(gameTime, width, height);

            if (Settings.INVENTORY.IsJustPressed())
            {
                if (Hud.openGui == null)
                {
                    Hud.openGui = new InventoryGui(Inignoto.game.player.Inventory);
                } else
                {
                    Hud.openGui.Close();
                }
            }
            if (Hud.openGui != null)
            {
                Hud.openGui.Update(gameTime, width, height);
            }
        }

        private GameTime time = new GameTime();

        public void UpdateVulkan()
        {
            int begin = System.DateTime.Now.Millisecond;
            while (running && VULKAN_ENABLED)
            {                
                int start = System.DateTime.Now.Millisecond;
                Update(time);
                RenderVulkan(time);
                int end = System.DateTime.Now.Millisecond;
                

                time.ElapsedGameTime = new TimeSpan(end - start);
                time.TotalGameTime = new TimeSpan(begin - System.DateTime.Now.Millisecond);
                if (this.IsFixedTimeStep)
                {
                    if (time.ElapsedGameTime > this.TargetElapsedTime)
                    {
                        time.IsRunningSlowly = true;
                    }
                }
            }
        }

        public void TryStartVulkan()
        {
            VULKAN_ENABLED = true;
            vulkan = new VulkanMain();
            Debug.WriteLine("VULKAN ENABLED = " + VULKAN_ENABLED);
            if (VULKAN_ENABLED)
            {
                ThreadStart start = new ThreadStart(UpdateVulkan);
                Thread thread = new Thread(start);
                thread.IsBackground = true;
                thread.Start();

                vulkan.Run();
            }
        }

        protected void RenderVulkan(GameTime time)
        {
            int width = ClientBounds.Right - ClientBounds.Left;
            int height = ClientBounds.Bottom - ClientBounds.Top;

            TileManager.TryLoadTileTextures();

            world.RenderVulkan(time);

        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GameResources.effect.Time = (float)gameTime.TotalGameTime.TotalSeconds;

            int width = Window.ClientBounds.Right - Window.ClientBounds.Left;
            int height = Window.ClientBounds.Bottom - Window.ClientBounds.Top;

            GameResources.effect.Projection = projectionMatrix;

            Vector3f lastPos = camera.position;
            camera.position = new Vector3f(0, 0, 0);
            Vector3f lastRot = camera.rotation;
            camera.rotation = new Vector3f(0, 0, 0);
            GameResources.effect.View = camera.ViewMatrix;

            GraphicsDevice.BlendState = BlendState.NonPremultiplied;

            ItemManager.DrawItems(gameTime);

            if (Hud.openGui != null)
            {
                Hud.openGui.PreRender(GraphicsDevice, spriteBatch, width, height, gameTime);
            }

            camera.position = lastPos;
            camera.rotation = lastRot;
            GameResources.effect.View = camera.ViewMatrix;
            GraphicsDevice.Clear(Color.CornflowerBlue);

            RasterizerState rasterizerState = new RasterizerState();
            rasterizerState.CullMode = CullMode.None;
            GraphicsDevice.RasterizerState = rasterizerState;
            GraphicsDevice.DepthStencilState = DepthStencilState.Default;
            GraphicsDevice.SamplerStates[0] = SamplerState.PointClamp;

            TileManager.TryLoadTileTextures();

            world.Render(GraphicsDevice, GameResources.effect, gameTime);



            spriteBatch.Begin(SpriteSortMode.Deferred,
              BlendState.NonPremultiplied,
              SamplerState.PointClamp);

            hud.Render(GraphicsDevice, spriteBatch, width, height, gameTime);

            spriteBatch.End();


            base.Draw(gameTime);

        }
    }
}
