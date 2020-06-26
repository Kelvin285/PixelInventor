package custom_models;

import java.io.Serializable;
import java.util.HashMap;

public class Keyframe implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, KeyTransformation> transformations = new HashMap<Integer, KeyTransformation>();
	public int time = 0;
	public double speed = 1.0f;
	public Keyframe(int time) {
		this.time = time;
	}
	
	public Keyframe copy() {
		Keyframe k = new Keyframe(time);
		k.speed = speed;
		for (Integer part : transformations.keySet()) {
			KeyTransformation kt = transformations.get(part);
			k.transformations.put(part, kt.copy());
		}
		return k;
	}

	public void paste(Keyframe k) {
		speed = k.speed;
		transformations.clear();
		for (Integer part : k.transformations.keySet()) {
			KeyTransformation kt = k.transformations.get(part);
			transformations.put(part, kt.copy());
		}
	}
}
