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
using Inignoto.World.Generator;
using Inignoto.Entities.Player;
using static Inignoto.Entities.Player.PlayerEntity;
using Inignoto.Inventory;

namespace Inignoto.World
{
    public class World
    {

        public enum WorldArea
        {
            MAIN = 0, TOP = 1, BOTTOM = 2
        }

        public struct TilePos
        {
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

            public override bool Equals(object o)
            {
                if (o is TilePos)
                {
                    TilePos obj = (TilePos)o;
                    return x == obj.x && y == obj.y && z == obj.z;
                }
                return false;
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    var hashCode = x.GetHashCode();
                    hashCode = (hashCode * 397) ^ y.GetHashCode();
                    hashCode = (hashCode * 397) ^ z.GetHashCode();

                    return hashCode;
                }
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

            public override string ToString()
            {
                return "TilePos: [" + x + "," + y + "," + z + "]";
            }

            public static bool operator ==(TilePos a, TilePos b)
            {
                return a.x == b.x && a.y == b.y && a.z == b.z;
            }

            public static bool operator !=(TilePos a, TilePos b)
            {
                return !(a == b);
            }
            }

        public ChunkManager chunkManager;
        public WorldProperties properties;
        public List<Entity> entities;

        private Mesh skybox;
        public Vector4 SkyColor = new Vector4(0, 0, 0, 0);

        public GameTime gameTime { get; private set; }

        public Random random;

        public float radius { get; private set; }

        public Vector3 sunLook = new Vector3(1, -1, 0);

        public float DayTime = 6000;

        public string name;

        public World(int seed = 0, string name = "New World")
        {
            this.name = name;
            chunkManager = new ChunkManager(this);
            properties = new WorldProperties(name, this);
            properties.seed = seed;
            properties.Load();
            ChunkGenerator.noise.SetSeed(properties.seed);
            entities = new List<Entity>();
            random = new Random();
            radius = 4096;

            skybox = TileBuilder.BuildTile(-0.5f, -0.5f, -0.5f, TileRegistry.DIRT.DefaultData, TileRegistry.AIR.DefaultData, Inignoto.game.GraphicsDevice);
            skybox.texture = Textures.white_square;
            skybox.scale = new Vector3(1000, 1000, 1000);

            BuildMeshes();
        }

        public void Construct(string name, WorldProperties properties)
        {
            properties.world = this;
            this.name = name;
            chunkManager = new ChunkManager(this);
            this.properties = properties;
            this.properties.Load();
            ChunkGenerator.noise.SetSeed(properties.seed);
            entities.Clear();
            radius = 4096;

            DayTime = 6000;

            BuildMeshes();
        }


        public void Dispose()
        {
            chunkManager.Dispose();
            properties.Save();
            foreach (Entity entity in entities)
            {
                entity.Save();
            }
        }

        public void UpdateChunkGeneration()
        {
            chunkManager.BeginUpdate(Inignoto.game.camera.position);
        }

        public void TickChunks()
        {
            chunkManager.UpdateChunks();
        }

        public void Update(Vector3 camera_position, GameTime time)
        {
            //chunkManager.BeginUpdate(camera_position);

            gameTime = time;

            float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;

            DayTime += delta * 0.5f;

            float angle = MathHelper.ToRadians(360 * (DayTime / 24000.0f) - 45);

            sunLook.X = -MathF.Cos(angle);
            sunLook.Y = -MathF.Sin(angle);
            
            for (int i = 0; i < entities.Count; i++)
            {
                Chunk chunk = TryGetChunk(entities[i].position.X, entities[i].position.Y, entities[i].position.Z);
                if (entities[i].TicksExisted == 0)
                {
                    entities[i].Update(time);
                }
                if (entities[i].TicksExisted > 1 || entities[i] is PlayerEntity && ((PlayerEntity)entities[i]).gamemode == Gamemode.FREECAM)
                {
                    entities[i].Update(time);
                    continue;
                }
                if (chunk != null)
                {
                    entities[i].Update(time);
                } else
                {

                    entities[i].velocity *= 0;
                }
            }
        }

