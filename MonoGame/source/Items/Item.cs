using Inignoto.Entities;
using Inignoto.Graphics.Mesh;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Items
{
    public class Item
    {
        public string Name { get; private set; }
        public string TranslatedName { get => Name; }
        public readonly int max_stack;

        public readonly double cooldown;
        protected double cooldown_time = 0;

        public Mesh Mesh { get; protected set; }

        public Item(string name, int max_stack = 64, double cooldown = 1.0f)
        {
            Name = name;
            this.max_stack = max_stack;
            this.cooldown = cooldown;
        }
           
        public void TryAttack(Entity user, GameTime time)
        {
            if (time.TotalGameTime.TotalMilliseconds > cooldown_time)
            {
                if (Attack(user, time))
                cooldown_time = time.TotalGameTime.TotalMilliseconds + cooldown * 1000;
            }
            
        }

        public void TryUse(Entity user, GameTime time)
        {
            if (time.TotalGameTime.TotalMilliseconds > cooldown_time)
            {
                if (Use(user, time))
                cooldown_time = time.TotalGameTime.TotalMilliseconds + cooldown * 1000;
            }

        }

        public virtual void TryStopUsing(Entity user, GameTime time)
        {

        }

        protected virtual bool Attack(Entity user, GameTime time)
        {
            return true;
        }

        protected virtual bool Use(Entity user, GameTime time)
        {
            return true;
        }

        public virtual Texture2D GetRenderTexture()
        {
            return null;
        }
    }
}
