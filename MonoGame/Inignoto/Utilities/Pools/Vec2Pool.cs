using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Utilities.Pools
{
    public class Vec2Pool
    {
        private Dictionary<string, Vector2> vertices = new Dictionary<string, Vector2>();

        public Vector2 Get(float x, float y)
        {
            string str = x + "," + y;
            if (!vertices.ContainsKey(str))
            {
                Vector2 o = new Vector2(x, y);
                vertices.Add(str, o);
                return o;
            }
            vertices.TryGetValue(str, out Vector2 v);
            return v;
        }
    }
}
