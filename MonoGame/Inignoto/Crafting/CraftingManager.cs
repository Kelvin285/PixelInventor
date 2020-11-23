using Inignoto.Items;
using Inignoto.Tiles;
using System;
using System.Collections.Generic;
using System.Text;
using Inignoto.Inventory;

namespace Inignoto.Crafting
{
    public class CraftingManager
    {
        public static List<CraftingRecipe> REGISTRY = new List<CraftingRecipe>();

        public static void RegisterRecipes()
        {
            foreach (Item item in ItemManager.REGISTRY.Values)
            {
                REGISTRY.Add(new CraftingRecipe(new ItemStack[] { new ItemStack(TileManager.LOG) }, new ItemStack(item)));
            }
        }
    }
}
