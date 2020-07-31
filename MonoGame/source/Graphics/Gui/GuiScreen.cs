using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Graphics.Gui
{
    public class GuiScreen
    {
        public virtual void Render(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {



            float health = Inignoto.game.player.health;

            spriteBatch.Draw(Textures.Textures.viginette, new Rectangle(0, 0, width, height), new Color(255 - (int)(255 * (health / 100)), 0, 0, 255 - (int)(255 * (health / 100))));

            spriteBatch.Draw(Textures.Textures.fp_cursor, new Rectangle(width / 2 - 8, height / 2 - 8, 16, 16), Inignoto.game.camera.highlightedTile == null ? Color.Gray : Color.White);

            int heartBeat = 0;

            if (health <= 50)
            {
                double millis = time.TotalGameTime.TotalMilliseconds % (int)MathHelper.Lerp(1000, 300, 1.0f - (health / 50));
                if (millis <= 100)
                {
                    heartBeat = 2;
                }
            }

            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(10 - heartBeat, 10 - heartBeat, 32 + heartBeat * 2, 32 + heartBeat * 2), new Rectangle(2, 0, 9, 9), Color.White);
            
            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(48, 10, 88 * 3, 9 * 3), new Rectangle(14, 0, 88, 9), Color.White);

            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(48, 10, (int)(88 * health / 100) * 3, 9 * 3), new Rectangle(14, 10, (int)(88 * health / 100), 9), Color.White);

            //14
            int r = (int)health - (100 - 14);
            if (r < 0) r = 0;

            int r2 = 15 - (health > 15 ? 15 : (int)health);
            

            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(48 + (int)(88 * health / 100) * 3 - (13 * 3) + r2 * 3, 10, (26 - r) * 3 - r2 * 3, 8 * 3), new Rectangle(103 + r2, 0, 26 - r - r2, 8), Color.White);


        }
    }
}
