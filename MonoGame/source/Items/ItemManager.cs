using Inignoto.Tiles;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Items
{
    public class ItemManager
    {
        public static Dictionary<string, Item> REGISTRY = new Dictionary<string, Item>();

        public static void LoadItems()
        {
            foreach (Tile tile in TileManager.REGISTRY.Values)
            {
                RegisterItem(new TileItem(tile));
            }
        }

        public static Item RegisterItem(Item item)
        {
            REGISTRY.Add(item.Name, item);
            return item;
        }

        public static Item GetItemForTile(Tile tile)
        {
            REGISTRY.TryGetValue(tile.name, out Item value);
            return value;
        }
    }
}
