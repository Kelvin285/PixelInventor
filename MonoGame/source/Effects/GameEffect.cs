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

        public float FogDistance { get => GetFloat("fog_distance"); set => SetFloat("fog_distance", value); }
        public Vector4 FogColor { get => GetFloat4("fog_color"); set => SetFloat4("fog_color", value); }
        public float Time { get => GetFloat("time"); set => SetFloat("time", value); }
        public bool Water { get => GetBool("water"); set => SetBool("water", value); }

        public GameEffect(GraphicsDevice graphicsDevice, byte[] effectCode) : base(graphicsDevice, effectCode)
        {
            Init();
        }

        public GameEffect(GraphicsDevice graphicsDevice, byte[] effectCode, int index, int count) : base(graphicsDevice, effectCode, index, count)
        {
            Init();
        }

        public GameEffect(Effect cloneSource) : base(cloneSource)
        {
            Init();
        }

        private void Init()
        {
            FogDistance = 500.0f;
            FogColor = new Vector4(Color.CornflowerBlue.R / 255.0f, Color.CornflowerBlue.G / 255.0f, Color.CornflowerBlue.B / 255.0f, 1.0f);
        }

        public Matrix GetMatrix(string name)
        {
            return Parameters[name].GetValueMatrix();
        }

        public void SetMatrix(string name, Matrix matrix)
        {
            Parameters[name].SetValue(matrix);
        }

        public float GetFloat(string name)
        {
            return Parameters[name].GetValueSingle();
        }

        public void SetFloat(string name, float value)
        {
            Parameters[name].SetValue(value);
        }

        public bool GetBool(string name)
        {
            return Parameters[name].GetValueBoolean();
        }

        public void SetBool(string name, bool value)
        {
            Parameters[name].SetValue(value);
        }

        public Vector4 GetFloat4(string name)
        {
            return Parameters[name].GetValueVector4();
        }

        public void SetFloat4(string name, Vector4 value)
        {
            Parameters[name].SetValue(value);
        }
    }
}
