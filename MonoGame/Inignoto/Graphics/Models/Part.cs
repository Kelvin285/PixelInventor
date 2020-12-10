using Inignoto.Effects;
using Inignoto.Graphics.Mesh;
using Inignoto.Graphics.World;
using Inignoto.Math;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static Inignoto.Graphics.Models.GameModel;

namespace Inignoto.Graphics.Models
{
    public class Part
    {
        public static readonly float SCALING = 1.0f / 32.0f;

        private Vector3 position = new Vector3();
        private Quaternion rotation = new Quaternion();
        private Vector3 scale = new Vector3(1, 1, 1);
        public Vector3 size = new Vector3();
        public Vector2 uv = new Vector2(0, 0);
        public Vector3 origin = new Vector3();

        public Vector3 renderPosition = new Vector3();
        public Quaternion renderRotation = new Quaternion();

        public Part parent;
        public List<Part> children = new List<Part>();

        public bool visible = true;
        public bool locked = true;

        public string name = "Part";

        public Mesh.Mesh mesh;

        private Texture2D texture;

        public Vector3 look = new Vector3(0, 0, 1);

        public Vector3 axisAngles = new Vector3(0, 0, 0);

        public Vector3 RenderPosition;
        public Quaternion RenderRotation;

        private GameModel model;
        public Part(GameModel model)
        {
            this.model = model;
        }

        public static void CopyModelPart(Part part, Part parent, GameModel model)
        {
            Part p = new Part(model);
            p.name = "" + part.name;
            p.SetPosition(new Vector3(part.position.X, part.position.Y, part.position.Z));
            p.SetRotation(new Quaternion(part.rotation.X, part.rotation.Y, part.rotation.Z, part.rotation.W));
            p.SetScale(new Vector3(part.scale.X, part.scale.Y, part.scale.Z));
            p.size = new Vector3(part.size.X, part.size.Y, part.size.Z);
            p.uv = new Vector2(part.uv.X, part.uv.Y);
            p.origin = new Vector3(part.origin.X, part.origin.Y, part.origin.Z);
            p.parent = parent;
            if (parent != null)
            {
                parent.children.Add(p);
            }
            foreach (Part c in part.children)
            {
                CopyModelPart(c, p, model);
            }
            p.BuildPart(part.texture);
            model.Parts.Add(p);
        }

        public void BuildPart()
        { 
            this.mesh = BuildMesh(this, this.texture);
        }

        public void BuildPart(Texture2D texture)
        {
            mesh = BuildMesh(this, texture);
            this.texture = texture;
        }

        public static Mesh.Mesh BuildMesh(Part part, Texture2D texture)
        {
            float size_x = part.size.X;
            float size_y = part.size.Y;
            float size_z = part.size.Z;
            float U = part.uv.X;
            float V = part.uv.Y;

            float[] texCoords = {
				//front
				U + size_z, V + size_z + size_y,
                U + size_z, V + size_z,
                U + size_z + size_x, V + size_z,
                U + size_z + size_x, V + size_z + size_y,
				//back
				U + size_z + size_x + size_z, V + size_z + size_y,
                U + size_z + size_x + size_z, V + size_z,
                U + size_z + size_x + size_x + size_z, V + size_z,
                U + size_z + size_x + size_x + size_z, V + size_z + size_y,
				//left
				U, V + size_z + size_y,
                U, V + size_z,
                U + size_z, V + size_z,
                U + size_z, V + size_z + size_y,
				//right
				U + size_z + size_x, V + size_z + size_y,
                U + size_z + size_x, V + size_z,
                U + size_z * 2 + size_x, V + size_z,
                U + size_z * 2 + size_x, V + size_z + size_y,
				//top
				U + size_z, V + size_z,
                U + size_z, V,
                U + size_z + size_x, V,
                U + size_z + size_x, V + size_z,
				//bottom
				U + size_z + size_x, V + size_z,
                U + size_z + size_x, V,
                U + size_z + size_x + size_x, V,
                U + size_z + size_x + size_x, V + size_z,

            };

            for (int i = 0; i < texCoords.Length; i++)
            {
                texCoords[i] /= (float)texture.Width;
                i++;
                texCoords[i] /= (float)texture.Height;
            }
            return BuildMesh(part, texture, texCoords);
        }

