﻿using System.Collections.Generic;
using Inignoto.Utilities;

namespace Inignoto.Tiles.Data
{
    public class TileDataHolder
    {
        public static List<TileData> REGISTRY = new List<TileData>(); // A list of all the tile states (used for save data)
        public static TileData NO_DATA;


        public List<TileData> data; // A list of all the states within the current block

        private static int INDEX;
        public static void Initialize()
        {
            TileDataHolder.NO_DATA = new TileData(TileRegistry.AIR.ID, 0, new Utilities.ResourcePath("Inignoto:tiledata/data/air.tile", "assets"), INDEX++);

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
            if (state < 0 || state > data.Count) return data[0];
            return data[state];
        }
    }
}
