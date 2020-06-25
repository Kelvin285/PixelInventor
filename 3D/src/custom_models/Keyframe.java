package custom_models;

import java.util.HashMap;

public class Keyframe {
	public HashMap<Part, KeyTransformation> transformations = new HashMap<Part, KeyTransformation>();
	public int time = 0;
	public Keyframe(int time) {
		this.time = time;
	}
	
	public Keyframe copy() {
		Keyframe k = new Keyframe(time);
		for (Part part : transformations.keySet()) {
			KeyTransformation kt = transformations.get(part);
			k.transformations.put(part, kt.copy());
		}
		return k;
	}
}
