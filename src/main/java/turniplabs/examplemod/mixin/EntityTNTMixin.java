package turniplabs.examplemod.mixin;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityTNT;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityTNT.class, remap = false)
public class EntityTNTMixin extends Entity {

	public EntityTNTMixin(World world) {
		super(world);
	}
	@Overwrite
	public void tick() {
	this.remove();
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
