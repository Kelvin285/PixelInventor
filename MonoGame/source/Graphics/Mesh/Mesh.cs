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

        public void Dispose()
        {
            vertexBuffer.Dispose();
        }
    }
}
