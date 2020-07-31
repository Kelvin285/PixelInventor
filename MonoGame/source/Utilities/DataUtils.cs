using System.Collections.Generic;

namespace Inignoto.Utilities
{
    public class DataUtils
    {
        public static Dictionary<string, string> GetDataList(string file_contents)
        {
            Dictionary<string, string> data = new Dictionary<string, string>();
            string[] lines = file_contents.Split('\n');
            foreach (string line in lines)
            {
                char[] ch = line.ToCharArray();
                string a = "";
                string b = "";
                bool start = false;
                bool second = false;
                char last = ' ';
                foreach (char c in ch)
                {
                    if (start && last == '\\')
                    {
                        if (c == '"')
                        {
                            if (!second) a += c;
                            else b += c;
                            continue;
                        }
                    }
                    if (!start && c == '=')
                    {
                        second = true;
                        continue;
                    }
                    if (!start && c == '"')
                    {
                        start = true;
                        continue;
                    }
                    if (start && c == '"')
                    {
                        start = false;
                        continue;
                    }
                    if (start && !second)
                    {
                        a += c;
                    }
                    if (start && second)
                    {
                        b += c;
                    }
                    last = c;
                }
                data.Add(a, b);
            }
            return data;
        }
    }
}
