using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using Microsoft.Xna.Framework.Audio;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.Tiles
{
    public class CactusTile : Tile
    {
        public CactusTile(string name, SoundEffect[] sound, bool solid = true, int hits = 1) : base(name, sound, solid, hits)
        {

        }

        public override bool CanPlace(int x, int y, int z, Chunk chunk)
        {
            TileData data = chunk.GetVoxel(x, y - 1, z);
            if (data.tile_id == TileManager.SAND.DefaultData.tile_id || TileManager.GetTile(data.tile_id) is CactusTile)
            {
                return true;
            }
            if (TileManager.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id) is CactusTile)
            {
                return true;
            }
            if (TileManager.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id) is CactusTile)
            {
                return true;
            }
            if (TileManager.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id) is CactusTile)
            {
                return true;
            }
            if (TileManager.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id) is CactusTile)
            {
                return true;
            }
            return false;
        }

        public override TileData GetStateForBlockPlacement(int x, int y, int z, Chunk chunk, TileFace face)
        {
            
            
            if (chunk.GetVoxel(x, y - 1, z) == TileManager.AIR.DefaultData)
            {
                switch (face)
                {
                    case TileFace.RIGHT:
                            if (chunk.GetVoxel(x - 1, y, z).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                            {
                                return stateHolder.data[1]; //correct
                            }
                            break;
                    case TileFace.FRONT:
                        if (chunk.GetVoxel(x, y, z - 1).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                        {
                            return stateHolder.data[4];
                        }
                        break;
                    case TileFace.LEFT:
                        if (chunk.GetVoxel(x + 1, y, z).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                        {
                            return stateHolder.data[3]; //correct
                        }
                        break;
                    case TileFace.BACK:
                        if (chunk.GetVoxel(x, y, z + 1).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                        {
                            return stateHolder.data[2];
                        }
                        break;
                }
                if (chunk.GetVoxel(x - 1, y, z).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                {
                    return stateHolder.data[1];
                }
                else if (chunk.GetVoxel(x, y, z - 1).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                {
                    return stateHolder.data[4];
                } else if (chunk.GetVoxel(x + 1, y, z).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                {
                    return stateHolder.data[3];
                }
                else if (chunk.GetVoxel(x, y, z + 1).tile_id == TileManager.CACTUS.DefaultData.tile_id)
                {
                    return stateHolder.data[2];
                }
            }
            if (chunk.GetVoxel(x, y - 1, z).tile_id == TileManager.CACTUS.DefaultData.tile_id)
            {
                if (chunk.GetVoxel(x, y - 1, z) == stateHolder.data[5])
                {
                    chunk.SetVoxel(x, y - 1, z, DefaultData);
                }
                if (chunk.GetVoxel(x, y + 1, z) == TileManager.AIR.DefaultData)
                {
                    return stateHolder.data[5];
                }
            }
            
            return DefaultData;
        }

    }
        
}
