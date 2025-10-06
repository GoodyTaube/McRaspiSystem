package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemConverterCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("convert").executes(context -> {
            Entity entity = context.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }
            McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
            RaspiPlayer raspiPlayer = Raspi.players().get(player);
            ItemStack convertRequest = player.getInventory().getItemInMainHand().clone();
            if (convertRequest.getType().equals(Material.AIR)) {
                raspiPlayer.sendMessage("<gradient:red:yellow>Du musst schon ein Item in der Hand haben... lol", true);
                return Command.SINGLE_SUCCESS;
            }
            convertRequest.setAmount(1);
            plugin.getModule().getItemConverterManager().set(convertRequest);
            raspiPlayer.sendMessage(String.format("Du hast %s:%s:%s als convert Item Festgelegt!", convertRequest.getType().name().toLowerCase(),
                    LegacyComponentSerializer.legacyAmpersand().serialize(convertRequest.displayName()),
                    "AMOUNT:" + convertRequest.getAmount()), true);
            return Command.SINGLE_SUCCESS;
        }).build();
    }

}
