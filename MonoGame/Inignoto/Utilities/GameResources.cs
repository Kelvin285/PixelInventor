﻿using Inignoto.Graphics.Textures;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.GameSettings;
using Inignoto.Audio;
using Inignoto.Graphics.Gui;
using Inignoto.Items;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Effects;
using Microsoft.Xna.Framework;
using Inignoto.Graphics.Shadows;

namespace Inignoto.Utilities
{
    public class GameResources
    {
        public static GameEffect effect;
        public static bool drawing_shadows = false;
        public static ShadowMap shadowMap;
        public static RenderTarget2D shadowImage;
        public static RenderTarget2D lightImage;
        public static RenderTarget2D gameImage;

        public static Effect postProcessing;


        public static void LoadResources()
        {
            Settings.LoadSettings();
            Textures.LoadTextures();
            TileManager.Loadtiles();
            TileDataHolder.Initialize();
            ItemManager.LoadItems();
            SoundEffects.LoadSoundEffects();

            Inignoto.game.world = new World.World();
            Inignoto.game.player = new Entities.Client.Player.ClientPlayerEntity(Inignoto.game.world, new Math.Vector3f(Inignoto.game.world.radius * 2, 10, Inignoto.game.world.radius));
            Inignoto.game.hud = new Graphics.Gui.Hud();

            effect = new GameEffect(Inignoto.game.Content.Load<Effect>("Effect"));
            effect.Init();

            postProcessing = Inignoto.game.Content.Load<Effect>("PostProcessing");

            shadowMap = new ShadowMap(new Vector3(0, 0, 0), new Vector3(0, -1, 0));

            shadowImage = new RenderTarget2D(Inignoto.game.GraphicsDevice, 2048, 2048, false, SurfaceFormat.Single, DepthFormat.Depth24, 2, RenderTargetUsage.DiscardContents);
            lightImage = new RenderTarget2D(Inignoto.game.GraphicsDevice, 2048, 2048, false, SurfaceFormat.Color, DepthFormat.Depth16);
            gameImage = new RenderTarget2D(Inignoto.game.GraphicsDevice, 2048, 2048, false, SurfaceFormat.Color, DepthFormat.Depth16);

            //new RenderTarget2D(Inignoto.game.GraphicsDevice, width, height, false, SurfaceFormat.Color, DepthFormat.Depth16);
        }

        public static void Dispose()
        {
            Textures.Dispose();
            SoundEffects.Dispose();

            InventoryGui.Dispose();
            //effect.Dispose();
            //shadowMap.Dispose();
        }
    }
}
