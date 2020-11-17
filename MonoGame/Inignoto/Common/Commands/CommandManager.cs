using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common.Commands
{
    public class CommandManager
    {
        public static List<Command> REGISTRY = new List<Command>();

        public static Command GIVE;
        public static Command KILL;
        public static Command GAMEMODE;
        public static Command FILL;
        public static Command REPLACE;
        public static Command STRUCTURE;

        public static void RegisterCommands()
        {
            RegisterCommand(GIVE = new GiveCommand());
            RegisterCommand(KILL = new KillCommand());
            RegisterCommand(GAMEMODE = new GamemodeCommand());
            RegisterCommand(FILL = new FillCommand());
            RegisterCommand(REPLACE = new ReplaceCommand());
            RegisterCommand(STRUCTURE = new StructureCommand());
        }

        public static void RegisterCommand(Command command)
        {
            REGISTRY.Add(command);
        }
    }
}
