package lusiiplugin.mixin;

import lusiiplugin.utils.PlayerData;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class EntityPlayerMPMixin {
	@Mixin(value = EntityPlayerMP.class, remap = false)
	public abstract static class Root extends EntityPlayer {
		protected Root(World world) {
			super(world);
		}

		@Inject(method = "tick", at = @At("HEAD"))
		private void tickPlayerData(CallbackInfo ci) {
			PlayerData data = PlayerData.get(this);
			if (data != null) {
				data.tick();
			}
		}

		@Inject(method = "onDeath", at = @At("HEAD"))
		public void saveOnDeath(Entity entity, CallbackInfo ci) {
			PlayerData.get(this).save();
		}
	}

	@Mixin(value = EntityPlayerMP.class, remap = false)
	public static class Data implements PlayerData.Interface {
		@Unique
		PlayerData playerData;

		@Override
		public PlayerData betterthanVanilla$getPlayerData() {
			return this.playerData;
		}

		@Override
		public void betterthanVanilla$setPlayerData(EntityPlayer player) {
			this.playerData = new PlayerData(player);
		}
	}

	@Mixin(value = EntityPlayerMP.class, remap = false)
	public interface Interface {
		@Invoker("getTileEntityInfo")
		void getTEInfo(TileEntity tileEntity);
	}
}
