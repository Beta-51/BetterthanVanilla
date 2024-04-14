package lusiiplugin.mixin;

import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityPlayer.class, remap = false)
public class EntityPlayerMixin extends EntityLiving {

	@Shadow
	protected boolean isDwarf;

	public EntityPlayerMixin(World world) {
		super(world);
	}

	@Shadow
	protected void init() {

	}

	@Overwrite
	public double getRidingHeight() {
		double rideOffset = 0.5;

		if (this.vehicle instanceof EntityPlayer) {
			rideOffset = -0.12;
		}
		return this.isDwarf ? this.heightOffset + (rideOffset / 10.0) : (this.heightOffset - rideOffset);
	}
}
