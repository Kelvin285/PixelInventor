using Inignoto.Graphics.Textures;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.GameSettings;
using Inignoto.Audio;
using Inignoto.Graphics.Gui;
using Inignoto.Items;
using Microsoft.Xna.Framework.Graphics;
using Inignoto.Effects;

namespace Inignoto.Utilities
{
    public class GameResources
    {
        public static GameEffect effect;

        public static void LoadResources()
        {
            Settings.LoadSettings();
            Textures.LoadTextures();
            TileManager.Loadtiles();
            TileDataHolder.Initialize();
            ItemManager.LoadItems();
            SoundEffects.LoadSoundEffects();

            Inignoto.game.world = new World.World();
            Inignoto.game.player = new Entities.Client.Player.ClientPlayerEntity(Inignoto.game.world, new Math.Vector3f(0, 10, 0));
            Inignoto.game.hud = new Graphics.Gui.Hud();

            effect = new GameEffect(Inignoto.game.Content.Load<Effect>("Effect"));
        }

        public static void Dispose()
        {
            Textures.Dispose();
            SoundEffects.Dispose();

            InventoryGui.Dispose();
        }
    }
}
