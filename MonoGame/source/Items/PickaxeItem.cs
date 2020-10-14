﻿using Inignoto.Entities;
using Inignoto.Tiles;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static Inignoto.World.World;

namespace Inignoto.Items
{
    public class PickaxeItem : ToolItem
    {
        public PickaxeItem(string name, double cooldown = 1.0f, bool model = true, Vector3 position = new Vector3(), Vector3 rotation = new Vector3(), Vector3 scale = new Vector3()) : base(name, cooldown, model, position, rotation, scale)
        {
            
        }

        protected override bool Attack(Entity user, GameTime time, World.RaytraceResult.TileRaytraceResult result)
        {
            if (result == null) return false;

            user.world.SetVoxel(result.pos, TileManager.AIR.DefaultData);

            return true;
        }

        protected override bool Use(Entity user, GameTime time)
        {
            return base.Use(user, time);
        }

    }
}
