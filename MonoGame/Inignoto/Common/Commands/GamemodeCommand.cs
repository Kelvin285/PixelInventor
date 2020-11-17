using Inignoto.Entities;
using Inignoto.Entities.Player;
using Inignoto.Inventory;
using Inignoto.Items;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.Entities.Player.PlayerEntity;

namespace Inignoto.Common.Commands
{
    public class GamemodeCommand : Command
    {
        public GamemodeCommand() : base("mode")
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
            if (spaces == 2)
            {
                return AutoCompleteList(current, new string[] { "survival", "sandbox", "freecam" });
            }
            return string.Empty;
        }

        public override void Execute(long sender, string[] data)
        {
            if (data.Length == 3)
            {
                string player = data[1];
                string gamemode = data[2];

                int GM = -1;
                switch (gamemode)
                {
                    case "survival":
                        GM = 0;
                        break;
                    case "sandbox":
                        GM = 1;
                        break;
                    case "freecam":
                        GM = 2;
                        break;
                    default: break;
                }
                if (int.TryParse(gamemode, out int gm))
                {
                    GM = gm;
                }

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

                if (PLAYER == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, player + " is currently in " + PLAYER.gamemode.ToString() + " mode.");
                    return;
                }

                switch (GM)
                {
                    case 0: PLAYER.gamemode = Gamemode.SURVIVAL; break;
                    case 1: PLAYER.gamemode = Gamemode.SANDBOX; break;
                    case 2: PLAYER.gamemode = Gamemode.FREECAM; break;
                    default: break;
                }
                Inignoto.game.client_system.SendChatMessage(-1, "Set the mode of " + player + " to " + PLAYER.gamemode.ToString());
            } else
            {
                Inignoto.game.client_system.SendChatMessage(-1, "INVALID COMMAND USAGE!");
                Inignoto.game.client_system.SendChatMessage(-1, "Use: /mode [playername] [mode (survival, freecam, sandbox)]");
            }
        }
    }
}
