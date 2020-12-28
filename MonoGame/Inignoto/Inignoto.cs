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
using Inignoto.Tiles;
using System.Collections.Generic;
using Inignoto.Audio;
using Inignoto.Items;
using System.Runtime.CompilerServices;
using System.Runtime;
using System.Threading.Tasks;

namespace Inignoto
{
    
    /// <summary>
    /// This is the main type for your game.
    /// </summary>
    public class Inignoto : Game
    {
        public enum GameState {
            MENU, GAME
        }

        public GameState game_state = GameState.MENU;


        public static Inignoto game;
        private GraphicsDeviceManager graphics;
        private SpriteBatch spriteBatch;

        public Point mousePos = new Point();
        public Point lastMousePos = new Point();

        public bool mouse_captured = false;

        public Matrix projectionMatrix;
        public Camera camera;

        public List<GameSound> SoundsToDispose = new List<GameSound>();

        public World.World world;
        public ClientPlayerEntity player;

        public Thread world_generation_thread;
        public Thread world_tick_thread;
        public Thread rebuild_chunk_thread;

        public bool running = true;

        public Hud hud;

        public bool paused;

        public long currentFrame = 0;
        public long lastFrame = 0;

        public Rectangle ClientBounds;

        public ClientSystem client_system;

        public readonly int target_width = 1920;
        public readonly int target_height = 1080;
        
