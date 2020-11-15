using Inignoto.GameSettings;
using Inignoto.Graphics.Fonts;
using Inignoto.Graphics.Gui;
using Inignoto.Graphics.Textures;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common
{
    public class ChatSystem
    {

        public Color CHATBOX_GRAY = new Color(0.1f, 0.1f, 0.1f, 0.5f);
        public static int character_limit = 50;

        public struct ChatMessage {
            public double TTL;
            public long UID { get; private set;  }
            public List<string> message { get; private set; }
            public ChatMessage(long UID, string msg, ClientServerSystem system)
            {
                message = new List<String>();
                string current = "";
                bool end = false;
                int I = 0;

                this.UID = UID;
                TTL = 60;

                if (msg.StartsWith("/") || msg.Length == 0) return;
                for (int i = 0; i < msg.Length; i++)
                {
                    current += msg[i];
                    end = false;
                    if (I == 0 && UID >= 0)
                    {
                        if (current.Length >= character_limit - system.PLAYERS[UID].Name.Length + 4 - 5)
                        {
                            message.Add(system.PLAYERS[UID].Name + " >> " + current);
                            current = "";
                            end = true;
                            I++;
                        }
                    } else
                    {
                        if (current.Length >= character_limit)
                        {
                            message.Add(current);
                            current = "";
                            end = true;
                            I++;
                        }
                    }
                    
                }
                if (!end)
                    if (I == 0) message.Add(system.PLAYERS[UID].Name + " >> " + current);
                    else message.Add(current);

            }

        }

        public List<ChatMessage> messages;

        public ClientServerSystem system;

        private KeyReader keyReader;

        public ChatSystem(ClientServerSystem system)
        {
            this.system = system;
            messages = new List<ChatMessage>();
            keyReader = new KeyReader(120);
        }

        private bool chatting = false;
        private bool pressed_chat = false;
        private bool pressed_tab = false;

        public void Render(SpriteBatch batch, GraphicsDevice device, GameTime time, int width, int height)
        {
            if (chatting)
            {
                Hud.Draw(batch, width, height, Textures.white_square, new Rectangle(0, 1080 - 30, 1920, 30), CHATBOX_GRAY);
                Hud.DrawString(batch, width, height, 2, 1080 - 36, 0.5f, FontManager.mandrill_regular, keyReader.text + (time.TotalGameTime.TotalMilliseconds % 1000 <= 500 ? "|" : ""), Color.White);

                if (keyReader.finished)
                {
                    system.SendChatMessage(Inignoto.game.player.UID, keyReader.text);
                    chatting = false;
                    keyReader.finished = false;
                    Inignoto.game.paused = false;
                    pressed_chat = true;
                } else
                {
                    keyReader.Update(time);

                    if (Keyboard.GetState().IsKeyDown(Keys.Tab))
                    {
                        if (!pressed_tab)
                        {
                            pressed_tab = true;

                            string auto = system.TryAutoComplete(keyReader.text);
                            if (auto != string.Empty)
                            {
                                keyReader.text = "/" + auto;
                            }
                        }
                    } else
                    {
                        pressed_tab = false;
                    }
                }
            }
        }
        public void RenderBackground(SpriteBatch batch, GraphicsDevice device, GameTime time, int width, int height)
        {

            if (Settings.CHAT.IsPressed())
            {
                if (!pressed_chat)
                {
                    if (!chatting)
                    {
                        keyReader.text = "";
                        keyReader.finished = false;
                        chatting = true;
                        Inignoto.game.paused = true;
                    }

                    pressed_chat = true;
                }
            } else
            {
                pressed_chat = false;
            }

            double delta = time.ElapsedGameTime.TotalMilliseconds * 60;


            int I = 0;
            for (int i = (int)MathF.Min(15, messages.Count - 1); i >= 0; i--)
            {
                ChatMessage message = messages[i];

                for (int j = message.message.Count - 1; j >= 0; j--)
                {
                    string msg = message.message[j];
                    Hud.DrawString(batch, width, height, 2, 1080 - I * 30 - 150, 0.5f, FontManager.mandrill_regular, msg, Color.White);
                    I++;
                }
                

                message.TTL -= delta;
                if (message.TTL <= 0) messages.Remove(message);
            }
            while (messages.Count >= 15)
            {
                messages.Remove(messages[0]);
            }
        }

    }
}
