package kmerrill285.Inignoto.item;

import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.RayTraceResult;

public class Item {
	public final String name;
	
	public final int stack_size;
	
	public Mesh mesh;
	
	public int use_time = 5;
	
	public Item(String name, int stack_size) {
		this.name = name;
		this.stack_size = stack_size;
	}
	
	public boolean rightClick(World world, PlayerEntity player, RayTraceResult result) {
		return false;
	}
	
	public boolean leftClick(World world, PlayerEntity player, RayTraceResult result) {
		return false;
	}
}
