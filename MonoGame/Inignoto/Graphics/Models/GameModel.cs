using Inignoto.Effects;
using Inignoto.Math;
using Inignoto.Utilities;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Inignoto.Graphics.Models
{
    public class GameModel
    {
        public readonly List<Part> Parts = new List<Part>();

        public List<Keyframe> timeline = new List<Keyframe>();

        public Dictionary<string, Vector3> extra_rotations = new Dictionary<string, Vector3>();

        public float currentTime = 0.0f;
        public float animationSpeed = 1.0f / 30.0f;

        public Vector3f rotation = new Vector3f(0, 0, 0);
        public Vector3f translation = new Vector3f(0, 0, 0);
        public Vector3f scale = new Vector3f(1, 1, 1);
        public Vector3f origin = new Vector3f(0, 0, 0);

        private Texture2D texture;

        public Texture2D Texture { get => texture; set {
                texture = value;
                ChangeTexture(value);
            } }

        public bool Combined = false;

        private bool playing = false;

        public bool IsPlaying()
        {
            return playing;
        }

        public void Play(float time)
        {
            playing = true;
            currentTime = time;
        }

        public void Stop()
        {
            playing = false;
        }

        public int GetCurrentFrame()
        {
            return (int)currentTime;
        }

        public int GetNextFrame()
        {
            int next = (int)currentTime + 1;
            if (next > timeline.Count() - 1)
            {
                next = 0;
            }
            return next;
        }

        public float TimeUntilNextFrame()
        {
            return (int)currentTime + 1 - currentTime;
        }

        public enum EditMode
        {
            MODEL, ANIMATION
        }

        public EditMode editMode = EditMode.MODEL;

        public GameModel()
        {
            ChangeTimelineLength(1);
        }

        public int GetAnimationLength()
        {
            return timeline.Count;
        }

        public void ChangeTimelineLength(int newValue)
        {
            if (newValue >= 0)
            {
                while (GetAnimationLength() < newValue)
                {
                    timeline.Add(new Keyframe(timeline.Count()));
                }
                while (GetAnimationLength() > newValue && GetAnimationLength() > 0)
                {
                    timeline.Remove(timeline[timeline.Count() - 1]);
                }
            }
        }

        public static void SaveAnimation(List<Keyframe> timeline, ResourcePath directory, string file)
        {
            string str = "";
            foreach (Keyframe frame in timeline)
            {
                str += ("\"frame\"=\"" + frame.time + "\"\n");
                str += ("\"speed\"=\"" + frame.speed + "\"\n");

                foreach (String key in frame.transformations.Keys)
                {
                    KeyTransformation transform = null;
                    frame.transformations.TryGetValue(key, out transform);
                    str += ("\"transform\"=\"" + str + "\"\n");
                    str += ("\"axisAngles\"=\"" + transform.axisAngles.X + "," + transform.axisAngles.Y + "," + transform.axisAngles.Z + "\"\n");
                    str += ("\"locked\"=\"" + transform.locked + "\"");
                    str += ("\"look\"=\"" + transform.look.X + "," + transform.look.Y + "," + transform.look.Z + "\"\n");
                    str += ("\"origin\"=\"" + transform.origin.X + "," + transform.origin.Y + "," + transform.origin.Z + "\"\n");
                    str += ("\"position\"=\"" + transform.position.X + "," + transform.position.Y + "," + transform.position.Z + "\"\n");
                    str += ("\"rotation\"=\"" + transform.rotation.X + "," + transform.rotation.Y + "," + transform.rotation.Z + "," + transform.rotation.W + "\"\n");
                    str += ("\"scale\"=\"" + transform.scale.X + "," + transform.scale.Y + "," + transform.scale.Z + "\"\n");
                    str += ("\"size\"=\"" + transform.size.X + "," + transform.size.Y + "," + transform.size.Z + "\"\n");
                    str += ("\"uv\"=\"" + transform.uv.X + "," + transform.uv.Y + "\"\n");
                    str += ("\"visible\"=\"" + transform.visible + "\"\n");
                }
            }
            FileUtils.WriteStringToFile(directory, new ResourcePath(directory.modid, directory.path + "/" + file, directory.root), str);
        }

        private bool merged = false;

        public GameModel Combine()
        {

            if (merged) return this;
            if (Parts.Count > 1)
            {
                Parts[0].BuildPart();

                Vector3f offset = new Vector3f(Parts[0].GetPosition()).Mul(-1);

                
                for (int i = 1; i < Parts.Count; i++)
                {
                    Parts[i].BuildPart();
                    Part part = Parts[i];

                    Parts[0].mesh.CombineWith(Parts[i].mesh, part.GetPosition(), part.GetScale(), part.GetRotation(), offset);
                }
                
                //Part p = Parts[0];
                //Parts.Clear();
                //Parts.Add(p);
                //p.locked = true;
                //merged = true;
            }
            //Combined = true;
            return this;
        }

        public static List<Keyframe> LoadAnimation(ResourcePath path)
        {
            List<Keyframe> frames = new List<Keyframe>();
            Keyframe currentKeyframe = null;
            KeyTransformation currentTransform = null;
            
            Dictionary<string, string> data = FileUtils.LoadFileAsDataList(path);

            List<string> commands = new List<string>();

            string name = "";

            foreach (string a in data.Keys)
            {
                data.TryGetValue(a, out string b);

                if (a.Length > 0 && b.Length > 0)
                {

                    if (commands.Contains(a)) continue;
                    commands.Add(a);
                    if (a.Contains("frame"))
                    {
                        currentKeyframe = new Keyframe(int.Parse(b));
                        frames.Add(currentKeyframe);
                    }
                    if (a.Contains("speed"))
                    {
                        currentKeyframe.speed = float.Parse(b);
                    }
                    if (a.Contains("transform"))
                    {
                        currentTransform = new KeyTransformation();
                        currentKeyframe.transformations.Add(b, currentTransform);
                        commands.Clear();
                        name = b;
                    }
                    if (a.Contains("rotation"))
                    {
                        string[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        float w = float.Parse(split[3]);
                        if (currentTransform.rotation.X == 0 &&
                            currentTransform.rotation.Y == 0 &&
                            currentTransform.rotation.Z == 0 &&
                            currentTransform.rotation.W == 0)
                        currentTransform.rotation = new Quaternionf(x, y, z, w);
                    }
                    if (a.Contains("look"))
                    {
                        string[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        if (currentTransform.look.X == 0 &&
                            currentTransform.look.Y == 0 &&
                            currentTransform.look.Z == 1)
                            currentTransform.look = new Vector3f(x, y, z);
                    }
                    if (a.Contains("origin"))
                    {
                        string[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        if (currentTransform.origin.X == 0 &&
                            currentTransform.origin.Y == 0 &&
                            currentTransform.origin.Z == 0)
                            currentTransform.origin = new Vector3f(x, y, z);
                    }
                    if (a.Contains("position"))
                    {
                        
                        string[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        if (currentTransform.position.X == 0 &&
                            currentTransform.position.Y == 0 &&
                            currentTransform.position.Z == 0)
                            currentTransform.position = new Vector3f(x, y, z);

                    }
                    if (a.Contains("axisAngles"))
                    {
                        string[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        if (currentTransform.axisAngles.X == 0 &&
                            currentTransform.axisAngles.Y == 0 &&
                            currentTransform.axisAngles.Z == 0)
                            currentTransform.axisAngles = new Vector3f(x, y, z);
                    }
                    if (a.Contains("scale"))
                    {
                        string[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        if (currentTransform.scale.X == 1 &&
                            currentTransform.scale.Y == 1 &&
                            currentTransform.scale.Z == 1)
                            currentTransform.scale = new Vector3f(x, y, z);
                    }
                    if (a.Contains("size"))
                    {
                        string[] split = b.Split(',');
                        int x = (int)float.Parse(split[0]);
                        int y = (int)float.Parse(split[1]);
                        int z = (int)float.Parse(split[2]);
                        if (currentTransform.size.X == 0 &&
                            currentTransform.size.Y == 0 &&
                            currentTransform.size.Z == 0)
                            currentTransform.size = new Vector3f(x, y, z);
                    }
                    if (a.Contains("uv"))
                    {
                        string[] split = b.Split(',');
                        int x = (int)float.Parse(split[0]);
                        int y = (int)float.Parse(split[1]);
                        if (currentTransform.uv.X == 0 &&
                            currentTransform.uv.Y == 0)
                            currentTransform.uv = new Vector2(x, y);
                    }
                    if (a.Contains("locked"))
                    {
                        currentTransform.locked = bool.Parse(b);
                    }
                    if (a.Contains("visible"))
                    {
                        currentTransform.locked = bool.Parse(b);
                    }
                }
            }
            return frames;
            
        }

        public static GameModel LoadModel(ResourcePath path, Texture2D texture)
        {
            GameModel model = new GameModel();

            Console.WriteLine("Object = " + path);
            Console.WriteLine("Id = " + path.modid);
            Console.WriteLine("Path = " + path.path);
            Console.WriteLine("Root = " + path.root);

            string str = FileUtils.LoadFileAsString(path);
            List<Part> parts = new List<Part>();

            string[] lines = str.Split('\n');
            Part part = null;
            foreach (string s in lines)
            {
                if (s.Trim().Equals(string.Empty))
                {
                    continue;
                }
                if (s.StartsWith("Part"))
                {
                    part = new Part(model);
                }
                else
                {
                    String[] data = s.Trim().Split(' ');
                    if (data[0].Contains("Position"))
                    {
                        
                        float x = (float)double.Parse(data[1]);
                        float y = (float)double.Parse(data[2]);
                        float z = (float)double.Parse(data[3]);
                        part.SetPosition(new Vector3f((float)x, (float)y, (float)z));
                    }
                    else
                    if (data[0].Contains("Rotation"))
                    {
                        float x = (float)double.Parse(data[1]);
                        float y = (float)double.Parse(data[2]);
                        float z = (float)double.Parse(data[3]);
                        float w = (float)double.Parse(data[4]);
                        part.SetRotation(new Quaternionf(x, y, z, w));
                    }
                    else
                    if (data[0].Contains("Size"))
                    {
                        int x = (int)int.Parse(data[1]);
                        int y = (int)int.Parse(data[2]);
                        int z = (int)int.Parse(data[3]);
                        part.size = new Vector3f(x, y, z);
                    }
                    else
                    if (data[0].Contains("Scale"))
                    {
                        float x = (float)double.Parse(data[1]);
                        float y = (float)double.Parse(data[2]);
                        float z = (float)double.Parse(data[3]);
                        part.SetScale(new Vector3f(x, y, z));
                    }
                    else
                    if (data[0].Contains("Angles"))
                    {
                        float x = (float)double.Parse(data[1]);
                        float y = (float)double.Parse(data[2]);
                        float z = (float)double.Parse(data[3]);
                        part.axisAngles = new Vector3f(x, y, z);
                    }
                    else
                    if (data[0].Contains("Locked"))
                    {
                        part.locked = bool.Parse(data[1]);
                    }
                    else
                    if (data[0].Contains("Visible"))
                    {
                        part.visible = bool.Parse(data[1]);
                    }
                    else
                    if (data[0].Contains("Origin"))
                    {
                        float x = (float)double.Parse(data[1]);
                        float y = (float)double.Parse(data[2]);
                        float z = (float)double.Parse(data[3]);
                        part.origin = new Vector3f(x, y, z);
                    }
                    else
                    if (data[0].Contains("UV"))
                    {
                        int x = (int)(float)double.Parse(data[1]);
                        int y = (int)(float)double.Parse(data[2]);
                        part.uv = new Vector2(x, y);
                        part.BuildPart(texture);
                        parts.Add(part);
                    }
                    else
                    if (data[0].Contains("Parent"))
                    {
                        int a = int.Parse(data[1]);
                        int b = int.Parse(data[2]);
                        parts[a].parent = parts[b];
                        parts[b].children.Add(parts[a]);
                    }
                    else
                    {
                        if (part.name.Equals("Part"))
                        part.name = s;
                    }
                }
            }
            model.Parts.Clear();
            model.Parts.AddRange(parts);
            model.ChangeTexture(texture);
            return model;
        }

        public void ChangeTexture(Texture2D texture)
        {
            foreach (Part part in Parts)
            {
                part.ChangeTexture(texture);
            }
            this.texture = texture;
        }

        public void Render(GraphicsDevice device, GameEffect effect, GameTime time, bool updateAnimation = true)
        {
            float delta = (float)time.ElapsedGameTime.TotalSeconds * 60;

            if (updateAnimation)
            {
                if (currentTime >= timeline.Count)
                {
                    currentTime -= timeline.Count;
                }
                if (currentTime < 0)
                {
                    currentTime += timeline.Count;
                }
                if (IsPlaying())
                {
                    currentTime += animationSpeed * (float)timeline[GetCurrentFrame()].speed * delta;
                }
                if (currentTime >= timeline.Count)
                {
                    currentTime -= timeline.Count;
                }
                if (currentTime < 0)
                {
                    currentTime += timeline.Count;
                }
            }

            editMode = EditMode.ANIMATION;

            foreach (Part part in Parts)
            {
                if (part.RenderRotation == null)
                {
                    part.RenderRotation = part.GetRotation();
                }
                part.RenderRotation.Rotation = Quaternion.Lerp(part.RenderRotation.Rotation, part.GetRotation().Rotation, 0.05f);
            }

            editMode = EditMode.MODEL;
            foreach (string str in extra_rotations.Keys)
            {
                extra_rotations.TryGetValue(str, out Vector3 val);

                foreach (Part part in Parts)
                {
                    if (part.name.Equals(str))
                    {
                        
                        part.Rotate(new Vector3f(val.X, 0, 0));
                        part.Rotate(new Vector3f(0, val.Y, 0));
                        part.Rotate(new Vector3f(0, 0, val.Z));
                    }
                }
            }
            editMode = EditMode.ANIMATION;

            foreach (Part part in Parts)
            {
                part.Render(device, effect);
            }
            editMode = EditMode.MODEL;
            foreach (string str in extra_rotations.Keys)
            {
                extra_rotations.TryGetValue(str, out Vector3 val);

                foreach (Part part in Parts)
                {
                    if (part.name.Equals(str))
                    {
                        part.Rotate(new Vector3f(0, 0, -val.Z));
                        part.Rotate(new Vector3f(0, -val.Y, 0));
                        part.Rotate(new Vector3f(-val.X, 0, 0));

                    }
                }
            }
            editMode = EditMode.ANIMATION;
        }

        public void Dispose()
        {
            foreach (Part part in this.Parts)
            {
                part.Dispose();
            }
        }

    }
}
