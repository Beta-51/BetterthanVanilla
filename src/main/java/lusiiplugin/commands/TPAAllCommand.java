package lusiiplugin.commands;

import lusiiplugin.utils.PlayerData;
import lusiiplugin.utils.PlayerData.TPInfo.RequestType;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.Objects;


public class TPAAllCommand extends Command {
	public TPAAllCommand() {
		super("tpaall", "tpall");
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/tpall");
		sender.sendMessage("§5Request all players to teleport to you");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		List<EntityPlayerMP> players = handler.asServer().minecraftServer.playerList.playerEntities;
		String destUser = sender.getPlayer().username;

		for (EntityPlayerMP p : players) {
			if (Objects.equals(p.username, destUser)) continue;

			PlayerData playerData = PlayerData.get(p);
			boolean isOnlyRequest = playerData.tpInfo().sendRequest(destUser, RequestType.TPAHERE);
			if (isOnlyRequest) {
				p.addChatMessage("§4" + destUser + "§1 has sent you a request to teleport to them.");
				p.addChatMessage("§5/tpyes §1to accept, §e/tpno §1to deny.");
			}
		}
		sender.sendMessage("§4Sent a request to all players.");

		return true;
	}
}
