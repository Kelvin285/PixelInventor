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
using System.IO;
using Inignoto.World;
using Inignoto.Common;
using static Inignoto.Entities.Player.PlayerEntity;
using Inignoto.Entities.Client.Player;
using Inignoto.Graphics.World;
using Inignoto.Tiles;
using Inignoto.Graphics.Models;
using Inignoto.Graphics.Models.New;
using Inignoto.Audio;
using static Inignoto.Math.Raytracing;

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

        public enum MenuState
        {
            TITLE, SETTINGS, SINGLEPLAYER, MULTIPLAYER, MODEL_CREATOR
        }

        private enum SettingsState {
            GRAPHICS, CONTROLS, AUDIO, SELECT
        }

        private enum SingleplayerState {
            SELECT, CREATE
        }

        public MenuState menu_state = MenuState.TITLE;
        private SettingsState settings_state = SettingsState.SELECT;
        private SingleplayerState singleplayer_state = SingleplayerState.SELECT;

        private int controls_page = 0;
        public int controls_pages = 3;
        public int world_page = 0;

        private InputSetting selected_input = null;

        private List<WorldProperties> worlds = new List<WorldProperties>();

        public bool inGame;

        public MainMenu(bool inGame = false)
        {
            this.inGame = inGame;
            if (inGame)
            {
                menu_state = MenuState.SETTINGS;
            }
        }

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
            } else if (menu_state == MenuState.SINGLEPLAYER)
            {
                RenderSingleplayer(device, spriteBatch, width, height, gameTime, clicked);
            }
            else if (menu_state == MenuState.MODEL_CREATOR)
            {
                RenderModelCreator(device, spriteBatch, width, height, gameTime, clicked);
            }
        }

        private Mesh.Mesh mesh;
        public Point lastMousePos = new Point(0, 0);
        private int WIDTH;
        private int HEIGHT;
        private bool grab_scroll = false;
        public ModelObject selected = null;
        private ModelObject hovered = null;
        public Vector3 mdir = new Vector3(0, 0, 0);
        public void Render3D(GameTime gameTime)
        {
            double delta = gameTime.ElapsedGameTime.TotalMilliseconds / 60.0;
            float movement_speed = 0.25f;


            if (mesh == null)
            {
                mesh = TileBuilder.BuildTile(-0.5f, -0.5f, -0.5f, TileRegistry.DIRT.DefaultData, TileRegistry.GRASS.DefaultData, Inignoto.game.GraphicsDevice);

                model = new NewGameModel();
                model.translation.Y = 1;
                model.AddChild(new ModelCube(new Vector3(0, 0, 0)));

                Inignoto.game.camera.position = new Vector3(0, 0, 10);
                Inignoto.game.camera.rotation = new Vector3(0, 0, 0);
            }
            mesh.Draw(GameResources.effect, Inignoto.game.GraphicsDevice);
            if (model != null)
            {
                Matrix rotation = Matrix.CreateFromQuaternion(Quaternion.CreateFromYawPitchRoll(Inignoto.game.camera.rotation.Y * (MathF.PI / 180.0f), Inignoto.game.camera.rotation.X * (MathF.PI / 180.0f), Inignoto.game.camera.rotation.Z * (MathF.PI / 180.0f)));
                Matrix translation = Matrix.CreateTranslation(mouse_dir);
                Matrix mat = translation * rotation;
                mdir = mat.Translation;
                mdir.Normalize();

                hovered = model.TestForIntersection(Inignoto.game.camera.position, mdir);
                model.editing = true;
                if (selected != null)
                {
                    selected.selected = true;
                }
                model.Render(Inignoto.game.GraphicsDevice, GameResources.effect, selected);
            }
            

            if (Settings.JUMP.IsPressed())
            {
                Inignoto.game.camera.position += Inignoto.game.camera.UpMotionVector * (float)delta * movement_speed;
            }
            if (Settings.SNEAK.IsPressed())
            {
                Inignoto.game.camera.position -= Inignoto.game.camera.UpMotionVector * (float)delta * movement_speed;
            }
            if (Settings.LEFT.IsPressed())
            {
                Inignoto.game.camera.position -= Inignoto.game.camera.RightMotionVector * (float)delta * movement_speed;
            }
            if (Settings.RIGHT.IsPressed())
            {
                Inignoto.game.camera.position += Inignoto.game.camera.RightMotionVector * (float)delta * movement_speed;
            }
            if (Settings.FORWARD.IsPressed())
            {
                Inignoto.game.camera.position += Inignoto.game.camera.ForwardMotionVector * (float)delta * movement_speed;
            }
            if (Settings.BACKWARD.IsPressed())
            {
                Inignoto.game.camera.position -= Inignoto.game.camera.ForwardMotionVector * (float)delta * movement_speed;
            }
            if (Settings.USE.IsPressed())
            {
                Point motion = Inignoto.game.mousePos - lastMousePos;

                Inignoto.game.camera.rotation -= new Vector3((float)motion.Y * (1920f / HEIGHT) * 4, (float)motion.X * (1080f / WIDTH) * 8, 0) * (float)delta * Settings.MOUSE_SENSITIVITY;
            }
            if (Inignoto.game.camera.rotation.X > 89.9) Inignoto.game.camera.rotation.X = 89.9f;
            if (Inignoto.game.camera.rotation.X < -89.9f) Inignoto.game.camera.rotation.X = -89.9f;
            lastMousePos = new Point(Inignoto.game.mousePos.X, Inignoto.game.mousePos.Y);
        }

        private int file_menu = -1;
        private bool save_dialog = false;
        private bool load_dialog = false;
        private bool save_model = false;
        private bool save_texture = false;
        private bool save_animation = false;
        private bool load_model = false;
        private bool load_texture = false;
        private bool load_animation = false;

        private NewGameModel model;

        private int scroll = 0;
        private int last_scrollwheel = 0;
        private string currentDirectory = FileUtils.GetResourcePath(new ResourcePath("Inignoto", "", "assets"));
        private string selected_file = "";
        private KeyReader reader = new KeyReader(200);

        private Vector3 mouse_dir = Vector3.Zero;
        private Vector2 clicked_pos = Vector2.Zero;
        int click_uv = -1;
        private Vector4 old_uv = Vector4.Zero;
        private bool can_deselect = true;


        public void RenderModelCreator(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime gameTime, bool clicked)
        {
            WIDTH = width;
            HEIGHT = height;

            if (clicked && can_deselect)
            {
                selected = hovered;
            }

            mouse_dir = new Vector3((lastMousePos.X / (float)width - 0.5f) * (1920.0f / 1080.0f) * 2.0f, -(lastMousePos.Y / (float)height - 0.5f) * 2.0f, -1);

            bool DrawButton(int x, int y, int w, int h, string str, float font_size = 1.0f, bool left_align = false)
            {

                int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
                int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

                int X = x + 1920 / 2 - w - 40;
                int Y = 1080 / 2 - 50 + y;
                int W = w * 2;
                int H = h;

                Color color = Color.White;
                Color color2 = Color.LightSlateGray;

                bool flag = false;

                if (mouse_x >= X && mouse_y >= Y && mouse_x <= X + W && mouse_y <= Y + H)
                {
                    flag = true;
                    color = Color.Gray;
                    color2 = Color.LightGray;
                }

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(x + 1920 / 2 - w - 40, 1080 / 2 - 50 + y, w * 2, h), color2);
                int strwidth = (int)((FontManager.mandrill_regular.width + FontManager.mandrill_regular.spacing) * str.Length * font_size);
                if (!left_align)
                {
                    DrawString(spriteBatch, width, height, x + 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2, 1080 / 2 - 50 + y, font_size, FontManager.mandrill_regular, str, color);
                }
                else
                {
                    DrawString(spriteBatch, width, height, x + 1920 / 2 - w - 40, 1080 / 2 - 50 + y, font_size, FontManager.mandrill_regular, str, color);
                }
                if (flag && clicked)
                {
                    SoundEffects.ui_click.Play(Settings.GUI_VOLUME / 100.0f, 1.0f, 0.0f);
                }
                return flag;
            }

            //Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(0, 0, 1920, 1080), Color.Black);
            
            Draw(spriteBatch, width, height, GameResources.gameImage, new Rectangle(0, 0, 1920, 1080), Color.White);

            Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(0, 0, 1920, 70), Color.DarkSlateGray);


            if (save_dialog || load_dialog)
            {
                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(0, 0, 1920, 1080), Color.Gray);

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(0, 0, 1920, 75), Color.DarkSlateGray);

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(0, 75, 1920, 75 / 2), Color.Black);

                reader.text = selected_file;
                reader.finished = false;
                reader.Update(gameTime);
                selected_file = reader.text;

                DrawString(spriteBatch, width, height, 5, 80, 0.5f, FontManager.mandrill_bold, selected_file + ((gameTime.TotalGameTime.Milliseconds % 1000 <= 500) ? "|" : ""), Color.Gray);

                if (DrawButton(900, -1080 / 2 + 75 + 75 / 2 + 14, 100, 75 / 2, save_dialog ? "Save" : "Load", 0.5f))
                {
                    if (clicked)
                    {
                        if (load_model)
                        {
                            string fstring = currentDirectory + Path.DirectorySeparatorChar + selected_file;
                            if (selected_file.Length > 0)
                            {
                                if (File.Exists(fstring))
                                {
                                    model = new NewGameModel();
                                }
                            }
                        }
                        save_dialog = false;
                        save_model = false;
                        save_texture = false;
                        save_animation = false;
                        load_model = false;
                        load_texture = false;
                        load_animation = false;
                        load_dialog = false;
                    }
                    
                }

                if (DrawButton(925, -475, 75 / 2, 75 / 2, "X", 0.5f))
                {
                    if (clicked)
                    {
                        save_dialog = false;
                        save_model = false;
                        save_texture = false;
                        save_animation = false;
                        load_model = false;
                        load_texture = false;
                        load_animation = false;
                        load_dialog = false;
                    }
                }
                string[] directory = Directory.GetDirectories(currentDirectory);
                string[] file = Directory.GetFiles(currentDirectory);

                string[] both = new string[directory.Length + file.Length];
                for (int i = 0; i < directory.Length; i++)
                {
                    both[i] = directory[i];
                }
                for (int i = 0; i < file.Length; i++)
                {
                    both[i + directory.Length] = file[i];
                }

                if (DrawButton(-1920 / 2 + 5, -1080 / 2 + 80 * 2 + 5, 1920 - 25, 25, "", 0.5f, false))
                {
                    DrawString(spriteBatch, width, height, 5, 75 + 75 / 2, 0.5f, FontManager.mandrill_bold, "...", Color.DarkSlateGray);
                    if (clicked)
                    {
                        currentDirectory = Directory.GetParent(currentDirectory).FullName;
                    }
                } else
                {
                    DrawString(spriteBatch, width, height, 5, 75 + 75 / 2, 0.5f, FontManager.mandrill_bold, "...", Color.White);
                }
                

                for (int i = 0; i < both.Length; i++)
                {
                    string[] str = both[i].Split(Path.DirectorySeparatorChar);
                    if (i >= scroll)
                    {
                        string before = "File: ";
                        if (Directory.Exists(both[i]))
                        {
                            before = "Directory: ";
                        }
                        int Y = 75 + 75 / 2 + i * 25 - scroll * 25 + 25;
                        if (Directory.Exists(both[i]))
                        {
                            if (DrawButton(-1920 / 2 + 5, -1080 / 2 + Y - (75 + 75 / 2) + (80 * 2) + 5, 1920 - 25, 25, "", 0.5f, false))
                            {
                                DrawString(spriteBatch, width, height, 5, Y, 0.5f, FontManager.mandrill_bold, before + str[str.Length - 1], Color.DarkSlateGray);
                                if (clicked)
                                {
                                    currentDirectory = both[i];
                                }
                            }
                            else
                            {
                                DrawString(spriteBatch, width, height, 5, Y, 0.5f, FontManager.mandrill_bold, before + str[str.Length - 1], Color.White);
                            }
                        } else
                        {
                            if (DrawButton(-1920 / 2 + 5, -1080 / 2 + Y - (75 + 75 / 2) + (80 * 2) + 5, 1920 - 25, 25, "", 0.5f, false))
                            {
                                DrawString(spriteBatch, width, height, 5, Y, 0.5f, FontManager.mandrill_bold, before + str[str.Length - 1], Color.DarkSlateGray);
                                if (clicked)
                                {
                                    selected_file = str[str.Length - 1];
                                }
                            }
                            else
                            {
                                DrawString(spriteBatch, width, height, 5, Y, 0.5f, FontManager.mandrill_bold, before + str[str.Length - 1], Color.White);
                            }
                        }
                    }
                }

                if (Mouse.GetState().ScrollWheelValue != last_scrollwheel)
                {
                    int scroll = (Mouse.GetState().ScrollWheelValue - last_scrollwheel) / 120;
                    this.scroll -= scroll;
                    
                    last_scrollwheel = Mouse.GetState().ScrollWheelValue;

                    if (this.scroll < 0) this.scroll = 0;
                    if (this.scroll > both.Length - 1) this.scroll = both.Length - 1;
                }

                if (this.scroll > both.Length) this.scroll = both.Length;

                DrawString(spriteBatch, width, height, 5, 5, 1.0f, FontManager.mandrill_bold, save_dialog ? "Save File" : "Load File", Color.Black);

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(1920 - 50, 75 + 75 / 2, 50, 1080), Color.Black);

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(1920 - 50, 75 + 75 / 2, 50, 1080 - (75 + 75 / 2)), Color.Black);
                if (DrawButton(1920 / 2 - 5 + 25 / 2 + 10, -1080 / 2 + (80 * 2) + scroll * ((1080 - (75 + 75 / 2)) / both.Length) + 5, 25, (1080 - (75 + 75 / 2)) / both.Length, ""))
                {
                    if (Settings.ATTACK.IsPressed())
                    {
                        grab_scroll = true;
                    }
                }
                if (grab_scroll)
                {
                    scroll = ((int)(Inignoto.game.mousePos.Y * (1080f / height)) - (80 / 2)) / ((1080 - (75 + 75 / 2)) / both.Length) - 1;
                    if (!Settings.ATTACK.IsPressed()) grab_scroll = false;
                }

                if (this.scroll < 0) this.scroll = 0;
                if (this.scroll > both.Length - 1) this.scroll = both.Length - 1;

                return;
            }

            bool hovered_menu = false;
            if (DrawButton(925, -475, 100 / 2, 75 / 2, "back", 0.5f))
            {
                if (clicked)
                {
                    menu_state = MenuState.TITLE;
                }
            }

            if (DrawButton(-850, -475, 100 / 2, 75 / 2, "save", 0.5f))
            {
                if (clicked)
                {
                    if (file_menu == 0) file_menu = -1;
                    else
                        file_menu = 0;
                }
                hovered_menu = true;
            }

            if (DrawButton(-725, -475, 100 / 2, 75 / 2, "load", 0.5f))
            {
                if (clicked)
                {
                    if (file_menu == 1) file_menu = -1;
                    else
                        file_menu = 1;
                }
                hovered_menu = true;
            }

            Draw(spriteBatch, width, height, Textures.Textures.translate_button, new Rectangle(25, 100, 64, 64), Color.White);
            if (lastMousePos.X * (1920.0f / width) > 25 && lastMousePos.Y * (1080.0f / height) > 100 && lastMousePos.X * (1920.0f / width) < 25 + 64 && lastMousePos.Y * (1080.0f / height) < 164)
            {
                Draw(spriteBatch, width, height, Textures.Textures.translate_button, new Rectangle(25, 100, 64, 64), Color.Gray);
                if (clicked)
                {
                    ModelObject.edit_mode = ModelObject.ModelEditMode.TRANSLATION;
                }
            }

            Draw(spriteBatch, width, height, Textures.Textures.rotate_button, new Rectangle(25 + 64, 100, 64, 64), Color.White);
            if (lastMousePos.X * (1920.0f / width) > 25 + 64 && lastMousePos.Y * (1080.0f / height) > 100 && lastMousePos.X * (1920.0f / width) < 25 + 64 * 2 && lastMousePos.Y * (1080.0f / height) < 164)
            {
                Draw(spriteBatch, width, height, Textures.Textures.rotate_button, new Rectangle(25 + 64, 100, 64, 64), Color.Gray);
                if (clicked)
                {
                    ModelObject.edit_mode = ModelObject.ModelEditMode.ROTATION;
                }
            }

            if (ModelObject.grid_lock_translation)
            {
                Draw(spriteBatch, width, height, Textures.Textures.lock_button, new Rectangle(25 + 64 * 2, 100, 64, 64), Color.Yellow);
            } else
            {
                Draw(spriteBatch, width, height, Textures.Textures.lock_button, new Rectangle(25 + 64 * 2, 100, 64, 64), Color.White);
            }
            if (lastMousePos.X * (1920.0f / width) > 25 + 64 * 2 && lastMousePos.Y * (1080.0f / height) > 100 && lastMousePos.X * (1920.0f / width) < 25 + 64 * 3 && lastMousePos.Y * (1080.0f / height) < 164)
            {
                Draw(spriteBatch, width, height, Textures.Textures.lock_button, new Rectangle(25 + 64 * 2, 100, 64, 64), Color.Gray);
                if (clicked)
                {
                    ModelObject.grid_lock_translation = !ModelObject.grid_lock_translation;
                }
            }

            int uv_w = 300;
            int uv_h = 300;
            can_deselect = true;
            if (selected != null)
            {
                Texture2D texture = null;

                bool plane = false;
                if (selected is ModelCube)
                {
                    texture = ((ModelCube)selected).mesh.texture;
                }

                if (selected is ModelPlane)
                {
                    texture = ((ModelPlane)selected).mesh.texture;
                    plane = true;
                }

                if (texture != null)
                {
                    int X = 1920 - uv_w - 10;
                    int Y = 100;

                    Vector4 uv = Vector4.Zero;
                    if (plane)
                    {
                       uv = ((ModelPlane)selected).uv;
                    }

                    
                    if (!Settings.ATTACK.IsPressed())
                    {
                        click_uv = -1;
                    }
                    if (plane)
                    {
                        ModelPlane p = (ModelPlane)selected;

                        Draw(spriteBatch, width, height, texture, new Rectangle(1920 - (int)p.GetTexWidth() - 10, Y, (int)p.GetTexWidth(), (int)p.GetTexHeight()), Color.White);
                        X = 1920 - (int)p.GetTexWidth() - 10;
                        float mouse_x = lastMousePos.X * (1920.0f / width) - X;
                        float mouse_y = lastMousePos.Y * (1080.0f / height) - Y;
                        int uvx = (int)(p.uv.X * 32) - 32;
                        int uvy = (int)(p.uv.Y * 32);
                        int uvw = (int)(p.uv.Z * 32);
                        int uvh = (int)(p.uv.W * 32);

                        if (click_uv == 0)
                        {
                            float newuvx = old_uv.X - (clicked_pos.X - mouse_x) * (1.0f / 32.0f);
                            float newuvy = old_uv.Y - (clicked_pos.Y - mouse_y) * (1.0f / 32.0f);
                            p.ChangeUV(new Vector4(newuvx, newuvy, p.uv.Z, p.uv.W));
                        }

                        if (mouse_x > uvx && mouse_y > uvy && mouse_x < uvx + uvw && mouse_y < uvy + uvh)
                        {
                            can_deselect = false;
                            if (Settings.ATTACK.IsPressed())
                            {
                                if (click_uv == -1)
                                {
                                    click_uv = 0;
                                    clicked_pos = new Vector2(mouse_x, mouse_y);
                                    old_uv = p.uv;
                                }
                            }

                        }

                        Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(X + (int)(p.uv.X * 32), Y + (int)(p.uv.Y * 32), (int)(p.uv.Z * 32), (int)(p.uv.W * 32)), new Color(1.0f, 0, 0, 0.2f));
                    } else
                    {
                        ModelCube p = (ModelCube)selected;
                        Draw(spriteBatch, width, height, texture, new Rectangle(1920 - (int)p.GetTexWidth() - 10, Y, (int)p.GetTexWidth(), (int)p.GetTexHeight()), Color.White);
                        X = 1920 - (int)p.GetTexWidth() - 10;
                        float mouse_x = lastMousePos.X * (1920.0f / width) - X;
                        float mouse_y = lastMousePos.Y * (1080.0f / height) - Y;
                        Vector3[] colors = {
                            new Vector3(1, 0, 0),
                            new Vector3(0, 1, 0),
                            new Vector3(0, 0, 1),
                            new Vector3(1, 0, 1),
                            new Vector3(1, 1, 0),
                            new Vector3(1, 1, 1)
                        };


                        for (int i = 0; i < 6; i++)
                        {
                            int uvx = (int)(p.uv[i].X * 32);
                            int uvy = (int)(p.uv[i].Y * 32);
                            int uvw = (int)(p.uv[i].Z * 32);
                            int uvh = (int)(p.uv[i].W * 32);

                            if (click_uv == i)
                            {
                                float newuvx = old_uv.X - (clicked_pos.X - mouse_x) * (1.0f / 32.0f);
                                float newuvy = old_uv.Y - (clicked_pos.Y - mouse_y) * (1.0f / 32.0f);
                                p.ChangeUV(new Vector4(newuvx, newuvy, p.uv[i].Z, p.uv[i].W), i);
                            }

                            if (mouse_x > uvx && mouse_y > uvy && mouse_x < uvx + uvw && mouse_y < uvy + uvh)
                            {
                                can_deselect = false;

                                if (Settings.ATTACK.IsPressed())
                                {                                    
                                    if (click_uv == -1)
                                    {
                                        click_uv = i;
                                        clicked_pos = new Vector2(mouse_x, mouse_y);
                                        old_uv = p.uv[i];
                                    }
                                }
                            }

                            Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(X + uvx, Y + uvy, uvw, uvh), new Color(colors[i].X, colors[i].Y, colors[i].Z, 0.2f));
                        }
                    }

                }
            }



            if (file_menu == 0)
            {
                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(5, 75, 125, 300), Color.LightGray);

                if (DrawButton(-850, -400, 100 / 2, 75 / 2, "model", 0.5f))
                {
                    if (clicked)
                    {
                        currentDirectory = FileUtils.GetResourcePath(new ResourcePath("Inignoto", "", "assets"));
                        scroll = 0;
                        save_dialog = true;
                        save_model = true;
                        selected_file = "";
                    }
                    hovered_menu = true;
                }

                if (hovered_menu)
                    clicked = false;
            }
            else if (file_menu == 1)
            {
                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(130, 75, 125, 300), Color.LightGray);

                if (DrawButton(-725, -400, 100 / 2, 75 / 2, "model", 0.5f))
                {
                    if (clicked)
                    {
                        currentDirectory = FileUtils.GetResourcePath(new ResourcePath("Inignoto", "", "assets"));
                        scroll = 0;
                        load_dialog = true;
                        load_model = true;
                        selected_file = "";
                    }
                    hovered_menu = true;
                }

                if (hovered_menu)
                    clicked = false;
            }

            if (!hovered_menu)
            {
                if (clicked)
                {
                    file_menu = -1;
                }
            }

        }

        private bool delete_world = false;
        private WorldProperties deleting = null;
        private WorldProperties creating = null;
        KeyReader name_reader = null;
        KeyReader seed_reader = new KeyReader(20);
        public void RenderSingleplayer(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime gameTime, bool clicked)
        {
            int w = 300;

            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

#pragma warning disable CS8321 // Local function is declared but never used
            void DrawCenteredString(int x, int y, string str, Color color)
#pragma warning restore CS8321 // Local function is declared but never used
            {
                int strwidth = (int)(FontManager.mandrill_regular.width + FontManager.mandrill_regular.spacing) * str.Length;
                DrawString(spriteBatch, width, height, 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2, 1080 / 2 - 50 + y, 1.0f, FontManager.mandrill_regular, str, color);
            }

            bool DrawButton(int x, int y, string str, float font_size = 1.0f, bool left_align = false)
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
                if (!left_align)
                {
                    DrawString(spriteBatch, width, height, x + 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2, 1080 / 2 - 50 + y, font_size, FontManager.mandrill_regular, str, color);
                } else
                {
                    DrawString(spriteBatch, width, height, x + 1920 / 2 - w - 40, 1080 / 2 - 50 + y, font_size, FontManager.mandrill_regular, str, color);
                }
                if (flag && clicked)
                {
                    SoundEffects.ui_click.Play(Settings.GUI_VOLUME / 100.0f, 1.0f, 0.0f);
                }
                return flag;
            }

            if (singleplayer_state == SingleplayerState.SELECT)
            {
                for (int i = world_page * 10; i < worlds.Count && i < world_page * 10 + 10; i++)
                {
                    int x = i % 2;
                    int y = (i - x) / 2;
                    int X = (x * 2) * 325 - 325;
                    int Y = y * 100 - 300;
                    string name = worlds[i].name;
                    float scale = 1.0f;
                    if (delete_world && deleting == worlds[i])
                    {
                        name = "Click again to delete";
                        scale = 0.75f;
                    }
                    if (DrawButton(X, Y, name, scale))
                    {
                        if (clicked)
                        {
                            if (delete_world)
                            {
                                if (deleting == worlds[i])
                                {
                                    ResourcePath directory = new ResourcePath("", "", "Worlds/" + worlds[i].name);
                                    if (Directory.Exists(FileUtils.GetResourcePath(directory)))
                                        Directory.Delete(FileUtils.GetResourcePath(directory), true);
                                    worlds.Remove(worlds[i]);
                                    world_page = 0;
                                    break;
                                }
                                deleting = worlds[i];
                            } else
                            {
                                Inignoto.game.world.Construct(worlds[i].name, worlds[i]);
                                openGui = null;
                                //Inignoto.game.player = new Entities.Client.Player.ClientPlayerEntity(Inignoto.game.world, new Vector3f(Inignoto.game.world.radius * 2, 10, Inignoto.game.world.radius));
                                Inignoto.game.world.entities.Add(Inignoto.game.player);
                                Inignoto.game.player.position = new Vector3(Inignoto.game.world.radius * 2, 10, Inignoto.game.world.radius);
                                Inignoto.game.player.position.Y = Inignoto.game.world.properties.generator.GetHeight(Inignoto.game.player.position.X, Inignoto.game.player.position.Z, Inignoto.game.world.radius, Inignoto.game.world.properties.infinite)[0] + 1;
                                Inignoto.game.player.OnGround = true;
                                Inignoto.game.player.FallStart = Inignoto.game.player.position.Y;
                                Inignoto.game.player.gamemode = worlds[i].default_gamemode;
                                Inignoto.game.player.health = 100.0f;
                                Inignoto.game.player.Load();
                                Inignoto.game.player.TicksExisted = 0;
                                Inignoto.game.game_state = Inignoto.GameState.GAME;
                                
                                return;
                            }
                            
                        }
                    }
                }
                if (world_page * 10 + 10 < worlds.Count)
                {
                    if (DrawButton(325, 200, "next"))
                    {
                        if (clicked)
                        {
                            world_page++;
                        }
                    }
                }
                if (world_page > 0)
                {
                    if (DrawButton(-325, 200, "back"))
                    {
                        if (clicked)
                        {
                            world_page--;
                        }
                    }
                }
                if (DrawButton(0, 400, "back"))
                {
                    if (clicked)
                    {
                        menu_state = MenuState.TITLE;
                        delete_world = false;
                        deleting = null;
                    }
                }
                if (DrawButton(-325, 300, !delete_world ? "delete" : "cancel"))
                {
                    if (clicked)
                    {
                        delete_world = !delete_world;
                        deleting = null;
                    }
                }
                if (DrawButton(325, 300, "create"))
                {
                    if (clicked)
                    {
                        singleplayer_state = SingleplayerState.CREATE;
                        creating = new WorldProperties("New World", null);
                        name_reader = null;
                        delete_world = false;
                        deleting = null;
                        seed_reader.finished = true;
                        seed_reader.text = ""+new Random().Next();
                    }
                }
            }
            else if (singleplayer_state == SingleplayerState.CREATE)
            {
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

                double gravity = DrawSlider(-325, 0, "gravity: " + creating.gravity, creating.gravity, 9.81f);

                if (clicked && seed_reader.finished == false)
                {
                    seed_reader.finished = true;
                }

                string SEED = "seed: " + seed_reader.text;
                if (seed_reader.finished == false)
                {
                    SEED += (gameTime.TotalGameTime.TotalMilliseconds % 1000 <= 500 ? "|" : "");
                }
                if (DrawButton(325, 0, SEED, 0.9f, true))
                {
                    if (clicked)
                    {
                        seed_reader.finished = false;
                    }
                }

                if (DrawButton(-325, 100, "Infinite: " + creating.infinite))
                {
                    if (clicked)
                    {
                        creating.infinite = !creating.infinite;
                    }
                }

                if (DrawButton(325, 100, "Mode: " + creating.default_gamemode.ToString()))
                {
                    if (clicked)
                    {
                        int gm = (int)creating.default_gamemode;
                        gm++;
                        gm %= 3;
                        creating.default_gamemode = (Gamemode)gm;
                    }
                }

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(325 - 30, 300, 1920 - 325 * 2, 75), Color.Gray);

                
                if (name_reader != null)
                {
                    DrawString(spriteBatch, width, height, 325 - 30, 300, 1.0f, FontManager.mandrill_regular, creating.name + (gameTime.TotalGameTime.TotalMilliseconds % 1000 <= 500 ? "|" : ""), Color.White);
                }
                else
                {
                    DrawString(spriteBatch, width, height, 325 - 30, 300, 1.0f, FontManager.mandrill_regular, creating.name, Color.White);
                }



                if (clicked && name_reader != null)
                {
                    name_reader = null;
                }
                if (mouse_x > 325 - 30 && mouse_y > 300 && mouse_x < 325 - 30 + 1920 - 325 * 2 && mouse_y < 375)
                {
                    if (clicked)
                    {
                        name_reader = new KeyReader(45);
                        name_reader.text = creating.name;
                    }
                }
                if (seed_reader.finished == false)
                {
                    seed_reader.Update(gameTime);
                    string str = "";
                    for (int i = 0; i < seed_reader.text.Length; i++)
                    {

                        if (char.IsLetterOrDigit(seed_reader.text[i]) || seed_reader.text[i] == ' ')
                        {
                            str += seed_reader.text[i];
                        }
                    }
                    seed_reader.text = str;
                }

                if (name_reader != null)
                {
                    name_reader.Update(gameTime);
                    string str = "";
                    for (int i = 0; i < name_reader.text.Length; i++)
                    {
                        
                        if (char.IsLetterOrDigit(name_reader.text[i]) || name_reader.text[i] == ' ')
                        {
                            str += name_reader.text[i];
                        }
                    }
                    
                    name_reader.text = str;
                    creating.name = name_reader.text;
                    if (name_reader.finished)
                    {
                        name_reader = null;
                    }
                }

                if (mouse_released)
                {
                    creating.gravity = (float)MathF.Max(1, MathF.Round((float)gravity * 10) / 10.0f);
                }

                if (DrawButton(-325, 300, "back"))
                {
                    if (clicked)
                    {
                        singleplayer_state = SingleplayerState.SELECT;
                    }
                }
                if (creating.name.Trim().Equals(""))
                {
                    DrawButton(325, 300, "Enter a name first!");
                }
                else
                if (DrawButton(325, 300, "create"))
                {
                    if (clicked)
                    {
                        int match = 0;
                        do
                        {
                            match = 0;
                            for (int j = 0; j < worlds.Count; j++)
                            {
                                if (creating.name.Equals(worlds[j].name))
                                {
                                    creating.name += "_";
                                    match++;
                                }
                            }
                        } while (match > 0);

                        if (int.TryParse(seed_reader.text, out int r))
                        {
                            creating.seed = r;
                        } else
                        {
                            string str = "";
                            for (int i = 0; i < seed_reader.text.Length; i++)
                            {
                                if (char.IsDigit(seed_reader.text[i]))
                                {
                                    str += seed_reader.text[i];
                                } else
                                {
                                    str += (int)seed_reader.text[i];
                                }
                            }
                            if (int.TryParse(str, out int s))
                            {
                                creating.seed = s;
                            } else
                            {
                                creating.seed = new Random().Next();
                            }
                        }

                        worlds.Add(creating);
                        singleplayer_state = SingleplayerState.SELECT;
                    }
                }
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
                if (flag && clicked)
                {
                    SoundEffects.ui_click.Play(Settings.GUI_VOLUME / 100.0f, 1.0f, 0.0f);
                }
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
                if (!inGame)
                {
                    if (DrawButton(0, 300, "back"))
                    {
                        if (clicked)
                        {
                            menu_state = MenuState.TITLE;
                        }
                    }
                } else
                {
                    if (DrawButton(-325, 300, "exit to menu"))
                    {
                        if (clicked)
                        {
                            Inignoto.game.world.Dispose();
                            Inignoto.game.game_state = Inignoto.GameState.MENU;
                            Inignoto.game.player.Save();
                            Inignoto.game.player.Inventory = new Inventory.PhysicalInventory(Inignoto.game.player);
                            menu_state = MenuState.TITLE;
                            inGame = false;
                        }
                    }
                    if (DrawButton(325, 300, "back to game"))
                    {
                        if (clicked)
                        {
                            openGui = null;
                        }
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
                        float sensitivity = (int)System.Math.Round(DrawSlider(325, -300, "Mouse Sensitivity: " + (int)(Settings.MOUSE_SENSITIVITY * 100), Settings.MOUSE_SENSITIVITY * 100, 100.0f));

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

                                DrawControl(325 * x * 2 - 325, y * 100 - 300, "Hotbar " + (i + 1), Settings.HOTBAR_KEYS[i]);

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


                    if (DrawButton(325, 100, "Shadows: " + (Settings.SHADOWS ? "On" : "Off")))
                    {
                        if (clicked)
                        {
                            Settings.SHADOWS = !Settings.SHADOWS;
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


                    float master = (int)System.Math.Round(DrawSlider(-325, -200, "Master: " + Settings.MASTER_VOLUME + " / 100", Settings.MASTER_VOLUME, 100.0f));
                    float music = (int)System.Math.Round(DrawSlider(325, -200, "Music: " + Settings.MUSIC_VOLUME + " / 100", Settings.MUSIC_VOLUME, 100.0f));


                    float player = (int)System.Math.Round(DrawSlider(-325, -100, "Players: " + Settings.PLAYER_VOLUME + " / 100", Settings.PLAYER_VOLUME, 100.0f));
                    float creature = (int)System.Math.Round(DrawSlider(-325, 0, "Creatures: " + Settings.CREATURE_VOLUME + " / 100", Settings.CREATURE_VOLUME, 100.0f));
                    float enemy = (int)System.Math.Round(DrawSlider(-325, 100, "Hostile: " + Settings.ENEMY_VOLUME + " / 100", Settings.ENEMY_VOLUME, 100.0f));
                    
                    float block = (int)System.Math.Round(DrawSlider(325, -100, "Blocks: " + Settings.BLOCK_VOLUME + " / 100", Settings.BLOCK_VOLUME, 100.0f));
                    float ambient = (int)System.Math.Round(DrawSlider(325, 0, "Ambience: " + Settings.AMBIENT_VOLUME + " / 100", Settings.AMBIENT_VOLUME, 100.0f));
                    float gui = (int)System.Math.Round(DrawSlider(325, 100, "Gui: " + Settings.GUI_VOLUME + " / 100", Settings.GUI_VOLUME, 100.0f));
                   
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
                if (flag && clicked)
                {
                    SoundEffects.ui_click.Play(Settings.GUI_VOLUME / 100.0f, 1.0f, 0.0f);
                }
                return flag;
            }
            
            if (DrawButton(0, "singleplayer"))
            {
                if (clicked)
                {
                    menu_state = MenuState.SINGLEPLAYER;

                    worlds.Clear();
                    string path = FileUtils.GetResourcePath(new ResourcePath("", "", "Worlds"));
                    if (!Directory.Exists(path))
                    {
                        Directory.CreateDirectory(path);
                    }
                    string[] files = FileUtils.GetAllDirectories(new ResourcePath("", "", "Worlds"));
                    if (files.Length > 0)
                    {
                        foreach(string file in files)
                        {
                            string[] s = file.Split(Path.DirectorySeparatorChar);
                            WorldProperties properties = new WorldProperties(s[s.Length - 1], null);
                            worlds.Add(properties);
                        }
                    }
                }
            }

            DrawButton(100, "multiplayer");

            if (DrawButton(200, "settings"))
            {
                if (clicked)
                {
                    menu_state = MenuState.SETTINGS;
                }
            }

            if (DrawButton(300, "model creator"))
            {
                if (clicked)
                {
                    menu_state = MenuState.MODEL_CREATOR;
                    mesh = null;
                }
            }

            if (DrawButton(400, "quit"))
            {
                if (clicked)
                {
                    Inignoto.game.Exit();
                }
            }
        }

        public void DrawPlayer(GraphicsDevice device, GameEffect effect, int width, int height, GameTime time)
        {
            Inignoto.game.player.arm_swing = 0;
            Inignoto.game.player.TryPlayAnimation(Inignoto.game.player.idle);
            Inignoto.game.player.look.X = 0;
            Inignoto.game.player.look.Y = 0;
            Inignoto.game.player.look.Z = 0;
            Inignoto.game.player.FallStart = Inignoto.game.player.position.Y;
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

            Inignoto.game.player.SetModelTransform(new Vector3(-0.7f, -1f, 1.25f), new Vector3(0, 0, 0));

            if (Inignoto.game.player.model != null)
            {
                if (Inignoto.game.player.Perspective != 1)
                    Inignoto.game.player.model.Stop();
                Inignoto.game.player.Render(device, effect, time, true);
                Inignoto.game.player.SetModelTransform(Inignoto.game.player.position, Inignoto.game.player.look);
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
