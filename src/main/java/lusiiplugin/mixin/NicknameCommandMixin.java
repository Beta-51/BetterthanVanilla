package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.ClearCommand;
import net.minecraft.core.net.command.commands.NicknameCommand;
import net.minecraft.core.net.packet.Packet72UpdatePlayerProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Arrays;

import static lusiiplugin.LusiiPlugin.NickLength;

@Mixin(value = NicknameCommand.class, remap = false)
public class NicknameCommandMixin extends ServerCommand {
	public NicknameCommandMixin(MinecraftServer server) {
		super(server, "nickname", new String[]{"nick"});
	}
	@Overwrite
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length == 0) {
			return false;
		} else {
			EntityPlayerMP player = (EntityPlayerMP)sender.getPlayer();
			StringBuilder enteredNicknameStr;
			int i;
			String enteredNickname;
			if (args[0].equalsIgnoreCase("set")) {
				if (!sender.isPlayer()) {
					throw new CommandError("Must be used by a player!");
				} else if (args.length == 1) {
					throw new CommandError("Enter a nickname!");
				} else {
					enteredNicknameStr = new StringBuilder();

					for(i = 1; i < args.length; ++i) {
						enteredNicknameStr.append(args[i]).append(" ");
					}

					enteredNickname = enteredNicknameStr.substring(0, enteredNicknameStr.length() - 1);
					if (enteredNickname.length() > NickLength) {
						enteredNickname = enteredNickname.substring(0, NickLength);
					}

					player.nickname = enteredNickname;
					player.hadNicknameSet = true;
					player.mcServer.playerList.sendPacketToAllPlayers(new Packet72UpdatePlayerProfile(player.username, player.nickname, player.score, player.chatColor, true, player.isOperator()));
					sender.sendMessage("You set your nickname to " + player.getDisplayName());
					return true;
				}
			} else if (args[0].equalsIgnoreCase("get")) {
				if (!sender.isPlayer()) {
					throw new CommandError("Must be used by a player!");
				} else if (args.length != 1) {
					return false;
				} else {
					sender.sendMessage("Your nickname is " + player.getDisplayName());
					return true;
				}
			} else if (args[0].equalsIgnoreCase("clear")) {
				if (!sender.isPlayer()) {
					throw new CommandError("Must be used by a player!");
				} else if (args.length != 1) {
					return false;
				} else {
					player.nickname = "";
					player.hadNicknameSet = true;
					player.mcServer.playerList.sendPacketToAllPlayers(new Packet72UpdatePlayerProfile(player.username, player.nickname, player.score, player.chatColor, true, player.isOperator()));
					sender.sendMessage("You cleared your nickname.");
					return true;
				}
			} else {
				player = (EntityPlayerMP)handler.getPlayer(args[0]);
				if (player == null) {
					throw new CommandError("Unknown username!");
				} else if (args.length < 2) {
					return false;
				} else if (!sender.isAdmin() && !sender.isConsole()) {
					return false;
				} else if (!args[1].equalsIgnoreCase("set")) {
					if (args[1].equalsIgnoreCase("get")) {
						if (args.length != 2) {
							return false;
						} else {
							sender.sendMessage("" + player.username + "'s nickname is " + player.getDisplayName());
							return true;
						}
					} else if (args[1].equalsIgnoreCase("clear")) {
						if (args.length != 2) {
							return false;
						} else {
							player.nickname = "";
							player.hadNicknameSet = true;
							player.mcServer.playerList.sendPacketToAllPlayers(new Packet72UpdatePlayerProfile(player.username, player.nickname, player.score, player.chatColor, true, player.isOperator()));
							sender.sendMessage("Cleared " + player.username + "'s nickname.");
							handler.sendMessageToPlayer(player, "Your nickname was cleared.");
							return true;
						}
					} else {
						return false;
					}
				} else if (args.length < 3) {
					return false;
				} else {
					enteredNicknameStr = new StringBuilder();

					for(i = 2; i < args.length; ++i) {
						enteredNicknameStr.append(args[i]).append(" ");
					}

					enteredNickname = enteredNicknameStr.substring(0, enteredNicknameStr.length() - 1);
					if (enteredNickname.length() > NickLength) {
						enteredNickname = enteredNickname.substring(0, NickLength);
					}

					player.nickname = enteredNickname;
					player.hadNicknameSet = true;
					player.mcServer.playerList.sendPacketToAllPlayers(new Packet72UpdatePlayerProfile(player.username, player.nickname, player.score, player.chatColor, true, player.isOperator()));
					sender.sendMessage("Set " + player.username + "'s nickname to " + player.getDisplayName());
					handler.sendMessageToPlayer(player, "Your nickname was set to " + player.getDisplayName());
					return true;
				}
			}
		}
	}

	public boolean opRequired(String[] args) {
		return false;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/nickname set <value>");
		sender.sendMessage("/nickname get");
		sender.sendMessage("/nickname clear");
		if (sender.isConsole() || sender.isAdmin()) {
			sender.sendMessage("/nickname <username> set <value>");
			sender.sendMessage("/nickname <username> get");
			sender.sendMessage("/nickname <username> clear");
		}

	}
}