        public static Mesh.Mesh BuildMesh(Part part, Texture2D texture, float[] texCoords)
        {

            float size_x = part.size.X;
            float size_y = part.size.Y;
            float size_z = part.size.Z;

            List<Vector3> vertexCoords = new List<Vector3>();

            vertexCoords.Add(new Vector3(0, size_y, 0));
            vertexCoords.Add(new Vector3(size_x, size_y, 0));
            vertexCoords.Add(new Vector3(size_x, 0, 0));
            vertexCoords.Add(new Vector3(0, 0, 0));

            vertexCoords.Add(new Vector3(0, size_y, size_z));
            vertexCoords.Add(new Vector3(size_x, size_y, size_z));
            vertexCoords.Add(new Vector3(size_x, 0, size_z));
            vertexCoords.Add(new Vector3(0, 0, size_z));

            List<Vector3> verts = new List<Vector3>();

            verts.Add(new Vector3(0, 0, 0));
            verts.Add(new Vector3(0, 1, 0));
            verts.Add(new Vector3(1, 1, 0));
            verts.Add(new Vector3(1, 0, 0));

            verts.Add(new Vector3(0, 0, 1));
            verts.Add(new Vector3(0, 1, 1));
            verts.Add(new Vector3(1, 1, 1));
            verts.Add(new Vector3(1, 0, 1));

            verts.Add(new Vector3(0, 0, 1));
            verts.Add(new Vector3(0, 1, 1));
            verts.Add(new Vector3(0, 1, 0));
            verts.Add(new Vector3(0, 0, 0));

            verts.Add(new Vector3(1, 0, 0));
            verts.Add(new Vector3(1, 1, 0));
            verts.Add(new Vector3(1, 1, 1));
            verts.Add(new Vector3(1, 0, 1));

            verts.Add(new Vector3(0, 1, 0));
            verts.Add(new Vector3(0, 1, 1));
            verts.Add(new Vector3(1, 1, 1));
            verts.Add(new Vector3(1, 1, 0));

            verts.Add(new Vector3(0, 0, 1));
            verts.Add(new Vector3(0, 0, 0));
            verts.Add(new Vector3(1, 0, 0));
            verts.Add(new Vector3(1, 0, 1));

            int[] indices = {
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            8, 9, 10, 10, 11, 8,
            12, 13, 14, 14, 15, 12,
            16, 17, 18, 18, 19, 16,
            20, 21, 22, 22, 23, 20
        };

            float[] vertices = new float[verts.Count * 3];

            //5
            for (int i = 0; i < verts.Count; i++)
            {
                int J = i * 3;


                Vector3 p5 = vertexCoords[5] * 0.5f;



                Vector3 v = (verts[i] - new Vector3(0.5f, 0.5f, 0.5f)) * new Vector3(p5.X * 2, p5.Y * 2, p5.Z * 2) * part.GetScale();
                vertices[J] = v.X;
                vertices[J + 1] = v.Y;
                vertices[J + 2] = v.Z;


            }

            VertexPositionLightTexture[] vpct = new VertexPositionLightTexture[indices.Length];
            //F, B, L, R, T, B

            List<int> normals = new List<int>();
            normals.Add((int)Tiles.Tile.TileFace.FRONT);
            normals.Add((int)Tiles.Tile.TileFace.FRONT);
            normals.Add((int)Tiles.Tile.TileFace.FRONT);
            normals.Add((int)Tiles.Tile.TileFace.FRONT);
            normals.Add((int)Tiles.Tile.TileFace.BACK);
            normals.Add((int)Tiles.Tile.TileFace.BACK);
            normals.Add((int)Tiles.Tile.TileFace.BACK);
            normals.Add((int)Tiles.Tile.TileFace.BACK);
            normals.Add((int)Tiles.Tile.TileFace.LEFT);
            normals.Add((int)Tiles.Tile.TileFace.LEFT);
            normals.Add((int)Tiles.Tile.TileFace.LEFT);
            normals.Add((int)Tiles.Tile.TileFace.LEFT);
            normals.Add((int)Tiles.Tile.TileFace.RIGHT);
            normals.Add((int)Tiles.Tile.TileFace.RIGHT);
            normals.Add((int)Tiles.Tile.TileFace.RIGHT);
            normals.Add((int)Tiles.Tile.TileFace.RIGHT);
            normals.Add((int)Tiles.Tile.TileFace.TOP);
            normals.Add((int)Tiles.Tile.TileFace.TOP);
            normals.Add((int)Tiles.Tile.TileFace.TOP);
            normals.Add((int)Tiles.Tile.TileFace.TOP);
            normals.Add((int)Tiles.Tile.TileFace.BOTTOM);
            normals.Add((int)Tiles.Tile.TileFace.BOTTOM);
            normals.Add((int)Tiles.Tile.TileFace.BOTTOM);
            normals.Add((int)Tiles.Tile.TileFace.BOTTOM);
            for (int i = 0; i < indices.Length; i++)
            {
                vpct[i] = new VertexPositionLightTexture(new Vector3(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2]), Color.White, new Vector4(texCoords[indices[i] * 2], texCoords[indices[i] * 2 + 1], -1, -1), normals[indices[i]]);
            }
            Mesh.Mesh mesh = new Mesh.Mesh(Inignoto.game.GraphicsDevice, vpct, false, texture);
            return mesh;
        }

