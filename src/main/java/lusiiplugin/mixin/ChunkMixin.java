package lusiiplugin.mixin;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Chunk.class, remap = false)
public class ChunkMixin {
	@Shadow
	@Final
	public int xPosition;

	@Shadow
	@Final
	public int zPosition;

	@Inject(
		method = "addEntity",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Thread;dumpStack()V"
		)
	)
	public void debugAddEntity(Entity entity, CallbackInfo ci) {
		System.out.println("Chunk pos: " + this.xPosition + " " + this.zPosition);
		System.out.println("Problem entity: " + entity.getClass().getName());
		System.out.println("Entity XYZ | entity's chunk XZ: " + entity.x + " " + entity.y  + " " + entity.z + " | " + entity.chunkCoordX + " " + entity.chunkCoordZ);

		((IEntityMixin) entity).invokeResetPos();
	}
}
