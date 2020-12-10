using Inignoto.Imported;
using Inignoto.Tiles;
using Inignoto.World.Chunks;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.World.Structures.Trees
{
    public class ForestOakStructure : BasicTreeStructure
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

                                    if (N <= 5 && N > 0)
                                    {
                                        if (chunk.GetVoxel(chunk_x + xx, chunk_y + yy - 1, chunk_z + zz) == TileRegistry.DIRT.DefaultData ||
                                            chunk.GetVoxel(chunk_x + xx, chunk_y + yy - 1, chunk_z + zz) == TileRegistry.LOG.DefaultData)
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
            Random random = new Random((int)chunk.GetWorld().chunkManager.GetIndexFor(x, y, z) + chunk.GetWorld().properties.seed);

            Vector3 Branch(Vector3 start, Vector3 end, float length = -1)
            {
                Vector3 dir = end - start;
                float len = dir.Length();
                if (length > 0) len = length;
                dir.Normalize();

                Vector3 tree_pos = new Vector3(start.X, start.Y, start.Z);
                if (len > 0)
                {
                    for (float i = 0; i < len; i += 1.0f / len)
                    {
                        tree_pos = start + dir * i;

                        chunk.SetVoxel((int)tree_pos.X, (int)tree_pos.Y, (int)tree_pos.Z, TileRegistry.LOG.DefaultData);
                    }
                }
                return tree_pos;
                
            }

            void Leaves(Vector3 pos, float rad)
            {
                for (float X = -rad; X < rad + 1; X++)
                {
                    for (float Y = -rad; Y < rad + 1; Y++)
                    {
                        for (float Z = -rad; Z < rad + 1; Z++)
                        {
                            if (MathF.Sqrt(X * X + Y * Y + Z * Z) <= rad)
                            {
                                if (chunk.GetVoxel((int)(pos.X + X), (int)(pos.Y + Y), (int)(pos.Z + Z)) == TileRegistry.AIR.DefaultData)
                                    chunk.SetVoxel((int)(pos.X + X), (int)(pos.Y + Y), (int)(pos.Z + Z), TileRegistry.LEAVES.DefaultData);
                            }
                        }
                    }
                }
            }

            int height = random.Next(8, 12);
            Vector3 tree_pos = new Vector3(chunk_x, chunk_y, chunk_z);
            Vector3 tree_pos2 = new Vector3(chunk_x + 1, chunk_y, chunk_z);
            Vector3 tree_pos3 = new Vector3(chunk_x - 1, chunk_y, chunk_z);
            Vector3 tree_pos4 = new Vector3(chunk_x, chunk_y, chunk_z + 1);
            Vector3 tree_pos5 = new Vector3(chunk_x, chunk_y, chunk_z - 1);

            Vector3 trunk_pos = new Vector3(chunk_x + random.Next(-3, 4), chunk_y + height, chunk_z + random.Next(-3, 4));
            Vector3 dir = trunk_pos - tree_pos;

            Branch(tree_pos, trunk_pos);
            Branch(tree_pos2, trunk_pos, random.Next((int)dir.Length() - 1));
            Branch(tree_pos3, trunk_pos, random.Next((int)dir.Length() - 1));
            Branch(tree_pos4, trunk_pos, random.Next((int)dir.Length() - 1));
            Branch(tree_pos5, trunk_pos, random.Next((int)dir.Length() - 1));

            float l = random.Next(2, 3);
            Leaves(trunk_pos + new Vector3(0, l * 0.75f, 0), l);

            for (int i = 0; i < random.Next(3, 5); i++)
            {
                Vector3 branch_pos = trunk_pos + new Vector3(random.Next(-5, 6), random.Next(2, 7), random.Next(-5, 6));
                Vector3 end = Branch(trunk_pos, branch_pos);
                int rad = random.Next(2, 3);
                Leaves(end, rad);
                for (int j = 0; j < random.Next(1, 3); j++)
                {
                    Vector3 leaf_pos = new Vector3(random.Next(-rad, rad) * (rad / 2), random.Next(-rad, rad) * (rad / 2), random.Next(-rad, rad) * (rad / 2));
                    Leaves(end + leaf_pos, (j + 1) * 0.75f);
                }
            }

            {
                Vector3 top = trunk_pos + new Vector3(random.Next(-1, 2), random.Next(4, 6), random.Next(-1, 2));
                Branch(trunk_pos, top);

                int rad = random.Next(2, 3);
                Leaves(top, rad);
                for (int j = 0; j < random.Next(1, 3); j++)
                {
                    Vector3 leaf_pos = new Vector3(random.Next(-rad, rad) * (rad / 2), random.Next(-rad, rad) * (rad / 2), random.Next(-rad, rad) * (rad / 2));
                    Leaves(top + leaf_pos, (j + 1) * 0.75f);
                }
            }
        }
    }
}
