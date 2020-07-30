using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Inignoto.Utilities;
using Inignoto.Settings;

namespace Inignoto.World.Chunk
{
    class ChunkManager
    {
        private readonly Dictionary<Vector3, Chunk> chunks;
        private int current_x, current_y, current_z;
        private int last_x, last_y, last_z;

        private readonly World world;

        public ChunkManager(World world)
        {
            chunks = new Dictionary<Vector3, Chunk>();
            this.world = world;
        }

        public void BeginUpdate(Vector3 camera)
        {
            current_x = (int)System.Math.Floor(camera.X / Constants.CHUNK_SIZE);
            current_y = (int)System.Math.Floor(camera.Y / Constants.CHUNK_SIZE);
            current_z = (int)System.Math.Floor(camera.Z / Constants.CHUNK_SIZE);

            if (last_x != current_x || last_y != current_y || last_z != current_z)
            {
                last_x = current_x;
                last_y = current_y;
                last_z = current_z;
                Update();
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
            }
        }

        public void TryUnloadChunk(int x, int y, int z)
        {
            int H_VIEW = Settings.Settings.HORIZONTAL_VIEW;
            int V_VIEW = Settings.Settings.VERTICAL_VIEW;
            if (System.Math.Abs(x - current_x) > H_VIEW ||
                System.Math.Abs(y - current_y) > V_VIEW ||
                System.Math.Abs(z - current_z) > H_VIEW)
            {
                chunks.Remove(new Vector3(x, y, z));
            }
        }

        public Chunk TryGetChunk(int x, int y, int z)
        {
            chunks.TryGetValue(new Vector3(x, y, z), out Chunk chunk);
            return chunk;
        }

        private void Update()
        {
            int H_VIEW = Settings.Settings.HORIZONTAL_VIEW;
            int V_VIEW = Settings.Settings.VERTICAL_VIEW;
            
            foreach (Vector3 vec in chunks.Keys)
            {
                TryUnloadChunk((int)vec.X, (int)vec.Y, (int)vec.Z);
            }

            for (int x = -H_VIEW; x < H_VIEW; x++)
            {
                for (int y = -V_VIEW; y < V_VIEW; y++)
                {
                    for (int z = -H_VIEW; z < H_VIEW; z++)
                    {
                        int X = x - current_x;
                        int Y = y - current_y;
                        int Z = z - current_z;
                        TryAddChunk(X, Y, Z);
                    }
                }
            }
        }

    }
}
