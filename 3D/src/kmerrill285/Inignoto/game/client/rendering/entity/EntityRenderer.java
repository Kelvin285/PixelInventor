package kmerrill285.Inignoto.game.client.rendering.entity;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.entity.Entity;

public abstract class EntityRenderer<T extends Entity> {
	
	public abstract void render(T entity, ShaderProgram shader);
}
