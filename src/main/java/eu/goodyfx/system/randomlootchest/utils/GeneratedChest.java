package eu.goodyfx.system.randomlootchest.utils;

import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.randomlootchest.RandomLootChest;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.atomic.AtomicBoolean;

public class GeneratedChest {


    private final FileConfiguration config;
    private FindAvLocations findAvLocations;
    private RandomLootChest system;
    @Getter
    private Chest chest = null;
    @Getter
    private Location location;
    @Getter
    private long killTime = 30 * 1000;

    /**
     * Spawn a new RandomLootChest in the given world
     *
     * @param subSystem the Main System
     */
    public GeneratedChest(RandomLootChest subSystem) {
        this.config = subSystem.getConfigManager().getConfig();
        spawnChest();
    }

    private void spawnChest() {
        AtomicBoolean found = new AtomicBoolean(false);

        for (int i = 0; i <= 100; ++i) {
            if (found.get()) {
                found.set(true);
                break;
            }

            Location loc = findAvLocations.findLocation();
            loc.subtract(0, 1, 0);
            if (!loc.getBlock().getType().equals(Material.WATER) || !(loc.getBlock() instanceof Waterlogged)) {
                found.set(true);
                loc.setY(loc.getY() + (double) 1.0F);
                loc.getBlock().setType(Material.CHEST);
                this.chest = (Chest) loc.getBlock();
                this.location = chest.getLocation();
                chest.getPersistentDataContainer().set(system.getChestKey(), PersistentDataType.BYTE, (byte) 1);
                String x = String.valueOf(loc.getBlockX());
                String y = String.valueOf(loc.getBlockY());
                String z = String.valueOf(loc.getBlockZ());
                String string = RaspiFormatting.formattingChatMessage(config.getString("SpawnBroadcastMessage"));
                String string2 = string.replace("{X}", x);
                String string3 = string2.replace("{Y}", y);
                String string4 = string3.replace("{Z}", z);
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(string4));
                //this.saveChest(loc);
                setKillTime();
                break;
            }
        }

    }

    private void setKillTime() {
        int timeAfter = config.getInt("KillChestAfterTime") * 1000;
        long now = System.currentTimeMillis();
        this.killTime = (now + timeAfter);
    }

}
