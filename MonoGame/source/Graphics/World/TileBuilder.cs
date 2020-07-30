using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using System.Collections.Generic;
using Inignoto.Math;
using static Inignoto.Tiles.Tile;
using Inignoto.Tiles.Data;
using Inignoto.Graphics.Textures;

namespace Inignoto.Graphics.World
{
    class TileBuilder
    {
        private static readonly Vector3f[] LEFT = new Vector3f[]
                {
                    new Vector3f(0, 0, 0),
                    new Vector3f(0, 1, 0),
                    new Vector3f(0, 1, 1),
                    new Vector3f(0, 0, 1)
                };
        private static readonly Vector3f[] FRONT =
            {
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0),
                new Vector3f(1, 0, 0)
            };

        private static readonly Vector3f[] BACK =
           {
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 1),
                new Vector3f(1, 1, 1),
                new Vector3f(1, 0, 1)
            };

        private static readonly Vector3f[] RIGHT = new Vector3f[]
                {
                    new Vector3f(1, 0, 0),
                    new Vector3f(1, 1, 0),
                    new Vector3f(1, 1, 1),
                    new Vector3f(1, 0, 1)
                };
        private static readonly Vector3f[] TOP = new Vector3f[]
                {
                    new Vector3f(0, 1, 0),
                    new Vector3f(0, 1, 1),
                    new Vector3f(1, 1, 1),
                    new Vector3f(1, 1, 0)
                };
        private static readonly Vector3f[] BOTTOM = new Vector3f[]
                {
                    new Vector3f(0, 0, 0),
                    new Vector3f(0, 0, 1),
                    new Vector3f(1, 0, 1),
                    new Vector3f(1, 0, 0)
                };

        public static int BuildFace(int x, int y, int z, TileData data, TileFace face, List<Vector3> vertices, List<Color> colors, List<Vector2> textures, List<int> indices, int index)
        {
            Vector3f[] verts = FRONT;

            int x_num = data.num_x;
            int y_num = data.num_y;

            TextureAtlas atlas = Textures.Textures.tiles;
            string texture = data.GetTexture(face);

            Rectangle location = atlas.GetLocation(texture);

            int u2 = 0;
            int v2 = 0;

            if (face == TileFace.FRONT || face == TileFace.BACK || face == TileFace.TOP || face == TileFace.BOTTOM)
            {
                u2 = x % data.num_x;
            }
            else u2 = z % data.num_x;

            if (face == TileFace.FRONT || face == TileFace.BACK || face == TileFace.LEFT || face == TileFace.RIGHT)
            {
                v2 = y % data.num_y;
            }
            else v2 = z % data.num_y;

            float u = (location.X + u2 * atlas.GetWidth()) / (float)atlas.GetWidth();
            float v = (location.Y + v2 * atlas.GetHeight()) / (float)atlas.GetHeight();
            float w = location.Width / (float)atlas.GetWidth();
            float h = location.Height / (float)atlas.GetHeight();

            w /= data.num_x;
            h /= data.num_y;

            textures.Add(new Vector2(u, v + h));
            textures.Add(new Vector2(u, v));
            textures.Add(new Vector2(u + w, v));
            textures.Add(new Vector2(u + w, v + h));


            Quaternionf rotation = new Quaternionf();
            if (face == TileFace.LEFT)
            {
                verts = LEFT;
            }
            if (face == TileFace.RIGHT)
            {
                verts = RIGHT;
            }
            if (face == TileFace.TOP)
            {
                verts = TOP;
            }
            if (face == TileFace.BOTTOM)
            {
                verts = BOTTOM;
            }
            if (face == TileFace.BACK)
            {
                verts = BACK;
            }



            for (int i = 0; i < verts.Length; i++)
            {
                vertices.Add(verts[i].Vector + new Vector3(x, y, z));
                colors.Add(Color.White);
            }

            int[] inds = { 0, 1, 2, 2, 3, 0 };
            
            for (int i = 0; i < inds.Length; i++)
            {
                indices.Add(inds[i] + index);
                
            }
            index+=4;
            return index;
        }
    }
}
