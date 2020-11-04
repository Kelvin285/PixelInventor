using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Runtime.InteropServices;

namespace Inignoto.Graphics.Mesh
{
    [StructLayout(LayoutKind.Sequential, Pack = 1)]
    public struct VertexPositionLightTexture : IVertexType
    {
        public Vector3 Position;
        public Color Color;
        public Vector2 TextureCoordinate;
        public float Normal;
        public static readonly VertexDeclaration VertexDeclaration;

        public VertexPositionLightTexture(Vector3 position, Color color, Vector2 textureCoordinate, int normal, int light = 0, int sunlight = 15)
        {
            Position = position;
            int light_r = light & 0b1111;
            int light_g = (light >> 4) & 0b1111;
            int light_b = (light >> 8) & 0b1111;

            Color = new Color(light_r / 15.0f, light_g / 15.0f, light_b / 15.0f, sunlight / 15.0f);

            TextureCoordinate = textureCoordinate;
            Normal = normal;
        }

        VertexDeclaration IVertexType.VertexDeclaration
        {
            get
            {
                return VertexDeclaration;
            }
        }

        public override int GetHashCode()
        {
            unchecked
            {
                var hashCode = Position.GetHashCode();
                hashCode = (hashCode * 397) ^ Color.GetHashCode();
                hashCode = (hashCode * 397) ^ TextureCoordinate.GetHashCode();
                hashCode = (hashCode * 397) ^ Normal.GetHashCode();
                return hashCode;
            }
        }

        public override string ToString()
        {
            return "{{Position:" + Position + " Color:" + Color + " TextureCoordinate:" + TextureCoordinate + " Normal: " + Normal + "}}";
        }

        public static bool operator ==(VertexPositionLightTexture left, VertexPositionLightTexture right)
        {
            return (((left.Position == right.Position) && (left.Color == right.Color)) && (left.TextureCoordinate == right.TextureCoordinate) && (left.Normal == right.Normal));
        }

        public static bool operator !=(VertexPositionLightTexture left, VertexPositionLightTexture right)
        {
            return !(left == right);
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;

            if (obj.GetType() != base.GetType())
                return false;

            return (this == ((VertexPositionLightTexture)obj));
        }

        static VertexPositionLightTexture()
        {
            var elements = new VertexElement[]
            {
                new VertexElement(0, VertexElementFormat.Vector3, VertexElementUsage.Position, 0),
                new VertexElement(12, VertexElementFormat.Color, VertexElementUsage.Color, 0),
                new VertexElement(16, VertexElementFormat.Vector2, VertexElementUsage.TextureCoordinate, 0),
                new VertexElement(24, VertexElementFormat.Single, VertexElementUsage.Normal, 0)
            };
            VertexDeclaration = new VertexDeclaration(elements);
        }
    }
}
