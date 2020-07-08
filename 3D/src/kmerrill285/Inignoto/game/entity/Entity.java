package kmerrill285.Inignoto.game.entity;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.resources.PhysicsHelper;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;
import kmerrill285.Inignoto.resources.TPSCounter;

public class Entity {
	public Vector3f position;
	public Vector3f lastPos;
	public float pitch, yaw;
	public Vector3f velocity;
	
	public World world;
	public float width, height;
	public boolean onGround = false;
	protected boolean running;
	public boolean isMoving;
	public boolean isSneaking;
	public boolean lastOnGround;
	public int ticksExisted = 0;
	public boolean headInGround = false;
	public float eyeHeight = 0;
	
	public Vector3f size;
	
	public float moveSpeed = 0.1f;
	
	public boolean isDead = false;
	
	public float renderDistance = 200;
	public boolean touchedGround = false;
	
	protected double fallTimer = 0;
	
	protected int jumpDelay = 0;
	
	public float rotY;
	public float headPitch;
	public float headYaw;
	
	public float mass;
	
	public float step_height = 0.5f;
	
	public boolean nearGround = false;
	
	public float arm_swing = 0;
	
	public Entity(Vector3f position, Vector3f size, World world, float mass) {
		this.position = position;
		this.velocity = new Vector3f(0, 0, 0);
		this.lastPos = new Vector3f(position);
		this.world = world;
		this.size = size;
		this.eyeHeight = this.size.y * 0.9f;
		this.mass = mass;
	}
	
