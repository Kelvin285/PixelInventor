using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using System.Collections.Generic;

namespace Inignoto.World.Chunks
{
    public class StructureChunk
    {
        public Dictionary<int, TileData> tiles;

        public readonly int x, y, z;
        public Vector3 cpos;
        public ChunkManager manager;
        public StructureChunk(int x, int y, int z, ChunkManager manager)
        {
            tiles = new Dictionary<int, TileData>();
            this.x = x;
            this.y = y;
            this.z = z;
            cpos = new Vector3(x, y, z);
            this.manager = manager;
        }

        public int GetIndex(int x, int y, int z)
        {
            return x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE;
        }

        public void SetTile(int x, int y, int z, TileData data)
        {
            if (x >= 0 && y >= 0 && z >= 0 && x < Constants.CHUNK_SIZE && y < Constants.CHUNK_SIZE && z < Constants.CHUNK_SIZE)
            {
                int index = GetIndex(x, y, z);
                if (tiles.TryGetValue(index, out TileData data2) == false)
                {
                    tiles.Add(index, data);
                } else
                {
                    tiles[index] = data;
                }
            }
            GetOrCreateAdjacent(x, y, z, out int X, out int Y, out int Z).SetTile(X, Y, Z, data);
        }

        public TileData GetTile(int x, int y, int z)
        {
            if (x >= 0 && y >= 0 && z >= 0 && x < Constants.CHUNK_SIZE && y < Constants.CHUNK_SIZE && z < Constants.CHUNK_SIZE)
            {
                int index = GetIndex(x, y, z);
                if (tiles.TryGetValue(index, out TileData data) != false)
                {
                    return data;
                }
            }
            if (HasAdjacent(x, y, z, out int X, out int Y, out int Z, out int CX, out int CY, out int CZ)) {
                return manager.GetOrCreateStructureChunk(CX, CY, CZ).GetTile(X, Y, Z);
            }

            return TileManager.AIR.DefaultData;
        }
        public bool HasAdjacent(int x, int y, int z, out int x2, out int y2, out int z2, out int CX, out int CY, out int CZ)
        {
            int X = this.x;
            int Y = this.y;
            int Z = this.z;
            while (x > Constants.CHUNK_SIZE)
            {
                x -= Constants.CHUNK_SIZE;
                X++;
            }
            while (x < 0)
            {
                x += Constants.CHUNK_SIZE;
                X--;
            }
            while (y > Constants.CHUNK_SIZE)
            {
                y -= Constants.CHUNK_SIZE;
                Y++;
            }
            while (y < 0)
            {
                y += Constants.CHUNK_SIZE;
                Y--;
            }
            while (z > Constants.CHUNK_SIZE)
            {
                z -= Constants.CHUNK_SIZE;
                Z++;
            }
            while (z < 0)
            {
                z += Constants.CHUNK_SIZE;
                Z--;
            }
            x2 = x;
            y2 = y;
            z2 = z;
            CX = X;
            CY = Y;
            CZ = Z;
            return manager.HasStructureChunk(X, Y, Z);
        }
        public StructureChunk GetOrCreateAdjacent(int x, int y, int z, out int x2, out int y2, out int z2)
        {
            int X = this.x;
            int Y = this.y;
            int Z = this.z;
            while (x > Constants.CHUNK_SIZE)
            {
                x -= Constants.CHUNK_SIZE;
                X++;
            }
            while (x < 0)
            {
                x += Constants.CHUNK_SIZE;
                X--;
            }
            while (y > Constants.CHUNK_SIZE)
            {
                y -= Constants.CHUNK_SIZE;
                Y++;
            }
            while (y < 0)
            {
                y += Constants.CHUNK_SIZE;
                Y--;
            }
            while (z > Constants.CHUNK_SIZE)
            {
                z -= Constants.CHUNK_SIZE;
                Z++;
            }
            while (z < 0)
            {
                z += Constants.CHUNK_SIZE;
                Z--;
            }
            x2 = x;
            y2 = y;
            z2 = z;
            return manager.GetOrCreateStructureChunk(X, Y, Z);
        }
    }
}
