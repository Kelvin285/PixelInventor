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

namespace Inignoto.World.Chunks
{
    public class ChunkManager
    {
        private readonly ChunkRenderer chunkRenderer;

        private readonly Dictionary<long, Chunk> chunks;
        private readonly List<StructureChunk> structureChunks;
        private readonly List<long> structureChunkIndices;

        private List<Chunk> chunksToBuild;
        public readonly List<Chunk> rendering;
        public readonly List<Chunk> updating;

        public List<Chunk> waterRender;
        public List<Chunk> transparentRender;

        public int current_x { get; private set; }
        public int current_y { get; private set; }
        public int current_z { get; private set; }
        private int last_x, last_y, last_z;

        public Vector3 current_xyz;

        private readonly World world;

        private bool start = false;

        private bool modifiedChunks = false;

        
        public ChunkManager(World world)
        {
            chunks = new Dictionary<long, Chunk>();
            structureChunks = new List<StructureChunk>();
            structureChunkIndices = new List<long>();

            this.world = world;
            chunkRenderer = new ChunkRenderer();
            chunksToBuild = new List<Chunk>();
            //chunksToRerender = new List<Chunk>();
            current_xyz = new Vector3(0, 0, 0);
            rendering = new List<Chunk>();
            updating = new List<Chunk>();
            waterRender = new List<Chunk>();
            transparentRender = new List<Chunk>();
        }

        public void GenerateChunks(ChunkGenerator generator)
        {
            if (Settings.PARALLEL_CHUNK_GENERATION)
            {
                //lock(chunksToBuild)
                {
                    List<Chunk> a = new List<Chunk>();
                    try
                    {
                        for (int i = 0; i < chunksToBuild.Count; i++)
                        {
                            Chunk chunk = chunksToBuild[i];
                            a.Add(chunk);
                            chunksToBuild.Remove(chunk);
                            if (i >= chunksToBuild.Count) break;
                        }
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.Message);
                    }

                    a.Sort();
                    Parallel.ForEach(a.Cast<Chunk>(), chunk =>
                    {
                        if (chunk != null)
                        {
                            if (chunk.NeedsToGenerate())
                            {
                                generator.GenerateChunk(chunk);

                                chunk.SetGenerated();
                                chunk.BuildMesh();
                            }
                            

                            //buildQueue.Add(chunk);
                        }
                    });
                }
            } else
            {
                chunksToBuild.Sort();
                if (chunksToBuild.Count > 0)
                {
                    Chunk chunk = chunksToBuild[0];
                    if (chunk != null)
                    {
                        generator.GenerateChunk(chunk);

                        chunk.SetGenerated();
                        chunk.BuildMesh();
                    }
                    chunksToBuild.Remove(chunk);
                }
            }
            
        }

        public void StructureGeneration()
        {
            try
            {
                structureChunks.Sort();
                for (int i = 0; i < structureChunks.Count; i++)
                {
                    StructureChunk schunk = structureChunks[i];
                    if (schunk == null) continue;
                    try
                    {
                        Chunk chunk = TryGetChunk(schunk.x, schunk.y, schunk.z);
                        if (!chunk.NeedsToGenerate())
                        {
                            foreach (var pos in schunk.tiles.Keys)
                            {
                                //x + y * size + z * size * size
                                //x = index % size
                                //y = (index - x) / size
                                int x = pos % Constants.CHUNK_SIZE;
                                int y = ((pos - x) / Constants.CHUNK_SIZE) % Constants.CHUNK_SIZE;
                                int z = (pos - x - (y * Constants.CHUNK_SIZE)) / (Constants.CHUNK_SIZE * Constants.CHUNK_SIZE);
                                chunk.SetVoxel(x, y, z, schunk.tiles[pos]);
                            }
                            chunk.BuildMesh();
                        }


                        structureChunks.Remove(schunk);
                    }
                    catch (NullReferenceException e)
                    {
                        Console.WriteLine(e);
                        break;
                    }

                }
            }catch (NullReferenceException e)
            {
                Console.WriteLine(e.Message);
            }
        }

        public void BeginUpdate(Vector3 camera)
        {
            current_x = (int)System.Math.Floor(camera.X / Constants.CHUNK_SIZE);
            current_y = (int)System.Math.Floor(camera.Y / Constants.CHUNK_SIZE);
            current_z = (int)System.Math.Floor(camera.Z / Constants.CHUNK_SIZE);

            current_xyz.X = current_x;
            current_xyz.Y = current_y;
            current_xyz.Z = current_z;

            if (last_x != current_x || last_y != current_y || last_z != current_z || !start)
            {
                last_x = current_x;
                last_y = current_y;
                last_z = current_z;
                Update();
                start = true;
            }
        }

