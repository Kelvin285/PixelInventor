using Inignoto.World.Chunks;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Utilities;
using Inignoto.Effects;
using static Inignoto.World.World;
using Microsoft.Xna.Framework;
using System;

namespace Inignoto.Graphics.World
{
    public class ChunkRenderer
    {
        public ChunkRenderer()
        {

        }

        public void RenderChunk(GraphicsDevice device, GameEffect effect, Chunk chunk, bool water = false, bool transparency = false)
        {
            if (chunk.Disposed) return;
            if (!transparency)
            {
                if (!water)
                {
                    if (chunk.mesh != null)
                    {
                        if (chunk.mesh.empty) return;
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        else
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        else
                            chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    }
                }
                else
                {
                    if (chunk.waterMesh != null)
                    {
                        if (chunk.waterMesh.empty) return;
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        else
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        else
                            chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    }
                }
            }
            else
            {

               if (chunk.transparencyMesh != null)
                {
                    if (chunk.transparencyMesh.empty) return;
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                        chunk.transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    else
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                        chunk.transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    else
                        chunk.transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                }
            }

        }
    }
}
