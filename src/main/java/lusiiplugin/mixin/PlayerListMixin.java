package lusiiplugin.mixin;

import lusiiplugin.utils.PlayerData;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, remap = false)
public class PlayerListMixin {
	@Inject(at = @At("TAIL"), method = "playerLoggedIn")
	public void onLogin(EntityPlayerMP player, CallbackInfo ci) {
		PlayerData.update(player);
	}

	@Inject(at = @At("TAIL"), method = "playerLoggedOut")
	public void onLogout(EntityPlayerMP entityplayermp, CallbackInfo ci) {
		PlayerData.get(entityplayermp).save();
	}
}
