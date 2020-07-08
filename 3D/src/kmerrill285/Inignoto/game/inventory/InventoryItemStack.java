package kmerrill285.Inignoto.game.inventory;

import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.item.Item;
import kmerrill285.Inignoto.item.Items;

public class InventoryItemStack {
	public int size = 0;
	public Item item = null;
	
	public InventoryItemStack(Item item, int stack) {
		this.item = item;
		this.size = stack;
	}
	
	public InventoryItemStack(Item item) {
		this(item, 1);
	}
	
	public InventoryItemStack(Tile tile, int stack) {
		this(Items.getItemForTile(tile), stack);
	}
	
	public InventoryItemStack(Tile tile) {
		this(tile, 1);
	}
}
