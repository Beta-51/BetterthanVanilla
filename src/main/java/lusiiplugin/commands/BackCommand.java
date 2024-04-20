package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerData;
import lusiiplugin.utils.WorldPosition;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.*;

public class BackCommand extends Command {
	public BackCommand() {
		super("back");
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.BackCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/back");
		sender.sendMessage("§5Teleport to where you last teleported from.");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer p = sender.getPlayer();
		PlayerData.TPInfo tpInfo = PlayerData.get(p).tpInfo();

		if (tpInfo.canTP() || sender.isAdmin()) {
			if (tpInfo.atNewPos()) {
				WorldPosition lastPos = tpInfo.getLastPos();

                tpInfo.update();

				if (LusiiPlugin.teleport(p, lastPos)) {
					sender.sendMessage("§4Went back.");
				}
			} else {
				sender.sendMessage("§4You have not moved!");
			}

        } else if (!sender.isAdmin()) {
			int waitTime = tpInfo.cooldown();
			sender.sendMessage("§4Teleport available in §1" + waitTime + "§4 seconds.");
        }

        return true;
	}
}
