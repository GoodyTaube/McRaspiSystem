package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemConverterCommandContainer extends RaspiCommand {

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }


    public ItemConverterCommandContainer(McRaspiSystem plugin) {
        super(plugin);
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal(getName()).executes(context -> {
            Entity entity = context.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }
            McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

            RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

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
