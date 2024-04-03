package lusiiplugin.commands;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class ColoursCommand extends Command {
	public ColoursCommand() {
		super("colours", "colors");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		sender.sendMessage("$$0 outputs §0White");
		sender.sendMessage("$$1 outputs §1Orange");
		sender.sendMessage("$$2 outputs §2Magenta");
		sender.sendMessage("$$3 outputs §3Light blue / Aqua");
		sender.sendMessage("$$4 outputs §4Yellow");
		sender.sendMessage("$$5 outputs §5Lime");
		sender.sendMessage("$$6 outputs §6Pink");
		sender.sendMessage("$$7 outputs §7Grey");
		sender.sendMessage("$$8 outputs §8Light Grey / Silver");
		sender.sendMessage("$$9 outputs §9Cyan / Turquoise");
		sender.sendMessage("$$a outputs §aPurple");
		sender.sendMessage("$$b outputs §bBlue");
		sender.sendMessage("$$c outputs §cBrown");
		sender.sendMessage("$$d outputs §dGreen");
		sender.sendMessage("$$e outputs §eRed");
		sender.sendMessage("$$f outputs §fBlack");
		sender.sendMessage("$$k outputs §kObfuscated §r(Obfuscated, §e§lOperator§r only.)");
		sender.sendMessage("$$l outputs §lBold");
		sender.sendMessage("$$m outputs §mStrikethrough");
		sender.sendMessage("$$n outputs §nUnderline");
		sender.sendMessage("$$o outputs §oItalic");
		sender.sendMessage("$$r §3resets colour to §rnormal!");
		return true;
	}
//
	public boolean opRequired(String[] args) {
		return false;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("§3/colours or /colors");
		sender.sendMessage("§5Display formatting code");
	}
}
