﻿using Inignoto.Entities;
using Inignoto.Entities.Client.Player;
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
    public class PickaxeItem : ToolItem
    {
        public PickaxeItem(string name, double cooldown = 1.0f, bool model = true, Vector3 position = new Vector3(), Vector3 rotation = new Vector3(), Vector3 scale = new Vector3()) : base(name, cooldown, model, position, rotation, scale)
        {
            BlockHitCooldown = 0.5f;
            MissCooldown = 0.5f;
            EntityHitCooldown = 0.5f;
        }

        protected override ActionResult Attack(Entity user, GameTime time, World.RaytraceResult.TileRaytraceResult result)
        {

            if (user is ClientPlayerEntity)
            {
                ClientPlayerEntity player = (ClientPlayerEntity)user;
                player.TryPlayAnimation(player.mining);
            }

            if (result == null) return ActionResult.MISS;

            user.world.entities.Add(new ItemEntity(user.world, new Vector3f(result.pos.x + 0.5f, result.pos.y + 0.5f, result.pos.z + 0.5f), new ItemStack(TileManager.GetTile(result.data.tile_id))));
            user.world.SetVoxel(result.pos, TileManager.AIR.DefaultData);

            return ActionResult.BLOCK;
        }

        protected override ActionResult Use(Entity user, GameTime time)
        {
            return base.Use(user, time);
        }

    }
}
