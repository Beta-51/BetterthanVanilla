package lusiiplugin.mixin;

import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotCrafting;
import net.minecraft.core.util.helper.MathHelper;
import org.apache.log4j.Logger;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(value = Container.class, remap = false)
public class ContainerMixin {
	private static final Logger logger = Logger.getLogger("Minecraft");
	@Overwrite
	public ItemStack clickInventorySlot(InventoryAction action, int[] args, EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		String slot1googative = "none";
		int slot1googativeAmount = 0;
		if (action != InventoryAction.DROP_HELD_SINGLE && action != InventoryAction.DROP_HELD_STACK) {
			if (action != InventoryAction.PICKUP_SIMILAR && action != InventoryAction.DRAG_ITEMS_ALL && action != InventoryAction.DRAG_ITEMS_ONE) {
				if (args != null && args.length != 0) {
					int slotId = args[0];
					Slot slot = this.getSlot(slotId);
					if (slot == null) {
						this.onCraftMatrixChanged(inventory);
						return null;
					} else {
						ItemStack controlStack = null;
						ItemStack stackInSlot = slot.getStack();
						Item itemInSlot = stackInSlot != null ? stackInSlot.getItem() : null;


						if (stackInSlot != null) {
							slot1googative = stackInSlot.getItemName();
							slot1googativeAmount = stackInSlot.stackSize;
							controlStack = stackInSlot.copy();
						}

						ItemStack stackInHand;
						ItemStack stack;
						if (action != InventoryAction.INTERACT_SLOT && action != InventoryAction.INTERACT_GRABBED) {
							if (action == InventoryAction.EQUIP_ARMOR) {
								this.handleArmorEquip(slot, player);
								this.onCraftMatrixChanged(inventory);
								return controlStack;
							} else if (action == InventoryAction.HOTBAR_ITEM_SWAP) {
								this.handleHotbarSwap(args, player);
								this.onCraftMatrixChanged(inventory);
								return controlStack;
							} else if (action != InventoryAction.MOVE_STACK && action != InventoryAction.MOVE_SINGLE_ITEM && action != InventoryAction.MOVE_SIMILAR && action != InventoryAction.MOVE_ALL) {
								if (action == InventoryAction.SORT) {
									if (player.world.isClientSide) {
										return null;
									} else {
										this.handleSort(args, player);
										this.onCraftMatrixChanged(inventory);
										return controlStack;
									}
								} else {
									slot.onSlotChanged();
									stackInHand = inventory.getHeldItemStack();
									int splitSize;
									if (action == InventoryAction.DROP) {
										if (stackInSlot == null) {
											return null;
										} else {
											splitSize = args.length > 1 ? args[1] : 1;
											splitSize = Math.min(splitSize, stackInSlot.stackSize);
											stack = slot.decrStackSize(splitSize);
											if (stackInSlot.stackSize <= 0) {
												slot.putStack((ItemStack)null);
											}
											System.out.println(player.username+" " + stack.getItemName() + ", "+stack.stackSize + " dropped " + splitSize + "DROP");
											logger.info(player.username+" " + stack.getItemName() + ", "+stack.stackSize + " dropped " + splitSize + "DROP");
											slot.onPickupFromSlot(stack);
											player.dropPlayerItem(stack);
											this.onCraftMatrixChanged(inventory);
											return controlStack;
										}
									} else if (action != InventoryAction.CREATIVE_GRAB && action != InventoryAction.CREATIVE_MOVE && action != InventoryAction.CREATIVE_DELETE) {
										if (stackInSlot == null) {
											if (stackInHand != null && slot.canPutStackInSlot(stackInHand)) {
												splitSize = action != InventoryAction.CLICK_LEFT ? 1 : stackInHand.stackSize;
												if (splitSize > slot.getSlotStackLimit()) {
													splitSize = slot.getSlotStackLimit();
												}

												slot.putStack(stackInHand.splitStack(splitSize));
												if (stackInHand.stackSize <= 0) {
													inventory.setHeldItemStack((ItemStack)null);
												}
											}
										} else if (stackInHand == null) {
											splitSize = action != InventoryAction.CLICK_LEFT ? (stackInSlot.stackSize + 1) / 2 : stackInSlot.stackSize;
											stack = slot.decrStackSize(splitSize);
											inventory.setHeldItemStack(stack);
											if (stackInSlot.stackSize <= 0) {
												slot.putStack((ItemStack)null);
											}

											slot.onPickupFromSlot(inventory.getHeldItemStack());
										} else if (slot.canPutStackInSlot(stackInHand)) {
											if (!stackInSlot.canStackWith(stackInHand)) {
												if (stackInHand.stackSize <= slot.getSlotStackLimit()) {
													slot.putStack(stackInHand);
													inventory.setHeldItemStack(stackInSlot);
												}
											} else {
												splitSize = action != InventoryAction.CLICK_LEFT ? 1 : stackInHand.stackSize;
												if (splitSize > slot.getSlotStackLimit() - stackInSlot.stackSize) {
													splitSize = slot.getSlotStackLimit() - stackInSlot.stackSize;
												}

												if (splitSize > stackInHand.getMaxStackSize() - stackInSlot.stackSize) {
													splitSize = stackInHand.getMaxStackSize() - stackInSlot.stackSize;
												}

												stackInHand.splitStack(splitSize);
												if (stackInHand.stackSize <= 0) {
													inventory.setHeldItemStack((ItemStack)null);
												}

												stackInSlot.stackSize += splitSize;
											}
										} else if (stackInSlot.canStackWith(stackInHand) && stackInSlot.stackSize + stackInHand.stackSize <= stackInHand.getMaxStackSize()) {
											slot.putStack((ItemStack)null);
											slot.onPickupFromSlot(stackInSlot);
											if (stackInSlot.canStackWith(stackInHand) && stackInHand.stackSize + stackInSlot.stackSize <= stackInHand.getMaxStackSize()) {
												stackInHand.stackSize += stackInSlot.stackSize;
											} else {
												player.dropPlayerItem(stackInSlot);
											}
										}

										if (inventory.getHeldItemStack() != null && inventory.getHeldItemStack().stackSize <= 0) {
											inventory.setHeldItemStack((ItemStack)null);
										}
										if (stackInSlot != null){
											System.out.println(player.username + " " + slot1googative + ", " + slot1googativeAmount + " CLICK_LEFT");
											logger.info(player.username + " " + slot1googative + ", " + slot1googativeAmount + " CLICK_LEFT");
										}

										this.onCraftMatrixChanged(player.inventory);
										return controlStack;
									} else if (player.getGamemode().consumeBlocks()) {
										System.out.println("Player " + player.username + " used a creative inventory action but is not in creative mode!");
										return null;
									} else {
										if (action == InventoryAction.CREATIVE_DELETE) {
											splitSize = args.length > 1 ? args[1] : 1;

											for(int i = 0; i < splitSize; ++i) {
												Slot slot1 = this.getSlot(slotId + i);
												if (slot1 != null) {
													slot1.putStack((ItemStack)null);
												}
											}
										} else {
											splitSize = args.length > 1 ? args[1] : 0;
											if (stackInSlot != null) {
												splitSize = MathHelper.clamp(splitSize, 0, stackInSlot.getMaxStackSize());
											} else {
												splitSize = 0;
											}

											if (action == InventoryAction.CREATIVE_GRAB) {
												if (splitSize > 0) {
													stack = slot.getStack().copy();
													stack.stackSize = splitSize;
												} else {
													stack = null;
												}

												inventory.setHeldItemStack(stack);
											}

											if (action == InventoryAction.CREATIVE_MOVE && splitSize > 0) {
												stack = slot.getStack().copy();
												stack.stackSize = splitSize;
												player.inventory.insertItem(stack, false);
											}
										}

										this.onCraftMatrixChanged(player.inventory);
										return controlStack;
									}
								}
							} else {
								int target = args.length > 1 ? args[1] : 0;
								System.out.println(player.username+" " + slot1googative + ", "+ slot1googativeAmount + " target " + target);
								logger.info(player.username+" " + slot1googative + ", "+ slot1googativeAmount + " target " + target);
								this.handleItemMove(action, slot, target, player);
								this.onCraftMatrixChanged(player.inventory);
								return controlStack;
							}
						} else {
							stackInHand = player.inventory.getHeldItemStack();
							Item interactItem;
							if (action == InventoryAction.INTERACT_SLOT) {
								if (stackInSlot == null) {
									return null;
								}
								System.out.println(player.username+" " + slot.getStack().getItemName() + ", "+slot.getStack().stackSize + "INTERACT_SLOT");
								logger.info(player.username+" " + slot.getStack().getItemName() + ", "+slot.getStack().stackSize + "INTERACT_SLOT");
								interactItem = itemInSlot;
							} else {
								interactItem = player.inventory.getHeldItemStack().getItem();
							}

							if (interactItem.hasInventoryInteraction() && slot.allowItemInteraction()) {
								stack = interactItem.onInventoryInteract(player, slot, stackInSlot, action == InventoryAction.INTERACT_GRABBED);
								if (stack != null && stack.stackSize <= 0) {
									stack = null;
								}
								System.out.println(player.username+" " + slot.getStack().getItemName() + ", "+slot.getStack().stackSize + "INTERACT_GRABBED");
								logger.info(player.username+" " + slot.getStack().getItemName() + ", "+slot.getStack().stackSize + "INTERACT_GRABBED");
								slot.putStack(stack);
								stackInHand = player.inventory.getHeldItemStack();
								if (stackInHand != null && stackInHand.stackSize <= 0) {
									player.inventory.setHeldItemStack((ItemStack)null);
								}

								this.onCraftMatrixChanged(inventory);
								return controlStack;
							} else {
								return controlStack;
							}
						}
					}
				} else {
					return null;
				}
			} else {
				ItemStack itemStack = inventory.getHeldItemStack();
				if (itemStack == null) {
					return null;
				} else {
					ItemStack controlStack = itemStack.copy();
					if (action == InventoryAction.PICKUP_SIMILAR) {
						System.out.println(player.username+" " + inventory.getHeldItemStack().getItemName() + ", "+inventory.getHeldItemStack().stackSize + "PICKUP_SIMILAR");
						logger.info(player.username+" " + inventory.getHeldItemStack().getItemName() + ", "+inventory.getHeldItemStack().stackSize + "PICKUP_SIMILAR");
						this.pickupSimilarItems(player);
					} else {
						this.dragItemsAcrossSlots(player, action, args);
					}

					this.onCraftMatrixChanged(player.inventory);
					return controlStack;
				}
			}
		} else {
			if (inventory.getHeldItemStack() != null) {
				if (action == InventoryAction.DROP_HELD_STACK) {
					System.out.println(player.username+" " + inventory.getHeldItemStack().getItemName() + ", "+inventory.getHeldItemStack().stackSize + "DROP_HELD_STACK");
					logger.info(player.username+" " + inventory.getHeldItemStack().getItemName() + ", "+inventory.getHeldItemStack().stackSize + "DROP_HELD_STACK");
					player.dropPlayerItem(inventory.getHeldItemStack());
					inventory.setHeldItemStack((ItemStack)null);
				}

				if (action == InventoryAction.DROP_HELD_SINGLE) {
					System.out.println(player.username+" " + inventory.getHeldItemStack().getItemName() + ", "+inventory.getHeldItemStack().stackSize + "DROP_HELD_SINGLE");
					logger.info(player.username+" " + inventory.getStackInSlot(args[0]).getItemName() + ", "+inventory.getHeldItemStack().stackSize + "DROP_HELD_SINGLE");
					player.dropPlayerItem(inventory.getHeldItemStack().splitStack(1));
					if (inventory.getHeldItemStack().stackSize == 0) {
						inventory.setHeldItemStack((ItemStack)null);
					}
				}
			}

			this.onCraftMatrixChanged(inventory);
			return null;
		}
	}
	@Overwrite
	public void handleHotbarSwap(int[] args, EntityPlayer player) {

		String slot1googative = "none";
		int slot1googativeAmount = 0;
		String slot2googative = "none";
		int slot2googativeAmount = 0;

		if (args.length >= 2) {
			int hotbarSlotNumber = args[1];
			if (hotbarSlotNumber >= 1 && hotbarSlotNumber <= 9) {
				Slot slot = this.getSlot(args[0]);
				Slot hotbarSlot = this.getSlot(this.getHotbarSlotId(hotbarSlotNumber));
				if (hotbarSlot != null && slot != hotbarSlot) {
					ItemStack slotStack = slot.getStack();
					ItemStack hotbarStack = hotbarSlot.getStack();
					if (slotStack != null) {
						slot1googative = slotStack.getItemName();
						slot1googativeAmount = slotStack.stackSize;
						slot.putStack((ItemStack)null);
						slot.onPickupFromSlot(slotStack);
					}

					if (hotbarStack != null) {
						slot2googative = hotbarStack.getItemName();
						slot2googativeAmount = hotbarStack.stackSize;
						hotbarSlot.putStack((ItemStack)null);
						hotbarSlot.onPickupFromSlot(hotbarStack);
					}

					System.out.println(player.username+" " + slot1googative + ", "+ slot1googativeAmount + " to " + slot2googative +", "+ slot2googativeAmount);
					logger.info(player.username+" " + slot1googative + ", "+ slot1googativeAmount + " to " + slot2googative + ", "+ slot2googativeAmount);
					this.mergeItems(slotStack, Collections.singletonList(hotbarSlot.id));
					this.storeOrDropItem(player, slotStack);
					this.mergeItems(hotbarStack, Collections.singletonList(slot.id));
					this.storeOrDropItem(player, hotbarStack);
					slot.onSlotChanged();
					hotbarSlot.onSlotChanged();
				}
			}
		}

	}
	@Shadow
	private int getHotbarSlotId(int hotbarSlotNumber) {
		return 0;
	}

