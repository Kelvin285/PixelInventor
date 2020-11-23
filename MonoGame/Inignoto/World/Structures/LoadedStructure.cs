using Inignoto.Imported;
using Inignoto.Utilities;
using Inignoto.World.Chunks;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.World.World;

namespace Inignoto.World.Structures
{
    public class LoadedStructure : Structure
    {
        protected Dictionary<TilePos, int[]> tiles;

        protected TilePos offset;

        public LoadedStructure(string structure, TilePos offset)
        {
            Load(structure);
            this.offset = offset;
        }


        private void Load(string structure)
        {
            ResourcePath path = new ResourcePath("Inignoto", "structures/"+structure, "assets");
            
            Dictionary<string, string> voxels = FileUtils.LoadFileAsDataList(path);

            Dictionary<TilePos, int[]> clip = new Dictionary<TilePos, int[]>();

            foreach (string key in voxels.Keys)
            {
                if (key.Equals(""))
                {
                    continue;
                }
                string[] data = key.Split(',');
                int x = int.Parse(data[0]);
                int y = int.Parse(data[1]);
                int z = int.Parse(data[2]);

                string[] voxel = voxels[key].Split(',');
                int tiledata = int.Parse(voxel[0]);
                int overlay = int.Parse(voxel[1]);

                clip.Add(new TilePos(x, y, z), new int[] { tiledata, overlay });
            }

            tiles = clip;
        }
    }
}
