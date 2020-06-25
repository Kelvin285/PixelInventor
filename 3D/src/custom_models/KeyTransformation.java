package custom_models;

import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.Inignoto.resources.raytracer.Raytracer;

public class KeyTransformation {
	public static final float SCALING = 1.0f / 32.0f;
	
	public Vector3f position = new Vector3f(0, 0, 0);
	public Quaternionf rotation = new Quaternionf().identity();
	public Vector3f scale = new Vector3f(0, 0, 0);
	public Vector3i size = new Vector3i(0, 0, 0);
	public Vector2i uv = new Vector2i(0, 0);
	public Vector3f origin = new Vector3f(0, 0, 0);
		
	public boolean visible = true;
	public boolean locked = true;
		
	public Vector3f look = new Vector3f(0, 0, 1);
	
	public Vector3f axisAngles = new Vector3f(0, 0, 0);
	
	
	public void translate(Vector3f translation) {
		this.position.add(translation);
	}
	
	public void rotate(Vector3f rotation) {
		rotate(rotation, new Vector3f(this.position).add(new Vector3f(this.origin).rotate(this.rotation)));
	}
	
	public void rotate(Vector3f rotation, Vector3f origin) {
		
		this.axisAngles.add(rotation);
		
        this.rotation.rotateLocalX((float)Math.toRadians(rotation.x));
        this.rotation.rotateLocalY((float)Math.toRadians(rotation.y));
        this.rotation.rotateLocalZ((float)Math.toRadians(rotation.z));
                
        Quaternionf nRot = new Quaternionf().identity();
        nRot.rotateLocalX((float)Math.toRadians(rotation.x));
        nRot.rotateLocalY((float)Math.toRadians(rotation.y));
        nRot.rotateLocalZ((float)Math.toRadians(rotation.z));
        
       
        this.position = Raytracer.rotateAround(this.position, origin, nRot);
        
    }
	
	public void setRotation(Vector3f rotation) {
		rotate(new Vector3f(rotation).mul(-1));
		this.axisAngles = new Vector3f(0, 0, 0);
		this.rotation.identity();
		rotate(rotation);
	}
	
	public KeyTransformation copy() {
		KeyTransformation p = new KeyTransformation();
		p.position = new Vector3f(position);
		p.rotation = new Quaternionf(rotation);
		p.scale = new Vector3f(scale);
		p.size = new Vector3i(size);
		p.uv = new Vector2i(uv);
		p.origin = new Vector3f(origin);
		return p;
	}
}
