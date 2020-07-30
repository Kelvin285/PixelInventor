using Inignoto.Utilities;
using System.Collections.Generic;
using static Inignoto.Tiles.Tile;

namespace Inignoto.Tiles.Data
{
    class TileData
    {
        public readonly int tile_id;
        public readonly int state;
        public readonly ResourcePath location;
        public readonly int index;
        public readonly int num_x, num_y;

        public string texture = "";
        public string side_texture = "";
        public string top_texture = "";
        public string bottom_texture = "";
        public string left_texture = "";
        public string right_texture = "";
        public string front_texture = "";
        public string back_texture = "";

        public TileData(int tile, int state, ResourcePath location, int index)
        {
            this.tile_id = tile;
            this.state = state;
            this.location = location;
            this.index = index;
            Dictionary<string, string> data = FileUtils.LoadFileAsDataList(location);
            foreach (string a in data.Keys)
            {
                if (data.TryGetValue(a, out string b))
                {
                    if (a.Equals("num_x"))
                    {
                        int.TryParse(b, out num_x);
                        continue;
                    }
                    if (a.Equals("num_y"))
                    {
                        int.TryParse(b, out num_y);
                        continue;
                    }
                    if (a.Equals("top"))
                    {
                        if (texture.Equals("")) texture = b;
                        top_texture = b;
                    }
                    if (a.Equals("bottom"))
                    {
                        if (texture.Equals("")) texture = b;
                        bottom_texture = b;
                    }
                    if (a.Equals("left"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        left_texture = b;
                    }
                    if (a.Equals("right"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        right_texture = b;
                    }
                    if (a.Equals("front"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        front_texture = b;
                    }
                    if (a.Equals("back"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        back_texture = b;
                    }
                    if (a.Equals("texture"))
                    {
                        texture = b;
                    }
                    if (a.Equals("side"))
                    {
                        side_texture = b;
                    }
                }
            }
        }

        public string GetTexture(TileFace face)
        {
            switch (face)
            {
                case TileFace.TOP:
                    {
                        if (top_texture.Equals("")) return texture;
                        return top_texture;
                    }
                case TileFace.BOTTOM:
                    {
                        if (bottom_texture.Equals("")) return texture;
                        return bottom_texture;
                    }
                case TileFace.LEFT:
                    {
                        if (left_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return left_texture;
                    }
                case TileFace.RIGHT:
                    {
                        if (right_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return right_texture;
                    }
                case TileFace.FRONT:
                    {
                        if (front_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return front_texture;
                    }
                case TileFace.BACK:
                    {
                        if (back_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return back_texture;
                    }
            }
            return texture;
        }

    }
}