        public void Render(GraphicsDevice device, GameEffect effect, GameTime time)
        {
            chunkManager.UpdatePosition();
            //float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;
            
            effect.Radius = radius;
            if (!GameResources.drawing_shadows)
            {
                effect.Area = (int)Inignoto.game.player.area;
                effect.FogDistance = GameSettings.Settings.HORIZONTAL_VIEW * Constants.CHUNK_SIZE + 100;
                effect.ShadowView = GameResources.shadowMap.view;
                effect.ShadowProjection = GameResources.shadowMap.projection;
                effect.ShadowProjection2 = GameResources.shadowMap.projection2;
                effect.ShadowProjection3 = GameResources.shadowMap.projection3;
                effect.FogColor = GetSkyColor();

                effect.SunLook = sunLook;
                GameResources.shadowMap._ShadowMapGenerate.SunLook = sunLook;

                //DRAW SKYBOX
                GameResources.effect.ObjectColor = GameResources.effect.FogColor;
                skybox.SetPosition(Inignoto.game.player.position.X, Inignoto.game.player.position.Y, Inignoto.game.player.position.Z);
                skybox.Draw(effect, device);
                GameResources.effect.ObjectColor = Constants.COLOR_WHITE;



            }

            effect.CameraPos = Inignoto.game.camera.position;
            effect.WorldRender = true;

            for (int i = 0; i < entities.Count; i++)
            {
                Entity entity = entities[i];
                if (entity != null)
                {
                    entity.PreRender(effect);
                    entity.Render(device, effect, time);
                    entity.PostRender(effect);
                }
            }

            //device.RasterizerState = GameResources.CULL_CLOCKWISE_RASTERIZER_STATE;

            chunkManager.Render(device, effect);

            //device.RasterizerState = GameResources.DEFAULT_RASTERIZER_STATE;


            //DRAW TILE SELECTION
            if (!GameResources.drawing_shadows)
            RenderTileSelection(device, effect);

            effect.WorldRender = false;
        }

        private Mesh selectionBox;
        private Mesh selectionFace;
        public void RenderTileSelection(GraphicsDevice device, GameEffect effect)
        {
            TileRaytraceResult result = Inignoto.game.camera.highlightedTile;
            if (result != null)
            {
                TilePos hitPos = result.pos;
                
                selectionBox.SetPosition(new Vector3(hitPos.x + 0.5f, hitPos.y + 0.5f, hitPos.z + 0.5f));
                selectionBox.Draw(Textures.white_square, effect, device);

                effect.ObjectColor = new Vector4(2, 2, 2, 0.1f);
                selectionFace.SetPosition(new Vector3(hitPos.x + 0.5f, hitPos.y + 0.5f, hitPos.z + 0.5f) + result.intersection.normal * 0.01f);
                selectionFace.Draw(Textures.white_square, effect, device);
                effect.ObjectColor = new Vector4(1, 1, 1, 1);
            }
        }

        public void BuildMeshes()
        {
            selectionBox = TileBuilder.BuildTile(-0.5f, -0.5f, -0.5f, TileRegistry.DIRT.DefaultData, TileRegistry.AIR.DefaultData, Inignoto.game.GraphicsDevice, true);

            selectionBox.SetScale(new Vector3(1.01f));

            selectionFace = TileBuilder.BuildTile(-0.5f, -0.5f, -0.5f, TileRegistry.DIRT.DefaultData, TileRegistry.AIR.DefaultData, Inignoto.game.GraphicsDevice, false);

            selectionFace.SetScale(new Vector3(0.99f));
        }

        public ChunkManager GetChunkManager()
        {
            return chunkManager;
        }

        public Chunk TryGetChunk(TilePos pos)
        {
            if (pos.x < 0) pos.x += (int)(radius * 4);
            if (pos.x > (int)(radius * 4)) pos.x -= (int)(radius * 4);
            return chunkManager.TryGetChunk(pos.x / Constants.CHUNK_SIZE, pos.y / Constants.CHUNK_SIZE, pos.z / Constants.CHUNK_SIZE);
        }


        public Chunk TryGetChunk(int tile_x, int tile_y, int tile_z)
        {
            if (tile_x < 0) tile_x += (int)(radius * 4);
            if (tile_x > (int)(radius * 4)) tile_x -= (int)(radius * 4);
            return chunkManager.TryGetChunk(tile_x / Constants.CHUNK_SIZE, tile_y / Constants.CHUNK_SIZE, tile_z / Constants.CHUNK_SIZE);
        }

