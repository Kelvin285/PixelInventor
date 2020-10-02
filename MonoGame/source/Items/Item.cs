using Inignoto.Client;
using Inignoto.Effects;
using Inignoto.Entities;
using Inignoto.Graphics.Mesh;
using Inignoto.Graphics.Models;
using Inignoto.Graphics.Textures;
using Inignoto.Math;
using Inignoto.Utilities;
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
        public GameModel Model { get; protected set; }

        public RenderTarget2D Target { get; protected set; }

        protected ResourcePath model_path, texture_path, anim_path;

        public Vector3 position, rotation, scale;

        public Item(string name, int max_stack = 64, double cooldown = 1.0f, bool model = true, Vector3 position = new Vector3(), Vector3 rotation = new Vector3(), Vector3 scale = new Vector3())
        {
            Name = name;
            this.max_stack = max_stack;
            this.cooldown = cooldown;
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;

            if (model)
            {
                string[] split = name.Split(':');
                model_path = new ResourcePath(split[0], "models/item/" + split[1] + ".model", "assets");
                texture_path = new ResourcePath(split[0], "textures/items/" + split[1] + ".png", "assets");
                anim_path = new ResourcePath(split[0], "models/item/" + split[1] + ".anim", "assets");

            }
        }

        public Item TrySetModel(GameTime time)
        {
            if (model_path != null)
            {
                SetModel(model_path, texture_path, time);
            }
            return this;
        }

        private Item SetModel(ResourcePath model, ResourcePath texture, GameTime time)
        {
            Model = GameModel.LoadModel(model, Textures.LoadTexture(texture));
            Model.timeline = GameModel.LoadAnimation(anim_path);
            Model.editMode = GameModel.EditMode.ANIMATION;
            Model.Play(0);
            Draw(Inignoto.game.GraphicsDevice, GameResources.effect, 1920, 1080, time);
            return this;
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
            return Target;
        }


        private static Camera itemCamera = new Camera();
        public void Draw(GraphicsDevice device, GameEffect effect, int width, int height, GameTime time)
        {
            effect.Radius = 0;
            itemCamera.rotation.Y = 180;

            effect.View = itemCamera.ViewMatrix;

            RasterizerState rasterizerState = new RasterizerState();
            rasterizerState.CullMode = CullMode.None;
            device.RasterizerState = rasterizerState;
            device.DepthStencilState = DepthStencilState.Default;
            device.SamplerStates[0] = SamplerState.PointClamp;

            if (Target == null)
            {
                Target = new RenderTarget2D(device, width, height, false, SurfaceFormat.Color, DepthFormat.Depth16);
            }

            device.SetRenderTarget(Target);
            device.Clear(Color.Transparent);

            device.DepthStencilState = DepthStencilState.Default;

            Model.translation = new Vector3f(position);
            Model.rotation = new Vector3f(rotation);
            Model.scale = new Vector3f(scale);
            Model.Render(device, effect, time, true);

            device.SetRenderTarget(null);

        }
    }
}
