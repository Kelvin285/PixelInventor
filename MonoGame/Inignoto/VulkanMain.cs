using Inignoto.GameSettings;
using Inignoto.Graphics.Mesh;
using Inignoto.Math;
using Microsoft.Xna.Framework;
using SharpDX.XAudio2;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Numerics;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;
using Vector3 = Microsoft.Xna.Framework.Vector3;

namespace Inignoto
{
    public class VulkanMain
    {


        public struct Vec3f
        {
            public float x;
            public float y;
            public float z;
            public Vec3f(float x, float y, float z)
            {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public void Set(float x, float y, float z)
            {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public void Set(Vector3f vec)
            {
                x = vec.X;
                y = vec.Y;
                z = vec.Z;
            }

            public void Set(Vector3 vec)
            {
                x = vec.X;
                y = vec.Y;
                z = vec.Z;
            }
        };

        public struct InignotoCamera
        {
            public Vec3f position;
            public Vec3f look;
            public Vec3f up;

            public InignotoCamera(Vec3f position, Vec3f look, Vec3f up)
            {
                this.position = position;
                this.look = look;
                this.up = up;
            }
        };

        public struct InignotoMesh
        {
            public int obj_id;
            public Vec3f position;
            public Vec3f rotation;
            public Vec3f scale;
        };

        public struct InignotoColor
        {
            public float r;
            public float g;
            public float b;
            public float a;
            public InignotoColor(float r, float g, float b, float a)
            {
                this.r = r;
                this.g = g;
                this.b = b;
                this.a = a;
            }
        }



        [DllImport("vk_ray_tracing__simple_KHR.dll", CharSet = CharSet.Unicode)]
        public static extern Vec3f GetMousePos();

        [DllImport("vk_ray_tracing__simple_KHR.dll", CharSet = CharSet.Unicode)]
        public static extern void CallMain();

        [DllImport("vk_ray_tracing__simple_KHR.dll", CharSet = CharSet.Unicode)]
        public static extern void UpdateCamera(InignotoCamera camera);

        [DllImport("vk_ray_tracing__simple_KHR.dll", CharSet = CharSet.Unicode)]
        public static extern bool IsKeyDown(int key);

        [DllImport("vk_ray_tracing__simple_KHR.dll", CharSet = CharSet.Unicode)]
        public static extern bool IsMouseButtonDown(int mouse_button);

        [DllImport("vk_ray_tracing__simple_KHR.dll", CharSet = CharSet.Unicode)]
        public static extern InignotoMesh CreateMesh(VertexPositionLightTexture[] verts, int v);

        [DllImport("vk_ray_tracing__simple_KHR.dll", CharSet = CharSet.Unicode)]
        public static extern void DeleteMesh(int obj_id);

        public static bool UpdateVulkan = false;
        public static InignotoCamera camera = new InignotoCamera(new Vec3f(0, 0, 0), new Vec3f(0, 0, 0), new Vec3f(0, 0, 0));


        public static void UpdateCamera()
        {
            Vec3f mousePos = GetMousePos();
            camera.look.Set(Inignoto.game.camera.Forward.Mul(-1));
            //camera.position.Set(Inignoto.game.camera.position);
            camera.position.Set(0, 5, 0);
            camera.up.Set(Vector3.Up);
            UpdateCamera(camera);
        }

        public VulkanMain()
        {
            
            //ThreadStart vulkan_thread_start = new ThreadStart(CallMain);
            //Thread vulkan_thread = new Thread(vulkan_thread_start);
            //vulkan_thread.IsBackground = true;

            //vulkan_thread.Start();
            

            //UpdateVulkan = true;
            Inignoto.game.VULKAN_ENABLED = false;
        }

        public void Run()
        {
            CallMain();
        }
    }
}
