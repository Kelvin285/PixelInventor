using Inignoto.Effects;
using Inignoto.Math;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;

namespace Inignoto.Graphics.Mesh
{
    public class Mesh : IDisposable
    {

        public Matrix worldMatrix;
        public VertexPositionLightTexture[] triangleVertices;
        public int[] indices;
        public VertexBuffer vertexBuffer;
        public IndexBuffer indexBuffer;

        public bool lines;

        public Texture2D texture;

        public Vector3 scale = new Vector3(1.0f);
        public Vector3 position = new Vector3(0.0f);
        public Quaternion rotation = new Quaternion();

        public bool empty = false;
        public bool IsDisposed => vertexBuffer != null ? vertexBuffer.IsDisposed : true;
        public int Length => triangleVertices.Length;

        public bool built = false;

        public Mesh(GraphicsDevice device, VertexPositionLightTexture[] triangleVertices, bool lines = false, Texture2D texture = null)
        {
            //Console.WriteLine("Meshes: " + (meshes++));
            if (device == null)
            {
                return;
            }
            if (triangleVertices == null) return;
            if (triangleVertices.Length == 0)
            {
                empty = true;
                return;
            }
            
            this.triangleVertices = triangleVertices;


            worldMatrix = Matrix.CreateWorld(new Vector3(0, 0, 0), Vector3.Forward, Vector3.Up);
            this.lines = lines;
            this.texture = texture;
        }

        public void SetIndexBuffer(GraphicsDevice device, int[] indices)
        {
            this.indices = indices;
        }

        public void BuildMesh(GraphicsDevice device)
        {
            vertexBuffer = new VertexBuffer(device, typeof(
                           VertexPositionLightTexture), triangleVertices.Length, BufferUsage.
                           WriteOnly);

            vertexBuffer.SetData(this.triangleVertices);

            if (indices != null)
            {
                indexBuffer = new IndexBuffer(device, IndexElementSize.ThirtyTwoBits, indices.Length, BufferUsage.WriteOnly);
                indexBuffer.SetData(indices);
            }

            built = true;
        }


        public void CombineWith(Mesh mesh, Vector3 position, Vector3 scale, Quaternion rotation, Vector3 offset)
        {
            lock (this.triangleVertices)
            {
                lock (mesh.triangleVertices)
                {
                    if (this.triangleVertices == null) return;
                    int verts = this.triangleVertices.Length / 3;
                    List<VertexPositionLightTexture> VPLT = new List<VertexPositionLightTexture>();


                    for (int i = 0; i < mesh.triangleVertices.Length; i++)
                    {
                        Vector3 vec = new Vector3(mesh.triangleVertices[i].Position.X, mesh.triangleVertices[i].Position.Y, mesh.triangleVertices[i].Position.Z);

                        Matrix mat = Matrix.CreateTranslation(vec);
                        mat *= Matrix.CreateFromQuaternion(rotation);
                        mat *= Matrix.CreateScale(scale);
                        mat *= Matrix.CreateTranslation(position);
                        mat *= Matrix.CreateTranslation(offset);

                        mesh.triangleVertices[i].Position = mat.Translation;
                        //vec.Rotate(rotation);
                        //vec.Mul(scale);
                        //vec.Add(position);
                        // vec.Add(offset);
                        VPLT.Add(mesh.triangleVertices[i]);
                    }
                    for (int i = 0; i < this.triangleVertices.Length; i++)
                    {
                        VPLT.Add(this.triangleVertices[i]);
                    }

                    VertexPositionLightTexture[] triangleVertices = VPLT.ToArray();
                    this.triangleVertices = triangleVertices;
                    this.built = false;
                }
            }
            
        }


        public Vector3 GetPosition()
        {
            return worldMatrix.Translation;
        }

        public void SetPosition(Vector3 position)
        {
            this.position = position;
            worldMatrix.Translation = position;
        }

        public void SetPosition(float x, float y, float z)
        {
            position.X = x;
            position.Y = y;
            position.Z = z;
            worldMatrix.Translation = position;
        }

