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
                    Mesh.Mesh mesh = chunk.mesh[(chunk.meshIndex + 1) % 2];
                    if (mesh == null)
                    {
                        mesh = chunk.mesh[chunk.meshIndex];
                    }
                    if (mesh != null)
                    {
                        if (mesh.empty) return;
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        
                            mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    }
                }
                else
                {
                    Mesh.Mesh waterMesh = chunk.waterMesh[(chunk.meshIndex + 1) % 2];
                    if (waterMesh == null)
                    {
                        waterMesh = chunk.waterMesh[chunk.meshIndex];
                    }
                    if (waterMesh != null)
                    {
                        if (waterMesh.empty) return;
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        
                            waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    }
                }
            }
            else
            {
               Mesh.Mesh transparencyMesh = chunk.transparencyMesh[(chunk.meshIndex + 1) % 2];
                if (transparencyMesh == null)
                {
                    transparencyMesh = chunk.transparencyMesh[chunk.meshIndex];
                }
               if (transparencyMesh != null)
                {
                    if (transparencyMesh.empty) return;
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                        transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                        transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    
                        transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                }
            }

        }
    }
}
