package kmerrill285.Inignoto.game.inventory;

import kmerrill285.Inignoto.item.Item;

public class InventorySlot {
	public InventoryItemStack stack;
	
	/*
	 * @param i
	 */
	/**
	 * Increase the stack size by a value (i).
	 * This method returns the amount of items that could not be added to the stack
	 * **/
	public int incrementStack(int i) {
		if (i == 0) return 0;
		int remaining = i;
		if (stack != null) {
			int first = stack.size;
			stack.size += i;
			if (stack.size > stack.item.stack_size) {
				stack.size = stack.item.stack_size;
			}
			remaining = i - (stack.size - first);
		}
		return remaining;
	}
	
	/**
	 * @param i
	 */
	
	/**
	 * Decrease the stack size by a value (i).
	 * **/
	public void decrementStack(int i) {
		if (stack != null) {
			stack.size -= i;
			if (stack.size <= 0) {
				stack = null;
			}
		}
	}
	
	/*
	 * @param stack
	 */
	/**
	 * Add the items from an item stack to the slot.  Returns the number of items that could not be added.
	 * **/
	public int addToStack(InventoryItemStack stack) {
		if (stack == null) return 0;
		if(stack.size == 0) return 0;
		if (this.stack == null) {
			this.stack = stack;
			return 0;
		} else {
			if (this.stack.item == stack.item) {
				return this.incrementStack(stack.size);
			}
		}
		return stack.size;
	}
}
