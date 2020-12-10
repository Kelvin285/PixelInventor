using Inignoto.Math;
using Inignoto.Tiles.Data;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static Inignoto.Math.Raytracing;
using static Inignoto.Tiles.Tile;
using static Inignoto.World.World;

namespace Inignoto.World.RaytraceResult
{
    public class TileRaytraceResult
    {
        public readonly RayIntersection intersection;
        public readonly TilePos pos;
        public readonly TileData data;
        public readonly Vector3 hit;

        public TileFace Face => GetFace();
        public TileRaytraceResult(RayIntersection intersection, TilePos pos, TileData data, Vector3 hit)
        {
            this.intersection = intersection;
            this.pos = pos;
            this.data = data;
            this.hit = hit;

            if (intersection.normal.X == 0 && intersection.normal.Y == 0 && intersection.normal.Z == 0)
            {
                intersection.normal.Y = 1;
            }
        }

        private TileFace GetFace()
        {
            if (intersection.normal.Y == -1) return TileFace.BOTTOM;
            if (intersection.normal.X == -1) return TileFace.LEFT;
            if (intersection.normal.Z == -1) return TileFace.FRONT;
            if (intersection.normal.X == 1) return TileFace.RIGHT;
            if (intersection.normal.Z == 1) return TileFace.BACK;
            return TileFace.TOP;
        }
    }
}
