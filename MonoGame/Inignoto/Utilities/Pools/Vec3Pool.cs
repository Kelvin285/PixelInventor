using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Utilities.Pools
{
    public class Vec3Pool
    {
        private Dictionary<string, Vector3> vertices = new Dictionary<string, Vector3>();

        public Vector3 Get(float x, float y, float z)
        {
            string str = x + "," + y + "," + z;
            if (!vertices.ContainsKey(str))
            {
                Vector3 o = new Vector3(x, y, z);
                vertices.Add(str, o);
                return o;
            }
            vertices.TryGetValue(str, out Vector3 v);
            return v;
        }
    }
}
