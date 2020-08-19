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

        private Vector3f position = new Vector3f();
        private Quaternionf rotation = new Quaternionf();
        private Vector3f scale = new Vector3f(1, 1, 1);
        public Vector3f size = new Vector3f();
        public Vector2 uv = new Vector2(0, 0);
        public Vector3f origin = new Vector3f();

        public Part parent;
        public List<Part> children = new List<Part>();

        public bool visible = true;
        public bool locked = true;

        public string name = "Part";

        public Mesh.Mesh mesh;

        private Texture2D texture;

        public Vector3f look = new Vector3f(0, 0, 1);

        public Vector3f axisAngles = new Vector3f(0, 0, 0);

        private GameModel model;
        public Part(GameModel model)
        {
            this.model = model;
        }

        public static void CopyModelPart(Part part, Part parent, GameModel model)
        {
            Part p = new Part(model);
            p.name = "" + part.name;
            p.SetPosition(new Vector3f(part.position));
            p.SetRotation(new Quaternionf(part.rotation.Rotation));
            p.SetScale(new Vector3f(part.scale));
            p.size = new Vector3f(part.size);
            p.uv = new Vector2(part.uv.X, part.uv.Y);
            p.origin = new Vector3f(part.origin);
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

            List<Vector3f> vertexCoords = new List<Vector3f>();

            vertexCoords.Add(new Vector3f(0, size_y, 0));
            vertexCoords.Add(new Vector3f(size_x, size_y, 0));
            vertexCoords.Add(new Vector3f(size_x, 0, 0));
            vertexCoords.Add(new Vector3f(0, 0, 0));

            vertexCoords.Add(new Vector3f(0, size_y, size_z));
            vertexCoords.Add(new Vector3f(size_x, size_y, size_z));
            vertexCoords.Add(new Vector3f(size_x, 0, size_z));
            vertexCoords.Add(new Vector3f(0, 0, size_z));

            List<Vector3f> verts = new List<Vector3f>();

            verts.Add(new Vector3f(0, 0, 0));
            verts.Add(new Vector3f(0, 1, 0));
            verts.Add(new Vector3f(1, 1, 0));
            verts.Add(new Vector3f(1, 0, 0));

            verts.Add(new Vector3f(1, 0, 1));
            verts.Add(new Vector3f(1, 1, 1));
            verts.Add(new Vector3f(0, 1, 1));
            verts.Add(new Vector3f(0, 0, 1));

            verts.Add(new Vector3f(0, 0, 1));
            verts.Add(new Vector3f(0, 1, 1));
            verts.Add(new Vector3f(0, 1, 0));
            verts.Add(new Vector3f(0, 0, 0));

            verts.Add(new Vector3f(1, 0, 0));
            verts.Add(new Vector3f(1, 1, 0));
            verts.Add(new Vector3f(1, 1, 1));
            verts.Add(new Vector3f(1, 0, 1));

            verts.Add(new Vector3f(0, 1, 0));
            verts.Add(new Vector3f(0, 1, 1));
            verts.Add(new Vector3f(1, 1, 1));
            verts.Add(new Vector3f(1, 1, 0));

            verts.Add(new Vector3f(0, 0, 1));
            verts.Add(new Vector3f(0, 0, 0));
            verts.Add(new Vector3f(1, 0, 0));
            verts.Add(new Vector3f(1, 0, 1));

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


                Vector3f p5 = new Vector3f(vertexCoords[5]).Mul(0.5f);



                Vector3f v = new Vector3f(verts[i].X, verts[i].Y, verts[i].Z).Sub(0.5f, 0.5f, 0.5f).Mul(p5.X * 2, p5.Y * 2, p5.Z * 2).Mul(part.GetScale());
                vertices[J] = v.X;
                vertices[J + 1] = v.Y;
                vertices[J + 2] = v.Z;


            }

            VertexPositionColorTexture[] vpct = new VertexPositionColorTexture[indices.Length];

            for (int i = 0; i < indices.Length; i++)
            {
                vpct[i] = new VertexPositionColorTexture(new Vector3(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2]), Color.White, new Vector2(texCoords[indices[i] * 2], texCoords[indices[i] * 2 + 1]));
            }
            return new Mesh.Mesh(Inignoto.game.GraphicsDevice, vpct, false, texture);
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
                    transformation.scale.Add(x, y, z);
                }
            } else
            {
                this.scale.Add(x, y, z);
            }
        }

        public void Translate(Vector3f translation)
        {
            if (model.editMode == GameModel.EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Translate(translation);
                }

                foreach (Part part in this.children)
                {
                    part.Translate(new Vector3f(translation));
                }
            } else
            {
                this.position.Add(translation);
                foreach (Part part in this.children)
                {
                    part.Translate(new Vector3f(translation));
                }
            }
        }

        public void RotateEuler(Vector3f rotation)
        {
            Vector3f origin = new Vector3f(this.origin).Rotate(this.rotation.Rotation);

            Quaternion rot = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);


            if (this.model.editMode == GameModel.EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Rotate(rot, new Vector3f(origin).Sub(position));
                }

                foreach (Part part in children)
                {
                    part.Rotate(rot, origin);
                }
            }

            this.rotation.Rotation = Quaternion.CreateFromYawPitchRoll(rotation.Y, rotation.X, rotation.Z);

            position = Raytracing.RotateAround(position, origin, new Quaternionf(rot));


            foreach (Part part in this.children)
            {
                part.Rotate(rot, origin);
            }
        }

        public void Rotate(Vector3f rotation)
        {
            Rotate(rotation, new Vector3f(position).Add(new Vector3f(origin).Rotate(this.rotation.Rotation)));
        }

        public void Rotate(Quaternion rot, Vector3f origin)
        {
            if (this.model.editMode == GameModel.EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Rotate(rot, new Vector3f(origin).Sub(position));
                }

                foreach (Part part in children)
                {
                    part.Rotate(rot, new Vector3f(origin));
                }
            } else
            {
                this.rotation.Rotation = Quaternion.Concatenate(this.rotation.Rotation, rot);

                position = Raytracing.RotateAround(position, origin, new Quaternionf(rot));


                foreach (Part part in this.children)
                {
                    part.Rotate(rot, origin);
                }
            }
            
        }

        public void Rotate(Vector3f rotation, Vector3f origin)
        {
            if (this.model.editMode == GameModel.EditMode.ANIMATION)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);

                if (transformation != null)
                {
                    transformation.Rotate(rotation, new Vector3f(origin).Sub(position));
                }

                foreach (Part part in children)
                {
                    part.Rotate(new Vector3f(rotation), new Vector3f(origin));
                }
            } else
            {
                Quaternion xRot = Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), rotation.X);
                Quaternion yRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), rotation.Y);
                Quaternion zRot = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), rotation.Z);

                Quaternion rot = Quaternion.Concatenate(Quaternion.Concatenate(xRot, yRot), zRot);

                this.rotation.Rotation = Quaternion.Concatenate(this.rotation.Rotation, rot);
                axisAngles.Add(rotation);

                position = Raytracing.RotateAround(position, origin, new Quaternionf(rot));


                foreach (Part part in this.children)
                {
                    part.Rotate(rotation, origin);
                }
            }
            
        }

        public void SetRotation(Vector3f rotation)
        {
            Rotate(new Vector3f(rotation).Mul(-1));
            axisAngles.Set(0, 0, 0);
            this.rotation.Identity();
            Rotate(rotation);
        }

        public void ChangeTexture(Texture2D texture)
        {
            this.BuildPart(texture);
        }

        public Vector3f GetScale()
        {
            if (model != null)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);
                if (transformation != null && model.editMode == EditMode.ANIMATION)
                {
                    KeyTransformation next = GetOrCreateKeyTransformation(model.GetNextFrame(), this);
                    if (next != null)
                    {
                        return new Vector3f(scale).Mul(new Vector3f(transformation.scale).Lerp(next.scale, 1.0f - model.TimeUntilNextFrame()));
                    }
                    else
                    {
                        return new Vector3f(scale).Mul(transformation.scale);
                    }
                }
            }
            return scale;
        }

        public void SetScale(Vector3f scale)
        {
            this.scale = scale;
        }

        public Vector3f GetPosition()
        {
            if (model != null)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);
                if (transformation != null && model.editMode == EditMode.ANIMATION)
                {
                    KeyTransformation next = GetOrCreateKeyTransformation(model.GetNextFrame(), this);
                    if (next != null)
                    {
                        return new Vector3f(position).Add(new Vector3f(transformation.position).Lerp(next.position, 1.0f - model.TimeUntilNextFrame()));
                    }
                    else
                    {
                        return new Vector3f(position).Add(transformation.position);
                    }

                }
            }
            return position;
        }

        public void SetPosition(Vector3f position)
        {
            this.position = position;
        }

        public Quaternionf GetRotation()
        {
            if (model != null)
            {
                KeyTransformation transformation = GetOrCreateKeyTransformation((int)model.currentTime, this);
                if (transformation != null && model.editMode == EditMode.ANIMATION)
                {
                    KeyTransformation next = GetOrCreateKeyTransformation(model.GetNextFrame(), this);
                    if (next != null)
                    {
                        return new Quaternionf(new Quaternionf(transformation.rotation.Rotation).Nlerp(next.rotation, 1.0f - model.TimeUntilNextFrame()).Rotation * rotation.Rotation);
                    }
                    else
                    {
                        return new Quaternionf(transformation.rotation.Rotation * rotation.Rotation);
                    }
                }
            }
            return rotation;
        }

        public void SetRotation(Quaternionf rotation)
        {
            this.rotation = rotation;
        }

        public Vector3f GetActualPosition()
        {
            return this.position;
        }
        public Quaternionf GetActualRotation()
        {
            return this.rotation;
        }

        public void Render(GraphicsDevice device, BasicEffect effect)
        {
            
            Quaternionf rotation = new Quaternionf(GetRotation().Rotation);

            Quaternion current = rotation.Rotation;

            Vector3f position = new Vector3f(GetPosition()).Mul(SCALING);
            Vector3f scale = new Vector3f(GetScale()).Mul(SCALING).Mul(1, 1, -1);

            Quaternion euler = Quaternion.CreateFromYawPitchRoll(model.rotation.Y, model.rotation.X, model.rotation.Z);
            position.Rotate(euler);

            rotation.Rotation = Quaternion.Concatenate(rotation.Rotation, euler);

            if (current.Equals(rotation.Rotation))
            {
                rotation.Rotation = euler;
            }

            position.Mul(model.scale);

            position.Add(model.translation);

            scale.Mul(model.scale);

            mesh.SetPosition(position.Vector);
            mesh.SetRotation(rotation.Rotation);
            mesh.SetScale(scale.Vector);

            if (this.mesh != null && this.visible)
                mesh.Draw(texture, effect, device);
            
        }

    }
}
