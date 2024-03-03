package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.*;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = ContainerWorkbench.class,remap = false)
public class ContainerWorkbenchMixin extends Container {@Shadow
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);@Shadow
	public IInventory craftResult = new InventoryCraftResult();@Shadow
	private World field_20133_c;@Shadow
	private int field_20132_h;@Shadow
	private int field_20131_i;@Shadow
	private int field_20130_j;

	@Shadow
	public List<Integer> getMoveSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
		return null;
	}

	@Shadow
	public List<Integer> getTargetSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
		return null;
	}

	@Overwrite
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		if (LusiiPlugin.craftCommand) {
			return true;
		}
		if (this.field_20133_c.getBlockId(this.field_20132_h, this.field_20131_i, this.field_20130_j) != Block.workbench.id) {
			return false;
		} else {
			return entityplayer.distanceToSqr((double)this.field_20132_h + 0.5, (double)this.field_20131_i + 0.5, (double)this.field_20130_j + 0.5) <= 64.0;
		}
	}
}
