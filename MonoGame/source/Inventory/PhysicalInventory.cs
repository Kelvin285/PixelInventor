using Inignoto.Entities.Player;
using Inignoto.Items;
using Inignoto.Tiles;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Inventory
{
    public class PhysicalInventory
    {
        public ItemStack[] hotbar = new ItemStack[10];
        public ItemStack[] inventory = new ItemStack[30];
        public ItemStack[] accessory = new ItemStack[2];

        public ItemStack head;
        public ItemStack chest;
        public ItemStack legs;
        public ItemStack offhand;
        public ItemStack trashStack;

        public ItemStack grabStack;

        public readonly PlayerEntity player;

        public int selected = 0;

        public enum SlotType
        {
            NORMAL, TRASH, HEAD, CHEST, LEGS, OFFHAND, ACCESSORY
        }

        public PhysicalInventory(PlayerEntity player)
        {
            this.player = player;
            inventory[0] = new ItemStack(TileManager.DIRT, 4);
            inventory[1] = new ItemStack(TileManager.GRASS, 64);
            hotbar[0] = new ItemStack(TileManager.GRASS, 64);
        }

        public void SwapStacks(ItemStack A, ItemStack B, out ItemStack a, out ItemStack b, SlotType type)
        {
            a = B;
            b = A;
            if (type == SlotType.TRASH)
            {
                if (A != null && B != null)
                {
                    b = null;
                }
            }
        }

        public ItemStack SplitStack(ItemStack stack, out ItemStack S, SlotType type)
        {
            if (stack != null)
                if (stack.count > 1)
                {
                    int half = stack.count / 2;
                    int count = stack.count;
                    Item item = stack.item;
                    stack.count = half;
                    if (stack.count == 0) S = null; else S = stack;
                    return new ItemStack(item, count - half);
                }
            S = stack;
            return null;
        }

        //-1 = fail, 0 = stack completely filled with some items left over, 1 = added all items successfully
        public int TryAddToStack(ItemStack stack, ItemStack add, out ItemStack a, SlotType type, int amount = -1) 
        {

            if (add != null && stack != null)
            {
                if (amount == -1) amount = add.count;
                if (stack.item == add.item)
                {
                    if (stack.count + amount <= stack.item.max_stack)
                    {
                        stack.count += amount;
                        a = null;
                        return 1;
                    }
                    else
                    {

                        if (stack.count == stack.item.max_stack)
                        {
                            a = add;
                            return -1;
                        }
                        int left = stack.item.max_stack - amount;
                        add.count -= left;
                        stack.count = left;
                        a = add;
                        return 0;
                    }
                }
            }
            a = add;
            return -1;
        }
    }
}
