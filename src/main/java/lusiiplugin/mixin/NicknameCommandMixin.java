package lusiiplugin.mixin;

import net.minecraft.core.net.command.*;
import net.minecraft.core.net.command.commands.NicknameCommand;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static lusiiplugin.LusiiPlugin.NickLength;

@Mixin(value = NicknameCommand.class, remap = false)
public class NicknameCommandMixin extends ServerCommand {
	public NicknameCommandMixin(MinecraftServer server) {
		super(server, "nickname", "nick");
	}

	@Shadow
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		return true;
	}

	@ModifyConstant(method = "execute", constant = @Constant(intValue = 16))
	private int overrideMaxLength(int len) {
		return NickLength;
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
