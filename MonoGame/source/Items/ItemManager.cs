using Inignoto.Tiles;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
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
        public static List<Item> ITEM_LIST = new List<Item>();

        private static int ticks = 0;

        public static Item IRON_PICKAXE;

        public static void LoadItems()
        {
            foreach (Tile tile in TileManager.REGISTRY.Values)
            {
                RegisterItem(new TileItem(tile));
            }

            IRON_PICKAXE = RegisterItem(new Item("Inignoto:iron_pickaxe", 1, 1, true, new Vector3(0.35f, -1.2f, 1.5f), new Vector3(0, 0, 30 * 3.14f / 180.0f), new Vector3(2.0f, 2.0f, 2.0f)));
        }

        public static void DrawItems(GameTime time)
        {
            if (ticks > 100) return;
            
            foreach (Item item in REGISTRY.Values)
            {
                if (ticks == 0)
                {
                    item.TrySetModel(time);
                } else
                {
                    if (item.Model != null)
                    item.Draw(Inignoto.game.GraphicsDevice, GameResources.effect, 1920, 1080, time);
                }
            }
            ticks++;
        }

        public static Item RegisterItem(Item item)
        {
            REGISTRY.Add(item.Name, item);
            if (!(item is TileItem && ((TileItem)item).tile == TileManager.AIR))
            ITEM_LIST.Add(item);
            return item;
        }

        public static Item GetItemForTile(Tile tile)
        {
            REGISTRY.TryGetValue(tile.name, out Item value);
            return value;
        }
    }
}
