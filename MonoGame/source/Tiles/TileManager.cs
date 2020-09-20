using Inignoto.Audio;
using Inignoto.Graphics.Mesh;
using Inignoto.Graphics.Textures;
using Inignoto.Graphics.World;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using System.Collections.Generic;

namespace Inignoto.Tiles
{
    public class TileManager
    {
        public static Dictionary<string, Tile> REGISTRY = new Dictionary<string, Tile>();

        public static Tile AIR;
        public static Tile DIRT;
        public static Tile GRASS;
        public static Tile STONE;
        public static Tile SMOOTH_STONE;
        public static Tile PURPLE_GRASS;
        public static Tile LOG;
        public static Tile LEAVES;
        public static Tile SAND;
        public static Tile MALECHITE;
        public static Tile SMOOTH_STONE_STAIRS;
        public static Tile WATER;

        private static bool TEXTURES_LOADED = false;

        public static Tile GetTile(int ID)
        {
            foreach (Tile tile in REGISTRY.Values)
            {
                if (tile.ID == ID) return tile;
            }
            return AIR;
        }

        public static void Loadtiles()
        {
            AIR = new Tile("Inignoto:air", null, false).SetRayTraceType(Tile.TileRayTraceType.GAS).SetVisible(false).SetBlocksMovement(false).SetReplaceable(true);
            DIRT = new Tile("Inignoto:dirt", SoundEffects.step_soil);
            GRASS = new Tile("Inignoto:grass", SoundEffects.step_grass);
            WATER = new Tile("Inignoto:water", null).SetBlocksMovement(false).SetRayTraceType(Tile.TileRayTraceType.FLUID).SetReplaceable(true);
        }

        public static void TryLoadTileTextures()
        {
            if (TEXTURES_LOADED) return;
            foreach (Tile tile in REGISTRY.Values)
            {
                if (tile.IsVisible() == false) continue;

                Mesh mesh = TileBuilder.BuildTile(0, 0, 0, tile.DefaultData, Inignoto.game.GraphicsDevice);

                Textures.TILE_ITEMS.Add(tile.DefaultData, mesh.CreateTexture(Textures.tiles.GetTexture(), GameResources.effect, Inignoto.game.GraphicsDevice, new Vector3(-0.75f, -0.2f, -1.4f), Quaternion.CreateFromYawPitchRoll(45 * 3.14f / 180, 30 * 3.14f / 180, 0), 128, 128));

                mesh.Dispose();
            }
            TEXTURES_LOADED = true;
        }
    }
}
