﻿using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.Tiles.Tile;

namespace Inignoto.World.Structures.Trees
{
    public class RedwoodStructure : BasicTreeStructure
    {
        public override void TryPlace(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise, double n, int orientation = 0)
        {
            if (chunk.GetVoxel(chunk_x, chunk_y - 1, chunk_z) == TileRegistry.DIRT.DefaultData)
            {
                if (TileRegistry.GetTile(chunk.GetVoxel(chunk_x, chunk_y, chunk_z).tile_id).IsReplaceable())
                {
                    if (n <= 10 && n > 0)
                    {
                        Grow(x, y, z, chunk_x, chunk_y, chunk_z, chunk, noise, n, orientation);

                        for (int xx = -5; xx < 5; xx++)
                        {
                            for (int yy = -5; yy < 5; yy++)
                            {
                                for (int zz = -5; zz < 5; zz++)
                                {
                                    double N = noise.GetWhiteNoise((x + xx) * 1000, (y + yy) * 1000, (z + zz) * 1000) * 10 * 100;

                                    if (N <= 10 && N > 0)
                                    {
                                        if (chunk.GetVoxel(chunk_x + xx, chunk_y + yy - 1, chunk_z + zz) == TileRegistry.DIRT.DefaultData ||
                                            chunk.GetVoxel(chunk_x + xx, chunk_y + yy - 1, chunk_z + zz) == TileRegistry.REDWOOD_LOG.DefaultData)
                                        {
                                            if (chunk.GetVoxel(chunk_x + xx, chunk_y + yy, chunk_z + zz) == TileRegistry.AIR.DefaultData)
                                                chunk.SetVoxel(chunk_x + xx, chunk_y + yy, chunk_z + zz, TileRegistry.CANDENTIS.GetStateForBlockPlacement(chunk_x + xx, chunk_y + yy, chunk_z + zz, chunk, Tile.TileFace.TOP));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        public override void Grow(int x, int y, int z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, FastNoise noise, double n, int orientation = 0)
        {

            void Leaves(int ox, int oy, int oz, int spread, double rad)
            {

                int dirX = 0;
                int dirY = 0;
                int dirZ = 0;

                for (int xx = (int)-rad; xx < rad + 1; xx++)
                {
                    for (int yy = (int)-rad; yy < rad + 1; yy++)
                    {
                        for (int zz = (int)-rad; zz < rad + 1; zz++)
                        {
                            if (chunk.GetVoxel(chunk_x + ox + xx + dirX, chunk_y + oy + yy + dirY, chunk_z + oz + zz + dirZ) == TileRegistry.AIR.DefaultData)
                            {
                                if (MathF.Sqrt(xx * xx + yy * yy + zz * zz) <= rad / 2)
                                {
                                    if (chunk.GetVoxel(chunk_x + ox + xx + dirX, chunk_y + oy + yy + dirY, chunk_z + oz + zz + dirZ) == TileRegistry.AIR.DefaultData)
                                    {
                                        chunk.SetVoxel(chunk_x + ox + xx + dirX, chunk_y + oy + yy + dirY, chunk_z + oz + zz + dirZ, TileRegistry.LEAVES.DefaultData);
                                    }
                                } else
                                {
                                    if (MathF.Sqrt(xx * xx + yy * yy + zz * zz) <= rad)
                                    {
                                        if (noise.GetSimplex((x + xx) * 15, (y + yy) * 15, (z + zz) * 15) * 10 >= 0.5f)
                                        if (chunk.GetVoxel(chunk_x + ox + xx + dirX, chunk_y + oy + yy + dirY, chunk_z + oz + zz + dirZ) == TileRegistry.AIR.DefaultData)
                                        {
                                            chunk.SetVoxel(chunk_x + ox + xx + dirX, chunk_y + oy + yy + dirY, chunk_z + oz + zz + dirZ, TileRegistry.LEAVES.DefaultData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            void Branch(double n, int ox, int oy, int oz)
            {
                int dir = (int)(n * 100) % 4;
                int dirX = dir == 0 ? 1 : dir == 2 ? -1 : 0;
                int dirZ = dir == 1 ? 1 : dir == 3 ? -1 : 0;

                if (chunk.GetVoxel(chunk_x + ox + dirX, chunk_y + oy, chunk_z + oz + dirZ) == TileRegistry.AIR.DefaultData)
                {
                    TileFace face = TileFace.FRONT;
                    if (dirZ == 1) face = TileFace.BACK;
                    if (dirX == -1) face = TileFace.LEFT;
                    if (dirX == 1) face = TileFace.RIGHT;
                    chunk.SetVoxel(chunk_x + ox + dirX, chunk_y + oy, chunk_z + oz + dirZ, TileRegistry.REDWOOD_LEAVES.GetStateForBlockPlacement(chunk_x, chunk_y, chunk_z, chunk, face));
                }
            }

            int Wood(double n, int ox, int oy, int oz, int min = 8, int max = 20, float multiply = 1)
            {
                int height = (int)(MathF.Abs(MathF.Min(max, MathF.Max(0, (float)n * multiply)) + min));

                for (int i = 0; i < height; i++)
                {
                    chunk.SetVoxel(chunk_x + ox, chunk_y + oy + i, chunk_z + oz, TileRegistry.REDWOOD_LOG.DefaultData);
                }
                if (height > 0)
                {
                    for (int i = -1; i > -5; i--)
                    {
                        if (chunk.GetVoxel(chunk_x + ox, chunk_y + oy + i, chunk_z + oz) != TileRegistry.AIR.DefaultData)
                        {
                            continue;
                        }
                        chunk.SetVoxel(chunk_x + ox, chunk_y + oy + i, chunk_z + oz, TileRegistry.REDWOOD_LOG.DefaultData);
                    }
                }
                return height;
            }

            int h = Wood(n, 0, 0, 0, 15, 25, 2);
            for (int i = 5; i < h; i++)
            {
                double N = noise.GetWhiteNoise(x * 1000, (y + i) * 1000, z * 1000) * 10 * 100;

                if (N <= 5)
                {
                    Branch(MathF.Abs((float)N), 0, i, 0);
                }
            }
            Wood(noise.GetWhiteNoise((x - 1) * 1000, z * 1000) * 10 * 100, -1, 0, 0, 1, 7);
            Wood(noise.GetWhiteNoise((x + 1) * 1000, z * 1000) * 10 * 100, 1, 0, 0, 1, 7);
            Wood(noise.GetWhiteNoise(x * 1000, (z - 1) * 1000) * 10 * 100, 0, 0, -1, 1, 7);
            Wood(noise.GetWhiteNoise(x * 1000, (z + 1) * 1000) * 10 * 100, 0, 0, 1, 1, 7);

            double rad = 4;
            for (int i = 0; i < rad * 2; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    Leaves(0, h + (int)(i * rad) - 5, 0, 0, rad);
                }
                rad -= 0.5;
            }
        }
    }
}
