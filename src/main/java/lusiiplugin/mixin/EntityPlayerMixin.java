package lusiiplugin.mixin;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPlayer.class, remap = false)
public class EntityPlayerMixin extends Entity {

	public EntityPlayerMixin(World world) {
		super(world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (this.vehicle instanceof EntityPlayer) {
			this.noPhysics = true;
			this.collision = false;
			if (((EntityPlayer) this.vehicle).isSneaking()) {
				this.vehicle = null;
			}
		}
	}









	protected void init() {

	}

	public void readAdditionalSaveData(CompoundTag compoundTag) {

	}

	public void addAdditionalSaveData(CompoundTag compoundTag) {

	}

	@Overwrite
	public double getRidingHeight() {
		return (double)(this.heightOffset) + .42;
	}

}
