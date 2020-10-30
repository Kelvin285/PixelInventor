using Inignoto.World.Chunks;
using Microsoft.Xna.Framework;
using Inignoto.Utilities;
using Inignoto.Graphics.World;
using Microsoft.Xna.Framework.Graphics;
using System.Threading;
using static Inignoto.Tiles.Tile;
using Inignoto.Math;
using static Inignoto.Math.Raytracing;
using Inignoto.Tiles.Data;
using Inignoto.Tiles;
using Inignoto.World.RaytraceResult;
using System.Collections.Generic;
using Inignoto.Entities;
using System;
using Inignoto.Graphics.Mesh;
using Inignoto.Graphics.Textures;
using Inignoto.Effects;
using System.Security.Cryptography.X509Certificates;

namespace Inignoto.World
{
    public class World
    {

        public enum WorldArea
        {
            MAIN = 0, TOP = 1, BOTTOM = 2
        }

        public struct TilePos {
            public int x, y, z;
            public TilePos(int x = 0, int y = 0, int z = 0)
            {
                this.x = x;
                this.y = y;
                this.z = z;
            }
            public TilePos(float x = 0, float y = 0, float z = 0)
            {
                this.x = (int)System.Math.Floor(x);
                this.y = (int)System.Math.Floor(y);
                this.z = (int)System.Math.Floor(z);
            }

            public void SetPosition(int x, int y, int z)
            {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public void SetPosition(float x, float y, float z)
            {
                this.x = (int)System.Math.Floor(x);
                this.y = (int)System.Math.Floor(y);
                this.z = (int)System.Math.Floor(z);
            }
        }

        public readonly ChunkManager chunkManager;
        public readonly WorldProperties properties;
        public readonly List<Entity> entities;

        private Mesh skybox;

        public GameTime gameTime { get; private set; }

        public readonly Random random;

        public float radius { get; private set; }

        public World()
        {
            chunkManager = new ChunkManager(this);
            properties = new WorldProperties();
            entities = new List<Entity>();
            random = new Random();
            radius = 4096;
        }

        public void UpdateChunkGeneration()
        {
            chunkManager.GenerateChunks(properties.generator);
        }

        public void FixChunkBorders()
        {
            chunkManager.FixChunkBorders();
            TickChunks();
        }

        public void TickChunks()
        {
            chunkManager.UpdateChunks();
        }

        public void Update(Vector3 camera_position, GameTime time)
        {
            this.gameTime = time;
            chunkManager.BeginUpdate(camera_position);
            for (int i = 0; i < entities.Count; i++)
            {
                Chunk chunk = TryGetChunk(new TilePos(entities[i].position.X, entities[i].position.Y, entities[i].position.Z));
                if (entities[i].TicksExisted > 0)
                {
                    entities[i].Update(time);
                    continue;
                }
                if (chunk != null)
                {
                    if (chunk.NeedsToGenerate() == false)
                    {
                        entities[i].Update(time);
                    }
                }
            }
        }

        public void RenderVulkan(GameTime time)
        {

            for (int i = 0; i < entities.Count; i++)
            {
                Entity entity = entities[i];
                if (entity != null)
                {
                    //entity.Render(device, effect, time);
                }
            }

            chunkManager.RenderVulkan();
        }

        public void Render(GraphicsDevice device, GameEffect effect, GameTime time)
        {
            effect.Radius = radius;
            effect.Area = (int)Inignoto.game.player.area;
            effect.FogDistance = GameSettings.Settings.HORIZONTAL_VIEW * Constants.CHUNK_SIZE + 100;
            effect.CameraPos = Inignoto.game.camera.position.Vector;
            effect.WorldRender = true;


            if (skybox == null)
            {
                skybox = TileBuilder.BuildTile(-0.5f, -0.5f, -0.5f, TileManager.DIRT.DefaultData, device);
                skybox.texture = Textures.white_square;
            }
            skybox.scale = new Vector3(1000.0f);
            


            for (int i = 0; i < entities.Count; i++)
            {
                Entity entity = entities[i];
                if (entity != null)
                {
                    entity.Render(device, effect, time);
                }
            }
            RenderTileSelection(device, effect);

            GameResources.effect.ObjectColor = GameResources.effect.FogColor;
            skybox.SetPosition(Inignoto.game.player.position.Vector);
            skybox.Draw(effect, device);
            GameResources.effect.ObjectColor = Color.White.ToVector4();


            RasterizerState rasterizerState = new RasterizerState();
            rasterizerState.CullMode = CullMode.CullClockwiseFace;
            device.RasterizerState = rasterizerState;

            chunkManager.Render(device, effect);

            RasterizerState rasterizerState2 = new RasterizerState();
            rasterizerState2.CullMode = CullMode.None;
            device.RasterizerState = rasterizerState2;

            effect.WorldRender = false;
        }

