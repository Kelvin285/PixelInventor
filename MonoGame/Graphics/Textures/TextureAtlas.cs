using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System.IO;
using Inignoto.Utilities;
using System.Collections.Generic;

namespace Inignoto.Graphics.Textures
{
    class TextureAtlas
    {
        private int width, height;
        private Texture2D texture;

        private Dictionary<string, Rectangle> textures = new Dictionary<string, Rectangle>();
        private List<Texture2D> texture_list = new List<Texture2D>();

        public TextureAtlas(ResourcePath path)
        {
            string[] files = FileUtils.getAllFiles(path);

            for (int i = 0; i < files.Length; i++)
            {
                FileStream stream = new FileStream(files[i], FileMode.Open);
                texture_list.Add(Texture2D.FromStream(Inignoto.game.GraphicsDevice, stream));
                stream.Close();
                width += texture_list[i].Width;
                if (height < texture_list[i].Height) height = texture_list[i].Height;
            }
            SpriteBatch batch = new SpriteBatch(Inignoto.game.GraphicsDevice);

            RenderTarget2D target;
            target = new RenderTarget2D(Inignoto.game.GraphicsDevice, width, height, false, SurfaceFormat.Color, DepthFormat.None);

            Inignoto.game.GraphicsDevice.SetRenderTarget(target);
            Inignoto.game.GraphicsDevice.Clear(Color.White);
            batch.Begin();
            
            int w = 0;
            for (int i = 0; i < texture_list.Count; i++)
            {
                batch.Draw(texture_list[i], new Rectangle(w, 0, texture_list[i].Width, texture_list[i].Height), Color.White);
                if (!this.textures.ContainsKey("Inignoto:" + texture_list[i].Name))
                this.textures.Add("Inignoto:" + texture_list[i].Name, new Rectangle(w, 0, texture_list[i].Width, texture_list[i].Height));
                w += texture_list[i].Width;
            }
            batch.End();
            batch.Dispose();
            texture = (Texture2D)target;
            Inignoto.game.GraphicsDevice.SetRenderTarget(null);
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }

        public Texture2D getTexture()
        {
            return texture;
        }

        public void dispose()
        {
            texture.Dispose();
            for (int i = 0; i < texture_list.Count; i++)
            {
                texture_list[i].Dispose();
            }
        }

        public Rectangle getLocation(string texture)
        {
            Rectangle o = new Rectangle(0, 0, 0, 0);
            textures.TryGetValue(texture, out o);
            return o;
        }
    }
}
