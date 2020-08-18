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

namespace Inignoto.World
{
    public class World
    {
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

        public GameTime gameTime { get; private set; }

        public readonly Random random;


        public World()
        {
            chunkManager = new ChunkManager(this);
            properties = new WorldProperties();
            entities = new List<Entity>();
            random = new Random();
        }

        public void UpdateChunkGeneration()
        {
            chunkManager.GenerateChunks(properties.generator);
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

        public void Render(GraphicsDevice device, BasicEffect effect)
        {
            chunkManager.Render(device, effect);
            RenderTileSelection(device, effect);

            for (int i = 0; i < entities.Count; i++)
            {
                Entity entity = entities[i];
                if (entity != null)
                {
                    entity.Render(device, effect);
                }
            }
        }

        private Mesh selectionBox;
        public void RenderTileSelection(GraphicsDevice device, BasicEffect effect)
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
            VertexPositionColorTexture[] vpct =
            {
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),

                new VertexPositionColorTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),

                new VertexPositionColorTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),

                new VertexPositionColorTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),

                 new VertexPositionColorTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(0, 0, 0), Color.White, new Vector2(0, 0)),

                 new VertexPositionColorTexture(
                    new Vector3(1, 0, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 0, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 1), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
                    new Vector3(1, 1, 0), Color.White, new Vector2(0, 0)),
                new VertexPositionColorTexture(
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
            return chunkManager.TryGetChunk(pos.x / Constants.CHUNK_SIZE, pos.y / Constants.CHUNK_SIZE, pos.z / Constants.CHUNK_SIZE);
        }

        public TileData GetVoxel(TilePos pos)
        {
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
            int cx = (int)System.Math.Floor((float)pos.x / Constants.CHUNK_SIZE);
            int cy = (int)System.Math.Floor((float)pos.y / Constants.CHUNK_SIZE);
            int cz = (int)System.Math.Floor((float)pos.z / Constants.CHUNK_SIZE);

            int x = pos.x - cx * Constants.CHUNK_SIZE;
            int y = pos.y - cy * Constants.CHUNK_SIZE;
            int z = pos.z - cz * Constants.CHUNK_SIZE;

            Chunk chunk = chunkManager.TryGetChunk(cx, cy, cz);
            if (chunk != null)
            {
                chunk.SetVoxel(x, y, z, voxel);
                chunk.MarkForRebuild();
                
                if (TileManager.GetTile(voxel.tile_id).IsVisible() == false)
                {
                    if (x % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx - 1, cy, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild();
                    }
                    if (x % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx + 1, cy, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild();
                    }

                    if (y % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy - 1, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild();
                    }
                    if (y % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy + 1, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild();
                    }

                    if (z % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy, cz - 1);
                        if (chunk2 != null) chunk2.MarkForRebuild();
                    }
                    if (z % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy, cz + 1);
                        if (chunk2 != null) chunk2.MarkForRebuild();
                    }
                }
                
            }
        }

        public TileRaytraceResult RayTraceTiles(Vector3f start, Vector3f end, TileRayTraceType type)
        {
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
    }
}
