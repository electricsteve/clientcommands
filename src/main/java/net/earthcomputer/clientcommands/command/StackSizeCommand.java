package net.earthcomputer.clientcommands.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;

import static net.minecraft.command.arguments.ItemStackArgumentType.*;
import static com.mojang.brigadier.arguments.IntegerArgumentType.*;
import static net.earthcomputer.clientcommands.command.ClientCommandManager.*;
import static net.minecraft.server.command.CommandManager.*;

public class StackSizeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        addClientSideCommand("cstacksize");

        dispatcher.register(literal("cstacksize")
            .then(argument("count", integer(0))
                .then(argument("item", itemStack())
                .executes(ctx -> {
                    ItemStack stack = getItemStackArgument(ctx, "item").createStack(1, false);
                    return getStackSize(ctx.getSource(), stack, getInteger(ctx, "count"));
                }))
            .executes(ctx -> {
                return getStackSize(ctx.getSource(), getInteger(ctx, "count"));
            })));
    }

    public static int getStackSize(ServerCommandSource source, ItemStack stack, int count) {
        int stacks = count / stack.getMaxCount();
        int remainder = count % stack.getMaxCount();

        if (stack.isEmpty()) {
            if (remainder == 0) {
                sendFeedback(new TranslatableText("commands.cstacksize.success.empty.exact", count, stacks));
            } else {
                sendFeedback(new TranslatableText("commands.cstacksize.success.empty", count, stacks, remainder));
            }
        } else {
            Text itemText = stack.toHoverableText();
            if (remainder == 0) {
                sendFeedback(new TranslatableText("commands.cstacksize.success.exact", count, itemText, stacks));
            } else {
                sendFeedback(new TranslatableText("commands.cstacksize.success", count, itemText, stacks, remainder));
            }
        }

        return 1;
    }

    public static int getStackSize(ServerCommandSource source, int count) {
        ItemStack heldStack = MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND).copy();
        return getStackSize(source, heldStack, count);
    }

}
