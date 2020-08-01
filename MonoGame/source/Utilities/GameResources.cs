using Inignoto.Graphics.Textures;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.GameSettings;
using Inignoto.Audio;

namespace Inignoto.Utilities
{
    public class GameResources
    {
        public static void LoadResources()
        {
            Settings.LoadSettings();
            Textures.LoadTextures();
            Tiles.TileManager.Loadtiles();
            TileDataHolder.Initialize();
            SoundEffects.LoadSoundEffects();

            Inignoto.game.world = new World.World();
            Inignoto.game.player = new Entities.Client.Player.ClientPlayerEntity(Inignoto.game.world, new Math.Vector3f(0, 10, 0));
            Inignoto.game.guiScreen = new Graphics.Gui.GuiScreen();
        }

        public static void Dispose()
        {
            Textures.Dispose();
            SoundEffects.Dispose();
        }
    }
}
