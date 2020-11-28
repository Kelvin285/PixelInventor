using Inignoto.Items;
using Inignoto.Tiles;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Inventory
{
    public class ItemStack
    {
        public int count = 0;
        public int damage = 0;

        public Item item;

        public ItemStack(Item item, int count = 1)
        {
            this.item = item;
            this.count = count;
        }

        public ItemStack(Tile tile, int count = 1)
        {
            item = ItemRegistry.GetItemForTile(tile);
            this.count = count;
        }

        internal void SetToAir()
        {
            item = ItemRegistry.GetItemForTile(TileRegistry.AIR);
            count = 0;
            damage = 0;
        }
    }
}
