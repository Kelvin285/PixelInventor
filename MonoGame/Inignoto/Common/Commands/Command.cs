﻿using Inignoto.Items;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common.Commands
{
    public abstract class Command
    {
        public readonly string line;
        public Command(string line)
        {
            this.line = line;
        }

        public abstract string AutoComplete(string current);

        public abstract void Execute(long sender, string[] data);

        protected string AutoCompletePlayername(string current)
        {
            string[] split = current.Split(' ');
            if (split.Length == 1)
            {
                foreach (var key in Inignoto.game.client_system.PLAYERS.Keys)
                {
                    return current + Inignoto.game.client_system.PLAYERS[key].Name;
                }
            }
            else
            {
                bool equals = false;
                foreach (var key in Inignoto.game.client_system.PLAYERS.Keys)
                {
                    if (Inignoto.game.client_system.PLAYERS[key].Name.ToLower().StartsWith(split[split.Length - 1].ToLower()) &&
                        !Inignoto.game.client_system.PLAYERS[key].Name.ToLower().Equals(split[split.Length - 1].ToLower()))
                    {
                        return current.Substring(0, current.Length - split[split.Length - 1].Length) + Inignoto.game.client_system.PLAYERS[key].Name;
                    }
                    if (equals)
                    {
                        return current.Substring(0, current.Length - split[split.Length - 1].Length) + Inignoto.game.client_system.PLAYERS[key].Name;
                    }
                    if (Inignoto.game.client_system.PLAYERS[key].Name.ToLower().Equals(split[split.Length - 1].ToLower())) equals = true;
                }
            }
            return string.Empty;
        }

        protected string AutoCompleteItem(string current)
        {
            string[] split = current.Split(' ');
            if (split.Length == 1)
            {
                foreach (var key in ItemManager.REGISTRY.Keys)
                {
                    return current + key;
                }
            }
            else
            {
                bool air = false;
                bool equals = false;
                foreach (var key in ItemManager.REGISTRY.Keys)
                {
                    if (!air)
                    {
                        air = true;
                        continue;
                    }
                    if (key.ToLower().StartsWith(split[split.Length - 1].ToLower()) &&
                        !key.ToLower().Equals(split[split.Length - 1].ToLower()))
                    {
                        return current.Substring(0, current.Length - split[split.Length - 1].Length) + key;
                    }
                    if (equals)
                    {
                        return current.Substring(0, current.Length - split[split.Length - 1].Length) + key;
                    }
                    if (key.ToLower().Equals(split[split.Length - 1].ToLower())) equals = true;
                }
            }
            return string.Empty;
        }
    }
}
