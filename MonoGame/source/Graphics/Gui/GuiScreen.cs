using Inignoto.Audio;
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
    public class GuiScreen
    {

        private float Fade = 0;

        private GameSound heartbeat;
        private GameSound death_noise;

        public virtual void Render(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {
            if (heartbeat == null)
            {
                heartbeat = new GameSound(SoundEffects.player_heartbeat.CreateInstance(), SoundType.PLAYERS);
                death_noise = new GameSound(SoundEffects.player_death_sound.CreateInstance(), SoundType.PLAYERS);
            }
            float health = Inignoto.game.player.health;
            
            if (health <= 0)
            {
                if (Fade < 1)
                {
                    Fade = MathHelper.Lerp(Fade, 1, 0.01f);
                    
                    if (System.Math.Abs(Fade) >= 1 - 0.01f)
                    {
                        Fade = 1;
                        Inignoto.game.player.position = new Vector3f(Inignoto.game.player.SpawnPosition);
                        Inignoto.game.player.health = 50;
                    }
                }
            } else
            {
                if (Fade > 0)
                {
                    Fade = MathHelper.Lerp(Fade, 0, 0.01f);
                    if (System.Math.Abs(Fade) <= 0.01f) Fade = 0;
                }
            }

            if (Inignoto.game.player.health <= 0)
            {
                death_noise.Volume = 1.0f - Fade;
                if (death_noise.State != SoundState.Playing)
                {
                    death_noise.Play();
                }
            } else
            {
                death_noise.Stop();
            }

            spriteBatch.Draw(Textures.Textures.viginette, new Rectangle(0, 0, width, height), new Color(255 - (int)(255 * (health / 100)), 0, 0, 255 - (int)(255 * (health / 100))));

            spriteBatch.Draw(Textures.Textures.fp_cursor, new Rectangle(width / 2 - 8, height / 2 - 8, 16, 16), Inignoto.game.camera.highlightedTile == null ? Color.Gray : Color.White);

            int heartBeat = 0;

            if (health <= 50)
            {
                double millis = time.TotalGameTime.TotalMilliseconds % 1000;
                if (millis <= 100)
                {
                    heartBeat = 2;
                    if (heartbeat.State != SoundState.Playing)
                    {
                        
                        heartbeat.Volume = (1.0f - (health / 50.0f)) * 0.5f;
                        if (Inignoto.game.player.health <= 0)
                        {
                            heartbeat.Volume = 0;
                        }

                        if (health > 25)
                        {
                            heartbeat.Volume = 0.1f;
                        }

                        heartbeat.Pitch = -0.25f;
                        heartbeat.Play();
                        
                        
                    }
                }
            }

            //healthbar

            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(10 - heartBeat, 10 - heartBeat, 32 + heartBeat * 2, 32 + heartBeat * 2), new Rectangle(2, 0, 9, 9), Color.White);
            
            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(48, 10, 88 * 3, 9 * 3), new Rectangle(14, 0, 88, 9), Color.White);

            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(48, 10, (int)(88 * health / 100) * 3, 9 * 3), new Rectangle(14, 10, (int)(88 * health / 100), 9), Color.White);

            //14
            int r = (int)health - (100 - 14);
            if (r < 0) r = 0;

            int r2 = 15 - (health > 15 ? 15 : (int)health);

            int frame = 0;
            frame = (int)(time.TotalGameTime.TotalMilliseconds / 500) % 3;

            spriteBatch.Draw(Textures.Textures.hud, new Rectangle(48 + (int)(88 * health / 100) * 3 - (13 * 3) + r2 * 3, 10, (26 - r) * 3 - r2 * 3, 8 * 3), new Rectangle(103 + r2, 8 * frame, 26 - r - r2, 8), Color.White);

            spriteBatch.Draw(Textures.Textures.white_square, new Rectangle(0, 0, width, height), new Color(0, 0, 0, Fade));

        }
    }
}
