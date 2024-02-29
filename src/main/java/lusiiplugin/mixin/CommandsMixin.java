package lusiiplugin.mixin;


import lusiiplugin.*;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import lusiiplugin.LusiiPlugin.*;

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
	}
}
