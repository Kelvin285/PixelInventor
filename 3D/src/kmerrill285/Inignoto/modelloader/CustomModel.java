package kmerrill285.Inignoto.modelloader;

import java.util.ArrayList;
import java.util.HashMap;

import javax.rmi.CORBA.Util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.modelloader.animation.Animation;
import kmerrill285.Inignoto.modelloader.animation.AnimationController;
import kmerrill285.Inignoto.modelloader.animation.AnimationFrame;
import kmerrill285.Inignoto.modelloader.animation.AnimationFrameData;
import kmerrill285.Inignoto.resources.MathHelper;

public class CustomModel {
	public AnimationController controller;
	private AnimModel model;
	
	private HashMap<String, Mesh> meshes = new HashMap<String, Mesh>();
	public HashMap<String, Vector3f> extraRotations = new HashMap<String, Vector3f>();
	
	private Texture texture;
	
	public CustomModel(AnimModel model, Texture texture) {
		this.model = model;
		this.controller = new AnimationController();
		
		this.texture = texture;
		
		/*
		 * Front: 
		 * U + size_x, V + size_y (top left)
		 */
		
		for (String str : model.parts.keySet()) {
			ModelPart part = model.parts.get(str);
			ArrayList<Vertex> vertexCoords = part.vertexCoords;
			float U = part.transformation.U;
			float V = part.transformation.V;
			float size_x = part.transformation.size_x;
			float size_y = part.transformation.size_y;
			float size_z = part.transformation.size_z;
			float offsX = part.transformation.offsX;
			float offsY = part.transformation.offsY;
			float offsZ = part.transformation.offsZ;
			
			boolean multV = false;
			
			if (vertexCoords.size() == 0) {
				
				vertexCoords.add(new Vertex(0, size_y, 0));
				vertexCoords.add(new Vertex(size_x, size_y, 0));
				vertexCoords.add(new Vertex(size_x, 0, 0));
				vertexCoords.add(new Vertex(0, 0, 0));
				
				vertexCoords.add(new Vertex(0, size_y, size_z));
				vertexCoords.add(new Vertex(size_x, size_y, size_z));
				vertexCoords.add(new Vertex(size_x, 0, size_z));
				vertexCoords.add(new Vertex(0, 0, size_z));
			} else {
				multV = true;
			}
			
			ArrayList<Vertex> verts = new ArrayList<Vertex>();
			
			verts.add(new Vertex(0, 0, 0));
			verts.add(new Vertex(0, 1, 0));
			verts.add(new Vertex(1, 1, 0));
			verts.add(new Vertex(1, 0, 0));
			
			verts.add(new Vertex(0, 0, 1));
			verts.add(new Vertex(0, 1, 1));
			verts.add(new Vertex(1, 1, 1));
			verts.add(new Vertex(1, 0, 1));
			
			verts.add(new Vertex(0, 0, 1));
			verts.add(new Vertex(0, 1, 1));
			verts.add(new Vertex(0, 1, 0));
			verts.add(new Vertex(0, 0, 0));
			
			verts.add(new Vertex(1, 0, 0));
			verts.add(new Vertex(1, 1, 0));
			verts.add(new Vertex(1, 1, 1));
			verts.add(new Vertex(1, 0, 1));
			
			verts.add(new Vertex(0, 1, 0));
			verts.add(new Vertex(0, 1, 1));
			verts.add(new Vertex(1, 1, 1));
			verts.add(new Vertex(1, 1, 0));
			
			verts.add(new Vertex(0, 0, 1));
			verts.add(new Vertex(0, 0, 0));
			verts.add(new Vertex(1, 0, 0));
			verts.add(new Vertex(1, 0, 1));
			
			int[] indices = {
				0, 1, 2, 2, 3, 0,
				4, 5, 6, 6, 7, 4,
				8, 9, 10, 10, 11, 8,
				12, 13, 14, 14, 15, 12,
				16, 17, 18, 18, 19, 16,
				20, 21, 22, 22, 23, 20
			};
			
			float[] texCoords = {
				//front
				U + size_z, V + size_z + size_y,
				U + size_z, V + size_z,
				U + size_z + size_x, V + size_z,
				U + size_z + size_x, V + size_z + size_y,
				//back
				U + size_z + size_x + size_z, V + size_z + size_y,
				U + size_z + size_x + size_z, V + size_z,
				U + size_z + size_x + size_x + size_z, V + size_z,
				U + size_z + size_x + size_x + size_z, V + size_z + size_y,
				//left
				U, V + size_z + size_y,
				U, V + size_z,
				U + size_z, V + size_z,
				U + size_z, V + size_z + size_y,
				//right
				U + size_z + size_x, V + size_z + size_y,
				U + size_z + size_x, V + size_z,
				U + size_z * 2 + size_x, V + size_z,
				U + size_z * 2 + size_x, V + size_z + size_y,
				//top
				U + size_z, V + size_z,
				U + size_z, V,
				U + size_z + size_x, V,
				U + size_z + size_x, V + size_z,
				//bottom
				U + size_z + size_x, V + size_z,
				U + size_z + size_x, V,
				U + size_z + size_x + size_x, V,
				U + size_z + size_x + size_x, V + size_z,
				
			};
			
			for (int i = 0; i < texCoords.length; i++) {
				texCoords[i] /= (float)texture.getWidth();
				i++;
				texCoords[i] /= (float)texture.getHeight();
			}
			
			float[] vertices = new float[verts.size() * 3];

			//5
			for (int i = 0; i < verts.size(); i++) {
				int J = i * 3;
				if (!multV) {
					Vector3f v = new Vector3f(verts.get(i).x, verts.get(i).y, verts.get(i).z).sub(0.5f, 0.5f, 0.5f).mul(size_x, size_y, size_z);
					vertices[J] = v.x;
					vertices[J + 1] = v.y;
					vertices[J + 2] = v.z;
				} else {
					Vertex p5 = vertexCoords.get(5);
					Vector3f v = new Vector3f(verts.get(i).x, verts.get(i).y, verts.get(i).z).sub(0.5f, 0.5f, 0.5f).mul(p5.x * 2, p5.y * 2, p5.z * 2);
					vertices[J] = v.x;
					vertices[J + 1] = v.y;
					vertices[J + 2] = v.z;
				}
			}
			
			Mesh mesh = new Mesh(vertices, texCoords, indices, texture);
			meshes.put(str, mesh);
		}
	}
	
