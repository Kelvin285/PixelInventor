using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;
using Inignoto.Graphics.Textures;
using Microsoft.Xna.Framework.Input;
using Inignoto.Effects;
using Inignoto.Math;
using Inignoto.Utilities;
using Inignoto.Graphics.Fonts;
using Inignoto.GameSettings;

namespace Inignoto.Graphics.Gui
{
    public class MainMenu : Hud
    {

        public double frame = 0;

        public Vector2 moon = new Vector2(1920 * 0.8f, 1080 * 0.1f);
        public Vector2 last_moon = new Vector2(1920 * 0.8f, 1080 * 0.1f);
        public Vector2 moon_velocity = new Vector2(0, 0);

        public bool grabbed = false;

        private RenderTarget2D target;

        private bool mouse_pressed = false;
        private bool mouse_released = false;

        private enum MenuState
        {
            TITLE, SETTINGS
        }

        private enum SettingsState {
            GRAPHICS, CONTROLS, AUDIO, SELECT
        }

        private MenuState menu_state = MenuState.TITLE;
        private SettingsState settings_state = SettingsState.SELECT;

        private int controls_page = 0;
        public int controls_pages = 3;

        private InputSetting selected_input = null;

        public override void Render(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime gameTime)
        {
            double delta = gameTime.ElapsedGameTime.TotalMilliseconds / 60.0;
            double time = gameTime.TotalGameTime.TotalMilliseconds / 60.0;
            frame += delta * 0.1f;
            if (frame > 3)
            {
                frame = 0;
            }
            Draw(spriteBatch, width, height, Textures.Textures.title_animation[(int)frame], new Rectangle(0, 0, 1920, 1080), Color.White);

            if (target != null)
            {

                Draw(spriteBatch, width, height, target, new Rectangle(-100, 50, (int)(250 * 1.5), (int)(200 * 1.5)), Color.White);

                DrawString(spriteBatch, width, height, 0, 0, 1.0f, FontManager.mandrill_regular, Inignoto.game.player.Name, Color.White);

            }

            bool clicked = false;

            if (Mouse.GetState().LeftButton == ButtonState.Pressed)
            {
                if (!mouse_pressed)
                {
                    mouse_pressed = true;
                    clicked = true;
                }
            }
            else
            {
                mouse_pressed = false;
                mouse_released = true;
            }

            if (menu_state == MenuState.TITLE)
            {
                RenderMainMenu(device, spriteBatch, width, height, gameTime, clicked);
            }
            else if (menu_state == MenuState.SETTINGS)
            {
                RenderSettings(device, spriteBatch, width, height, gameTime, clicked);
            }
        }

        public void RenderSettings(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime gameTime, bool clicked)
        {
            int w = 300;

            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

            double DrawSlider(int x, int y, string str, double value, double max)
            {
                int X = x + 1920 / 2 - w - 40;
                int Y = 1080 / 2 - 50 + y;
                int W = w * 2;
                int H = 75;

                Color color = Color.DarkGray;
                Color color2 = Color.LightSlateGray;
                Color color3 = Color.White;

                int SLIDER_X = (int)((W - 15) * (value / max));
                if (mouse_x >= X - 5 && mouse_y >= Y && mouse_x <= X + W + 5 && mouse_y <= Y + H)
                {
                    if (Mouse.GetState().LeftButton == ButtonState.Pressed && mouse_released)
                    {
                        SLIDER_X = (int)MathF.Min((W - 15), MathF.Max(0, mouse_x - X));
                        color = Color.Gray;
                        color2 = Color.LightGray;
                        color3 = Color.Gray;
                    }
                }
                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(x + 1920 / 2 - w - 40, 1080 / 2 - 50 + y, w * 2, 75), color2);
                int strwidth = (int)(FontManager.mandrill_regular.width + FontManager.mandrill_regular.spacing) * str.Length;
                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(x + 1920 / 2 - w - 40 + SLIDER_X, 1080 / 2 - 50 + y, 15, 75), color);
                DrawString(spriteBatch, width, height, x + 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2 - 15, 1080 / 2 - 50 + y, 1.0f, FontManager.mandrill_regular, str, color3);


                return MathF.Max(0, MathF.Min((float)max, (float)(max * SLIDER_X / (W - 15))));
            }