        //width = world size
        //height = infinite
        //length = world size
        public long GetIndexFor(int x, int y, int z)
        {
            float diameter = world.radius;
            long chunkWidth = (int)(diameter / Constants.CHUNK_SIZE) * 2;
            long index = x + y * chunkWidth + z * chunkWidth * int.MaxValue;
            return index;
        }

        public Chunk TryAddChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);
            if (chunks.ContainsKey(index))
            {
                Chunk c = null;
                chunks.TryGetValue(index, out c);
                return c;
            } else
            {
                Chunk chunk = new Chunk(x, y, z, world);
                chunks.Add(index, chunk);
                //lock(chunksToBuild)
                chunksToBuild.Add(chunk);
                modifiedChunks = true;
                return chunk;
            }
        }

        public bool TryUnloadChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);

            if (System.Math.Abs(x - current_x) > Settings.HORIZONTAL_VIEW + 1 ||
                    System.Math.Abs(y - current_y) > Settings.VERTICAL_VIEW + 1 ||
                    System.Math.Abs(z - current_z) > Settings.HORIZONTAL_VIEW + 1)
            {
                if (System.Math.Abs(x - (current_x + (int)((world.radius * 4) / Constants.CHUNK_SIZE))) > Settings.HORIZONTAL_VIEW + 1)
                {
                    if (System.Math.Abs(x - (current_x - (int)((world.radius * 4) / Constants.CHUNK_SIZE))) > Settings.HORIZONTAL_VIEW + 1)
                    {
                        chunks.TryGetValue(index, out Chunk chunk);
                        chunk.Dispose();
                        chunks.Remove(index);
                        modifiedChunks = true;
                        return true;

                    }
                }
            }
            return false;
        }


        public bool HasStructureChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);
            if (structureChunkIndices.Contains(index))
            {
                return true;
            }
            return false;
        }
        public StructureChunk GetOrCreateStructureChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);
            if (structureChunkIndices.Contains(index))
            {
                int i = structureChunkIndices.IndexOf(index);
                return structureChunks[i];
            }
            StructureChunk chunk = new StructureChunk(x, y, z, this);
            structureChunks.Add(chunk);
            structureChunkIndices.Add(index);
            return chunk;
        }

        public Chunk TryGetChunk(int x, int y, int z)
        {
            long index = GetIndexFor(x, y, z);

            if (chunks.TryGetValue(index, out Chunk chunk))
            {
                return chunk;
            }
            return null;
        }

        public void UpdateChunks() {
            if (modifiedChunks || current_x != last_x || current_y != last_y || current_z != last_z)
            {
                updating.Clear();
                //lock(chunks)
                for (int i = 0; i < rendering.Count; i++)
                {
                    if (Vector3.Distance(rendering[i].cpos, current_xyz) <= Constants.ACTIVE_CHUNK_DISTANCE)
                   updating.Add(rendering[i]);
                }
                updating.Sort();
            }
            for (int i = 0; i < updating.Count; i++)
            {
                Chunk chunk = updating[i];
                if (chunk != null)
                {
                    float distance = (float)IMathHelper.Distance3(chunk.GetX(), chunk.GetY(), chunk.GetZ(), current_x, current_y, current_z);
                    chunk.Tick(distance);
                }
            }
            /*
            if (updating.Count > 2)
            Parallel.ForEach(updating.Cast<Chunk>(), chunk =>
            {
                if (chunk != null)
                {
                    float distance = (float)IMathHelper.Distance3(chunk.GetX(), chunk.GetY(), chunk.GetZ(), current_x, current_y, current_z);
                    chunk.Tick(distance);
                }
            });
            */
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
            waterRender.Clear();
            transparentRender.Clear();
            if (modifiedChunks || current_x != last_x || current_y != last_y || current_z != last_z)
            {
                ////lock (rendring)
                {
                    rendering.Clear();
                    foreach (Chunk chunk in chunks.Values)
                    {
                        if (!chunk.Empty)
                        rendering.Add(chunk);
                    }
                }
            }
            
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
                    if (Vector3.Distance(current_xyz, rendering[i].cpos) <= Constants.CHUNK_LIGHT_DISTANCE)
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
            waterRender.Clear();
            transparentRender.Clear();
        }
    }
}
