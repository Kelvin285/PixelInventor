using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static Microsoft.Xna.Framework.Graphics.SpriteFont;

namespace Inignoto.Graphics.Fonts
{
    public class FontManager
    {
        public static BitmapFont mandrill_regular;
        public static BitmapFont mandrill_bold;


        public static void LoadFonts()
        {
            mandrill_regular = LoadFont(Textures.Textures.font_mandrill_regular, 24, 48, new ResourcePath("Inignoto", "fonts/mandrill/mandrill_regular_48.font", "assets"));
            mandrill_bold = LoadFont(Textures.Textures.font_mandrill_bold, 24, 48, new ResourcePath("Inignoto", "fonts/mandrill/mandrill_bold_48.font", "assets"));

        }

        public static BitmapFont LoadFont(Texture2D texture, int width, int height, ResourcePath path)
        {
            return new BitmapFont(path, texture, width, height);
        }
    }

    public class FontPart : IComparable<FontPart> {
        public Rectangle glyphBounds;
        public char ch;
        public Vector2 offset;

        public int CompareTo(FontPart obj)
        {
            return ch.CompareTo(obj.ch);
        }
    }
   

    public class BitmapFont {
        
        public readonly int lineSpacing;
        public readonly float spacing;
        public readonly int width;
        public readonly int height;
        public readonly Texture2D texture;

        public readonly Dictionary<char, FontPart> parts = new Dictionary<char, FontPart>();

        public BitmapFont(ResourcePath path, Texture2D texture, int width, int height)
        {
            this.width = width;
            this.height = height;
            this.texture = texture;

            List<FontPart> parts = new List<FontPart>();

            lineSpacing = 1;
            spacing = 1;
            Dictionary<string, string> data = FileUtils.LoadFileAsDataList(path);
            foreach (string str in data.Keys) {
                string a = str;
                string b = data[a];
                if (a.Trim().Equals("code"))
                {
                    char.TryParse(b, out char c);
                    
                    FontPart part = parts.Last();
                    part.ch = c;
                    if (!this.parts.ContainsKey(c))
                    this.parts.Add(c, part);
                }
                if (a.Trim().Equals("rect"))
                {
                    string[] s = b.Split(' ');
                    int.TryParse(s[0], out int x);
                    int.TryParse(s[1], out int y);
                    int.TryParse(s[2], out int w);
                    int.TryParse(s[3], out int h);
                    FontPart part = parts.Last();
                    part.glyphBounds = new Rectangle(x, y, w, h);
                }
                if (a.Trim().Equals("offset"))
                {
                    string[] s = b.Split(' ');
                    int.TryParse(s[0], out int x);
                    int.TryParse(s[1], out int y);
                    parts.Add(new FontPart());

                    FontPart part = parts.Last();
                    part.offset = new Vector2(x, y);
                }
            }
        }
    }
}
