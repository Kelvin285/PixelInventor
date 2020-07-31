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
            DIRT = new Tile("Inignoto:dirt", null);
            GRASS = new Tile("Inignoto:grass", null);

        }
    }
}
