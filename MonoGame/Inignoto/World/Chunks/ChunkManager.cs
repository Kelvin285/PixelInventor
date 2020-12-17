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
using Microsoft.VisualBasic.CompilerServices;
using Inignoto.Math;
using Inignoto.Tiles;

namespace Inignoto.World.Chunks
{
    public class ChunkManager
    {
        private readonly ChunkRenderer chunkRenderer;

        private readonly Dictionary<long, Chunk> chunks;
        private readonly Dictionary<long, StructureChunk> structureChunks;

        public List<WeakReference<Chunk>> waterRender;
        public List<WeakReference<Chunk>> transparentRender;

        public int current_x { get; private set; }
        public int current_y { get; private set; }
        public int current_z { get; private set; }
        private int last_x, last_y, last_z;

        public Vector3 current_xyz;

        private readonly World world;

        private bool start = false;

        public ChunkManager(World world)
        {
            chunks = new Dictionary<long, Chunk>();
            structureChunks = new Dictionary<long, StructureChunk>();

            this.world = world;
            chunkRenderer = new ChunkRenderer();
            //chunksToRerender = new List<Chunk>();
            current_xyz = new Vector3(0, 0, 0);
            waterRender = new List<WeakReference<Chunk>>();
            transparentRender = new List<WeakReference<Chunk>>();
        }


