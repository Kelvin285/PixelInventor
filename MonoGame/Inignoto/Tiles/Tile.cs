using Inignoto.Tiles.Data;
using Inignoto.Math;
using static Inignoto.Math.Raytracing;
using Microsoft.Xna.Framework.Audio;
using System;

namespace Inignoto.Tiles
{
    public class Tile
    {
        public enum TileRayTraceType
        {
            BLOCK, FLUID, GAS
        };

        public enum TileFace
        {
            TOP, BOTTOM, LEFT, RIGHT, FRONT, BACK
        };

        public readonly string name;

        private static int CURRENT_ID = 0;
        public readonly int ID;

        public readonly TileDataHolder stateHolder;

        public readonly SoundEffect[] step_sound;

        public readonly int hits = 1;
        public readonly bool solid = true;

        private TileRayTraceType rayTraceType = TileRayTraceType.BLOCK;
        private bool blocksMovement = true;
        private bool visible = true;
        private bool replaceable = false;
        private bool FullSpace;

        private bool Opaque = true;

        public int light_red { get; private set; }
        public int light_green { get; private set; }
        public int light_blue { get; private set; }

        public Tile(string name, SoundEffect[] sound, bool solid = true, int hits = 1)
        {
            SetLight(0, 0, 0);
            this.step_sound = sound;
            this.name = name;
            TileManager.REGISTRY.Add(this.name, this);


            ID = CURRENT_ID++;
            stateHolder = new TileDataHolder(this);
            this.solid = solid;
            this.hits = hits;
        }

        public string TranslatedName => name;

        public Tile SetLight(int R, int G, int B)
        {
            light_red = R;
            light_green = G;
            light_blue = B;
            return this;
        }

        public RayBox[] GetCollisionBoxes(TileData state)
        {
            RayBox box = new Raytracing.RayBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
            return new RayBox[] { box };
        }

        public Tile SetTransparent()
        {
            Opaque = false;
            return this;
        }

        public bool IsOpaque()
        {
            return Opaque;
        }
        public Tile SetFull()
        {
            FullSpace = true;
            return this;
        }

        public bool TakesUpEntireSpace()
        {
            return this.FullSpace;
        }

        public bool IsVisible()
        {
            return visible;
        }

        public Tile SetVisible(bool visible)
        {
            this.visible = visible;
            return this;
        }

        public bool IsRaytraceType(TileRayTraceType rayTraceType)
        {
            return GetRayTraceType() == rayTraceType;
        }

        public bool IsOpaqueOrNotBlock()
        {
            return Opaque || rayTraceType != TileRayTraceType.BLOCK;
        }

        public bool IsRaytraceTypeOrSolid(TileRayTraceType rayTraceType)
        {
            return IsRaytraceType(rayTraceType) || solid;
        }

        public bool IsRaytraceTypeOrSolidAndOpaque(TileRayTraceType rayTraceType)
        {
            return Opaque && (IsRaytraceTypeOrSolid(rayTraceType));
        }

        public bool BlocksMovement()
        {
            return blocksMovement;
        }

        public Tile SetBlocksMovement(bool blocksMovement)
        {
            this.blocksMovement = blocksMovement;
            return this;
        }

        public TileRayTraceType GetRayTraceType()
        {
            return rayTraceType;
        }

        public Tile SetRayTraceType(TileRayTraceType type)
        {
            rayTraceType = type;
            return this;
        }

        public Tile SetReplaceable(bool replaceable = true)
        {
            this.replaceable = true;
            return this;
        }

        public bool IsReplaceable()
        {
            return replaceable;
        }

        public TileData DefaultData => stateHolder.data[0];

    }

}
