package kmerrill285.Inignoto.game.world.structures;

import kmerrill285.Inignoto.game.tile.Tiles;

public class StoneCube extends Structure {
	private static final long serialVersionUID = -6518038958304776167L;

	public StoneCube() {
		super(10, 10, 10);
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				for (int z = 0; z < 10; z++) {
					this.getLocalTile(x, y, z).setTile(Tiles.STONE.getID());
				}
			}
		}
	}

}
