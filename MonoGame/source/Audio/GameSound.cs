using Microsoft.Xna.Framework.Audio;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Audio
{
    
    public class GameSound
    {
        public readonly SoundEffectInstance sound;
        public readonly SoundType soundType;

        private float masterVolume = 1.0f;
        public GameSound(SoundEffectInstance sound, SoundType soundType)
        {
            this.sound = sound;
        }

        public float Volume
        {
            get => sound.Volume / masterVolume;
            set
            {
                masterVolume = GetMasterVolume();
                sound.Volume = value * masterVolume;
            }
        }

        public SoundState State => sound.State;
        public float Pitch { get => sound.Pitch; set => sound.Pitch = value; }
        public bool IsLooped { get => sound.IsLooped; set => sound.IsLooped = value; }

    public void Play()
        {
            sound.Play();
        }

        public void Stop()
        {
            sound.Stop();
        }

        public void Dispose()
        {
            sound.Dispose();
        }

        private float GetMasterVolume()
        {
            switch (soundType)
            {
                case SoundType.PLAYERS:
                    return GameSettings.Settings.PLAYER_VOLUME / 100.0f;
                case SoundType.CREATURES:
                    return GameSettings.Settings.CREATURE_VOLUME / 100.0f;
                case SoundType.AMBIENT:
                    return GameSettings.Settings.AMBIENT_VOLUME / 100.0f;
                case SoundType.ENEMIES:
                    return GameSettings.Settings.ENEMY_VOLUME / 100.0f;
                case SoundType.MUSIC:
                    return GameSettings.Settings.MUSIC_VOLUME / 100.0f;
                case SoundType.BLOCKS:
                    return GameSettings.Settings.BLOCK_VOLUME / 100.0f;
            }
            return 1;
        }
    }
}
