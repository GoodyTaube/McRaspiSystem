package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.managers.WarteschlangenManager;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public class PlayerListeners implements Listener {

    private final McRaspiSystem plugin;
    private final WarteschlangenManager warteschlangenManager;
    private final UserManager userManager;
    private final List<UUID> playerChangedWorld = new ArrayList<>();

    private final Random random = new Random();

    private final List<UUID> debugActivation = new ArrayList<>();


    public PlayerListeners(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getModule().getUserManager();
        this.warteschlangenManager = plugin.getModule().getWarteschlangenManager();
        plugin.setListeners(this);
    }


    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && plugin.getRaspiPlayer(player).isDefault()) {
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        playerChangedWorld.add(event.getPlayer().getUniqueId());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent teleportEvent) {
        Player player = teleportEvent.getPlayer();

        Location dest = teleportEvent.getTo();
        if (warteschlangenManager.isQueue(player) && plugin.getModule().getRaspiMessages().blockTeleport() && plugin.getModule().getLocationManager().get("waiting").getWorld() != Objects.requireNonNull(teleportEvent.getTo()).getWorld()) {
            teleportEvent.setCancelled(true);
            player.sendRichMessage(plugin.getModule().getRaspiMessages().blocking());

        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent deathEvent) {
        Player player = deathEvent.getPlayer();
        List<ItemStack> drops = deathEvent.getDrops();
        int exp = deathEvent.getDroppedExp();

        userManager.setPersistantValue(player, PlayerValues.DEATH, System.currentTimeMillis());
        if (plugin.getConfig().getBoolean("Utilities.dead-signs")) {
            Location location = deathEvent.getEntity().getLocation();
            Block highest = location.getBlock();
            Location blockLoc = highest.getLocation();


            for (int i = 0; i < 10; i++) {
                if (highest.getType().equals(Material.AIR)) {
                    break;
                }
                for (BlockFace face : BlockFace.values()) {

                    if (highest.getRelative(face).getType().equals(Material.AIR)) {
                        if (!face.equals(BlockFace.UP) && !face.equals(BlockFace.DOWN)) {
                            blockLoc = highest.getRelative(face).getLocation();
                            break;
                        }
                    } else {
                        blockLoc = highest.getRelative(face, 1).getLocation();
                    }
                }
            }

            if (!blockLoc.getBlock().getType().equals(Material.AIR)) {
                plugin.getLogger().severe("<red>Sign Location not Found! (Try count 7 Times!)");
                return;
            }

            if (blockLoc.clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                return;
            }

            summonSign(deathEvent.getEntity(), blockLoc);
        }


    }


    private void summonSign(Entity entity, Location location) {
        location.getBlock().setType(Material.OAK_SIGN);
        Sign sign = (Sign) location.getBlock().getState();
        sign.getSide(Side.FRONT).line(0, MiniMessage.miniMessage().deserialize(" || R.I.P || "));
        sign.getSide(Side.FRONT).line(1, MiniMessage.miniMessage().deserialize(entity.getName()));
        sign.getSide(Side.FRONT).line(2, Component.text(new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()))));
        sign.getSide(Side.FRONT).line(3, Component.text(Objects.requireNonNull(entity.getLastDamageCause()).getCause().name()));
        sign.update();
    }
}
