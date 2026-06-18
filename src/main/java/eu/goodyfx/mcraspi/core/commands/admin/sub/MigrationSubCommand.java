package eu.goodyfx.mcraspi.core.commands.admin.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.PlayerLifeCycleService;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MigrationSubCommand implements RaspiSubCommand {


    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("migration").executes(this::migrationCheckSubCommand)
                .then(Commands.argument("spieler", StringArgumentType.string()).suggests(suggestOffline()).executes(this::migrationCheckTargetSubCommandArg))
                .then(Commands.literal("reset").then(Commands.argument("spieler", StringArgumentType.string()).suggests(suggestOffline()).executes(this::migrationResetTarget)));
    }


    /**
     * Check if Command Sender has HoursMigration Completed
     *
     * @param context The Command Context
     * @return True OR False Message if CommandSender has Hours Migration Completed
     */
    private Integer migrationCheckSubCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("ONLY PLAYER");
            return 1;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        PersistentDataContainer container = player.getPersistentDataContainer();
        NamespacedKey key = PlayerLifeCycleService.getHOURS_MIGRATION_KEY();
        if (key == null) {
            return 1;
        }
        if (!container.has(key, PersistentDataType.BYTE)) {
            raspiPlayer.sendMessage("<red>Du wurdest noch nicht migriert.", true);
            return 1;
        }
        raspiPlayer.sendMessage("<green>Du wurdest bereits migriert.", true);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Check if Target has HoursMigration Completed
     *
     * @param context The Command Context
     * @return True OR False Message if Target has Hours Migration Completed
     */
    private Integer migrationCheckTargetSubCommandArg(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("ONLY PLAYER");
            return 1;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(context.getArgument("player", String.class));
        if (!target.hasPlayedBefore() || !target.isOnline()) {
            return 1;
        }
        Player onlineTarget = target.getPlayer();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        assert onlineTarget != null;
        PersistentDataContainer container = onlineTarget.getPersistentDataContainer();
        NamespacedKey key = PlayerLifeCycleService.getHOURS_MIGRATION_KEY();
        if (key == null) {
            return 1;
        }
        if (!container.has(key, PersistentDataType.BYTE)) {
            raspiPlayer.sendMessage(String.format("<red>%s wurde noch nicht migriert.", onlineTarget.getName()), true);
            return 1;
        }
        raspiPlayer.sendMessage(String.format("<green>%s wurde bereits migriert.", onlineTarget.getName()), true);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * To reset the PDC value
     *
     * @param context The Command Context
     * @return The Command Return
     */
    private Integer migrationResetTarget(CommandContext<CommandSourceStack> context) {

        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("ONLY PLAYER");
            return 1;
        }

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String target = context.getArgument("player", String.class);
        Player targetPlayer = Bukkit.getPlayer(target);

        if (targetPlayer == null) {
            raspiPlayer.sendMessage("<red>Der Spieler muss online sein!", true);
            raspiPlayer.playSound(RaspiSounds.ERROR);
            return 1;
        }

        PersistentDataContainer container = targetPlayer.getPersistentDataContainer();

        if (!container.has(PlayerLifeCycleService.getHOURS_MIGRATION_KEY(), PersistentDataType.BYTE)) {
            raspiPlayer.sendMessage("<red>Der Spieler ist noch nicht migriert.", true);
            raspiPlayer.playSound(RaspiSounds.ERROR);
            return 1;
        }

        container.remove(PlayerLifeCycleService.getHOURS_MIGRATION_KEY());
        raspiPlayer.sendMessage("<green>Die Migration wurde zurückgesetzt!", true);
        raspiPlayer.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }


}
