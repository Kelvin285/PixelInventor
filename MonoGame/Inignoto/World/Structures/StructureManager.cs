﻿using Inignoto.Tiles;
using Inignoto.World.Structures.Trees;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.World.Structures
{
    public class StructureManager
    {
        public static List<Structure> REGISTRY = new List<Structure>();

        public static BasicTreeStructure OAK_TREE1;
        public static BasicTreeStructure SNOW_PINE_TREE;

        public static void RegisterStructures()
        {
            RegisterStructure(OAK_TREE1 = new BasicTreeStructure("trees/oak_tree1.structure", new World.TilePos(-2, 0, -3), TileManager.DIRT.DefaultData));
            RegisterStructure(SNOW_PINE_TREE = new BasicTreeStructure("trees/pine_tree1.structure", new World.TilePos(-5, 3, -4), TileManager.SNOW.DefaultData));

        }

        public static void RegisterStructure(Structure structure)
        {
            REGISTRY.Add(structure);
        }
    }
}
