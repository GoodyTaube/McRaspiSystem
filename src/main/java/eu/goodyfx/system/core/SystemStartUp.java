package eu.goodyfx.system.core;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.*;
import eu.goodyfx.system.core.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SystemStartUp {

    public final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public SystemStartUp() {
        welcome();
        addCommands(plugin);
        addEvents(plugin);
    }

    private void welcome() {
        Bukkit.getConsoleSender().sendMessage("""
                
                
                ███╗   ███╗ ██████╗██████╗  █████╗ ███████╗██████╗ ██╗      ███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗
                ████╗ ████║██╔════╝██╔══██╗██╔══██╗██╔════╝██╔══██╗██║      ██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║
                ██╔████╔██║██║     ██████╔╝███████║███████╗██████╔╝██║█████╗███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║
                ██║╚██╔╝██║██║     ██╔══██╗██╔══██║╚════██║██╔═══╝ ██║╚════╝╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║
                ██║ ╚═╝ ██║╚██████╗██║  ██║██║  ██║███████║██║     ██║      ███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║
                ╚═╝     ╚═╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝      ╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝
                                                                                                                                \s
                """);
    }

    private void addCommands(McRaspiSystem plugin) {
        //Main System
        new AdminCommand();
        new AFKCommand(plugin);
        new ChatColorCommand(plugin);
        new InfoCommand(plugin);
        new MessageCommand(plugin);
        new PrefixCommand();
        //new RequestCommand(plugin);
        new SettingsCommand(plugin);
        //new SitCommand(plugin);
        new TempBanCommand(plugin);
        new UnBanCommand(plugin);
        new WarteschlangeCommand(plugin);
        new RandomTeleportCommand(plugin);
        new BackCommand(plugin);
        new MuteCommand();
        new InHeadCommand();
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
    }



}
