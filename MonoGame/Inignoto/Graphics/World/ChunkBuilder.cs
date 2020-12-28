using Inignoto.World.Chunks;
using System.Collections.Generic;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Inignoto.Graphics.Mesh;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Tiles.Data;
using Inignoto.Tiles;
using System;
using System.Buffers;
using static Inignoto.Tiles.Tile;
using System.Threading.Tasks;
using static Inignoto.World.World;

namespace Inignoto.Graphics.World
{
    public class ChunkBuilder
    {
        public static Dictionary<TileData, Mesh.Mesh> meshes = new Dictionary<TileData, Mesh.Mesh>();

        public static Mesh.Mesh BuildMeshForChunk(GraphicsDevice device, Chunk c)
        {
            if (c == null) return null;
            Chunk chunk = null;
            
                chunk = c;
            List<VertexPositionLightTexture> vpct = new List<VertexPositionLightTexture>();

            List<Vector3> vertices = new List<Vector3>();
            List<Color> colors = new List<Color>();
            List<Vector4> textures = new List<Vector4>();
            List<int> indices = new List<int>();
            List<int> light = new List<int>();
            List<int> sunlight = new List<int>();
            List<Vector3> pos = new List<Vector3>();
            List<int> normals = new List<int>();

            int index = 0;


            List<VertexPositionLightTexture> vpct2 = new List<VertexPositionLightTexture>();

            List<Vector3> vertices2 = new List<Vector3>();
            List<Color> colors2 = new List<Color>();
            List<Vector4> textures2 = new List<Vector4>();
            List<int> indices2 = new List<int>();
            List<int> light2 = new List<int>();
            List<int> sunlight2 = new List<int>();
            List<Vector3> pos2 = new List<Vector3>();
            List<int> normals2 = new List<int>();
            int index2 = 0;

            List<VertexPositionLightTexture> vpct3 = new List<VertexPositionLightTexture>();

            List<Vector3> vertices3 = new List<Vector3>();
            List<Color> colors3 = new List<Color>();
            List<Vector4> textures3 = new List<Vector4>();
            List<int> indices3 = new List<int>();
            List<int> light3 = new List<int>();
            List<int> sunlight3 = new List<int>();
            List<Vector3> pos3 = new List<Vector3>();
            List<int> normals3 = new List<int>();
            int index3 = 0;

            List<VertexPositionLightTexture> vpct4 = new List<VertexPositionLightTexture>();

            List<Vector3> vertices4 = new List<Vector3>();
            List<Color> colors4 = new List<Color>();
            List<Vector4> textures4 = new List<Vector4>();
            List<int> indices4 = new List<int>();
            List<int> light4 = new List<int>();
            List<int> sunlight4 = new List<int>();
            List<Vector3> pos4 = new List<Vector3>();
            List<int> normals4 = new List<int>();
            int index4 = 0;

            try
            {
                //foreach (Vector3 vec in chunk.buildVoxels)
                for (int x = 0; x < Constants.CHUNK_SIZE; x++)
                    for (int y = 0; y < Constants.CHUNK_SIZE; y++)
                        for (int z = 0; z < Constants.CHUNK_SIZE; z++)
                    {
                //int x = (int)vec.X;
                //int y = (int)vec.Y;
                //int z = (int)vec.Z;
                TileData data = chunk.GetVoxel(x, y, z);
                TileData overlay = chunk.GetOverlayVoxel(x, y, z);
                Tile tile = TileRegistry.GetTile(data.tile_id);

                Tile.TileRayTraceType rayTraceType = Tile.TileRayTraceType.BLOCK;

                if (data.model != null)
                {
                    Mesh.Mesh mesh = TileBuilder.BuildTile(0, 0, 0, data, TileRegistry.AIR.DefaultData, device, false);
                    if (mesh != null)
                    {

                        for (int i = 0; i < mesh.triangleVertices.Length; i++)
                        {
                            indices4.Add(index4++);
                            vertices4.Add(mesh.triangleVertices[i].Position + new Vector3(x, y, z));
                            pos4.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            light4.Add(chunk.GetLight(x, y, z));
                            sunlight4.Add(chunk.GetSunlight(x, y, z));
                            colors4.Add(mesh.triangleVertices[i].Color);
                            textures4.Add(new Vector4(mesh.triangleVertices[i].TextureCoordinate, -1.0f, -1.0f));
                            normals4.Add(0);
                        }
                    }


                    continue;
                }

                if (tile.GetRayTraceType() == Tile.TileRayTraceType.FLUID)
                {
                    rayTraceType = Tile.TileRayTraceType.FLUID;
                    if (tile.IsVisible())
                    {
                        if (!TileRegistry.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                        {
                            index2 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.FRONT, vertices2, colors2, textures2, indices2, normals2, index2);
                            AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                        }
                        if (!TileRegistry.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                        {
                            index2 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.BACK, vertices2, colors2, textures2, indices2, normals2, index2);
                            AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                        }
                        if (!TileRegistry.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                        {
                            index2 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.LEFT, vertices2, colors2, textures2, indices2, normals2, index2);
                            AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                        }
                        if (!TileRegistry.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                        {
                            index2 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.RIGHT, vertices2, colors2, textures2, indices2, normals2, index2);
                            AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                        }
                        if (!TileRegistry.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                        {
                            index2 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.TOP, vertices2, colors2, textures2, indices2, normals2, index2);
                            AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                        }
                        if (!TileRegistry.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                        {
                            index2 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.BOTTOM, vertices2, colors2, textures2, indices2, normals2, index2);
                            AddLight(light2, sunlight2, chunk.GetLight(x, y, z), chunk.GetSunlight(x, y, z));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            pos2.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                        }
                    }
                } else
                {
                    if (tile.IsVisible())
                    {
                                
                        if (tile.IsOpaque())
                        {
                            if (!TileRegistry.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                            {
                                index = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.FRONT, vertices, colors, textures, indices, normals, index);
                                AddLight(light, sunlight, chunk.GetLight(x, y, z - 1), chunk.GetSunlight(x, y, z - 1));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (!TileRegistry.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                            {
                                index = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.BACK, vertices, colors, textures, indices, normals, index);
                                AddLight(light, sunlight, chunk.GetLight(x, y, z + 1), chunk.GetSunlight(x, y, z + 1));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (!TileRegistry.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                            {
                                index = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.LEFT, vertices, colors, textures, indices, normals, index);
                                AddLight(light, sunlight, chunk.GetLight(x - 1, y, z), chunk.GetSunlight(x - 1, y, z));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (!TileRegistry.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                            {
                                index = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.RIGHT, vertices, colors, textures, indices, normals, index);
                                AddLight(light, sunlight, chunk.GetLight(x + 1, y, z), chunk.GetSunlight(x + 1, y, z));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (!TileRegistry.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                            {
                                index = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.TOP, vertices, colors, textures, indices, normals, index);
                                AddLight(light, sunlight, chunk.GetLight(x, y + 1, z), chunk.GetSunlight(x, y + 1, z));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (!TileRegistry.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsRaytraceTypeOrSolidAndOpaque(rayTraceType))
                            {
                                index = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.BOTTOM, vertices, colors, textures, indices, normals, index);
                                AddLight(light, sunlight, chunk.GetLight(x, y - 1, z), chunk.GetSunlight(x, y - 1, z));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                        } else
                        {

                            if (TileRegistry.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id).IsNotFullBlockOrTransparentAndDoesNotEqual(tile))
                            {
                                index3 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.FRONT, vertices3, colors3, textures3, indices3, normals3, index3);
                                AddLight(light3, sunlight3, chunk.GetLight(x, y, z - 1), chunk.GetSunlight(x, y, z - 1));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (TileRegistry.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsNotFullBlockOrTransparentAndDoesNotEqual(tile))
                            {
                                index3 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.BACK, vertices3, colors3, textures3, indices3, normals3, index3);
                                AddLight(light3, sunlight3, chunk.GetLight(x, y, z + 1), chunk.GetSunlight(x, y, z + 1));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (TileRegistry.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsNotFullBlockOrTransparentAndDoesNotEqual(tile))
                            {
                                index3 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.LEFT, vertices3, colors3, textures3, indices3, normals3, index3);
                                AddLight(light3, sunlight3, chunk.GetLight(x - 1, y, z), chunk.GetSunlight(x - 1, y, z));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (TileRegistry.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsNotFullBlockOrTransparentAndDoesNotEqual(tile))
                            {
                                index3 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.RIGHT, vertices3, colors3, textures3, indices3, normals3, index3);
                                AddLight(light3, sunlight3, chunk.GetLight(x + 1, y, z), chunk.GetSunlight(x + 1, y, z));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (TileRegistry.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsNotFullBlockOrTransparentAndDoesNotEqual(tile))
                            {
                                index3 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.TOP, vertices3, colors3, textures3, indices3, normals3, index3);
                                AddLight(light3, sunlight3, chunk.GetLight(x, y + 1, z), chunk.GetSunlight(x, y + 1, z));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                            if (TileRegistry.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsNotFullBlockOrTransparentAndDoesNotEqual(tile))
                            {
                                index3 = TileBuilder.BuildFace(x, y, z, data, overlay, Tiles.Tile.TileFace.BOTTOM, vertices3, colors3, textures3, indices3, normals3, index3);
                                AddLight(light3, sunlight3, chunk.GetLight(x, y - 1, z), chunk.GetSunlight(x, y - 1, z));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                                pos3.Add(new Vector3(x + 0.5f, y + 0.5f, z + 0.5f));
                            }
                        }
                                
                    }
                }
            }
            }
            catch (InvalidOperationException e)
            {
                //chunk.MarkForRebuild();
                return null;
            }
            int[] GetLights(int ind, bool smooth_lights, List<int> light, List<int> sunlight, List<Vector3> vertices, List<Vector3> pos)
            {
                int[] LIGHTS = { light[ind], sunlight[ind] };

                if (smooth_lights)
                {
                    Vector3 normal = (vertices[ind] - pos[ind]);
                    normal.Normalize();
                    normal.Round();

                    int LOCAL_RED = 0;
                    int LOCAL_GREEN = 0;
                    int LOCAL_BLUE = 0;
                    int LOCAL_SUN = 0;
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
            for (int i = 0; i < vertices.Count || i < vertices2.Count || i < vertices3.Count || i < vertices4.Count; i++)
            {
                if (i < vertices.Count)
                {
                    int[] lights = GetLights(i, smooth_lights, light, sunlight, vertices, pos);

                    vpct.Add(new VertexPositionLightTexture(vertices[i], colors[i], textures[i], normals[i], lights[0], lights[1]));
                }
                if (i < vertices3.Count)
                {
                    int[] lights = GetLights(i, smooth_lights, light3, sunlight3, vertices3, pos3);

                    vpct3.Add(new VertexPositionLightTexture(vertices3[i], colors3[i], textures3[i], normals3[i], lights[0], lights[1]));
                }
                if (i < vertices2.Count)
                {
                    int[] lights = GetLights(i, smooth_lights, light2, sunlight2, vertices2, pos2);

                    vpct2.Add(new VertexPositionLightTexture(vertices2[i], colors2[i], textures2[i], normals2[i], lights[0], lights[1]));
                }
                if (i < vertices4.Count)
                {
                    int[] lights = GetLights(i, smooth_lights, light4, sunlight4, vertices4, pos4);
                    vpct4.Add(new VertexPositionLightTexture(vertices4[i], colors4[i], textures4[i], normals4[i], lights[0], lights[1]));
                }
            }
            chunk.waterMesh = new Mesh.Mesh(device, vpct2.ToArray());
            chunk.waterMesh.SetIndexBuffer(device, indices2.ToArray());

            chunk.transparencyMesh = new Mesh.Mesh(device, vpct3.ToArray());
            chunk.transparencyMesh.SetIndexBuffer(device, indices3.ToArray());

            chunk.customMesh = new Mesh.Mesh(device, vpct4.ToArray());
            chunk.customMesh.SetIndexBuffer(device, indices4.ToArray());
            
            Mesh.Mesh m = new Mesh.Mesh(device, vpct.ToArray());
            m.SetIndexBuffer(device, indices.ToArray());
            
            return m;

        }

        public static void UpdateLights(Chunk chunk, Mesh.Mesh mesh)
        {
            VertexPositionLightTexture[] triangleVertices = mesh.triangleVertices;
            void SetLights(int ind, bool smooth_lights)
            {

                if (smooth_lights)
                {
                    int n = (int)triangleVertices[ind].Normal;
                    Vector3 normal = new Vector3(0, 1, 0);
                    if (n == (int)TileFace.FRONT)
                    {
                        normal.X = 0;
                        normal.Y = 0;
                        normal.Z = -1;
                    }
                    else if (n == (int)TileFace.BACK)
                    {
                        normal.X = 0;
                        normal.Y = 0;
                        normal.Z = 1;
                    }
                    else if (n == (int)TileFace.LEFT)
                    {
                        normal.X = -1;
                        normal.Y = 0;
                        normal.Z = 0;
                    }
                    else if (n == (int)TileFace.RIGHT)
                    {
                        normal.X = 1;
                        normal.Y = 0;
                        normal.Z = 0;
                    }

                    int LOCAL_RED = 0;
                    int LOCAL_GREEN = 0;
                    int LOCAL_BLUE = 0;
                    int LOCAL_SUN = 0;
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

                                int X = (int)MathF.Round(triangleVertices[ind].Position.X - 0.5f + xx * 0.5f);
                                int Y = (int)MathF.Round(triangleVertices[ind].Position.Y - 0.5f + yy * 0.5f);
                                int Z = (int)MathF.Round(triangleVertices[ind].Position.Z - 0.5f + zz * 0.5f);

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

                    triangleVertices[ind].Color = new Color(RED / 15.0f, GREEN / 15.0f, BLUE / 15.0f, SUNLIGHT / 15.0f);
                }
            }
            if (triangleVertices != null)
            for (int i = 0; i < triangleVertices.Length; i++)
            {
                SetLights(i, true);
            }
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

