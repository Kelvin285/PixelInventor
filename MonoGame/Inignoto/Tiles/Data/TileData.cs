using Inignoto.Graphics.Models;
using Inignoto.Graphics.Textures;
using Inignoto.Utilities;
using Inignoto.World.Chunks;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using static Inignoto.Tiles.Tile;

namespace Inignoto.Tiles.Data
{
    public class TileData
    {
        public readonly int tile_id;
        public readonly int state;
        public readonly ResourcePath location;
        public readonly int index;
        public readonly int num_x, num_y;

        public string texture = "";
        public string side_texture = "";
        public string top_texture = "";
        public string bottom_texture = "";
        public string left_texture = "";
        public string right_texture = "";
        public string front_texture = "";
        public string back_texture = "";

        public GameModel model;

        public TileData(int tile, int state, ResourcePath location, int index)
        {
            tile_id = tile;
            this.state = state;
            this.location = location;
            this.index = index;
            Dictionary<string, string> data = FileUtils.LoadFileAsDataList(location);
            foreach (string a in data.Keys)
            {
                if (data.TryGetValue(a, out string b))
                {
                    
                    if (a.Equals("num_x"))
                    {
                        int.TryParse(b, out num_x);
                        continue;
                    }
                    if (a.Equals("num_y"))
                    {
                        int.TryParse(b, out num_y);
                        continue;
                    }
                    if (a.Equals("top"))
                    {
                        if (texture.Equals("")) texture = b;
                        top_texture = b;
                    }
                    if (a.Equals("bottom"))
                    {
                        if (texture.Equals("")) texture = b;
                        bottom_texture = b;
                    }
                    if (a.Equals("left"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        left_texture = b;
                    }
                    if (a.Equals("right"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        right_texture = b;
                    }
                    if (a.Equals("front"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        front_texture = b;
                    }
                    if (a.Equals("back"))
                    {
                        if (texture.Equals("")) texture = b;
                        if (side_texture.Equals("")) side_texture = b;
                        back_texture = b;
                    }
                    if (a.Equals("texture"))
                    {
                        texture = b;
                    }
                    if (a.Equals("side"))
                    {
                        side_texture = b;
                    }
                    if (a.Equals("model"))
                    {
                        ResourcePath path = new ResourcePath(b + ".model", "assets");
                        model = GameModel.LoadModel(path, Textures.white_square);
                    }
                    if (model != null)
                    {
                        if (a.Equals("model_rotation_x"))
                        {
                            model.rotation.X = int.Parse(b);
                        }
                        if (a.Equals("model_rotation_y"))
                        {
                            model.rotation.Y = int.Parse(b);
                        }
                        if (a.Equals("model_rotation_z"))
                        {
                            model.rotation.Z = int.Parse(b);
                        }
                        if (a.Equals("model_offset_x"))
                        {
                            model.translation.X = (float)double.Parse(b);
                        }
                        if (a.Equals("model_offset_y"))
                        {
                            model.translation.Y = (float)double.Parse(b);
                        }
                        if (a.Equals("model_offset_z"))
                        {
                            model.translation.Z = (float)double.Parse(b);
                        }
                    }
                    
                }
            }
            if (model != null)
            {
                model.Texture = Textures.LoadTexture(new Utilities.ResourcePath(texture.Split(':')[0], "textures/tiles/" + texture.Split(':')[1] + ".png", "assets"));
            }
        }

        public string GetTexture(TileFace face)
        {
            switch (face)
            {
                case TileFace.TOP:
                    {
                        if (top_texture.Equals("")) return texture;
                        return top_texture;
                    }
                case TileFace.BOTTOM:
                    {
                        if (bottom_texture.Equals("")) return texture;
                        return bottom_texture;
                    }
                case TileFace.LEFT:
                    {
                        if (left_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return left_texture;
                    }
                case TileFace.RIGHT:
                    {
                        if (right_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return right_texture;
                    }
                case TileFace.FRONT:
                    {
                        if (front_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return front_texture;
                    }
                case TileFace.BACK:
                    {
                        if (back_texture.Equals(""))
                        {
                            if (side_texture.Equals("")) return texture;
                            return side_texture;
                        }
                        return back_texture;
                    }
            }
            return texture;
        }

        public void UpdateLightWhenPlaced(Chunk chunk, int x, int y, int z)
        {
            Tile tile = TileRegistry.GetTile(tile_id);

            if (tile == TileRegistry.AIR) return;

            if (!chunk.NeedsToGenerate())
            {
                if (tile.glowing)
                {
                    chunk.SetLight(x, y, z, tile.light_red, tile.light_green, tile.light_blue, -1);
                }
                else
                {
                    if (tile.tinted)
                    {
                        chunk.RemoveLight(x, y, z, tile.RedTint == 0, tile.GreenTint == 0, tile.BlueTint == 0, false);
                    }
                    else
                    {
                        if (tile.IsOpaque())
                        {
                            chunk.RemoveLight(x, y, z, true, true, true, true);
                        }
                    }
                }
            }
            
        }

        public void UpdateLightWhenRemoved(Chunk chunk, int x, int y, int z)
        {
            Tile tile = TileRegistry.GetTile(tile_id);

            if (tile == TileRegistry.AIR) return;

            if (!chunk.NeedsToGenerate())
            {
                if (tile.glowing)
                {
                    chunk.RemoveLight(x, y, z, true, true, true, true);
                } else
                {
                    chunk.PropogateLights(x, y, z, true, true, true, true);
                }
                
            }
        }

        public void Tick(int cx, int cy, int cz, Chunk chunk)
        {
            
        }
    }

}
