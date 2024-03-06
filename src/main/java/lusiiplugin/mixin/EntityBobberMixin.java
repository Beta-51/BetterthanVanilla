package lusiiplugin.mixin;

import com.mojang.nbt.CompoundTag;
import lusiiplugin.LusiiPlugin;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityBobber;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
@Mixin(value = EntityBobber.class, remap = false)
public class EntityBobberMixin extends Entity {
	private int xTile;
	private int yTile;
	private int zTile;
	private int inTile;
	private boolean inGround;
	public int shake;
	public EntityPlayer angler;
	private int ticksInGround;
	private int ticksInAir;
	private int ticksCatchable;
	public Entity bobber;
	private int field_6388_l;
	private double field_6387_m;
	private double field_6386_n;
	private double field_6385_o;
	private double field_6384_p;
	private double field_6383_q;
	private double velocityX;
	private double velocityY;
	private double velocityZ;
	public EntityBobberMixin(World world) {
		super(world);
	}

	@Overwrite
	public void tick() {
		super.tick();
		if (this.field_6388_l > 0) {
			double d = this.x + (this.field_6387_m - this.x) / (double)this.field_6388_l;
			double d1 = this.y + (this.field_6386_n - this.y) / (double)this.field_6388_l;
			double d2 = this.z + (this.field_6385_o - this.z) / (double)this.field_6388_l;

			double d4;
			for(d4 = this.field_6384_p - (double)this.yRot; d4 < -180.0; d4 += 360.0) {
			}

			while(d4 >= 180.0) {
				d4 -= 360.0;
			}

			this.yRot = (float)((double)this.yRot + d4 / (double)this.field_6388_l);
			this.xRot = (float)((double)this.xRot + (this.field_6383_q - (double)this.xRot) / (double)this.field_6388_l);
			--this.field_6388_l;
			this.setPos(d, d1, d2);
			this.setRot(this.yRot, this.xRot);
		} else {
			if (!this.world.isClientSide) {
				ItemStack itemstack = this.angler.getCurrentEquippedItem();
				if (this.angler.removed || !this.angler.isAlive() || itemstack == null || itemstack.getItem() != Item.toolFishingrod || this.distanceToSqr(this.angler) > 1024.0) {
					this.remove();
					this.angler.fishEntity = null;
					return;
				}

				if (this.bobber != null) {
					if (!this.bobber.removed) {
						this.x = this.bobber.x;
						this.y = this.bobber.bb.minY + (double)this.bobber.bbHeight * 0.8;
						this.z = this.bobber.z;
						return;
					}

					this.bobber = null;
				}
			}

			if (this.shake > 0) {
				--this.shake;
			}

			if (this.inGround) {
				int i = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
				if (i == this.inTile) {
					++this.ticksInGround;
					if (this.ticksInGround == 1200) {
						this.remove();
					}

					return;
				}

				this.inGround = false;
				this.xd *= (double)(this.random.nextFloat() * 0.2F);
				this.yd *= (double)(this.random.nextFloat() * 0.2F);
				this.zd *= (double)(this.random.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			} else {
				++this.ticksInAir;
			}

			Vec3d vec3d = Vec3d.createVector(this.x, this.y, this.z);
			Vec3d vec3d1 = Vec3d.createVector(this.x + this.xd, this.y + this.yd, this.z + this.zd);
			HitResult movingobjectposition = this.world.checkBlockCollisionBetweenPoints(vec3d, vec3d1);
			vec3d = Vec3d.createVector(this.x, this.y, this.z);
			vec3d1 = Vec3d.createVector(this.x + this.xd, this.y + this.yd, this.z + this.zd);
			if (movingobjectposition != null) {
				vec3d1 = Vec3d.createVector(movingobjectposition.location.xCoord, movingobjectposition.location.yCoord, movingobjectposition.location.zCoord);
			}

			Entity entity = null;
			List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.addCoord(this.xd, this.yd, this.zd).expand(1.0, 1.0, 1.0));
			double d3 = 0.0;

			double d7;
			for(int j = 0; j < list.size(); ++j) {
				Entity entity1 = (Entity)list.get(j);
				if (entity1.isPickable() && (entity1 != this.angler || this.ticksInAir >= 5)) {
					float f2 = 0.3F;
					AABB axisalignedbb = entity1.bb.expand((double)f2, (double)f2, (double)f2);
					HitResult movingobjectposition1 = axisalignedbb.func_1169_a(vec3d, vec3d1);
					if (movingobjectposition1 != null) {
						d7 = vec3d.distanceTo(movingobjectposition1.location);
						if (d7 < d3 || d3 == 0.0) {
							entity = entity1;
							d3 = d7;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new HitResult(entity);
			}

			if (movingobjectposition != null && movingobjectposition.entity != null && movingobjectposition.entity.hurt(this.angler, 0, DamageType.COMBAT)) {
				this.bobber = movingobjectposition.entity;
			}

			if (!this.inGround) {
				this.move(this.xd, this.yd, this.zd);
				float f = MathHelper.sqrt_double(this.xd * this.xd + this.zd * this.zd);
				this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / 3.1415927410125732);

				for(this.xRot = (float)(Math.atan2(this.yd, (double)f) * 180.0 / 3.1415927410125732); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
				}

				while(this.xRot - this.xRotO >= 180.0F) {
					this.xRotO += 360.0F;
				}

				while(this.yRot - this.yRotO < -180.0F) {
					this.yRotO -= 360.0F;
				}

				while(this.yRot - this.yRotO >= 180.0F) {
					this.yRotO += 360.0F;
				}

				this.xRot = this.xRotO + (this.xRot - this.xRotO) * 0.2F;
				this.yRot = this.yRotO + (this.yRot - this.yRotO) * 0.2F;
				float f1 = 0.92F;
				if (this.onGround || this.horizontalCollision) {
					f1 = 0.5F;
				}

				int k = 5;
				double d5 = 0.0;

				int catchRate;
				for(catchRate = 0; catchRate < k; ++catchRate) {
					double d8 = this.bb.minY + (this.bb.maxY - this.bb.minY) * (double)(catchRate + 0) / (double)k - 0.125 + 0.125;
					double d9 = this.bb.minY + (this.bb.maxY - this.bb.minY) * (double)(catchRate + 1) / (double)k - 0.125 + 0.125;
					AABB axisalignedbb1 = AABB.getBoundingBoxFromPool(this.bb.minX, d8, this.bb.minZ, this.bb.maxX, d9, this.bb.maxZ);
					if (this.world.isAABBInMaterial(axisalignedbb1, Material.water)) {
						d5 += 1.0 / (double)k;
					}
				}

				if (d5 > 0.0) {
					if (this.ticksCatchable > 0) {
						--this.ticksCatchable;
					} else {
						catchRate = 500;
						int rainRate = 0;
						int algaeRate = 0;
						if (this.world.canBlockBeRainedOn(MathHelper.floor_double(this.x), MathHelper.floor_double(this.y) + 1, MathHelper.floor_double(this.z))) {
							rainRate = 200;
						}

						if (this.world.getBlockId(MathHelper.floor_double(this.x), MathHelper.floor_double(this.y) + 1, MathHelper.floor_double(this.z)) == Block.algae.id) {
							algaeRate = 100;
						}

						catchRate = catchRate - rainRate - algaeRate;
						if (this.random.nextInt(catchRate) == 0) {
							if (LusiiPlugin.addedTicksCatchable <=-40) {
								this.remove();
							} else {
								this.ticksCatchable = this.random.nextInt(30) + 10 + LusiiPlugin.addedTicksCatchable;
							}
							this.yd -= 0.20000000298023224;
							this.world.playSoundAtEntity(this, "random.splash", 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
							float f3 = (float)MathHelper.floor_double(this.bb.minY);

							int j1;
							float f7;
							float f5;
							for(j1 = 0; (float)j1 < 1.0F + this.bbWidth * 20.0F; ++j1) {
								f5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
								f7 = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
								this.world.spawnParticle("bubble", this.x + (double)f5, (double)(f3 + 1.0F), this.z + (double)f7, this.xd, this.yd - (double)(this.random.nextFloat() * 0.2F), this.zd);
							}

							for(j1 = 0; (float)j1 < 1.0F + this.bbWidth * 20.0F; ++j1) {
								f5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
								f7 = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
								this.world.spawnParticle("splash", this.x + (double)f5, (double)(f3 + 1.0F), this.z + (double)f7, this.xd, this.yd, this.zd);
							}
						}
					}
				}

				if (this.ticksCatchable > 0) {
					this.yd -= (double)(this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat()) * 0.2;
				}

				d7 = d5 * 2.0 - 1.0;
				this.yd += 0.03999999910593033 * d7;
				if (d5 > 0.0) {
					f1 = (float)((double)f1 * 0.9);
					this.yd *= 0.8;
				}

				this.xd *= (double)f1;
				this.yd *= (double)f1;
				this.zd *= (double)f1;
				this.setPos(this.x, this.y, this.z);
			}
		}
	}




	@Shadow
	protected void init() {

	}

	@Shadow
	public void readAdditionalSaveData(CompoundTag compoundTag) {

	}

	@Shadow
	public void addAdditionalSaveData(CompoundTag compoundTag) {

	}


}
