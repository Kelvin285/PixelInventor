using Inignoto.Entities;
using Inignoto.Inventory;
using Inignoto.Math;
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
    public class StructureWrenchItem : ToolItem
    {
        public StructureWrenchItem(string name, double cooldown = 1.0f, bool model = true, Vector3 position = new Vector3(), Vector3 rotation = new Vector3(), Vector3 scale = new Vector3()) : base(name, cooldown, model, position, rotation, scale)
        {
            canBreakBlocks = false;
        }

        protected override ActionResult Attack(Entity user, GameTime time, World.RaytraceResult.TileRaytraceResult result)
        {
            if (result == null) return ActionResult.MISS;

            return ActionResult.BLOCK;
        }

        protected override ActionResult Use(Entity user, GameTime time)
        {
            return base.Use(user, time);
        }

    }
}
