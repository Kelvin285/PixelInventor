using Inignoto.Audio;
using Inignoto.Math;
using Inignoto.Tiles;
using Inignoto.World;
using Inignoto.World.RaytraceResult;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Graphics;
using System.Collections.Generic;
using static Inignoto.World.World;

namespace Inignoto.Entities
{
    public class Entity
    {
        public Vector3f position;
        public readonly World.World world;

        public Vector3f velocity;
        public Vector3f size;
        
        public bool OnGround { get; protected set; }
        public bool LastOnGround { get; protected set; }

        public bool Running { get; protected set; }
        public bool Crouching { get; protected set; }
        public bool Crawling { get; protected set; }

        public float StepHeight { get; protected set; }

        public bool NearGround { get; protected set; }

        public bool Jumping { get; protected set; }

        public int TicksExisted { get; protected set; }

        public float FallStart { get; protected set; }

        public bool BlockAboveHead { get; protected set; }

        public string Name { get; protected set; }

        public float ReachDistance { get; protected set; }

        public float moveSpeed = 0.1f;

        public float health = 100.0f;
        public float hunger = 100.0f;
        public float stamina = 100.0f;
        public float defense = 0.0f;

        protected List<GameSound> SoundsToDispose = new List<GameSound>();

        protected double WalkCycle;

        public Vector3f look = new Vector3f(0, 0, 0);

        public SoundType soundType = SoundType.CREATURES;

