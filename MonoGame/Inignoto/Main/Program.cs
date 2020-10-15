using System;
using System.Threading;

namespace Inignoto.Main
{
    /// <summary>
    /// The main class.
    /// </summary>
    public static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            using (var game = new Inignoto())
            {
                Inignoto.game = game;
                game.Run();
            }
        }
    }
}