	public void render(Vector3f position, Vector3f scale, Vector3f rotation, ShaderProgram shader) {
		
		for (String str : meshes.keySet()) {
			ModelPart part = model.parts.get(str);
			Matrix4f first = new Matrix4f();
//			first.rotateZ((float)Math.toRadians(rotation.z));
//			first.rotateY((float)Math.toRadians(rotation.y));
//			first.rotateX((float)Math.toRadians(rotation.x));
			first.rotateYXZ(
					(float)Math.toRadians(rotation.y),
					(float)Math.toRadians(rotation.x),
					(float)Math.toRadians(rotation.z)
					);
			if (part.parent == null) {
				//renderMesh(part, position, scale, new Vector3f(0, 0, 0), shader, rotation, position);
				renderMesh(part, new Matrix4f(first), shader, new Vector3f(position), new Vector3f(rotation), new Vector3f(0, 0, 0));
			}
			
		}
	}
	

	public void renderMesh(ModelPart part, Matrix4f matrix, ShaderProgram shader, Vector3f truePos, Vector3f trueRot, Vector3f renderRot) {
		Mesh mesh = meshes.get(part.name);
		ModelTransformation transform = part.transformation;
		
		Vertex framePos = getPositionForFrame(part, controller.currentAnimation);
		Vertex frameRot = getRotationForFrame(part, controller.currentAnimation);
		
		
		float scale = 0.055f;
		

		Vector3f pos = new Vector3f(transform.x, transform.y, transform.z);

		Vector3f offset = new Vector3f(transform.offsX, transform.offsY, transform.offsZ);
		
		pos.add(offset.x, offset.y, offset.z);
				
		Vector3f r = new Vector3f(0, 0, 0);
		if (extraRotations.get(part.name) != null)
		r.add(extraRotations.get(part.name));
		
		matrix.translate(pos.x + framePos.x, pos.y + framePos.y, pos.z + framePos.z);
		matrix.rotateYXZ(
				(float)Math.toRadians(transform.rotY + frameRot.y + r.y),
				(float)Math.toRadians(transform.rotX + frameRot.x + r.x),
				(float)Math.toRadians(transform.rotZ + frameRot.z + r.z)
				);
		
		renderRot.add(transform.rotX + frameRot.x + r.x, transform.rotY + frameRot.y + r.y, transform.rotZ + frameRot.z + r.z);
		
		Vector3f translationDest = new Vector3f(0, 0, 0);
		
		Vector3f rotationDest = new Vector3f(0, 0, 0);
		
		new Matrix4f(matrix).getTranslation(translationDest);
		new Matrix4f(matrix).getEulerAnglesZYX(rotationDest);
		rotationDest.x = (float)Math.toDegrees(rotationDest.x);
		rotationDest.y = (float)Math.toDegrees(rotationDest.y);
		rotationDest.z = (float)Math.toDegrees(rotationDest.z);
		
		MeshRenderer.renderMesh(mesh, new Vector3f(truePos).add(translationDest.mul(scale)), new Vector3f(renderRot).add(trueRot), new Vector3f(1, 1, -1).mul(scale), shader);
		
		
		for (ModelPart child : part.children) {
			renderMesh(child, new Matrix4f(matrix), shader, new Vector3f(truePos), new Vector3f(trueRot), new Vector3f(renderRot));
		}
	}
	