            void DrawCenteredString(int x, int y, string str, Color color)
            {
                int strwidth = (int)(FontManager.mandrill_regular.width + FontManager.mandrill_regular.spacing) * str.Length;
                DrawString(spriteBatch, width, height, 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2, 1080 / 2 - 50 + y, 1.0f, FontManager.mandrill_regular, str, color);
            }

            bool DrawButton(int x, int y, string str, float font_size = 1.0f)
            {
                int X = x + 1920 / 2 - w - 40;
                int Y = 1080 / 2 - 50 + y;
                int W = w * 2;
                int H = 75;

                Color color = Color.White;
                Color color2 = Color.LightSlateGray;

                bool flag = false;

                if (mouse_x >= X && mouse_y >= Y && mouse_x <= X + W && mouse_y <= Y + H)
                {
                    flag = true;
                    color = Color.Gray;
                    color2 = Color.LightGray;
                }

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(x + 1920 / 2 - w - 40, 1080 / 2 - 50 + y, w * 2, 75), color2);
                int strwidth = (int)((FontManager.mandrill_regular.width + FontManager.mandrill_regular.spacing) * str.Length * font_size);
                DrawString(spriteBatch, width, height, x + 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2, 1080 / 2 - 50 + y, font_size, FontManager.mandrill_regular, str, color);

