package kmerrill285.Inignoto.resources;

import org.joml.Vector3f;

public class PhysicsHelper {
	public static float DRAG_COEFFICIENT = 1.05f;
	public static float GRAVITY = 9.8f;
	public static Vector3f GRAVITY_VECTOR = new Vector3f(0, -GRAVITY, 0);
	public static float calculateDrag (float density, float velocity, float area) {
		return 0.5f * density * velocity * velocity * DRAG_COEFFICIENT * area;
	}

	public static float calculateFriction(float movement_force, float acceleration, float normal_force, float mass) {
		float normal_acceleration = normal_force / mass;
		float coefficient = acceleration / normal_acceleration;
		return coefficient * movement_force;
	}
	
	public static float calculateVerticalFriction(float movement_force, float acceleration) {
		float normal_acceleration = GRAVITY;
		float coefficient = acceleration / normal_acceleration;
		return coefficient * movement_force;
	}
	
	public static float calculateRestitution(float velocity, float coefficient) {
		return velocity * coefficient;
	}
	public static float calculateGravity(float mass) {
		return (float)(GRAVITY * mass * TPSCounter.getTrueDelta());
	}
	
	public static void applyGravity(Vector3f velocity) 
	{
		velocity.add(new Vector3f(GRAVITY_VECTOR).mul((float)TPSCounter.getTrueDelta()));
	}
	
	public static void applyDrag(Vector3f velocity, Vector3f size, float mass, float fluid_density) {
		float dragX = calculateDrag(fluid_density, velocity.x, size.z * size.y) * -Math.signum(velocity.x);
		float dragY = calculateDrag(fluid_density, velocity.y, size.z * size.x) * -Math.signum(velocity.y);
		float dragZ = calculateDrag(fluid_density, velocity.z, size.x * size.y) * -Math.signum(velocity.z);
		Vector3f drag = new Vector3f(dragX, dragY, dragZ);
		applyForce(velocity, drag, 1.0f);
	}
		
	public static void applyForceWithFriction(Vector3f velocity, Vector3f force, float mass, Vector3f frictionAxis) {
		float ax = frictionAxis.x * (force.x / mass);
		float ay = frictionAxis.y * (force.y / mass);
		float az = frictionAxis.z * (force.z / mass);
		
		float fx = frictionAxis.z * calculateFriction(force.x, (float)Math.abs(az), (float)Math.abs(force.z), mass) +
					frictionAxis.y * calculateFriction(force.x, (float)Math.abs(ay), (float)Math.abs(force.y), mass);
		float fy = frictionAxis.z * calculateFriction(force.y, (float)Math.abs(az), (float)Math.abs(force.z), mass) +
				frictionAxis.x * calculateFriction(force.y, (float)Math.abs(ax), (float)Math.abs(force.x), mass);
		float fz = frictionAxis.x * calculateFriction(force.z, (float)Math.abs(ax), (float)Math.abs(force.x), mass) +
				frictionAxis.y * calculateFriction(force.z, (float)Math.abs(ay), (float)Math.abs(force.y), mass);
		
		velocity.add(new Vector3f(force).add(-fx, -fy, -fz).div(mass).mul((float)TPSCounter.getTrueDelta()));
	}
	
	public static void applyForce(Vector3f velocity, Vector3f force, float mass) {
		velocity.add(new Vector3f(force).div(mass).mul((float)TPSCounter.getTrueDelta() * 3));
	}
}
