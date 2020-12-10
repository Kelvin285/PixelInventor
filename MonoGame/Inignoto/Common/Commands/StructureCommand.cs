using Inignoto.Entities;
using Inignoto.Entities.Player;
using Inignoto.Inventory;
using Inignoto.Items;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.Utilities;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.Chunks.Chunk;
using static Inignoto.World.World;

namespace Inignoto.Common.Commands
{
    public class StructureCommand : Command
    {

        public Dictionary<long, Dictionary<TilePos, int[]>> clipboard = new Dictionary<long, Dictionary<TilePos, int[]>>();
        public StructureCommand() : base("structure")
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
                return AutoCompleteList(current, new string[] { "save", "load", "copy", "paste" });
            }
            return string.Empty;
        }

        private void Save(long sender, string structure)
        {
            ResourcePath path = new ResourcePath("Structures", "", "Worlds/" + Inignoto.game.world.name);

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
                Inignoto.game.client_system.SendChatMessage(-1, "Cannot save area.  Number of blocks in the area: " + (dx + dy + dz) + " is over the maximum limit of " + (64 ^ 3) + " blocks");
                return;
            }

            TilePos pos = new TilePos(0, 0, 0);

            string file = "";

            World.World world = Inignoto.game.world;

            int min_x = (int)MathF.Min(x1, x2);
            int max_x = (int)MathF.Max(x1, x2);

            int min_y = (int)MathF.Min(y1, y2);
            int max_y = (int)MathF.Max(y1, y2);

            int min_z = (int)MathF.Min(z1, z2);
            int max_z = (int)MathF.Max(z1, z2);

            for (int x = min_x; x <= max_x; x++)
            {
                for (int y = min_y; y <= max_y; y++)
                {
                    for (int z = min_z; z <= max_z; z++)
                    {
                        
                        pos.SetPosition(x, y, z);
                        TileData voxel = world.GetVoxel(pos);
                        TileData overlay = world.GetOverlayVoxel(pos);
                        TileRegistry.GetTile(0);
                        
                        int X = x - min_x;
                        int Y = y - min_y;
                        int Z = z - min_z;
                        file += "\"" + X + "," + Y + "," + Z + "\"=\"" + voxel.index + "," + overlay.index + "\"\n";
                    }
                }
            }
            
            FileUtils.WriteStringToFile(path, new ResourcePath("Structures", structure, "Worlds/" + Inignoto.game.world.name), file);

            Inignoto.game.client_system.SendChatMessage(-1, "Saved structure: " + structure);
        }

        private void Load(long sender, string structure)
        {
            ResourcePath path = new ResourcePath("Structures", structure, "Worlds/" + Inignoto.game.world.name);

            Dictionary<string, string> voxels = FileUtils.LoadFileAsDataList(path);

            Dictionary<TilePos, int[]> clip = new Dictionary<TilePos, int[]>();

            foreach (string key in voxels.Keys)
            {
                if (key.Equals(""))
                {
                    continue;
                }
                string[] data = key.Split(',');
                int x = int.Parse(data[0]);
                int y = int.Parse(data[1]);
                int z = int.Parse(data[2]);

                string[] voxel = voxels[key].Split(',');
                int tiledata = int.Parse(voxel[0]);
                int overlay = int.Parse(voxel[1]);

                clip.Add(new TilePos(x, y, z), new int[] { tiledata, overlay });
            }

            if (!clipboard.ContainsKey(sender))
            {
                clipboard.Add(sender, clip);
            } else
            {
                clipboard[sender] = clip;
            }

            Inignoto.game.client_system.SendChatMessage(-1, "Loaded structure: " + structure + " into the clipboard");
        }

        private void Copy(long sender)
        {
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
                Inignoto.game.client_system.SendChatMessage(-1, "Cannot copy area.  Number of blocks in the area: " + (dx + dy + dz) + " is over the maximum limit of " + (64 ^ 3) + " blocks");
                return;
            }

            TilePos pos = new TilePos(0, 0, 0);
            
            World.World world = Inignoto.game.world;

            int min_x = (int)MathF.Min(x1, x2);
            int max_x = (int)MathF.Max(x1, x2);

            int min_y = (int)MathF.Min(y1, y2);
            int max_y = (int)MathF.Max(y1, y2);

            int min_z = (int)MathF.Min(z1, z2);
            int max_z = (int)MathF.Max(z1, z2);

            Dictionary<TilePos, int[]> clip = new Dictionary<TilePos, int[]>();

            for (int x = min_x; x <= max_x; x++)
            {
                for (int y = min_y; y <= max_y; y++)
                {
                    for (int z = min_z; z <= max_z; z++)
                    {

                        pos.SetPosition(x, y, z);
                        TileData voxel = world.GetVoxel(pos);
                        TileData overlay = world.GetOverlayVoxel(pos);
                        TileRegistry.GetTile(0);

                        int X = x - min_x;
                        int Y = y - min_y;
                        int Z = z - min_z;

                        TilePos pos2 = new TilePos(X, Y, Z);
                        clip.Add(pos2, new int[] { voxel.index, overlay.index });
                    }
                }
            }

            if (!clipboard.ContainsKey(sender))
            {
                clipboard.Add(sender, clip);
            }
            else
            {
                clipboard[sender] = clip;
            }

            Inignoto.game.client_system.SendChatMessage(-1, "Copied the area of " + StructureWrenchItem.pos1 + " and " + StructureWrenchItem.pos2 + " into the clipboard");
        }

        private void Paste(long sender)
        {
            if (!clipboard.ContainsKey(sender))
            {
                Inignoto.game.client_system.SendChatMessage(-1, "There is no structure saved to the clipboard");
                return;
            }
            Dictionary<TilePos, int[]> voxels = clipboard[sender];

            if (StructureWrenchItem.pos1 == null)
            {
                Inignoto.game.client_system.SendChatMessage(-1, "You need to set the first position with the Structure Wrench!");
                return;
            }
            
            TilePos pos2 = new TilePos(0, 0, 0);
            foreach (TilePos pos in voxels.Keys)
            {
                int[] voxel = voxels[pos];
                pos2.x = (int)StructureWrenchItem.pos1.X + pos.x;
                pos2.y = (int)StructureWrenchItem.pos1.Y + pos.y;
                pos2.z = (int)StructureWrenchItem.pos1.Z + pos.z;

                Inignoto.game.world.SetVoxel(pos2, TileDataHolder.REGISTRY[voxel[0]], TileDataHolder.REGISTRY[voxel[1]]);
            }
            Inignoto.game.client_system.SendChatMessage(-1, "Pasted the contents of the clipboard at position: " + StructureWrenchItem.pos1);
        }

        public override void Execute(long sender, string[] data)
        {
            if (data.Length >= 2)
            {
                string command = data[1];

                string structure = "";
                if (data.Length > 2)
                {
                    for (int i = 2; i < data.Length; i++)
                    {
                        structure += data[i];
                        if (i < data.Length - 1) structure += " ";
                    }
                }
                

                if (command.Equals("save"))
                {
                    if (data.Length <= 2)
                    {

                        Inignoto.game.client_system.SendChatMessage(-1, "INVALID COMMAND USAGE!");
                        Inignoto.game.client_system.SendChatMessage(-1, "Use: /structure save <structure path>");
                        return;
                    }
                    Save(sender, structure);
                }

                if (command.Equals("load"))
                {
                    if (data.Length <= 2)
                    {

                        Inignoto.game.client_system.SendChatMessage(-1, "INVALID COMMAND USAGE!");
                        Inignoto.game.client_system.SendChatMessage(-1, "Use: /structure load <structure path>");
                        return;
                    }
                    Load(sender, structure);
                }

                if (command.Equals("copy"))
                {
                    Copy(sender);
                }

                if (command.Equals("paste"))
                {
                    Paste(sender);
                }
            } else
            {
                Inignoto.game.client_system.SendChatMessage(-1, "INVALID COMMAND USAGE!");
                Inignoto.game.client_system.SendChatMessage(-1, "Use: /structure [save/load] <structure path>");
                Inignoto.game.client_system.SendChatMessage(-1, "Or: /structure [copy/paste]");
            }
        }
    }
}