        public Chunk TryGetChunk(double x, double y, double z)
        {
            return TryGetChunk((int)x, (int)y, (int)z);
        }

        public TileData GetOverlayVoxel(TilePos pos)
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
                return chunk.GetOverlayVoxel(x, y, z);
            }
            return TileRegistry.AIR.DefaultData;
        }
        public TileData GetVoxel(TilePos pos)
        {
            return GetVoxel(pos.x, pos.y, pos.z);
        }

        public TileData GetVoxel(int pos_x, int pos_y, int pos_z)
        {
            if (pos_x < 0) pos_x += (int)(radius * 4);
            if (pos_x > (int)(radius * 4)) pos_x -= (int)(radius * 4);
            int cx = (int)System.Math.Floor((float)pos_x / Constants.CHUNK_SIZE);
            int cy = (int)System.Math.Floor((float)pos_y / Constants.CHUNK_SIZE);
            int cz = (int)System.Math.Floor((float)pos_z / Constants.CHUNK_SIZE);

            int x = pos_x - cx * Constants.CHUNK_SIZE;
            int y = pos_y - cy * Constants.CHUNK_SIZE;
            int z = pos_z - cz * Constants.CHUNK_SIZE;

            Chunk chunk = chunkManager.TryGetChunk(cx, cy, cz);
            if (chunk != null)
            {
                return chunk.GetVoxel(x, y, z);
            }
            return TileRegistry.AIR.DefaultData;
        }

        public void MineVoxel(TilePos pos, int strength)
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
                int index = chunk.GetIndexFor(x, y, z);
                if (chunk.voxels[index].mining_time < 0) chunk.voxels[index].mining_time = 0;
                chunk.voxels[index].mining_time += strength;
                
                int hits = TileRegistry.GetTile(chunk.voxels[index].voxel.tile_id).hits;
                if (chunk.voxels[index].overlay.tile_id != TileRegistry.AIR.DefaultData.tile_id)
                {
                    if (TileRegistry.GetTile(chunk.voxels[index].overlay.tile_id).hits > hits)
                    {
                        hits = TileRegistry.GetTile(chunk.voxels[index].overlay.tile_id).hits;
                    }
                }
                
                if (chunk.voxels[index].mining_time >= hits)
                {
                    if (TileRegistry.GetTile(chunk.voxels[index].voxel.tile_id).CanDropAsItem())
                    entities.Add(new ItemEntity(this, new Vector3(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f), new ItemStack(TileRegistry.GetTile(chunk.voxels[index].voxel.tile_id))));

                    if (TileRegistry.GetTile(chunk.voxels[index].overlay.tile_id).CanDropAsItem())
                        entities.Add(new ItemEntity(this, new Vector3(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f), new ItemStack(TileRegistry.GetTile(chunk.voxels[index].overlay.tile_id))));


                    chunk.voxels[index].mining_time = 0;
                    chunk.SetVoxel(x, y, z, TileRegistry.AIR.DefaultData);
                    chunk.MarkForRebuild();
                }
            }
        }

        public void SetVoxel(TilePos pos, TileData voxel)
        {
            SetVoxel(pos, voxel, TileRegistry.AIR.DefaultData);
        }

        public void SetVoxel(TilePos pos, TileData voxel, TileData overlay)
        {
            if (pos.x < 0) pos.x += (int)(radius * 4);
            if (pos.x > (int)(radius * 4)) pos.x -= (int)(radius * 4);
            int cx = (int)System.Math.Floor((float)pos.x / Constants.CHUNK_SIZE);
            int cy = (int)System.Math.Floor((float)pos.y / Constants.CHUNK_SIZE);
            int cz = (int)System.Math.Floor((float)pos.z / Constants.CHUNK_SIZE);

            int x = pos.x - cx * Constants.CHUNK_SIZE;
            int y = pos.y - cy * Constants.CHUNK_SIZE;
            int z = pos.z - cz * Constants.CHUNK_SIZE;

            TileRayTraceType raytraceType = TileRegistry.GetTile(voxel.tile_id).GetRayTraceType();

            Chunk chunk = chunkManager.TryGetChunk(cx, cy, cz);
            if (chunk != null)
            {
                chunk.SetVoxel(x, y, z, voxel);
                chunk.SetOverlayVoxel(x, y, z, overlay);
                chunk.MarkForRebuild();
                
                if (TileRegistry.GetTile(voxel.tile_id).IsVisible() == false)
                {
                    if (x % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx - 1, cy, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild(false, false);
                    }
                    if (x % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx + 1, cy, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild(false, false);
                    }

                    if (y % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy - 1, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild(false, false);
                    }
                    if (y % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy + 1, cz);
                        if (chunk2 != null) chunk2.MarkForRebuild(false, false);
                    }

                    if (z % Constants.CHUNK_SIZE == 0)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy, cz - 1);
                        if (chunk2 != null) chunk2.MarkForRebuild(false, false);
                    }
                    if (z % Constants.CHUNK_SIZE == Constants.CHUNK_SIZE - 1)
                    {
                        Chunk chunk2 = chunkManager.TryGetChunk(cx, cy, cz + 1);
                        
                        if (chunk2 != null) chunk2.MarkForRebuild(false, false);
                    }
                }
                
            }
        }

        public TileRaytraceResult RayTraceTiles(Vector3 start, Vector3 end, TileRayTraceType type, bool needsSolid = true)
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

            float length = Vector3.Distance(end, start);

            Vector3 raypos = new Vector3(start.X, start.Y, start.Z);
            Vector3 raydir = (end - start) / length;
            
            RayBox raybox = new RayBox
            {
                Min = new Vector3(),
                Max = new Vector3()
            };
            for (int ii = 0; ii < 10; ii++)
            {
                
                if (Vector3.Distance(raypos, start) > length)
                {
                    break;
                }
                pos.SetPosition(raypos.X, raypos.Y, raypos.Z);

                raybox.Min.X = pos.x;
                raybox.Min.Y = pos.y;
                raybox.Min.Z = pos.z;
                raybox.Max.X = pos.x + 1;
                raybox.Max.Y = pos.y + 1;
                raybox.Max.Z = pos.z + 1;
               
                RayIntersection intersection = Raytracing.IntersectBox(start, raydir, raybox);
                raypos.X = start.X;
                raypos.Y = start.Y;
                raypos.Z = start.Z;
                raypos += (raydir * (intersection.lambda.Y + 0.01f));


                TileData data = GetVoxel(pos);

                Tile tile = TileRegistry.GetTile(data.tile_id);
                if (tile != null)
                {
                    if (tile.IsVisible())
                    {
                        if (!needsSolid || (needsSolid && tile.solid && tile.BlocksMovement()))
                        if (tile.GetRayTraceType() == type)
                        {
                            Vector3 dir = end - start;
                            dir = dir / length;
                            float min = float.MaxValue;
                            bool hit = false;
                            RayIntersection r = new RayIntersection();
                            foreach (RayBox box in tile.GetCollisionBoxes(data)) {
                                RayBox b2 = new RayBox
                                {
                                    Min = box.Min + new Vector3(pos.x, pos.y, pos.z),
                                    Max = box.Max + new Vector3(pos.x, pos.y, pos.z)
                                };

                                if (Raytracing.DoesCollisionOccur(start, dir, b2, new Quaternion()))
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
                                return new TileRaytraceResult(r, pos, data, start + (dir * r.lambda.X));
                            }
                        }
                        
                    }
                }
            }

            return null;
        }
        public Vector4 GetSkyColor()
        {
            float dot = Vector3.Dot(Vector3.Down, sunLook);
            if (dot < 0.05) dot = 0.1f;

            float sky_r = Color.CornflowerBlue.R / 255.0f;
            float sky_g = Color.CornflowerBlue.G / 255.0f;
            float sky_b = Color.CornflowerBlue.B / 255.0f;

            if (dot <= 0.5f)
            {
                float lerp = 0.5f - dot;
                sky_r = MathHelper.Lerp(sky_r, Color.OrangeRed.R / 255.0f, lerp);
                sky_g = MathHelper.Lerp(sky_g, Color.OrangeRed.G / 255.0f, lerp);
                sky_b = MathHelper.Lerp(sky_b, Color.OrangeRed.B / 255.0f, lerp);
            }

            SkyColor.X = sky_r * dot;
            SkyColor.Y = sky_g * dot;
            SkyColor.Z = sky_b * dot;
            SkyColor.W = 1;
            return SkyColor;
        }
    }
}
