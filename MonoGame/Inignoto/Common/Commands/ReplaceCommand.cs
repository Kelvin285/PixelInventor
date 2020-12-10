using Inignoto.Entities;
using Inignoto.Entities.Player;
using Inignoto.Inventory;
using Inignoto.Items;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.Common.Commands
{
    public class ReplaceCommand : Command
    {
        public ReplaceCommand() : base("replace")
        {

        }

        public override string AutoComplete(string current)
        {
            int spaces = 0;
            for (int i = 0; i < current.Length; i++)
            {
                if (current[i] == ' ') spaces++;
            }

            if (spaces == 1 || spaces == 2)
            {
                return AutoCompleteTile(current);
            }

            return string.Empty;
        }

        public override void Execute(long sender, string[] data)
        {
            if (data.Length == 3 || data.Length == 4)
            {
                string tile = data[1];
                string replace = data[2];
                int state = 0;
                if (data.Length == 4)
                {
                    if (int.TryParse(data[3], out int s))
                    {
                        state = s;
                    }
                    else
                    {
                        Inignoto.game.client_system.SendChatMessage(-1, data[3] + " is not a valid integer!");
                        return;
                    }
                }

                Tile TILE = null;
                Tile REPLACE = null;

                foreach (string key in TileRegistry.REGISTRY.Keys)
                {
                    if (key.Equals(tile))
                    {
                        TILE = TileRegistry.REGISTRY[key];
                        break;
                    }
                }

                foreach (string key in TileRegistry.REGISTRY.Keys)
                {
                    if (key.Equals(replace))
                    {
                        REPLACE = TileRegistry.REGISTRY[key];
                        break;
                    }
                }

                if (TILE == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "Could not find the tile: " + tile);
                    return;
                }

                if (REPLACE == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "Could not find the tile: " + tile);
                    return;
                }

                TileData tiledata = TILE.stateHolder.getStateFor(state);
                TileData replacedata = REPLACE.stateHolder.getStateFor(state);

                if (StructureWrenchItem.pos1 == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "You need to set the first position with the Structure Wrench!");
                    return;
                }

                if (StructureWrenchItem.pos2 == null)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "You need to set the second position with the Structure Wrench!");
                    return;
                }

                int x1 = (int)StructureWrenchItem.pos1.X;
                int y1 = (int)StructureWrenchItem.pos1.Y;
                int z1 = (int)StructureWrenchItem.pos1.Z;

                int x2 = (int)StructureWrenchItem.pos2.X;
                int y2 = (int)StructureWrenchItem.pos2.Y;
                int z2 = (int)StructureWrenchItem.pos2.Z;

                int dx = (int)MathF.Abs(x1 - x2);
                int dy = (int)MathF.Abs(y1 - y2);
                int dz = (int)MathF.Abs(z1 - z2);

                if (dx + dy + dz >= 64 * 64 * 64)
                {
                    Inignoto.game.client_system.SendChatMessage(-1, "Cannot fill area.  Number of blocks in the area: " + (dx + dy + dz) + " is over the maximum limit of " + (64 ^ 3) + " blocks");
                    return;
                }

                TilePos pos = new TilePos(0, 0, 0);

                for (int x = (int)MathF.Min(x1, x2); x <= MathF.Max(x1, x2); x++)
                {
                    for (int y = (int)MathF.Min(y1, y2); y <= MathF.Max(y1, y2); y++)
                    {
                        for (int z = (int)MathF.Min(z1, z2); z <= MathF.Max(z1, z2); z++)
                        {
                            

                            pos.x = x;
                            pos.y = y;
                            pos.z = z;
                            if (Inignoto.game.world.GetVoxel(pos) == tiledata)
                            Inignoto.game.world.SetVoxel(pos, replacedata);
                        }
                    }
                }

                Inignoto.game.client_system.SendChatMessage(-1, "Replaced all instances of " + tile + " between " + StructureWrenchItem.pos1 + " and " + StructureWrenchItem.pos2 + " with " + replace + " (state " + state + ")");
            } else
            {
                Inignoto.game.client_system.SendChatMessage(-1, "INVALID COMMAND USAGE!");
                Inignoto.game.client_system.SendChatMessage(-1, "Use: /replace [tile] [tile] <state>");
            }
        }
    }
}
