﻿using Inignoto.World.Chunks;
using System.Collections.Generic;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Inignoto.Graphics.Mesh;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Tiles.Data;
using Inignoto.Tiles;

namespace Inignoto.Graphics.World
{
    public class ChunkBuilder
    {

        public static Mesh.Mesh BuildMeshForChunk(GraphicsDevice device, Chunk chunk)
        {
            int invisible = 0;
            List<VertexPositionColorTexture> vpct = new List<VertexPositionColorTexture>();

            List<Vector3> vertices = new List<Vector3>();
            List<Color> colors = new List<Color>();
            List<Vector2> textures = new List<Vector2>();
            List<int> indices = new List<int>();
            int index = 0;

            int invisible2 = 0;

            List<VertexPositionColorTexture> vpct2 = new List<VertexPositionColorTexture>();

            List<Vector3> vertices2 = new List<Vector3>();
            List<Color> colors2 = new List<Color>();
            List<Vector2> textures2 = new List<Vector2>();
            List<int> indices2 = new List<int>();
            int index2 = 0;

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
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.FRONT, vertices2, colors2, textures2, indices2, index2);
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BACK, vertices2, colors2, textures2, indices2, index2);
                                if (!TileManager.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.LEFT, vertices2, colors2, textures2, indices2, index2);
                                if (!TileManager.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.RIGHT, vertices2, colors2, textures2, indices2, index2);
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.TOP, vertices2, colors2, textures2, indices2, index2);
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index2 = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BOTTOM, vertices2, colors2, textures2, indices2, index2);
                            }
                            else invisible2++;
                        } else
                        {
                            if (tile.IsVisible())
                            {
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y, z - 1).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.FRONT, vertices, colors, textures, indices, index);
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y, z + 1).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BACK, vertices, colors, textures, indices, index);
                                if (!TileManager.GetTile(chunk.GetVoxel(x - 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.LEFT, vertices, colors, textures, indices, index);
                                if (!TileManager.GetTile(chunk.GetVoxel(x + 1, y, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.RIGHT, vertices, colors, textures, indices, index);
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y + 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.TOP, vertices, colors, textures, indices, index);
                                if (!TileManager.GetTile(chunk.GetVoxel(x, y - 1, z).tile_id).IsRaytraceTypeOrSolid(rayTraceType))
                                    index = TileBuilder.BuildFace(x, y, z, data, Tiles.Tile.TileFace.BOTTOM, vertices, colors, textures, indices, index);
                            }
                            else invisible++;
                        }
                    }
                }
            }
            if (!(invisible2 >= Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE))
            {
                for (int i = 0; i < indices2.Count; i++)
                {
                    int ind = indices2[i];
                    vpct2.Add(new VertexPositionColorTexture(vertices2[ind], colors2[ind], textures2[ind]));
                }

                chunk.secondWaterMesh = new Mesh.Mesh(device, vpct2.ToArray());
            }



            if (invisible >= Constants.CHUNK_SIZE * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE) return null;

            for (int i = 0; i < indices.Count; i++)
            {
                int ind = indices[i];
                vpct.Add(new VertexPositionColorTexture(vertices[ind], colors[ind], textures[ind]));
            }
            return new Mesh.Mesh(device, vpct.ToArray());
        }
    }
}
