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
    public class Hud
    {

        protected static float Fade = 0;

        protected static GameSound heartbeat;
        protected static GameSound death_noise;

        public static Hud openGui;

        public Hud renderGui;

        protected bool OverrideHealthbar = false;

        protected static float health = 0;
        protected static int heartBeat = 0;

        public virtual void Render(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {
            if (openGui != null)
            {
                renderGui = openGui;
            }
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
            }
            else
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
            }
            else
            {
                death_noise.Stop();
            }


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

            Hud.heartBeat = heartBeat;
            Hud.health = health;

            Draw(spriteBatch, width, height, Textures.Textures.viginette, new Rectangle(0, 0, 1920, 1080), new Color(255 - (int)(255 * (health / 100)), 0, 0, 255 - (int)(255 * (health / 100))));

            if (renderGui != null)
            {
                renderGui.Render(device, spriteBatch, width, height, time);
                if (!renderGui.OverrideHealthbar)
                {
                    RenderHealthbar(device, spriteBatch, width, height, time);
                }
            } else
            {
                Draw(spriteBatch, width, height, Textures.Textures.fp_cursor, new Rectangle(1920 / 2 - 8, 1080 / 2 - 8, 16 * 3, 16 * 3), Inignoto.game.camera.highlightedTile == null ? Color.Gray : Color.White);

                RenderHealthbar(device, spriteBatch, width, height, time);
            }

            //inventory hotbar
            Draw(spriteBatch, width, height, Textures.Textures.inventory_row, new Rectangle(1920 / 2 - (324 * 3) / 2, 1080 - 40 * 3, 324 * 3, 40 * 3), Color.White);

            /////////////////////////////////////////////
            Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(0, 0, 1920, 1080), new Color(0, 0, 0, Fade));

            if (openGui == null)
            {
                renderGui = null;
            }
        }

        protected void RenderHealthbar(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time, int x = 0, int y = 0)
        {
            //healthbar
            
            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(x + 10 - heartBeat, y + 10 - heartBeat, 32 + heartBeat * 2, 32 + heartBeat * 2), new Rectangle(2, 0, 9, 9), Color.White);

            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(x + 48, y + 10, 88 * 3, 9 * 3), new Rectangle(14, 0, 88, 9), Color.White);

            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(x + 48, y + 10, (int)(88 * health / 100) * 3, 9 * 3), new Rectangle(14, 10, (int)(88 * health / 100), 9), Color.White);

            //14
            int r = (int)health - (100 - 14);
            if (r < 0) r = 0;

            int r2 = 15 - (health > 15 ? 15 : (int)health);

            int frame = 0;
            frame = (int)(time.TotalGameTime.TotalMilliseconds / 500) % 3;

            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(x + 48 + (int)(88 * health / 100) * 3 - (13 * 3) + r2 * 3, y + 10, (26 - r) * 3 - r2 * 3, 8 * 3), new Rectangle(103 + r2, 8 * frame, 26 - r - r2, 8), Color.White);

        }

        protected void Draw(SpriteBatch batch, int width, int height, Texture2D texture, Rectangle r1, Rectangle r2, Color color, float rotation)
        {
            float x = r1.X * (width / 1920.0f);
            float y = r1.Y * (height / 1080.0f);
            float w = r1.Width * (width / 1920.0f);
            float h = r1.Height * (height / 1080.0f);
#pragma warning disable CS0618 // Type or member is obsolete
            batch.Draw(texture, null, new Rectangle((int)x, (int)y, (int)w, (int)h), r2, null, rotation, null, color);
#pragma warning restore CS0618 // Type or member is obsolete
        }

        protected void Draw(SpriteBatch batch, int width, int height, Texture2D texture, Rectangle r1, Rectangle r2, Color color)
        {
            float x = r1.X * (width / 1920.0f);
            float y = r1.Y * (height / 1080.0f);
            float w = r1.Width * (width / 1920.0f);
            float h = r1.Height * (height / 1080.0f);
            batch.Draw(texture, new Rectangle((int)x, (int)y, (int)w, (int)h), r2, color);
        }

        protected void Draw(SpriteBatch batch, int width, int height, Texture2D texture, Rectangle r1, Color color)
        {
            float x = r1.X * (width / 1920.0f);
            float y = r1.Y * (height / 1080.0f);
            float w = r1.Width * (width / 1920.0f);
            float h = r1.Height * (height / 1080.0f);
            batch.Draw(texture, new Rectangle((int)x, (int)y, (int)w, (int)h), color);
        }

        protected void DrawString(SpriteBatch batch, int W, int H, int x, int y, float size, BitmapFont font, string str, Color color)
        {
            char[] ch = str.ToCharArray();
            int I = 0;
            for (int i = 0; i < ch.Length; i++)
            {
                char c = ch[i];
                FontPart part = null;
                font.parts.TryGetValue(c, out part);
                if (part != null)
                {
                    Rectangle rect = new Rectangle(x + (int)(size * font.width + size * font.spacing + size * part.offset.X) * I, y + (int)(size * part.offset.Y), (int)(size * part.glyphBounds.Width + size ), (int)(size * part.glyphBounds.Height));
                    Draw(batch, W, H, font.texture, rect, part.glyphBounds, color);
                    I++;
                }
            }
        }

        public virtual void Close()
        {
            Hud.openGui = null;
        }
    }
}
