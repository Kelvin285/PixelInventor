using Inignoto.Tiles.Data;
using Inignoto.Math;
using static Inignoto.Math.Raytracing;
using Microsoft.Xna.Framework.Audio;
using System;
using Inignoto.Items;
using Inignoto.World.Chunks;
using static Inignoto.World.World;
using Microsoft.Xna.Framework;
using Inignoto.Entities;

namespace Inignoto.Tiles
{
    public class Tile
    {
        public enum TileRayTraceType // Used for raytraced collisions (see World.cs)
        {
            BLOCK, FLUID, GAS
        };

        public enum TileFace
        {
            TOP, BOTTOM, LEFT, RIGHT, FRONT, BACK
        };

        public readonly string name; // The untranslated name for the block

        public static int CURRENT_ID = 0; // Block IDs (used for save data)
        public readonly int ID;

        public readonly TileDataHolder stateHolder; // Used to hold the different tile states

        public readonly SoundEffect[] step_sound;

        public readonly int hits = 1; // How many hits it takes to break the blocks
        public bool solid = true; // Whether or not entities can fall/walk through the block

        public bool tinted { get; private set; }

        private TileRayTraceType rayTraceType = TileRayTraceType.BLOCK;
        private bool visible = true; // Is the block visible? (Can it be rendered?)
        private bool replaceable = false; // Can this block be replaced when building?
        public bool FullSpace { get; protected set; } // Does the block take up a full space?  (Is it a full cube?)

        private string item_model = string.Empty; // This is set if the block has a custom item model

        private bool Opaque = true; // Is this block rendered as opaque or transparent?  (true = opaque, false = transparent)

        private bool DropsAsItem = true; // Can the block be dropped as an item?

        public bool Overlay { get; private set; } // Is the block used as an overlay texture?

        // These next few values represent block light emission
        public int light_red { get; private set; }
        public int light_green { get; private set; }
        public int light_blue { get; private set; }

        public bool glowing { get; private set; }

        public bool allows_red_light { get; private set; }
        public bool allows_green_light { get; private set; }
        public bool allows_blue_light { get; private set; }
        public bool allows_sunlight { get; private set; }

        // This is used for tinted blocks (which light values are blocked and which ones are allowed?)
        public int RedTint { get; private set; } // Blocks red light
        public int GreenTint { get; private set; } // Blocks green light
        public int BlueTint { get; private set; } // Blocks blue light
        public int SunTint { get; private set; } // Blocks sunlight

        public Tile(string name, SoundEffect[] sound, bool solid = true, int hits = 1)
        {
            SetLight(0, 0, 0); // Light is set to 0 by default
            step_sound = sound;
            this.name = name;


            ID = CURRENT_ID++;
            TileRegistry.REGISTRY.Add(this.name, this);
            TileRegistry.ID_REGISTRY.Add(ID, this);

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
            RayBox box = new RayBox(new Vector3(0, 0, 0), new Vector3(1, 1, 1));
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
            return FullSpace;
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

        public bool IsNotFullBlockOrTransparentAndDoesNotEqual(Tile initial)
        {
            return !FullSpace || rayTraceType != TileRayTraceType.BLOCK || (!Opaque && this != initial);
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
                if (chunk.GetOverlayVoxel(x, y, z) != TileRegistry.AIR.DefaultData || !TileRegistry.GetTile(chunk.GetVoxel(x, y, z).tile_id).FullSpace)
                return false;
            }
            return true;
        }

        public virtual TileData GetStateForBlockPlacement(int x, int y, int z, Chunk chunk, TileFace face, Entity placer = null)
        {
            return DefaultData;
        }

        public bool BlocksMovement()
        {
            return solid;
        }

        public Tile SetBlocksMovement(bool blocksMovement)
        {
            this.solid = blocksMovement;
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
