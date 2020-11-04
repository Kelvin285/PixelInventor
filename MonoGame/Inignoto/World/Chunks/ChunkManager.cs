using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Inignoto.Utilities;
using Inignoto.GameSettings;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Graphics.World;
using Inignoto.World.Generator;
using Inignoto.Graphics.Mesh;
using Inignoto.Effects;
using System;
using System.Diagnostics;
using System.Reflection.Metadata.Ecma335;
using System.Net.NetworkInformation;
using System.Threading;
using System.Threading.Tasks;
using System.Linq;

namespace Inignoto.World.Chunks
{
    public class ChunkManager
    {
        private readonly ChunkRenderer chunkRenderer;

        private readonly Dictionary<Vector3, Chunk> chunks;
        private List<Chunk> chunksToBuild;
        public readonly List<Chunk> rendering;
        public readonly List<Chunk> updating;
        private List<Chunk> buildQueue;
        private List<Chunk> building;

        public int current_x { get; private set; }
        public int current_y { get; private set; }
        public int current_z { get; private set; }
        private int last_x, last_y, last_z;

        public Vector3 current_xyz { get; private set; }

        private readonly World world;

        private bool start = false;

        private bool modifiedChunks = false;

        
        public ChunkManager(World world)
        {
            chunks = new Dictionary<Vector3, Chunk>();
            this.world = world;
            chunkRenderer = new ChunkRenderer();
            chunksToBuild = new List<Chunk>();
            //chunksToRerender = new List<Chunk>();
            buildQueue = new List<Chunk>();
            current_xyz = new Vector3(0, 0, 0);
            rendering = new List<Chunk>();
            updating = new List<Chunk>();
            building = new List<Chunk>();
        }

        public void GenerateChunks(ChunkGenerator generator)
        {
            lock(chunksToBuild)
            {
                chunksToBuild.Sort();
                if (chunksToBuild.Count > 2)
                {
                    List<Chunk> a = new List<Chunk>();
                    Parallel.ForEach(chunksToBuild.Cast<Chunk>(), chunk =>
                    {
                        if (chunk != null)
                        {
                            generator.GenerateChunk(chunk);

                            chunk.SetGenerated();
                            buildQueue.Add(chunk);
                            a.Add(chunk);
                        }
                    });
                    for (int i = 0; i < a.Count; i++)
                    {
                        chunksToBuild.Remove(a[i]);
                    }
                }
            }
            
            

            BuildChunks();

        }

        public void BuildChunks()
        {
            
            if (modifiedChunks || current_x != last_x || current_y != last_y || current_z != last_z)
            {
                lock (building)
                {
                    building.Clear();
                    lock (rendering)
                    {
                        for (int i = 0; i < rendering.Count; i++)
                        {
                            building.Add(rendering[i]);
                        }
                    }
                }
                
            }
            lock (building)
            if (building.Count > 2)
            {
                Parallel.ForEach(building.Cast<Chunk>(), chunk => {
                    if (chunk != null)
                    {
                        if (chunk.NeedsToRebuild() && !chunk.Empty)
                        {
                            chunk.BuildMesh();
                        } else if (!chunk.Empty)
                        {
                            if (world.random.Next(100) == 0)
                            {
                                chunk.BuildMesh();
                            }
                        }
                    }
                });
            }
            
        }

        public void BeginUpdate(Vector3 camera)
        {
            current_x = (int)System.Math.Floor(camera.X / Constants.CHUNK_SIZE);
            current_y = (int)System.Math.Floor(camera.Y / Constants.CHUNK_SIZE);
            current_z = (int)System.Math.Floor(camera.Z / Constants.CHUNK_SIZE);

            current_xyz = new Vector3(current_x, current_y, current_z);

            if (last_x != current_x || last_y != current_y || last_z != current_z || !start)
            {
                last_x = current_x;
                last_y = current_y;
                last_z = current_z;
                Update();
                start = true;
            }
        }

        public Chunk TryAddChunk(int x, int y, int z)
        {
            Vector3 position = new Vector3(x, y, z);
            if (chunks.ContainsKey(position))
            {
                Chunk c = null;
                chunks.TryGetValue(position, out c);
                return c;
            } else
            {
                Chunk chunk = new Chunk(x, y, z, world);
                chunks.Add(position, chunk);
                lock(chunksToBuild)
                chunksToBuild.Add(chunk);
                modifiedChunks = true;
                return chunk;
            }
        }

        public bool TryUnloadChunk(int x, int y, int z)
        {
            int H_VIEW = GameSettings.Settings.HORIZONTAL_VIEW;
            int V_VIEW = GameSettings.Settings.VERTICAL_VIEW;
            if (System.Math.Abs(x - current_x) > H_VIEW + 1 ||
                    System.Math.Abs(y - current_y) > V_VIEW + 1 ||
                    System.Math.Abs(z - current_z) > H_VIEW + 1)
            {
                if (System.Math.Abs(x - (current_x + (int)((world.radius * 4) / Constants.CHUNK_SIZE))) > H_VIEW + 1)
                {
                    if (System.Math.Abs(x - (current_x - (int)((world.radius * 4) / Constants.CHUNK_SIZE))) > H_VIEW + 1)
                    {
                        lock (chunks)
                            chunks.Remove(new Vector3(x, y, z));
                        modifiedChunks = true;
                        return true;

                    }
                }
            }
            return false;
        }

