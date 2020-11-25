using Inignoto.Tiles.Data;
using Inignoto.Math;
using static Inignoto.Math.Raytracing;
using Microsoft.Xna.Framework.Audio;
using System;
using Inignoto.Items;
using Inignoto.World.Chunks;
using static Inignoto.World.World;

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

        public bool tinted { get; private set; }

        private TileRayTraceType rayTraceType = TileRayTraceType.BLOCK;
        private bool blocksMovement = true;
        private bool visible = true;
        private bool replaceable = false;
        public bool FullSpace { get; protected set; }

        private string item_model = string.Empty;

        private bool Opaque = true;

        private bool DropsAsItem = true;

        public bool Overlay { get; private set; }

        public int light_red { get; private set; }
        public int light_green { get; private set; }
        public int light_blue { get; private set; }

        public bool glowing { get; private set; }

        public bool allows_red_light { get; private set; }
        public bool allows_green_light { get; private set; }
        public bool allows_blue_light { get; private set; }
        public bool allows_sunlight { get; private set; }

        public int RedTint { get; private set; }
        public int GreenTint { get; private set; }
        public int BlueTint { get; private set; }
        public int SunTint { get; private set; }

        public Tile(string name, SoundEffect[] sound, bool solid = true, int hits = 1)
        {
            SetLight(0, 0, 0);
            step_sound = sound;
            this.name = name;
            TileManager.REGISTRY.Add(this.name, this);


            ID = CURRENT_ID++;
            stateHolder = new TileDataHolder(this);
            this.solid = solid;
            this.hits = hits;
            BlockLight(true, true, true, true);
        }

        public string TranslatedName => name;

        public Tile SetLight(int R, int G, int B)
        {
            allows_red_light = R > 0;
            allows_green_light = G > 0;
            allows_blue_light = B > 0;
            light_red = R;
            light_green = G;
            light_blue = B;

            glowing = R > 0 || G > 0 || B > 0;
            return this;
        }

        public Tile SetOverlay()
        {
            Overlay = true;
            return this;
        }

        public Tile SetItemModel(string model)
        {
            item_model = model;
            return this;
        }

        public string GetItemModel()
        {
            return item_model;
        }

        public Tile SetCanDrop(bool drop)
        {
            DropsAsItem = drop;
            return this;
        }

        public bool CanDropAsItem()
        {
            return DropsAsItem;
        }

        public RayBox[] GetCollisionBoxes(TileData state)
        {
            RayBox box = new Raytracing.RayBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
            return new RayBox[] { box };
        }

        public Tile BlockLight(bool red, bool green, bool blue, bool sun)
        {
            allows_red_light = !red;
            allows_green_light = !green;
            allows_blue_light = !blue;
            allows_sunlight = !sun;
            return this;
        }

        public Tile SetTint(int redlight, int greenlight, int bluelight, int sunlight)
        {
            BlockLight(redlight == 0, greenlight == 0, bluelight == 0, sunlight == 0);
            RedTint = redlight;
            GreenTint = greenlight;
            BlueTint = bluelight;
            SunTint = sunlight;
            tinted = true;
            return this;
        }

        public Tile SetTransparent()
        {
            BlockLight(false, false, false, false);
            Opaque = false;
            return this;
        }

        public bool IsOpaque()
        {
            return Opaque;
        }

        public bool IsOpaqueAndNotGlowing()
        {
            return Opaque && !glowing;
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
            return (Opaque && FullSpace) || rayTraceType != TileRayTraceType.BLOCK;
        }

        public bool IsRaytraceTypeOrSolid(TileRayTraceType rayTraceType)
        {
            return IsRaytraceType(rayTraceType) || solid;
        }

        public bool IsRaytraceTypeOrSolidAndOpaque(TileRayTraceType rayTraceType)
        {
            return (Opaque && FullSpace) && (IsRaytraceTypeOrSolid(rayTraceType));
        }

        public virtual bool CanPlace(int x, int y, int z, Chunk chunk)
        {
            if (Overlay)
            {
                if (chunk.GetOverlayVoxel(x, y, z) != TileManager.AIR.DefaultData || !TileManager.GetTile(chunk.GetVoxel(x, y, z).tile_id).FullSpace)
                return false;
            }
            return true;
        }

        public virtual TileData GetStateForBlockPlacement(int x, int y, int z, Chunk chunk, TileFace face)
        {
            return DefaultData;
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
