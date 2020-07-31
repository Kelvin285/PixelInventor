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
            if (chunk.mesh != null)
            chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device);
        }
    }
}