        public void SetScale(Vector3 scale)
        {
            this.scale = scale;
        }

        public void SetRotation(Quaternion rotation)
        {
            this.rotation = rotation;
        }

        public void Draw(GameEffect effect, GraphicsDevice device)
        {
            Draw(texture, effect, device, worldMatrix);
        }

        public void Draw(Texture texture, GameEffect effect, GraphicsDevice device)
        {
            Draw(texture, effect, device, worldMatrix);
        }

        public void Draw(Texture texture, GameEffect effect, GraphicsDevice device, Matrix worldMatrix)
        {
            if (built == false)
            {
                BuildMesh(device);
                built = true;
            }
            if (this == null) return;
            if (vertexBuffer != null)
            {
                if (vertexBuffer.IsDisposed) return;
            }
            if (empty) return;
            Matrix matrix = Matrix.CreateScale(scale) * Matrix.CreateFromQuaternion(rotation) * worldMatrix;
            
            effect.World = matrix;
            
            device.SetVertexBuffer(vertexBuffer);
            if (indexBuffer != null)
            {
                device.Indices = indexBuffer;
            }

            foreach (EffectPass pass in effect.CurrentTechnique.
                    Passes)
            {
                if (texture == null) continue;
                effect.Parameters["ModelTexture"].SetValue(texture);
                
                if (!GameResources.drawing_shadows)
                {
                    if (GameResources.shadowMap.shadowMapRenderTarget == null) continue;
                    effect.Parameters["ShadowTexture"].SetValue(GameResources.shadowMap.shadowMapRenderTarget[0]);
                    effect.Parameters["ShadowTexture2"].SetValue(GameResources.shadowMap.shadowMapRenderTarget[1]);
                }
                

                pass.Apply();

                if (vertexBuffer != null && triangleVertices != null)
                    if (indexBuffer != null)
                    {
                        device.DrawIndexedPrimitives(lines ? PrimitiveType.LineList : PrimitiveType.TriangleList, 0, 0, indexBuffer.IndexCount / (lines ? 2 : 3));
                    } else
                    device.DrawPrimitives(lines ? PrimitiveType.LineList : PrimitiveType.TriangleList, 0, Length);

            }
        }

        public Texture2D CreateTexture(Texture texture, GameEffect effect, GraphicsDevice device, Vector3 position, Quaternion rotation, int width, int height)
        {
            Vector3 pos = new Vector3(Inignoto.game.camera.position.X, Inignoto.game.camera.position.Y, Inignoto.game.camera.position.Z);
            Vector3 rot = new Vector3(Inignoto.game.camera.rotation.X, Inignoto.game.camera.rotation.Y, Inignoto.game.camera.rotation.Z);

            Inignoto.game.camera.position = new Vector3(0, 0, 0);
            Inignoto.game.camera.rotation = new Vector3(0, 0, 0);

            effect.View = Inignoto.game.camera.ViewMatrix;

            RenderTarget2D target;
            target = new RenderTarget2D(Inignoto.game.GraphicsDevice, width, height, false, SurfaceFormat.Color, DepthFormat.Depth16);

            Inignoto.game.GraphicsDevice.SetRenderTarget(target);
            Inignoto.game.GraphicsDevice.Clear(Color.Transparent);
            
            Inignoto.game.GraphicsDevice.DepthStencilState = DepthStencilState.Default;


            Matrix matrix = Matrix.CreateFromQuaternion(rotation);
            matrix.Translation = position;

            Draw(texture, effect, device, matrix);

            Inignoto.game.GraphicsDevice.SetRenderTarget(null);

            Inignoto.game.camera.position = pos;
            Inignoto.game.camera.rotation = rot;

            effect.View = Inignoto.game.camera.ViewMatrix;

            return target;
        }
        
        public void Dispose()
        {
            if (vertexBuffer != null)
            vertexBuffer.Dispose();
        }
    }
}
