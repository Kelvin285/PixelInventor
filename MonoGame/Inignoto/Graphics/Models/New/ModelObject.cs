using Inignoto.Effects;
using Inignoto.GameSettings;
using Inignoto.Graphics.Gui;
using Inignoto.Math;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Text;
using static Inignoto.Math.Raytracing;

namespace Inignoto.Graphics.Models.New
{
    public class ModelObject
    {

        public enum ModelEditMode
        {
            TRANSLATION, ROTATION
        }

        public static ModelEditMode edit_mode = ModelEditMode.ROTATION; // Whether or not you are in translation mode or rotation mode (for model editing)
        private static Vector2 initial_mouse_pos = Vector2.Zero; // Used for calculating rotation (for model editing).  Sets the center of the rotation calculation
        private static float last_rad; // Used for calculating rotation (for model editing)
        public static bool grid_lock_translation; // Translation locked to a pixel grid if true

        public List<ModelObject> children;
        protected ModelObject parent;

        public Vector3 translation;
        private Vector3 lock_translation;
        public Quaternion rotation;
        public Vector3 scale;
        public Vector3 origin;

        protected Vector3 renderTranslation; // The rotation of the object that is used in rendering
        protected Quaternion renderRotation;

        public bool editing;
        public bool root = false;
        public bool selected = false;
        public bool select_x, select_y, select_z;

        public static ModelPlane plane = new ModelPlane(new Vector3(0, 0, 0));

        public ModelObject()
        {
            editing = false;
            children = new List<ModelObject>();
            translation = Vector3.Zero;
            rotation = Quaternion.Identity;
            scale = Vector3.One;
            origin = Vector3.Zero;
        }

        public virtual void SetParent(ModelObject newParent)
        {
            if (parent != null)
            {
                parent.children.Remove(this);
            }

            parent = newParent;
            if (parent != null) parent.children.Add(this);
        }

        public virtual void AddChild(ModelObject child)
        {
            if (!children.Contains(child))
            {
                child.SetParent(this);
            }
        }

        public ModelObject GetParent()
        {
            return parent;
        }

        public virtual void Render(GraphicsDevice device, GameEffect effect, ModelObject select)
        {
            lock (children)
            {
                for (int i = 0; i < children.Count; i++)
                {
                    children[i].Render(device, effect, Matrix.CreateTranslation(translation), rotation, select);
                }
            }
        }

        public virtual RayIntersection GetIntersection(Vector3 position, Vector3 direction)
        {
            
            RayBox box = new RayBox();
            box.Min = renderTranslation - scale / 2.0f;
            box.Max = box.Min + scale;

            box.Min -= renderTranslation;
            box.Max -= renderTranslation;
            box.Min = Raytracing.RotateDir(box.Min, renderRotation);
            box.Max = Raytracing.RotateDir(box.Max, renderRotation);
            if (box.Min.X > box.Max.X)
            {
                float X = box.Min.X;
                box.Min.X = box.Max.X;
                box.Max.X = X;
            }
            if (box.Min.Y > box.Max.Y)
            {
                float Y = box.Min.Y;
                box.Min.Y = box.Max.Y;
                box.Max.Y = Y;
            }
            if (box.Min.Z > box.Max.Z)
            {
                float Z = box.Min.Z;
                box.Min.Z = box.Max.Z;
                box.Max.Z = Z;
            }
            box.Min += renderTranslation;
            box.Max += renderTranslation;

            RayIntersection intersection = Raytracing.IntersectBox(Inignoto.game.camera.position, direction, box, renderRotation);

            if (!Raytracing.DoesCollisionOccur(Inignoto.game.camera.position, direction, box, Quaternion.Identity))
            {
                intersection.lambda = new Vector2(float.MaxValue);
            }

            return intersection;
        }

        public ModelObject TestForIntersection(Vector3 position, Vector3 direction)
        {
            float distance = float.MaxValue;
            ModelObject closest = null;
            RayIntersection RecursiveIntersect(ModelObject obj)
            {
                RayIntersection intersection = new RayIntersection();
                intersection.lambda = new Vector2(float.MaxValue);
                if (!root)
                {
                    intersection = obj.GetIntersection(position, direction);
                    obj.selected = false;
                    if (intersection.lambda.X < distance)
                    {
                        distance = intersection.lambda.X;
                        closest = obj;
                    }
                }
                for (int i = 0; i < obj.children.Count; i++)
                {
                    RecursiveIntersect(obj.children[i]);
                }
                return intersection;
            }
            RecursiveIntersect(this);
            if (closest != null)
            {
                closest.selected = true;
            }
            return closest;
        }

