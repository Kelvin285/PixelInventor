package kmerrill285.Inignoto.game.tile;

import java.util.Random;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.BlockFace;
import kmerrill285.Inignoto.game.entity.ItemDropEntity;
import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.inventory.InventoryItemStack;
import kmerrill285.Inignoto.game.settings.Translation;
import kmerrill285.Inignoto.game.tile.data.TileState;
import kmerrill285.Inignoto.game.tile.data.TileStateHolder;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.item.Items;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.raytracer.RayBox;

public class Tile {
	
	public enum TileRayTraceType {
		SOLID, LIQUID, GAS
	};
	
	private String name;
	
	

	private static int CURRENT_ID = 0;
	private int ID;
	
	
	
	
	protected final TileStateHolder stateHolder;
	
	public RayBox[] collisionBoxes = new RayBox[] {
			new RayBox() {
				{
					min = new Vector3f(0, 0, 0);
					max = new Vector3f(1, 1, 1);
				}
			}
	};
	
	
	
	public int[] sound;
	
	public Tile(String name, int[] sound) {
		this.sound = sound;
		this.name = name;
		Tiles.REGISTRY.put(this.name, this);
		
		
		ID = CURRENT_ID++;
		this.stateHolder = new TileStateHolder(this);
	}
	
	public final int getNumberOfStates() {
		return getStateHolder().num_states;
	}
	
	public TileStateHolder getStateHolder() {
		return this.stateHolder;
	}
	
	
	
	public int getID() {
		return this.ID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTranslatedName() {
		return Translation.translateText(getName().split(":")[0]+":tiles."+getName().split(":")[1]);
	}
	
	public TileRayTraceType getRayTraceType(TileState data) {
		return data.getRayTraceType();
	}
	
	public float getHardness(TileState data) {
		return data.getHardness();
	}
	
	public float getDensity(TileState data) {
		return data.getDensity();
	}
	public boolean isFullCube(TileState data) {
		return data.isFullCube();
	}
	
	public boolean blocksMovement(TileState data) {
		return data.blocksMovement();
	}
	
	public boolean isVisible(TileState data) {
		return data.isVisible();
	}
	
	public int getWidth(TileState data) {
		return data.getWidth();
	}
	public int getHeight(TileState data) {
		return data.getHeight();
	}
	
	public String getModel(TileState data) {
		return data.getModel();
	}
	
	public String getTextureFor(BlockFace face, TileState data) {
		return data.getTextureFor(face);
	}
	
	public String getSideTexture(TileState data) {
		return data.getSideTexture();
	}
	
	public void dropAsItem(World world, int x, int y, int z) {
		ItemDropEntity drop = new ItemDropEntity(new Vector3f(x+0.5f, y+0.5f, z+0.5f), world, new InventoryItemStack(Items.getItemForTile(this), 1));
		world.entities.add(drop);
	}
	
	
	public TileState getDefaultState() {
		return this.stateHolder.getStateFor(0);
	}
	
	public void updateLightWhenAddedToWorld(int x, int y, int z, Chunk chunk, TileState state) {
		if (state.getLightIntensity() > 0) {
			chunk.setTorchlight(x, y, z, state.getLightIntensity());
		}
		
//		if (state.getLightColor().x > 0) {
//			chunk.setRed(x, y, z, (int)state.getLightColor().x);
//		}
//		if (state.getLightColor().y > 0) {
//			chunk.setGreen(x, y, z, (int)state.getLightColor().y);
//		}
//		if (state.getLightColor().z > 0) {
//			chunk.setBlue(x, y, z, (int)state.getLightColor().z);
//		}
		
		if (chunk.generated)
		if (state.isOpaque()) {
			if (chunk.getSunlightValue(x, y, z) > 0) {
				chunk.removeSunlight(x, y, z);
			}
		} else {
			if (chunk.getSunlightValue(x, y + 1, z) > 0) {
				chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y + 1, z));
			} else {
				if (chunk.getSunlightValue(x, y - 1, z) > 0) {
					chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y - 1, z) - 1);
				}
				else
					if (chunk.getSunlightValue(x - 1, y, z) > 0) {
						chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x - 1, y, z) - 1);
					}
					else
						if (chunk.getSunlightValue(x + 1, y, z) > 0) {
							chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x + 1, y, z) - 1);
						}
						else
							if (chunk.getSunlightValue(x, y, z - 1) > 0) {
								chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y, z - 1) - 1);
							}
							else
								if (chunk.getSunlightValue(x, y, z + 1) > 0) {
									chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y, z + 1) - 1);
								}
			}
		}
	}

	public void updateLightWhenRemovedFromWorld(int x, int y, int z, Chunk chunk, TileState state) {
		if (state.getLightIntensity() > 0) {
			chunk.removeTorchlight(x, y, z);
		}
//		if (state.getLightColor().x > 0) {
//			chunk.removeRed(x, y, z);
//		}
//		if (state.getLightColor().y > 0) {
//			chunk.removeGreen(x, y, z);
//		}
//		if (state.getLightColor().z > 0) {
//			chunk.removeBlue(x, y, z);
//		}
		if (chunk.generated)
		if (state.isOpaque()) {
			if (chunk.getSunlightValue(x, y + 1, z) > 0) {
				chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y + 1, z));
			} else {
				if (chunk.getSunlightValue(x, y - 1, z) > 0) {
					chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y - 1, z) - 1);
				}
				else
					if (chunk.getSunlightValue(x - 1, y, z) > 0) {
						chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x - 1, y, z) - 1);
					}
					else
						if (chunk.getSunlightValue(x + 1, y, z) > 0) {
							chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x + 1, y, z) - 1);
						}
						else
							if (chunk.getSunlightValue(x, y, z - 1) > 0) {
								chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y, z - 1) - 1);
							}
							else
								if (chunk.getSunlightValue(x, y, z + 1) > 0) {
									chunk.setSunlightValue(x, y, z, chunk.getSunlightValue(x, y, z + 1) - 1);
								}
			}
		}
	}
	
	public TileState getStateWhenPlaced(int x, int y, int z, RayTraceResult result, PlayerEntity placer, World world) {
		return getDefaultState();
	}
	
	public float getPitch(TileState state) {
		return state.getPitch();
	}
	
	public float getYaw(TileState state) {
		return state.getYaw();
	}
	
	
	public void tick(World world, TilePos pos, Random random, TileState state) {
		
	}
	
	public double getTickPercent() {
		return 0;
	}

	
}
