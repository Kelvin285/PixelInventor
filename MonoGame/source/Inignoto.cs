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

        public BasicEffect BasicEffect { get; private set; }
        public List<GameSound> SoundsToDispose = new List<GameSound>();

        public World.World world;
        public ClientPlayerEntity player;

        public Thread world_thread;
        private bool running = true;

        public Hud hud;

        public long currentFrame = 0;
        public long lastFrame = 0;

        public Inignoto()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
           
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
            
            

            BasicEffect = new BasicEffect(GraphicsDevice);
            BasicEffect.Alpha = 1f;
            BasicEffect.VertexColorEnabled = true;
            BasicEffect.TextureEnabled = true;

            BasicEffect.LightingEnabled = false;
            
            Vector3f a = new Vector3f(0, 20, 0);
            Vector3f b = new Vector3f(0, -20, 0);
            Vector3f c = new Vector3f(20, -20, 0);
            Vector3f d = new Vector3f(20, 20, 0);

            GameResources.LoadResources();

            ThreadStart world_thread_start = new ThreadStart(UpdateWorldGeneration);
            world_thread = new Thread(world_thread_start);
            world_thread.IsBackground = true;
            world_thread.Start();
        }

        public static void UpdateWorldGeneration()
        {
            while (Inignoto.game.running)
            {
                Inignoto.game.world.UpdateChunkGeneration();
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
            int width = Window.ClientBounds.Right - Window.ClientBounds.Left;
            int height = Window.ClientBounds.Top - Window.ClientBounds.Bottom;

            projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);

            mousePos = Mouse.GetState().Position;

            UpdateInput(gameTime);

            camera.Update(gameTime);

            world.Update(camera.position.Vector, gameTime);

            base.Update(gameTime);
            lastMousePos = new Point(mousePos.X, mousePos.Y);

            

            if (!IsActive) mouse_captured = false;
            else
                mouse_captured = Hud.openGui == null;

            if (mouse_captured)
            {
                Mouse.SetPosition(width / 2, -height / 2);
                mousePos = new Point(width / 2, -height / 2);
                lastMousePos = new Point(width / 2, -height / 2);
                IsMouseVisible = false;
            } else
            {
                IsMouseVisible = true;
            }
            lastFrame = currentFrame;
            currentFrame++;
        }

        private void UpdateInput(GameTime gameTime)
        {
            InputSetting.Update();

            int width = Window.ClientBounds.Right - Window.ClientBounds.Left;
            int height = Window.ClientBounds.Bottom - Window.ClientBounds.Top;
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

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            int width = Window.ClientBounds.Right - Window.ClientBounds.Left;
            int height = Window.ClientBounds.Bottom - Window.ClientBounds.Top;
            
            BasicEffect.Projection = projectionMatrix;

            Vector3f lastPos = camera.position;
            camera.position = new Vector3f(0, 0, 0);
            Vector3f lastRot = camera.rotation;
            camera.rotation = new Vector3f(0, 0, 0);
            BasicEffect.View = camera.ViewMatrix;

            if (Hud.openGui != null)
            {
                
                Hud.openGui.PreRender(GraphicsDevice, spriteBatch, width, height, gameTime);
            }

            camera.position = lastPos;
            camera.rotation = lastRot;
            BasicEffect.View = camera.ViewMatrix;
            GraphicsDevice.Clear(Color.CornflowerBlue);
            
            RasterizerState rasterizerState = new RasterizerState();
            rasterizerState.CullMode = CullMode.None;
            GraphicsDevice.RasterizerState = rasterizerState;
            GraphicsDevice.DepthStencilState = DepthStencilState.Default;
            GraphicsDevice.SamplerStates[0] = SamplerState.PointClamp;

            TileManager.TryLoadTileTextures();

            world.Render(GraphicsDevice, BasicEffect, gameTime);


            
            spriteBatch.Begin(SpriteSortMode.Deferred,
      BlendState.NonPremultiplied,
      SamplerState.PointClamp);

            hud.Render(GraphicsDevice, spriteBatch, width, height, gameTime);

            spriteBatch.End();

            base.Draw(gameTime);
        }
    }
}
