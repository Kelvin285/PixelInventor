using Inignoto.World.Chunks;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Utilities;

namespace Inignoto.Graphics.World
{
    public class ChunkRenderer
    {
        public ChunkRenderer()
        {

        }

        public void RenderChunk(GraphicsDevice device, BasicEffect effect, Chunk chunk)
        {
            if (chunk.secondMesh != null)
                chunk.secondMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device);
            if (chunk.mesh != null)
            {
                if (chunk.mesh.IsDisposed)
                {
                    if (chunk.secondMesh != null)
                    {
                        chunk.mesh = chunk.secondMesh;
                    }
                }
                chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device);
            }
        }
    }
}
