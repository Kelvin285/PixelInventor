using Inignoto.Entities.Player;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common
{
    public abstract class ClientServerSystem
    {
        public Dictionary<long, PlayerEntity> PLAYERS { get; private set; }

        public ChatSystem chat;

        public ClientServerSystem()
        {
            PLAYERS = new Dictionary<long, PlayerEntity>();
            chat = new ChatSystem(this);
        }

        public abstract void SendChatMessage(long uid, string message);

        public abstract string TryAutoComplete(string text);
    }
}
