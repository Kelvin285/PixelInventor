using Inignoto.Utilities;
using Microsoft.Xna.Framework.Audio;
using System.Collections.Generic;
using System.IO;

namespace Inignoto.Audio
{

    public class SoundEffects
    {
        private static readonly List<SoundEffect> soundEffects = new List<SoundEffect>();

        public static SoundEffect player_hit;
        public static SoundEffect player_heartbeat;
        public static SoundEffect player_death_sound;

        public static SoundEffect ambient_wind;
        public static SoundEffect ambient_fabric_in_wind;

        public static SoundEffect[] step_grass = new SoundEffect[5];
        public static SoundEffect[] step_soil = new SoundEffect[5];

        public static SoundEffect inventory_open;
        public static SoundEffect inventory_close;

        public static void LoadSoundEffects()
        {
            player_hit = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/player/hit.wav", "assets"));
            player_heartbeat = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/player/heartbeat.wav", "assets"));
            player_death_sound = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/player/noise.wav", "assets"));

            ambient_wind = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/ambient/wind.wav", "assets"));
            ambient_fabric_in_wind = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/ambient/fabric_in_wind.wav", "assets"));

            inventory_open = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/inventory/open.wav", "assets"));
            inventory_close = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/inventory/close.wav", "assets"));

            for (int i = 0; i < step_grass.Length; i++)
            {
                int I = i + 1;
                step_grass[i] = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/footsteps/grass_step"+I+".wav", "assets"));
            }

            for (int i = 0; i < step_soil.Length; i++)
            {
                int I = i + 1;
                step_soil[i] = LoadSoundEffect(new ResourcePath("Inignoto", "sounds/footsteps/soil_step" + I + ".wav", "assets"));
            }


        }

        public static SoundEffect LoadSoundEffect(ResourcePath path)
        {
            FileStream stream = FileUtils.GetStreamForPath(path, FileMode.Open);
            SoundEffect sound = SoundEffect.FromStream(stream);
            stream.Close();
            soundEffects.Add(sound);
            return sound;
        }

        public static void Dispose()
        {
            foreach (SoundEffect effect in soundEffects)
            {
                effect.Dispose();
            }
        }
    }
}
