using Inignoto.Entities;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Items
{
    public class ToolItem : Item
    {
        public ToolItem(string name, double cooldown = 1.0f, bool model = true, Vector3 position = new Vector3(), Vector3 rotation = new Vector3(), Vector3 scale = new Vector3()) : base(name, 1, cooldown, model, position, rotation, scale)
        {
            
        }

        protected override ActionResult Attack(Entity user, GameTime time, World.RaytraceResult.TileRaytraceResult result)
        {
            return base.Attack(user, time, result);
        }

        protected override ActionResult Use(Entity user, GameTime time)
        {
            return base.Use(user, time);
        }

    }
}
