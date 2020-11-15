using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Common
{
    public class KeyReader
    {
        private KeyboardState currentState;
        private KeyboardState oldState;

        public string text;
        public bool finished;
        private bool enter_released;

        private int type_limit;
    
        public KeyReader(int type_limit = int.MaxValue)
        {
            text = "";
            finished = false;
            enter_released = false;
            this.type_limit = type_limit;
        }

        public string MapKey(string key, bool second)
        {
            if (second)
            {
                switch (key)
                {
                    case "1": return "!";
                    case "2": return "@";
                    case "3": return "#";
                    case "4": return "$";
                    case "5": return "%";
                    case "6": return "^";
                    case "7": return "&";
                    case "8": return "*";
                    case "9": return "(";
                    case "0": return ")";
                    case "OemPeriod": return ">";
                    case "OemComma": return "<";
                    case "OemQuestion": return "?";
                    case "OemSemicolon": return ":";
                    case "OemQuotes": return "\"";
                    case "OemOpenBrackets": return "{";
                    case "OemCloseBrackets": return "}";
                    case "OemPipe":return "|";
                    case "OemPlus": return "+";
                    case "OemMinus": return "_";
                    case "OemTilde": return "~";
                }
            }
            switch (key)
            {
                case "OemPeriod": return ".";
                case "OemComma": return ",";
                case "OemQuestion": return "/";
                case "OemSemicolon": return ";";
                case "OemQuotes": return "'";
                case "OemOpenBrackets": return "[";
                case "OemCloseBrackets": return "]";
                case "OemPipe": return "" + '\\';
                case "OemPlus": return "=";
                case "OemMinus": return "-";
                case "OemTilde": return "`";
            }


            return key;
        }

        private Dictionary<Keys, double> lastPressed = new Dictionary<Keys, double>();
        public void Update(GameTime time)
        {
            if (!finished)
            {
                oldState = currentState;
                currentState = Keyboard.GetState();

                if (currentState.IsKeyUp(Keys.Enter))
                {
                    enter_released = true;
                } else
                {
                    if (enter_released == false)
                    {
                        return;
                    }
                }

                Keys[] pressed;
                pressed = currentState.GetPressedKeys();

                foreach (var key in pressed)
                {
                    if (!lastPressed.ContainsKey(key))
                    {
                        lastPressed.Add(key, time.TotalGameTime.TotalMilliseconds);
                    }
                    if (oldState.IsKeyUp(key) || time.TotalGameTime.TotalMilliseconds > lastPressed[key] + 500)
                    {
                        if (oldState.IsKeyUp(key))
                        {
                            lastPressed[key] = time.TotalGameTime.TotalMilliseconds;
                        }
                        if (key == Keys.Back && text.Length > 0)
                        {
                            text = text.Remove(text.Length - 1, 1);
                        }
                        else
                        {
                            if (key == Keys.Space)
                            {
                                text += " ";
                            } else
                            {
                                if (key == Keys.Enter)
                                {
                                    finished = true;
                                    enter_released = false;
                                } else
                                {
                                    string kstr = key.ToString();

                                    bool upper = currentState.IsKeyDown(Keys.LeftShift) || currentState.IsKeyDown(Keys.RightShift);
                                    if (currentState.CapsLock)
                                    {
                                        upper = !upper;
                                    }

                                    if (kstr.Length == 2 && kstr.StartsWith("D"))
                                    {
                                        kstr = kstr.Remove(0, 1);
                                    }
                                    
                                    kstr = MapKey(kstr, currentState.IsKeyDown(Keys.LeftShift) || currentState.IsKeyDown(Keys.RightShift));

                                    if (kstr.Length == 1)
                                    {
                                        text += upper ? kstr.ToUpper() : kstr.ToLower();
                                    }

                                }
                            }
                        }
                    }
                    text = text.Substring(0, (int)MathF.Min(text.Length, type_limit));
                }
            }
        }
    }
}
