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

namespace Inignoto.World.Chunks
{
    public class ChunkManager
    {
        private readonly ChunkRenderer chunkRenderer;

        private readonly Dictionary<Vector3, Chunk> chunks;
        private List<Chunk> chunksToBuild;
        public readonly List<Chunk> chunksToRerender;
        public readonly List<Chunk> secondaryChunksToRerender;

        public readonly List<Chunk> rendering;

        private int current_x, current_y, current_z;
        private int last_x, last_y, last_z;

        private readonly World world;

        private bool start = false;

        public ChunkManager(World world)
        {
            chunks = new Dictionary<Vector3, Chunk>();
            this.world = world;
            chunkRenderer = new ChunkRenderer();
            chunksToBuild = new List<Chunk>();
            chunksToRerender = new List<Chunk>();
            secondaryChunksToRerender = new List<Chunk>();

            rendering = new List<Chunk>();
        }

        public void GenerateChunks(ChunkGenerator generator)
        {
            float distance = float.MaxValue;
            Chunk closest = null;

            int H_VIEW = GameSettings.Settings.HORIZONTAL_VIEW;
            int V_VIEW = GameSettings.Settings.VERTICAL_VIEW;

            for (int i = 0; i < chunksToBuild.Count; i++)
            {
                Chunk c = chunksToBuild[i];
                if (c == null) continue;
                if (System.Math.Abs(c.GetX() - current_x) > H_VIEW + 1 ||
                    System.Math.Abs(c.GetY() - current_y) > V_VIEW + 1 ||
                    System.Math.Abs(c.GetZ() - current_z) > H_VIEW + 1) {

                    if (System.Math.Abs(c.GetX() - (current_x + (int)((world.radius * 4) / Constants.CHUNK_SIZE))) > H_VIEW + 1)
                    {
                        if (System.Math.Abs(c.GetX() - (current_x - (int)((world.radius * 4) / Constants.CHUNK_SIZE))) > H_VIEW + 1)
                        {

                            chunksToBuild.Remove(c);
                            continue;
                        }
                    }

                }
                if (chunksToBuild[i].NeedsToGenerate())
                {
                    //generator.GenerateChunk(chunksToBuild[i]);
                    //chunksToBuild[i].SetGenerated();
                    Chunk chunk = chunksToBuild[i];
                    
                    float dist = Vector3.Distance(new Vector3(chunk.GetX(), chunk.GetY(), chunk.GetZ()), new Vector3(current_x, current_y, current_z));
                    if (dist < distance) {
                        distance = dist;
                        closest = chunk;
                    }

                    dist = Vector3.Distance(new Vector3(chunk.GetX(), chunk.GetY(), chunk.GetZ()), new Vector3(current_x + (int)((world.radius * 4) / Constants.CHUNK_SIZE), current_y, current_z));
                    if (dist < distance)
                    {
                        distance = dist;
                        closest = chunk;
                    }

                    dist = Vector3.Distance(new Vector3(chunk.GetX(), chunk.GetY(), chunk.GetZ()), new Vector3(current_x - (int)((world.radius * 4) / Constants.CHUNK_SIZE), current_y, current_z));
                    if (dist < distance)
                    {
                        distance = dist;
                        closest = chunk;
                    }

                } else
                {
                    chunksToBuild.Remove(chunksToBuild[i]);
                }
            }
            if (closest != null)
            {
                generator.GenerateChunk(closest);
                closest.SetGenerated();
                chunksToBuild.Remove(closest);
            }

            for (int i = 0; i < chunksToRerender.Count; i++)
            {
                Chunk chunk = chunksToRerender[i];

                if (chunk.NeedsToRebuild())
                {
                    chunk.secondMesh = ChunkBuilder.BuildMeshForChunk(Inignoto.game.GraphicsDevice, chunk);
                    if (chunk.secondMesh != null)
                        chunk.secondMesh.SetPosition(new Microsoft.Xna.Framework.Vector3(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    chunk.FinishRebuilding();
                    if (chunk.mesh != null)
                    {
                        chunk.mesh.Dispose();
                    }

                    if (chunk.waterMesh != null)
                    {
                        chunk.waterMesh.Dispose();
                    }


                }
                chunksToRerender.Remove(chunksToRerender[i]);
            }

            for (int i = 0; i < secondaryChunksToRerender.Count; i++)
            {
                
                Chunk chunk = secondaryChunksToRerender[i];

                if (!chunk.NeedsToRebuild())
                {
                    chunk.mesh = ChunkBuilder.BuildMeshForChunk(Inignoto.game.GraphicsDevice, chunk);
                    if (chunk.secondWaterMesh != null)
                        chunk.secondWaterMesh.SetPosition(new Microsoft.Xna.Framework.Vector3(chunk.GetX() * Constants.CHUNK_SIZE, chunk.GetY() * Constants.CHUNK_SIZE, chunk.GetZ() * Constants.CHUNK_SIZE));

                    if (chunk.waterMesh != null)
                    {
                        chunk.waterMesh.Dispose();
                    }

                    chunk.transparentRebuild = false;
                }

                secondaryChunksToRerender.Remove(secondaryChunksToRerender[i]);
                if (i > 2) break;
            }
        }

        public void BeginUpdate(Vector3 camera)
        {
            current_x = (int)System.Math.Floor(camera.X / Constants.CHUNK_SIZE);
            current_y = (int)System.Math.Floor(camera.Y / Constants.CHUNK_SIZE);
            current_z = (int)System.Math.Floor(camera.Z / Constants.CHUNK_SIZE);
            
            if (last_x != current_x || last_y != current_y || last_z != current_z || !start)
            {
                last_x = current_x;
                last_y = current_y;
                last_z = current_z;
                Update();
                start = true;
            }
        }

        public void TryAddChunk(int x, int y, int z)
        {
            Vector3 position = new Vector3(x, y, z);
            if (chunks.ContainsKey(position))
            {
                return;
            } else
            {
                Chunk chunk = new Chunk(x, y, z, world);
                chunks.Add(position, chunk);
                chunksToBuild.Add(chunk);
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
                        chunks.Remove(new Vector3(x, y, z));
                        return true;

                    }
                }
            }
            return false;
        }

        public Chunk TryGetChunk(int x, int y, int z)
        {
            chunks.TryGetValue(new Vector3(x, y, z), out Chunk chunk);
            return chunk;
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

        public void Render(GraphicsDevice device, GameEffect effect)
        {
            rendering.Clear();
            foreach (Chunk chunk in chunks.Values)
            {
                rendering.Add(chunk);
            }

            for (int i = 0; i < rendering.Count; i++)
            {
                chunkRenderer.RenderChunk(device, effect, rendering[i]);
                TryUnloadChunk(rendering[i].GetX(), rendering[i].GetY(), rendering[i].GetZ());
            }
            
            GameResources.effect.Water = true;
            for (int i = 0; i < rendering.Count; i++)
            {
                chunkRenderer.RenderChunk(device, effect, rendering[i], true);
            }
            GameResources.effect.Water = false;

        }



    }
}
