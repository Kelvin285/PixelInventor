using Inignoto.Audio;
using Inignoto.Common;
using Inignoto.Crafting;
using Inignoto.Effects;
using Inignoto.GameSettings;
using Inignoto.Graphics.Fonts;
using Inignoto.Inventory;
using Inignoto.Items;
using Inignoto.Math;
using Inignoto.Tiles;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using System.Xml.Serialization;
using static Inignoto.Inventory.PhysicalInventory;
using Keyboard = Microsoft.Xna.Framework.Input.Keyboard;

namespace Inignoto.Graphics.Gui
{
    public class InventoryGui : Hud
    {
        private float DropdownAnimation = 1.0f;
        private bool closing = false;

        public static GameSound open_sound;
        public static GameSound close_sound;

        private PhysicalInventory inventory;

        private RenderTarget2D target;

        private bool mouse_pressed = false;

        private int PHYSICAL = 0, DIGITAL = 1, SANDBOX = 2;
        private int current_inventory = 0;

        private int recipe_index = 0;

        private List<CraftingRecipe> recipes = new List<CraftingRecipe>();

        private KeyReader keyReader;
        public InventoryGui(PhysicalInventory inventory)
        {
            OverrideHealthbar = true;
            if (open_sound == null)
            {
                open_sound = new GameSound(SoundEffects.inventory_open.CreateInstance(), SoundType.GUI);
                close_sound = new GameSound(SoundEffects.inventory_close.CreateInstance(), SoundType.GUI);

                open_sound.Volume = 0.75f;
                close_sound.Volume = 0.75f;
            }
            open_sound.Play();

            this.inventory = inventory;
            keyReader = new KeyReader(16);

            UpdateRecipes();
        }

        public override void Close()
        {
            closing = true;
            close_sound.Play();
        }

        private bool MouseInSpace(int mx, int my, Rectangle rect)
        {
            return rect.Contains(mx, my);
        }

        private int last_scroll;
        public void RenderMainInventory(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {
            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

            Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(0, -(int)(DropdownAnimation * 360 * 3), 640 * 3, 293 * 3), new Rectangle(0, 0, 640, 293), Color.White);

            if (Inignoto.game.player != null)
            {
                char[] ch = Inignoto.game.player.Name.ToCharArray();
                for (int i = 0; i < ch.Length; i++)
                {
                    int x = 89 * 3 + i * 12;
                    int y = -(int)(DropdownAnimation * 360 * 3) + 227 * 3 - (int)(i * 0.5f);
                    DrawString(spriteBatch, width, height, x, y, 0.5f, FontManager.mandrill_bold, "" + ch[i], Color.White);
                }

            }

            int drop = -(int)(DropdownAnimation * 360 * 3);

            RenderHealthbar(device, spriteBatch, width, height, time, 67 * 3, drop + 245 * 3);


            

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


            //228, 105 <- First inventory slot
            //Horizontal spacing: 1 px
            //Vertical Spacing: 24 px

            if (last_scroll - Mouse.GetState().ScrollWheelValue < 0)
            {
                recipe_index++;
                if (recipe_index > recipes.Count - 7) recipe_index = recipes.Count - 7;
            }
            if (last_scroll - Mouse.GetState().ScrollWheelValue > 0)
            {
                recipe_index--;
                if (recipe_index < 0) recipe_index = 0;
            }

            last_scroll = Mouse.GetState().ScrollWheelValue;

            for (int i = recipe_index; i < recipes.Count && i < recipe_index + 9; i++)
            {
                int x = 228 + (i - recipe_index) * 31 + 15;
                int y = 105 - 31 * 2;

                Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);

                if (i - recipe_index == 0 || i == recipes.Count || i - recipe_index == 8)
                {
                    color.A = 255 / 2;
                }
                if (i - recipe_index == 1 || i == recipes.Count - 1 || i - recipe_index == 7)
                {
                    color.A -= 255 / 4;
                }

                if (i < 0 || i >= recipes.Count) continue;
                if (recipes[i] == null) continue;

                ItemStack stack = recipes[i].output;
                
                if (MouseInSpace(mouse_x, mouse_y, new Rectangle(x * 3, y * 3 + drop, 90, 90)))
                {
                    //24, 330
                    Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(x * 3, y * 3 + drop, 90, 90), new Rectangle(24, 330, 30, 30), color);

                    for (int ii = 0; ii < recipes[i].input.Length; ii++)
                    {
                        ItemStack stack2 = recipes[i].input[ii];

                        if (stack2 != null)
                        {
                            if (stack2.item.GetRenderTexture() != null)
                                DrawItem(spriteBatch, width, height, stack2, x * 3 + ii * 31 * 3 - (int)(recipes[i].input.Length * 31 * 3 / 2.0) + (recipes[i].input.Length % 2 == 1 ? (int)(31 * 3 / 2.0) : 0), y * 3 + drop + 31 * 3, 0.75f, 0.75f);

                        }
                    }
                } else
                {
                    Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(x * 3, y * 3 + drop, 90, 90), new Rectangle(54, 300, 30, 30), color);
                }

