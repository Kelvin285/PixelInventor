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

        public Vector4[] uv =
        {
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
            new Vector4(0, 0, 1, 1),
        };

        public Mesh.Mesh mesh;

        public ModelCube(Vector3 translation) : base()
        {
            this.translation = translation;
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
                    mesh.built = false;
                }
            }
        }

        public void ChangeTexture(Texture2D texture)
        {
            mesh.texture = texture;
        }

        public float GetTexWidth()
        {
            float uvw = 1.0f;
            if (mesh != null)
            {
                if (mesh.texture == null) mesh.texture = Textures.Textures.white_square;
                uvw = mesh.texture.Width;
            }
            return uvw;
         }

        public float GetTexHeight()
        {
            float uvh = 1.0f;
            if (mesh != null)
            {
                if (mesh.texture == null) mesh.texture = Textures.Textures.white_square;
                uvh = mesh.texture.Height;
            }
            return uvh;
        }

        public Vector4 GetUvCoords(int index, int face)
        {
            float uvw = 1.0f;
            float uvh = 1.0f;
            if (mesh != null)
            {
                if (mesh.texture == null) mesh.texture = Textures.Textures.white_square;
                uvw = mesh.texture.Width;
                uvh = mesh.texture.Height;
            }
            //front, back, left, right, top, bottom
            float x = uv[face].X;
            float y = uv[face].Y;

            float w = uv[face].Z;
            float h = uv[face].W;

            x *= 1.0f / uvw;
            y *= 1.0f / uvh;
            w *= 1.0f / uvw;
            h *= 1.0f / uvh;
            x *= 32;
            y *= 32;

            if (face == 0 || face == 1)
            {
                w *= 32 * scale.X;
                h *= 32 * scale.Y;
            }
            else if (face == 2 || face == 3)
            {
                w *= 32 * scale.Z;
                h *= 32 * scale.Y;
            }
            else if (face == 4 || face == 5)
            {
                w *= 32 * scale.X;
                h *= 32 * scale.Z;
            }

            switch (index)
            {
                case 1:
                    {
                        return new Vector4(x, y + h, -1, -1);
                    }
                case 2:
                    {
                        return new Vector4(x + w, y + h, -1, -1);
                    }
                case 3:
                    {
                        return new Vector4(x + w, y, -1, -1);
                    }
            }

            return new Vector4(x, y, -1, -1);
        }
        public override void Render(GraphicsDevice device, GameEffect effect, Matrix lastMatrix, Quaternion lastRotation, ModelObject select)
        {
            Vector3 newTranslation = (Matrix.CreateTranslation(translation) * Matrix.CreateFromQuaternion(lastRotation)).Translation;
            base.Render(device, effect, lastMatrix * Matrix.CreateTranslation(newTranslation), lastRotation * rotation, select);

            mesh.SetPosition(newTranslation + lastMatrix.Translation);

            mesh.SetRotation(lastRotation * rotation);

            renderTranslation = newTranslation + lastMatrix.Translation;
            renderRotation = lastRotation * rotation;

            mesh.SetScale(scale);

            if (selected)
            {
                effect.ObjectColor = new Vector4(1, 0.8f, 0.8f, 1);
            }

            mesh.Draw(effect, device);

            effect.ObjectColor = new Vector4(1, 1, 1, 1);
        }
    }
}
