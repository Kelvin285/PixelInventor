using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using System.Collections.Generic;
using Inignoto.Math;
using static Inignoto.Tiles.Tile;
using Inignoto.Tiles.Data;
using Inignoto.Graphics.Textures;
using Inignoto.Graphics.Mesh;
using System;
using System.Security.Cryptography.X509Certificates;
using Inignoto.Tiles;
using Inignoto.Utilities.Pools;
using Inignoto.Utilities;

namespace Inignoto.Graphics.World
{
    public class TileBuilder
    {
        private static Vector2 AIR = new Vector2(-1, -1);
        public static readonly float offset = 0.0f;
        private static readonly Vector3[] LEFT = new Vector3[]
                {
                    new Vector3(-offset, -offset, 1 + offset),
                    new Vector3(-offset, 1 + offset, 1 + offset),
                    new Vector3(-offset, 1 + offset, -offset),
                    new Vector3(-offset, -offset, -offset)
                };
        private static readonly Vector3[] FRONT =
            {
                new Vector3(-offset, -offset, -offset),
                new Vector3(-offset, 1 + offset, -offset),
                new Vector3(1 + offset, 1 + offset, -offset),
                new Vector3(1 + offset, -offset, -offset)
            };

        private static readonly Vector3[] BACK =
           {
                new Vector3(1 + offset, -offset, 1 + offset),
                new Vector3(1 + offset, 1 + offset, 1 + offset),
                new Vector3(-offset, 1 + offset, 1 + offset),
                new Vector3(-offset, -offset, 1 + offset)
            };

        private static readonly Vector3[] RIGHT = new Vector3[]
                {
                    new Vector3(1 + offset, -offset, -offset),
                    new Vector3(1 + offset, 1 + offset, -offset),
                    new Vector3(1 + offset, 1 + offset, 1 + offset),
                    new Vector3(1 + offset, -offset, 1 + offset)
                };
        private static readonly Vector3[] TOP = new Vector3[]
                {
                    new Vector3(-offset, 1 + offset, -offset),
                    new Vector3(-offset, 1 + offset, 1 + offset),
                    new Vector3(1 + offset, 1 + offset, 1 + offset),
                    new Vector3(1 + offset, 1 + offset, -offset)
                };
        private static readonly Vector3[] BOTTOM = new Vector3[]
                {
                    new Vector3(-offset, -offset, 1 + offset),
                    new Vector3(-offset, -offset, -offset),
                    new Vector3(1 + offset, -offset, -offset),
                    new Vector3(1 + offset, -offset, 1 + offset)
                };

        private static readonly List<List<Vector3>>[] VoxelFaces = new List<List<Vector3>>[Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];