        public Chunk TryGetChunk(int x, int y, int z)
        {
            if (chunks.ContainsKey(new Vector3(x, y, z)))
            {
                chunks.TryGetValue(new Vector3(x, y, z), out Chunk chunk);
                return chunk;
            }
            return null;
        }

        public void UpdateChunks() {
            if (modifiedChunks || current_x != last_x || current_y != last_y || current_z != last_z)
            {
                updating.Clear();
                lock(chunks)
                foreach (Chunk chunk in chunks.Values)
                {
                    if (!TryUnloadChunk(chunk.GetX(), chunk.GetY(), chunk.GetZ()))
                        updating.Add(chunk);
                }
                updating.Sort();
            }
            if (updating.Count > 2)
            Parallel.ForEach(updating.Cast<Chunk>(), chunk =>
            {
                if (chunk != null)
                {
                    float distance = Vector3.Distance(new Vector3(chunk.GetX(), chunk.GetY(), chunk.GetZ()), new Vector3(current_x, current_y, current_z));
                    chunk.Tick(distance);
                }
            });
        }

        private void Update()
        {
            int H_VIEW = GameSettings.Settings.HORIZONTAL_VIEW;
            int V_VIEW = GameSettings.Settings.VERTICAL_VIEW;


            int rad = (int)((world.radius * 4.0f) / Constants.CHUNK_SIZE);

            for (int y = -V_VIEW; y < V_VIEW; y++)
            {
                for (int z = -H_VIEW; z < H_VIEW; z++)
                {
                    for (int x = -H_VIEW; x < H_VIEW; x++)
                    {
                        int X = x + current_x;
                        int Y = y + current_y;
                        int Z = z + current_z;

                        int W = X;
                        if (W < 0) W += rad;
                        if (W >= rad) W -= rad;
                        lock (chunks)
                            TryAddChunk(W, Y, Z);
                    }
                }

            }
            

        }

        public bool IsChunkWithinFrustum(Chunk chunk)
        {
            if (GameResources.drawing_shadows) return true;
            Vector3 min = new Vector3(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE);
            Vector3 max = min + new Vector3(Constants.CHUNK_SIZE);
            BoundingBox box = new BoundingBox(min, max);
            if (Inignoto.game.camera.frustum.Intersects(box)) return true;

            min = new Vector3(chunk.GetX() * Constants.CHUNK_SIZE - chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE);
            max = min + new Vector3(Constants.CHUNK_SIZE);
            box = new BoundingBox(min, max);
            if (Inignoto.game.camera.frustum.Intersects(box)) return true;

            min = new Vector3(chunk.GetX() * Constants.CHUNK_SIZE + chunk.GetWorld().radius * 4 + chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE);
            max = min + new Vector3(Constants.CHUNK_SIZE);
            box = new BoundingBox(min, max);
            if (Inignoto.game.camera.frustum.Intersects(box)) return true;

            return false;
        }

        public void Render(GraphicsDevice device, GameEffect effect)
        {

            if (modifiedChunks || current_x != last_x || current_y != last_y || current_z != last_z)
            {
                lock (rendering)
                {
                    rendering.Clear();
                    foreach (Chunk chunk in chunks.Values)
                    {
                        if (!chunk.Empty && !chunk.Occluded)
                        rendering.Add(chunk);
                    }
                }
            }
                

            List<Chunk> waterRender = new List<Chunk>();
            List<Chunk> transparentRender = new List<Chunk>();
            
            for (int i = 0; i < rendering.Count; i++)
            {
                
                if (TryUnloadChunk(rendering[i].GetX(), rendering[i].GetY(), rendering[i].GetZ()))
                {
                    continue;
                }
                

                if (!IsChunkWithinFrustum(rendering[i]))
                {
                    continue;
                }


                if (rendering[i].NeedsToRebuild())
                {
                    if (Vector3.Distance(current_xyz, rendering[i].cpos) <= Constants.ACTIVE_CHUNK_DISTANCE)
                    {
                        rendering[i].BuildMesh();
                    }
                }

                chunkRenderer.RenderChunk(device, effect, rendering[i]);

                if (rendering[i].waterMesh != null || rendering[i].secondWaterMesh != null)
                {
                    waterRender.Add(rendering[i]);
                }

                if (rendering[i].transparencyMesh != null || rendering[i].secondTransparencyMesh != null)
                {
                    transparentRender.Add(rendering[i]);
                }
            }
            GameResources.effect.Water = true;
            for (int i = 0; i < waterRender.Count; i++)
            {
                chunkRenderer.RenderChunk(device, effect, waterRender[i], true);
            }
            GameResources.effect.Water = false;
            for (int i = 0; i < transparentRender.Count; i++)
            {
                chunkRenderer.RenderChunk(device, effect, transparentRender[i], false, true);
            }

        }

        public void QueueForRerender(Chunk chunk)
        {
            if (!buildQueue.Contains(chunk))
            {
                buildQueue.Add(chunk);
            }
        }
    }
}
