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

        public static void RegisterCommands()
        {
            RegisterCommand(GIVE = new GiveCommand());
            RegisterCommand(KILL = new KillCommand());
        }

        public static void RegisterCommand(Command command)
        {
            REGISTRY.Add(command);
        }
    }
}
