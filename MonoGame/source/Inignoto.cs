﻿using Microsoft.Xna.Framework;
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

        public bool mouse_captured = true;

        Matrix projectionMatrix;
        Camera camera;

        BasicEffect basicEffect;

        World.World world;

        public Thread world_thread;
        private bool running = true;

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
                1f, 1000f);
            
            

            basicEffect = new BasicEffect(GraphicsDevice);
            basicEffect.Alpha = 1f;
            basicEffect.VertexColorEnabled = true;
            basicEffect.TextureEnabled = true;

            basicEffect.LightingEnabled = false;
            
            Vector3f a = new Vector3f(0, 20, 0);
            Vector3f b = new Vector3f(0, -20, 0);
            Vector3f c = new Vector3f(20, -20, 0);
            Vector3f d = new Vector3f(20, 20, 0);

            GameResources.LoadResources();

            world = new World.World();

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
            mousePos = Mouse.GetState().Position;
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back ==
               ButtonState.Pressed || Keyboard.GetState().IsKeyDown(
               Keys.Escape))
            {
                mouse_captured = false;
                this.IsMouseVisible = true;
            }
            camera.Update(gameTime);
            
            base.Update(gameTime);
            lastMousePos = new Point(mousePos.X, mousePos.Y);

            if (mouse_captured)
            {
                int width = Window.ClientBounds.Right - Window.ClientBounds.Left;
                int height = Window.ClientBounds.Top - Window.ClientBounds.Bottom;
                Mouse.SetPosition(width / 2, -height / 2);
                mousePos = new Point(width / 2, -height / 2);
                lastMousePos = new Point(width / 2, -height / 2);
            } else
            {
                if (Mouse.GetState().LeftButton == ButtonState.Pressed)
                {
                    mouse_captured = true;
                    this.IsMouseVisible = false;
                }
            }
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            basicEffect.Projection = projectionMatrix;
            basicEffect.View = camera.ViewMatrix;
            
            GraphicsDevice.Clear(Color.CornflowerBlue);
            
            RasterizerState rasterizerState = new RasterizerState();
            rasterizerState.CullMode = CullMode.None;
            GraphicsDevice.RasterizerState = rasterizerState;
            GraphicsDevice.DepthStencilState = DepthStencilState.Default;
            GraphicsDevice.SamplerStates[0] = SamplerState.PointClamp;

            world.Update(camera.position.Vector);

            world.Render(GraphicsDevice, basicEffect);

            base.Draw(gameTime);
        }
    }
}