        private Mesh selectionBox;
        public void RenderTileSelection(GraphicsDevice device, GameEffect effect)
        {
            if (selectionBox == null)
            {
                BuildSelectionBox();
            }
            TileRaytraceResult result = Inignoto.game.camera.highlightedTile;
            if (result != null)
            {
                TilePos hitPos = result.pos;

                selectionBox.SetPosition(new Vector3(hitPos.x, hitPos.y, hitPos.z));
                selectionBox.Draw(Textures.tiles.GetTexture(), effect, device);
            }
        }

        public void BuildSelectionBox()
        {
            VertexPositionLightTexture[] vpct =
            { 
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),

                new VertexPositionLightTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),

                new VertexPositionLightTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),

                new VertexPositionLightTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),

                 new VertexPositionLightTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),

                 new VertexPositionLightTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionLightTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
            };
            for (int i = 0; i < vpct.Length; i++)
            {
                vpct[i].Position -= new Vector3(0.5f);
                vpct[i].Position *= 1.01f;
                vpct[i].Position += new Vector3(0.5f);
            }
            selectionBox = new Mesh(Inignoto.game.GraphicsDevice, vpct, true);
        }

        public ChunkManager GetChunkManager()
        {
            return chunkManager;
        }

        public Chunks.Chunk TryGetChunk(TilePos pos)
        {
            if (pos.x < 0) pos.x += (int)(radius * 4);
            if (pos.x > (int)(radius * 4)) pos.x -= (int)(radius * 4);
            return chunkManager.TryGetChunk(pos.x / Constants.CHUNK_SIZE, pos.y / Constants.CHUNK_SIZE, pos.z / Constants.CHUNK_SIZE);
        }

        public TileData GetVoxel(TilePos pos)
        {
            if (pos.x < 0) pos.x += (int)(radius * 4);
            if (pos.x > (int)(radius * 4)) pos.x -= (int)(radius * 4);
            int cx = (int)System.Math.Floor((float)pos.x / Constants.CHUNK_SIZE);
            int cy = (int)System.Math.Floor((float)pos.y / Constants.CHUNK_SIZE);
            int cz = (int)System.Math.Floor((float)pos.z / Constants.CHUNK_SIZE);
            
            int x = pos.x - cx * Constants.CHUNK_SIZE;
            int y = pos.y - cy * Constants.CHUNK_SIZE;
            int z = pos.z - cz * Constants.CHUNK_SIZE;

            Chunk chunk = chunkManager.TryGetChunk(cx, cy, cz);
            if (chunk != null)
            {
                return chunk.GetVoxel(x, y, z);
            }
            return TileManager.AIR.DefaultData;
        }

        public void SetVoxel(TilePos pos, TileData voxel)
        {
            if (pos.x < 0) pos.x += (int)(radius * 4);
            if (pos.x > (int)(radius * 4)) pos.x -= (int)(radius * 4);
            int cx = (int)System.Math.Floor((float)pos.x / Constants.CHUNK_SIZE);
            int cy = (int)System.Math.Floor((float)pos.y / Constants.CHUNK_SIZE);
            int cz = (int)System.Math.Floor((float)pos.z / Constants.CHUNK_SIZE);

            int x = pos.x - cx * Constants.CHUNK_SIZE;
            int y = pos.y - cy * Constants.CHUNK_SIZE;
            int z = pos.z - cz * Constants.CHUNK_SIZE;

            TileRayTraceType raytraceType = TileManager.GetTile(voxel.tile_id).GetRayTraceType();

            Chunk chunk = chunkManager.TryGetChunk(cx, cy, cz);
            if (chunk != null)
            {

                chunk.SetVoxel(x, y, z, voxel);
                chunk.MarkForRebuild();
                //chunk.BuildMesh();
                
                if (TileManager.GetTile(voxel.tile_id).IsVisible() == false)
                {
                    if (x % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx - 1, cy, cz);
                        if (chunk2 != null) chunk2.GetWorld().GetChunkManager().QueueForRerender(chunk2);
                    }
                    if (x % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx + 1, cy, cz);
                        if (chunk2 != null) chunk2.GetWorld().GetChunkManager().QueueForRerender(chunk2);
                    }

                    if (y % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy - 1, cz);
                        if (chunk2 != null) chunk2.GetWorld().GetChunkManager().QueueForRerender(chunk2);
                    }
                    if (y % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy + 1, cz);
                        if (chunk2 != null) chunk2.GetWorld().GetChunkManager().QueueForRerender(chunk2);
                    }

                    if (z % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy, cz - 1);
                        if (chunk2 != null) chunk2.GetWorld().GetChunkManager().QueueForRerender(chunk2);
                    }
                    if (z % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy, cz + 1);
                        
                        if (chunk2 != null) chunk2.GetWorld().GetChunkManager().QueueForRerender(chunk2);
                    }
                }
                
            }
        }

        public TileRaytraceResult RayTraceTiles(Vector3f start, Vector3f end, TileRayTraceType type)
        {
            if (end.X > (int)(radius * 4))
            {
                end.X -= (int)(radius * 4);
                start.X -= (int)(radius * 4);
            }
            else if (end.X < 0)
            {
                end.X += (int)(radius * 4);
                start.X += (int)(radius * 4);
            }
            TilePos pos = new TilePos(start.X, start.Y, start.Z);

            float length = end.DistanceTo(start.Vector);

            Vector3f raypos = new Vector3f(start);
            Vector3f raydir = new Vector3f(end).Sub(start).Div(length);
            
            RayBox raybox = new RayBox
            {
                Min = new Vector3f(),
                Max = new Vector3f()
            };
            for (int ii = 0; ii < 10; ii++)
            {
                
                if (raypos.DistanceTo(start.Vector) > length)
                {
                    break;
                }
                pos.SetPosition(raypos.X, raypos.Y, raypos.Z);
                raybox.Min.Set(pos.x, pos.y, pos.z);
                raybox.Max.Set(pos.x + 1, pos.y + 1, pos.z + 1);
                RayIntersection intersection = Raytracing.IntersectBox(start, raydir, raybox);
                raypos.Set(start);
                raypos.Add(new Vector3f(raydir).Mul(intersection.lambda.Y + 0.01f));


                TileData data = GetVoxel(pos);
                Tile tile = TileManager.GetTile(data.tile_id);
                if (tile != null)
                {
                    if (tile.IsVisible())
                    {
                        if (tile.GetRayTraceType() == type)
                        {
                            Vector3f dir = new Vector3f(end).Sub(start);
                            dir.Div(dir.Vector.Length());
                            float min = float.MaxValue;
                            bool hit = false;
                            RayIntersection r = new RayIntersection();
                            foreach (RayBox box in tile.GetCollisionBoxes(data)) {
                                RayBox b2 = new RayBox
                                {
                                    Min = new Vector3f(box.Min).Add(pos.x, pos.y, pos.z),
                                    Max = new Vector3f(box.Max).Add(pos.x, pos.y, pos.z)
                                };

                                if (Raytracing.DoesCollisionOccur(start, dir, b2, new Quaternionf()))
                                {
                                    RayIntersection it = Raytracing.IntersectBox(start, dir, b2);
                                    if (it.lambda.X < min)
                                    {
                                        min = it.lambda.X;
                                        hit = true;
                                        r = it;
                                    }
                                }
                            }
                            if (hit && min <= length)
                            {
                                return new TileRaytraceResult(r, pos, data, new Vector3f(start).Add(new Vector3f(dir).Mul(r.lambda.X)));
                            }
                        }
                        
                    }
                }
            }

            return null;
        }

        public Vector4 GetSkyColor()
        {
            return new Vector4(Color.CornflowerBlue.R / 255.0f, Color.CornflowerBlue.G / 255.0f, Color.CornflowerBlue.B / 255.0f, 1.0f);
        }
    }
}
