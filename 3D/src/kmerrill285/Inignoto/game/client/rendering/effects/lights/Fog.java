package kmerrill285.Inignoto.game.client.rendering.effects.lights;

import org.joml.Vector3f;

public class Fog {

	public boolean active;

    public Vector3f color;

    public float density;

    public static Fog NOFOG = new Fog();

    public Fog() {
        active = false;
        this.color = new Vector3f(0, 0, 0);
        this.density = 0;
    }

    public Fog(boolean active, Vector3f color, float density) {
        this.color = color;
        this.density = density;
        this.active = active;
    }
}