        public Inignoto()
        {
            graphics = new GraphicsDeviceManager(this);
            graphics.GraphicsProfile = GraphicsProfile.HiDef;
            graphics.PreferMultiSampling = false;
            TargetElapsedTime = TimeSpan.FromSeconds(1.0f / 60.0f);
            
            float scaleX = graphics.PreferredBackBufferWidth / target_width;
            float scaleY = graphics.PreferredBackBufferHeight / target_height;

            client_system = new ClientSystem();

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
            
            Vector3 a = new Vector3(0, 20, 0);
            Vector3 b = new Vector3(0, -20, 0);
            Vector3 c = new Vector3(20, -20, 0);
            Vector3 d = new Vector3(20, 20, 0);

            GameResources.LoadResources();

            ThreadStart world_thread_start = new ThreadStart(UpdateWorldGeneration);
            world_generation_thread = new Thread(world_thread_start);
            world_generation_thread.IsBackground = true;
            world_generation_thread.Priority = ThreadPriority.AboveNormal;
            world_generation_thread.Start();

            ThreadStart rebuild_chunk_start = new ThreadStart(RebuildChunks);
            rebuild_chunk_thread = new Thread(rebuild_chunk_start);
            rebuild_chunk_thread.IsBackground = true;
            world_generation_thread.Priority = ThreadPriority.Highest;
            //rebuild_chunk_thread.Start();

            ThreadStart world_tick_start = new ThreadStart(TickWorld);
            world_tick_thread = new Thread(world_tick_start);
            world_tick_thread.IsBackground = true;
            world_generation_thread.Priority = ThreadPriority.Normal;
            world_tick_thread.Start();

            
        }
        public GameTime gametime = new GameTime();
        public static void RebuildChunks()
        {
            while (game.running)
            {
                if (game.game_state == GameState.GAME)
                {
                }
            }
        }
        public static void TickWorld()
        {
            while (game != null && game.running)
            {
                if (game.game_state == GameState.GAME)
                {
                    Action b = () =>
                    {
                        if (game.world != null)
                            game.world.chunkManager.RebuildChunks();
                    };
                    Action c = () =>
                    {
                        if (game.world != null)
                            game.world.UpdateChunkGeneration();
                    };
                    try
                    {
                        Parallel.Invoke(b, c);
                    } catch (System.AggregateException e)
                    {
                        Console.WriteLine(e.Message);
                    }
                }
            }
        }
        public static void UpdateWorldGeneration()
        {
            while (game.running)
            {
                if (game.game_state == GameState.GAME)
                    if (game.world != null)
                    {
                        game.world.TickChunks();
                    }
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

        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            if (graphics.SynchronizeWithVerticalRetrace != Settings.VSYNC)
            graphics.SynchronizeWithVerticalRetrace = Settings.VSYNC;

            ClientBounds = Window.ClientBounds;
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

            mousePos = Mouse.GetState().Position;


            UpdateInput(gameTime);

            camera.Update(gameTime);


            if (game.game_state == GameState.GAME)
            {
                world.Update(camera.position, gameTime);
            }

            base.Update(gameTime);

            lastMousePos.X = mousePos.X;
            lastMousePos.Y = mousePos.Y;


            if (!IsActive || paused || !(game_state == GameState.GAME)) mouse_captured = false;
            else
                mouse_captured = Hud.openGui == null;

            if (mouse_captured)
            {
                Mouse.SetPosition(width / 2, -height / 2);
                mousePos.X = width / 2;
                mousePos.Y = -height / 2;
                lastMousePos.X = width / 2;
                lastMousePos.Y = -height / 2;

                IsMouseVisible = false;
            } else
            {
                IsMouseVisible = true;
            }
            if (graphics.IsFullScreen != Settings.FULLSCREEN)
            {
                if (Settings.FULLSCREEN)
                {
                    graphics.PreferredBackBufferWidth = GraphicsAdapter.DefaultAdapter.CurrentDisplayMode.Width;
                    graphics.PreferredBackBufferHeight = GraphicsAdapter.DefaultAdapter.CurrentDisplayMode.Height;
                } else
                {
                    graphics.PreferredBackBufferWidth = GraphicsAdapter.DefaultAdapter.CurrentDisplayMode.Width / 2;
                    graphics.PreferredBackBufferHeight = GraphicsAdapter.DefaultAdapter.CurrentDisplayMode.Height / 2;
                }
                graphics.ToggleFullScreen();
                graphics.ApplyChanges();
            }
            lastFrame = currentFrame;
            currentFrame++;
        }

        private Rectangle screen_rect = new Rectangle(0, 0, 0, 0);
        private void UpdateInput(GameTime gameTime)
        {
            
            InputSetting.Update();

            if (Settings.RELOAD_ASSETS.IsJustPressed())
            {
                lock (Textures.TILE_ITEMS)
                {
                    GameResources.ReloadResources();
                }
                
            }

            int width = ClientBounds.Right - ClientBounds.Left;
            int height = ClientBounds.Bottom - ClientBounds.Top;
            hud.Update(gameTime, width, height);

            if (Settings.FULLSCREEN_KEY.IsJustPressed())
            {
                Settings.FULLSCREEN = !Settings.FULLSCREEN;
            }

            if (game.game_state == GameState.GAME)
                if (Settings.INVENTORY.IsJustPressed())
                {
                    if (Hud.openGui == null)
                    {
                        Hud.openGui = new InventoryGui(game.player.Inventory);
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
            gametime = gameTime;
            GameResources.effect.Time = (float)gameTime.TotalGameTime.TotalSeconds;

            int width = Window.ClientBounds.Right - Window.ClientBounds.Left;
            int height = Window.ClientBounds.Bottom - Window.ClientBounds.Top;

            GameResources.effect.Projection = projectionMatrix;

            Vector3 lastPos = camera.position;
            camera.position = new Vector3(0, 0, 0);
            Vector3 lastRot = camera.rotation;
            camera.rotation = new Vector3(0, 0, 0);
            GameResources.effect.View = camera.ViewMatrix;

            GraphicsDevice.BlendState = BlendState.NonPremultiplied;

            ItemRegistry.DrawItems(gameTime);

            if (Hud.openGui != null)
            {
                Hud.openGui.PreRender(GraphicsDevice, spriteBatch, width, height, gameTime);
            }

            camera.position = lastPos;
            camera.rotation = lastRot;

                
            GameResources.effect.View = camera.ViewMatrix;
            GraphicsDevice.Clear(Color.CornflowerBlue);

            GraphicsDevice.RasterizerState = GameResources.DEFAULT_RASTERIZER_STATE;
            GraphicsDevice.DepthStencilState = DepthStencilState.Default;
            GraphicsDevice.SamplerStates[0] = SamplerState.PointClamp;

            TileRegistry.TryLoadTileTextures();

            GameResources.effect.HasShadows = Settings.SHADOWS;
            if (game.game_state == GameState.GAME)
                if (Settings.SHADOWS)
                {

                    Vector3 vec = new Vector3(camera.position.X, camera.position.Y, camera.position.Z);
                    vec.Ceiling();

                    GameResources.shadowMap.Begin(vec, world.sunLook, 0);
                    world.Render(GraphicsDevice, GameResources.shadowMap._ShadowMapGenerate, gameTime);

                    GameResources.shadowMap.Begin(vec, world.sunLook, 1);
                    world.Render(GraphicsDevice, GameResources.shadowMap._ShadowMapGenerate, gameTime);

                    GameResources.shadowMap.End();

                }


            Viewport port = GraphicsDevice.Viewport;
            GraphicsDevice.Viewport = new Viewport(new Rectangle(0, 0, 1920, 1080));

            GraphicsDevice.SetRenderTarget(GameResources.gameImage);

            if (game.game_state == GameState.GAME)
            {
                world.Render(GraphicsDevice, GameResources.effect, gameTime);
            }

            if (Hud.openGui is MainMenu)
            {
                MainMenu menu = (MainMenu)Hud.openGui;
                if (menu.menu_state == MainMenu.MenuState.MODEL_CREATOR)
                {
                    menu.Render3D(gameTime);
                }
            }


            GraphicsDevice.SetRenderTarget(null);
            GraphicsDevice.Viewport = port; 

            spriteBatch.Begin(SpriteSortMode.Deferred, BlendState.NonPremultiplied, SamplerState.PointClamp, DepthStencilState.Default, RasterizerState.CullCounterClockwise, GameResources.postProcessing);

            screen_rect.Width = width;
            screen_rect.Height = height;

            spriteBatch.Draw(GameResources.gameImage, screen_rect, Color.White);

            spriteBatch.End();

            spriteBatch.Begin(SpriteSortMode.Deferred,
              BlendState.NonPremultiplied,
              SamplerState.PointClamp);

            client_system.RenderBackground(spriteBatch, GraphicsDevice, gameTime, width, height);

            hud.Render(GraphicsDevice, spriteBatch, width, height, gameTime);

            client_system.Render(spriteBatch, GraphicsDevice, gameTime, width, height);

            spriteBatch.End();

            base.Draw(gameTime);

        }
    }
}
