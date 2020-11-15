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
        public static Tile GLOWING_CRYSTAL;
        public static Tile GLASS;
        public static Tile RED_GLASS;
        public static Tile SNOW;
        public static Tile ICE;

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
            AIR = new Tile("Inignoto:air", null, false).SetRayTraceType(Tile.TileRayTraceType.GAS).SetTransparent().SetVisible(false).SetBlocksMovement(false).SetReplaceable(true);
            DIRT = new Tile("Inignoto:dirt", SoundEffects.step_soil).SetFull();
            STONE = new Tile("Inignoto:stone", null).SetFull();
            SMOOTH_STONE = new Tile("Inignoto:smooth_stone", null).SetFull();
            SAND = new Tile("Inignoto:sand", null).SetFull();
            LEAVES = new Tile("Inignoto:leaves", null);
            LOG = new Tile("Inignoto:log", null).SetFull();
            GRASS = new Tile("Inignoto:grass", SoundEffects.step_grass).SetFull().SetOverlay();
            PURPLE_GRASS = new Tile("Inignoto:purple_grass", SoundEffects.step_grass).SetFull();
            WATER = new Tile("Inignoto:water", null, false).SetBlocksMovement(false).SetTransparent().SetRayTraceType(Tile.TileRayTraceType.FLUID).SetReplaceable(true);
            GLOWING_CRYSTAL = new Tile("Inignoto:glowing_crystal", null).SetFull().SetLight(10, 14, 15);
            MALECHITE = new Tile("Inignoto:malechite", null).SetFull().SetLight(0, 8, 0);
            GLASS = new Tile("Inignoto:glass", null).SetFull().SetTransparent();
            RED_GLASS = new Tile("Inignoto:red_glass", null).SetFull().SetTransparent().SetTint(15, 0, 0, 10);
            SNOW = new Tile("Inignoto:snow", null).SetFull();
            ICE = new Tile("Inignoto:ice", null).SetFull();
        }

        public static void TryLoadTileTextures()
        {
            if (TEXTURES_LOADED) return;
            foreach (Tile tile in REGISTRY.Values)
            {
                if (tile.IsVisible() == false) continue;

                Mesh mesh = TileBuilder.BuildTile(0, 0, 0, tile.DefaultData, TileManager.AIR.DefaultData, Inignoto.game.GraphicsDevice);

                Textures.TILE_ITEMS.Add(tile.DefaultData, mesh.CreateTexture(Textures.tiles.GetTexture(), GameResources.effect, Inignoto.game.GraphicsDevice, new Vector3(-0.75f, -0.2f, -1.4f), Quaternion.CreateFromYawPitchRoll(45 * 3.14f / 180, 30 * 3.14f / 180, 0), 128, 128));

                mesh.Dispose();
            }
            TEXTURES_LOADED = true;
        }
    }
}
