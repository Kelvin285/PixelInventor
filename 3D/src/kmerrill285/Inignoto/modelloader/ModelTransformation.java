package kmerrill285.Inignoto.modelloader;

public class ModelTransformation {
	public float x, y, z;
	public float size_x, size_y, size_z;
	public float offsX, offsY, offsZ;
	public float rotX, rotY, rotZ;
	public float U, V;
	
	public ModelTransformation(float x, float y, float z, float size_x, float size_y, float size_z,
			float offsX, float offsY, float offsZ,
			float rotX, float rotY, float rotZ,
			float U, float V) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size_x = size_x;
		this.size_y = size_y;
		this.size_z = size_z;
		this.offsX = offsX;
		this.offsY = offsY;
		this.offsZ = offsZ;
		this.U = U;
		this.V = V;
	}
	
	public ModelTransformation(ModelTransformation last) {
		this.x = last.x;
		this.y = last.y;
		this.z = last.z;
		this.size_x = last.size_x;
		this.size_y = last.size_y;
		this.size_z = last.size_z;
		this.offsX = last.offsX;
		this.offsY = last.offsY;
		this.offsZ = last.offsZ;
		this.rotX = last.rotX;
		this.rotY = last.rotY;
		this.rotZ = last.rotZ;
		this.U = last.U;
		this.V = last.V;
	}
	
	public ModelTransformation() {}
}
