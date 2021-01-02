using Inignoto.Effects;
using Inignoto.Graphics.Mesh;
using Inignoto.Math;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.Math.Raytracing;

namespace Inignoto.Graphics.Models.New
{
    public class ModelPlane : ModelObject
    {
        protected static Vector3[] plane_vertices = {
            new Vector3(-0.5f, -0.5f, 0),
            new Vector3(-0.5f, 0.5f, 0),
            new Vector3(0.5f, 0.5f, 0),
            new Vector3(0.5f, -0.5f, 0)
        };

        public Mesh.Mesh mesh;

        public Vector4 uv = new Vector4(0, 0, 1, 1);

        public ModelPlane(Vector3 translation) : base()
        {
            this.translation = translation;
            VertexPositionLightTexture[] triangleVertices = new VertexPositionLightTexture[4];


            for (int i = 0; i < 4; i++)
            {
                triangleVertices[i] = new VertexPositionLightTexture(plane_vertices[i], Color.White, GetUvCoords(i), (int)Tiles.Tile.TileFace.FRONT);
            }
            mesh = new Mesh.Mesh(Inignoto.game.GraphicsDevice, triangleVertices);
            mesh.SetIndexBuffer(Inignoto.game.GraphicsDevice, new int[] { 0, 1, 2, 2, 3, 0 });
            mesh.texture = Textures.Textures.white_square;
        }

        public void ChangeUV(Vector4 newUV)
        {
            uv = newUV;
            lock (mesh.triangleVertices)
            {
                for (int i = 0; i < 4; i++)
                {
                    Vector4 coords = GetUvCoords(i);

                    mesh.triangleVertices[i].TextureCoordinate = new Vector2(coords.X, coords.Y);
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

        public Vector4 GetUvCoords(int index)
        {
            float uvw = 32.0f;
            float uvh = 32.0f;
            if (mesh != null)
            {
                if (mesh.texture == null) mesh.texture = Textures.Textures.white_square;
                uvw = mesh.texture.Width;
                uvh = mesh.texture.Height;
            }
            //front, back, left, right, top, bottom
            float x = uv.X;
            float y = uv.Y;

            float w = uv.Z;
            float h = uv.W;

            x *= 1.0f / uvw;
            y *= 1.0f / uvh;
            w *= 1.0f / uvw;
            h *= 1.0f / uvh;
            x *= 32;
            y *= 32;

            w *= 32 * scale.X;
            h *= 32 * scale.Y;

            switch (index) {
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

        public override RayIntersection GetIntersection(Vector3 position, Vector3 direction)
        {

            RayBox box = new RayBox();
            box.Min = renderTranslation - scale / 2.0f;
            box.Max = box.Min + scale;
            box.Min.Z = renderTranslation.Z - 0.01f;
            box.Max.Z = renderTranslation.Z + 0.01f;

            box.Min -= renderTranslation;
            box.Max -= renderTranslation;
            box.Min = Raytracing.RotateDir(box.Min, renderRotation);
            box.Max = Raytracing.RotateDir(box.Max, renderRotation);
            if (box.Min.X > box.Max.X)
            {
                float X = box.Min.X;
                box.Min.X = box.Max.X;
                box.Max.X = X;
            }
            if (box.Min.Y > box.Max.Y)
            {
                float Y = box.Min.Y;
                box.Min.Y = box.Max.Y;
                box.Max.Y = Y;
            }
            if (box.Min.Z > box.Max.Z)
            {
                float Z = box.Min.Z;
                box.Min.Z = box.Max.Z;
                box.Max.Z = Z;
            }
            box.Min += renderTranslation;
            box.Max += renderTranslation;

            RayIntersection intersection = Raytracing.IntersectBox(Inignoto.game.camera.position, direction, box, renderRotation);

            if (!Raytracing.DoesCollisionOccur(Inignoto.game.camera.position, direction, box, Quaternion.Identity))
            {
                intersection.lambda = new Vector2(float.MaxValue);
            }

            return intersection;
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
