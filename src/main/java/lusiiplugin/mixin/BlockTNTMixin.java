package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockTNT;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityTNT;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemFirestriker;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BlockTNT.class, remap = false)
public class BlockTNTMixin extends Block {

	public BlockTNTMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Overwrite
	public void ignite(World world, int x, int y, int z, EntityPlayer player, boolean sound) {
			if (world.dimension.id == 0 && LusiiPlugin.DisableTNTOverworld <= y) {
				player.addChatMessage("Tnt is disabled above y:" + LusiiPlugin.DisableTNTOverworld);
				return;
			}
			if (world.dimension.id == 1 && LusiiPlugin.DisableTNTNether <= y) {
				player.addChatMessage("Tnt is disabled above y:" + LusiiPlugin.DisableTNTNether);
				return;
			}
			if (world.dimension.id == 2 && LusiiPlugin.DisableTNTSky <= y) {
				player.addChatMessage("Tnt is disabled above y:" + LusiiPlugin.DisableTNTSky);
				return;
			}

		if (world.isClientSide) {
			if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFirestriker) {
				player.inventory.getCurrentItem().damageItem(1, player);
			}

		} else {
				world.setBlockWithNotify(x, y, z, 0);
				EntityTNT e = new EntityTNT(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F));
				world.entityJoinedWorld(e);
				world.playSoundAtEntity((Entity)null, e, "random.fuse", 1.0F, 1.0F);
				if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFirestriker) {
					player.inventory.getCurrentItem().damageItem(1, player);
				}

			}
	}




}
