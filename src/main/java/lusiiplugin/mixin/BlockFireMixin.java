package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFire;
import net.minecraft.core.block.BlockMoss;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

	@Overwrite
	public int tickRate() {
		if (LusiiPlugin.staticFire) {
			return 2147483647;
		}
		else {
			return 40;
		}
	}

	public void setBurnResult(World world, int x, int y, int z) {
		world.setBlockWithNotify(x, y, z, this.getBurnResultId(world, x, y, z));
	}

	@ModifyVariable(method = "updateTick", name = "infiniBurn", at = @At("STORE"), require = 1,remap = false)
	public boolean modifyInfiniBurn(boolean old)
	{
		if (LusiiPlugin.staticFire)
		return true;
		else {
			return old;
		}
	}

	@Overwrite
	private boolean canNeighborCatchFire(World world, int x, int y, int z) {
		if (LusiiPlugin.staticFire) {
			return false;
		} else {
			if (this.canBlockCatchFire(world, x + 1, y, z)) {
				return true;
			} else if (this.canBlockCatchFire(world, x - 1, y, z)) {
				return true;
			} else if (this.canBlockCatchFire(world, x, y - 1, z)) {
				return true;
			} else if (this.canBlockCatchFire(world, x, y + 1, z)) {
				return true;
			} else if (this.canBlockCatchFire(world, x, y, z - 1)) {
				return true;
			} else {
				return this.canBlockCatchFire(world, x, y, z + 1);
			}
		}
	}
	@Overwrite
	public boolean canBlockCatchFire(WorldSource iblockaccess, int i, int j, int k) {
		if (LusiiPlugin.staticFire) {
			return true;
		}
		return this.chanceToEncourageFire[iblockaccess.getBlockId(i, j, k)] > 0;
	}
	@Overwrite
	public boolean canFirePersist(World world, int x, int y, int z) {
		if (LusiiPlugin.staticFire) {
			if (world.getBlock(x + 1, y, z) != null && !world.getBlock(x + 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x + 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x - 1, y, z) != null && !world.getBlock(x - 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x - 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y + 1, z) != null && !world.getBlock(x, y + 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y + 1, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y - 1, z) != null && !world.getBlock(x, y - 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y - 1, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y, z + 1) != null && !world.getBlock(x, y, z + 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z + 1).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y, z - 1) != null && !world.getBlock(x, y, z - 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z - 1).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else {
				return false;
			}
		}
		else return world.isBlockNormalCube(x, y - 1, z) || this.canNeighborCatchFire(world, x, y, z);
	}
	@Overwrite
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		if (LusiiPlugin.staticFire) {
			if (world.getBlock(x + 1, y, z) != null && !world.getBlock(x + 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x + 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x - 1, y, z) != null && !world.getBlock(x - 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x - 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y + 1, z) != null && !world.getBlock(x, y + 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y + 1, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y - 1, z) != null && !world.getBlock(x, y - 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y - 1, z).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y, z + 1) != null && !world.getBlock(x, y, z + 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z + 1).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else if (world.getBlock(x, y, z - 1) != null && !world.getBlock(x, y, z - 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z - 1).hasTag(BROKEN_BY_FLUIDS)) {
				return true;
			} else {
				return false;
			}
		}
		else return this.canFirePersist(world, x, y, z);
    }
	@Overwrite
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		if (LusiiPlugin.staticFire) {
		if (world.getBlock(x + 1, y, z) != null && !world.getBlock(x + 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x + 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
		} else if (world.getBlock(x - 1, y, z) != null && !world.getBlock(x - 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x - 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
		} else if (world.getBlock(x, y + 1, z) != null && !world.getBlock(x, y + 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y + 1, z).hasTag(BROKEN_BY_FLUIDS)) {
		} else if (world.getBlock(x, y - 1, z) != null && !world.getBlock(x, y - 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y - 1, z).hasTag(BROKEN_BY_FLUIDS)) {
		} else if (world.getBlock(x, y, z + 1) != null && !world.getBlock(x, y, z + 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z + 1).hasTag(BROKEN_BY_FLUIDS)) {
		} else if (world.getBlock(x, y, z - 1) != null && !world.getBlock(x, y, z - 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z - 1).hasTag(BROKEN_BY_FLUIDS)) {
		} else {
			world.setBlock(x, y, z, 0);
		}
	}
		else
		if (!world.isBlockNormalCube(x, y - 1, z) && !this.canNeighborCatchFire(world, x, y, z)) {
			world.setBlockWithNotify(x, y, z, this.getBurnResultId(world, x, y, z));
		}
	}
	@Overwrite
	protected int getBurnResultId(World world, int x, int y, int z) {
		if (LusiiPlugin.staticFire) {
			return world.getBlockId(x, y, z);
		}
		else {
			int id = world.getBlockId(x, y, z);
			Block stoneBlock = BlockMoss.getStoneBlock(id);
			return stoneBlock != null ? stoneBlock.id : 0;
		}
	}
	@Overwrite
	public void onBlockAdded(World world, int x, int y, int z) {
		if (LusiiPlugin.staticFire) {
			if (world.getBlock(x + 1, y, z) != null && !world.getBlock(x + 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x + 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
			} else if (world.getBlock(x - 1, y, z) != null && !world.getBlock(x - 1, y, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x - 1, y, z).hasTag(BROKEN_BY_FLUIDS)) {
			} else if (world.getBlock(x, y + 1, z) != null && !world.getBlock(x, y + 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y + 1, z).hasTag(BROKEN_BY_FLUIDS)) {
			} else if (world.getBlock(x, y - 1, z) != null && !world.getBlock(x, y - 1, z).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y - 1, z).hasTag(BROKEN_BY_FLUIDS)) {
			} else if (world.getBlock(x, y, z + 1) != null && !world.getBlock(x, y, z + 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z + 1).hasTag(BROKEN_BY_FLUIDS)) {
			} else if (world.getBlock(x, y, z - 1) != null && !world.getBlock(x, y, z - 1).hasTag(PLACE_OVERWRITES) && !world.getBlock(x, y, z - 1).hasTag(BROKEN_BY_FLUIDS)) {
			} else {
				world.setBlock(x, y, z, 0);
			}
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