        public static void BuildFirst()
        {
            for (int x = 0; x < Constants.CHUNK_SIZE; x++)
            {
                for (int y = 0; y < Constants.CHUNK_SIZE; y++)
                {
                    for (int z = 0; z < Constants.CHUNK_SIZE; z++)
                    {
                        Vector3 pos = new Vector3(x, y, z);
                        VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE] = 
                            new List<List<Vector3>>();

                        VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE].Add(new List<Vector3>());
                        for (int i = 0; i < 4; i++)
                        {
                            VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][0].Add(TOP[i] + pos);
                        }
                        VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE].Add(new List<Vector3>());
                        for (int i = 0; i < 4; i++)
                        {
                            VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][1].Add(BOTTOM[i] + pos);
                        }
                        VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE].Add(new List<Vector3>());
                        for (int i = 0; i < 4; i++)
                        {
                            VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][2].Add(LEFT[i] + pos);
                        }
                        VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE].Add(new List<Vector3>());
                        for (int i = 0; i < 4; i++)
                        {
                            VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][3].Add(RIGHT[i] + pos);
                        }
                        VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE].Add(new List<Vector3>());
                        for (int i = 0; i < 4; i++)
                        {
                            VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][4].Add(FRONT[i] + pos);
                        }
                        VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE].Add(new List<Vector3>());
                        for (int i = 0; i < 4; i++)
                        {
                            VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][5].Add(BACK[i] + pos);
                        }
                    }
                }
            }
        }

        public static Mesh.Mesh BuildTile(float x, float y, float z, TileData data, TileData overlay, GraphicsDevice device, bool lines = false)
        {
            if (data.model != null)
            {
                Mesh.Mesh mesh = null;
                if (!data.model.Combined)
                {
                    data.model.Combine();

                    mesh = data.model.Parts[0].mesh;
                    for (int i = 0; i < mesh.triangleVertices.Length; i++)
                    {
                        Vector2 texCoord = mesh.triangleVertices[i].TextureCoordinate;

                        Rectangle location = Textures.Textures.tiles.GetLocation(data.texture);


                        texCoord.X = (texCoord.X * location.Width + location.X) / Textures.Textures.tiles.GetWidth();
                        texCoord.Y = (texCoord.Y * location.Height + location.Y) / Textures.Textures.tiles.GetHeight();

                        Quaternion quat = Quaternion.CreateFromYawPitchRoll(MathF.PI * data.model.rotation.Y / 180.0f,
                            MathF.PI * data.model.rotation.X / 180.0f,
                            MathF.PI * data.model.rotation.Z / 180.0f);

                        Matrix matrix = Matrix.CreateTranslation(mesh.triangleVertices[i].Position) * Matrix.CreateFromQuaternion(quat);

                        mesh.triangleVertices[i].Position = matrix.Translation;
                        mesh.triangleVertices[i].TextureCoordinate = texCoord;
                        mesh.triangleVertices[i].Position *= 1.0f / 32.0f;
                        mesh.triangleVertices[i].Position += data.model.translation;
                        mesh.triangleVertices[i].Position += new Vector3(x + 0.65f, y + 0.5f, z + 0.5f);
                    }

                    if (Textures.Textures.tiles.GetTexture() != null)
                    mesh.texture = Textures.Textures.tiles.GetTexture();
                    mesh.built = false;
                } else
                {
                    mesh = data.model.Parts[0].mesh;
                }
                 
                if (mesh != null)
                {
                    return mesh;
                }
                
            }

            List<VertexPositionLightTexture> vpct = new List<VertexPositionLightTexture>();

            List<Vector3> vertices = new List<Vector3>();
            List<Color> colors = new List<Color>();
            List<Vector4> textures = new List<Vector4>();
            List<int> indices = new List<int>();
            List<int> normals = new List<int>();
            int index = 0;

            index = BuildSingleTileFace(x, y, z, data, overlay, TileFace.LEFT, vertices, colors, textures, indices, normals, index, lines);
            index = BuildSingleTileFace(x, y, z, data, overlay, TileFace.RIGHT, vertices, colors, textures, indices, normals, index, lines);
            index = BuildSingleTileFace(x, y, z, data, overlay, TileFace.FRONT, vertices, colors, textures, indices, normals, index, lines);
            index = BuildSingleTileFace(x, y, z, data, overlay, TileFace.BACK, vertices, colors, textures, indices, normals, index, lines);
            index = BuildSingleTileFace(x, y, z, data, overlay, TileFace.TOP, vertices, colors, textures, indices, normals, index, lines);
            index = BuildSingleTileFace(x, y, z, data, overlay, TileFace.BOTTOM, vertices, colors, textures, indices, normals, index, lines);

            for (int i = 0; i < indices.Count; i++)
            {
                int ind = indices[i];
                vpct.Add(new VertexPositionLightTexture(vertices[ind], colors[ind], textures[ind], normals[ind]));
            }
            return new Mesh.Mesh(device, vpct.ToArray(), lines, Textures.Textures.tiles.GetTexture());
        }

        public static int BuildSingleTileFace(float x, float y, float z, TileData data, TileData overlay, TileFace face, List<Vector3> vertices, List<Color> colors, List<Vector4> textures, List<int> indices, List<int> normals, int index, bool lines = false)
        {
            Vector3[] verts = FRONT;
            TextureAtlas atlas = Textures.Textures.tiles;

            Vector2[] GetUV(TileData data)
            {
                string texture = data.GetTexture(face);

                Rectangle location = atlas.GetLocation(texture);
                if (data == TileRegistry.AIR.DefaultData || !TileRegistry.GetTile(data.tile_id).IsVisible())
                {
                    return new Vector2[]
                    {
                        AIR, AIR, AIR, AIR
                    };
                }
                int u2;
                int v2;

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

                float u = (location.X + u2 * location.Width / data.num_x) / (float)atlas.GetWidth();
                float v = (location.Y + v2 * location.Height / data.num_y) / (float)atlas.GetHeight();
                float w = location.Width / (float)atlas.GetWidth();
                float h = location.Height / (float)atlas.GetHeight();

                w /= data.num_x;
                h /= data.num_y;
                return new Vector2[]{
                    new Vector2(u, v + h),
                    new Vector2(u, v),
                    new Vector2(u + w, v),
                    new Vector2(u + w, v + h)
                };
            }

            Vector4 MergeVec2(Vector2 a, Vector2 b)
            {
                return new Vector4(a.X, a.Y, b.X, b.Y);
            }

            Vector2[] UV1 = GetUV(data);
            
            Vector2[] UV2 = GetUV(overlay);


            textures.Add(MergeVec2(UV1[0], UV2[0]));
            textures.Add(MergeVec2(UV1[1], UV2[1]));
            textures.Add(MergeVec2(UV1[2], UV2[2]));
            textures.Add(MergeVec2(UV1[3], UV2[3]));

            normals.Add((int)face);
            normals.Add((int)face);
            normals.Add((int)face);
            normals.Add((int)face);

            if (face == TileFace.LEFT)
            {
                verts = LEFT;
            }
            else
            if (face == TileFace.RIGHT)
            {
                verts = RIGHT;
            }
            else
            if (face == TileFace.TOP)
            {
                verts = TOP;
            }
            if (face == TileFace.BOTTOM)
            {
                verts = BOTTOM;
            }
            else
            if (face == TileFace.BACK)
            {
                verts = BACK;
            }

            Vector3 xyz = new Vector3(x, y, z);

            for (int i = 0; i < 4; i++)
            {
                vertices.Add(new Vector3(verts[i].X + xyz.X, verts[i].Y + xyz.Y, verts[i].Z + xyz.Z));
                colors.Add(Color.White);
            }

            if (!lines)
            {
                indices.Add(0 + index);
                indices.Add(1 + index);
                indices.Add(2 + index);
                indices.Add(2 + index);
                indices.Add(3 + index);
                indices.Add(0 + index);
            } else
            {
                indices.Add(0 + index);
                indices.Add(1 + index);
                indices.Add(1 + index);
                indices.Add(2 + index);
                indices.Add(2 + index);
                indices.Add(3 + index);
                indices.Add(3 + index);
                indices.Add(0 + index);
            }

            index += 4;
            return index;
        }

        public static int BuildFace(int x, int y, int z, TileData data, TileData overlay, TileFace face, List<Vector3> vertices, List<Color> colors, List<Vector4> textures, List<int> indices, List<int> normals, int index, bool lines = false)
        {
            TextureAtlas atlas = Textures.Textures.tiles;

            Vector2[] GetUV(TileData data)
            {
                string texture = data.GetTexture(face);

                Rectangle location = atlas.GetLocation(texture);
                if (data == TileRegistry.AIR.DefaultData || !TileRegistry.GetTile(data.tile_id).IsVisible())
                {
                    return new Vector2[]
                    {
                        AIR, AIR, AIR, AIR
                    };
                }
                int u2;
                int v2;

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

                float u = (location.X + u2 * location.Width / data.num_x) / (float)atlas.GetWidth();
                float v = (location.Y + v2 * location.Height / data.num_y) / (float)atlas.GetHeight();
                float w = location.Width / (float)atlas.GetWidth();
                float h = location.Height / (float)atlas.GetHeight();

                w /= data.num_x;
                h /= data.num_y;
                return new Vector2[]{
                    new Vector2(u, v + h),
                    new Vector2(u, v),
                    new Vector2(u + w, v),
                    new Vector2(u + w, v + h)
                };
            }

            Vector4 MergeVec2(Vector2 a, Vector2 b)
            {
                return new Vector4(a.X, a.Y, b.X, b.Y);
            }

            Vector2[] UV1 = GetUV(data);

            Vector2[] UV2 = GetUV(overlay);


            textures.Add(MergeVec2(UV1[0], UV2[0]));
            textures.Add(MergeVec2(UV1[1], UV2[1]));
            textures.Add(MergeVec2(UV1[2], UV2[2]));
            textures.Add(MergeVec2(UV1[3], UV2[3]));

            normals.Add((int)face);
            normals.Add((int)face);
            normals.Add((int)face);
            normals.Add((int)face);

            List<Vector3> verts = VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][(int)TileFace.FRONT];
            if (face == TileFace.LEFT)
            {
                verts = VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][(int)TileFace.LEFT];
            }
            else
            if (face == TileFace.RIGHT)
            {
                verts = VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][(int)TileFace.RIGHT];
            }
            else
            if (face == TileFace.TOP)
            {
                verts = VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][(int)TileFace.TOP];
            }
            if (face == TileFace.BOTTOM)
            {
                verts = VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][(int)TileFace.BOTTOM];
            }
            else
            if (face == TileFace.BACK)
            {
                verts = VoxelFaces[x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE][(int)TileFace.BACK];
            }

            for (int i = 0; i < 4; i++)
            {
                vertices.Add(verts[i]);
                colors.Add(Color.White);
            }

            if (!lines)
            {
                indices.Add(0 + index);
                indices.Add(1 + index);
                indices.Add(2 + index);
                indices.Add(2 + index);
                indices.Add(3 + index);
                indices.Add(0 + index);
            }
            else
            {
                indices.Add(0 + index);
                indices.Add(1 + index);
                indices.Add(1 + index);
                indices.Add(2 + index);
                indices.Add(2 + index);
                indices.Add(3 + index);
                indices.Add(3 + index);
                indices.Add(0 + index);
            }

            index += 4;
            return index;
        }
    }
}
