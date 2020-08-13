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
        
        public Mesh(GraphicsDevice device, VertexPositionColorTexture[] triangleVertices, bool lines = false)
        {
            this.triangleVertices = triangleVertices;
            vertexBuffer = new VertexBuffer(device, typeof(
                           VertexPositionColorTexture), triangleVertices.Length, BufferUsage.
                           WriteOnly);

            vertexBuffer.SetData(this.triangleVertices);

            worldMatrix = Matrix.CreateWorld(new Vector3(0, 0, 0), Vector3.Forward, Vector3.Up);
            this.lines = lines;
        }

        public void SetPosition(Vector3 position)
        {
            worldMatrix.Translation = position;
        }

        public bool IsDisposed => vertexBuffer.IsDisposed;
        public int Length => triangleVertices.Length;

        public void Draw(Texture texture, BasicEffect effect, GraphicsDevice device)
        {
            Draw(texture, effect, device, worldMatrix);
        }

        public void Draw(Texture texture, BasicEffect effect, GraphicsDevice device, Matrix worldMatrix)
        {
            effect.World = worldMatrix;
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
