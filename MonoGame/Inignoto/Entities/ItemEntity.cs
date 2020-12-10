using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Effects;
using Inignoto.Entities.Player;
using Inignoto.Inventory;
using Inignoto.Math;
using Inignoto.Tiles;
using Inignoto.World;
using Inignoto.World.RaytraceResult;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Inignoto.Entities
{
    public class ItemEntity : Entity
    {
        public ItemStack Stack {get; private set;}

        private int timeUntilPickup = 0;

        public ItemEntity(World.World world, Vector3 position, ItemStack stack, int timeUntilPickup = 0) : base(world, position)
        {
            StepHeight = 0;
            Stack = stack;
            this.timeUntilPickup = timeUntilPickup;
        }

        public override void Update(GameTime time)
        {
            base.Update(time);
            float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;

            OnGround = false;
            if (velocity.Y <= 0)
            {
                TileRaytraceResult result = world.RayTraceTiles(position, position + new Vector3(0, velocity.Y - 0.5f, 0), Tile.TileRayTraceType.BLOCK);

                if (result != null)
                {
                    if (TileRegistry.GetTile(result.data.tile_id).BlocksMovement())
                    {
                        velocity.Y = 0;
                        position.Y = result.hit.Y + 0.5f;
                        OnGround = true;
                    }
                        
                }
            }

            PlayerEntity closest = null;
            float distance = float.MaxValue;

            if (TicksExisted > timeUntilPickup)
                for (int i = 0; i < world.entities.Count(); i++)
                {
                    if (world.entities[i] is PlayerEntity) {
                        PlayerEntity player = (PlayerEntity)world.entities[i];
                        float dist = Vector3.Distance(player.GetEyePosition(), position + new Vector3(0, 1, 0));
                        if (dist <= 2.5f)
                        {
                            if (dist < distance)
                            {
                                distance = dist;
                                closest = player;
                            }
                        }
                    }
                }
            if (closest != null)
            {
                MoveToPlayer(closest, time);
            } else
            {
                if (!OnGround)
                {
                    velocity.Y = MathHelper.Lerp(velocity.Y, -1, 0.01f * delta);
                }
            }


            position += velocity;
        }

        private void MoveToPlayer(PlayerEntity player, GameTime time)
        {
            float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;

            position = Vector3.Lerp(position, player.GetEyePosition(), 0.075f * delta);
            if (Vector3.Distance(player.GetEyePosition(), position) <= 1)
            {
                bool add = false;

                ItemStack stack = Stack;
                if (stack == null)
                {
                    world.entities.Remove(this);
                    return;
                }
                for (int j = 0; j < 10; j++)
                {
                    if (player.Inventory.hotbar[j] == null)
                    {
                        player.Inventory.hotbar[j] = stack;
                        add = true;
                        Stack = null;
                        world.entities.Remove(this);
                        return;
                    }
                    int n = player.Inventory.TryAddToStack(player.Inventory.hotbar[j], stack, out stack, PhysicalInventory.SlotType.NORMAL);
                    if (n != -1)
                    {
                        add = true;
                        break;
                    }
                }
                if (!add)
                {
                    for (int j = 0; j < 30; j++)
                    {
                        if (player.Inventory.inventory[j] == null)
                        {
                            player.Inventory.inventory[j] = stack;
                            add = true;
                            Stack = null;
                            world.entities.Remove(this);
                            return;
                        }
                        int n = player.Inventory.TryAddToStack(player.Inventory.inventory[j], stack, out stack, PhysicalInventory.SlotType.NORMAL);
                        if (n != -1)
                        {
                            add = true;
                            break;
                        }
                    }
                }

                Stack = stack;
                if (stack != null)
                {
                    if (stack.count <= 0)
                    {
                        world.entities.Remove(this);
                    }
                    else
                    {
                        this.timeUntilPickup += 20;
                    }
                }
                else
                {
                    world.entities.Remove(this);
                }


            }
        }

        public override void Render(GraphicsDevice device, GameEffect effect, GameTime time, bool showModel = false)
        {
            ItemStack stack = Stack;
            if (stack != null)
                if (stack.item.Mesh != null)
                {
                    stack.item.Mesh.SetPosition(position + new Vector3(0, (float)System.Math.Sin(TicksExisted * 0.01f) * 0.25f, 0));
                    stack.item.Mesh.SetScale(new Vector3(0.5f, 0.5f, 0.5f));
                    stack.item.Mesh.SetRotation(Quaternion.CreateFromYawPitchRoll(TicksExisted * 0.01f, 0, 0));
                    stack.item.Mesh.Draw(effect, device);
                }
        }
    }
}
