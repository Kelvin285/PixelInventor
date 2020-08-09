using Inignoto.Audio;
using Inignoto.Graphics.Fonts;
using Inignoto.Math;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Graphics.Gui
{
    public class InventoryGui : Hud
    {
        private float DropdownAnimation = 1.0f;
        private bool closing = false;


        public InventoryGui()
        {
            OverrideHealthbar = true;
        }

        public override void Close()
        {
            closing = true;
        }

        public override void Render(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {
            if (!closing)
            {
                DropdownAnimation = MathHelper.Lerp(DropdownAnimation, 0.0f, 0.1f);
            } else
            {
                DropdownAnimation = MathHelper.Lerp(DropdownAnimation, 1.0f, 0.1f);
                if (DropdownAnimation >= 0.9f)
                {
                    openGui = null;
                }
            }

            
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(0, -(int)(DropdownAnimation * 360 * 3), 640 * 3, 293 * 3), new Rectangle(0, 0, 640, 293), Color.White);

            if (Inignoto.game.player != null)
            {
                char[] ch = Inignoto.game.player.Name.ToCharArray();
                for (int i = 0; i < ch.Length; i++)
                {
                    int x = 89 * 3 + i * 12;
                    int y = -(int)(DropdownAnimation * 360 * 3) + 227 * 3 - (int)(i * 0.5f);
                    DrawString(spriteBatch, width, height, x, y, 0.5f, FontManager.mandrill_bold, ""+ch[i], Color.White);
                }
                
            }

            int drop = -(int)(DropdownAnimation * 360 * 3);

            RenderHealthbar(device, spriteBatch, width, height, time, 67 * 3, drop + 245 * 3);


            //main inventory icon (Backpack)
            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44, 38, 38), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3 + 19 * 3 - 10 * 3, 2 * 3 + drop + 19 * 3 - (int)(19 * 3 * 0.5), 20 * 3, 19 * 3), new Rectangle(40, 44, 20, 19), Color.White);

            //digital inventory icon (Computer)
            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + 40 * 3 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44, 38, 38), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3 + 19 * 3 - 11 * 3, 2 * 3 + 40 * 3 + drop + 19 * 3 - (int)(19 * 3 * 0.5), 22 * 3, 19 * 3), new Rectangle(63, 44, 22, 19), Color.White);


            //0, 44, 38, 38 <- Circle
            //40, 44, 20, 19 <- Backpack
            //63, 44, 22, 19 <- computer

            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(236 * 3, 258 * 3 + drop, 14 * 3, 18 * 3), new Rectangle(57, 342, 14, 18), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(264 * 3, 262 * 3 + drop, 13 * 3, 11 * 3), new Rectangle(73, 347, 13, 11), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(63 * 3, 115 * 3 + drop, 16 * 3, 18 * 3), new Rectangle(89, 340, 16, 18), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(63 * 3, 152 * 3 + drop, 17 * 3, 16 * 3), new Rectangle(107, 340, 17, 16), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(65 * 3, 189 * 3 + drop, 13 * 3, 15 * 3), new Rectangle(126, 340, 13, 15), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(166 * 3, 174 * 3 + drop, 17 * 3, 21 * 3), new Rectangle(141, 337, 17, 21), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(168 * 3, 105 * 3 + drop, 14 * 3, 14 * 3), new Rectangle(160, 340, 14, 14), Color.White);
            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(168 * 3, 141 * 3 + drop, 14 * 3, 14 * 3), new Rectangle(160, 340, 14, 14), Color.White);


            //57, 342, 14, 18 <- Trash Can (236, 258)
            //73, 347, 13, 11 <- Hammer (264, 262)
            //89, 340, 16, 18 <- Head Slot (63, 115)
            //107, 340, 17, 16 <- Shirt Slot (63, 152)
            //126, 340, 13, 15 <- Leggings Slot (65, 188)
            //141, 337, 17, 21 <- Shield Slot (166, 174)
            //160, 340, 14, 14 <- Accessory Slot (168, 105), (168, 141)
        }


    }
}
