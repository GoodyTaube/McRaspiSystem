package eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils;

import eu.goodyfx.mcraspi.core.utils.RaspiFormatting;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.RandomLootChest;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

@Getter
public class    RLCChest {

    public Location location;
    @Setter
    public int killTime;
    private final RandomLootChest system;


    public RLCChest(RandomLootChest system, Location location, int killTime) {
        this.location = location;
        this.killTime = killTime;
        this.system = system;
    }

    public void generate() {
        location.getBlock().setType(Material.CHEST);
        system.getChestCache().put(location, this);
        broadCast(RLCConfigContents.MESSAGE_SPAWN.getPath());
    }

    public void kill() {
        remove(true);
    }

    public void kill(boolean message) {
        remove(message);
    }

    private void remove(boolean message) {
        location.getBlock().setType(Material.AIR);
        system.getChestCache().remove(location);
        if (message) {
            broadCast(RLCConfigContents.MESSAGE_KILL.getPath());
        }
    }

    private void broadCast(String configPath) {
        String configMessage = system.getConfigManager().getConfig().getString(configPath);
        if (configMessage == null) {
            return;
        }
        String message = RaspiFormatting.formattingChatMessage(configMessage);
        String worldName = WorldNameCompiler.compile(location.getWorld());
        if (worldName == null) worldName = "<red>w:404";
        message = message.replace("{WORLD}", worldName);
        message = message.replace("{X}", String.valueOf(location.getBlockX()));
        message = message.replace("{Y}", String.valueOf(location.getBlockY()));
        message = message.replace("{Z}", String.valueOf(location.getBlockZ()));
        Bukkit.getServer().broadcast(MiniMessage.miniMessage().deserialize(message));
    }

}
