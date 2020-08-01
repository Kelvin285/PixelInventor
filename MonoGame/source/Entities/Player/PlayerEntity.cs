using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Client;
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

        public float ReachDistance { get; protected set; }

        public Vector3f SpawnPosition { get; protected set; }

        public bool Flying { get; protected set; }
        public bool Walking { get; protected set; }


        public float UseTimer = 0;
        public float PlaceTimer = 0;



        public PlayerEntity(World.World world, Vector3f position) : base(world, position)
        {
            position.Y = world.properties.generator.GetHeight(position.X, position.Z) + 1;
            ReachDistance = 4.0f;
            SpawnPosition = new Vector3f(position);
        }

        public override void Update(GameTime time)
        {
            base.Update(time);

            size.Y = 1.85f;
            if (Crouching)
            {
                size.Y = 1.75f;
            }
            else if (Crawling)
            {
                size.Y = 0.5f;
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
