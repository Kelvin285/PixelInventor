using Inignoto.Math;
using Inignoto.World;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Inignoto.Entities
{
    public class Entity
    {
        public Vector3f position;
        public readonly World.World world;

        public Vector3f velocity;
        public Vector3f size;
        
        public bool OnGround { get; protected set; }

        public bool Running { get; protected set; }
        public bool Crouching { get; protected set; }
        public bool Crawling { get; protected set; }

        public float StepHeight { get; protected set; }

        public bool NearGround { get; protected set; }

        public bool Jumping { get; protected set; }

        public int TicksExisted { get; protected set; }

        public float FallStart { get; protected set; }

        public float moveSpeed = 0.1f;

        public float health = 100.0f;
        public float hunger = 100.0f;
        public float stamina = 100.0f;
        public float defense = 0.0f;


        public Entity(World.World world, Vector3f position)
        {
            this.world = world;
            this.position = position;
            size = new Vector3f(0.5f, 1.8f, 0.5f);
            velocity = new Vector3f(0, 0, 0);
            
            world.entities.Add(this);

            StepHeight = 0.5f;
        }

        public virtual void Update(GameTime time)
        {
            DoPhysicsUpdates(time);
            if (OnGround)
            {
                Jumping = false;
                LandOnGround();
                FallStart = position.Y;
            }
            TicksExisted++;
        }

        public void DoPhysicsUpdates(GameTime time)
        {
            
        }

        public virtual void Render(GraphicsDevice device, BasicEffect effect)
        {

        }
        
        public virtual float GetEyeHeight()
        {
            return size.Y * 0.85f;
        }

        public virtual float GetMovementSpeed()
        {
            if (Crawling) return 0.025f;
            if (Crouching) return 0.035f;
            if (Running) return 0.12f;
            return 0.065f;
        }

        public virtual Vector3f GetEyePosition()
        {
            return new Vector3f(position).Add(size.X * 0.5f, GetEyeHeight(), size.Z * 0.5f);
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
    }
}
