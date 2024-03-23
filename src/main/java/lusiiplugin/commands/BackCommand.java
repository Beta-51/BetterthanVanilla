package lusiiplugin.commands;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.PlayerTPInfo;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.*;
import net.minecraft.core.util.phys.Vec3d;

public class BackCommand extends Command {
	public BackCommand() {
		super("back");
	}

	public boolean opRequired(String[] args) {
		return !LusiiPlugin.BackCommand;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/back");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole()) return true;

		EntityPlayer p = sender.getPlayer();

		if (p.dimension != 0) {
			sender.sendMessage("§4You may only use this in the overworld!");
			return true;
		}
		PlayerTPInfo tpInfo = LusiiPlugin.getTPInfo(p);

		if (tpInfo.canTP()) {
			if (tpInfo.atNewPos(p)) {
				Vec3d lastPos = tpInfo.getLastPos();

                tpInfo.update(p);
				LusiiPlugin.teleport(p, lastPos);

				sender.sendMessage("§4Went back.");
			} else {
				sender.sendMessage("§4You have not moved!");
			}

        } else {
			int waitTime = tpInfo.cooldown();
			sender.sendMessage("§4Teleport available in §1" + waitTime + "§4 seconds.");
        }

        return true;
	}
}
