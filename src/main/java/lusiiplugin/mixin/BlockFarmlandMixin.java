package lusiiplugin.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFarmland;
import net.minecraft.core.block.BlockFence;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
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
	@Overwrite
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
		Block blockBelow = world.getBlock(x,y-1,z);
		if ((blockBelow == Block.fencePlanksOak || blockBelow == Block.fencePlanksOakPainted) && enableAntiTrampleFence || disableTrample) {
			return;
		}
		if (entity instanceof EntityPlayer) {
			if (((EntityPlayer) entity).inventory.armorInventory[0] != null && ((EntityPlayer) entity).inventory.armorInventory[0].getItem() == Item.armorBootsLeather) {
				return;
			}
		}

		if (world.getBlockMetadata(x, y, z) > 0) {
			world.setBlockWithNotify(x, y, z, Block.mud.id);
		} else {
			world.setBlockWithNotify(x, y, z, Block.dirt.id);
		}
	}


}
