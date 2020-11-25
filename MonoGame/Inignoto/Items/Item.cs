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
        public enum ActionResult
        {
            BLOCK, ENTITY, MISS
        }
        public string Name { get; private set; }
        public string TranslatedName { get => Name; }
        public readonly int max_stack;

        public double CurrentCooldown { get; protected set; }
        public double BlockHitCooldown { get; protected set; }
        public double EntityHitCooldown { get; protected set; }
        public double MissCooldown { get; protected set; }

        public double CooldownTime { get; protected set; }

        public Mesh Mesh;
        public GameModel Model;

        public RenderTarget2D Target { get; protected set; }

        protected ResourcePath model_path, texture_path, anim_path;

        public Vector3 position, rotation, scale;

        public bool canBreakBlocks { get; protected set; }

        public bool Using = false;

        public Item(string name, int max_stack = 64, double cooldown = 1.0f, bool model = true, Vector3 position = new Vector3(), Vector3 rotation = new Vector3(), Vector3 scale = new Vector3())
        {
            Name = name;
            this.max_stack = max_stack;
            BlockHitCooldown = cooldown;
            EntityHitCooldown = cooldown;
            MissCooldown = cooldown;
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
            canBreakBlocks = true;
            if (model)
            {
                SetModel(name);
            }
        }

        public void SetModel(string model)
        {
            Mesh = null;
            string[] split = model.Split(':');
            model_path = new ResourcePath(split[0], "models/item/" + split[1] + ".model", "assets");
            texture_path = new ResourcePath(split[0], "textures/items/" + split[1] + ".png", "assets");
            anim_path = new ResourcePath(split[0], "models/item/" + split[1] + ".anim", "assets");
        }

        public Item breaksBlocks(bool b)
        {
            canBreakBlocks = b;
            return this;
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
           
        public bool TryAttack(Entity user, GameTime time, World.RaytraceResult.TileRaytraceResult result)
        {
            if (time.TotalGameTime.TotalMilliseconds > CooldownTime)
            {
                ActionResult action = Attack(user, time, result);
                if (action == ActionResult.BLOCK)
                {
                    CurrentCooldown = BlockHitCooldown;
                }
                if (action == ActionResult.ENTITY)
                {
                    CurrentCooldown = EntityHitCooldown;
                }
                if (action == ActionResult.MISS)
                {
                    CurrentCooldown = MissCooldown;
                }
                CooldownTime = time.TotalGameTime.TotalMilliseconds + CurrentCooldown * 1000;
                return true;
            }
            return false;
        }

        public bool TryUse(Entity user, GameTime time)
        {

            if (time.TotalGameTime.TotalMilliseconds > CooldownTime)
            {
                Using = true;
                ActionResult action = Use(user, time);

                if (action == ActionResult.BLOCK)
                {
                    CurrentCooldown = BlockHitCooldown;
                }
                if (action == ActionResult.ENTITY)
                {
                    CurrentCooldown = EntityHitCooldown;
                }
                if (action == ActionResult.MISS)
                {
                    CurrentCooldown = MissCooldown;
                }
                CooldownTime = time.TotalGameTime.TotalMilliseconds + CurrentCooldown * 1000;
                return true;
            }
            return false;
        }

        public virtual void TryStopUsing(Entity user, GameTime time)
        {
            if (Using)
            {
                StopUsing(user, time);
                Using = false;
            }
        }

        public virtual void StopUsing(Entity user, GameTime time)
        {

        }

        protected virtual ActionResult Attack(Entity user, GameTime time, World.RaytraceResult.TileRaytraceResult result)
        {
            return ActionResult.MISS;
        }

        protected virtual ActionResult Use(Entity user, GameTime time)
        {
            return ActionResult.MISS;
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
