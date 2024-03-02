package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockBed;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumSleepStatus;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Iterator;

import static net.minecraft.core.block.BlockBed.*;

@Mixin(value = BlockBed.class,remap = false)
public class BlockBedMixin extends Block {

	public BlockBedMixin(String key, int id, Material material) {
		super(key, id, material);
	}
	@Overwrite
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		if (world.isClientSide) {
			return true;
		} else {
			int meta = world.getBlockMetadata(x, y, z);
			if (!isBlockFootOfBed(meta)) {
				int dir = getDirectionFromMetadata(meta);
				x += headBlockToFootBlockMap[dir][0];
				z += headBlockToFootBlockMap[dir][1];
				if (world.getBlockId(x, y, z) != this.id) {
					return true;
				}

				meta = world.getBlockMetadata(x, y, z);
			}

			if (!world.worldType.mayRespawn()) {
				if (!LusiiPlugin.disableBedExplosion) {
				double d = (double)x + 0.5;
				double d1 = (double)y + 0.5;
				double d2 = (double)z + 0.5;
				world.setBlockWithNotify(x, y, z, 0);
				int dir = getDirectionFromMetadata(meta);
				x += headBlockToFootBlockMap[dir][0];
				z += headBlockToFootBlockMap[dir][1];
				if (world.getBlockId(x, y, z) == this.id) {
					world.setBlockWithNotify(x, y, z, 0);
					d = (d + (double)x + 0.5) / 2.0;
					d1 = (d1 + (double)y + 0.5) / 2.0;
					d2 = (d2 + (double)z + 0.5) / 2.0;
				}
					world.newExplosion((Entity) null, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), 5.0F, true, false);
				}
				return true;
			} else {
				if (isBedOccupied(meta)) {
					EntityPlayer player1 = null;
					Iterator var8 = world.players.iterator();

					while(var8.hasNext()) {
						EntityPlayer p = (EntityPlayer)var8.next();
						if (p.isPlayerSleeping()) {
							ChunkCoordinates pos = p.bedChunkCoordinates;
							if (pos.x == x && pos.y == y && pos.z == z) {
								player1 = p;
							}
						}
					}

					if (player1 != null) {
						player.addChatMessage("bed.occupied");
						return true;
					}

					setBedOccupied(world, x, y, z, false);
				}

				if (player.sleepInBedAt(x, y, z) == EnumSleepStatus.OK) {
					setBedOccupied(world, x, y, z, true);
					return true;
				} else {
					return true;
				}
			}
		}
	}
}
