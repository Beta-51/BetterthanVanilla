package lusiiplugin.commands;

import net.minecraft.core.net.command.*;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.util.Arrays;

public class SudoCommand extends Command {
	public SudoCommand() {
		super("sudo", "doas");
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length < 2) return false;

		String username = args[0];
		String commandTitle = args[1];
		String[] commandArgs = null;
		if (args.length > 2) {
			commandArgs = Arrays.copyOfRange(args, 2, args.length);
		}

		EntityPlayerMP targetPlayer;

		if (handler.playerExists(username)) {
			targetPlayer = (EntityPlayerMP) handler.getPlayer(username);
		} else {
			sender.sendMessage(TextFormatting.RED + "Player not found: " + TextFormatting.ORANGE + username);
			return false;
		}
		ServerPlayerCommandSender targetPlayerSender = new ServerPlayerCommandSender(handler.asServer().minecraftServer, targetPlayer);

		for (Command command : Commands.commands) {
			if (!command.isName(commandTitle)) continue;
			if (!targetPlayer.isOperator() && command.opRequired(commandArgs)) {
				sender.sendMessage(TextFormatting.ORANGE + username + TextFormatting.RED + "doesn't have permission to use this command!");
				return true;
			}
			try {
				boolean success = command.execute(handler, targetPlayerSender, commandArgs);
				if (!success) {
					sender.sendMessage(TextFormatting.RED + "Error: invalid arguments");
					command.sendCommandSyntax(handler, sender);
				}
			} catch (CommandError e) {
				sender.sendMessage(TextFormatting.RED + e.getMessage());
			}
			return true;
		}

		sender.sendMessage(TextFormatting.RED + "Command not found: " + TextFormatting.ORANGE + commandTitle);
		return false;
	}

	@Override
	public boolean opRequired(String[] strings) {
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/sudo §4<username> <command>");
		sender.sendMessage("§5Run a command as another user.");
	}
}
