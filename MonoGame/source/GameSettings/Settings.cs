using Inignoto.Utilities;
using System.Collections.Generic;
namespace Inignoto.GameSettings
{
    public class Settings
    {
        public static int HORIZONTAL_VIEW = 8;
        public static int VERTICAL_VIEW = 4;
        public static float FIELD_OF_VIEW = 90;
        public static float MOUSE_SENSITIVITY = 0.05f;


        public static float MASTER_VOLUME = 100;
        public static float PLAYER_VOLUME = 100;
        public static float CREATURE_VOLUME = 100;
        public static float ENEMY_VOLUME = 100;
        public static float BLOCK_VOLUME = 100;
        public static float AMBIENT_VOLUME = 100;
        public static float MUSIC_VOLUME = 100;


        public static void LoadSettings()
        {
            ResourcePath path = new ResourcePath("Inignoto:settings.txt", "data");
            Dictionary<string, string> data = FileUtils.LoadFileAsDataList(path);
            foreach (string a in data.Keys)
            {
                if (data.TryGetValue(a, out string b))
                {
                    if (a.Equals("HORIZONTAL_VIEW"))
                    {
                        int.TryParse(b, out HORIZONTAL_VIEW);
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

            ResourcePath path = new ResourcePath("Inignoto:settings.txt", "data");
            ResourcePath directory = new ResourcePath("Inignoto:", "data");
            FileUtils.WriteStringToFile(directory, path, str);
        }
    }
}
