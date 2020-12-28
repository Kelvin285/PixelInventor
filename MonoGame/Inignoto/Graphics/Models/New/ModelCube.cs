using Inignoto.Effects;
using Inignoto.Graphics.Mesh;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Graphics.Models.New
{
    public class ModelCube : ModelObject
    {
        public Vector3[] cube_vertices =
        {
            // Front face
            new Vector3(-0.5f, -0.5f, -0.5f),
            new Vector3(-0.5f, 0.5f, -0.5f),
            new Vector3(0.5f, 0.5f, -0.5f),
            new Vector3(0.5f, -0.5f, -0.5f),
            // Back face
            new Vector3(-0.5f, -0.5f, 0.5f),
            new Vector3(-0.5f, 0.5f, 0.5f),
            new Vector3(0.5f, 0.5f, 0.5f),
            new Vector3(0.5f, -0.5f, 0.5f),
            // Left face
            new Vector3(-0.5f, -0.5f, 0.5f),
            new Vector3(-0.5f, 0.5f, 0.5f),
            new Vector3(-0.5f, 0.5f, -0.5f),
            new Vector3(-0.5f, -0.5f, -0.5f),
            // Right face
            new Vector3(0.5f, -0.5f, 0.5f),
            new Vector3(0.5f, 0.5f, 0.5f),
            new Vector3(0.5f, 0.5f, -0.5f),
            new Vector3(0.5f, -0.5f, -0.5f),
            // Top face
            new Vector3(-0.5f, 0.5f, -0.5f),
            new Vector3(-0.5f, 0.5f, 0.5f),
            new Vector3(0.5f, 0.5f, 0.5f),
            new Vector3(0.5f, 0.5f, -0.5f),
            // Bottom face
            new Vector3(-0.5f, -0.5f, -0.5f),
            new Vector3(-0.5f, -0.5f, 0.5f),
            new Vector3(0.5f, -0.5f, 0.5f),
            new Vector3(0.5f, -0.5f, -0.5f)
        };

        Vector4[] uv =
        {
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
        };

        public Mesh.Mesh mesh;

        public ModelCube() : base()
        {
            translation = new Vector3(0, 1, 0);
            List<VertexPositionLightTexture> triangleVertices = new List<VertexPositionLightTexture>();
            List<int> indices = new List<int>();
            int I = 0;
            for (int i = 0; i < 6; i++)
            {

                for (int j = 0; j < 4; j++)
                {
                    triangleVertices.Add(new VertexPositionLightTexture(cube_vertices[i * 4 + j], Color.White, GetUvCoords(j, i), (int)Tiles.Tile.TileFace.FRONT));
                }
                indices.Add(i * 4);
                indices.Add(i * 4 + 1);
                indices.Add(i * 4 + 2);
                indices.Add(i * 4 + 2);
                indices.Add(i * 4 + 3);
                indices.Add(i * 4 + 0);
            }

            mesh = new Mesh.Mesh(Inignoto.game.GraphicsDevice, triangleVertices.ToArray());
            mesh.SetIndexBuffer(Inignoto.game.GraphicsDevice, indices.ToArray());
            mesh.texture = Textures.Textures.white_square;
        }

        public void ChangeUV(Vector4 newUV, int face)
        {
            uv[face] = newUV;
            lock (mesh.triangleVertices)
            {
                for (int i = 0; i < 4; i++)
                {
                    Vector4 coords = GetUvCoords(i, face);

                    mesh.triangleVertices[face * 4 + i].TextureCoordinate = new Vector2(coords.X, coords.Y);
                }
            }
        }

        public void ChangeTexture(Texture2D texture)
        {
            mesh.texture = texture;
        }

        public Vector4 GetUvCoords(int index, int face)
        {
            switch (index)
            {
                case 1:
                    {
                        return new Vector4(uv[face].X, uv[face].W, -1, -1);
                    }
                case 2:
                    {
                        return new Vector4(uv[face].Z, uv[face].W, -1, -1);
                    }
                case 3:
                    {
                        return new Vector4(uv[face].Z, uv[face].Y, -1, -1);
                    }
            }

            return new Vector4(uv[face].X, uv[face].Y, -1, -1);
        }
        public override void Render(GraphicsDevice device, GameEffect effect, Matrix lastMatrix)
        {
            base.Render(device, effect, lastMatrix);

            
            if (mesh.GetPosition() != translation) mesh.SetPosition(translation + lastMatrix.Translation);
            if (mesh.rotation != rotation) mesh.SetRotation(rotation);
            if (mesh.scale != scale) mesh.SetScale(scale);
            mesh.Draw(effect, device);
        }
    }
}
