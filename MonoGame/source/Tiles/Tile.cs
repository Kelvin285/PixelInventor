using Inignoto.Tiles.Data;
using Inignoto.Math;
using static Inignoto.Math.Raytracing;

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

        public readonly int[] sound;

        public readonly int hits = 1;
        public readonly bool solid = true;

        private TileRayTraceType rayTraceType = TileRayTraceType.BLOCK;
        private bool blocksMovement = true;
        private bool visible = true;
        private bool replaceable = false;

        public Tile(string name, int[] sound, bool solid = true, int hits = 1)
        {
            this.sound = sound;
            this.name = name;
            TileManager.REGISTRY.Add(this.name, this);


            ID = CURRENT_ID++;
            stateHolder = new TileDataHolder(this);
            this.solid = solid;
            this.hits = hits;
        }

        public string TranslatedName => name;

        public RayBox[] GetCollisionBoxes(TileData state)
        {
            RayBox box = new Raytracing.RayBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
            return new RayBox[] { box };
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
