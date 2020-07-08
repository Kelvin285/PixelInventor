package kmerrill285.Inignoto.game.inventory;

import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.item.Item;
import kmerrill285.Inignoto.item.Items;

public class PlayerInventory {
	public final PlayerEntity player;
	
	public int hotbarSelected = 0;
	
	public InventorySlot[] hotbar = new InventorySlot[10];
		
	public PlayerInventory(PlayerEntity player) {
		this.player = player;
		for (int i = 0; i < hotbar.length; i++) {
			hotbar[i] = new InventorySlot();
		}
		hotbar[0].stack = new InventoryItemStack(Items.getItemForTile(Tiles.GRASS), 64);
	}
	
	public int addStack(InventoryItemStack stack) {
		for (int i = 0; i < hotbar.length; i++) {
			int val = hotbar[i].addToStack(stack);
			stack = new InventoryItemStack(stack.item, val);
		}
		return stack.size;
	}
}
