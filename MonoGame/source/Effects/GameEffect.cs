using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Effects
{
    public class GameEffect : Effect
    {
        public Matrix World { get => GetMatrix("World"); set => SetMatrix("World", value); }
        public Matrix View { get => GetMatrix("View"); set => SetMatrix("View", value); }
        public Matrix Projection { get => GetMatrix("Projection"); set => SetMatrix("Projection", value); }

        public GameEffect(GraphicsDevice graphicsDevice, byte[] effectCode) : base(graphicsDevice, effectCode)
        {
        }

        public GameEffect(GraphicsDevice graphicsDevice, byte[] effectCode, int index, int count) : base(graphicsDevice, effectCode, index, count)
        {
        }

        public GameEffect(Effect cloneSource) : base(cloneSource)
        {
        }

        public Matrix GetMatrix(string name)
        {
            return Parameters[name].GetValueMatrix();
        }

        public void SetMatrix(string name, Matrix matrix)
        {
            Parameters[name].SetValue(matrix);
        }
    }
}
