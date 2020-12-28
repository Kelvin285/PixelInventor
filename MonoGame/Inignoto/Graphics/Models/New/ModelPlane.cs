using Inignoto.Effects;
using Inignoto.Graphics.Mesh;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Graphics.Models.New
{
    public class ModelPlane : ModelObject
    {
        protected static Vector3[] plane_vertices = {
            new Vector3(-0.5f, -0.5f, -0.5f),
            new Vector3(-0.5f, 0.5f, -0.5f),
            new Vector3(0.5f, 0.5f, -0.5f),
            new Vector3(0.5f, -0.5f, -0.5f)
        };

        public Mesh.Mesh mesh;

        private Vector4 uv = new Vector4(0, 0, 1, 1);

        public ModelPlane() : base()
        {

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

        public Vector4 GetUvCoords(int index)
        {
            switch (index) {
                case 1:
                    {
                        return new Vector4(uv.X, uv.W, -1, -1);
                    }
                case 2:
                    {
                        return new Vector4(uv.Z, uv.W, -1, -1);
                    }
                case 3:
                    {
                        return new Vector4(uv.Z, uv.Y, -1, -1);
                    }
            }
            
            return new Vector4(uv.X, uv.Y, -1, -1);
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
