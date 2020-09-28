using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using System.Collections.Generic;
using Inignoto.Math;
using static Inignoto.Tiles.Tile;
using Inignoto.Tiles.Data;
using Inignoto.Graphics.Textures;

namespace Inignoto.Graphics.World
{
    public class TileBuilder
    {
        private static readonly Vector3f[] LEFT = new Vector3f[]
                {
                    new Vector3f(0, 0, 1),
                    new Vector3f(0, 1, 1),
                    new Vector3f(0, 1, 0),
                    new Vector3f(0, 0, 0)
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
                new Vector3f(1, 0, 1),
                new Vector3f(1, 1, 1),
                new Vector3f(0, 1, 1),
                new Vector3f(0, 0, 1)
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
                    new Vector3f(0, 0, 1),
                    new Vector3f(0, 0, 0),
                    new Vector3f(1, 0, 0),
                    new Vector3f(1, 0, 1)
                };

        public static Mesh.Mesh BuildTile(float x, float y, float z, TileData data, GraphicsDevice device)
        {
            List<VertexPositionColorTexture> vpct = new List<VertexPositionColorTexture>();

            List<Vector3> vertices = new List<Vector3>();
            List<Color> colors = new List<Color>();
            List<Vector2> textures = new List<Vector2>();
            List<int> indices = new List<int>();
            int index = 0;

            index = BuildFace(x, y, z, data, TileFace.LEFT, vertices, colors, textures, indices, index);
            index = BuildFace(x, y, z, data, TileFace.RIGHT, vertices, colors, textures, indices, index);
            index = BuildFace(x, y, z, data, TileFace.FRONT, vertices, colors, textures, indices, index);
            index = BuildFace(x, y, z, data, TileFace.BACK, vertices, colors, textures, indices, index);
            index = BuildFace(x, y, z, data, TileFace.TOP, vertices, colors, textures, indices, index);
            index = BuildFace(x, y, z, data, TileFace.BOTTOM, vertices, colors, textures, indices, index);

            for (int i = 0; i < indices.Count; i++)
            {
                int ind = indices[i];
                vpct.Add(new VertexPositionColorTexture(vertices[ind], colors[ind], textures[ind]));
            }
            return new Mesh.Mesh(device, vpct.ToArray(), false, Textures.Textures.tiles.GetTexture());
        }

        public static int BuildFace(float x, float y, float z, TileData data, TileFace face, List<Vector3> vertices, List<Color> colors, List<Vector2> textures, List<int> indices, int index)
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
                u2 = (int)x % data.num_x;
            }
            else u2 = (int)z % data.num_x;

            if (face == TileFace.FRONT || face == TileFace.BACK || face == TileFace.LEFT || face == TileFace.RIGHT)
            {
                v2 = (int)y % data.num_y;
            }
            else v2 = (int)z % data.num_y;

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
