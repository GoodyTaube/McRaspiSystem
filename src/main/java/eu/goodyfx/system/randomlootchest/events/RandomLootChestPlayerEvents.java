package eu.goodyfx.system.randomlootchest.events;

import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.randomlootchest.RandomLootChest;
import eu.goodyfx.system.randomlootchest.managers.DatabaseManager;
import eu.goodyfx.system.randomlootchest.utils.RLCChest;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RandomLootChestPlayerEvents implements Listener {

    private final RandomLootChest system;
    private final DatabaseManager database;

    public RandomLootChestPlayerEvents(RandomLootChest subSystem) {
        subSystem.setEvents(this);
        this.system = subSystem;
        this.database = subSystem.getDatabaseManager();
    }


    public boolean isChest(Location loc) {
        return system.getChestCache().containsKey(loc);
    }

    public void deleteChest(Location loc) {
        RLCChest chest = system.getChestCache().get(loc);
        chest.kill(false);
    }

    public void killAllChests() {
        for (RLCChest chest : system.getChestCache().values()) {
            chest.kill(false);
        }
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null && block.getType().equals(Material.CHEST) && this.isChest(block.getLocation())) {
            e.setCancelled(true);
            this.deleteChest(block.getLocation());
            system.getOpenLootInventory().openInvenory(player);
            if (system.getConfigManager().getConfig().getBoolean("BroadcastMessageOnLoot")) {
                RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
                Location loc = block.getLocation();
                formatMessage(loc, raspiPlayer);
            }

        }

    }

    private void formatMessage(Location location, RaspiPlayer raspiPlayer) {
        String string = RaspiFormatting.formattingChatMessage(system.getConfigManager().getConfig().getString("MessageOnLoot"));
        string = string.replace("{W}", location.getWorld().getName());
        string = string.replace("{X}", String.valueOf(location.getBlockX()));
        string = string.replace("{Y}", String.valueOf(location.getBlockY()));
        string = string.replace("{Z}", String.valueOf(location.getBlockZ()));
        string = string.replace("{Player}", raspiPlayer.getColorName());
        system.getPlugin().getServer().broadcast(MiniMessage.miniMessage().deserialize(string));
    }


}
