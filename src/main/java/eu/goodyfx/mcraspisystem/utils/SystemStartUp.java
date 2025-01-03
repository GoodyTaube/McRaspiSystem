package eu.goodyfx.mcraspisystem.utils;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.*;
import eu.goodyfx.mcraspisystem.events.*;
import eu.goodyfx.mcraspisystem.systems.LootChest;
import eu.goodyfx.mcraspisystem.systems.RaspiItems;

import java.util.ArrayList;
import java.util.List;

public class SystemStartUp {

    public final McRaspiSystem plugin;

    public SystemStartUp(McRaspiSystem plugin) {
        this.plugin = plugin;
        systeme();
        addCommands(plugin);
        addEvents(plugin);
    }

    private void systeme() {
        new RaspiItems(this);
        new LootChest(this);
    }

    private List<SystemTemplate> systemTemplates = new ArrayList<>();


    private void addCommands(McRaspiSystem plugin) {
        systemTemplates.forEach(SystemTemplate::commands);
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
        new RandomTeleportCommand(plugin);
        new BackCommand(plugin);
        new VoteCommand(plugin);
    }


    private void addEvents(McRaspiSystem plugin) {
        systemTemplates.forEach(SystemTemplate::events);
        new CommandListeners(plugin);
        new PlayerChatListeners(plugin);
        new PlayerListeners(plugin);
        new ServerListeners(plugin);
        new TeleportListeners(plugin);
        new WarteschlangeListeners(plugin);
        new LootConsumeEvents(plugin);
        new LootSpongeEvents(plugin);
        new PlayerMoveListener(plugin);
        new PlayerInteractAtEntitiesListeners(plugin);
        new PlayerInteractListeners(plugin);
        new LootChestListeners(plugin);
        //new WarnListeners(plugin);
    }

    public void addToLists(SystemTemplate system) {
        this.systemTemplates.add(system);
    }

    public boolean isEnabled(String path) {
        return plugin.getConfig().contains(path) && plugin.getConfig().getBoolean(path);
    }
}
