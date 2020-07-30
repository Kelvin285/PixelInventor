using System.Collections.Generic;
using Inignoto.Utilities;

namespace Inignoto.Tiles.Data
{
    class TileDataHolder
    {
        public static List<TileData> REGISTRY = new List<TileData>();
        public static TileData NO_DATA;


        public List<TileData> data;

        private static int INDEX;
        public static void Initialize()
        {
            TileDataHolder.NO_DATA = new TileData(TileManager.AIR.ID, 0, new Utilities.ResourcePath("Inignoto:tiledata/data/air.tile", "assets"), INDEX++);

        }

        public TileDataHolder(Tile tile)
        {
            Dictionary<string, string> locations = FileUtils.LoadFileAsDataList(new Utilities.ResourcePath("Inignoto:tiledata/states/" + tile.name.Split(':')[1]+".states", "assets"));

            data = new List<TileData>();
            int i = 0;
            foreach (string val in locations.Values) {
                data.Add(RegisterTileData(tile, i++, new Utilities.ResourcePath(val + ".tile", "assets")));
            }
        }

        public static TileData RegisterTileData(Tile tile, int state, Utilities.ResourcePath location)
        {
            TileData data = new TileData(tile.ID, state, location, INDEX++);
            REGISTRY.Add(data);
            return data;
        }

        public TileData getStateFor(int state)
        {
            return data[state];
        }
    }
}
