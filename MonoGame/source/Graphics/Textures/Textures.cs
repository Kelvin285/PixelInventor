using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System.IO;
using Inignoto.Utilities;
using System.Collections.Generic;

namespace Inignoto.Graphics.Textures
{
    public class Textures
    {
        public static TextureAtlas tiles;
        public static Texture2D fp_cursor;
        public static Texture2D hud;
        public static Texture2D viginette;

        private static List<Texture> textures = new List<Texture>();

        public static void LoadTextures()
        {
            tiles = new TextureAtlas(new ResourcePath("Inignoto", "textures/tiles", "assets"));
            fp_cursor = LoadTexture(new ResourcePath("Inignoto", "textures/gui/fp_cursor.png", "assets"));
            hud = LoadTexture(new ResourcePath("Inignoto", "textures/gui/hud.png", "assets"));
            viginette = LoadTexture(new ResourcePath("Inignoto", "textures/gui/viginette.png", "assets"));

        }

        public static Texture2D LoadTexture(ResourcePath path)
        {
            FileStream stream = FileUtils.GetStreamForPath(path, FileMode.Open);
            Texture2D texture = Texture2D.FromStream(Inignoto.game.GraphicsDevice, stream);
            stream.Close();
            textures.Add(texture);
            return texture;
        }

        public static void Dispose()
        {
            foreach (Texture tex in textures)
            {
                tex.Dispose();
            }
        }
    }
}
