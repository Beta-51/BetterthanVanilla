package lusiiplugin.mixin;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandError;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.commands.GamemodeCommand;
import net.minecraft.core.player.gamemode.Gamemode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GamemodeCommand.class, remap = false)
public class GamemodeCommandMixin extends Command {
	public GamemodeCommandMixin() {
		super("gamemode", new String[]{"gm"});
	}

	@Overwrite
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length == 0) {
			return false;
		} else {
			String gamemodeString = args[0];

			Gamemode gamemode;
			try {
				gamemode = Gamemode.gamemodesList[Integer.parseInt(gamemodeString)];
			} catch (Exception var8) {
				gamemode = getGamemode(gamemodeString);
			}

			if (gamemode == null) {
				throw new CommandError("Can't find gamemode \"" + gamemodeString + "\"!");
			} else {
				EntityPlayer player = sender.getPlayer();
				if (args.length > 1 && (sender.isAdmin() || sender.isConsole())) {
					player = handler.getPlayer(args[1]);
				}

				if (player == null) {
					throw new CommandError("You cannot do that!");
				} else {
					player.setGamemode(gamemode);
					handler.sendCommandFeedback(sender, "Set gamemode to " + gamemode.getLanguageKey().substring(9) + " for " + player.getDisplayName());
					return true;
				}
			}
		}
	}
	@Overwrite
	public boolean opRequired(String[] args) {
		return LusiiPlugin.gamemodeAll;
	}
	@Shadow
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/gamemode <gamemode>");
	}
	@Shadow
	private static Gamemode getGamemode(String string) {
		Gamemode[] var1 = Gamemode.gamemodesList;
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			Gamemode gamemode = var1[var3];
			if (string.equalsIgnoreCase(gamemode.getLanguageKey().substring(9))) {
				return gamemode;
			}
		}

		return null;
	}
}
