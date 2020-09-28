using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System.IO;
using Inignoto.Utilities;
using System.Collections.Generic;

namespace Inignoto.Graphics.Textures
{
    public class TextureAtlas
    {
        private readonly int width, height;
        private readonly Texture2D texture;

        private readonly Dictionary<string, Rectangle> textures = new Dictionary<string, Rectangle>();
        private readonly List<Texture2D> texture_list = new List<Texture2D>();

        public TextureAtlas(ResourcePath path)
        {
            string[] files = FileUtils.getAllFiles(path);

            List<string> names = new List<string>();

            for (int i = 0; i < files.Length; i++)
            {
                FileStream stream = new FileStream(files[i], FileMode.Open);
                texture_list.Add(Texture2D.FromStream(Inignoto.game.GraphicsDevice, stream));
                stream.Close();
                width += texture_list[i].Width;
                if (height < texture_list[i].Height) height = texture_list[i].Height;
                

                names.Add(Path.GetFileName(files[i]).Split('.')[0]);
            }
            SpriteBatch batch = new SpriteBatch(Inignoto.game.GraphicsDevice);

            RenderTarget2D target;
            target = new RenderTarget2D(Inignoto.game.GraphicsDevice, width, height, false, SurfaceFormat.Color, DepthFormat.None);

            Inignoto.game.GraphicsDevice.SetRenderTarget(target);
            Inignoto.game.GraphicsDevice.Clear(new Color(0, 0, 0, 0));
            batch.Begin();
            
            int w = 0;
            for (int i = 0; i < texture_list.Count; i++)
            {
                batch.Draw(texture_list[i], new Rectangle(w, 0, texture_list[i].Width, texture_list[i].Height), Color.White);
                if (!this.textures.ContainsKey("Inignoto:" + names[i]))
                this.textures.Add("Inignoto:" + names[i], new Rectangle(w, 0, texture_list[i].Width, texture_list[i].Height));
                w += texture_list[i].Width;
                System.Console.WriteLine("loaded texture: " + "Inignoto:" + names[i]);
            }
            batch.End();
            batch.Dispose();
            texture = (Texture2D)target;
            Inignoto.game.GraphicsDevice.SetRenderTarget(null);
        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public Texture2D GetTexture()
        {
            return texture;
        }

        public void Dispose()
        {
            texture.Dispose();
            for (int i = 0; i < texture_list.Count; i++)
            {
                texture_list[i].Dispose();
            }
        }

        public Rectangle GetLocation(string texture)
        {
            Rectangle o = new Rectangle(0, 0, 0, 0);
            textures.TryGetValue(texture, out o);
            return o;
        }
    }
}