	public void tick() {
		float delta = (float)TPSCounter.getDelta() * 10;
		
		if (jumpDelay > 0) {
			jumpDelay--;
		}
		
		lastOnGround = onGround;
		onGround = false;
		nearGround = false;
		
		
		if (position.isFinite() == false) {
			if (lastPos.isFinite() == false) {
				position.x = 0;
				position.y = 32;
				position.z = 0;
				velocity = new Vector3f(0, 0, 0);
			} else {
				position = new Vector3f(lastPos);
				velocity = new Vector3f(0, 0, 0);
			}
		}
		
		float bias = 0.1f;
		
		
		if (velocity.x < 0) {
			boolean collision = false;
			
			Vector3f pos = new Vector3f(position).add(0, lastOnGround ? step_height : 0, 0);
			RayTraceResult result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta - bias, 0, 0), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				pos = new Vector3f(position).add(0, lastOnGround ? step_height : 0, size.z);
				result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta - bias, 0, 0), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					pos = new Vector3f(position).add(0, size.y, size.z);
					result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta - bias, 0, 0), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						pos = new Vector3f(position).add(0, size.y, 0);
						result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta - bias, 0, 0), TileRayTraceType.SOLID);
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			if (collision) {
				velocity.x = 0;
				position.x = lastPos.x;
			}
		}
		if (velocity.x > 0) {
			boolean collision = false;
			
			Vector3f pos = new Vector3f(position).add(size.x, lastOnGround ? step_height : 0, 0);
			RayTraceResult result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta + bias, 0, 0), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				pos = new Vector3f(position).add(size.x, lastOnGround ? step_height : 0, size.z);
				result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta + bias, 0, 0), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					pos = new Vector3f(position).add(size.x, size.y, size.z);
					result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta + bias, 0, 0), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						pos = new Vector3f(position).add(size.x, size.y, 0);
						result = world.rayTraceTiles(pos, new Vector3f(pos).add(velocity.x * delta + bias, 0, 0), TileRayTraceType.SOLID);
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			if (collision) {
				velocity.x = 0;
				position.x = lastPos.x;
			}
		}
		
		if (velocity.z < 0) {
			boolean collision = false;
			
			Vector3f pos = new Vector3f(position).add(0, lastOnGround ? step_height : 0, 0);
			RayTraceResult result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta - bias), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				pos = new Vector3f(position).add(size.x, lastOnGround ? step_height : 0, 0);
				result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta - bias), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					pos = new Vector3f(position).add(size.x, size.y, 0);
					result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta - bias), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						pos = new Vector3f(position).add(0, size.y, 0);
						result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta - bias), TileRayTraceType.SOLID);
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			if (collision) {
				velocity.z = 0;
				position.z = lastPos.z;
			}
		}
		
		if (velocity.z > 0) {
			boolean collision = false;
			
			Vector3f pos = new Vector3f(position).add(0, lastOnGround ? step_height : 0, size.z);
			RayTraceResult result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta + bias), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				pos = new Vector3f(position).add(size.x, lastOnGround ? step_height : 0, size.z);
				result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta + bias), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					pos = new Vector3f(position).add(size.x, size.y, size.z);
					result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta + bias), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						pos = new Vector3f(position).add(0, size.y, size.z);
						result = world.rayTraceTiles(pos, new Vector3f(pos).add(0, 0, velocity.z * delta + bias), TileRayTraceType.SOLID);
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			if (collision) {
				velocity.z = 0;
				position.z = lastPos.z;
			}
		}
		
		if (velocity.y <= 0) {
			boolean collision = false;
			float sub = -0.0f;
			RayTraceResult result = world.rayTraceTiles(position, new Vector3f(position).add(0, velocity.y * delta - bias - sub, 0), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				result = world.rayTraceTiles(new Vector3f(position).add(size.x, 0, 0), new Vector3f(position).add(size.x, velocity.y * delta - bias - sub, 0), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					result = world.rayTraceTiles(new Vector3f(position).add(size.x, 0, size.z), new Vector3f(position).add(size.x, velocity.y * delta - bias - sub, size.z), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						result = world.rayTraceTiles(new Vector3f(position).add(0, 0, size.z), new Vector3f(position).add(0, velocity.y * delta - bias - sub, size.z), TileRayTraceType.SOLID);
						
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			
			if (collision) {
				if (!lastOnGround) {
					jumpDelay = 1;
				}
				velocity.y = 0;
				position.y = result.getHit().y;
				onGround = true;
			}
		}
		
		{
			boolean collision = false;
			float sub = -0.5f;
			RayTraceResult result = world.rayTraceTiles(position, new Vector3f(position).add(0, sub, 0), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				result = world.rayTraceTiles(new Vector3f(position).add(size.x, 0, 0), new Vector3f(position).add(size.x, sub, 0), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					result = world.rayTraceTiles(new Vector3f(position).add(size.x, 0, size.z), new Vector3f(position).add(size.x, sub, size.z), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						result = world.rayTraceTiles(new Vector3f(position).add(0, 0, size.z), new Vector3f(position).add(0, sub, size.z), TileRayTraceType.SOLID);
						
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			
			if (collision) {
				onGround = true;
			}
		}
		
		{
			boolean collision = false;

			RayTraceResult result = world.rayTraceTiles(new Vector3f(position).add(0, step_height, 0), new Vector3f(position), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				result = world.rayTraceTiles(new Vector3f(position).add(size.x, step_height, 0), new Vector3f(position).add(size.x, 0, 0), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					result = world.rayTraceTiles(new Vector3f(position).add(size.x, step_height, size.z), new Vector3f(position).add(size.x, 0, size.z), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						result = world.rayTraceTiles(new Vector3f(position).add(0, step_height, size.z), new Vector3f(position).add(0, 0, size.z), TileRayTraceType.SOLID);
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			
			if (collision) {
				if (!lastOnGround) {
					jumpDelay = 1;
				}
				velocity.y = 0;
				position.y = result.getHit().y + 0.1f;
				onGround = true;
			}
		}
		
		{
			boolean collision = false;

			RayTraceResult result = world.rayTraceTiles(new Vector3f(position).add(0, 0, 0), new Vector3f(position).add(0, -step_height * 2, 0), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				result = world.rayTraceTiles(new Vector3f(position).add(size.x, 0, 0), new Vector3f(position).add(size.x, -step_height * 2, 0), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					result = world.rayTraceTiles(new Vector3f(position).add(size.x, 0, size.z), new Vector3f(position).add(size.x, -step_height * 2, size.z), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						result = world.rayTraceTiles(new Vector3f(position).add(0, 0, size.z), new Vector3f(position).add(0, -step_height * 2, size.z), TileRayTraceType.SOLID);
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			
			if (collision) {
				nearGround = true;
			}
		}
		
		
		if (velocity.y > 0) {
			boolean collision = false;

			RayTraceResult result = world.rayTraceTiles(new Vector3f(position).add(0, size.y, 0), new Vector3f(position).add(0, size.y + velocity.y * delta + bias, 0), TileRayTraceType.SOLID);
			if (result.getType() == RayTraceType.TILE) {
				collision = true;
			} else {
				result = world.rayTraceTiles(new Vector3f(position).add(size.x, size.y, 0), new Vector3f(position).add(size.x, size.y + velocity.y * delta + bias, 0), TileRayTraceType.SOLID);
				if (result.getType() == RayTraceType.TILE) {
					collision = true;
				} else {
					result = world.rayTraceTiles(new Vector3f(position).add(size.x, size.y, size.z), new Vector3f(position).add(size.x, size.y + velocity.y * delta + bias, size.z), TileRayTraceType.SOLID);
					if (result.getType() == RayTraceType.TILE) {
						collision = true;
					} else {
						result = world.rayTraceTiles(new Vector3f(position).add(0, size.y, size.z), new Vector3f(position).add(0, size.y + velocity.y * delta + bias, size.z), TileRayTraceType.SOLID);
						if (result.getType() == RayTraceType.TILE) {
							collision = true;
						}
					}
				}
			}
			
			if (collision) {
				velocity.y = 0;
				position.y = lastPos.y;
			}
		}

		if (!onGround) {
			
			if (fallTimer > 0) {
				fallTimer-=TPSCounter.getDelta() * 8.0f;
			} else {
				fallTimer = 0;
				
				if (!this.isInLiquid()) {
					PhysicsHelper.applyForce(velocity, new Vector3f(0, -PhysicsHelper.calculateGravity(mass) * 1.25f, 0), mass);
				} else {
					if (this.isSneaking) {
						PhysicsHelper.applyForce(velocity, new Vector3f(0, -PhysicsHelper.calculateGravity(mass) * 7f, 0), mass);
					} else {
						PhysicsHelper.applyForce(velocity, new Vector3f(0, -PhysicsHelper.calculateGravity(mass) * 0.5f, 0), mass);
					}
				}
			}
		} else {
			fallTimer = 5;
		}
		if (fallTimer > 0) onGround = true;
		
		
		
		lastPos.x = position.x;
		lastPos.y = position.y;
		lastPos.z = position.z;
		
		isMoving = (int)(velocity.x * 10) != 0 && (int)(velocity.y * 10) != 0;
		
		if (world.getTile(getTilePos().add(0, 1, 0)).getRayTraceType() == TileRayTraceType.LIQUID) {
			velocity.lerp(new Vector3f(velocity.x, 0, velocity.z), 0.3f);
			velocity.lerp(new Vector3f(0, velocity.y, 0), 0.01f);
		}
		if (world.getTile(getTilePos()).getRayTraceType() == TileRayTraceType.LIQUID) {
			velocity.lerp(new Vector3f(0, velocity.y, 0), 0.01f);
		}
		
		if (ticksExisted > 100) {
			position.x += velocity.x * delta;
			position.y += velocity.y * delta;
			position.z += velocity.z * delta;
		}
		
		ticksExisted++;
		if (touchedGround == false) {
			if (world.rayTraceTiles(position, new Vector3f(position).add(0, -50, 0), TileRayTraceType.SOLID).getType() != RayTraceType.EMPTY) {
				touchedGround=  true;
			}
		}
		
	}
	
	public void render(ShaderProgram shader) {
		
	}
	
	public void renderShadow(ShaderProgram shader, ShadowRenderer renderer) {
		
	}
	
	public boolean isInLiquid() {
		return world.getTile(getTilePos().add(0, 1, 0)).getRayTraceType() == TileRayTraceType.LIQUID ||
				world.getTile(getTilePos()).getRayTraceType() == TileRayTraceType.LIQUID;
	}
	
	public float getGravity() {
		return 2.0f / 90.0f;
	}
	
	public float getTerminalVelocity() {
		return (1.0f / 60.0f) * 43;
	}
	
	public Vector3f getEyePosition() {
		return new Vector3f(position).add(size.x / 2.0f, eyeHeight, size.z / 2.0f);
	}
	
	public void jump() {
		if (jumpDelay > 0) return;
		if (world.getTile(getTilePos().add(0, 1, 0)).getRayTraceType() != TileRayTraceType.LIQUID) {
			velocity.y = 0.05f;
		}
		if (world.getTile(getTilePos().add(0, 1, 0)).getRayTraceType() == TileRayTraceType.LIQUID) {
			if (velocity.y < 0) {
				velocity.lerp(new Vector3f(velocity.x, 0.05f, velocity.z), 0.025f);
			} else {
				velocity.lerp(new Vector3f(velocity.x, 0.15f, velocity.z), 0.1f);
			}
			if (world.getTile(getTilePos().add(0, 2, 0)).getRayTraceType() != TileRayTraceType.LIQUID) {
				velocity.lerp(new Vector3f(velocity.x, 0, velocity.z), 0.2f);
			}
		}
		
		
		
		
	}
	
	public void dispose() {
		
	}
	
	public TilePos getTilePos() {
		return new TilePos(position.x, position.y, position.z);
	}
	
	public boolean doesCollisionOccur(float x, float y, float z) {
		
		TilePos pos = new TilePos(x, y, z);
		
		if (world.getTile(pos).blocksMovement()) {
			return true;
		}
		return false;
	}
	
	public boolean isRunning() {
		return running;
	}


}
