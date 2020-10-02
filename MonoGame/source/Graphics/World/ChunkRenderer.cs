using Inignoto.World.Chunks;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Utilities;
using Inignoto.Effects;
using static Inignoto.World.World;
using Microsoft.Xna.Framework;

namespace Inignoto.Graphics.World
{
    public class ChunkRenderer
    {
        public ChunkRenderer()
        {

        }

        public void RenderChunk(GraphicsDevice device, GameEffect effect, Chunk chunk, bool water = false)
        {
            
            if (!water)
            {
                if (chunk.secondMesh != null)
                {
                    chunk.secondMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.secondMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.secondMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                }
                if (chunk.mesh != null)
                {
                    if (chunk.mesh.IsDisposed)
                    {
                        if (chunk.secondMesh != null)
                        {
                            chunk.mesh = chunk.secondMesh;
                        }
                    }

                    chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                }
            } else
            {
                if (chunk.secondWaterMesh != null)
                {
                    chunk.secondWaterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.secondWaterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.secondWaterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                }
                if (chunk.waterMesh != null)
                {
                    if (chunk.waterMesh.IsDisposed)
                    {
                        if (chunk.secondWaterMesh != null)
                        {
                            chunk.waterMesh = chunk.secondWaterMesh;
                        }
                    }
                    chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f)
                        chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                }
            }
        }
    }
}
