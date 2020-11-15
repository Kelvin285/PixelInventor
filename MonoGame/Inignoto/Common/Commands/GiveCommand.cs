using Inignoto.Entities;
using Inignoto.Entities.Player;
using Inignoto.Inventory;
using Inignoto.Items;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common.Commands
{
    public class GiveCommand : Command
    {
        public GiveCommand() : base("give")
        {

        }

        public override string AutoComplete(string current)
        {
            int spaces = 0;
            for (int i = 0; i < current.Length; i++)
            {
                if (current[i] == ' ') spaces++;
            }
            if (spaces == 1)
            {
                return AutoCompletePlayername(current);
            } else
            {
                if (spaces == 2)
                {
                    return AutoCompleteItem(current);
                }
            }
            return string.Empty;
        }

        public override void Execute(long sender, string[] data)
        {
            if (data.Length == 3 || data.Length == 4)
            {
                string player = data[1];
                string item = data[2];
                int count = 1;

                PlayerEntity PLAYER = null;
                Item ITEM = null;

                foreach (long key in Inignoto.game.client_system.PLAYERS.Keys)
                {
                    PlayerEntity p = Inignoto.game.client_system.PLAYERS[key];
                    if (p.Name.Equals(player))
                    {
                        PLAYER = p;
                        break;
                    }
                }

                foreach (string key in ItemManager.REGISTRY.Keys)
                {
                    if (key.Equals(item))
                    {
                        ITEM = ItemManager.REGISTRY[key];
                    }
                }

                if (PLAYER == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "Could not find a player by the name of: " + player);
                    return;
                }

                if (ITEM == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "Could not find the item: " + item);
                    return;
                }

                if (count < 0)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "Item count must be greater than zero!");
                    return;
                }

                if (data.Length == 4)
                {
                    count = int.TryParse(data[3], out int r) ? (int)MathF.Max(0, r) : 1;
                }

                Inignoto.game.world.entities.Add(new ItemEntity(Inignoto.game.world, PLAYER.GetEyePosition(), new ItemStack(ITEM, count), 0));
            }
        }
    }
}
