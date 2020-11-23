using Inignoto.Utilities;
using Inignoto.World.Generator;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static Inignoto.Entities.Player.PlayerEntity;

namespace Inignoto.World
{
    public class WorldProperties
    {
        public ChunkGenerator generator;
        public float gravity;
        public bool infinite = false;
        public string name;
        public int seed = 0;
        public Gamemode default_gamemode = Gamemode.SURVIVAL;

        public WorldProperties(string name)
        {
            Load();
            generator = new ChunkGenerator();
            gravity = 9.81f;
            this.name = name;
        }

        public void Load()
        {
            ResourcePath directory = new ResourcePath("Chunks", "", "Worlds/" + name);
            ResourcePath file = new ResourcePath("", "world.properties", "Worlds/" + name);
            if (!Directory.Exists(FileUtils.GetResourcePath(directory)))
            {
                return;
            }
            if (!File.Exists(FileUtils.GetResourcePath(file)))
            {
                return;
            }
            string str = File.ReadAllText(FileUtils.GetResourcePath(file));
            string[] data = str.Split("\n");
            gravity = float.Parse(data[0]);
            infinite = bool.Parse(data[1]);
            name = data[2];
            seed = int.Parse(data[3]);
        }

        public void Save()
        {
            ResourcePath directory = new ResourcePath("Chunks", "", "Worlds/" + name);
            ResourcePath file = new ResourcePath("", "world.properties", "Worlds/" + name);
            if (!Directory.Exists(FileUtils.GetResourcePath(directory)))
            {
                Directory.CreateDirectory(FileUtils.GetResourcePath(directory));
            }
            if (File.Exists(FileUtils.GetResourcePath(file)))
            {
                File.Delete(FileUtils.GetResourcePath(file));
            }
            string str = "";
            str += gravity + "\n";
            str += infinite + "\n";
            str += name + "\n";
            str += seed;
            File.WriteAllText(FileUtils.GetResourcePath(file), str);
        }
    }
}
