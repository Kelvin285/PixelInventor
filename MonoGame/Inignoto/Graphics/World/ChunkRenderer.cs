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

        public void RenderChunk(GraphicsDevice device, GameEffect effect, Chunk chunk, bool water = false, bool transparency = false, bool custom = false)
        {
            if (chunk.Disposed) return;


            if (custom)
            {
                Mesh.Mesh customMesh = chunk.customMesh;
                if (customMesh == null) customMesh = chunk.lastCustomMesh;
                if (customMesh != null)
                {
                    if (customMesh.empty) return;
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                        customMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                        customMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    customMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                }
            } else
            {
                if (!transparency)
                {
                    if (!water)
                    {
                        Mesh.Mesh mesh = chunk.mesh;
                        if (mesh == null)
                        {
                            mesh = chunk.lastMesh;
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
                        Mesh.Mesh waterMesh = chunk.waterMesh;
                        if (waterMesh == null) waterMesh = chunk.lastWaterMesh;
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
                    Mesh.Mesh transparencyMesh = chunk.transparencyMesh;
                    if (transparencyMesh == null) transparencyMesh = chunk.lastTransparencyMesh;
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
}
