using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Client;
using Inignoto.Inventory;
using Inignoto.Math;
using Inignoto.World;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;

namespace Inignoto.Entities.Player
{
    public class PlayerEntity : Entity
    {
        public enum Gamemode {
            SURVIVAL, FREECAM, SANDBOX
        }

        public Gamemode gamemode = Gamemode.SURVIVAL;

        public Vector3f SpawnPosition { get; protected set; }

        public bool Flying { get; protected set; }
        public bool Walking { get; protected set; }


        public float UseTimer = 0;
        public float PlaceTimer = 0;

        public PhysicalInventory Inventory { get; protected set; }

        public PlayerEntity(World.World world, Vector3f position) : base(world, position)
        {
            position.Y = world.properties.generator.GetHeight(position.X, position.Z) + 1;
            ReachDistance = 4.0f;
            SpawnPosition = new Vector3f(position);

            Name = "Test Username 1234";

            Name = Name.Substring(0, System.Math.Min(Name.Length, 18));
            Inventory = new PhysicalInventory(this);
        }

        public override void Update(GameTime time)
        {
            base.Update(time);

            size.Y = 1.85f;
            if (Crouching)
            {
                size.Y = 1.75f;
            }
            if (Crawling)
            {
                size.Y = 0.5f;
                StepHeight = 0.1f;
            } else
            {
                StepHeight = 0.55f;
            }
            if (!OnGround)
            {
                Crawling = false;
            }
            else
            {
                if (BlockAboveHead) Crawling = true;
            }

            if (UseTimer > 0) UseTimer--;
            else UseTimer = 0;

            if (PlaceTimer > 0) PlaceTimer--;
            else PlaceTimer = 0;
        }

        public override void DamageEntity(float damage)
        {
            if (gamemode == Gamemode.SURVIVAL)
            {
                base.DamageEntity(damage);
            } else
            {
                damage = 0;
            }

        }

    }

    
}
