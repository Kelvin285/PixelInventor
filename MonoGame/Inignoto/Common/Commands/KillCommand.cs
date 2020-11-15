using Inignoto.Entities;
using Inignoto.Entities.Player;
using Inignoto.Inventory;
using Inignoto.Items;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common.Commands
{
    public class KillCommand : Command
    {
        public KillCommand() : base("kill")
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
            }
            return string.Empty;
        }

        public override void Execute(long sender, string[] data)
        {
            if (data.Length == 2)
            {
                string player = data[1];

                PlayerEntity PLAYER = null;

                foreach (long key in Inignoto.game.client_system.PLAYERS.Keys)
                {
                    PlayerEntity p = Inignoto.game.client_system.PLAYERS[key];
                    if (p.Name.Equals(player))
                    {
                        PLAYER = p;
                        break;
                    }
                }

                if (PLAYER == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "Could not find a player by the name of: " + player);
                    return;
                }
                PLAYER.DamageEntity(float.MaxValue, true);
            }
        }
    }
}
