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
            if (!transparency)
            {
                if (!water)
                {
                    Chunk c = null;
                    if ((c = chunk.GetAdjacentChunk(-1, 0, 0)) != null && c.Full)
                    {
                        if ((c = chunk.GetAdjacentChunk(1, 0, 0)) != null && c.Full)
                        {
                            if ((c = chunk.GetAdjacentChunk(0, -1, 0)) != null && c.Full)
                            {
                                if ((c = chunk.GetAdjacentChunk(0, 1, 0)) != null && c.Full)
                                {
                                    if ((c = chunk.GetAdjacentChunk(0, 0, -1)) != null && c.Full)
                                    {
                                        if ((c = chunk.GetAdjacentChunk(0, 0, 1)) != null && c.Full)
                                        {
                                            return;
                                        }
                                    }
                                }
                            }
                        }
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
                        if (chunk.mesh.empty) return;
                        chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            chunk.mesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    }
                    if (chunk.secondMesh != null)
                    {
                        if (chunk.secondMesh.empty) return;
                        chunk.secondMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            chunk.secondMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            chunk.secondMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    }
                }
                else
                {

                    if (chunk.waterMesh != null)
                    {
                        if (chunk.waterMesh.IsDisposed)
                        {
                            if (chunk.secondWaterMesh != null)
                            {
                                chunk.waterMesh = chunk.secondWaterMesh;
                            }
                        }
                        if (chunk.waterMesh.empty) return;
                        chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            chunk.waterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    }
                    if (chunk.secondWaterMesh != null)
                    {
                        if (chunk.secondWaterMesh.empty) return;
                        chunk.secondWaterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                            chunk.secondWaterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                        if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                            chunk.secondWaterMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    }
                }
            }
            else
            {

                if (chunk.transparencyMesh != null)
                {
                    if (chunk.transparencyMesh.IsDisposed)
                    {
                        if (chunk.secondTransparencyMesh != null)
                        {
                            chunk.transparencyMesh = chunk.secondTransparencyMesh;
                        }
                    }
                    if (chunk.transparencyMesh.empty) return;
                    chunk.transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                        chunk.transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                        chunk.transparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                }
                if (chunk.secondTransparencyMesh != null)
                {
                    if (chunk.secondTransparencyMesh.empty) return;
                    chunk.secondTransparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() < chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X > chunk.GetWorld().radius * 2.0f)
                        chunk.secondTransparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));
                    if (chunk.GetX() > chunk.GetWorld().radius * 2.0f / 16.0f && Inignoto.game.camera.position.X <= chunk.GetWorld().radius * 2.0f)
                        chunk.secondTransparencyMesh.Draw(Textures.Textures.tiles.GetTexture(), effect, device, Matrix.CreateTranslation(-chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                }
            }

        }
    }
}
