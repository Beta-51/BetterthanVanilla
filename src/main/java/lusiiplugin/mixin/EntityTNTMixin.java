package lusiiplugin.mixin;

import com.mojang.nbt.CompoundTag;
import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityTNT;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityTNT.class, remap = false)
public class EntityTNTMixin extends Entity {

	public EntityTNTMixin(World world) {
		super(world);
	}
	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
			if (this.world.dimension.id == 0 && LusiiPlugin.DisableTNTOverworld <= this.y) {this.remove();}
			if (this.world.dimension.id == 1 && LusiiPlugin.DisableTNTNether <= this.y) {this.remove();}
			if (this.world.dimension.id == 2 && LusiiPlugin.DisableTNTSky <= this.y) {this.remove();}
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
