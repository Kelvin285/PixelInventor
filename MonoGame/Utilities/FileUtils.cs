using System.IO;

namespace Inignoto.Utilities
{
    class FileUtils
    {

        public static string getResourcePath(ResourcePath resource_path)
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

        public static FileStream getStreamForPath(ResourcePath resource_path, FileMode mode)
        {
            return new FileStream(getResourcePath(resource_path), mode);
        }

        public static string loadFileAsString(ResourcePath resource_path)
        {
            FileStream stream = getStreamForPath(resource_path, FileMode.Open);
            StreamReader reader = new StreamReader(stream);
            string str = reader.ReadToEnd();
            reader.Close();
            return str;
        }

        public static string[] getAllDirectories(ResourcePath resource_path)
        {
            string path = getResourcePath(resource_path);
            return Directory.GetDirectories(path);
        }

        public static string[] getAllFiles(ResourcePath resource_path)
        {
            string path = getResourcePath(resource_path);
            return Directory.GetFiles(path);
        }

        public static void writeStringToFile(ResourcePath resource_path, string write)
        {
            FileStream stream = getStreamForPath(resource_path, FileMode.OpenOrCreate);
            StreamWriter writer = new StreamWriter(stream);
            writer.WriteAsync(write);
            writer.Close();
        }
    }
}
