package lusiiplugin.mixin;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.projectile.EntityArrow;
import net.minecraft.core.entity.projectile.EntityArrowGolden;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = EntityArrow.class, remap = false)
public class EntityArrowMixin extends Entity {

	protected int xTile;
	protected int yTile;
	protected int zTile;
	protected int inTile;
	protected int field_28019_h;
	protected boolean inGround;
	public boolean doesArrowBelongToPlayer;
	public int arrowShake;
	public EntityLiving owner;
	protected int ticksInGround;
	protected int ticksInAir;
	protected float arrowSpeed;
	protected float arrowGravity;
	protected int arrowDamage;
	protected int arrowType;
	protected ItemStack stack;

	public EntityArrowMixin(World world) {
		super(world);
	}

	@Overwrite
	public void tick() {
		super.tick();
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			float f = MathHelper.sqrt_double(this.xd * this.xd + this.zd * this.zd);
			this.yRotO = this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / 3.1415927410125732);
			this.xRotO = this.xRot = (float)(Math.atan2(this.yd, (double)f) * 180.0 / 3.1415927410125732);
		}

		int i = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
		if (i > 0) {
			Block.blocksList[i].setBlockBoundsBasedOnState(this.world, this.xTile, this.yTile, this.zTile);
			AABB axisalignedbb = Block.blocksList[i].getCollisionBoundingBoxFromPool(this.world, this.xTile, this.yTile, this.zTile);
			if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3d.createVector(this.x, this.y, this.z))) {
				this.inGround = true;
			}
		}

		if (this.arrowShake > 0) {
			--this.arrowShake;
		}

		if (this.inGround) {
			int j = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
			int k = this.world.getBlockMetadata(this.xTile, this.yTile, this.zTile);
			if (j == this.inTile && k == this.field_28019_h) {
				++this.ticksInGround;
				if (this.ticksInGround >= 600) {
					this.remove();
				}

			} else {
				this.inGround = false;
				this.xd *= (double)(this.random.nextFloat() * 0.2F);
				this.yd *= (double)(this.random.nextFloat() * 0.2F);
				this.zd *= (double)(this.random.nextFloat() * 0.2F);
				//this.ticksInGround = 0; // this is a stupid idea. Such an easy way to lag servers.
				//this.ticksInAir = 0; // What's the point of this?
			}
		} else {

			++this.ticksInAir;
			Vec3d oldPos = Vec3d.createVector(this.x, this.y, this.z);
			Vec3d newPos = Vec3d.createVector(this.x + this.xd, this.y + this.yd, this.z + this.zd);
			HitResult movingobjectposition = this.world.checkBlockCollisionBetweenPoints(oldPos, newPos, false, true);
			oldPos = Vec3d.createVector(this.x, this.y, this.z);
			newPos = Vec3d.createVector(this.x + this.xd, this.y + this.yd, this.z + this.zd);
			if (movingobjectposition != null) {
				newPos = Vec3d.createVector(movingobjectposition.location.xCoord, movingobjectposition.location.yCoord, movingobjectposition.location.zCoord);
			}

			Entity entity = null;
			List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.addCoord(this.xd, this.yd, this.zd).expand(1.0, 1.0, 1.0));
			double d = 0.0;

			float f5;
			for(int l = 0; l < list.size(); ++l) {
				Entity entity1 = (Entity)list.get(l);
				if (entity1.isPickable() && (entity1 != this.owner || this.ticksInAir >= 5)) {
					f5 = 0.3F;
					AABB axisalignedbb1 = entity1.bb.expand((double)f5, (double)f5, (double)f5);
					HitResult movingobjectposition1 = axisalignedbb1.func_1169_a(oldPos, newPos);
					if (movingobjectposition1 != null) {
						double d1 = oldPos.distanceTo(movingobjectposition1.location);
						if (d1 < d || d == 0.0) {
							entity = entity1;
							d = d1;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new HitResult(entity);
			}

			float f1;
			if (movingobjectposition != null) {
				if (movingobjectposition.entity != null) {
					if (movingobjectposition.entity.hurt(this.owner, this.arrowDamage, DamageType.COMBAT)) {
						if (this.isOnFire()) {
							movingobjectposition.entity.fireHurt();
						}

						this.world.playSoundAtEntity((Entity)null, this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
						if (!(this.arrowType == 2)) {
							this.remove();
						}
					} else if (!(this.arrowType == 2)) {
						this.xd *= -0.10000000149011612;
						this.yd *= -0.10000000149011612;
						this.zd *= -0.10000000149011612;
						this.yRot += 180.0F;
						this.yRotO += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.xTile = movingobjectposition.x;
					this.yTile = movingobjectposition.y;
					this.zTile = movingobjectposition.z;
					this.inTile = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
					this.field_28019_h = this.world.getBlockMetadata(this.xTile, this.yTile, this.zTile);
					this.xd = (double)((float)(movingobjectposition.location.xCoord - this.x));
					this.yd = (double)((float)(movingobjectposition.location.yCoord - this.y));
					this.zd = (double)((float)(movingobjectposition.location.zCoord - this.z));
					f1 = MathHelper.sqrt_double(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
					this.x -= this.xd / (double)f1 * 0.05000000074505806;
					this.y -= this.yd / (double)f1 * 0.05000000074505806;
					this.z -= this.zd / (double)f1 * 0.05000000074505806;
					this.inGroundAction();
				}
			}

			this.x += this.xd;
			this.y += this.yd;
			this.z += this.zd;
			f1 = MathHelper.sqrt_double(this.xd * this.xd + this.zd * this.zd);
			this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / 3.1415927410125732);

			for(this.xRot = (float)(Math.atan2(this.yd, (double)f1) * 180.0 / 3.1415927410125732); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
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
			float f3 = this.arrowSpeed;
			f5 = this.arrowGravity;
			if (this.isInWater()) {
				for(int i1 = 0; i1 < 4; ++i1) {
					float f6 = 0.25F;
					this.world.spawnParticle("bubble", this.x - this.xd * (double)f6, this.y - this.yd * (double)f6, this.z - this.zd * (double)f6, this.xd, this.yd, this.zd);
				}

				f3 = 0.8F;
			}

			this.xd *= (double)f3;
			this.yd *= (double)f3;
			this.zd *= (double)f3;
			this.yd -= (double)f5;
			this.setPos(this.x, this.y, this.z);
		}
	}

	public double getRideHeight() {
		return (double)this.bbHeight * 0.0 - 1.5;
	}



	@Shadow
	protected void inGroundAction() {
		this.world.playSoundAtEntity((Entity)null, this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
		this.inGround = true;
		this.arrowShake = 7;
	}



	public Entity ejectRider() {
		Entity entity = this.passenger;
		if (entity == null) {
			return null;
		} else {
			this.passenger = null;
			entity.vehicle = null;
			int x = MathHelper.floor_double(this.x);
			int y = MathHelper.floor_double(this.y);
			int z = MathHelper.floor_double(this.z);
			entity.moveTo((double)x - 0.5, (double)y+1, (double)z + 0.5, entity.yRot, entity.xRot);
			this.remove();

			return entity;
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
