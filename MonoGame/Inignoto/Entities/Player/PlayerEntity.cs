using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Inignoto.Client;
using Inignoto.Inventory;
using Inignoto.Math;
using Inignoto.Utilities;
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

        public Gamemode gamemode = Gamemode.SANDBOX;

        public Vector3f SpawnPosition { get; protected set; }

        public bool Flying { get; protected set; }
        public bool Walking { get; protected set; }


        public float UseTimer = 0;
        public float PlaceTimer = 0;

        public long UID = 0;

        public PhysicalInventory Inventory { get; protected set; }

        public PlayerEntity(World.World world, Vector3f position, long UID) : base(world, position)
        {
            gamemode = world.properties.default_gamemode;
            position.Y = world.properties.generator.GetHeight(position.X, position.Z, world.radius, world.properties.infinite) + 1;
            ReachDistance = 4.0f;
            SpawnPosition = new Vector3f(position);

            this.UID = UID;

            Name = "TestUsername";

            Name = Name.Substring(0, System.Math.Min(Name.Length, 18));
            Inventory = new PhysicalInventory(this);

            soundType = Audio.SoundType.PLAYERS;

            Load();
        }

        public override void Update(GameTime time)
        {
            base.Update(time);

            if (gamemode == Gamemode.SURVIVAL) Flying = false;

            ReachDistance = gamemode != Gamemode.SANDBOX ? 4.5f : 8.0f;

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

        public void DamageEntity(float damage, bool ignore_sandbox)
        {
            if (gamemode == Gamemode.SURVIVAL || ignore_sandbox)
            {
                base.DamageEntity(damage);
            } else
            {
                damage = 0;
            }

        }

        public override void DamageEntity(float damage)
        {
            DamageEntity(damage, false);
        }

        public override void Save()
        {
            ResourcePath directory = new ResourcePath("Players", "", "Worlds/" + world.name);
            ResourcePath file = new ResourcePath("Players", "player" + UID + ".player", "Worlds/" + world.name);
            if (!Directory.Exists(FileUtils.GetResourcePath(directory)))
            {
                Directory.CreateDirectory(FileUtils.GetResourcePath(directory));
            }
            if (File.Exists(FileUtils.GetResourcePath(file)))
            {
                File.Delete(FileUtils.GetResourcePath(file));
            }
            string str = "";
            str += UID + "\n";
            str += position.X + "\n";
            str += position.Y + "\n";
            str += position.Z + "\n";
            str += health + "\n";
            str += hunger + "\n";
            str += look.X + "\n";
            str += look.Y + "\n";
            str += look.Z + "\n";
            str += stamina + "\n";
            str += (int)gamemode + "\n";
            str += "INVENTORY\n";
            str += Inventory.Save();
            File.WriteAllText(FileUtils.GetResourcePath(file), str);
        }

        public override void Load()
        {
            ResourcePath directory = new ResourcePath("Players", "", "Worlds/" + world.name);
            ResourcePath file = new ResourcePath("Players", "player"+UID+".player", "Worlds/" + world.name);
            if (!Directory.Exists(FileUtils.GetResourcePath(directory)))
            {
                return;
            }
            if (!File.Exists(FileUtils.GetResourcePath(file)))
            {
                return;
            }
            string str = File.ReadAllText(FileUtils.GetResourcePath(file));
            string[] split = str.Split("INVENTORY");
            Inventory.Load(split[1]);
            string[] data = split[0].Split("\n");
            UID = long.Parse(data[0]);
            position.X = float.Parse(data[1]);
            position.Y = float.Parse(data[2]);
            position.Z = float.Parse(data[3]);
            health = float.Parse(data[4]);
            hunger = float.Parse(data[5]);
            look.X = float.Parse(data[6]);
            look.Y = float.Parse(data[7]);
            look.Z = float.Parse(data[8]);
            stamina = float.Parse(data[9]);
            gamemode = (Gamemode)int.Parse(data[10]);
        }

    }

    
}
