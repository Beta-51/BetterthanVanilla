package lusiiplugin.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFarmland;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static lusiiplugin.LusiiPlugin.disableTrample;
import static lusiiplugin.LusiiPlugin.enableAntiTrampleFence;

@Mixin(value = BlockFarmland.class,remap = false)
public class BlockFarmlandMixin extends Block {
	public BlockFarmlandMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Inject(
		method = "onEntityWalking(Lnet/minecraft/core/world/World; I I I Lnet/minecraft/core/entity/Entity;) V",
		at = @At("HEAD"),
		cancellable = true
	)
	public void trampleControl(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
		Block blockBelow = world.getBlock(x,y-1,z);
		if (((blockBelow == Block.fencePlanksOak || blockBelow == Block.fencePlanksOakPainted) && enableAntiTrampleFence) || disableTrample) {
			world.notifyBlockChange(x, y, z, Block.farmlandDirt.id);
			ci.cancel();
		}
	}
}
