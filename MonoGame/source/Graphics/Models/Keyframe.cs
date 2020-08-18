using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Graphics.Models
{
    public class Keyframe
    {
        public Dictionary<string, KeyTransformation> transformations = new Dictionary<string, KeyTransformation>();
        public int time = 0;
        public double speed = 1.0f;
        public Keyframe(int time)
        {
            this.time = time;
        }

        public Keyframe Copy()
        {
            Keyframe k = new Keyframe(time);
            k.speed = speed;
            foreach (String part in transformations.Keys)
            {
                KeyTransformation kt = null;
                transformations.TryGetValue(part, out kt);
                k.transformations.Add(part, kt.Copy());
            }
            return k;
        }

        public void Paste(Keyframe k)
        {
            speed = k.speed;
            transformations.Clear();
            foreach (String part in k.transformations.Keys)
            {
                KeyTransformation kt = null;
                transformations.TryGetValue(part, out kt);
                transformations.Add(part, kt.Copy());
            }
        }
    }
}
