package turniplabs.examplemod.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFire;
import net.minecraft.core.block.BlockPortal;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

import static net.minecraft.core.block.tag.BlockTags.BROKEN_BY_FLUIDS;
import static net.minecraft.core.block.tag.BlockTags.PLACE_OVERWRITES;

@Mixin(value = BlockFire.class, remap = false)
public class BlockFireMixin extends Block {
	private int[] chanceToEncourageFire;
	private int[] abilityToCatchFire;

	public BlockFireMixin(String key, int id) {
		super(key, id, Material.fire);
		this.chanceToEncourageFire = new int[Block.blocksList.length];
		this.abilityToCatchFire = new int[Block.blocksList.length];
		this.setTicking(true);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		return null;
	}
	@Overwrite
	public void initializeBlock() {

	}
	@Overwrite
	private void setBurnRate(int id, int chanceToEncourageFire, int abilityToCatchFire) {
		this.chanceToEncourageFire[id] = 0;
		this.abilityToCatchFire[id] = 0;
	}

	public AABB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	public void onBlockRemoval(World world, int x, int y, int z) {
		for(int i = 0; i < 8; ++i) {
			world.spawnParticle("largesmoke", (double)x + Math.random(), (double)y + 0.5, (double)z + Math.random(), 0.0, 0.0, 0.0);
		}

	}

	public int quantityDropped(int meta, Random rand) {
		return 0;
	}
	@Overwrite
	public int tickRate() {
		return 2147483647;
	}

	public void setBurnResult(World world, int x, int y, int z) {
		world.setBlockWithNotify(x, y, z, this.getBurnResultId(world, x, y, z));
	}

	@ModifyVariable(method = "updateTick", name = "infiniBurn", at = @At("STORE"), require = 1,remap = false)
	public boolean modifyInfiniBurn(boolean old)
	{
		return true;
	}






	//@Shadow
	//public void updateTick(World world, int x, int y, int z, Random rand) {
	//	boolean infiniBurn = (world.getBlockMetadata(x,y,z) > 0);
	//}
	@Shadow
	private void tryToCatchBlockOnFire(World world, int x, int y, int z, int chance, Random random, int meta) {

	}
	@Overwrite
	private boolean canNeighborCatchFire(World world, int x, int y, int z) {
		return false;
	}

	private int getChanceOfNeighborsEncouragingFire(World world, int i, int j, int k) {
		int l = 0;
		if (!world.isAirBlock(i, j, k)) {
			return 0;
		} else {
			l = this.getChanceToEncourageFire(world, i + 1, j, k, l);
			l = this.getChanceToEncourageFire(world, i - 1, j, k, l);
			l = this.getChanceToEncourageFire(world, i, j - 1, k, l);
			l = this.getChanceToEncourageFire(world, i, j + 1, k, l);
			l = this.getChanceToEncourageFire(world, i, j, k - 1, l);
			l = this.getChanceToEncourageFire(world, i, j, k + 1, l);
			return l;
		}
	}
	@Overwrite
	public boolean canBlockCatchFire(WorldSource iblockaccess, int i, int j, int k) {
		return true;
	}

	public int getChanceToEncourageFire(World world, int i, int j, int k, int l) {
		int i1 = this.chanceToEncourageFire[world.getBlockId(i, j, k)];
		return i1 > l ? i1 : l;
	}
	@Overwrite
	public boolean canFirePersist(World world, int x, int y, int z) {
		if (world.getBlock(x+1,y,z) != null && !world.getBlock(x+1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x+1,y,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x-1,y,z) != null && !world.getBlock(x-1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x-1,y,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y+1,z) != null && !world.getBlock(x,y+1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y+1,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y-1,z) != null && !world.getBlock(x,y-1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y-1,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y,z+1) != null && !world.getBlock(x,y,z+1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z+1).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y,z-1) != null && !world.getBlock(x,y,z-1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z-1).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else {
			return false;
		}
	}
	@Overwrite
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		if (world.getBlock(x+1,y,z) != null && !world.getBlock(x+1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x+1,y,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x-1,y,z) != null && !world.getBlock(x-1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x-1,y,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y+1,z) != null && !world.getBlock(x,y+1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y+1,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y-1,z) != null && !world.getBlock(x,y-1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y-1,z).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y,z+1) != null && !world.getBlock(x,y,z+1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z+1).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else if (world.getBlock(x,y,z-1) != null && !world.getBlock(x,y,z-1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z-1).hasTag(BROKEN_BY_FLUIDS)) {
			return true;
		} else {
			return false;
		}
    }
	@Overwrite
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		if (world.getBlock(x+1,y,z) != null && !world.getBlock(x+1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x+1,y,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x-1,y,z) != null && !world.getBlock(x-1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x-1,y,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y+1,z) != null && !world.getBlock(x,y+1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y+1,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y-1,z) != null && !world.getBlock(x,y-1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y-1,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y,z+1) != null && !world.getBlock(x,y,z+1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z+1).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y,z-1) != null && !world.getBlock(x,y,z-1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z-1).hasTag(BROKEN_BY_FLUIDS)) {

		} else {
			world.setBlock(x,y,z,0);
		}
	}
	@Overwrite
	protected int getBurnResultId(World world, int x, int y, int z) {
		return world.getBlockId(x,y,z);
	}
	@Overwrite
	public void onBlockAdded(World world, int x, int y, int z) {

		if (world.getBlock(x+1,y,z) != null && !world.getBlock(x+1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x+1,y,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x-1,y,z) != null && !world.getBlock(x-1,y,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x-1,y,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y+1,z) != null && !world.getBlock(x,y+1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y+1,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y-1,z) != null && !world.getBlock(x,y-1,z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y-1,z).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y,z+1) != null && !world.getBlock(x,y,z+1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z+1).hasTag(BROKEN_BY_FLUIDS)) {

		} else if (world.getBlock(x,y,z-1) != null && !world.getBlock(x,y,z-1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x,y,z-1).hasTag(BROKEN_BY_FLUIDS)) {

		} else {
			world.setBlock(x,y,z,0);
		}

		if (world.getBlockId(x, y - 1, z) != Block.obsidian.id || !((BlockPortal)Block.portalNether).tryToCreatePortal(world, x,y,z)) {
			if (!world.isBlockNormalCube(x, y - 1, z) && !this.canNeighborCatchFire(world, x,y,z)) {
				world.setBlockWithNotify(x,y,z, this.getBurnResultId(world, x,y,z));
			} else {
				world.scheduleBlockUpdate(x,y,z, this.id, this.tickRate());
			}
		}

	}

}
