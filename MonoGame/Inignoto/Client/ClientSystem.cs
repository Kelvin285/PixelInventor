using Inignoto.Common;
using Inignoto.Common.Commands;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Client
{
    public class ClientSystem : ClientServerSystem
    {
        public void RenderBackground(SpriteBatch batch, GraphicsDevice device, GameTime time, int width, int height)
        {
            chat.RenderBackground(batch, device, time, width, height);
        }

        public void Render(SpriteBatch batch, GraphicsDevice device, GameTime time, int width, int height)
        {
            chat.Render(batch, device, time, width, height);
        }

        public override void SendChatMessage(long uid, string message)
        {
            chat.messages.Add(new ChatSystem.ChatMessage(uid, message, this));

            if (message.StartsWith('/') && message.Length > 0)
            {
                message = message.Substring(1, message.Length - 1);
                for (int i = 0; i < CommandManager.REGISTRY.Count; i++)
                {
                    if (message.StartsWith(CommandManager.REGISTRY[i].line))
                    {
                        CommandManager.REGISTRY[i].Execute(uid, message.Split(" "));
                    }
                }
            }
        }

        public override string TryAutoComplete(string message)
        {
            if (message.StartsWith('/') && message.Length > 0)
            {
                message = message.Substring(1, message.Length - 1);
                for (int i = 0; i < CommandManager.REGISTRY.Count; i++)
                {
                    if (message.StartsWith(CommandManager.REGISTRY[i].line))
                    {
                        return CommandManager.REGISTRY[i].AutoComplete(message);
                    }
                }
            }
            return string.Empty;
        }

    }
}
