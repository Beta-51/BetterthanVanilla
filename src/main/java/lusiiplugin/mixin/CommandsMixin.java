package lusiiplugin.mixin;


import lusiiplugin.commands.*;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Commands.class, remap = false)
public final class CommandsMixin {
	@Shadow
	public static List<Command> commands;
	@Inject(method = "initCommands", at = @At("TAIL"))
	private static void initCommands(CallbackInfo ci) {
			commands.add(new MOTDCommand());
			commands.add(new InfoCommand());
			commands.add(new SitCommand());
			commands.add(new ColoursCommand());
			commands.add(new FixCommand());
			commands.add(new SethomeCommand());
			commands.add(new DelhomeCommand());
			commands.add(new HomeCommand());
			commands.add(new HomesCommand());
			commands.add(new RulesCommand());
			commands.add(new OPChatCommand());
			commands.add(new InvseeCommand());
			commands.add(new CraftingCommand());
			commands.add(new PingCommand());
			commands.add(new RTPCommand());
			commands.add(new CrashCommand());
			commands.add(new PayCommand());
			commands.add(new BackCommand());
			commands.add(new TPAAllCommand());
			commands.add(new TPACommand());
			commands.add(new TPConfirmCommand());
			commands.add(new TPDenyCommand());
			commands.add(new TPRequestsCommand());
	}
}
