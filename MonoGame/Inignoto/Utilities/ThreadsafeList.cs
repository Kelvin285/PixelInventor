using Microsoft.VisualBasic.CompilerServices;
using System;
using System.Collections.Generic;
using System.Text;

namespace Inignoto.Utilities
{
    public class ThreadsafeList<T>
    {
        T[] current = new T[0];
        T[] last = new T[0];

        public T this[int index] { get => Get(index); }

        public int Count => current.Length;

        public T Get(int i)
        {
            lock (current)
            {
                if (i < current.Length)
                {
                    return current[i];
                }
                else
                {
                    if (i < last.Length)
                    {
                        return last[i];
                    }
                }
            }
            
            return default(T);
        }

        public void Add(T t)
        {
            bool changed = false;
            T[] next;
            lock (current)
            {
                last = current;
                next = new T[current.Length + 1];
                for (int i = 0; i < current.Length; i++)
                {
                    next[i] = current[i];
                    changed = true;
                }
                next[current.Length] = t;
            }
            if (changed) current = next;
        }

        public void RemoveAt(int index)
        {
            T[] next;
            bool changed = false;
            lock (current)
            {
                last = current;
                next = new T[current.Length - 1];
                for (int i = 0; i < current.Length; i++)
                {
                    if (i < index)
                    {
                        next[i] = current[i];
                    }
                    else if (i > index)
                    {
                        next[i - 1] = current[i];
                    }
                }
            }
            if (changed) current = next;
        }
    }
}
