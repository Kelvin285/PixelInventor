using Inignoto.Effects;
using Inignoto.Math;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.Math.Raytracing;

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

        protected Vector3 renderTranslation;
        protected Quaternion renderRotation;

        public bool editing;
        public bool root = false;
        public bool selected = false;

        public ModelObject()
        {
            editing = false;
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
                    children[i].Render(device, effect, Matrix.CreateTranslation(translation), rotation);
                }
            }
        }

        public virtual RayIntersection GetIntersection(Vector3 position, Vector3 direction)
        {
            
            RayBox box = new RayBox();
            box.Min = renderTranslation - scale / 2.0f;
            box.Max = box.Min + scale / 2.0f;
            RayIntersection intersection = Raytracing.IntersectBox(Inignoto.game.camera.position, direction, box, renderRotation);

            if (!Raytracing.DoesCollisionOccur(Inignoto.game.camera.position, direction, box, Quaternion.Identity))
            {
                intersection.lambda = new Vector2(float.MaxValue);
            }

            return intersection;
        }

        public ModelObject TestForIntersection(Vector3 position, Vector3 direction)
        {
            float distance = float.MaxValue;
            ModelObject closest = null;
            RayIntersection RecursiveIntersect(ModelObject obj)
            {
                RayIntersection intersection = new RayIntersection();
                intersection.lambda = new Vector2(float.MaxValue);
                if (!root)
                {
                    intersection = obj.GetIntersection(position, direction);
                    obj.selected = false;
                    if (intersection.lambda.X < distance)
                    {
                        distance = intersection.lambda.X;
                        closest = obj;
                    }
                }
                for (int i = 0; i < obj.children.Count; i++)
                {
                    RecursiveIntersect(obj.children[i]);
                }
                return intersection;
            }
            RecursiveIntersect(this);
            if (closest != null)
            {
                closest.selected = true;
            }
            return closest;
        }

        public virtual void Render(GraphicsDevice device, GameEffect effect, Matrix lastMatrix, Quaternion lastRotation)
        {
            renderRotation = rotation;
            renderTranslation = translation;
            lock (children)
            {
                for (int i = 0; i < children.Count; i++)
                {
                    children[i].Render(device, effect, lastMatrix, lastRotation);
                }
            }
        }
    }
}