        public bool HasStructureChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);
            lock (structureChunks)
                return structureChunks.ContainsKey(index);
        }
        public StructureChunk GetOrCreateStructureChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);
            lock (structureChunks)
            {
                if (structureChunks.TryGetValue(index, out StructureChunk schunk))
                {
                    return schunk;
                }
                StructureChunk chunk = new StructureChunk(x, y, z, this);
                structureChunks.Add(index, chunk);

                return chunk;
            }
        }

        public Chunk TryGetChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);

            if (chunks.ContainsKey(index))
                if (chunks.TryGetValue(index, out Chunk chunk))
                {
                    return chunk;
                }
            return null;
        }

        public void RebuildChunks()
        {
            Chunk[] ar = null;
            lock (chunks)
            {
                ar = chunks.Values.ToArray();
            }
            Array.Sort(ar);

            for (int i = 0; i < ar.Length; i++)
            {
                if (current_x != last_x || current_y != last_y || current_z != last_z) break;
                lock (ar)
                if (ar[i].NeedsToGenerate())
                {
                    Chunk chunk = ar[i];

                    if (chunk != null)
                        {
                            chunk.SetGenerated();
                        }
                }

                BuildChunk(ar, i);
            }

        }

        private void BuildChunk(Chunk[] ar, int i)
        {
            Chunk chunk = ar[i];

            if (chunk.LightRebuild)
            {
                chunk.UpdateLights();
            }

            if (chunk.NeedsToRebuild() && chunk.NotEmpty)
            {
                chunk.BuildMesh();
            }
        }

        public void RemoveStructureChunk(int x, int y, int z)
        {
            lock (structureChunks)
            {
                structureChunks.Remove(GetIndexFor(x, y, z));
            }
        }

        public void StructureGeneration()
        {
            lock (structureChunks)
            {
                foreach (var key in structureChunks.Keys)
                {
                    if (!structureChunks.TryGetValue(key, out StructureChunk schunk)) continue;
                    if (schunk == null) continue;

                    BuildStructureChunk(key, schunk);
                }
            }
            

        }

        private bool BuildStructureChunk(long key, StructureChunk schunk)
        {
            Chunk chunk = TryGetChunk(schunk.x, schunk.y, schunk.z);
            if (MathF.Abs(schunk.x - current_x) >= Settings.HORIZONTAL_VIEW * 1.5f || MathF.Abs(schunk.z - current_z) >= Settings.HORIZONTAL_VIEW * 1.5f
                || MathF.Abs(schunk.cpos.Y - current_y) >= Settings.VERTICAL_VIEW * 1.5f)
            {
                structureChunks.Remove(key);
                return false;
            }

            if (chunk != null)
            {
                bool rebuild = false;
                if (!chunk.NeedsToGenerate())
                {
                    bool modified = chunk.modified;
                    if (schunk != null)
                    {
                        if (schunk.done == false)
                        {
                            for (int x = 0; x < Constants.CHUNK_SIZE; x++)
                            {
                                for (int y = 0; y < Constants.CHUNK_SIZE; y++)
                                {
                                    for (int z = 0; z < Constants.CHUNK_SIZE; z++)
                                    {
                                        if (schunk.GetTile(x, y, z) != TileRegistry.AIR.DefaultData)
                                            chunk.SetVoxel(x, y, z, schunk.GetTile(x, y, z));
                                    }
                                }
                            }
                            rebuild = true;
                        }

                        schunk.done = true;
                    }
                    chunk.modified = modified;
                    if (rebuild)
                        chunk.MarkForRebuild();
                    structureChunks.Remove(key);
                    return true;
                }
            }
            return false;
        }

        public void UpdatePosition()
        {
            current_x = (int)System.Math.Floor(Inignoto.game.camera.position.X / Constants.CHUNK_SIZE);
            current_y = (int)System.Math.Floor(Inignoto.game.camera.position.Y / Constants.CHUNK_SIZE);
            current_z = (int)System.Math.Floor(Inignoto.game.camera.position.Z / Constants.CHUNK_SIZE);

            current_xyz = new Vector3(current_x, current_y, current_z);
        }

        public void BeginUpdate(Vector3 camera)
        {
            if (last_x != current_x || last_y != current_y || last_z != current_z || !start)
            {
                last_x = current_x;
                last_y = current_y;
                last_z = current_z;
                Update();
                StructureGeneration();

                start = true;
            }
        }

        public long GetIndexFor(int x, int y, int z)
        {
            float diameter = world.radius;
            long chunkWidth = (int)(diameter / Constants.CHUNK_SIZE) * 2;

            if (x < 0) x += (int)chunkWidth;
            if (x > chunkWidth) x -= (int)chunkWidth;

            long index = x + y * chunkWidth + z * chunkWidth * int.MaxValue;
            return index;
        }

        public void TryAddChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);
            if (!chunks.ContainsKey(index))
            {
                Chunk chunk = new Chunk(x, y, z, world);

                world.properties.generator.GenerateChunk(chunk);

                lock (chunks) chunks.Add(index, chunk);
            }
        }


        public bool TryUnloadChunk(Chunk chunk)
        {
            long index = GetIndexFor((int)chunk.cpos.X, (int)chunk.cpos.Y, (int)chunk.cpos.Z);

            if (chunk == null) return false;
            if (Vector2.Distance(new Vector2(chunk.cpos.X, chunk.cpos.Z), new Vector2(current_x, current_z)) >= Settings.HORIZONTAL_VIEW * 2.0f
                                || MathF.Abs(chunk.cpos.Y - current_y) >= Settings.VERTICAL_VIEW * 2f)
            {
                lock (chunks) chunks.Remove(index);

                chunk.Dispose();
                return true;
            }
            return false;
        }

        public void UpdateChunks()
        {


            Chunk[] ar = null;

            lock (chunks)
                ar = chunks.Values.ToArray();

            for (int i = 0; i < ar.Length; i++)
            {
                Chunk chunk = ar[i];
                if (chunk != null)
                {
                    
                    
                    if (chunk.ReadyToFix())
                    {
                        chunk.BuildMesh();
                        chunk.mesh_fixed = true;
                    } else
                    {
                        if (chunk.NeedsToRebuild() && chunk.NotEmpty || chunk.LightRebuild)
                        {
                            chunk.BuildMesh();
                        }
                    }

                    if (Vector3.Distance(chunk.cpos, current_xyz) <= Constants.ACTIVE_CHUNK_DISTANCE)
                    {
                        float distance = (float)IMathHelper.Distance3(chunk.GetX(), chunk.GetY(), chunk.GetZ(), current_x, current_y, current_z);
                        chunk.Tick(distance);
                    }
                    if (TryUnloadChunk(chunk))
                    {
                        continue;
                    }
                }

            }

        }

        private void Update()
        {
            int H_VIEW = Settings.HORIZONTAL_VIEW;
            int V_VIEW = Settings.VERTICAL_VIEW;


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
                        if (TryGetChunk(W, Y, Z) == null)
                        {
                            TryAddChunk(W, Y, Z);
                        }
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

        int i = 0;
        public void Render(GraphicsDevice device, GameEffect effect)
        {
            i++;
            if (i > 100)
            {
                i = 0;
                RefreshChunks();
            }
            waterRender.Clear();
            transparentRender.Clear();

            Chunk[] ar = null;
            lock (chunks)
            {
                ar = chunks.Values.ToArray();
            }
               

            for (int i = 0; i < ar.Length; i++)
            {
                Chunk chunk = ar[i];
                if (!IsChunkWithinFrustum(chunk))
                {
                    continue;
                }

                chunkRenderer.RenderChunk(device, effect, chunk);

                if (chunk.waterMesh != null)
                {
                    waterRender.Add(new WeakReference<Chunk>(chunk));
                }

                if (chunk.transparencyMesh != null)
                {
                    transparentRender.Add(new WeakReference<Chunk>(chunk));
                }
            }

            for (int i = 0; i < waterRender.Count || i < transparentRender.Count; i++)
            {
                if (i < waterRender.Count)
                {
                    GameResources.effect.Water = true;
                    waterRender[i].TryGetTarget(out Chunk chunk);
                    chunkRenderer.RenderChunk(device, effect, chunk, true);
                    GameResources.effect.Water = false;
                }
                if (i < transparentRender.Count)
                {
                    transparentRender[i].TryGetTarget(out Chunk chunk);
                    chunkRenderer.RenderChunk(device, effect, chunk, false, true);
                }
            }
        }

        internal void Dispose()
        {
            lock (chunks)
            {
                foreach (Chunk chunk in chunks.Values)
                {
                    chunk.Dispose();
                }
            }
        }

        public void RefreshChunks()
        {
            lock (chunks)
            {
                foreach (Chunk chunk in chunks.Values)
                {
                    chunk.sunlightBfsQueue.Clear();
                    chunk.sunlightRemovalBfsQueue.Clear();
                    chunk.redBfsQueue.Clear();
                    chunk.greenBfsQueue.Clear();
                    chunk.blueBfsQueue.Clear();
                    chunk.redRemovalBfsQueue.Clear();
                    chunk.greenRemovalBfsQueue.Clear();
                    chunk.blueRemovalBfsQueue.Clear();
                }
            }
        }
    }
}
