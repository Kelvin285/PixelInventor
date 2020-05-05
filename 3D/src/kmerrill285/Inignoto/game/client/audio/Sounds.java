package kmerrill285.Inignoto.game.client.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;

import imported.WaveData;
import kmerrill285.Inignoto.game.client.Camera;

public class Sounds {
	
	public static List<Integer> buffers = new ArrayList<Integer>();
	
	public static void initAL() {
		long device = ALC11.alcOpenDevice((ByteBuffer)null);
		if(device == 0L){
			System.out.println("Unable to open default audio device");
			return;
		}
		
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		
		if(!deviceCaps.OpenALC10){
			System.out.println("OpenALC10 Unsupported");
			return;
		}
		
		
		long context = ALC11.alcCreateContext(device, (IntBuffer)null);
		if(context == 0L){
			System.out.println("Unable to create ALC Context");
			return;
		}
		
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		TileSound.init();
	}
	
	public static int loadSound(String modid, String file) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData waveFile = WaveData.create(modid, "sounds/"+file);
		AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}
	
	public static void setListenerData() {
		AL10.alListener3f(AL10.AL_POSITION, Camera.position.x, Camera.position.y, Camera.position.z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
	public static void dispose() {
		for (int buffer : buffers) {
			AL10.alDeleteBuffers(buffer);
		}
	}
	
}
