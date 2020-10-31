using Inignoto.World.Chunks;
using System.Collections.Generic;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Inignoto.Graphics.Mesh;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Tiles.Data;
using Inignoto.Tiles;
using System;

namespace Inignoto.Graphics.World
{
    public class ChunkBuilder
    {
        public static Mesh.Mesh BuildMeshForChunk(GraphicsDevice device, Chunk chunk)
        {
            int invisible = 0;
            List<VertexPositionLightTexture> vpct = new List<VertexPositionLightTexture>();

            List<Vector3> vertices = new List<Vector3>();
            List<Color> colors = new List<Color>();
            List<Vector2> textures = new List<Vector2>();
            List<int> indices = new List<int>();
            List<int> light = new List<int>();
            List<int> sunlight = new List<int>();
            List<Vector3> pos = new List<Vector3>();

            int index = 0;

            int invisible2 = 0;

            List<VertexPositionLightTexture> vpct2 = new List<VertexPositionLightTexture>();

            List<Vector3> vertices2 = new List<Vector3>();
            List<Color> colors2 = new List<Color>();
            List<Vector2> textures2 = new List<Vector2>();
            List<int> indices2 = new List<int>();
            List<int> light2 = new List<int>();
            List<int> sunlight2 = new List<int>();
            List<Vector3> pos2 = new List<Vector3>();
            int index2 = 0;

            List<VertexPositionLightTexture> vpct3 = new List<VertexPositionLightTexture>();

            List<Vector3> vertices3 = new List<Vector3>();
            List<Color> colors3 = new List<Color>();
            List<Vector2> textures3 = new List<Vector2>();
            List<int> indices3 = new List<int>();
            List<int> light3 = new List<int>();
            List<int> sunlight3 = new List<int>();
            List<Vector3> pos3 = new List<Vector3>();
            int index3 = 0;

            for (int x = 0; x < Constants.CHUNK_SIZE; x++)
            {
                for (int y = 0; y < Constants.CHUNK_SIZE; y++)
                {
                    for (int z = 0; z < Constants.CHUNK_SIZE; z++)
                    {
                        TileData data = chunk.GetVoxel(x, y, z);
                        Tile tile = TileManager.GetTile(data.tile_id);

                        Tile.TileRayTraceType rayTraceType = Tile.TileRayTraceType.BLOCK;

                        if (tile.GetRayTraceType() == Tile.TileRayTraceType.FLUID)
                        {
                            rayTraceType = Tile.TileRayTraceType.FLUID;
                            if (tile.IsVisible())
                            {
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                {
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.FRONT, vertices2, colors2, textures2, indices2, index2);
                                    AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                }
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                {
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BACK, vertices2, colors2, textures2, indices2, index2);
                                    AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                }
                                if (!TileManager.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                {
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.LEFT, vertices2, colors2, textures2, indices2, index2);
                                    AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                }
                                if (!TileManager.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                {
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.RIGHT, vertices2, colors2, textures2, indices2, index2);
                                    AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                }
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                {
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.TOP, vertices2, colors2, textures2, indices2, index2);
                                    AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                }
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                {
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BOTTOM, vertices2, colors2, textures2, indices2, index2);
                                    AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                }
                            }
                            else invisible2++;
                        } else
                        {
                            if (tile.IsVisible())
                            {
                                if (tile.IsOpaque())
                                {
                                    if (!TileManager.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                                    {
                                        index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.FRONT, vertices, colors, textures, indices, index);
                                        AddLight(light, sunlight, chunk.GetLight(x, y, z - 1), chunk.GetSunlight(x, y, z - 1));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (!TileManager.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                                    {
                                        index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BACK, vertices, colors, textures, indices, index);
                                        AddLight(light, sunlight, chunk.GetLight(x, y, z + 1), chunk.GetSunlight(x, y, z + 1));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (!TileManager.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                                    {
                                        index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.LEFT, vertices, colors, textures, indices, index);
                                        AddLight(light, sunlight, chunk.GetLight(x - 1, y, z), chunk.GetSunlight(x - 1, y, z));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (!TileManager.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                                    {
                                        index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.RIGHT, vertices, colors, textures, indices, index);
                                        AddLight(light, sunlight, chunk.GetLight(x + 1, y, z), chunk.GetSunlight(x + 1, y, z));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (!TileManager.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                                    {
                                        index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.TOP, vertices, colors, textures, indices, index);
                                        AddLight(light, sunlight, chunk.GetLight(x, y + 1, z), chunk.GetSunlight(x, y + 1, z));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (!TileManager.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                                    {
                                        index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BOTTOM, vertices, colors, textures, indices, index);
                                        AddLight(light, sunlight, chunk.GetLight(x, y - 1, z), chunk.GetSunlight(x, y - 1, z));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                } else
                                {
                                    if (TileManager.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id).IsOpaqueOrNotBlock())
                                    {
                                        index3 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.FRONT, vertices3, colors3, textures3, indices3, index3);
                                        AddLight(light3, sunlight3, chunk.GetLight(x, y, z - 1), chunk.GetSunlight(x, y, z - 1));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (TileManager.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsOpaqueOrNotBlock())
                                    {
                                        index3 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BACK, vertices3, colors3, textures3, indices3, index3);
                                        AddLight(light3, sunlight3, chunk.GetLight(x, y, z + 1), chunk.GetSunlight(x, y, z + 1));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (TileManager.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsOpaqueOrNotBlock())
                                    {
                                        index3 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.LEFT, vertices3, colors3, textures3, indices3, index3);
                                        AddLight(light3, sunlight3, chunk.GetLight(x - 1, y, z), chunk.GetSunlight(x - 1, y, z));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (TileManager.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsOpaqueOrNotBlock())
                                    {
                                        index3 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.RIGHT, vertices3, colors3, textures3, indices3, index3);
                                        AddLight(light3, sunlight3, chunk.GetLight(x + 1, y, z), chunk.GetSunlight(x + 1, y, z));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (TileManager.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsOpaqueOrNotBlock())
                                    {
                                        index3 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.TOP, vertices3, colors3, textures3, indices3, index3);
                                        AddLight(light3, sunlight3, chunk.GetLight(x, y + 1, z), chunk.GetSunlight(x, y + 1, z));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                    if (TileManager.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsOpaqueOrNotBlock())
                                    {
                                        index3 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BOTTOM, vertices3, colors3, textures3, indices3, index3);
                                        AddLight(light3, sunlight3, chunk.GetLight(x, y - 1, z), chunk.GetSunlight(x, y - 1, z));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                        pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                    }
                                }
                                
                            }
                            else invisible++;
                        }
                    }
                }
            }

            int[] GetLights(int ind, bool smooth_lights, List<int> light, List<int> sunlight, List<Vector3> vertices, List<Vector3> pos)
            {
                int[] LIGHTS = { light[ind], sunlight[ind] };

                if (smooth_lights)
                {
                    Vector3 normal = (vertices[ind] - pos[ind]);
                    normal.Normalize();
                    normal.Round();
                    
                    int LOCAL_RED = chunk.GetRedLight((int)vertices[ind].X, (int)vertices[ind].Y, (int)vertices[ind].Z);
                    int LOCAL_GREEN = chunk.GetGreenLight((int)vertices[ind].X, (int)vertices[ind].Y, (int)vertices[ind].Z);
                    int LOCAL_BLUE = chunk.GetBlueLight((int)vertices[ind].X, (int)vertices[ind].Y, (int)vertices[ind].Z);
                    int LOCAL_SUN = chunk.GetSunlight((int)vertices[ind].X, (int)vertices[ind].Y, (int)vertices[ind].Z);
                    LOCAL_RED = 0;
                    LOCAL_GREEN = 0;
                    LOCAL_BLUE = 0;
                    LOCAL_SUN = 0;
                    float RED = 0;
                    float GREEN = 0;
                    float BLUE = 0;
                    float SUNLIGHT = 0;
                    int cr = 0;
                    int cg = 0;
                    int cb = 0;
                    int cs = 0;
                    for (int xx = -1; xx < 2; xx++)
                    {
                        for (int yy = -1; yy < 2; yy++)
                        {
                            for (int zz = -1; zz < 2; zz++)
                            {

                                int X = (int)MathF.Round(vertices[ind].X - 0.5f + xx * 0.5f);
                                int Y = (int)MathF.Round(vertices[ind].Y - 0.5f + yy * 0.5f);
                                int Z = (int)MathF.Round(vertices[ind].Z - 0.5f + zz * 0.5f);

                                int R = chunk.GetRedLight(X, Y, Z);
                                int G = chunk.GetGreenLight(X, Y, Z);
                                int B = chunk.GetBlueLight(X, Y, Z);
                                int S = chunk.GetSunlight(X, Y, Z);

                                if (R > LOCAL_RED)
                                {
                                    RED += R;
                                    cr++;
                                }
                                if (G > LOCAL_GREEN)
                                {
                                    GREEN += G;
                                    cg++;
                                }
                                if (B > LOCAL_BLUE)
                                {
                                    BLUE += B;
                                    cb++;
                                }
                                if (S > LOCAL_SUN)
                                {
                                    SUNLIGHT += S;
                                    cs++;
                                }

                            }
                        }
                    }
                    /*
                    RED /= count;
                    GREEN /= count;
                    BLUE /= count;
                    SUNLIGHT /= count;
                    */
                    RED /= cr;
                    GREEN /= cg;
                    BLUE /= cb;
                    SUNLIGHT /= cs;

                    LIGHTS = new int[]{ chunk.GetRGB((int)RED, (int)GREEN, (int)BLUE), (int)SUNLIGHT};
                }

                return LIGHTS;
            }

            bool smooth_lights = true;
            for (int i = 0; i < indices.Count || i < indices2.Count || i < indices3.Count; i++)
            {
                
                if (i < indices.Count)
                {
                    int ind = indices[i];

                    int[] lights = GetLights(ind, smooth_lights, light, sunlight, vertices, pos);

                    vpct.Add(new VertexPositionLightTexture(vertices[ind], colors[ind], textures[ind], lights[0], lights[1]));
                }
                if (i < indices3.Count)
                {
                    int ind = indices3[i];
                    int[] lights = GetLights(ind, smooth_lights, light3, sunlight3, vertices3, pos3);

                    vpct3.Add(new VertexPositionLightTexture(vertices3[ind], colors3[ind], textures3[ind], lights[0], lights[1]));
                }
                if (!(invisible2 >= Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE))
                {
                    if (i < indices2.Count)
                    {
                        int ind = indices2[i];
                        int[] lights = GetLights(ind, smooth_lights, light2, sunlight2, vertices2, pos2);

                        vpct2.Add(new VertexPositionLightTexture(vertices2[ind], colors2[ind], textures2[ind], lights[0], lights[1]));
                    }

                }
            }
            if (!(invisible2 >= Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE)) {
                chunk.secondWaterMesh = new Mesh.Mesh(device, vpct2.ToArray());
            }
            if (invisible >= Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE) return null;

            chunk.secondTransparencyMesh = new Mesh.Mesh(device, vpct3.ToArray());

            return new Mesh.Mesh(device, vpct.ToArray());
        }

        private static void AddLight(List<int> light, List<int> sunlight, int LIGHT, int SUNLIGHT)
        {
            for (int i = 0; i < 4; i++)
            {
                light.Add(LIGHT);
                sunlight.Add(SUNLIGHT);
            }
        }
    }
}
