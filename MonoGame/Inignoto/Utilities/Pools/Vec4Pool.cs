using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Utilities.Pools
{
    public class Vec4Pool
    {
        private Dictionary<string, Vector4> vertices = new Dictionary<string, Vector4>();

        public Vector4 Get(float x, float y, float z, float w)
        {
            string str = x + "," + y + "," + z + "," + w;
            if (!vertices.ContainsKey(str))
            {
                Vector4 o = new Vector4(x, y, z, w);
                vertices.Add(str, o);
                return o;
            }
            vertices.TryGetValue(str, out Vector4 v);
            return v;
        }
    }
}
