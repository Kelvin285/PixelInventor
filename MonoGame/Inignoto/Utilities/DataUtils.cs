using System.Collections.Generic;
using System.IO;

namespace Inignoto.Utilities
{
    public class DataUtils
    {
        public static Dictionary<string, string> GetDataList(string[] lines)
        {
            Dictionary<string, string> data = new Dictionary<string, string>();
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
                while(data.ContainsKey(a))
                {
                    a += " ";
                }
                data.Add(a, b);
            }
            return data;
        }
    }
}