	public void renderMesh(ModelPart part, Vector3f lastPos, Vector3f lastScale, Vector3f lastRotation, ShaderProgram shader, Vector3f mainRot, Vector3f mainPos) {
		Mesh mesh = meshes.get(part.name);
		ModelTransformation transform = part.transformation;
		
		float scale = 0.055f;
		
		Vertex framePos = getPositionForFrame(part, controller.currentAnimation);
		Vertex frameRot = getRotationForFrame(part, controller.currentAnimation);
		
		Vector3f pos = new Vector3f(transform.x, transform.y, transform.z);

		Vector3f offset = new Vector3f(transform.offsX, transform.offsY, transform.offsZ);
		
		Vector3f rot = new Vector3f(transform.rotX, transform.rotY, transform.rotZ);
		
		
		if (framePos != null) {
			float mul = 1f;
			pos.add(framePos.x * mul, framePos.y * mul, framePos.z * mul);
		}
		if (frameRot != null) {
			float mul = 1f;
			rot.add(frameRot.x * mul, frameRot.y * mul, frameRot.z * mul);
		}
		
		
		pos.add(offset.x, offset.y, offset.z);
		
		rot.add(lastRotation);		
		if (part.parent != null) {
			float X = pos.x;
			float Y = pos.y;
			float Z = pos.z;
			
			

			
			//rotate X
			{
				final float z = Z;
				final float y = Y;
				Z = (float)(z * Math.cos(Math.toRadians(lastRotation.x)) + y * Math.sin(Math.toRadians(lastRotation.x)));
				Y = (float)(y * Math.cos(Math.toRadians(lastRotation.x)) - z * Math.sin(Math.toRadians(lastRotation.x)));
			}


			//rotate Y
			{
				final float x = X;
				final float z = Z;
				X = (float)(x * Math.cos(Math.toRadians(lastRotation.y)) + z * Math.sin(Math.toRadians(lastRotation.y)));
				Z = (float)(z * Math.cos(Math.toRadians(lastRotation.y)) - x * Math.sin(Math.toRadians(lastRotation.y)));
			}

			
			//rotate Z
			{
				final float x = X;
				final float y = Y;
				X = (float)(x * Math.cos(Math.toRadians(lastRotation.z)) - y * Math.sin(Math.toRadians(lastRotation.z)));
				Y = (float)(y * Math.cos(Math.toRadians(lastRotation.z)) + x * Math.sin(Math.toRadians(lastRotation.z)));
			}
			
			pos.x = X;
			pos.y = Y;
			pos.z = Z;
		}
		pos.mul(scale);
		pos.add(lastPos);
		
		Vector3f newPos = new Vector3f(pos);
//		
//		newPos.sub(mainPos);
//		final double x = newPos.x;
//		final double z = newPos.z;
//		final double X = x * Math.cos(Math.toRadians(mainRot.y)) + z * Math.sin(Math.toRadians(mainRot.y));
//		final double Z = z * Math.cos(Math.toRadians(mainRot.y)) - x * Math.sin(Math.toRadians(mainRot.y));
//		newPos.x = (float)X;
//		newPos.z = (float)Z;
//		newPos.x += mainPos.x;
//		newPos.y += mainPos.y;
//		newPos.z += mainPos.z;
		

		MeshRenderer.renderMesh(mesh, newPos, rot, new Vector3f(1, 1, -1).mul(scale), shader);
		
		for (ModelPart child : part.children) {
			renderMesh(child, new Vector3f(pos), lastScale, rot, shader, mainRot, mainPos);
		}
	}

	public void dispose() {
		for (String str : meshes.keySet()) {
			Mesh mesh = meshes.get(str);
			mesh.dispose();
		}
	}
	

