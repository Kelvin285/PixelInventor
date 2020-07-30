using Inignoto.Utilities;
using System.Collections.Generic;
namespace Inignoto.GameSettings
{
    class Settings
    {
        public static int HORIZONTAL_VIEW = 8;
        public static int VERTICAL_VIEW = 4;
        public static float FIELD_OF_VIEW = 90;
        public static float MOUSE_SENSITIVITY = 0.05f;


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

            ResourcePath path = new ResourcePath("Inignoto:settings.txt", "data");
            ResourcePath directory = new ResourcePath("Inignoto:", "data");
            FileUtils.WriteStringToFile(directory, path, str);
        }
    }
}
