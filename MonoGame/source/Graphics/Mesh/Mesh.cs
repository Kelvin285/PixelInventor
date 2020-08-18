using Inignoto.Math;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Inignoto.Graphics.Mesh
{
    public class Mesh
    {
        Matrix worldMatrix;
        readonly VertexPositionColorTexture[] triangleVertices;
        VertexBuffer vertexBuffer;

        public readonly bool lines;

        public readonly Texture2D texture;

        public Vector3 scale = new Vector3(1.0f);
        public Quaternion rotation = new Quaternion();
        
        public Mesh(GraphicsDevice device, VertexPositionColorTexture[] triangleVertices, bool lines = false, Texture2D texture = null)
        {
            this.triangleVertices = triangleVertices;
            vertexBuffer = new VertexBuffer(device, typeof(
                           VertexPositionColorTexture), triangleVertices.Length, BufferUsage.
                           WriteOnly);

            vertexBuffer.SetData(this.triangleVertices);

            worldMatrix = Matrix.CreateWorld(new Vector3(0, 0, 0), Vector3.Forward, Vector3.Up);
            this.lines = lines;
            this.texture = texture;
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

        public bool IsDisposed => vertexBuffer.IsDisposed;
        public int Length => triangleVertices.Length;

        public void Draw(BasicEffect effect, GraphicsDevice device)
        {
            Draw(texture, effect, device, worldMatrix);
        }

        public void Draw(Texture texture, BasicEffect effect, GraphicsDevice device)
        {
            Draw(texture, effect, device, worldMatrix);
        }

        public void Draw(Texture texture, BasicEffect effect, GraphicsDevice device, Matrix worldMatrix)
        {
            Matrix matrix = Matrix.CreateScale(scale) * Matrix.CreateFromQuaternion(rotation) * worldMatrix;
            
            effect.World = matrix;
            device.SetVertexBuffer(vertexBuffer);
            foreach (EffectPass pass in effect.CurrentTechnique.
                    Passes)
            {
                effect.Parameters["Texture"].SetValue(texture);
                pass.Apply();

                device.DrawPrimitives(lines ? PrimitiveType.LineList : PrimitiveType.TriangleList, 0, Length);
            }
        }

        public Texture2D CreateTexture(Texture texture, BasicEffect effect, GraphicsDevice device, Vector3 position, Quaternion rotation, int width, int height)
        {
            RenderTarget2D target;
            target = new RenderTarget2D(Inignoto.game.GraphicsDevice, width, height, false, SurfaceFormat.Color, DepthFormat.Depth16);

            Inignoto.game.GraphicsDevice.SetRenderTarget(target);
            Inignoto.game.GraphicsDevice.Clear(Color.Transparent);
            
            Inignoto.game.GraphicsDevice.DepthStencilState = DepthStencilState.Default;


            Matrix matrix = Matrix.CreateFromQuaternion(rotation);
            matrix.Translation = position;

            Draw(texture, effect, device, matrix);

            Inignoto.game.GraphicsDevice.SetRenderTarget(null);
            return (Texture2D)target;
        }

        public void Dispose()
        {
            vertexBuffer.Dispose();
        }
    }
}