        public Vector3f ForwardLook
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll((look.Y - 1) * ((float)System.Math.PI / 180.0f), look.X * ((float)System.Math.PI / 180.0f), look.Z * ((float)System.Math.PI / 180.0f)));
        }

        public Vector3f ForwardMotionVector
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll(look.Y * (float)System.Math.PI / 180, 0, 0));
        }

        public Vector3f RightMotionVector
        {
            get => new Vector3f(Vector3.Forward).Rotate(Quaternion.CreateFromYawPitchRoll((look.Y - 90) * (float)System.Math.PI / 180, 0, 0));
        }

        public Vector3f UpMotionVector
        {
            get => new Vector3f(0, 1, 0);
        }


        public Entity(World.World world, Vector3f position)
        {
            this.world = world;
            this.position = position;
            size = new Vector3f(0.5f, 1.8f, 0.5f);
            velocity = new Vector3f(0, 0, 0);
            
            world.entities.Add(this);

            StepHeight = 0.55f;
            ReachDistance = 4.0f;
        }

        public virtual void Update(GameTime time)
        {
            DoPhysicsUpdates(time);
            if (OnGround)
            {
                Jumping = false;
                if (!LastOnGround)
                LandOnGround();
                FallStart = position.Y;
            }
            TicksExisted++;
            for (int i = 0; i < SoundsToDispose.Count; i++)
            {
                if (SoundsToDispose[i].State != SoundState.Playing)
                {
                    SoundsToDispose[i].Dispose();
                    SoundsToDispose.Remove(SoundsToDispose[i]);
                }
            }
            LastOnGround = OnGround;
        }

        public void DoPhysicsUpdates(GameTime time)
        {
            
        }

        public virtual void Render(GraphicsDevice device, BasicEffect effect, GameTime time, bool showModel = false)
        {

        }
        
        public virtual float GetEyeHeight()
        {
            return size.Y * 0.85f;
        }

        public virtual float GetMovementSpeed(GameTime time)
        {
            if (Crawling) return 0.025f * (float)time.ElapsedGameTime.TotalSeconds * 60;
            if (Crouching) return 0.035f * (float)time.ElapsedGameTime.TotalSeconds * 60;
            if (Running) return 0.11f * (float)time.ElapsedGameTime.TotalSeconds * 60;
            return 0.055f * (float)time.ElapsedGameTime.TotalSeconds * 60;
        }

        public virtual Vector3f GetEyePosition()
        {
            return new Vector3f(position).Add(size.X * 0.5f, GetEyeHeight(), size.Z * 0.5f);
        }

        public TilePos GetTilePos()
        {
            return new TilePos(position.X + size.X * 0.5f, position.Y, position.Z + size.Z * 0.5f);
        }

        public virtual void DamageEntity(float damage)
        {
            damage -= defense;
            if (damage <= 0) damage = 1;
            health -= damage;
            if (health < 0) health = 0;
        }

        public virtual void LandOnGround()
        {
            //minimum damage = 10
            float damage = System.Math.Abs(FallStart - position.Y) / 5;
            if (damage > 1)
            {
                DamageEntity(damage * damage * damage * 5);
            }
        }

        public virtual void DoBlockCollisions()
        {
            OnGround = false;


            for (float sx = 0; sx < 2; sx++)
            {
                for (float sz = 0; sz < 2; sz++)
                {
                    TileRaytraceResult near_ground = world.RayTraceTiles(new Vector3f(position).Set(position).Add(size.X * sx, size.Y, size.Z * sz), new Vector3f(position).Set(position).Add(size.X * sx, -1.1f, size.Z * sz), Tiles.Tile.TileRayTraceType.BLOCK);
                    if (near_ground != null)
                    {
                        NearGround = true;
                    }
                    TileRaytraceResult ground_result = world.RayTraceTiles(new Vector3f(position).Set(position).Add(size.X * sx, size.Y, size.Z * sz), new Vector3f(position).Set(position).Add(size.X * sx, 0, size.Z * sz), Tiles.Tile.TileRayTraceType.BLOCK);
                    if (ground_result != null)
                    {
                        OnGround = true;
                        if (velocity.Y < 0)
                        {
                            velocity.Y = 0;
                        }
                    }

                    TileRaytraceResult step_result = world.RayTraceTiles(new Vector3f(position).Set(position).Add(size.X * sx, 0, size.Z * sz), new Vector3f(position).Set(position).Add(size.X * sx, StepHeight, size.Z * sz), Tiles.Tile.TileRayTraceType.BLOCK);
                    if (step_result != null && NearGround)
                    {
                        position.Y += StepHeight;
                    }
                }
            }


            if (velocity.Y <= 0)
            {
                Vector3f collision_p1 = new Vector3f(position).Add(size.X * 0.5f, 0.01f, size.Z * 0.5f);
                Vector3f collision_p2 = new Vector3f(position).Add(size.X * 0.5f, velocity.Y - 0.01f, size.Z * 0.5f);

                TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                if (result != null)
                {
                    if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                    {
                        if (!LastOnGround && velocity.Y < 0)
                        {
                            PlayStepSound(soundType, -1);
                            LandOnGround();
                        }
                        position.Y = result.hit.Y;
                        velocity.Y = 0;
                        OnGround = true;
                    }

                }
            }

            BlockAboveHead = false;
            for (float sx = 0; sx < 2; sx++)
            {
                for (float sz = 0; sz < 2; sz++)
                {
                    Vector3f collision_p1 = new Vector3f(position).Add(size.X * sx, size.Y - 0.1f, size.Z * sz);
                    Vector3f collision_p2 = new Vector3f(position).Add(size.X * sx, size.Y + velocity.Y + 0.1f, size.Z * sz);

                    TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                        {
                            if (velocity.Y > 0)
                            {
                                velocity.Y = 0.0f;
                                position.Y = result.hit.Y - size.Y - 0.1f;
                            }
                            BlockAboveHead = true;
                            break;
                        }

                    }

                }
            }

            if (TileManager.GetTile(world.GetVoxel(new TilePos(position.X + 0.5f, position.Y + size.Y, position.Z + 0.5f)).tile_id).BlocksMovement())
            {
                BlockAboveHead = true;
            }

            for (float sz = 0; sz < 2; sz++)
            {
                Vector3f collision_p1 = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * sz);
                Vector3f collision_p2 = new Vector3f(position).Add(size.X * 0.5f, 0, size.Z * sz).Add(velocity.X, 0, 0).Add(size.X * 0.52f * velocity.X / System.Math.Abs(velocity.X), 0, 0);
                for (float h = OnGround ? StepHeight : 0.1f; h < size.Y - 0.2f; h += 0.1f)
                {
                    collision_p1.Y = position.Y + h;
                    collision_p2.Y = position.Y + h;
                    TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                        {
                            velocity.X = 0;
                            break;
                        }

                    }
                }
            }

            for (float sx = 0; sx < 2; sx++)
            {
                Vector3f collision_p1 = new Vector3f(position).Add(size.X * sx, 0, size.Z * 0.5f);
                Vector3f collision_p2 = new Vector3f(position).Add(size.X * sx, 0, size.Z * 0.5f).Add(0, 0, velocity.Z).Add(0, 0, size.Z * 0.52f * velocity.Z / System.Math.Abs(velocity.Z));
                for (float h = OnGround ? StepHeight : 0.1f; h < size.Y - 0.2f; h += 0.1f)
                {
                    collision_p1.Y = position.Y + h;
                    collision_p2.Y = position.Y + h;
                    TileRaytraceResult result = world.RayTraceTiles(collision_p1, collision_p2, Tiles.Tile.TileRayTraceType.BLOCK);
                    if (result != null)
                    {
                        if (TileManager.GetTile(result.data.tile_id).BlocksMovement())
                        {
                            velocity.Z = 0;
                            break;
                        }

                    }
                }
            }
            LastOnGround = OnGround;
        }

        public virtual void PlayStepSound(SoundType soundType, float y_offset = 0)
        {
            TilePos pos = GetTilePos();
            if (y_offset != 0)
            {
                pos = new TilePos(pos.x, (float)System.Math.Round(pos.y + y_offset), pos.z);
            }
            SoundEffect[] sounds = TileManager.GetTile(world.GetVoxel(pos).tile_id).step_sound;
            if (sounds != null)
            {
                SoundEffect effect = sounds[world.random.Next(sounds.Length)];
                GameSound sound = new GameSound(effect.CreateInstance(), soundType);
                sound.Volume = 1.0f;
                sound.Play();
                SoundsToDispose.Add(sound);
            }
        }
    }
}
