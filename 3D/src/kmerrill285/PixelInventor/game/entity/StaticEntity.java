package kmerrill285.PixelInventor.game.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;

public class StaticEntity {
	public Vector3f position;
	public float pitch, yaw;
	public Chunk chunk;
	public float width, height;
	public int ticksExisted = 0;
	
	public int type = 0;
	
	public Vector3f size;
		
	public boolean isDead = false;
	
	public Vector3f offset = new Vector3f(0, 0, 0);
	
	public Mesh mesh;
	
	public float renderDistance = 100;
	
	public StaticEntity(Vector3f position, Vector3f size, Chunk chunk, int type) {
		this.position = position;
		this.chunk = chunk;
		this.size = size;
		this.type = type;
	}
	
	public StaticEntity(int type) {
		this.type = type;
		this.position = new Vector3f(0, 0, 0);
		this.size = new Vector3f(0, 0, 0);
	}
	
	public StaticEntity create(Chunk chunk) {
		StaticEntity entity = null;
		try {
			entity = this.getClass().getConstructor(Vector3f.class, Vector3f.class, Chunk.class, int.class).newInstance(new Vector3f(position), new Vector3f(size), chunk, this.type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (entity != null) {
			entity.pitch = pitch;
			entity.yaw = yaw;
			entity.ticksExisted = ticksExisted;
			entity.isDead = isDead;
		}
		return entity;
	}
	
	public void tick() {
		ticksExisted++;
	}
	
	public void render(ShaderProgram shader) {
		if (mesh != null)
		if (position.distance(Camera.position) <= renderDistance)
		MeshRenderer.renderMesh(mesh, new Vector3f(position).add(offset), new Vector3f(pitch, yaw, 0), size, shader);
	}
	
	public void renderShadow(ShaderProgram shader, Matrix4f view) {
		if (mesh != null)
		if (position.distance(Camera.position) <= renderDistance)
		MeshRenderer.renderShadowMesh(mesh, new Vector3f(position).add(offset), new Vector3f(pitch, yaw, 0), size, shader, view);
	}
	
	public void dispose() {
		
	}
	
	public TilePos getTilePos() {
		return new TilePos(position.x, position.y, position.z);
	}
	
	public String getSaveData() {
		String str = "";
		str += "se:"+type+","+position.x+","+position.y+","+position.z+","+pitch+","+yaw+","+ticksExisted+","+isDead;
		return str;
	}
	
	public void load(String line) {
		String[] data = line.split(",");
		type = Integer.parseInt(data[0]);
		position.x = Float.parseFloat(data[1]);
		position.y = Float.parseFloat(data[2]);
		position.z = Float.parseFloat(data[3]);
		pitch = Float.parseFloat(data[4]);
		yaw = Float.parseFloat(data[5]);
		ticksExisted = Integer.parseInt(data[6]);
		isDead = Boolean.parseBoolean(data[7]);
	}
}
