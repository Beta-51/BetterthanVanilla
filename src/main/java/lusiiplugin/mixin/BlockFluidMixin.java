package lusiiplugin.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.BlockPortal;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockFluid.class, remap = false)
public class BlockFluidMixin extends Block {

	public BlockFluidMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Inject(
		method = "onBlockAdded(Lnet/minecraft/core/world/World; I I I) V",
		at = @At("TAIL")
	)
	public void addParadise(World world, int x, int y, int z, CallbackInfo ci) {
		if (world.getBlockId(x, y-1, z) == ((BlockPortal)Block.portalParadise).portalFrameId && !world.isClientSide){
			((BlockPortal)Block.portalParadise).tryToCreatePortal(world, x, y, z);
		}
	}
}
