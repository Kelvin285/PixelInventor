using Inignoto.Entities;
using Inignoto.Inventory;
using Inignoto.Math;
using Inignoto.Tiles;
using Inignoto.World.RaytraceResult;
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
        public static Vector3f pos1 = null;
        public static Vector3f pos2 = null;
        public StructureWrenchItem(string name, double cooldown = 1.0f, bool model = true, Vector3 position = new Vector3(), Vector3 rotation = new Vector3(), Vector3 scale = new Vector3()) : base(name, cooldown, model, position, rotation, scale)
        {
            MissCooldown = 0;
            BlockHitCooldown = 0.2f;
            canBreakBlocks = false;
        }

        protected override ActionResult Attack(Entity user, GameTime time, World.RaytraceResult.TileRaytraceResult result)
        {
            if (result == null) return ActionResult.MISS;

            Inignoto.game.client_system.SendChatMessage(-1, "Position 1 set to [" + result.pos.x + ", " + result.pos.y + ", " + result.pos.z + "]");

            pos1 = new Vector3f(result.pos.x, result.pos.y, result.pos.z);

            return ActionResult.BLOCK;
        }

        protected override ActionResult Use(Entity user, GameTime time)
        {
            World.World world = user.world;
            Vector3f eyePosition = user.GetEyePosition();

            TileRaytraceResult result = world.RayTraceTiles(eyePosition, new Vector3f(eyePosition).Add(user.ForwardLook.Mul(user.ReachDistance)), Tiles.Tile.TileRayTraceType.BLOCK);

            if (result == null) return ActionResult.MISS;

            Inignoto.game.client_system.SendChatMessage(-1, "Position 2 set to [" + result.pos.x + ", " + result.pos.y + ", " + result.pos.z + "]");

            pos2 = new Vector3f(result.pos.x, result.pos.y, result.pos.z);

            return ActionResult.BLOCK;
        }

    }
}
