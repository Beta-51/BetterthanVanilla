package lusiiplugin.mixin;

import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.HelpCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = HelpCommand.class, remap = false)
public class HelpCommandMixin extends Command {
	public HelpCommandMixin() { super("help", "?"); }

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int itemsPerPage = 10;
		int pageNumber = 1;

		if (args.length > 0) {
			try {
				pageNumber = Integer.parseInt(args[0]);
			} catch (NumberFormatException ignored) {
				getCommand(handler, sender, args);
				return true;
			}
		}

		List<Command> allCmds = Commands.commands;
		if (!sender.isAdmin()) { // remove non op commands for non ops
			allCmds = allCmds.stream()
				.filter(c -> !c.opRequired(new String[0]))
				.collect(Collectors.toList());
		}
		boolean outOfBounds = (pageNumber - 1) * itemsPerPage >= allCmds.size();
		if (pageNumber < 1 || outOfBounds) pageNumber = 1; // stay in your lane

		List<Command> cmdsPage = getCommandPage(allCmds, pageNumber, itemsPerPage);

		for (Command cmd : cmdsPage) {
			String helpLine = TextFormatting.LIGHT_BLUE + cmd.getName();

			if (cmd.getNames().size() > 1) {
				String alts = String.join("§8, §4", cmd.getNames());
				helpLine += TextFormatting.LIGHT_GRAY + ": " + TextFormatting.YELLOW + alts;
			}
			sender.sendMessage(helpLine);
		}
		return true;
	}

	@Unique
	private List<Command> getCommandPage(List<Command> commands, int pageNumber, int itemsPerPage) {
		int totalItems = commands.size();
		int startIndex = (pageNumber - 1) * itemsPerPage;

		int endIndex = startIndex + itemsPerPage;
		endIndex = Math.min(endIndex, totalItems); // Ensure the end index does not exceed the list size

		return commands.subList(startIndex, endIndex);
	}

	@Unique
	private void getCommand(CommandHandler handler, CommandSender sender, String[] args) {
		Command command = Commands.getCommand(args[0]);
		if (command == null) {
			throw new CommandError("Can't find command: \"" + args[0] + "\"");
		}
		if (!sender.isAdmin() && command.opRequired(args)) {
			throw new CommandError("You don't have permission to use this command!");
		}
		if (command.getNames().size() > 1) {
			StringBuilder aliasStr = new StringBuilder();
			aliasStr.append(TextFormatting.LIGHT_BLUE).append("Aliases: ");
			for (int i = 0; i < command.getNames().size(); ++i) {
				if (i > 0) {
					aliasStr.append(TextFormatting.LIGHT_GRAY)
						.append(", ");
				}
				aliasStr.append(TextFormatting.YELLOW)
					.append(command.getNames().get(i));
			}
			sender.sendMessage(aliasStr.toString());
		}
		sender.sendMessage(TextFormatting.LIGHT_BLUE + "Syntax: ");
		command.sendCommandSyntax(handler, sender);
	}

	@Shadow
	public boolean opRequired(String[] strings) {
		return false;
	}

	@Override
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/help [page]");
		sender.sendMessage("/help [command]");
	}
}
