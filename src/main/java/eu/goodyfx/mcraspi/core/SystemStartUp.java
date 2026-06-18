package eu.goodyfx.mcraspi.core;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.commandsOLD.AdminCommand;
import eu.goodyfx.mcraspi.core.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SystemStartUp {

    public final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public SystemStartUp() {
        welcomeScreen();
        addOldCommands(plugin);
        addEvents(plugin);
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


}
