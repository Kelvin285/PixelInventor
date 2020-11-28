using Inignoto.Tiles;
using Inignoto.World.Structures.DesertStructures;
using Inignoto.World.Structures.Trees;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Structures
{
    public class StructureRegistry
    {
        public static List<Structure> REGISTRY = new List<Structure>();

        public static BasicTreeStructure OAK_TREE1;
        public static BasicTreeStructure OAK_TREE3;

        public static BasicTreeStructure SNOW_PINE_TREE;

        public static CactusStructure CACTUS;

        public static void RegisterStructures()
        {
            RegisterStructure(OAK_TREE1 = new ForestOakStructure());

            //RegisterStructure(SNOW_PINE_TREE = new BasicTreeStructure("trees/pine_tree1.structure", new World.TilePos(-5, 3, -4), TileRegistry.SNOW.DefaultData));
            RegisterStructure(CACTUS = new CactusStructure());
        }

        public static void RegisterStructure(Structure structure)
        {
            REGISTRY.Add(structure);
        }
    }
}
