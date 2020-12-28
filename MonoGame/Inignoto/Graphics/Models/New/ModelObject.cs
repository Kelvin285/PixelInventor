using Inignoto.Effects;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Graphics.Models.New
{
    public class ModelObject
    {
        public List<ModelObject> children;
        protected ModelObject parent;

        public Vector3 translation;
        public Quaternion rotation;
        public Vector3 scale;
        public Vector3 origin;

        public ModelObject()
        {
            children = new List<ModelObject>();
            translation = Vector3.Zero;
            rotation = Quaternion.Identity;
            scale = Vector3.One;
            origin = Vector3.Zero;
        }

        public virtual void SetParent(ModelObject newParent)
        {
            if (parent != null)
            {
                parent.children.Remove(this);
            }

            parent = newParent;
            if (parent != null) parent.children.Add(this);
        }

        public virtual void AddChild(ModelObject child)
        {
            if (!children.Contains(child))
            {
                child.SetParent(this);
            }
        }

        public ModelObject GetParent()
        {
            return parent;
        }

        public virtual void Render(GraphicsDevice device, GameEffect effect)
        {
            lock (children)
            {
                for (int i = 0; i < children.Count; i++)
                {
                    children[i].Render(device, effect, Matrix.CreateTranslation(translation));
                }
            }
        }
        public virtual void Render(GraphicsDevice device, GameEffect effect, Matrix lastMatrix)
        {
            lock (children)
            {
                for (int i = 0; i < children.Count; i++)
                {
                    children[i].Render(device, effect, lastMatrix);
                }
            }
        }
    }
}
