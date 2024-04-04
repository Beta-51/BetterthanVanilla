package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockBed;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBed.class, remap = false)
public class BlockBedMixin extends Block {

	public BlockBedMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Inject(
		method = "blockActivated(Lnet/minecraft/core/world/World; I I I Lnet/minecraft/core/entity/player/EntityPlayer;) Z",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/core/world/World;setBlockWithNotify(IIII)Z",
			shift = At.Shift.BEFORE
		),
		cancellable = true
	)
	public void bedBoomStop(World world, int x, int y, int z, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
		if (LusiiPlugin.disableBedExplosion) {
			player.addChatMessage("§1You may not sleep here.");
			cir.setReturnValue(true);
		}
	}
}
