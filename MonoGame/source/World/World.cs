using Inignoto.World.Chunks;
using Microsoft.Xna.Framework;
using Inignoto.Utilities;
using Inignoto.Graphics.World;
using Microsoft.Xna.Framework.Graphics;
using System.Threading;

namespace Inignoto.World
{
    class World
    {
        public struct TilePos {
            public int x, y, z;
            public TilePos(int x = 0, int y = 0, int z = 0)
            {
                this.x = x;
                this.y = y;
                this.z = z;
            }
        }

        private readonly ChunkManager chunkManager;
        public readonly WorldProperties properties;
        public World()
        {
            chunkManager = new ChunkManager(this);
            properties = new WorldProperties();
        }

        public void UpdateChunkGeneration()
        {
            chunkManager.GenerateChunks(properties.generator);
        }

        public void Update(Vector3 camera_position)
        {
            chunkManager.BeginUpdate(camera_position);
        }

        public void Render(GraphicsDevice device, BasicEffect effect)
        {
            chunkManager.Render(device, effect);
        }

        public ChunkManager GetChunkManager()
        {
            return this.chunkManager;
        }

        public Chunks.Chunk TryGetChunk(TilePos pos)
        {
            return chunkManager.TryGetChunk(pos.x / Constants.CHUNK_SIZE, pos.y / Constants.CHUNK_SIZE, pos.z / Constants.CHUNK_SIZE);
        }
    }
}
