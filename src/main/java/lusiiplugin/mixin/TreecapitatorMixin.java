package lusiiplugin.mixin;

import lusiiplugin.utils.PlayerData;
import net.minecraft.core.data.gamerule.TreecapitatorHelper;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TreecapitatorHelper.class, remap = false)
public class TreecapitatorMixin {
	@Shadow
	@Final
	public EntityPlayer player;

	@Inject(
		method = "chopTree",
		at = @At("HEAD"),
		cancellable = true
	)
	private void disableTreecapPerList(CallbackInfoReturnable<Boolean> cir) {
		if (PlayerData.get(player).treecap().isActive()) {
			cir.setReturnValue(false);
		}
	}
}
