using Inignoto.Common;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Server
{
    public class ServerSystem : ClientServerSystem
    {
        public override void SendChatMessage(long uid, string message)
        {
            throw new NotImplementedException();
        }

        public override string TryAutoComplete(string text)
        {
            throw new NotImplementedException();
        }
    }
}