                if (stack != null)
                {
                    if (stack.item.GetRenderTexture() != null)
                        DrawItem(spriteBatch, width, height, stack, x * 3, y * 3 + drop, 0.75f, 0.75f);

                }
            }

            for (int i = 0; i < 10; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    int x = 228 + i * 31;
                    int y = 105 + j * (30 + 24);
                    ItemStack stack = inventory.inventory[i + j * 10];

                    if (MouseInSpace(mouse_x, mouse_y, new Rectangle(x * 3, y * 3 + drop, 90, 90)))
                    {
                        //24, 330
                        Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(x * 3, y * 3 + drop, 90, 90), new Rectangle(24, 330, 30, 30), Color.White);

                    }

                    if (stack != null)
                    {
                        if (stack.item.GetRenderTexture() != null)
                            DrawItem(spriteBatch, width, height, stack, x * 3, y * 3 + drop, 0.75f, 0.75f);

                    }
                }
            }

            if (MouseInSpace(mouse_x, mouse_y, new Rectangle(228 * 3, 252 * 3 + drop, 90, 90)))
            {
                Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(228 * 3, 252 * 3 + drop, 90, 90), new Rectangle(24, 330, 30, 30), Color.White);
            }

            if (inventory.trashStack != null)
            {


                if (inventory.trashStack.item.GetRenderTexture() != null)
                {
                    //228 * 3, 252 * 3 + drop
                    DrawItem(spriteBatch, width, height, inventory.trashStack, 228 * 3, 252 * 3 + drop, 0.75f, 0.75f);
                }
            }
            else
            {
                Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(236 * 3, 258 * 3 + drop, 14 * 3, 18 * 3), new Rectangle(57, 342, 14, 18), Color.White);
            }

            if (target != null)
            {
                //96, 106
                int w = (int)(164 * 2.5f);
                int h = (int)(108 * 2.5f);

                Draw(spriteBatch, width, height, (Texture2D)target, new Rectangle((96 + (57 / 2)) * 3 - (w * 3 / 5) - 5 + 170, (106 + (97 / 2)) * 3 - h / 2 - 10 + drop + 15, (int)(w / 2.5f), h - 10), new Rectangle(1920 / 4 + 270, 0, (int)(1920 / 2.5f), 1080), Color.White);
            }
        }

        private int page = 0;
        private string searchstring = "";
        private bool[] pressed = new bool[256];
        private bool clicked = false;
        private bool just_clicked = false;
        private bool has_none = false;

        private List<Item> search_items = new List<Item>();

        public void RenderSandboxInventory(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime gameTime)
        {
            int pages = ItemRegistry.REGISTRY.Count / (10 * 4);

            if (search_items.Count > 0)
            {
                pages = search_items.Count / (10 * 4);
            }

            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

            int drop = -(int)(DropdownAnimation * 360 * 3);
            int X = 1920 / 2 - (322 * 3) / 2;
            int Y = 1080 / 2 - (173 * 3) / 2 + drop;

            //322, 173
            Draw(spriteBatch, width, height, Textures.Textures.item_browser, new Rectangle(X, Y, 322 * 3, 173 * 3), new Rectangle(0, 0, 322, 173), Color.White);

            //first slot: 6, 29
            //slot size: 30 (+1)
            if (!has_none)
            for (int x = 0; x < 10; x++)
            {
                for (int y = 0; y < 4; y++)
                {
                    int I = x + y * 10 + page * (10 * 4);

                    Item item = null;
                    if (search_items.Count == 0)
                    {
                        if (I < ItemRegistry.ITEM_LIST.Count)
                        {
                            item = ItemRegistry.ITEM_LIST[I];

                            if (MouseInSpace(mouse_x, mouse_y, new Rectangle(X + 18 + x * 93, Y + 29 * 3 + y * 93, 90, 90)))
                            {
                                Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(X + 18 + x * 93, Y + 29 * 3 + y * 93, 90, 90), new Rectangle(24, 330, 30, 30), Color.White);
                                if (Settings.ATTACK.IsPressed())
                                {
                                    inventory.grabStack = new ItemStack(item, item.max_stack);
                                }
                            }
                            DrawItem(spriteBatch, width, height, new ItemStack(item, item.max_stack), X + 18 + x * 93, Y + 29 * 3 + y * 93, 0.75f, 0.75f);
                        }
                    } else
                    {
                        if (I < search_items.Count)
                        {
                            
                            item = search_items[I];

                            if (MouseInSpace(mouse_x, mouse_y, new Rectangle(X + 18 + x * 93, Y + 29 * 3 + y * 93, 90, 90)))
                            {
                                Draw(spriteBatch, width, height, Textures.Textures.inventory, new Rectangle(X + 18 + x * 93, Y + 29 * 3 + y * 93, 90, 90), new Rectangle(24, 330, 30, 30), Color.White);
                                if (Settings.ATTACK.IsPressed())
                                {
                                    inventory.grabStack = new ItemStack(item, item.max_stack);
                                }
                            }
                            DrawItem(spriteBatch, width, height, new ItemStack(item, item.max_stack), X + 18 + x * 93, Y + 29 * 3 + y * 93, 0.75f, 0.75f);
                        }
                    }
                    
                }
            }

            keyReader.finished = false;
            keyReader.Update(gameTime);
            searchstring = keyReader.text;

            search_items.Clear();
            if (searchstring.Trim().Length > 0)
            {
                has_none = true;
                foreach (Item item in ItemRegistry.ITEM_LIST)
                {
                    if (item.Name.ToLower().Replace("_", " ").Contains(searchstring.ToLower().Trim()))
                    {
                        search_items.Add(item);
                        has_none = false;
                    }
                }
                page = 0;
            }
            else
            {
                has_none = false;
            }

            //283, 155
            string pagestr = (page + 1) + "/" + (pages + 1);
            DrawString(spriteBatch, width, height, X + 283 * 3 + 5 + 25 - (int)(10 * (pagestr.Length / 2.0f)), Y + 155 * 3 - 5, 0.5f, FontManager.mandrill_bold, pagestr, Color.White);

            bool a = (int)gameTime.TotalGameTime.Seconds % 2 <= 0 && searchstring.Length == 0;
            string s2 = searchstring + (a ? "|" : "");
            //230, 14
            DrawString(spriteBatch, width, height, X + 230 * 3 + 5, Y + 14 * 3, 0.5f, FontManager.mandrill_bold, s2.Substring(0, MathHelper.Min(s2.Length, 18)), Color.White);


            //right = 305, 155
            //arrow size = 5, 7
            if (MouseInSpace(mouse_x, mouse_y, new Rectangle(X + 305 * 3, Y + 155 * 3, 5 * 3, 7 * 3)))
            {
                
                if (just_clicked)
                {
                    if (page < pages) page++;
                }
            } else
            {
                Draw(spriteBatch, width, height, Textures.Textures.item_browser, new Rectangle(X + 305 * 3, Y + 155 * 3, 5 * 3, 7 * 3), new Rectangle(305, 155, 5, 7), Color.LightGray);
            }
            if (MouseInSpace(mouse_x, mouse_y, new Rectangle(X + 277 * 3, Y + 155 * 3, 5 * 3, 7 * 3)))
            {
                if (just_clicked)
                {
                    if (page > 0) page--;
                }
            } else
            {
                Draw(spriteBatch, width, height, Textures.Textures.item_browser, new Rectangle(X + 277 * 3, Y + 155 * 3, 5 * 3, 7 * 3), new Rectangle(277, 155, 5, 7), Color.LightGray);
            }


            if (GameSettings.Settings.ATTACK.IsPressed())
            {
                if (!clicked)
                {
                    just_clicked = true;
                } else
                {
                    just_clicked = false;
                }
                clicked = true;
            } else
            {
                clicked = false;
            }
        }

        public override void Render(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {


            

            float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;

            if (!closing)
            {
                DropdownAnimation = MathHelper.Lerp(DropdownAnimation, 0.0f, 0.1f * delta);
            }
            else
            {
                DropdownAnimation = MathHelper.Lerp(DropdownAnimation, 1.0f, 0.1f * delta);
                if (DropdownAnimation >= 0.9f)
                {
                    openGui = null;
                }
            }


            if (current_inventory == PHYSICAL)
                RenderMainInventory(device, spriteBatch, width, height, time);

            if (current_inventory == SANDBOX)
                RenderSandboxInventory(device, spriteBatch, width, height, time);
            else
                searchstring = "";

            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));

            


            int drop = -(int)(DropdownAnimation * 360 * 3);

            bool DrawButton(int x, int y, string str, float font_size = 1.0f)
            {
                int w = 100;
                int X = x + 1920 / 2 - w - 40;
                int Y = 1080 / 2 - 50 + y;
                int W = w * 2;
                int H = 50;

                Color color = Color.White;
                Color color2 = Color.LightSlateGray;

                bool flag = false;

                if (mouse_x >= X && mouse_y >= Y && mouse_x <= X + W && mouse_y <= Y + H)
                {
                    flag = true;
                    color = Color.Gray;
                    color2 = Color.LightGray;
                }

                Draw(spriteBatch, width, height, Textures.Textures.white_square, new Rectangle(x + 1920 / 2 - w - 40, Y, w * 2, H), color2);
                int strwidth = (int)((FontManager.mandrill_regular.width + FontManager.mandrill_regular.spacing) * str.Length * font_size);
                DrawString(spriteBatch, width, height, x + 1920 / 2 - w - 40 + (w - strwidth) / 2 + w / 2, Y, font_size, FontManager.mandrill_regular, str, color);

                return flag;
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
            }

            if (DrawButton(875, 500, "Settings", 0.75f))
            {
                if (clicked)
                    openGui = new MainMenu(true);
            }


            if (inventory.grabStack != null)
            {
                if (inventory.grabStack.item.GetRenderTexture() != null)
                {
                    DrawItem(spriteBatch, width, height, inventory.grabStack, mouse_x - 15 * 3, mouse_y - 15 * 3, 0.75f, 0.75f);
                }
            }

            //0, 44, 38, 38 <- Circle
            //0, 44+38, 38, 38 <- Circle (highlighted)

            //40, 44, 20, 19 <- Backpack
            //63, 44, 22, 19 <- computer

            
            if (this.MouseInSpace(mouse_x, mouse_y, new Rectangle(2 * 3, 2 * 3 + 40 * 3 + drop, 38 * 3, 38 * 3)))
            {
                //digital inventory icon (Computer)
                Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + 40 * 3 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44+38, 38, 38), Color.White);
                if (GameSettings.Settings.ATTACK.IsPressed())
                {
                    current_inventory = DIGITAL;
                }
            }
            else
            {
                //digital inventory icon (Computer)
                Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + 40 * 3 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44, 38, 38), Color.White);

            }

            if (this.MouseInSpace(mouse_x, mouse_y, new Rectangle(2 * 3, 2 * 3 + drop, 38 * 3, 38 * 3)))
            {
                //main inventory icon (Backpack)
                Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44+38, 38, 38), Color.White);
                if (GameSettings.Settings.ATTACK.IsPressed())
                {
                    current_inventory = PHYSICAL;
                }
            }
            else
            {
                //main inventory icon (Backpack)
                Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44, 38, 38), Color.White);
            }
            //Backpack
            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3 + 19 * 3 - 10 * 4, 2 * 3 + drop + 19 * 3 - (int)(19 * 4 * 0.5), 20 * 4, 19 * 4), new Rectangle(40, 44, 20, 19), Color.White);
            //Computer
            Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle((int)(2 * 3 + 19 * 3 - 11 * 3.5f), 2 * 3 + 40 * 3 + drop + 19 * 3 - (int)(19 * 3.5f * 0.5), (int)(22 * 3.5f), (int)(19 * 3.5f)), new Rectangle(63, 44, 22, 19), Color.White);
            if (Inignoto.game.player.gamemode == Entities.Player.PlayerEntity.Gamemode.SANDBOX)
            {
                if (MouseInSpace(mouse_x, mouse_y, new Rectangle(2 * 3, 2 * 3 + 40 * 6 + drop, 38 * 3, 38 * 3)))
                {
                    //Circle
                    Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + 40 * 6 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44+38, 38, 38), Color.White);
                    if (GameSettings.Settings.ATTACK.IsPressed())
                    {
                        current_inventory = SANDBOX;
                    }
                }
                else
                {
                    //Circle
                    Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle(2 * 3, 2 * 3 + 40 * 6 + drop, 38 * 3, 38 * 3), new Rectangle(0, 44, 38, 38), Color.White);

                }

                //Block
                Draw(spriteBatch, width, height, Textures.Textures.hud, new Rectangle((int)(2 * 3 + 19 * 3 - 14 * 2.5f), 2 * 3 + 40 * 6 + drop + 19 * 3 - (int)(17 * 5 * 0.5), 14 * 5, 17 * 5), new Rectangle(91, 47, 14, 17), Color.White);
                
            }

        }

        public override void PreRender(GraphicsDevice device, SpriteBatch spriteBatch, int width, int height, GameTime time)
        {
            Inignoto.game.projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(90),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);
            GameResources.effect.Projection = Inignoto.game.projectionMatrix;
            DrawPlayer(device, GameResources.effect, 1920, 1080, time);
            Inignoto.game.projectionMatrix = Matrix.CreatePerspectiveFieldOfView(
                               MathHelper.ToRadians(Settings.FIELD_OF_VIEW),
                               Inignoto.game.GraphicsDevice.DisplayMode.AspectRatio,
                0.01f, 1000f);
            GameResources.effect.Projection = Inignoto.game.projectionMatrix;
        }


        public void DrawPlayer(GraphicsDevice device, GameEffect effect, int width, int height, GameTime time)
        {
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

        }

        protected override void UpdateKeys()
        {
            
        }

        private int grabTick = 0;
        public void UpdateRecipes()
        {
            foreach(CraftingRecipe recipe in CraftingRegistry.REGISTRY)
            {
                bool has = true;
                foreach (ItemStack stack in recipe.input)
                {
                    int count = 0;
                    if (stack != null)
                    {
                        foreach (ItemStack s in inventory.hotbar)
                        {
                            if (s != null)
                            {
                                if (s.item == stack.item)
                                {
                                    count += s.count;
                                }
                            }
                        }

                        foreach (ItemStack s in inventory.inventory)
                        {
                            if (s != null)
                            {
                                if (s.item == stack.item)
                                {
                                    count += s.count;
                                }
                            }
                        }

                        if (count < stack.count)
                        {
                            has = false;
                            break;
                        }
                    } else
                    {
                        has = false;
                    }
                    
                }

                if (has)
                {
                    if (!recipes.Contains(recipe))
                    {
                        recipes.Add(recipe);
                    }
                } else
                {
                    if (recipes.Contains(recipe))
                    {
                        recipes.Remove(recipe);
                    }
                }
                
            }
            if (recipe_index > recipes.Count - 7) recipe_index = recipes.Count - 7;
        }

        public void UpdateMainInventory(GameTime time, int width, int height)
        {
            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));
            int drop = -(int)(DropdownAnimation * 360 * 3);


            if (Settings.USE.IsPressed())
            {
                grabTick++;
                if (grabTick > 10) grabTick = 0;
            } else
            {
                grabTick = 0;
            }

            for (int i = recipe_index; i < recipes.Count && i < recipe_index + 9; i++)
            {
                int x = 228 + (i - recipe_index) * 31 + 15;
                int y = 105 - 31 * 2;

                if (i >= recipes.Count || i < 0) continue;
                if (recipes[i] == null) continue;

                ItemStack stack = recipes[i].output;
                if (stack != null)
                    if (MouseInSpace(mouse_x, mouse_y, new Rectangle(x * 3, y * 3 + drop, 90, 90)))
                    {
                        if (Settings.ATTACK.IsJustPressed() || Settings.USE.IsPressed() && grabTick == 0)
                        {
                        

                            if (inventory.grabStack == null)
                            {
                                inventory.grabStack = new ItemStack(stack.item, stack.count);
                            } else
                            {
                                if (inventory.grabStack.item != stack.item)
                                {
                                    continue;
                                }
                                if (inventory.grabStack.count + stack.count > stack.item.max_stack)
                                {
                                    continue;
                                }
                                inventory.grabStack.count += stack.count;
                            }
                            for (int ii = 0; ii < recipes[i].input.Length; ii++)
                            {
                                ItemStack s = recipes[i].input[ii];
                                int count = 0;
                                for(int j = 0; j < inventory.hotbar.Length; j++)
                                {
                                    ItemStack st = inventory.hotbar[j];
                                    if (count < s.count)
                                    {
                                        if (st != null)
                                        {
                                            if (st.item == s.item)
                                            {
                                                while (st.count > 0)
                                                {
                                                    st.count--;
                                                    count++;
                                                    if (count >= s.count) break;
                                                }
                                                if (st.count <= 0)
                                                {
                                                    inventory.hotbar[j] = null;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    else break;
                                }

                                for (int j = 0; j < inventory.inventory.Length; j++)
                                {
                                    ItemStack st = inventory.inventory[j];
                                    if (count < s.count)
                                    {
                                        if (st != null)
                                        {
                                            if (st.item == s.item)
                                            {
                                                while (st.count > 0)
                                                {
                                                    st.count--;
                                                    count++;
                                                    if (count >= s.count) break;
                                                }
                                                if (st.count <= 0)
                                                {
                                                    inventory.inventory[j] = null;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    else break;
                                }
                                UpdateRecipes();
                                    return;
                            }
                        }
                    }
            }

                    for (int i = 0; i < 10; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    int x = 228 + i * 31;
                    int y = 105 + j * (30 + 24);

                    if (MouseInSpace(mouse_x, mouse_y, new Rectangle(x * 3, y * 3 + drop, 90, 90)))
                    {
                        UpdateRecipes();
                        UpdateSlot(mouse_x, mouse_y, drop, inventory.inventory[i + j * 10], out inventory.inventory[i + j * 10], SlotType.NORMAL);
                    }
                }
            }

            if (MouseInSpace(mouse_x, mouse_y, new Rectangle(228 * 3, 252 * 3 + drop, 90, 90))) //Trash slot
            {
                UpdateRecipes();
                UpdateSlot(mouse_x, mouse_y, drop, inventory.trashStack, out inventory.trashStack, SlotType.TRASH);
            }
        }

        public void UpdateHotbar(GameTime time, int width, int height)
        {
            int mouse_x = (int)(Inignoto.game.mousePos.X * (1920.0 / width));
            int mouse_y = (int)(Inignoto.game.mousePos.Y * (1080.0 / height));
            int drop = -(int)(DropdownAnimation * 360 * 3);

            for (int i = 0; i < 10; i++)
            {
                int x = (7 + i * 31) * 3 + 1920 / 2 - (324 * 3) / 2;
                int y = 5 * 3 + 1080 - 40 * 3;
                ItemStack stack = Inignoto.game.player.Inventory.hotbar[i];

                if (MouseInSpace(mouse_x, mouse_y, new Rectangle(x, y, 90, 90)))
                {
                    UpdateSlot(mouse_x, mouse_y, 0, inventory.hotbar[i], out inventory.hotbar[i], SlotType.NORMAL);
                }
            }
        }

        public override void Update(GameTime time, int width, int height)
        {

            UpdateKeys();

            if (current_inventory == PHYSICAL)
            UpdateMainInventory(time, width, height);
            UpdateHotbar(time, width, height);
            
        }

        private void UpdateSlot(int mouse_x, int mouse_y, int drop, ItemStack stack, out ItemStack o, SlotType type)
        {
            o = stack;
            if (GameSettings.Settings.ATTACK.IsJustPressed())
            {
                int add = inventory.TryAddToStack(stack, inventory.grabStack, out inventory.grabStack, type);
                if (add == -1)
                {
                    inventory.SwapStacks(stack, inventory.grabStack, out o, out inventory.grabStack, type);
                }

            }
            if (GameSettings.Settings.USE.IsJustPressed())
            {

                if (inventory.grabStack == null)
                {
                    inventory.grabStack = inventory.SplitStack(stack, out o, type);
                } else
                {
                    if (stack == null)
                    {
                        o = new ItemStack(inventory.grabStack.item, 0);
                        stack = o;
                    }
                    if (stack != null)
                    {
                        if (stack.count <= stack.item.max_stack && stack.item == inventory.grabStack.item)
                        {
                            stack.count++;
                            inventory.grabStack.count--;
                            if (inventory.grabStack.count <= 0) inventory.grabStack = null;
                        }
                    }
                }
            }
        }

        public static void Dispose()
        {
            if (open_sound != null)
            {
                open_sound.Dispose();
                close_sound.Dispose();
            }
        }

    }
}