        public static KeyTransformation GetOrCreateKeyTransformation(int frame, Part part)
        {
            KeyTransformation transformation = null;
            List<Keyframe> keyframes = part.model.timeline;
            if (frame <= keyframes.Count - 1 && frame >= 0)
            {
                Keyframe currentFrame = keyframes[frame];

                currentFrame.transformations.TryGetValue(part.name, out transformation);
                if (transformation == null)
                {
                    transformation = new KeyTransformation();
                    currentFrame.transformations.Add(part.name, transformation);
                }
            }
            return transformation;
        }

        public void Scale(float x, float y, float z)
        {
            if (model.editMode == GameModel.EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.scale += new Vector3(x, y, z);
                }
            } else
            {
                scale += new Vector3(x, y, z);
            }
        }

        public void Translate(Vector3 translation)
        {
            if (model.editMode == EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Translate(translation);
                }

                foreach (Part part in children)
                {
                    part.Translate(translation);
                }
            } else
            {
                position += (translation);
                foreach (Part part in children)
                {
                    part.Translate(translation);
                }
            }
        }

        public void RotateEuler(Vector3 rotation)
        {
            Vector3 origin = (Matrix.CreateTranslation(this.origin) * Matrix.CreateFromQuaternion(this.rotation)).Translation;

            Quaternion rot = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);


            if (model.editMode == EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Rotate(rot, origin - position);
                }

                foreach (Part part in children)
                {
                    part.Rotate(rot, origin);
                }
            }

            this.rotation = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);

            position = Raytracing.RotateAround(position, origin, rot);


            foreach (Part part in this.children)
            {
                part.Rotate(rot, origin);
            }
        }

        public void Rotate(Vector3 rotation)
        {
            Rotate(rotation, position + (Matrix.CreateTranslation(origin) * Matrix.CreateFromQuaternion(this.rotation)).Translation);
        }

        public void Rotate(Quaternion rotation)
        {
            Rotate(rotation, position + (Matrix.CreateTranslation(origin) * Matrix.CreateFromQuaternion(this.rotation)).Translation);
        }

        public void Rotate(Quaternion rot, Vector3 origin)
        {
            if (this.model.editMode == GameModel.EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Rotate(rot, origin - position);
                }

                foreach (Part part in children)
                {
                    part.Rotate(rot, origin);
                }
            } else
            {
                rotation = Quaternion.Concatenate(rotation, rot);

                position = Raytracing.RotateAround(position, origin, rot);


                foreach (Part part in this.children)
                {
                    part.Rotate(rot, origin);
                }
            }
            
        }

        public void Rotate(Vector3 rotation, Vector3 origin)
        {
            if (model.editMode == EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Rotate(rotation, origin - position);
                }

                foreach (Part part in children)
                {
                    part.Rotate(rotation, origin);
                }
            } else
            {
                Quaternion xRot = Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), rotation.X);
                Quaternion yRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), rotation.Y);
                Quaternion zRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), rotation.Z);

                Quaternion rot = Quaternion.Concatenate(Quaternion.Concatenate(xRot, yRot), zRot);

                this.rotation = Quaternion.Concatenate(this.rotation, rot);
                axisAngles += rotation;

                position = Raytracing.RotateAround(position, origin, rot);


                foreach (Part part in this.children)
                {
                    part.Rotate(rotation, origin);
                }
            }
            
        }

        public void SetRotation(Vector3 rotation)
        {
            Rotate(rotation * -1);
            axisAngles = new Vector3(0, 0, 0);
            this.rotation = Quaternion.Identity;
            Rotate(rotation);
        }

        public void ChangeTexture(Texture2D texture)
        {
            this.BuildPart(texture);
        }

        public Vector3 GetScale()
        {
            if (model != null)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);
                if (transformation != null && model.editMode == EditMode.ANIMATION)
                {
                    KeyTransformation next = GetOrCreateKeyTransformation(model.GetNextFrame(), this);
                    if (next != null)
                    {
                        return scale * (Vector3.Lerp(transformation.scale, next.scale, 1.0f - model.TimeUntilNextFrame()));
                    }
                    else
                    {
                        return scale * transformation.scale;
                    }
                }
            }
            return scale;
        }

        public void SetScale(Vector3 scale)
        {
            this.scale = scale;
        }

        public Vector3 GetPosition()
        {
            if (model != null)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);
                if (transformation != null && model.editMode == EditMode.ANIMATION)
                {
                    KeyTransformation next = GetOrCreateKeyTransformation(model.GetNextFrame(), this);
                    if (next != null)
                    {
                        return position + Vector3.Lerp(transformation.position, next.position, 1.0f - model.TimeUntilNextFrame());
                    }
                    else
                    {
                        return position + transformation.position;
                    }

                }
            }
            return position;
        }

        public void SetPosition(Vector3 position)
        {
            this.position = position;
        }

        public Quaternion GetRotation()
        {
            if (model != null)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);
                if (transformation != null && model.editMode == EditMode.ANIMATION)
                {
                    KeyTransformation next = GetOrCreateKeyTransformation(model.GetNextFrame(), this);
                    if (next != null)
                    {
                        return Quaternion.Lerp(transformation.rotation, next.rotation, 1.0f - model.TimeUntilNextFrame()) * rotation;
                    }
                    else
                    {
                        return transformation.rotation * rotation;
                    }
                }
            }
            return rotation;
        }

        public void SetRotation(Quaternion rotation)
        {
            this.rotation = rotation;
        }

        public Vector3 GetActualPosition()
        {
            return this.position;
        }
        public Quaternion GetActualRotation()
        {
            return this.rotation;
        }

        public void Dispose()
        {
            mesh.Dispose();
        }

        public void Render(GraphicsDevice device, GameEffect effect)
        {

            Quaternion rotation = GetRotation();

            Quaternion current = rotation;

            Vector3 position = GetPosition() * SCALING;

            Vector3 scale = GetScale() * SCALING * new Vector3(1, 1, -1);

            renderPosition = Vector3.Lerp(renderPosition, position, 0.25f);
            renderRotation = Quaternion.Lerp(renderRotation, rotation, 0.25f);

            position = new Vector3(renderPosition.X, renderPosition.Y, renderPosition.Z);
            rotation = new Quaternion(renderRotation.X, renderRotation.Y, renderRotation.Z, renderRotation.W);

            Quaternion euler = Quaternion.CreateFromYawPitchRoll(model.rotation.Y, model.rotation.X, model.rotation.Z);
            position = (Matrix.CreateTranslation(position) * Matrix.CreateFromQuaternion(euler)).Translation;

            rotation = Quaternion.Concatenate(rotation, euler);


            position = position * model.scale;

            position += model.translation;

            scale = scale * model.scale;

            mesh.SetPosition(position);
            mesh.SetRotation(rotation);
            mesh.SetScale(scale);

            RenderPosition = position;

            if (this.mesh != null && this.visible)
                mesh.Draw(texture, effect, device);
            
        }

    }
}
