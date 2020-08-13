using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.GameSettings
{
    public class InputSetting
    {
        public int Input { get; private set; }
        public bool Mouse { get; private set; }

        private bool justPressed = false;

        private static List<InputSetting> inputs = new List<InputSetting>();

        public InputSetting(int input, bool mouse)
        {
            this.Input = input;
            this.Mouse = mouse;
            inputs.Add(this);
        }

        public InputSetting(Keys input, bool mouse)
        {
            this.Input = (int)input;
            this.Mouse = mouse;
            inputs.Add(this);
        }

        public void Read(string str)
        {
            string[] data = str.Split(',');

            int.TryParse(data[0], out int input);
            bool.TryParse(data[1], out bool mouse);
            this.Input = input;
            this.Mouse = mouse;
        }

        public string Write()
        {
            return Input + "," + Mouse;
        }

        public bool IsPressed()
        {

            if (Mouse)
            {
                switch (Input) {
                    case 0:
                        return Microsoft.Xna.Framework.Input.Mouse.GetState().LeftButton == ButtonState.Pressed;
                    case 1:
                        return Microsoft.Xna.Framework.Input.Mouse.GetState().RightButton == ButtonState.Pressed;
                    case 2:
                        return Microsoft.Xna.Framework.Input.Mouse.GetState().MiddleButton == ButtonState.Pressed;
                }
                
            } else
            {
                return Keyboard.GetState().IsKeyDown((Keys)Input);
            }
            return false;
        }

        private long lastPressed = 0;

        public bool IsJustPressed()
        {
            if (IsPressed() && Inignoto.game.currentFrame <= lastPressed)
            {
                return true;
            }
            return false;
        }

        public static void Update()
        {
            
            foreach (InputSetting input in inputs)
            {
                if (input.IsPressed())
                {
                    if (!input.justPressed)
                    {
                        input.justPressed = true;
                        input.lastPressed = Inignoto.game.currentFrame;
                    }
                } else
                {
                    input.justPressed = false;
                }
            }
            
        }
    }
}
