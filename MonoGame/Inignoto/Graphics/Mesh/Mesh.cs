using Inignoto.Effects;
using Inignoto.Math;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;

namespace Inignoto.Graphics.Mesh
{
    public class Mesh
    {
        public Matrix worldMatrix;
        readonly VertexPositionLightTexture[] triangleVertices;
        VertexBuffer vertexBuffer;

        public readonly bool lines;

        public Texture2D texture;

        public Vector3 scale = new Vector3(1.0f);
        public Quaternion rotation = new Quaternion();

        public bool empty = false;

        public Mesh(GraphicsDevice device, VertexPositionLightTexture[] triangleVertices, bool lines = false, Texture2D texture = null)
        {
            if (triangleVertices.Length == 0)
            {
                empty = true;
                return;
            }
            this.triangleVertices = triangleVertices;
            vertexBuffer = new VertexBuffer(device, typeof(
                           VertexPositionLightTexture), triangleVertices.Length, BufferUsage.
                           WriteOnly);

            vertexBuffer.SetData(this.triangleVertices);

            worldMatrix = Matrix.CreateWorld(new Vector3(0, 0, 0), Vector3.Forward, Vector3.Up);
            this.lines = lines;
            this.texture = texture;
        }

        public Vector3 GetPosition()
        {
            return this.worldMatrix.Translation;
        }

        public void SetPosition(Vector3 position)
        {
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

        public bool IsDisposed => vertexBuffer != null ? vertexBuffer.IsDisposed : true;
        public int Length => triangleVertices.Length;

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
            
            if (empty) return;
            Matrix matrix = Matrix.CreateScale(scale) * Matrix.CreateFromQuaternion(rotation) * worldMatrix;
            
            effect.World = matrix;
            device.SetVertexBuffer(vertexBuffer);


            foreach (EffectPass pass in effect.CurrentTechnique.
                    Passes)
            {
                if (texture == null) continue;
                effect.Parameters["ModelTexture"].SetValue(texture);
                
                if (!GameResources.drawing_shadows)
                {
                    if (GameResources.shadowMap.shadowMapRenderTarget == null) continue;
                    effect.Parameters["ShadowTexture"].SetValue(GameResources.shadowMap.shadowMapRenderTarget);
                }
                

                pass.Apply();

                //device.DrawUserPrimitives(lines ? PrimitiveType.LineList : PrimitiveType.TriangleList, triangleVertices, 0, Length);
                device.DrawPrimitives(lines ? PrimitiveType.LineList : PrimitiveType.TriangleList, 0, Length);
            }
        }

        public Texture2D CreateTexture(Texture texture, GameEffect effect, GraphicsDevice device, Vector3 position, Quaternion rotation, int width, int height)
        {
            Vector3f pos = new Vector3f(Inignoto.game.camera.position);
            Vector3f rot = new Vector3f(Inignoto.game.camera.rotation);

            Inignoto.game.camera.position = new Vector3f(0, 0, 0);
            Inignoto.game.camera.rotation = new Vector3f(0, 0, 0);

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

            return (Texture2D)target;
        }

        public void Dispose()
        {
            if (vertexBuffer != null)
            vertexBuffer.Dispose();
        }
    }
}
