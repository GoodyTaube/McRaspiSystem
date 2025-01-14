package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootSpongeEvents implements Listener {


    private final McRaspiSystem plugin;

    public LootSpongeEvents(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setListeners(this);
    }

    private final Map<UUID, Location> locationMap = new HashMap<>();

    private void stone(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ItemStack stack = event.getItemInHand();
        if (plugin.getMapItem() != null && block.getType().equals(plugin.getMapItem().getMaterial())) {
            event.setCancelled(true);

            PluginCommand command = null;

            if (plugin.getServer().getPluginCommand("dynmap") != null) {
                command = plugin.getServer().getPluginCommand("dynmap");
            }

            if (command == null) {
                player.sendRichMessage(plugin.getModule().getRaspiMessages().getPrefix() + "<red>Es scheint ein Problem zu geben. (<yellow>R#2243<red>)");
                return;
            }

            String text = PlainTextComponentSerializer.plainText().serialize(stack.getItemMeta().displayName());

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dmarker add \"" + text + "\" icon:redflag x:" + block.getLocation().getX() + " y:" + block.getLocation().getY() + " z:" + block.getLocation().getZ() + " world:" + block.getLocation().getWorld().getName());

            ItemStack copyStack = stack.clone();
            copyStack.setAmount(copyStack.getAmount() - 1);
            player.getInventory().setItem(event.getHand(), copyStack);

        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack stack = event.getItemInHand();
        Location location = event.getBlockPlaced().getLocation();
        if (block.getType().equals(Material.POLISHED_BLACKSTONE_BUTTON) && stack.getItemMeta() != null && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1) {

            if (locationMap.containsKey(player.getUniqueId())) {
                if (!plugin.getModule().getLootManager().existAndActive(locationMap.get(player.getUniqueId()), location)) {
                    plugin.getModule().getLootManager().setWarp(player, locationMap.get(player.getUniqueId()), location);
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getPrefix() + "Position 2 für Teleport gesetzt!"));
                    player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getPrefix() + "Der Teleport Nr." + plugin.getModule().getLootManager().size() + " wurde erstellt!"));
                    player.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
                    locationMap.remove(player.getUniqueId());
                    return;
                }
            } else {
                locationMap.put(player.getUniqueId(), location);
                player.sendActionBar(MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getPrefix() + "Position 1 für Teleport gesetzt!"));
            }
            return;
        }


        if (stack.getItemMeta() != null && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1) {
            if (block.getType().equals(Material.SPONGE)) {
                Set<Block> sphere = sphereAround(event.getBlock().getLocation(), plugin.getConfig().getInt("items.sponge.radius")); //List of all Blocks rad = 30
                int amount = sphere.size();
                for (Block block1 : sphere) {
                    if (block1.getType().equals(Material.SEAGRASS) || block1.getType().equals(Material.KELP) || block1.getType().equals(Material.TALL_SEAGRASS) || block1.getType().equals(Material.KELP_PLANT)) {
                        block1.breakNaturally();
                        amount--;
                    }
                    if (block1.getBlockData() instanceof Waterlogged waterlogged) {
                        waterlogged.setWaterlogged(false);
                        block1.setBlockData(waterlogged);
                        amount--;
                    }
                    if (block1.getType().equals(Material.WATER)) {
                        block1.setType(Material.AIR);
                        amount--;
                    }
                }
            }

        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent breakEvent) {
        Location location = breakEvent.getBlock().getLocation();
        Block block = breakEvent.getBlock();
        Material material = block.getType();
        Player player = breakEvent.getPlayer();
        if (material == Material.POLISHED_BLACKSTONE_BUTTON) {
            if (plugin.getModule().getLootManager().warpExist(location)) {
                plugin.getModule().getLootManager().disable(location);
                player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getPrefix() + "<red>Teleport zerstört!"));
                player.playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1f, 1f);
            }
        }
    }

    public Set<Block> sphereAround(Location location, int radius) {
        Set<Block> sphere = new HashSet<>();
        Block center = location.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = center.getRelative(x, y, z);
                    if (b.getType() != Material.AIR && center.getLocation().distance(b.getLocation()) <= radius) {
                        sphere.add(b);
                    }
                }

            }
        }
        return sphere;
    }
}
