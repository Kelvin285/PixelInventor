using Inignoto.Audio;
using Inignoto.Graphics.Mesh;
using Inignoto.Graphics.Textures;
using Inignoto.Graphics.World;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using System.Collections.Generic;

namespace Inignoto.Tiles
{
    public class TileRegistry
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
        public static Tile COPPER_ORE;
        public static Tile CACTUS;
        public static Tile IRON_SCRAP;
        public static Tile CANDENTIS;
        public static Tile TALL_GRASS;
        public static Tile ROCK_PILE;

        public static Tile IRON_PLATING;
        public static Tile IRON_PLATING_SIMPLE;
        public static Tile IRON_PLATING_LARGE;
        public static Tile STORAGE_CONTAINER;


        public static Tile STEEL_PLATING;
        public static Tile STEEL_PLATING_LAMP;
        public static Tile SPACECRAFT_THRUSTER;
        public static Tile HEAT_RESISTANT_PLATING;
        public static Tile REDWOOD_LOG;
        public static Tile REDWOOD_LEAVES;
        public static Tile FLOURESCENT_LAMP;
        public static Tile CONEWOOD_LOG;

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
            AIR = new Tile("Inignoto:air", null, false).SetRayTraceType(Tile.TileRayTraceType.GAS).SetTransparent().SetVisible(false).SetBlocksMovement(false).SetReplaceable(true).SetCanDrop(false);
            DIRT = new Tile("Inignoto:dirt", SoundEffects.step_soil, true, 3).SetFull();
            STONE = new Tile("Inignoto:stone", null, true, 7).SetFull();
            SMOOTH_STONE = new Tile("Inignoto:smooth_stone", null, true, 7).SetFull();
            SAND = new Tile("Inignoto:sand", null, true, 3).SetFull();
            LEAVES = new Tile("Inignoto:leaves", null, true, 1).SetTransparent();
            LOG = new Tile("Inignoto:log", null, true, 5).SetFull();
            GRASS = new Tile("Inignoto:grass", SoundEffects.step_grass, true, 4).SetFull().SetOverlay().SetCanDrop(false);
            PURPLE_GRASS = new Tile("Inignoto:purple_grass", SoundEffects.step_grass, true, 4).SetFull().SetOverlay();
            WATER = new Tile("Inignoto:water", null, false).SetBlocksMovement(false).SetTransparent().SetRayTraceType(Tile.TileRayTraceType.FLUID).SetReplaceable(true);
            GLOWING_CRYSTAL = new Tile("Inignoto:glowing_crystal", null, true, 4).SetFull().SetLight(10, 14, 15);
            MALECHITE = new Tile("Inignoto:malechite", null, true, 4).SetFull().SetLight(0, 8, 0);
            GLASS = new Tile("Inignoto:glass", null, true, 2).SetFull().SetTransparent();
            RED_GLASS = new Tile("Inignoto:red_glass", null, true, 2).SetFull().SetTransparent().SetTint(15, 0, 0, 10);
            SNOW = new Tile("Inignoto:snow", null, true, 3).SetFull();
            ICE = new Tile("Inignoto:ice", null, true, 3).SetFull();
            COPPER_ORE = new Tile("Inignoto:copper_ore", null, true, 7).SetFull().SetItemModel("Inignoto:copper_ore_item").SetOverlay();
            CACTUS = new CactusTile("Inignoto:cactus", null, true, 3).BlockLight(false, false, false, false).SetTransparent();
            IRON_SCRAP = new PileTile("Inignoto:iron_scrap");
            CANDENTIS = new PlantTile("Inignoto:candentis", new Tile[] { DIRT, LOG }).SetLight(0, 0, 4);
            TALL_GRASS = new PlantTile("Inignoto:tall_grass", new Tile[] { DIRT });
            ROCK_PILE = new PileTile("Inignoto:rock_pile");
            IRON_PLATING = new Tile("Inignoto:iron_plating", null, true, 10).SetFull();
            STEEL_PLATING = new Tile("Inignoto:steel_plating", null, true, 15).SetFull();
            STEEL_PLATING_LAMP = new Tile("Inignoto:steel_plating_lamp", null, true, 15).SetFull().SetLight(15, 15, 15);
            SPACECRAFT_THRUSTER = new Tile("Inignoto:spacecraft_thruster", null, true, 10).SetFull();
            HEAT_RESISTANT_PLATING = new Tile("Inignoto:heat_resistant_plating", null, true, 10).SetFull();
            REDWOOD_LEAVES = new HorizontalDirectionalTile("Inignoto:redwood_leaves", null, false, 1).SetTransparent().SetReplaceable();
            REDWOOD_LOG = new Tile("Inignoto:redwood_log", null, true, 5).SetFull();
            FLOURESCENT_LAMP = new HorizontalDirectionalTile("Inignoto:flourescent_lamp", null, false, 1).SetTransparent().SetLight(15, 15, 15);
            CONEWOOD_LOG = new Tile("Inignoto:conewood_log", null, true, 5).SetFull();
            IRON_PLATING_SIMPLE = new Tile("Inignoto:iron_plating_simple", null, true, 10).SetFull();
            IRON_PLATING_LARGE = new Tile("Inignoto:iron_plating_large", null, true, 10).SetFull();
            STORAGE_CONTAINER = new Tile("Inignoto:storage_container", null, true, 10).SetFull();
        }

        public static void TryLoadTileTextures()
        {
            if (TEXTURES_LOADED) return;
            foreach (Tile tile in REGISTRY.Values)
            {
                if (tile.IsVisible() == false) continue;

                Mesh mesh = TileBuilder.BuildTile(0, 0, 0, tile.DefaultData, TileRegistry.AIR.DefaultData, Inignoto.game.GraphicsDevice);

                Textures.TILE_ITEMS.Add(tile.DefaultData, mesh.CreateTexture(Textures.tiles.GetTexture(), GameResources.effect, Inignoto.game.GraphicsDevice, new Vector3(-0.75f, -0.2f, -1.4f), Quaternion.CreateFromYawPitchRoll(45 * 3.14f / 180, 30 * 3.14f / 180, 0), 128, 128));

                mesh.Dispose();
            }
            TEXTURES_LOADED = true;
        }
    }
}
