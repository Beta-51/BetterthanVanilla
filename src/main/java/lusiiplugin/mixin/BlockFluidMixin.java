package lusiiplugin.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.BlockPortal;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockFluid.class, remap = false)
public class BlockFluidMixin extends Block {

	public BlockFluidMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Overwrite
	public void onBlockAdded(World world, int x, int y, int z) {
		if (world.getBlockId(x, y-1, z) == ((BlockPortal)Block.portalParadise).portalFrameId && !world.isClientSide && this.blockMaterial == Material.water){
			((BlockPortal)Block.portalParadise).tryToCreatePortal(world, x, y, z);
		}

		this.checkForHarden(world, x, y, z);

	}
	@Shadow
	private void checkForHarden(World world, int x, int y, int z) {
	}


}
