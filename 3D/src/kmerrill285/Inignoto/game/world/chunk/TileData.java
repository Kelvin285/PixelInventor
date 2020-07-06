package kmerrill285.Inignoto.game.world.chunk;

import java.io.Serializable;

public class TileData implements Serializable {
	private static final long serialVersionUID = 1L;
	private int tile;
	float miningTime;
	int waterLevel;
	float lastMiningTime;
	int state;
	
	public TileData(int tile) {
		this.setTile(tile);
	}
	public int getTile() {
		return tile;
	}
	public void setTile(int tile) {
		this.tile = tile;
	}
	
	public float getMiningTime() {
		return miningTime;
	}
	
	public void setMiningTime(float miningTime) {
		this.lastMiningTime = this.miningTime;
		this.miningTime = miningTime;
	}
	
	public float getLastMiningTime() {
		return this.lastMiningTime;
	}
	
	public int getWaterLevel() {
		return this.waterLevel;
	}
	
	public int getState() {
		return this.state;
	}
	
	public void setWaterLevel(int waterLevel) {
		this.waterLevel = waterLevel;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public String toString() {
		return tile + "," + waterLevel + "," + miningTime;
	}
	
}
