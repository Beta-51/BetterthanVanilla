package lusiiplugin.mixin;

import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityPlayer.class, remap = false)
public class EntityPlayerMixin extends EntityLiving {

	public EntityPlayerMixin(World world) {
		super(world);
	}

	@Shadow
	protected void init() {

	}

	@Overwrite
	public double getRidingHeight() {
		return this.heightOffset + 0.42;
	}

}
