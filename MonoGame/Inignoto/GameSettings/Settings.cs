using Inignoto.Utilities;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
namespace Inignoto.GameSettings
{
    public class Settings
    {
        public static int HORIZONTAL_VIEW = 6;
        public static int VERTICAL_VIEW = 4;
        public static float FIELD_OF_VIEW = 90;
        public static float MOUSE_SENSITIVITY = 0.05f;
        public static bool FULLSCREEN = false;


        public static float MASTER_VOLUME = 100;
        public static float PLAYER_VOLUME = 100;
        public static float CREATURE_VOLUME = 100;
        public static float ENEMY_VOLUME = 100;
        public static float BLOCK_VOLUME = 100;
        public static float AMBIENT_VOLUME = 100;
        public static float MUSIC_VOLUME = 100;
        public static float GUI_VOLUME = 100;

        public static bool VSYNC = false;
        public static bool SHADOWS = false;
        public static bool PARALLEL_CHUNK_GENERATION = false;

        public static bool HEAD_BOBBING = true;

        public static InputSetting FORWARD = new InputSetting(Keys.W, false);
        public static InputSetting BACKWARD = new InputSetting(Keys.S, false);
        public static InputSetting LEFT = new InputSetting(Keys.A, false);
        public static InputSetting RIGHT = new InputSetting(Keys.D, false);
        public static InputSetting JUMP = new InputSetting(Keys.Space, false);
        public static InputSetting SNEAK = new InputSetting(Keys.LeftControl, false);
        public static InputSetting RUN = new InputSetting(Keys.LeftShift, false);
        public static InputSetting CRAWL = new InputSetting(Keys.C, false);
        public static InputSetting INVENTORY = new InputSetting(Keys.Escape, false);
        public static InputSetting ATTACK = new InputSetting(0, true);
        public static InputSetting USE = new InputSetting(1, true);
        public static InputSetting PERSPECTIVE_SWITCH = new InputSetting(Keys.F5, false);
        public static InputSetting FULLSCREEN_KEY = new InputSetting(Keys.F11, false);
        public static InputSetting CHAT = new InputSetting(Keys.Enter, false);
        public static InputSetting RELOAD_ASSETS = new InputSetting(Keys.F1, false);


        public static InputSetting[] HOTBAR_KEYS = new InputSetting[10];


