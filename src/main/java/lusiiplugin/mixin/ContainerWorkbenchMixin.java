package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.*;
import net.minecraft.core.player.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(value = ContainerWorkbench.class, remap = false)
public class ContainerWorkbenchMixin extends Container {

	@Inject(
		method = "isUsableByPlayer(Lnet/minecraft/core/entity/player/EntityPlayer;) Z",
		at = @At("HEAD"),
		cancellable = true
	)
	public void craftCommand(EntityPlayer entityplayer, CallbackInfoReturnable<Boolean> cir) {
		if (LusiiPlugin.craftCommand) {
			cir.setReturnValue(true);
		}
	}

	@Shadow
	public List<Integer> getMoveSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
        return Collections.emptyList();
    }

	@Shadow
	public List<Integer> getTargetSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
		return Collections.emptyList();
	}

	@Shadow
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return false;
	}
}
