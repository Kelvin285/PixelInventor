using Inignoto.World.Chunks;
using Inignoto.GameSettings;
using Inignoto.Utilities;
using Inignoto.Tiles;
using Inignoto.Imported;
using Microsoft.Xna.Framework;
using System.Runtime.CompilerServices;
using System.CodeDom;
using System;
using Inignoto.Graphics.World;
using System.Diagnostics;

namespace Inignoto.World.Generator
{
    public class ChunkGenerator
    {
        public readonly FastNoise noise;
        public ChunkGenerator()
        {
            noise = new FastNoise();
        }
        public void GenerateChunk(Chunks.Chunk chunk)
        {
            for (int chunk_x = 0; chunk_x < Constants.CHUNK_SIZE; chunk_x++)
            {
                for (int chunk_z = 0; chunk_z < Constants.CHUNK_SIZE; chunk_z++)
                {
                    
                    int x = chunk_x + chunk.GetX() * Constants.CHUNK_SIZE;
                    int z = chunk_z + chunk.GetZ() * Constants.CHUNK_SIZE;

                    float height = GetHeight(x, z, chunk.GetWorld().radius);

                    int voxel_height = (int)height;

                    for (int chunk_y = 0; chunk_y < Constants.CHUNK_SIZE; chunk_y++)
                    {
                        int y = chunk_y + chunk.GetY() * Constants.CHUNK_SIZE;

                        if (y < voxel_height)
                        {
                            chunk.SetLight(chunk_x, chunk_y, chunk_z, -1, -1, -1, 0);

                            if (y < voxel_height - 1)
                            {
                                chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileManager.DIRT.DefaultData);
                            }
                            else
                            {
                                chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileManager.GRASS.DefaultData);
                            }
                        }
                        else
                        {
                            chunk.SetLight(chunk_x, chunk_y, chunk_z, -1, -1, -1, 15);

                            chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileManager.AIR.DefaultData);

                            if (y <= 0)
                            {
                                chunk.SetVoxel(chunk_x, chunk_y, chunk_z, TileManager.WATER.DefaultData);
                            }
                        }
                    }
                }
            }
            chunk.UpdateLights();
            //chunk.BuildMesh();
            chunk.MarkForRebuild();
        }

        public float GetHeight(float x, float z, float radius)
        {
            float diameter = radius * 2;
            float length = diameter * 2;

            if (x >= 0 && z >= 0 && x <= length && z <= length)
            {
                if (z < diameter)
                {
                    float lat = (x / length) * MathHelper.TwoPi * radius;
                    float lon = (z / diameter) * radius;
                    return GetPolarHeight(lat, lon);
                }
                else
                {
                    if (Vector2.Distance(new Vector2(x, z), new Vector2(radius, diameter + radius)) <= radius)
                    {
                        float dist = Vector2.Distance(new Vector2(x, z), new Vector2(radius, diameter + radius));
                        float lon = -(radius - dist);

                        float atan = (float)System.Math.Atan2(z - (diameter + radius), x - radius);
                        if (atan < 0) atan += MathHelper.TwoPi;

                        float lat = atan * radius;
                        return GetPolarHeight(lat, lon);
                    }
                    else if (Vector2.Distance(new Vector2(x, z), new Vector2(diameter + radius, diameter + radius)) <= radius)
                    {
                        float dist = Vector2.Distance(new Vector2(x, z), new Vector2(diameter + radius, diameter + radius));
                        float lon = (radius - dist) + radius;

                        float atan = (float)System.Math.Atan2(z - (diameter + radius), x - (diameter + radius));
                        if (atan < 0) atan += MathHelper.TwoPi;

                        float lat = atan * radius;
                        return GetPolarHeight(lat, lon);
                    }
                }
            }

            return -100;
        }

        public float GetPolarHeight(float x, float y)
        {
            return noise.GetSimplex(x, y * 2.0f) * 50.0f;
        }
    }
}
