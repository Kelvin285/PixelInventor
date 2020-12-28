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
    public class ItemRegistry
    {
        public static Dictionary<string, Item> REGISTRY = new Dictionary<string, Item>(); // Registers items based on 
        public static List<Item> ITEM_LIST = new List<Item>();

        private static int ticks = 0;

        public static Item IRON_PICKAXE;
        public static Item GOLD_PICKAXE;
        public static Item STRUCTURE_WRENCH;

        public static void LoadItems()
        {
            REGISTRY.Clear();
            foreach (Tile tile in TileRegistry.REGISTRY.Values)
            {
                Item item = new TileItem(tile);

                if (tile == TileRegistry.COPPER_ORE)
                {
                    item = new Item("Inignoto:copper_ore", 64, 0.1f, true, new Vector3(-0.5f, -0.7f, 1.5f), new Vector3(0, 0, 0), new Vector3(5.0f, 5.0f, 5.0f));
                } else
                {
                    if (tile.GetItemModel() != string.Empty)
                    {
                        item.SetModel(tile.GetItemModel());
                    }
                }

                RegisterItem(item);
            }
            


            IRON_PICKAXE = RegisterItem(new PickaxeItem("Inignoto:iron_pickaxe", 0.1f, true, new Vector3(0.35f, -1.2f, 1.5f), new Vector3(0, 0, 30 * 3.14f / 180.0f), new Vector3(2.0f, 2.0f, 2.0f)));
            STRUCTURE_WRENCH = RegisterItem(new StructureWrenchItem("Inignoto:structure_wrench", 1, true, new Vector3(0.35f, -1.2f, 1.5f), new Vector3(0, 0, 30 * 3.14f / 180.0f), new Vector3(2.0f, 2.0f, 2.0f)));
            GOLD_PICKAXE = RegisterItem(new PickaxeItem("Inignoto:gold_pickaxe", 0.1f, true, new Vector3(0.35f, -1.2f, 1.5f), new Vector3(0, 0, 30 * 3.14f / 180.0f), new Vector3(2.0f, 2.0f, 2.0f)));

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
            if (!(item is TileItem && ((TileItem)item).tile == TileRegistry.AIR))
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
