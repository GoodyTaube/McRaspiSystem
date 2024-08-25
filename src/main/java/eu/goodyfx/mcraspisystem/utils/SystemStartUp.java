package eu.goodyfx.mcraspisystem.utils;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.*;
import eu.goodyfx.mcraspisystem.events.*;

public class SystemStartUp {

    public SystemStartUp(McRaspiSystem plugin) {
        addCommands(plugin);
        addEvents(plugin);
    }

    private void addCommands(McRaspiSystem plugin) {
        //Main System
        new AdminCommand(plugin);
        new AFKCommand(plugin);
        new ChatColorCommand(plugin);
        new InfoCommand(plugin);
        new MessageCommand(plugin);
        new PrefixCommand(plugin);
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
    }


    private void addEvents(McRaspiSystem plugin) {
        new CommandListeners(plugin);
        new PlayerChatListeners(plugin);
        new PlayerListeners(plugin);
        new ServerListeners(plugin);
        new TeleportListeners(plugin);
        new WarteschlangeListeners(plugin);
        new LootConsumeEvents(plugin);
        new LootEvents(plugin);
        new LootSpongeEvents(plugin);
        new PlayerMoveListener(plugin);
        new PlayerInteractAtEntitiesListeners(plugin);
        new PlayerInteractListeners(plugin);
    }


}
