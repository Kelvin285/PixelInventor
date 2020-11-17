using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;

namespace Inignoto.World.Chunks
{
    public class StructureChunk
    {
        public Chunk.Voxel[] tiles;

        public readonly int x, y, z;
        public Vector3 cpos;
        public ChunkManager manager;
        public StructureChunk(int x, int y, int z, ChunkManager manager)
        {
            tiles = new Chunk.Voxel[Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE];
            for (int i = 0; i < tiles.Length; i++)
            {
                tiles[i] = new Chunk.Voxel(TileManager.AIR.DefaultData);
            }
            this.x = x;
            this.y = y;
            this.z = z;
            cpos = new Vector3(x, y, z);
            this.manager = manager;
        }

        public int GetIndex(int x, int y, int z)
        {
            return x + y * Constants.CHUNK_SIZE + z * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE;
        }

        public void SetTile(int x, int y, int z, TileData data)
        {
            if (x >= 0 && y >= 0 && z >= 0 && x < Constants.CHUNK_SIZE && y < Constants.CHUNK_SIZE && z < Constants.CHUNK_SIZE)
            {
                int index = GetIndex(x, y, z);
                tiles[index].voxel = data;
                return;
            }
            StructureChunk schunk = GetOrCreateAdjacent(x, y, z, out int X, out int Y, out int Z);//.SetTile(X, Y, Z, data);
            if (schunk != null)
            {
                schunk.SetTile(X, Y, Z, data);
            }
        }

        public TileData GetTile(int x, int y, int z)
        {
            
            if (x >= 0 && y >= 0 && z >= 0 && x < Constants.CHUNK_SIZE && y < Constants.CHUNK_SIZE && z < Constants.CHUNK_SIZE)
            {
                int index = GetIndex(x, y, z);

                return tiles[index].voxel;
            }
            StructureChunk schunk = GetOrCreateAdjacent(x, y, z, out int X, out int Y, out int Z);//.SetTile(X, Y, Z, data);
            if (schunk != null)
            {
                schunk.GetTile(X, Y, Z);
            }
            
            return TileManager.AIR.DefaultData;
        }
        public bool HasAdjacent(int x, int y, int z, out int x2, out int y2, out int z2, out int CX, out int CY, out int CZ)
        {
            int X = x >= 0 ? x / Constants.CHUNK_SIZE : x / Constants.CHUNK_SIZE - 1;
            int Y = y >= 0 ? y / Constants.CHUNK_SIZE : y / Constants.CHUNK_SIZE - 1;
            int Z = z >= 0 ? z / Constants.CHUNK_SIZE : z / Constants.CHUNK_SIZE - 1;
            X += this.x;
            Y += this.y;
            Z += this.z;
            x %= Constants.CHUNK_SIZE;
            y %= Constants.CHUNK_SIZE;
            z %= Constants.CHUNK_SIZE;

            if (x < 0) x += Constants.CHUNK_SIZE;
            if (y < 0) y += Constants.CHUNK_SIZE;
            if (z < 0) z += Constants.CHUNK_SIZE;

            CX = X;
            CY = Y;
            CZ = Z;
            x2 = x;
            y2 = y;
            z2 = z;
            return manager.HasStructureChunk(X, Y, Z);
        }
        public StructureChunk GetOrCreateAdjacent(int x, int y, int z, out int x2, out int y2, out int z2)
        {
            int X = x >= 0 ? x / Constants.CHUNK_SIZE : x / Constants.CHUNK_SIZE - 1;
            int Y = y >= 0 ? y / Constants.CHUNK_SIZE : y / Constants.CHUNK_SIZE - 1;
            int Z = z >= 0 ? z / Constants.CHUNK_SIZE : z / Constants.CHUNK_SIZE - 1;
            X += this.x;
            Y += this.y;
            Z += this.z;
            x %= Constants.CHUNK_SIZE;
            y %= Constants.CHUNK_SIZE;
            z %= Constants.CHUNK_SIZE;

            if (x < 0) x += Constants.CHUNK_SIZE;
            if (y < 0) y += Constants.CHUNK_SIZE;
            if (z < 0) z += Constants.CHUNK_SIZE;

            x2 = x;
            y2 = y;
            z2 = z;
            return manager.GetOrCreateStructureChunk(X, Y, Z);
        }
    }
}
