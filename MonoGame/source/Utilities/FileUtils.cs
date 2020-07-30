using System.Collections.Generic;
using System.IO;

namespace Inignoto.Utilities
{
    class FileUtils
    {

        public static string GetResourcePath(ResourcePath resource_path)
        {
            string modid = resource_path.modid;
            string path = resource_path.path;
            string root = resource_path.root;
            char separator = '/';
            if (!path.Contains((separator+"")))
            {
                separator = Path.DirectorySeparatorChar;
            }
            string new_path = root + separator + modid + separator + path;
            string[] split = new_path.Split(separator);
            
            return Path.Combine(new_path);
        }

        public static FileStream GetStreamForPath(ResourcePath resource_path, FileMode mode)
        {
            return new FileStream(GetResourcePath(resource_path), mode);
        }

        public static string LoadFileAsString(ResourcePath resource_path)
        {
            if (!File.Exists(GetResourcePath(resource_path)))
            {
                return "";
            }
            FileStream stream = GetStreamForPath(resource_path, FileMode.Open);
            StreamReader reader = new StreamReader(stream);
            string str = reader.ReadToEnd();
            reader.Close();
            return str;
        }

        public static Dictionary<string, string> LoadFileAsDataList(ResourcePath resource_path)
        {
            string file_contents = LoadFileAsString(resource_path);
            return DataUtils.GetDataList(file_contents);
        }

        public static string[] GetAllDirectories(ResourcePath resource_path)
        {
            string path = GetResourcePath(resource_path);
            return Directory.GetDirectories(path);
        }

        public static string[] getAllFiles(ResourcePath resource_path)
        {
            string path = GetResourcePath(resource_path);
            return Directory.GetFiles(path);
        }

        public static void WriteStringToFile(ResourcePath directory, ResourcePath resource_path, string write)
        {
            if (!Directory.Exists(GetResourcePath(directory)))
            {
                Directory.CreateDirectory(GetResourcePath(directory));
            }
            FileStream stream = GetStreamForPath(resource_path, FileMode.OpenOrCreate);
            StreamWriter writer = new StreamWriter(stream);
            writer.WriteAsync(write);
            writer.Close();
        }
    }
}
