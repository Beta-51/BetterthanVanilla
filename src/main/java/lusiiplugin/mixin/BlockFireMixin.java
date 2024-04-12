package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFire;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = BlockFire.class, remap = false)
public class BlockFireMixin extends Block {
	public BlockFireMixin(String key, int id) {
		super(key, id, Material.fire);
		this.setTicking(!LusiiPlugin.staticFire);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
	public void stopTick(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
		if (LusiiPlugin.staticFire) ci.cancel();
	}

	@Inject(method = "canFirePersist", at = @At("HEAD"), cancellable = true)
	public void stopFireDespawn(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		if (LusiiPlugin.staticFire) {
			cir.setReturnValue(world.isBlockNormalCube(x, y - 1, z));
		}
	}

	@Inject(method = "tryToCatchBlockOnFire", at = @At("HEAD"), cancellable = true)
	public void disableCatchOnFire(World world, int x, int y, int z, int chance, Random random, int meta, CallbackInfo ci) {
		if (LusiiPlugin.staticFire) ci.cancel();
	}
}
