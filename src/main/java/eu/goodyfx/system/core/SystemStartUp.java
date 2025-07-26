package eu.goodyfx.system.core;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.*;
import eu.goodyfx.system.core.events.*;
import eu.goodyfx.system.lootchest.commands.RaspiItemsCommand;
import eu.goodyfx.system.lootchest.events.LootChestListeners;
import eu.goodyfx.system.lootchest.events.LootConsumeEvents;
import eu.goodyfx.system.lootchest.events.LootSpongeEvents;
import eu.goodyfx.system.raspievents.events.CraftingEventListeners;
import eu.goodyfx.system.reise.commands.ReiseCommand;
import eu.goodyfx.system.reise.commands.ReisePortCommand;
import eu.goodyfx.system.reise.commands.ReiseSucheCommand;
import eu.goodyfx.system.trader.commands.TraderCommand;
import eu.goodyfx.system.trader.events.TraderListeners;
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
        Bukkit.getConsoleSender().sendRichMessage("<br><br><rainbow>" +
                "███╗   ███╗ ██████╗██████╗  █████╗ ███████╗██████╗ ██╗      ███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗<br>" +
                "████╗ ████║██╔════╝██╔══██╗██╔══██╗██╔════╝██╔══██╗██║      ██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║<br>" +
                "██╔████╔██║██║     ██████╔╝███████║███████╗██████╔╝██║█████╗███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║<br>" +
                "██║╚██╔╝██║██║     ██╔══██╗██╔══██║╚════██║██╔═══╝ ██║╚════╝╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║<br>" +
                "██║ ╚═╝ ██║╚██████╗██║  ██║██║  ██║███████║██║     ██║      ███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║<br>" +
                "╚═╝     ╚═╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝      ╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝<br>" +
                "                                                                                                                 <br>");
    }


    private void addCommands(McRaspiSystem plugin) {
        //Main System
        new AdminCommand();
        new AFKCommand(plugin);
        new ChatColorCommand(plugin);
        new InfoCommand(plugin);
        new MessageCommand(plugin);
        new PrefixCommand();
        new RequestCommand(plugin);
        new SettingsCommand(plugin);
        new SitCommand(plugin);
        new TempBanCommand(plugin);
        new UnBanCommand(plugin);
        new WarteschlangeCommand(plugin);
        new RaspiItemsCommand(plugin);
        new ReiseCommand(plugin);
        new ReiseSucheCommand(plugin);
        new ReisePortCommand(plugin);
        new RandomTeleportCommand(plugin);
        new BackCommand(plugin);
        new TraderCommand();
        new MuteCommand();
        new InHeadCommand();
    }


    private void addEvents(McRaspiSystem plugin) {
        new CommandListeners();
        new PlayerChatListeners(plugin);
        new PlayerListeners(plugin);
        new ServerListeners(plugin);
        new TeleportListeners(plugin);
        //new WarteschlangeListeners(plugin);
        new LootConsumeEvents(plugin);
        new LootSpongeEvents(plugin);
        new PlayerMoveListener(plugin);
        new PlayerInteractAtEntitiesListeners(plugin);
        new PlayerInteractListeners(plugin);
        new RaspiPlayerConnectionEvents();
        new LootChestListeners(plugin);
        new InHeadListeners();
        //new WarnListeners(plugin);
        new TraderListeners();
        new CompassEvents();
        new CraftingEventListeners();
        new InventoryListeners();

    }
}
