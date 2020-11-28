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
            inventory[0] = new ItemStack(TileRegistry.DIRT, 4);
            inventory[1] = new ItemStack(TileRegistry.GRASS, 64);
            hotbar[0] = new ItemStack(TileRegistry.GRASS, 64);
            hotbar[1] = new ItemStack(ItemRegistry.IRON_PICKAXE, 1);
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
                if (stack.count == stack.item.max_stack || add.count == add.item.max_stack)
                {
                    a = add;
                    return -1;
                }
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


        public string Save()
        {
            string str = "";
            for (int i = 0; i < hotbar.Length; i++)
            {
                if (hotbar[i] != null)
                {
                    string item = hotbar[i].item.Name;
                    int count = hotbar[i].count;
                    str += item + "," + count + "\n";
                } else
                {
                    str += "NULL\n";
                }
            }
            for (int i = 0; i < inventory.Length; i++)
            {
                if (inventory[i] != null)
                {
                    string item = inventory[i].item.Name;
                    int count = inventory[i].count;
                    str += item + "," + count + "\n";
                }
                else
                {
                    str += "NULL\n";
                }
            }
            for (int i = 0; i < accessory.Length; i++)
            {
                if (accessory[i] != null)
                {
                    string item = accessory[i].item.Name;
                    int count = accessory[i].count;
                    str += item + "," + count + "\n";
                }
                else
                {
                    str += "NULL\n";
                }
            }
            ItemStack[] other = { head, chest, legs, offhand, trashStack };
            for (int i = 0; i < other.Length; i++)
            {
                if (other[i] != null)
                {
                    string item = other[i].item.Name;
                    int count = other[i].count;
                    str += item + "," + count + "\n";
                }
                else
                {
                    str += "NULL\n";
                }
            }
            return str;
        }

        public void Load(string file)
        {
            string[] data = file.Split("\n");
            int I = 1;
            for (int i = 0; i < hotbar.Length; i++)
            {
                if (!data[I].Equals("NULL"))
                {
                    string[] item = data[I].Split(",");
                    hotbar[i] = new ItemStack(ItemRegistry.REGISTRY[item[0]], int.Parse(item[1]));
                } else
                {
                    hotbar[i] = null;
                }
                I++;
            }
            for (int i = 0; i < inventory.Length; i++)
            {
                if (!data[I].Equals("NULL"))
                {
                    string[] item = data[I].Split(",");
                    inventory[i] = new ItemStack(ItemRegistry.REGISTRY[item[0]], int.Parse(item[1]));
                }
                I++;
            }
            for (int i = 0; i < accessory.Length; i++)
            {
                if (!data[I].Equals("NULL"))
                {
                    string[] item = data[I].Split(",");
                    accessory[i] = new ItemStack(ItemRegistry.REGISTRY[item[0]], int.Parse(item[1]));
                } else
                {
                    accessory[i] = null;
                }
                I++;
            }
            ItemStack[] other = { head, chest, legs, offhand, trashStack };
            for (int i = 0; i < other.Length; i++)
            {
                if (!data[I].Equals("NULL"))
                {
                    string[] item = data[I].Split(",");
                    other[i] = new ItemStack(ItemRegistry.REGISTRY[item[0]], int.Parse(item[1]));
                } else
                {
                    other[i] = null;
                }
                I++;
            }
            head = other[0];
            chest = other[1];
            legs = other[2];
            offhand = other[3];
            trashStack = other[4];
        }
    }
}
