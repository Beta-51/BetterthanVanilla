package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockTNT;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockTNT.class, remap = false)
public class BlockTNTMixin extends Block {

	public BlockTNTMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Inject(
		method = "ignite(Lnet/minecraft/core/world/World; I I I Lnet/minecraft/core/entity/player/EntityPlayer;Z)V",
		at = @At("HEAD"),
		cancellable = true
	)
	public void disableAbove(World world, int x, int y, int z, EntityPlayer player, boolean sound, CallbackInfo ci) {
		String msg = "Tnt is disabled above y: ";
		if (world.dimension.id == 0 && LusiiPlugin.DisableTNTOverworld <= y) {
			player.addChatMessage(msg + LusiiPlugin.DisableTNTOverworld);
			ci.cancel();
		}
		if (world.dimension.id == 1 && LusiiPlugin.DisableTNTNether <= y) {
			player.addChatMessage(msg + LusiiPlugin.DisableTNTNether);
			ci.cancel();
		}
		if (world.dimension.id == 2 && LusiiPlugin.DisableTNTSky <= y) {
			player.addChatMessage(msg + LusiiPlugin.DisableTNTSky);
			ci.cancel();
		}
	}
}
