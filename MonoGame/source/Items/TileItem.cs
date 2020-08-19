using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Audio;
using Inignoto.Entities;
using Inignoto.Entities.Player;
using Inignoto.Graphics.Textures;
using Inignoto.Graphics.World;
using Inignoto.Math;
using Inignoto.Tiles;
using Inignoto.Utilities;
using Inignoto.World.RaytraceResult;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Graphics;
using static Inignoto.World.World;

namespace Inignoto.Items
{
    public class TileItem : Item
    {

        public readonly Tile tile;

        public TileItem(Tile tile, int max_stack = 64) : base(tile.name, max_stack, 0.25f)
        {
            this.tile = tile;
            if (tile.IsVisible())
            Mesh = TileBuilder.BuildTile(-0.5f, -0.5f, -0.5f, tile.DefaultData, Inignoto.game.GraphicsDevice);
        }

        protected override bool Attack(Entity user, GameTime time)
        {
            base.Attack(user, time);
            return true;
        }

        protected override bool Use(Entity user, GameTime time)
        {
            World.World world = user.world;

            Vector3f eyePosition = user.GetEyePosition();

            TileRaytraceResult result = world.RayTraceTiles(eyePosition, new Vector3f(eyePosition).Add(user.ForwardLook.Mul(user.ReachDistance)), Tiles.Tile.TileRayTraceType.BLOCK);

            if (result == null) return false;

            Vector3f normal = result.intersection.normal;

            TilePos pos = new World.World.TilePos(result.pos.x + normal.X, result.pos.y + normal.Y, result.pos.z + normal.Z);
            if (TileManager.GetTile(world.GetVoxel(pos).tile_id).IsReplaceable())
            {
                bool intersects = false;
                foreach (Entity e in world.entities)
                {
                    Rectangle b1 = new Rectangle(pos.x * 32, pos.y * 32, 32, 32);
                    Rectangle b2 = new Rectangle(pos.x * 32, pos.z * 32, 32, 32);

                    Rectangle p1 = new Rectangle((int)(e.position.X * 32), (int)(e.position.Y * 32) + 1, (int)(e.size.X * 32), (int)(e.size.Y * 32) - 1);
                    Rectangle p2 = new Rectangle((int)(e.position.X * 32), (int)(e.position.Z * 32), (int)(e.size.X * 32), (int)(e.size.Z * 32));

                    if (b1.Intersects(p1) && b2.Intersects(p2))
                    {
                        intersects = true;
                        break;
                    }
                }
                if (!intersects)
                {
                    world.SetVoxel(pos, tile.DefaultData);
                    if (user is PlayerEntity)
                    {
                        PlayerEntity player = (PlayerEntity)user;
                        player.Inventory.hotbar[player.Inventory.selected].count--;
                        if (player.Inventory.hotbar[player.Inventory.selected].count <= 0)
                        {
                            player.Inventory.hotbar[player.Inventory.selected] = null;
                        }
                    }

                    SoundEffect[] sounds = tile.step_sound;
                    if (sounds != null)
                    {
                        SoundEffect effect = sounds[world.random.Next(sounds.Length)];
                        GameSound sound = new GameSound(effect.CreateInstance(), SoundType.PLAYERS);
                        sound.Play();
                        Inignoto.game.SoundsToDispose.Add(sound);
                    }
                    return true;
                }
            }
            return false;
        }

        public override void TryStopUsing(Entity user, GameTime time)
        {
            cooldown_time = time.TotalGameTime.TotalMilliseconds;
        }


        public override Texture2D GetRenderTexture()
        {
            Textures.TILE_ITEMS.TryGetValue(tile.DefaultData, out Texture2D texture);

            return texture;
        }

    }
}