	public Vertex getRotationForFrame(ModelPart part, Animation currentAnimation) 
	{
		Vertex animRot = null;
		Vertex nextRot = null;
		Vertex newAnimRot = null;
		
		AnimationFrame currentRotationFrame = null;
		AnimationFrame nextRotationFrame = null;
		
		if (currentAnimation != null) {
			//get current position frame data
			int iterations = 0;
			for (int i = (int)currentAnimation.currentFrame; i >= -currentAnimation.duration; i--) {
				if (i < 0) {
					i = (int)currentAnimation.duration + 1;
					iterations++;
					if (iterations > 1) break;
				}
				AnimationFrame frame = currentAnimation.getFrameFor(i);
				AnimationFrameData frameData = frame.frameData.get(part.name);
				if (frameData != null) {
					
					if (animRot == null) {
						if (frameData.rotation != null) {
							animRot = new Vertex(frameData.rotation.x + 0, frameData.rotation.y + 0, frameData.rotation.z + 0);
							currentRotationFrame = frame;
						}
					}
				}
			}
			
			iterations = 0;
			for (int i = (int)currentAnimation.currentFrame; i <= currentAnimation.duration * 2; i++) {
				if (i > currentAnimation.duration) {
					i = -1;
					iterations++;
					if (iterations > 1) break;
				}
				AnimationFrame frame = currentAnimation.getNextFrameAfter(i);
				AnimationFrameData frameData = frame.frameData.get(part.name);
				
				
				if (frameData != null) {
					
					if (nextRot == null) {
						if (frameData.rotation != null) {
							nextRot = new Vertex(frameData.rotation.x + 0, frameData.rotation.y + 0, frameData.rotation.z + 0);
							nextRotationFrame = frame;
						}
					}
				}
			}
		}
		
		
		
		double rotx = 0, roty = 0, rotz = 0;
		if (animRot != null) {
			if (nextRot != null) {
				float time = controller.currentAnimation.currentFrame;
				float startTime = currentRotationFrame.time;
				float endTime = nextRotationFrame.time;
				if (endTime < startTime) endTime += controller.currentAnimation.duration;
				float frameLerp = controller.currentAnimation.getFrameLerp(endTime - startTime, endTime - time);
				
				if (endTime - startTime > 0) {
					animRot.x = (float)MathHelper.lerp(animRot.x, nextRot.x, frameLerp);
					animRot.y = (float)MathHelper.lerp(animRot.y, nextRot.y, frameLerp);
					animRot.z = (float)MathHelper.lerp(animRot.z, nextRot.z, frameLerp);
					
					
				}
			}
			
			roty += animRot.y;
			rotx += animRot.x;
			rotz += animRot.z;
			
		}
		return new Vertex((float)rotx, (float)roty, (float)rotz);
	}
	
	public Vertex getPositionForFrame(ModelPart part, Animation currentAnimation) {
		Vertex animPos = null;
		
		Vertex nextPos = null;
		
		
		AnimationFrame currentPositionFrame = null;
		AnimationFrame nextPositionFrame = null;
		
		Vertex newAnimPos = null;
		
		if (currentAnimation != null) {
			//get current position frame data
			int iterations = 0;
			for (int i = (int)currentAnimation.currentFrame; i >= -currentAnimation.duration; i--) {
				if (i < 0) {
					i = (int)currentAnimation.duration + 1;
					iterations++;
					if (iterations > 1) break;
				}
				AnimationFrame frame = currentAnimation.getFrameFor(i);
				AnimationFrameData frameData = frame.frameData.get(part.name);
				if (frameData != null) {
					if (animPos == null) {
						if (frameData.position != null) {
							animPos = new Vertex(frameData.position.x + 0, frameData.position.y + 0, frameData.position.z + 0);
							currentPositionFrame = frame;
						}
					}
				}
			}
			
			iterations = 0;
			for (int i = (int)currentAnimation.currentFrame; i <= currentAnimation.duration * 2; i++) {
				if (i > currentAnimation.duration) {
					i = -1;
					iterations++;
					if (iterations > 1) break;
				}
				AnimationFrame frame = currentAnimation.getNextFrameAfter(i);
				AnimationFrameData frameData = frame.frameData.get(part.name);
				
				
				if (frameData != null) {
					if (nextPos == null) {
						if (frameData.position != null) {
							nextPos = new Vertex(frameData.position.x + 0, frameData.position.y + 0, frameData.position.z + 0);
							nextPositionFrame = frame;
						}
					}
				}
			}
		}
		
		
		Vertex pos = new Vertex(0, 0, 0);
		
		
		
		if (animPos != null) {
			if (nextPos != null) {
				float time = controller.currentAnimation.currentFrame;
				float startTime = currentPositionFrame.time;
				float endTime = nextPositionFrame.time;
				if (endTime < startTime) endTime += controller.currentAnimation.duration;
				float frameLerp = controller.currentAnimation.getFrameLerp(endTime - startTime, endTime - time);
				if (endTime - startTime > 0) {
					animPos.x = (float)MathHelper.lerp(animPos.x, nextPos.x, frameLerp);
					animPos.y = (float)MathHelper.lerp(animPos.y, nextPos.y, frameLerp);
					animPos.z = (float)MathHelper.lerp(animPos.z, nextPos.z, frameLerp);
				}
			}
			
			pos.x += animPos.x;
			pos.y += animPos.y;
			pos.z += animPos.z;
		}
		return pos;
	}
}
