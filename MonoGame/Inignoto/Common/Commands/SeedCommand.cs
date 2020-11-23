using Inignoto.Entities;
using Inignoto.Entities.Player;
using Inignoto.Inventory;
using Inignoto.Items;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common.Commands
{
    public class SeedCommand : Command
    {
        public SeedCommand() : base("seed")
        {

        }

        public override string AutoComplete(string current)
        {
            return string.Empty;
        }

        public override void Execute(long sender, string[] data)
        {
            Inignoto.game.client_system.SendChatMessage(-1, "World seed: " + Inignoto.game.world.properties.seed);
        }
    }
}
