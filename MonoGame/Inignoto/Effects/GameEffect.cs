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
        public Matrix Projection2 { get => GetMatrix("Projection2"); set => SetMatrix("Projection2", value); }
        public Matrix Projection3 { get => GetMatrix("Projection3"); set => SetMatrix("Projection3", value); }

        public Matrix ShadowView { get => GetMatrix("ShadowView"); set => SetMatrix("ShadowView", value); }

        public Matrix ShadowProjection { get => GetMatrix("ShadowProjection"); set => SetMatrix("ShadowProjection", value); }
        public Matrix ShadowProjection2 { get => GetMatrix("ShadowProjection2"); set => SetMatrix("ShadowProjection2", value); }
        public Matrix ShadowProjection3 { get => GetMatrix("ShadowProjection3"); set => SetMatrix("ShadowProjection3", value); }


        public float FogDistance { get => GetFloat("fog_distance"); set => SetFloat("fog_distance", value); }
        public Vector4 FogColor { get => GetFloat4("fog_color"); set => SetFloat4("fog_color", value); }
        public float Time { get => GetFloat("time"); set => SetFloat("time", value); }
        public bool Water { get => GetBool("water"); set => SetBool("water", value); }
        public bool HasShadows { get => GetBool("has_shadows"); set => SetBool("has_shadows", value); }

        public Vector4 ObjectColor { get => GetFloat4("color"); set => SetFloat4("color", value); }
        public Vector4 ObjectLight { get => GetFloat4("ObjectLight"); set => SetFloat4("ObjectLight", value); }

        public float Radius { get => GetFloat("radius"); set => SetFloat("radius", value); }
        public float Area { get => GetFloat("area"); set => SetFloat("area", value); }
        public bool WorldRender { get => GetBool("world_render"); set => SetBool("world_render", value); }

        public Vector3 CameraPos { get => GetFloat3("camera_pos"); set => SetFloat3("camera_pos", value); }
        public Vector3 SunLook { get => GetFloat3("sunLook"); set => SetFloat3("sunLook", value); }

        public GameEffect(GraphicsDevice graphicsDevice, byte[] effectCode) : base(graphicsDevice, effectCode)
        {
        }

        public GameEffect(GraphicsDevice graphicsDevice, byte[] effectCode, int index, int count) : base(graphicsDevice, effectCode, index, count)
        {
        }

        public GameEffect(Effect cloneSource) : base(cloneSource)
        {
        }

        public void Init()
        {
            ObjectColor = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
            ObjectLight = new Vector4(-1, -1, -1, -1);
            FogDistance = 500.0f;
            FogColor = new Vector4(Color.CornflowerBlue.R / 255.0f, Color.CornflowerBlue.G / 255.0f, Color.CornflowerBlue.B / 255.0f, 1.0f);

            //FogDistance = 25.0f;
            //FogColor = new Vector4(1.0f, 0.0f, 0.0f, 1.0f);
        }

        public Matrix GetMatrix(string name)
        {
            if (Parameters[name] == null) return new Matrix();
            return Parameters[name].GetValueMatrix();
        }

        public void SetMatrix(string name, Matrix matrix)
        {
            if (Parameters[name] == null) return;
            Parameters[name].SetValue(matrix);
        }

        public float GetFloat(string name)
        {
            if (Parameters[name] == null) return 0;
            return Parameters[name].GetValueSingle();
        }

        public void SetFloat(string name, float value)
        {
            if (Parameters[name] == null) return;
            Parameters[name].SetValue(value);
        }

        public bool GetBool(string name)
        {
            if (Parameters[name] == null) return false;
            return Parameters[name].GetValueBoolean();
        }

        public void SetBool(string name, bool value)
        {
            if (Parameters[name] == null) return;
            Parameters[name].SetValue(value);
        }

        public Vector4 GetFloat4(string name)
        {
            if (Parameters[name] == null) return new Vector4();
            return Parameters[name].GetValueVector4();
        }

        public void SetFloat4(string name, Vector4 value)
        {
            if (Parameters[name] == null) return;
            Parameters[name].SetValue(value);
        }

        public Vector3 GetFloat3(string name)
        {
            if (Parameters[name] == null) return new Vector3();
            return Parameters[name].GetValueVector3();
        }

        public void SetFloat3(string name, Vector3 value)
        {
            if (Parameters[name] == null) return;
            Parameters[name].SetValue(value);
        }
    }
}
