using Inignoto.Graphics.Textures;
using Inignoto.Tiles;
using Inignoto.Tiles.Data;
using Inignoto.GameSettings;
namespace Inignoto.Utilities
{
    class GameResources
    {
        public static void LoadResources()
        {
            Settings.LoadSettings();
            Textures.LoadTextures();
            Tiles.TileManager.Loadtiles();
            TileDataHolder.Initialize();
        }

        public static void Dispose()
        {
            
        }
    }
}
