using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System.IO;
using Inignoto.Utilities;
using Inignoto.Graphics.Textures;
using Inignoto.Math;

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

        Vector3 camTarget;
        Vector3 camPosition;
        Matrix projectionMatrix;
        Matrix viewMatrix;
        Matrix worldMatrix;

        BasicEffect basicEffect;

        VertexPositionColorTexture[] triangleVertices;
        VertexBuffer vertexBuffer;
        
        bool orbit = false;
        
        

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

            camTarget = new Vector3(0f, 0f, 0f);
            camPosition = new Vector3(0f, 0f, -100f);
            projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(45f),
                               GraphicsDevice.DisplayMode.AspectRatio,
                1f, 1000f);
            viewMatrix = Matrix.CreateLookAt(camPosition, camTarget,
                         new Vector3(0f, 1f, 0f));// Y up
            worldMatrix = Matrix.CreateWorld(camTarget, Vector3.
                          Forward, Vector3.Up);

            basicEffect = new BasicEffect(GraphicsDevice);
            basicEffect.Alpha = 1f;
            basicEffect.VertexColorEnabled = true;
            basicEffect.TextureEnabled = true;

            basicEffect.LightingEnabled = false;

            Vector3f a = new Vector3f(0, 20, 0);
            Vector3f b = new Vector3f(0, -20, 0);
            Vector3f c = new Vector3f(20, -20, 0);
            Vector3f d = new Vector3f(20, 20, 0);

            Quaternionf quat = new Quaternionf(1, 1, 1);
            a.rotate(quat);
            //b.rotate(quat);
            //c.rotate(quat);
            //d.rotate(quat);

            //Geometry  - a simple triangle about the origin
            triangleVertices = new VertexPositionColorTexture[6];
            triangleVertices[0] = new VertexPositionColorTexture(a.getVector(), Color.White, new Vector2(0, 0));
            triangleVertices[1] = new VertexPositionColorTexture(b.getVector(), Color.White, new Vector2(0, 1.0f));
            triangleVertices[2] = new VertexPositionColorTexture(c.getVector(), Color.White, new Vector2(1.0f, 1.0f));


            triangleVertices[3] = new VertexPositionColorTexture(c.getVector(), Color.White, new Vector2(1.0f, 1.0f));
            triangleVertices[4] = new VertexPositionColorTexture(d.getVector(), Color.White, new Vector2(1.0f, 0));
            triangleVertices[5] = new VertexPositionColorTexture(a.getVector(), Color.White, new Vector2(0, 0));
            
            vertexBuffer = new VertexBuffer(GraphicsDevice, typeof(
                           VertexPositionColorTexture), 6, BufferUsage.
                           WriteOnly);

            vertexBuffer.SetData<VertexPositionColorTexture>(triangleVertices);

            GameResources.loadResources();
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
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back ==
                ButtonState.Pressed || Keyboard.GetState().IsKeyDown(
                Keys.Escape))
                Exit();

            if (Keyboard.GetState().IsKeyDown(Keys.Left))
            {
                camPosition.X -= 1f;
                camTarget.X -= 1f;
            }
            if (Keyboard.GetState().IsKeyDown(Keys.Right))
            {
                camPosition.X += 1f;
                camTarget.X += 1f;
            }
            if (Keyboard.GetState().IsKeyDown(Keys.Up))
            {
                camPosition.Y -= 1f;
                camTarget.Y -= 1f;
            }
            if (Keyboard.GetState().IsKeyDown(Keys.Down))
            {
                camPosition.Y += 1f;
                camTarget.Y += 1f;
            }
            if (Keyboard.GetState().IsKeyDown(Keys.OemPlus))
            {
                camPosition.Z += 1f;
            }
            if (Keyboard.GetState().IsKeyDown(Keys.OemMinus))
            {
                camPosition.Z -= 1f;
            }
            if (Keyboard.GetState().IsKeyDown(Keys.Space))
            {
                orbit = !orbit;
            }

            if (orbit)
            {
                Matrix rotationMatrix = Matrix.CreateRotationY(
                                        MathHelper.ToRadians(1f));
                camPosition = Vector3.Transform(camPosition,
                              rotationMatrix);
            }
            viewMatrix = Matrix.CreateLookAt(camPosition, camTarget,
                         Vector3.Up);
            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            basicEffect.Projection = projectionMatrix;
            basicEffect.View = viewMatrix;
            basicEffect.World = worldMatrix;

            GraphicsDevice.Clear(Color.CornflowerBlue);
            GraphicsDevice.SetVertexBuffer(vertexBuffer);
            
            RasterizerState rasterizerState = new RasterizerState();
            rasterizerState.CullMode = CullMode.None;
            GraphicsDevice.RasterizerState = rasterizerState;

            
            foreach (EffectPass pass in basicEffect.CurrentTechnique.
                    Passes)
            {
                basicEffect.Parameters["Texture"].SetValue(Textures.tiles.getTexture());
                pass.Apply();
                
                GraphicsDevice.DrawPrimitives(PrimitiveType.TriangleList, 0, 6);
            }

            base.Draw(gameTime);
        }
    }
}
