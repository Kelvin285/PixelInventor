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

        public readonly List<Keyframe> timeline = new List<Keyframe>();

        public float currentTime = 0.0f;
        public float animationSpeed = 1.0f / 30.0f;

        public Vector3f rotation = new Vector3f(0, 0, 0);
        public Vector3f translation = new Vector3f(0, 0, 0);
        public Vector3f scale = new Vector3f(1, 1, 1);
        public Vector3f origin = new Vector3f(0, 0, 0);

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

        public static List<Keyframe> LoadAnimation(ResourcePath path)
        {
            List<Keyframe> frames = new List<Keyframe>();
            Keyframe currentKeyframe = null;
            KeyTransformation currentTransform = null;
            
            Dictionary<string, string> data = FileUtils.LoadFileAsDataList(path);

            foreach (string a in data.Keys)
            {
                string b = "";
                data.TryGetValue(a, out b);

                if (a.Length > 0 && b.Length > 0)
                {
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
                    }
                    if (a.Contains("rotation"))
                    {
                        String[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        float w = float.Parse(split[3]);
                        currentTransform.rotation = new Quaternionf(x, y, z, w);
                    }
                    if (a.Contains("look"))
                    {
                        String[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        currentTransform.look = new Vector3f(x, y, z);
                    }
                    if (a.Contains("origin"))
                    {
                        String[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        currentTransform.origin = new Vector3f(x, y, z);
                    }
                    if (a.Contains("position"))
                    {
                        String[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        currentTransform.position = new Vector3f(x, y, z);
                    }
                    if (a.Contains("axisAngles"))
                    {
                        String[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        currentTransform.axisAngles = new Vector3f(x, y, z);
                    }
                    if (a.Contains("scale"))
                    {
                        String[] split = b.Split(',');
                        float x = float.Parse(split[0]);
                        float y = float.Parse(split[1]);
                        float z = float.Parse(split[2]);
                        currentTransform.scale = new Vector3f(x, y, z);
                    }
                    if (a.Contains("size"))
                    {
                        String[] split = b.Split(',');
                        int x = (int)float.Parse(split[0]);
                        int y = (int)float.Parse(split[1]);
                        int z = (int)float.Parse(split[2]);
                        currentTransform.size = new Vector3f(x, y, z);
                    }
                    if (a.Contains("uv"))
                    {
                        String[] split = b.Split(',');
                        int x = (int)float.Parse(split[0]);
                        int y = (int)float.Parse(split[1]);
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
            
            string str = FileUtils.LoadFileAsString(path);
            List<Part> parts = new List<Part>();

            string[] lines = str.Split('\n');
            Part part = null;
            foreach (string s in lines)
            {
                if (s.StartsWith("Part"))
                {
                    part = new Part(model);
                }
                else
                {
                    String[] data = s.Trim().Split(' ');
                    if (data[0].Contains("Position"))
                    {
                        float x = float.Parse(data[1]);
                        float y = float.Parse(data[2]);
                        float z = float.Parse(data[3]);
                        part.SetPosition(new Vector3f(x, y, z));
                    }
                    else
                    if (data[0].Contains("Rotation"))
                    {
                        float x = float.Parse(data[1]);
                        float y = float.Parse(data[2]);
                        float z = float.Parse(data[3]);
                        float w = float.Parse(data[4]);
                        part.SetRotation(new Quaternionf(x, y, z, w));
                    }
                    else
                    if (data[0].Contains("Size"))
                    {
                        int x = (int)float.Parse(data[1]);
                        int y = (int)float.Parse(data[2]);
                        int z = (int)float.Parse(data[3]);
                        part.size = new Vector3f(x, y, z);
                    }
                    else
                    if (data[0].Contains("Scale"))
                    {
                        float x = float.Parse(data[1]);
                        float y = float.Parse(data[2]);
                        float z = float.Parse(data[3]);
                        part.SetScale(new Vector3f(x, y, z));
                    }
                    else
                    if (data[0].Contains("Angles"))
                    {
                        float x = float.Parse(data[1]);
                        float y = float.Parse(data[2]);
                        float z = float.Parse(data[3]);
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
                        float x = float.Parse(data[1]);
                        float y = float.Parse(data[2]);
                        float z = float.Parse(data[3]);
                        part.origin = new Vector3f(x, y, z);
                    }
                    else
                    if (data[0].Contains("UV"))
                    {
                        int x = (int)float.Parse(data[1]);
                        int y = (int)float.Parse(data[2]);
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
                        part.name = s;
                    }
                }
            }
            model.Parts.Clear();
            model.Parts.AddRange(parts);
            
            return model;
        }

        public void ChangeTexture(Texture2D texture)
        {
            foreach (Part part in Parts)
            {
                part.ChangeTexture(texture);
            }
        }

        public void Render(GraphicsDevice device, BasicEffect effect)
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
                currentTime += animationSpeed * (float)timeline[GetCurrentFrame()].speed;
            }
            if (currentTime >= timeline.Count)
            {
                currentTime -= timeline.Count;
            }
            if (currentTime < 0)
            {
                currentTime += timeline.Count;
            }
            List<Part> p = new List<Part>();
            foreach (Part part in Parts)
            {
                if (part.parent == null)
                {
                    p.Add(part);
                }
            }

            foreach (Part part in p)
            {
                part.Rotate(new Vector3f(rotation.X, 0, 0), origin);
                part.Rotate(new Vector3f(0, rotation.Y, 0), origin);
                part.Rotate(new Vector3f(0, 0, rotation.Z), origin);
            }

            foreach (Part part in Parts)
            {
                part.Render(device, effect);
            }
            foreach (Part part in p)
            {
                part.Rotate(new Vector3f(0, 0, -rotation.Z), origin);
                part.Rotate(new Vector3f(0, -rotation.Y, 0), origin);
                part.Rotate(new Vector3f(-rotation.X, 0, 0), origin);
            }
        }

    }
}
