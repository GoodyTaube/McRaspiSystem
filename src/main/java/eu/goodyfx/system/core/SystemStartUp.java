package eu.goodyfx.system.core;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.AdminCommand;
import eu.goodyfx.system.core.commandsOLD.WarteschlangeCommand;
import eu.goodyfx.system.core.events.*;
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
        new WarteschlangeCommand(plugin);
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
