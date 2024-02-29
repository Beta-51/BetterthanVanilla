package turniplabs.examplemod.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockTNT;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BlockTNT.class, remap = false)
public class BlockTNTMixin extends Block {

	public BlockTNTMixin(String key, int id, Material material) {
		super(key, id, material);
	}
	@Overwrite
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
	}
	@Overwrite
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {

	}

	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		return dropCause == EnumDropCause.EXPLOSION ? null : new ItemStack[]{new ItemStack(this)};
	}
	@Overwrite
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {

	}
	@Overwrite
	public void ignite(World world, int x, int y, int z, boolean sound) {

	}
	@Overwrite
	public void ignite(World world, int x, int y, int z, EntityPlayer player, boolean sound) {

	}
	@Overwrite
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {

	}
	@Overwrite
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		return false;
	}



}
