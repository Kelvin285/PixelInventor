namespace Inignoto.Utilities
{
    class ResourcePath
    {
        public string modid, path, root;
        public ResourcePath(string modid, string path, string root)
        {
            this.modid = modid;
            this.path = path;
            this.root = root;
        }
        public ResourcePath(string resource_path, string root)
        {
            string[] data = resource_path.Split(':');
            modid = data[0];
            path = data[1];
            this.root = root;
        }
    }
}
