using Inignoto.World.Structures.Trees;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Structures
{
    public class StructureManager
    {
        public static List<Structure> REGISTRY = new List<Structure>();

        public static BasicTreeStructure BASIC_TREE;
        public static void RegisterStructures()
        {
            RegisterStructure(BASIC_TREE = new BasicTreeStructure("trees/oak_tree1.structure", new World.TilePos(2, 1, 3)));
        }

        public static void RegisterStructure(Structure structure)
        {
            REGISTRY.Add(structure);
        }
    }
}