        public virtual void Render(GraphicsDevice device, GameEffect effect, Matrix lastMatrix, Quaternion lastRotation, ModelObject select)
        {
            renderRotation = rotation;
            renderTranslation = translation;
            lock (children)
            {
                for (int i = 0; i < children.Count; i++)
                {
                    children[i].Render(device, effect, lastMatrix, lastRotation, select);
                }
            }

            if (!Settings.ATTACK.IsPressed())
            {
                select_x = false;
                select_y = false;
                select_z = false;
                last_rad = float.MaxValue;
            }

            if (this == select || select_x || select_y || select_z)
            {
                Vector3 newTranslation = (Matrix.CreateTranslation(translation) * Matrix.CreateFromQuaternion(lastRotation)).Translation;
                newTranslation = Vector3.Zero;
                double dist = double.MaxValue;
                bool Y = false, X = false, Z = false;
                MainMenu menu = (MainMenu)Hud.openGui;

                void UpArrow()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, MathF.Abs(scaleMatrix.Translation.Y) + 0.5f, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.position_handle_y;

                    plane.mesh.SetScale(Vector3.One);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, MathF.PI, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(MathF.PI / 2, MathF.PI, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = true;
                                X = false;
                                Z = false;
                            }
                        }
                    }
                }

                void DownArrow()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, -MathF.Abs(scaleMatrix.Translation.Y) - 0.5f, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.position_handle_y;
                    plane.mesh.SetScale(Vector3.One);
                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, 0, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(MathF.PI / 2, 0, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);
                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = true;
                                X = false;
                                Z = false;
                            }
                        }
                    }
                }

                void LeftArrow()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(-MathF.Abs(scaleMatrix.Translation.X) - 0.5f, 0, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.position_handle_x;
                    plane.mesh.SetScale(Vector3.One);
                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, 0, -MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, MathF.PI / 2, -MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                X = true;
                                Y = false;
                                Z = false;
                            }
                        }
                    }
                }

                void RightArrow()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(MathF.Abs(scaleMatrix.Translation.X) + 0.5f, 0, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.position_handle_x;
                    plane.mesh.SetScale(Vector3.One);
                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, 0, MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, MathF.PI / 2, MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                X = true;
                                Y = false;
                                Z = false;
                            }
                        }
                    }
                }

                void BackArrow()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, 0, -MathF.Abs(scaleMatrix.Translation.Z) - 0.5f);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.position_handle_z;
                    plane.mesh.SetScale(Vector3.One);
                    plane.rotation = Quaternion.CreateFromYawPitchRoll(MathF.PI, -MathF.PI / 2, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    plane.rotation = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), MathF.PI / 2) * Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Z = true;
                                Y = false;
                                X = false;
                            }
                        }
                    }
                }

                void FrontArrow()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, 0, MathF.Abs(scaleMatrix.Translation.Z) + 0.5f);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.position_handle_z;
                    plane.mesh.SetScale(Vector3.One);
                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, -MathF.PI / 2, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Z = true;
                                Y = false;
                                X = false;
                            }
                        }
                    }

                    plane.rotation = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), MathF.PI / 2) * Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), -MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);

                    
                }

                void UpRotation()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, MathF.Abs(scaleMatrix.Translation.Y) + 0.5f, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.rotation_handle_y;

                    plane.mesh.SetScale(Vector3.One);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, MathF.PI / 2, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);


                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = true;
                                X = false;
                                Z = false;
                            }
                        }
                    }
                }

                void DownRotation()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, -MathF.Abs(scaleMatrix.Translation.Y) - 0.5f, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.rotation_handle_y;

                    plane.mesh.SetScale(Vector3.One);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, MathF.PI / 2, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);


                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = true;
                                X = false;
                                Z = false;
                            }
                        }
                    }
                }

                void LeftRotation()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(-MathF.Abs(scaleMatrix.Translation.X) - 0.5f, 0, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.rotation_handle_x;

                    plane.mesh.SetScale(Vector3.One);

                    plane.rotation = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), MathF.PI / 2) * Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);


                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = false;
                                X = true;
                                Z = false;
                            }
                        }
                    }
                }

                void RightRotation()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(MathF.Abs(scaleMatrix.Translation.X) + 0.5f, 0, 0);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.rotation_handle_x;

                    plane.mesh.SetScale(Vector3.One);

                    plane.rotation = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), MathF.PI / 2) * Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), MathF.PI / 2);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);


                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = false;
                                X = true;
                                Z = false;
                            }
                        }
                    }
                }

                void FrontRotation()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, 0, MathF.Abs(scaleMatrix.Translation.Z) + 0.5f);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.rotation_handle_z;

                    plane.mesh.SetScale(Vector3.One);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, 0, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);


                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = false;
                                X = false;
                                Z = true;
                            }
                        }
                    }
                }

                void BackRotation()
                {
                    Matrix scaleMatrix = Matrix.CreateTranslation(scale);
                    scaleMatrix *= Matrix.CreateFromQuaternion(rotation);
                    plane.translation = newTranslation + lastMatrix.Translation + new Vector3(0, 0, -MathF.Abs(scaleMatrix.Translation.Z) - 0.5f);

                    plane.renderTranslation = plane.translation;

                    plane.mesh.texture = Textures.Textures.rotation_handle_z;

                    plane.mesh.SetScale(Vector3.One);

                    plane.rotation = Quaternion.CreateFromYawPitchRoll(0, 0, 0);
                    plane.Render(device, effect, Matrix.Identity, Quaternion.Identity, null);


                    RayIntersection intersection = plane.GetIntersection(Inignoto.game.camera.position, menu.mdir);
                    if (intersection.lambda.X != float.MaxValue)
                    {
                        if (Settings.ATTACK.IsPressed())
                        {
                            if (intersection.lambda.X < dist)
                            {
                                dist = intersection.lambda.X;
                                Y = false;
                                X = false;
                                Z = true;
                            }
                        }
                    }
                }

                if (edit_mode == ModelEditMode.TRANSLATION)
                {
                    if (Inignoto.game.camera.position.Y > translation.Y)
                    {
                        UpArrow();
                    }
                    else
                    {
                        DownArrow();
                    }

                    if (Inignoto.game.camera.position.X > translation.X)
                    {
                        RightArrow();
                    }
                    else
                    {
                        LeftArrow();
                    }

                    if (Inignoto.game.camera.position.Z > translation.Z)
                    {
                        FrontArrow();
                    }
                    else
                    {
                        BackArrow();
                    }
                } else
                {
                    if (Inignoto.game.camera.position.Y > translation.Y)
                    {
                        UpRotation();
                    }
                    else
                    {
                        DownRotation();
                    }

                    if (Inignoto.game.camera.position.X > translation.X)
                    {
                        RightRotation();
                    }
                    else
                    {
                        LeftRotation();
                    }

                    if (Inignoto.game.camera.position.Z > translation.Z)
                    {
                        FrontRotation();
                    }
                    else
                    {
                        BackRotation();
                    }
                }

                if (!select_x && !select_y && !select_z)
                {

                    if (!select_y && !select_z)
                    {
                        select_x = X;
                        initial_mouse_pos = new Vector2(menu.lastMousePos.X, menu.lastMousePos.Y);
                    }
                    if (!select_x && !select_z)
                    {
                        select_y = Y;
                        initial_mouse_pos = new Vector2(menu.lastMousePos.X, menu.lastMousePos.Y);
                    }
                    if (!select_x && !select_y)
                    {
                        select_z = Z;
                        initial_mouse_pos = new Vector2(menu.lastMousePos.X, menu.lastMousePos.Y);
                    }
                }
                if (select_x)
                {
                    if (edit_mode == ModelEditMode.TRANSLATION)
                    {
                        float move_x = menu.lastMousePos.X - Inignoto.game.mousePos.X;
                        float move_y = menu.lastMousePos.Y - Inignoto.game.mousePos.Y;
                        move_x /= 1920.0f;
                        move_y /= 1080.0f;

                        float camdist = Vector3.Distance(Inignoto.game.camera.position, newTranslation);
                        move_x *= camdist;
                        move_y *= camdist;

                        float move = MathF.Cos(Inignoto.game.camera.rotation.Y * MathF.PI / 180.0f) * move_x + MathF.Sin(Inignoto.game.camera.rotation.Y * MathF.PI / 180.0f) * move_y;

                        if (grid_lock_translation)
                        {
                            lock_translation.X -= move;
                            translation.X = lock_translation.X;
                            translation.X *= 32;
                            translation.X = MathF.Round(translation.X);
                            translation.X /= 32.0f;
                        }
                        else
                        {
                            translation.X -= move;
                            lock_translation.X = translation.X;
                        }
                    } else {
                        float rad = MathF.Atan2(menu.lastMousePos.Y - initial_mouse_pos.Y, menu.lastMousePos.X - initial_mouse_pos.X);
                        if (Vector2.Distance(new Vector2(menu.lastMousePos.X, menu.lastMousePos.Y), initial_mouse_pos) >= 10)
                        {
                            if (last_rad != float.MaxValue)
                            {
                                if (rad < last_rad - MathF.PI)
                                {
                                    rad += MathF.PI;
                                }
                                if (rad > last_rad + MathF.PI)
                                {
                                    rad -= MathF.PI;
                                }
                                float rotate = rad - last_rad;
                                if (Inignoto.game.camera.position.X < renderTranslation.X)
                                {
                                    rotate *= -1;
                                }
                                rotation = Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), -rotate) * rotation;
                            }
                            last_rad = rad;
                        }
                    }
                    selected = true;
                }
                else if (select_y)
                {
                    if (edit_mode == ModelEditMode.TRANSLATION)
                    {
                        float move_y = menu.lastMousePos.Y - Inignoto.game.mousePos.Y;
                        move_y /= 1080.0f;

                        float camdist = Vector3.Distance(Inignoto.game.camera.position, newTranslation);
                        move_y *= camdist;

                        if (grid_lock_translation)
                        {
                            lock_translation.Y += move_y;
                            translation.Y = lock_translation.Y;
                            translation.Y *= 32;
                            translation.Y = MathF.Round(translation.Y);
                            translation.Y /= 32.0f;
                        }
                        else
                        {
                            translation.Y += move_y;
                            lock_translation.Y = translation.Y;
                        }
                    } else
                    {
                        float rad = MathF.Atan2(menu.lastMousePos.Y - initial_mouse_pos.Y, menu.lastMousePos.X - initial_mouse_pos.X);
                        if (Vector2.Distance(new Vector2(menu.lastMousePos.X, menu.lastMousePos.Y), initial_mouse_pos) >= 10)
                        {
                            if (last_rad != float.MaxValue)
                            {
                                if (rad < last_rad - MathF.PI)
                                {
                                    rad += MathF.PI;
                                }
                                if (rad > last_rad + MathF.PI)
                                {
                                    rad -= MathF.PI;
                                }
                                float rotate = rad - last_rad;
                                if (Inignoto.game.camera.position.Y < renderTranslation.Y)
                                {
                                    rotate *= -1;
                                }
                                //rotation *= Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), -rotate);
                                rotation = Quaternion.CreateFromAxisAngle(new Vector3(0, 1, 0), -rotate) * rotation;
                            }
                            last_rad = rad;
                        }
                    }
                    

                    selected = true;
                } else if (select_z)
                {
                    if (edit_mode == ModelEditMode.TRANSLATION)
                    {
                        float move_x = menu.lastMousePos.X - Inignoto.game.mousePos.X;
                        float move_y = menu.lastMousePos.Y - Inignoto.game.mousePos.Y;
                        move_x /= 1920.0f;
                        move_y /= 1080.0f;

                        float camdist = Vector3.Distance(Inignoto.game.camera.position, newTranslation);
                        move_x *= camdist;
                        move_y *= camdist;

                        float move = -MathF.Sin(Inignoto.game.camera.rotation.Y * MathF.PI / 180.0f) * move_x + MathF.Cos(Inignoto.game.camera.rotation.Y * MathF.PI / 180.0f) * move_y;

                        if (grid_lock_translation)
                        {
                            lock_translation.Z -= move;
                            translation.Z = lock_translation.Z;
                            translation.Z *= 32;
                            translation.Z = MathF.Round(translation.Z);
                            translation.Z /= 32.0f;
                        }
                        else
                        {
                            translation.Z -= move;
                            lock_translation.Z = translation.Z;
                        }
                    } else
                    {
                        float rad = MathF.Atan2(menu.lastMousePos.Y - initial_mouse_pos.Y, menu.lastMousePos.X - initial_mouse_pos.X);
                        if (Vector2.Distance(new Vector2(menu.lastMousePos.X, menu.lastMousePos.Y), initial_mouse_pos) >= 10)
                        {
                            if (last_rad != float.MaxValue)
                            {
                                if (rad < last_rad - MathF.PI)
                                {
                                    rad += MathF.PI;
                                }
                                if (rad > last_rad + MathF.PI)
                                {
                                    rad -= MathF.PI;
                                }
                                float rotate = rad - last_rad;
                                if (Inignoto.game.camera.position.Z < renderTranslation.Z)
                                {
                                    rotate *= -1;
                                }
                                rotation = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), -rotate) * rotation;
                            }
                            last_rad = rad;
                        }
                    }
                    
                    selected = true;
                }
                if (selected)
                {
                    menu.selected = this;
                }
                
            }
        }
    }
}
