using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System.IO;
using Inignoto.Utilities;

namespace Inignoto.Graphics.Textures
{
    class Textures
    {
        public static TextureAtlas tiles;

        public static void loadTextures()
        {
            tiles = new TextureAtlas(new ResourcePath("Inignoto", "textures/tiles", "assets"));
        }

        public static Texture2D loadTexture(ResourcePath path)
        {
            FileStream stream = FileUtils.getStreamForPath(path, FileMode.Open);
            Texture2D texture = Texture2D.FromStream(Inignoto.game.GraphicsDevice, stream);
            stream.Close();
            return texture;
        }
    }
}
