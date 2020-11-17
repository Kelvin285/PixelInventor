using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System.IO;
using Inignoto.Utilities;
using System.Collections.Generic;
using Inignoto.Graphics.Fonts;
using Inignoto.Tiles.Data;

namespace Inignoto.Graphics.Textures
{
    public class Textures
    {
        public static TextureAtlas tiles;
        public static Texture2D fp_cursor;
        public static Texture2D hud;
        public static Texture2D viginette;
        public static Texture2D white_square;
        public static Texture2D inventory_row;
        public static Texture2D inventory;
        public static Texture2D font_mandrill_regular;
        public static Texture2D font_mandrill_bold;
        public static Texture2D item_browser;

        public static Texture2D[] title_animation;
        public static Texture2D inignoto_logo;
        public static Texture2D title_moon;
        public static Texture2D moon_clip;

        public static Dictionary<TileData, Texture2D> TILE_ITEMS = new Dictionary<TileData, Texture2D>();

        private static List<Texture> textures = new List<Texture>();

        public static void LoadTextures()
        {
            tiles = new TextureAtlas(new ResourcePath("Inignoto", "textures/tiles", "assets"));
            fp_cursor = LoadTexture(new ResourcePath("Inignoto", "textures/gui/fp_cursor.png", "assets"));
            hud = LoadTexture(new ResourcePath("Inignoto", "textures/gui/hud.png", "assets"));
            viginette = LoadTexture(new ResourcePath("Inignoto", "textures/gui/viginette.png", "assets"));
            white_square = LoadTexture(new ResourcePath("Inignoto", "textures/gui/white_square.png", "assets"));
            inventory_row = LoadTexture(new ResourcePath("Inignoto", "textures/gui/inventory_row.png", "assets"));
            inventory = LoadTexture(new ResourcePath("Inignoto", "textures/gui/inventory.png", "assets"));
            font_mandrill_regular = LoadTexture(new ResourcePath("Inignoto", "fonts/mandrill/mandrill_regular_48.png", "assets"));
            font_mandrill_bold = LoadTexture(new ResourcePath("Inignoto", "fonts/mandrill/mandrill_bold_48.png", "assets"));
            item_browser = LoadTexture(new ResourcePath("Inignoto", "textures/gui/item_browser.png", "assets"));

            title_animation = new Texture2D[4];
            for (int i = 0; i < 4; i++)
            {
                title_animation[i] = LoadTexture(new ResourcePath("Inignoto", "textures/gui/title_background/sprite_" + i + ".png", "assets"));
            }

            inignoto_logo = LoadTexture(new ResourcePath("Inignoto", "textures/gui/title_background/inignoto.png", "assets"));
            title_moon = LoadTexture(new ResourcePath("Inignoto", "textures/gui/title_background/moon.png", "assets"));
            moon_clip = LoadTexture(new ResourcePath("Inignoto", "textures/gui/title_background/moon_clip.png", "assets"));


            FontManager.LoadFonts();
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
            foreach (Texture tex in TILE_ITEMS.Values)
            {
                tex.Dispose();
            }
        }
    }
}
