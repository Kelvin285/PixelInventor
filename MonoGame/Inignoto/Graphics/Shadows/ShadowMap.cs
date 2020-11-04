using Inignoto.Effects;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace Inignoto.Graphics.Shadows
{
    public class ShadowMap
    {
        public RenderTarget2D shadowMapRenderTarget;
        public GameEffect _ShadowMapGenerate;

        public Matrix view;
        public Matrix projection;
        public Matrix projection2;
        public Matrix projection3;


        public ShadowMap(Vector3 lightPosition, Vector3 _lightDirection)
        {
            //            target = new RenderTarget2D(Inignoto.game.GraphicsDevice, width, height, false, SurfaceFormat.Color, DepthFormat.Depth16);

            shadowMapRenderTarget = new RenderTarget2D(Inignoto.game.GraphicsDevice, 2048, 2048, false, SurfaceFormat.Single, DepthFormat.Depth24, 2, RenderTargetUsage.DiscardContents);
            _ShadowMapGenerate = new GameEffect(Inignoto.game.Content.Load<Effect>("ShadowMapsGenerate"));

            Update(lightPosition, _lightDirection);
        }
        public void Update(Vector3 lightPosition, Vector3 lightDirection)
        {
            Matrix lightView = Matrix.CreateLookAt(lightPosition,
                        lightPosition + lightDirection,
                        new Vector3(0f, 1f, 0f));

            float size = 25;

            Matrix lightProjection = Matrix.CreateOrthographic(size, size, -size, size);
            Matrix lightProjection2 = Matrix.CreateOrthographic(size * 2, size * 2, -size * 2, size * 2);
            Matrix lightProjection3 = Matrix.CreateOrthographic(size * 4, size * 4, -size * 4, size * 4);

            _ShadowMapGenerate.CameraPos = lightPosition;
            _ShadowMapGenerate.View = lightView;
            _ShadowMapGenerate.Projection = lightProjection;
            //_ShadowMapGenerate.Projection2 = lightProjection2;
            //_ShadowMapGenerate.Projection3 = lightProjection3;


            //_ShadowMapGenerate.Projection = Inignoto.game.projectionMatrix;

            view = lightView;
            projection = lightProjection;
            projection2 = lightProjection;
            projection3 = lightProjection;

        }

        public void Begin(Vector3 lightPosition, Vector3 lightDirection)
        {
            Inignoto.game.GraphicsDevice.SetRenderTarget(shadowMapRenderTarget);
            GameResources.drawing_shadows = true;
            Update(lightPosition, lightDirection);
        }

        public void End()
        {
            Inignoto.game.GraphicsDevice.SetRenderTarget(null);
            GameResources.drawing_shadows = false;
        }

        public void Dispose()
        {
            //_ShadowMapGenerate.Dispose();
        }
    }
}
