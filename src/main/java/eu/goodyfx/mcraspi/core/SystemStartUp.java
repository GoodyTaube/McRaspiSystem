package eu.goodyfx.mcraspi.core;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.commands.*;
import eu.goodyfx.mcraspi.core.commands.admin.AdminCommandContainer;
import eu.goodyfx.mcraspi.core.commands.coins.CoinCommandContainer;
import eu.goodyfx.mcraspi.core.commands.request.RequestCommandContainer;
import eu.goodyfx.mcraspi.core.commands.tempban.TempBanCommandContainer;
import eu.goodyfx.mcraspi.core.commandsold.AdminCommand;
import eu.goodyfx.mcraspi.core.events.*;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SystemStartUp {

    public final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);


    public SystemStartUp() {
        welcomeScreen();
        addOldCommands(plugin);
        commands();
        setUpCommands();
        addEvents(plugin);
    }

    private void commands() {
        new AdminCommandContainer(plugin);
        new CoinCommandContainer(plugin);
        new RequestCommandContainer(plugin);
        new TempBanCommandContainer(plugin);
        new AFKCommandContainer(plugin);
        new BackCommandContainer(plugin);
        new ChatColorCommandContainer(plugin);
        new InHeadCommandContainer(plugin);
        new ItemConverterCommandContainer(plugin);
        new MessageCommandContainer(plugin);
        new MuteCommandContainer(plugin);
        new PlayerInfoCommandContainer(plugin);
        new PrefixCommandContainer(plugin);
        new RandomTeleportCommandContainer(plugin);
        new RaspiGiveCommandContainer(plugin);
        new SettingsCommandContainer(plugin);
        new SitCommandContainer(plugin);
        new UnBanCommandContainer(plugin);
        new UnMuteCommandContainer(plugin);
        new VoteCommandContainer(plugin);
    }

    private void welcomeScreen() {
        Bukkit.getConsoleSender().sendMessage("""
                
                ███╗   ███╗ ██████╗██████╗  █████╗ ███████╗██████╗ ██╗      ███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗
                ████╗ ████║██╔════╝██╔══██╗██╔══██╗██╔════╝██╔══██╗██║      ██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║
                ██╔████╔██║██║     ██████╔╝███████║███████╗██████╔╝██║█████╗███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║
                ██║╚██╔╝██║██║     ██╔══██╗██╔══██║╚════██║██╔═══╝ ██║╚════╝╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║
                ██║ ╚═╝ ██║╚██████╗██║  ██║██║  ██║███████║██║     ██║      ███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║
                ╚═╝     ╚═╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝      ╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝                                                                               \s
                """);
    }

    @Deprecated()
    private void addOldCommands(McRaspiSystem plugin) {
        new AdminCommand();
    }


    private void addEvents(McRaspiSystem plugin) {
        new CommandListeners();
        new PlayerChatListeners();
        new PlayerListeners(plugin);
        new ServerListeners();
        new TeleportListeners(plugin);
        //new WarteschlangeListeners(plugin);
        new PlayerInteractAtEntitiesListeners(plugin);
        new PlayerInteractListeners(plugin);
        new RaspiPlayerConnectionEvents();
        new InHeadListeners();
        //new WarnListeners(plugin);
        new CompassEvents();
        new InventoryListeners();
        new PlayerAFKHandler();
        new RaspiCoinsEvents();
        new RaspiWorldEvents(plugin);
    }

    public void setUpCommands() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            final Commands register = commands.registrar();
            for (RaspiCommand command : plugin.getCommandCache()) {
                register.register(command.getCommand(), command.getDescription());
                if (command.getAliases() != null) {
                    for (String alias : command.getAliases()) {
                        register.register(Commands.literal(alias).redirect(command.getCommand()).build(), "Optionaler Command für / " + command.getName());
                    }
                }
            }
        });
    }

}
