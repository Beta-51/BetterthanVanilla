package turniplabs.examplemod.mixin;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.HitResult;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.projectile.EntityFireball;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
@Mixin(value = EntityFireball.class, remap = false)
public class EntityFireballMixin extends Entity {

	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private boolean inGround = false;
	public int shake = 0;
	public EntityLiving owner;
	private int field_9396_k;
	private int ticksInAir = 0;
	public double field_9405_b;
	public double field_9404_c;
	public double field_9403_d;

	public EntityFireballMixin(World world) {
		super(world);
	}

	@Shadow
	protected void init() {

	}

	public void tick() {
		if (this.getPassenger() != null) {

			this.xd = 0;
			this.yd = 0;
			this.zd = 0;
			this.remainingFireTicks = 0;
			this.inGround = true;
			this.field_9396_k = 1200;
			return;
		}


		super.tick();
		this.remainingFireTicks = 10;
		if (this.shake > 0) {
			--this.shake;
		}

		if (this.inGround) {
			int i = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
			if (i == this.inTile) {
				++this.field_9396_k;
				if (this.field_9396_k >= 1200) {
					this.remove();
				}

				return;
			}

			this.inGround = false;
			this.xd *= (double)(this.random.nextFloat() * 0.5F);
			this.yd *= (double)(this.random.nextFloat() * 0.5F);
			this.zd *= (double)(this.random.nextFloat() * 0.5F);
			this.field_9396_k = 0;
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
		double d = 0.0;

		for(int j = 0; j < list.size(); ++j) {
			Entity entity1 = (Entity)list.get(j);
			if (entity1.isPickable() && (entity1 != this.owner || this.ticksInAir >= 25)) {
				float f2 = 0.3F;
				AABB axisalignedbb = entity1.bb.expand((double)f2, (double)f2, (double)f2);
				HitResult movingobjectposition1 = axisalignedbb.func_1169_a(vec3d, vec3d1);
				if (movingobjectposition1 != null) {
					double d1 = vec3d.distanceTo(movingobjectposition1.location);
					if (d1 < d || d == 0.0) {
						entity = entity1;
						d = d1;
					}
				}
			}
		}

		this.world.spawnParticle("flame", this.x, this.y, this.z, this.xd * 0.05000000074505806, this.yd * 0.05000000074505806 - 0.10000000149011612, this.zd * 0.05000000074505806);
		this.world.spawnParticle("flame", this.x + this.xd * 0.5, this.y + this.yd * 0.5, this.z + this.zd * 0.5, this.xd * 0.05000000074505806, this.yd * 0.05000000074505806 - 0.10000000149011612, this.zd * 0.05000000074505806);
		if (entity != null) {
			movingobjectposition = new HitResult(entity);
		}

		if (movingobjectposition != null) {
			if (!this.world.isClientSide) {
				if (movingobjectposition.entity != null && !movingobjectposition.entity.hurt(this.owner, 0, DamageType.COMBAT)) {
				}

				this.world.newExplosion(this, this.x, this.y, this.z, 1.5F, true, false);
			}

			this.remove();
		}

		this.x += this.xd;
		this.y += this.yd;
		this.z += this.zd;
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
		float f1 = 1.0F;
		if (this.isInWater()) {
			for(int k = 0; k < 4; ++k) {
				float f3 = 0.25F;
				this.world.spawnParticle("bubble", this.x - this.xd * (double)f3, this.y - this.yd * (double)f3, this.z - this.zd * (double)f3, this.xd, this.yd, this.zd);
			}

			f1 = 0.8F;
		}

		this.xd += this.field_9405_b;
		this.yd += this.field_9404_c;
		this.zd += this.field_9403_d;
		this.xd *= (double)f1;
		this.yd *= (double)f1;
		this.zd *= (double)f1;
		this.world.spawnParticle("largesmoke", this.x, this.y, this.z, 0.0, 0.0, 0.0);
		this.setPos(this.x, this.y, this.z);
	}
	@Shadow
	public void readAdditionalSaveData(CompoundTag compoundTag) {

	}
	@Shadow
	public void addAdditionalSaveData(CompoundTag compoundTag) {

	}


}