	@Shadow
	private void storeOrDropItem(EntityPlayer player, ItemStack stack) {
	}

	@Shadow
	private void handleArmorEquip(Slot slot, EntityPlayer player) {
	}

	@Shadow
	private void handleSort(int[] args, EntityPlayer player) {
	}

	@Shadow
	private Slot getSlot(int i) {
		return null;
	}

	@Shadow
	public void handleItemMove(InventoryAction action, Slot slot, int target, EntityPlayer player) {

	}

    @Shadow
    public List<Integer> getMoveSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
        return null;
    }

    @Shadow
	private boolean compare(ItemStack compareStack, ItemStack item) {
		return false;
	}

	@Shadow
	private int getFreeSpace(ItemStack item, List<Integer> targetSlots) {
		return 0;
	}

	@Shadow
	private Slot getSlotFromList(List<Integer> targetSlots, int i) {
		return null;
	}

	@Shadow
	private void mergeItems(ItemStack item, List targetSlots) {
	}

    @Shadow
    public List<Integer> getTargetSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
        return null;
    }

    @Shadow
	private void handleCrafting(InventoryAction action, SlotCrafting slot, int target, EntityPlayer player) {
	}

	@Shadow
	private void dragItemsAcrossSlots(EntityPlayer player, InventoryAction action, int[] args) {
	}

	@Shadow
	public void onCraftMatrixChanged(IInventory iinventory) {

	}

	@Shadow
	private void pickupSimilarItems(EntityPlayer player) {
	}


}
