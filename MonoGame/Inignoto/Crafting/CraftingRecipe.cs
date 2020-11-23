using Inignoto.Inventory;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Crafting
{
    public class CraftingRecipe
    {
        public ItemStack[] input;
        public ItemStack output;

        public CraftingRecipe(ItemStack[] input, ItemStack output)
        {
            this.input = input;
            this.output = output;
        }
    }
}
