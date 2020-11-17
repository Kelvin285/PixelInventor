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
        public RenderTarget2D[] shadowMapRenderTarget = new RenderTarget2D[3];
        public GameEffect _ShadowMapGenerate;

        public Matrix view;
        public Matrix projection;
        public Matrix projection2;
        public Matrix projection3;


        public ShadowMap(Vector3 lightPosition, Vector3 _lightDirection)
        {
            //            target = new RenderTarget2D(Inignoto.game.GraphicsDevice, width, height, false, SurfaceFormat.Color, DepthFormat.Depth16);

            shadowMapRenderTarget[0] = new RenderTarget2D(Inignoto.game.GraphicsDevice, 2048, 2048, false, SurfaceFormat.Single, DepthFormat.Depth24, 2, RenderTargetUsage.DiscardContents);
            shadowMapRenderTarget[1] = new RenderTarget2D(Inignoto.game.GraphicsDevice, 2048 * 2, 2048 * 2, false, SurfaceFormat.Single, DepthFormat.Depth24, 2, RenderTargetUsage.DiscardContents);
            shadowMapRenderTarget[2] = new RenderTarget2D(Inignoto.game.GraphicsDevice, 2048 * 4, 2048 * 4, false, SurfaceFormat.Single, DepthFormat.Depth24, 2, RenderTargetUsage.DiscardContents);

            _ShadowMapGenerate = new GameEffect(Inignoto.game.Content.Load<Effect>("ShadowMapsGenerate"));

            Update(lightPosition, _lightDirection, 0);
        }
        public void Update(Vector3 lightPosition, Vector3 lightDirection, int map)
        {
            Matrix lightView = Matrix.CreateLookAt(lightPosition,
                        lightPosition + lightDirection,
                        new Vector3(0f, 1f, 0f));

            float size = 25;
            float size2 = 100;
            float size3 = 300;
            Matrix lightProjection = Matrix.CreateOrthographic(size, size, -size, size);
            Matrix lightProjection2 = Matrix.CreateOrthographic(size2, size2, -size2, size2);
            Matrix lightProjection3 = Matrix.CreateOrthographic(size3, size3, -size3, size3);

            _ShadowMapGenerate.CameraPos = lightPosition;
            _ShadowMapGenerate.View = lightView;
            if (map == 0) _ShadowMapGenerate.Projection = lightProjection;
            if (map == 1) _ShadowMapGenerate.Projection = lightProjection2;
            if (map == 2) _ShadowMapGenerate.Projection = lightProjection3;
            //_ShadowMapGenerate.Projection = Inignoto.game.projectionMatrix;

            view = lightView;
            projection = lightProjection;
            projection2 = lightProjection2;
            projection3 = lightProjection3;

        }

        public void Begin(Vector3 lightPosition, Vector3 lightDirection, int map)
        {
            Inignoto.game.GraphicsDevice.SetRenderTarget(shadowMapRenderTarget[map]);
            GameResources.drawing_shadows = true;
            Update(lightPosition, lightDirection, map);
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