        public static void LoadSettings()
        {
            for (int i = 0; i < 10; i++)
            {
                int I = i != 9 ? i + 1 : 0;
                HOTBAR_KEYS[i] = new InputSetting(Keys.D0 + I, false);
            }

            ResourcePath path = new ResourcePath("Inignoto:settings.txt", "data");
            Dictionary<string, string> data = FileUtils.LoadFileAsDataList(path);
            foreach (string a in data.Keys)
            {
                if (data.TryGetValue(a, out string b))
                {
                    if (a.Equals("HORIZONTAL_VIEW"))
                    {
                        int.TryParse(b, out HORIZONTAL_VIEW);
                        Console.WriteLine("HORIZONTAL VIEW: " + b);
                    }
                    if (a.Equals("VERTICAL_VIEW"))
                    {
                        int.TryParse(b, out VERTICAL_VIEW);
                    }
                    if (a.Equals("FIELD_OF_VIEW"))
                    {
                        float.TryParse(b, out FIELD_OF_VIEW);
                    }
                    if (a.Equals("MOUSE_SENSITIVITY"))
                    {
                        float.TryParse(b, out MOUSE_SENSITIVITY);
                    }
                    if (a.Equals("MASTER_VOLUME"))
                    {
                        float.TryParse(b, out MASTER_VOLUME);
                        MASTER_VOLUME = System.Math.Max(System.Math.Min(MASTER_VOLUME, 100), 0);
                    }
                    if (a.Equals("PLAYER_VOLUME"))
                    {
                        float.TryParse(b, out PLAYER_VOLUME);
                        PLAYER_VOLUME = System.Math.Max(System.Math.Min(PLAYER_VOLUME, 100), 0);
                    }
                    if (a.Equals("CREATURE_VOLUME"))
                    {
                        float.TryParse(b, out CREATURE_VOLUME);
                        CREATURE_VOLUME = System.Math.Max(System.Math.Min(CREATURE_VOLUME, 100), 0);
                    }
                    if (a.Equals("ENEMY_VOLUME"))
                    {
                        float.TryParse(b, out ENEMY_VOLUME);
                        ENEMY_VOLUME = System.Math.Max(System.Math.Min(ENEMY_VOLUME, 100), 0);
                    }
                    if (a.Equals("BLOCK_VOLUME"))
                    {
                        float.TryParse(b, out BLOCK_VOLUME);
                        BLOCK_VOLUME = System.Math.Max(System.Math.Min(BLOCK_VOLUME, 100), 0);
                    }
                    if (a.Equals("AMBIENT_VOLUME"))
                    {
                        float.TryParse(b, out AMBIENT_VOLUME);
                        AMBIENT_VOLUME = System.Math.Max(System.Math.Min(AMBIENT_VOLUME, 100), 0);
                    }
                    if (a.Equals("MUSIC_VOLUME"))
                    {
                        float.TryParse(b, out MUSIC_VOLUME);
                        MUSIC_VOLUME = System.Math.Max(System.Math.Min(MUSIC_VOLUME, 100), 0);
                    }
                    if (a.Equals("GUI_VOLUME"))
                    {
                        float.TryParse(b, out GUI_VOLUME);
                        GUI_VOLUME = System.Math.Max(System.Math.Min(GUI_VOLUME, 100), 0);
                    }
                    if (a.Equals("VSYNC"))
                    {
                        bool.TryParse(b, out VSYNC);
                    }
                    if (a.Equals("FULLSCREEN"))
                    {
                        bool.TryParse(b, out FULLSCREEN);
                    }
                    if (a.Equals("SHADOWS"))
                    {
                        bool.TryParse(b, out SHADOWS);
                    }
                    if (a.Equals("PARALLEL_CHUNK_GENERATION"))
                    {
                        bool.TryParse(b, out PARALLEL_CHUNK_GENERATION);
                    }
                    if (a.Equals("HEAD_BOBBING"))
                    {
                        bool.TryParse(b, out HEAD_BOBBING);
                    }
                    if (a.Equals("FORWARD"))
                    {
                        FORWARD.Read(b);
                    }
                    if (a.Equals("BACKWARD"))
                    {
                        BACKWARD.Read(b);
                    }
                    if (a.Equals("LEFT"))
                    {
                        LEFT.Read(b);
                    }
                    if (a.Equals("RIGHT"))
                    {
                        RIGHT.Read(b);
                    }
                    if (a.Equals("JUMP"))
                    {
                        JUMP.Read(b);
                    }
                    if (a.Equals("SNEAK"))
                    {
                        SNEAK.Read(b);
                    }
                    if (a.Equals("RUN"))
                    {
                        RUN.Read(b);
                    }
                    if (a.Equals("CRAWL"))
                    {
                        CRAWL.Read(b);
                    }
                    if (a.Equals("ATTACK"))
                    {
                        ATTACK.Read(b);
                    }
                    if (a.Equals("USE"))
                    {
                        USE.Read(b);
                    }
                    if (a.Equals("INVENTORY"))
                    {
                        INVENTORY.Read(b);
                    }
                    if (a.Equals("PERSPECTIVE_SWITCH"))
                    {
                        PERSPECTIVE_SWITCH.Read(b);
                    }
                    if (a.Equals("FULLSCREEN_KEY"))
                    {
                        FULLSCREEN_KEY.Read(b);
                    }
                    if (a.Equals("CHAT_KEY"))
                    {
                        CHAT.Read(b);
                    }
                    if (a.Equals("RELOAD_TEXTURES_KEY"))
                    {
                        RELOAD_ASSETS.Read(b);
                    }
                    for (int i = 0; i < 10; i++)
                    {
                        if (a.Equals("HOTBAR_KEY"+i))
                        {
                            HOTBAR_KEYS[i].Read(b);
                        }
                    }
                }
            }
        }
        public static string GetSaveString(string a, string b)
        {
            return "\""+a+"\"=\"" + b + "\"\n";
        }
        public static void SaveSettings()
        {
            string str = "";
            str += GetSaveString("HORIZONTAL_VIEW", "" + HORIZONTAL_VIEW);
            str += GetSaveString("VERTICAL_VIEW", "" + VERTICAL_VIEW);
            str += GetSaveString("FIELD_OF_VIEW", "" + FIELD_OF_VIEW);
            str += GetSaveString("MOUSE_SENSITIVITY", "" + MOUSE_SENSITIVITY);

            str += GetSaveString("MASTER_VOLUME", "" + MASTER_VOLUME);

            str += GetSaveString("PLAYER_VOLUME", "" + PLAYER_VOLUME);
            str += GetSaveString("CREATURE_VOLUME", "" + CREATURE_VOLUME);
            str += GetSaveString("ENEMY_VOLUME", "" + ENEMY_VOLUME);
            str += GetSaveString("BLOCK_VOLUME", "" + BLOCK_VOLUME);
            str += GetSaveString("AMBIENT_VOLUME", "" + AMBIENT_VOLUME);
            str += GetSaveString("MUSIC_VOLUME", "" + MUSIC_VOLUME);
            str += GetSaveString("GUI_VOLUME", "" + GUI_VOLUME);
            str += GetSaveString("PARALLEL_CHUNK_GENERATION", "" + PARALLEL_CHUNK_GENERATION);
            str += GetSaveString("HEAD_BOBBING", "" + HEAD_BOBBING);

            str += GetSaveString("VSYNC", "" + VSYNC);
            str += GetSaveString("FULLSCREEN", "" + FULLSCREEN);
            str += GetSaveString("SHADOWS", "" + SHADOWS);

            str += GetSaveString("FORWARD", FORWARD.Write());
            str += GetSaveString("BACKWARD", BACKWARD.Write());
            str += GetSaveString("LEFT", LEFT.Write());
            str += GetSaveString("RIGHT", RIGHT.Write());
            str += GetSaveString("JUMP", JUMP.Write());
            str += GetSaveString("SNEAK", SNEAK.Write());
            str += GetSaveString("RUN", RUN.Write());
            str += GetSaveString("CRAWL", CRAWL.Write());
            str += GetSaveString("ATTACK", ATTACK.Write());
            str += GetSaveString("USE", USE.Write());
            str += GetSaveString("INVENTORY", INVENTORY.Write());
            str += GetSaveString("PERSPECTIVE_SWITCH", PERSPECTIVE_SWITCH.Write());
            str += GetSaveString("FULLSCREEN_KEY", FULLSCREEN_KEY.Write());
            str += GetSaveString("CHAT_KEY", CHAT.Write());
            str += GetSaveString("RELOAD_TEXTURES_KEY", RELOAD_ASSETS.Write());
            for (int i = 0; i < 10; i++)
            {
                str += GetSaveString("HOTBAR_KEY" + i, HOTBAR_KEYS[i].Write());
            }

            ResourcePath path = new ResourcePath("Inignoto:settings.txt", "data");
            ResourcePath directory = new ResourcePath("Inignoto:", "data");
            FileUtils.WriteStringToFile(directory, path, str);
        }
    }
}
