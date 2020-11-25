﻿using Microsoft.Xna.Framework.Graphics;
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

namespace Inignoto.Graphics.World
{
    public class TileBuilder
    {
        public static readonly float offset = 0.0f;
        private static readonly Vector3f[] LEFT = new Vector3f[]
                {
                    new Vector3f(-offset, -offset, 1 + offset),
                    new Vector3f(-offset, 1 + offset, 1 + offset),
                    new Vector3f(-offset, 1 + offset, -offset),
                    new Vector3f(-offset, -offset, -offset)
                };
        private static readonly Vector3f[] FRONT =
            {
                new Vector3f(-offset, -offset, -offset),
                new Vector3f(-offset, 1 + offset, -offset),
                new Vector3f(1 + offset, 1 + offset, -offset),
                new Vector3f(1 + offset, -offset, -offset)
            };

        private static readonly Vector3f[] BACK =
           {
                new Vector3f(1 + offset, -offset, 1 + offset),
                new Vector3f(1 + offset, 1 + offset, 1 + offset),
                new Vector3f(-offset, 1 + offset, 1 + offset),
                new Vector3f(-offset, -offset, 1 + offset)
            };

        private static readonly Vector3f[] RIGHT = new Vector3f[]
                {
                    new Vector3f(1 + offset, -offset, -offset),
                    new Vector3f(1 + offset, 1 + offset, -offset),
                    new Vector3f(1 + offset, 1 + offset, 1 + offset),
                    new Vector3f(1 + offset, -offset, 1 + offset)
                };
        private static readonly Vector3f[] TOP = new Vector3f[]
                {
                    new Vector3f(-offset, 1 + offset, -offset),
                    new Vector3f(-offset, 1 + offset, 1 + offset),
                    new Vector3f(1 + offset, 1 + offset, 1 + offset),
                    new Vector3f(1 + offset, 1 + offset, -offset)
                };
        private static readonly Vector3f[] BOTTOM = new Vector3f[]
                {
                    new Vector3f(-offset, -offset, 1 + offset),
                    new Vector3f(-offset, -offset, -offset),
                    new Vector3f(1 + offset, -offset, -offset),
                    new Vector3f(1 + offset, -offset, 1 + offset)
                };
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
                        mesh.triangleVertices[i].Position += data.model.translation.Vector;
                        mesh.triangleVertices[i].Position += new Vector3(x + 0.65f, y + 0.5f, z + 0.5f);
                    }

                    mesh.vertexBuffer = new VertexBuffer(device, typeof(
                               VertexPositionLightTexture), mesh.triangleVertices.Length, BufferUsage.
                               WriteOnly);
                    if (mesh.vertexBuffer != null)
                        mesh.vertexBuffer.SetData(mesh.triangleVertices);
                    mesh.texture = Textures.Textures.tiles.GetTexture();
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

            index = BuildFace(x, y, z, data, overlay, TileFace.LEFT, vertices, colors, textures, indices, normals, index, lines);
            index = BuildFace(x, y, z, data, overlay, TileFace.RIGHT, vertices, colors, textures, indices, normals, index, lines);
            index = BuildFace(x, y, z, data, overlay, TileFace.FRONT, vertices, colors, textures, indices, normals, index, lines);
            index = BuildFace(x, y, z, data, overlay, TileFace.BACK, vertices, colors, textures, indices, normals, index, lines);
            index = BuildFace(x, y, z, data, overlay, TileFace.TOP, vertices, colors, textures, indices, normals, index, lines);
            index = BuildFace(x, y, z, data, overlay, TileFace.BOTTOM, vertices, colors, textures, indices, normals, index, lines);

            for (int i = 0; i < indices.Count; i++)
            {
                int ind = indices[i];
                vpct.Add(new VertexPositionLightTexture(vertices[ind], colors[ind], textures[ind], normals[ind]));
            }
            return new Mesh.Mesh(device, vpct.ToArray(), lines, Textures.Textures.tiles.GetTexture());
        }

        public static int BuildFace(float x, float y, float z, TileData data, TileData overlay, TileFace face, List<Vector3> vertices, List<Color> colors, List<Vector4> textures, List<int> indices, List<int> normals, int index, bool lines = false)
        {
            Vector3f[] verts = FRONT;
            TextureAtlas atlas = Textures.Textures.tiles;

            Vector2[] GetUV(TileData data)
            {
                string texture = data.GetTexture(face);

                Rectangle location = atlas.GetLocation(texture);
                if (data == TileManager.AIR.DefaultData || !TileManager.GetTile(data.tile_id).IsVisible())
                {
                    return new Vector2[]
                    {
                        new Vector2(-1, -1),
                        new Vector2(-1, -1),
                        new Vector2(-1, -1),
                        new Vector2(-1, -1)
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



            vertices.Add(verts[0].Vector + new Vector3(x, y, z));
            colors.Add(Color.White);
            vertices.Add(verts[1].Vector + new Vector3(x, y, z));
            colors.Add(Color.White);
            vertices.Add(verts[2].Vector + new Vector3(x, y, z));
            colors.Add(Color.White);
            vertices.Add(verts[3].Vector + new Vector3(x, y, z));
            colors.Add(Color.White);

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
    }
}