                return flag;
            }
            if (settings_state == SettingsState.SELECT)
            {
                if (DrawButton(0, 0, "graphics"))
                {
                    if (clicked)
                    {
                        settings_state = SettingsState.GRAPHICS;
                        mouse_released = false;
                    }
                }
                if (DrawButton(0, 100, "audio"))
                {
                    if (clicked)
                    {
                        settings_state = SettingsState.AUDIO;
                        mouse_released = false;
                    }
                }
                if (DrawButton(0, 200, "controls"))
                {
                    if (clicked)
                    {
                        settings_state = SettingsState.CONTROLS;
                        mouse_released = false;
                    }
                }
                if (DrawButton(0, 300, "back"))
                {
                    if (clicked)
                    {
                        menu_state = MenuState.TITLE;
                    }
                }

                DrawCenteredString(0, -100, "Settings", Color.White);
            } else
            {
                bool set = false;
                if (settings_state == SettingsState.CONTROLS)
                {

                    bool DrawControl(int x, int y, string name, InputSetting setting)
                    {
                        int input = setting.Input;
                        bool mouse = setting.Mouse;

                        

                        if (mouse)
                        {
                            if (DrawButton(x, y, name + ": Mouse " + input))
                            {
                                if (clicked)
                                {
                                    mouse_released = false;
                                    if (!set)
                                        selected_input = setting;
                                }
                                return true;
                            }
                            return false;
                        }
                        else
                        {
                            if(DrawButton(x, y, name + ": " + (Keys)input))
                            {
                                if (clicked)
                                {
                                    mouse_released = false;
                                    if (!set)
                                        selected_input = setting;
                                }
                                return true;
                            }
                            return false;
                        }
                    }

                    if (selected_input != null && mouse_released)
                    {
                        if (Mouse.GetState().LeftButton == ButtonState.Pressed)
                        {
                            selected_input.Mouse = true;
                            selected_input.Input = 0;
                            selected_input = null;
                            set = true;
                        }
                        else if (Mouse.GetState().RightButton == ButtonState.Pressed)
                        {
                            selected_input.Mouse = true;
                            selected_input.Input = 1;
                            selected_input = null;
                            set = true;
                        }
                        else if (Mouse.GetState().MiddleButton == ButtonState.Pressed)
                        {
                            selected_input.Mouse = true;
                            selected_input.Input = 2;
                            selected_input = null;
                            set = true;
                        } else
                        {
                            if (Keyboard.GetState().GetPressedKeyCount() > 0)
                            {
                                selected_input.Mouse = false;
                                selected_input.Input = (int)Keyboard.GetState().GetPressedKeys()[0];
                                selected_input = null;
                                set = true;
                            }
                        }
                    }
                    DrawCenteredString(0, -400, "Controls Settings", Color.White);
                    
                    if (controls_page == 0)
                    {
                        float sensitivity = (int)DrawSlider(325, -300, "Mouse Sensitivity: " + (int)(Settings.MOUSE_SENSITIVITY * 100), Settings.MOUSE_SENSITIVITY * 100, 100.0f);

                        DrawControl(-325, -300, "Forward", Settings.FORWARD);
                        DrawControl(-325, -200, "Backward", Settings.BACKWARD);
                        DrawControl(-325, -100, "Left", Settings.LEFT);
                        DrawControl(-325, 0, "Right", Settings.RIGHT);
                        DrawControl(-325, 100, "Jump", Settings.JUMP);

                        DrawControl(325, -200, "Sneak", Settings.SNEAK);
                        DrawControl(325, -100, "Run", Settings.RUN);
                        DrawControl(325, 0, "Crawl", Settings.CRAWL);
                        DrawControl(325, 100, "Attack", Settings.ATTACK);

                        if (mouse_released)
                        {
                            Settings.MOUSE_SENSITIVITY = sensitivity / 100.0f;
                        }
                    }
                    if (controls_page == 1)
                    {
                        DrawControl(-325, -300, "Use", Settings.USE);
                        DrawControl(-325, -200, "Perspective", Settings.PERSPECTIVE_SWITCH);
                        DrawControl(-325, -100, "Chat", Settings.CHAT);

                        DrawControl(325, -300, "Inventory", Settings.INVENTORY);
                        DrawControl(325, -200, "Fullscreen", Settings.FULLSCREEN_KEY);
                    }
                    if (controls_page == 2)
                    {
                        for (int x = 0; x < 2; x++)
                        {
                            for (int y = 0; y < 5; y++)
                            {
                                int i = x + y * 2;

                                DrawControl(325 * x * 2 - 325, y * 100 - 300, "Hotbar " + i, Settings.HOTBAR_KEYS[i]);

                            }
                        }
                        
                    }
                    
                    if (controls_page == controls_pages - 1)
                    {
                        if (DrawButton(-325, 200, "Back"))
                        {
                            if (clicked) controls_page--;
                        }
                    } else
                    {
                        if (controls_page > 0)
                        {
                            if (DrawButton(-325, 200, "Back"))
                            {
                                if (clicked) controls_page--;
                                selected_input = null;
                            }
                            if (DrawButton(325, 200, "Next"))
                            {
                                if (clicked) controls_page++;
                                selected_input = null;
                            }
                        }
                        else
                        {
                            if (DrawButton(325, 200, "Next"))
                            {
                                if (clicked) controls_page++;
                                selected_input = null;
                            }
                        }
                    }
                    
                    
                }

                if (settings_state == SettingsState.GRAPHICS)
                {
                    DrawCenteredString(0, -300, "Graphics Settings", Color.White);
                    float x_view = (int)System.Math.Round(DrawSlider(-325, -200, "Horizontal View: " + Settings.HORIZONTAL_VIEW, Settings.HORIZONTAL_VIEW, 32));
                    float y_view = (int)System.Math.Round(DrawSlider(-325, -100, "Vertical View: " + Settings.VERTICAL_VIEW, Settings.VERTICAL_VIEW, 16));
                    float fov = (int)System.Math.Round(DrawSlider(-325, 0, "FOV: " + Settings.FIELD_OF_VIEW, Settings.FIELD_OF_VIEW, 140));

                    if (DrawButton(-325, 100, "Parallel Chunks: " + Settings.PARALLEL_CHUNK_GENERATION, 0.9f))
                    {
                        if (clicked)
                        {
                            Settings.PARALLEL_CHUNK_GENERATION = !Settings.PARALLEL_CHUNK_GENERATION;
                        }
                    }

                    if (DrawButton(-325, 200, "Shadows: " + Settings.SHADOWS))
                    {
                        if (clicked)
                        {
                            Settings.SHADOWS = !Settings.SHADOWS;
                        }
                    }

                    if (DrawButton(325, -200, "Head Bobbing: " + Settings.HEAD_BOBBING))
                    {
                        if (clicked)
                        {
                            Settings.HEAD_BOBBING = !Settings.HEAD_BOBBING;
                        }
                    }

                    if (DrawButton(325, -100, "Vsync: " + Settings.VSYNC))
                    {
                        if (clicked)
                        {
                            Settings.VSYNC = !Settings.VSYNC;
                        }
                    }

                    if (DrawButton(325, 0, "Fullscreen: " + Settings.FULLSCREEN))
                    {
                        if (clicked)
                        {
                            Settings.FULLSCREEN = !Settings.FULLSCREEN;
                        }
                    }

                    if (mouse_released)
                    {
                        Settings.HORIZONTAL_VIEW = (int)MathF.Max(4, x_view);
                        Settings.VERTICAL_VIEW = (int)MathF.Max(3, y_view);
                        Settings.FIELD_OF_VIEW = (int)MathF.Max(20, (int)fov);
                    }
                }
                if (settings_state == SettingsState.AUDIO)
                {
                    DrawCenteredString(0, -300, "Audio Settings", Color.White);


                    float master = (int)DrawSlider(-325, -200, "Master: " + Settings.MASTER_VOLUME + " / 100", Settings.MASTER_VOLUME, 100.0f);
                    float music = (int)DrawSlider(325, -200, "Music: " + Settings.MUSIC_VOLUME + " / 100", Settings.MUSIC_VOLUME, 100.0f);


                    float player = (int)DrawSlider(-325, -100, "Players: " + Settings.PLAYER_VOLUME + " / 100", Settings.PLAYER_VOLUME, 100.0f);
                    float creature = (int)DrawSlider(-325, 0, "Creatures: " + Settings.CREATURE_VOLUME + " / 100", Settings.CREATURE_VOLUME, 100.0f);
                    float enemy = (int)DrawSlider(-325, 100, "Hostile: " + Settings.ENEMY_VOLUME + " / 100", Settings.ENEMY_VOLUME, 100.0f);
                    
                    float block = (int)DrawSlider(325, -100, "Blocks: " + Settings.BLOCK_VOLUME + " / 100", Settings.BLOCK_VOLUME, 100.0f);
                    float ambient = (int)DrawSlider(325, 0, "Ambience: " + Settings.AMBIENT_VOLUME + " / 100", Settings.AMBIENT_VOLUME, 100.0f);
                    float gui = (int)DrawSlider(325, 100, "Gui: " + Settings.GUI_VOLUME + " / 100", Settings.GUI_VOLUME, 100.0f);
                   
                    if (mouse_released)
                    {
                        Settings.MASTER_VOLUME = master;
                        Settings.MUSIC_VOLUME = music;
                        Settings.PLAYER_VOLUME = player;
                        Settings.CREATURE_VOLUME = creature;
                        Settings.ENEMY_VOLUME = enemy;
                        Settings.BLOCK_VOLUME = block;
                        Settings.AMBIENT_VOLUME = ambient;
                        Settings.GUI_VOLUME = gui;
                    }
                    
                }
                if (DrawButton(0, 300, "back"))
                {
                    if (clicked)
                    {
                        settings_state = SettingsState.SELECT;
                        mouse_released = false;
                        selected_input = null;
                    }
                }
            }
        }

        public void RenderMainMenu(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime gameTime, bool clicked)
        {

            double delta = gameTime.ElapsedGameTime.TotalMilliseconds / 60.0;
            double time = gameTime.TotalGameTime.TotalMilliseconds / 60.0;
            frame += delta * 0.1f;
            if (frame > 3)
            {
                frame = 0;
            }

            Draw(spriteBatch, width, height, Textures.Textures.title_moon, new Rectangle((int)moon.X - 352 / 2, (int)moon.Y - 340 / 2, 352, 340), Color.White);

            Draw(spriteBatch, width, height, Textures.Textures.moon_clip, new Rectangle(0, 0, 1920, 1080), Color.White);

            Draw(spriteBatch, width, height, Textures.Textures.inignoto_logo, new Rectangle(0, (int)(MathF.Sin((float)time / 30.0f) * 25.0f), 1920, 1080), Color.White);


            int w = 300;

            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));
            
            bool DrawButton(int y, string str)
            {
                int X = 1920 / 2 - w - 40;
                int Y = 1080 / 2 - 50 + y;
                int W = w * 2;
                int H = 75;

                Color color = Color.White;
                Color color2 = Color.LightSlateGray;

                bool flag = false;

                if (mouse_x >= X && mouse_y >= Y && mouse_x <= X + W && mouse_y <= Y + H)
                {
                    flag = true;
                    color = Color.Gray;
                    color2 = Color.LightGray;
                }

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(1920 / 2 - w - 40, 1080 / 2 - 50 + y, w * 2, 75), color2);
                int strwidth = (int)(FontManager.mandrill_regular.width + FontManager.mandrill_regular.spacing) * str.Length;
                DrawString(spriteBatch, width, height, 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2, 1080 / 2 - 50 + y, 1.0f, FontManager.mandrill_regular, str, color);

                return flag;
            }
            
            DrawButton(0, "singleplayer");

            DrawButton(100, "multiplayer");

            if (DrawButton(200, "settings"))
            {
                if (clicked)
                {
                    menu_state = MenuState.SETTINGS;
                }
            }

            if (DrawButton(300, "quit"))
            {
                if (clicked)
                {
                    Inignoto.game.Exit();
                }
            }
        }

        public void DrawPlayer(GraphicsDevice device, GameEffect effect, int width, int height, GameTime time)
        {
            Inignoto.game.projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(90),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);
            GameResources.effect.Projection = Inignoto.game.projectionMatrix;
            Inignoto.game.player.OnGround = true;

            effect.Radius = 0;
            Inignoto.game.camera.rotation.Y = 180;

            effect.View = Inignoto.game.camera.ViewMatrix;

            RasterizerState rasterizerState = new RasterizerState();
            rasterizerState.CullMode = CullMode.None;
            device.RasterizerState = rasterizerState;
            device.DepthStencilState = DepthStencilState.Default;
            device.SamplerStates[0] = SamplerState.PointClamp;

            if (target == null)
            {
                target = new RenderTarget2D(device, width, height, false, SurfaceFormat.Color, DepthFormat.Depth16);
            }

            Inignoto.game.GraphicsDevice.SetRenderTarget(target);
            Inignoto.game.GraphicsDevice.Clear(Color.Transparent);

            Inignoto.game.GraphicsDevice.DepthStencilState = DepthStencilState.Default;

            Inignoto.game.player.SetModelTransform(new Vector3f(-0.7f, -1f, 1.25f), new Vector3f(0, 0, 0));

            if (Inignoto.game.player.model != null)
            {
                if (Inignoto.game.player.Perspective != 1)
                    Inignoto.game.player.model.Stop();
                Inignoto.game.player.Render(device, effect, time, true);
                Inignoto.game.player.SetModelTransform();
                if (Inignoto.game.player.Perspective != 1)
                    Inignoto.game.player.model.Play(Inignoto.game.player.model.currentTime);
            }


            Inignoto.game.GraphicsDevice.SetRenderTarget(null);
            Inignoto.game.projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);
            GameResources.effect.Projection = Inignoto.game.projectionMatrix;
        }

        public override void PreRender(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime gameTime)
        {
            DrawPlayer(device, GameResources.effect, width, height, gameTime);
        }

        public override void Update(GameTime gameTime, int width, int height)
        {
            double delta = gameTime.ElapsedGameTime.TotalMilliseconds / 60.0;
            
            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

            float dist = Vector2.Distance(new Vector2(mouse_x, mouse_y), moon);

            if (Mouse.GetState().LeftButton == ButtonState.Pressed)
            {
                if (Vector2.Distance(new Vector2(mouse_x, mouse_y), moon) <= 64)
                {
                    if (grabbed == false)
                    {
                        grabbed = true;
                    }
                }
            } else
            {
                grabbed = false;
            }
            if (grabbed)
            {
                moon.X = mouse_x;
                moon.Y = mouse_y;
                moon_velocity = (moon - last_moon) * 2.0f;
            } else
            {
                moon += (moon_velocity * (float)delta);
            }

            if (moon.X < 0 || moon.X > 1920)
            {
                moon_velocity.X *= -1;
                moon.X = last_moon.X;
            }

            //525 = max
            //686 = min

            //height = 121

            if (moon.Y < 0 || moon.Y > 600)
            {
                moon_velocity.Y *= -1;
                moon.Y = last_moon.Y;
            }


            float rad = (moon.X * MathF.PI) / 1920.0f;
            double h = MathF.Sin(rad) * 121;

            if (moon.Y > 605 - h)
            {
                Vector2 normal = new Vector2(-MathF.Cos(rad), -MathF.Sin(rad));

                moon_velocity += normal * moon_velocity.Length();

                moon.Y = 605 - (float)h;
            }

            moon_velocity = Vector2.Lerp(moon_velocity, new Vector2(0), (float)delta * 0.01f);

            last_moon.X = moon.X;
            last_moon.Y = moon.Y;
        }

        public static void Dispose()
        {
            
        }
    }
}
