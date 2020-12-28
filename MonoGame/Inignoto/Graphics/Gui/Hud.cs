using Inignoto.Audio;
using Inignoto.GameSettings;
using Inignoto.Graphics.Fonts;
using Inignoto.Graphics.World;
using Inignoto.Inventory;
using Inignoto.Math;
using Inignoto.Tiles;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
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

        public virtual void Update(GameTime time, int width, int height)
        {
            UpdateKeys();
        }

        public virtual void PreRender(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {
        }

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
                        Inignoto.game.player.position = new Vector3(Inignoto.game.player.SpawnPosition.X, Inignoto.game.player.SpawnPosition.Y, Inignoto.game.player.SpawnPosition.Z);
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

            //inventory hotbar
            Draw(spriteBatch, width, height, Textures.Textures.inventory_row, new Rectangle(1920 / 2 - (324 * 3) / 2, 1080 - 40 * 3 + 1, 324 * 3, 40 * 3), Color.White);

            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

            for (int i = 0; i < 10; i++)
            {
                int x = (7 + i * 31) * 3 + 1920 / 2 - (324 * 3) / 2;
                int y = 5 * 3 + 1080 - 40 * 3;
                ItemStack stack = Inignoto.game.player.Inventory.hotbar[i];

                if (mouse_x >= x && mouse_y >= y && mouse_x <= x + 90 && mouse_y <= y + 90)
                {
                    Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(x, y, 90, 90), new Rectangle(24, 330, 30, 30), Color.White);
                }
                if (openGui == null && i == Inignoto.game.player.Inventory.selected)
                {
                    Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(x, y, 90, 90), new Rectangle(24, 300, 30, 30), Color.White);
                }

                if (stack != null)
                {
                    DrawItem(spriteBatch, width, height, stack, x, y, 0.75f, 0.75f);
                }
            }

            if (renderGui != null)
            {
                renderGui.Render(device, spriteBatch, width, height, time);
                if (!renderGui.OverrideHealthbar)
                {
                    RenderHealthbar(device, spriteBatch, width, height, time);
                }
            }
            else
            {
                if (Inignoto.game.player.Perspective == 1)
                    Draw(spriteBatch, width, height, Textures.Textures.fp_cursor, new Rectangle(1920 / 2 - 4 * 3, 1080 / 2 - 4 * 3, 8 * 3, 8 * 3), Inignoto.game.camera.highlightedTile == null ? Color.Gray : Color.White);

                RenderHealthbar(device, spriteBatch, width, height, time);
            }

            /////////////////////////////////////////////
            Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(0, 0, 1920, 1080), new Color(0, 0, 0, Fade));

            if (openGui == null)
            {
                renderGui = null;
            }
        }

        protected void RenderHealthbar(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time, int x = 0, int y = 0)
        {
            if (openGui is MainMenu)
            {
                return;
            }
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

        private int current_scroll = 0;
        private int last_scroll = 0;

        protected virtual void UpdateKeys()
        {
            if (Inignoto.game.paused || Inignoto.game.game_state != Inignoto.GameState.GAME)
            {
                if (Inignoto.game.game_state != Inignoto.GameState.GAME)
                {
                    Inignoto.game.player.Inventory.hotbar[0] = null;
                    Inignoto.game.player.Inventory.selected = 0;
                }
                return;
            }


            for (int i = 0; i < Settings.HOTBAR_KEYS.Length; i++)
            {
                if (Settings.HOTBAR_KEYS[i].IsPressed())
                {
                    Inignoto.game.player.Inventory.selected = i;
                }
            }
            current_scroll = (Mouse.GetState().ScrollWheelValue / 8) / 15;

            int scroll = last_scroll - current_scroll;

            if (Inignoto.game.player.Inventory.selected + scroll > 9)
            {
                Inignoto.game.player.Inventory.selected = 0;
            }
            else
            if (Inignoto.game.player.Inventory.selected + scroll < 0)
            {
                Inignoto.game.player.Inventory.selected = 9;
            }
            else Inignoto.game.player.Inventory.selected += scroll;
            last_scroll = current_scroll;
        }

        public static void DrawItem(SpriteBatch spriteBatch, int width, int height, ItemStack stack, int x, int y, float scaleX, float scaleY)
        {
            Draw(spriteBatch, width, height, stack.item.GetRenderTexture(), new Rectangle(x - 11 * 3, y, (int)(192 * scaleX), (int)(108 * scaleY)), Color.White);
            if (stack.item.max_stack > 1)
            {
                DrawString(spriteBatch, width, height, x + 7, y + 6, 0.75f, FontManager.mandrill_bold, "" + stack.count, Color.Gray);
                DrawString(spriteBatch, width, height, x + 7, y + 5, 0.75f, FontManager.mandrill_bold, "" + stack.count, Color.Gray);
                DrawString(spriteBatch, width, height, x + 5, y + 6, 0.75f, FontManager.mandrill_bold, "" + stack.count, Color.Gray);
                DrawString(spriteBatch, width, height, x + 5, y + 5, 0.75f, FontManager.mandrill_bold, "" + stack.count, Color.White);
            }
        }



        public static void Draw(SpriteBatch batch, int width, int height, Texture2D texture, Rectangle r1, Rectangle r2, Color color, float rotation)
        {
            float x = r1.X * (width / 1920.0f);
            float y = r1.Y * (height / 1080.0f);
            float w = r1.Width * (width / 1920.0f);
            float h = r1.Height * (height / 1080.0f);
            batch.Draw(texture, new Rectangle((int)x, (int)y, (int)w, (int)h), r2, color);
        }

        public static void Draw(SpriteBatch batch, int width, int height, Texture2D texture, Rectangle r1, Rectangle r2, Color color)
        {
            if (texture == null) return;
            float x = r1.X * (width / 1920.0f);
            float y = r1.Y * (height / 1080.0f);
            float w = r1.Width * (width / 1920.0f);
            float h = r1.Height * (height / 1080.0f);
            batch.Draw(texture, new Rectangle((int)x, (int)y, (int)w, (int)h), r2, color);
        }

        public static void Draw(SpriteBatch batch, int width, int height, Texture2D texture, Rectangle r1, Color color)
        {
            if (texture == null) return;
            float x = r1.X * (width / 1920.0f);
            float y = r1.Y * (height / 1080.0f);
            float w = r1.Width * (width / 1920.0f);
            float h = r1.Height * (height / 1080.0f);
            batch.Draw(texture, new Rectangle((int)x, (int)y, (int)w, (int)h), color);
        }

        public static void DrawString(SpriteBatch batch, int W, int H, int x, int y, float size, BitmapFont font, string str, Color color)
        {
            char[] ch = str.ToCharArray();
            int I = 0;
            int pos = 0;
            for (int i = 0; i < ch.Length; i++)
            {
                char c = ch[i];
                FontPart part = null;
                font.parts.TryGetValue(c, out part);
                if (part != null)
                {
                    Rectangle rect = new Rectangle(x + pos + (int)(6.25f * I * size), y + (int)(size * part.offset.Y), (int)(size * part.glyphBounds.Width + size), (int)(size * part.glyphBounds.Height));
                    Draw(batch, W, H, font.texture, rect, part.glyphBounds, color);
                    pos += (int)(size * font.width + size * font.spacing + size * part.offset.X);
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
