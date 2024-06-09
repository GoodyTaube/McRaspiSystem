package eu.goodyfx.mcraspisystem.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.AFKCommand;
import eu.goodyfx.mcraspisystem.commands.SitCommand;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.managers.WarteschlangenManager;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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


    @EventHandler(priority = EventPriority.MONITOR)
    public void onSneak(PlayerToggleSneakEvent sneakEvent) {
        if (!sneakEvent.isSneaking() && (warteschlangenManager.getQueuedPlayers().contains(sneakEvent.getPlayer().getUniqueId()) && (userManager.hasPersistantValue(sneakEvent.getPlayer(), PlayerValues.AFK)))) {
            Bukkit.dispatchCommand(sneakEvent.getPlayer(), "afk");
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent jumpEvent) {
        if (warteschlangenManager.getQueuedPlayers().contains(jumpEvent.getPlayer().getUniqueId()) && (userManager.hasPersistantValue(jumpEvent.getPlayer(), PlayerValues.AFK))) {
            Bukkit.dispatchCommand(jumpEvent.getPlayer(), "afk");
        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND && event.getPlayer().getInventory().getItem(EquipmentSlot.HAND).getType().equals(Material.AIR) && !SitCommand.getSits().containsKey(player.getUniqueId())) {
            if (event.getClickedBlock().getLocation().getY() > player.getLocation().getY()) {
                return;
            }
            Block block = event.getClickedBlock();
            if (block != null && (block.getBlockData() instanceof Stairs || block.getBlockData() instanceof Slab)) {
                Location location = block.getLocation();
                SitCommand.add(player, location, -0.53);
            }
            if (block != null && Tag.WOOL_CARPETS.isTagged(block.getType())) {
                Location location = block.getLocation();
                SitCommand.add(player, location, 0.046);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer(); //The player who click Animal
        sit(event); //Sit Implementation
        if (player.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            ItemStack debug = player.getInventory().getItemInMainHand();
            if (debug.hasItemMeta() && debug.getItemMeta().hasCustomModelData() && debug.getItemMeta().getCustomModelData() == 777) {
                Player target = null;
                if (entity instanceof Player pl) {
                    target = pl;
                }
                if (target == null) {
                    return;
                }
                if (debugActivation.contains(entity.getUniqueId()) && !player.isSneaking()) {
                    sendDemoGUIPacket(target);
                } else if (debugActivation.contains(entity.getUniqueId()) && player.isSneaking()) {
                    debugActivation.remove(entity.getUniqueId());
                } else {
                    debugActivation.add(entity.getUniqueId());
                    fakeVirusInjection(target);
                }
            }
        }

    }

    /**
     * Create and Send Demo Packet to Player
     *
     * @param target The Packet receiver
     */
    private void sendDemoGUIPacket(Player target) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        container.getGameStateIDs().write(0, 5);
        plugin.getHookManager().getProtocolManager().sendServerPacket(target, container);
    }

    /**
     * Send Fake virus Injection to Target Player
     *
     * @param target The Injection receiver
     */
    private void fakeVirusInjection(Player target) {
        new BukkitRunnable() {
            int per = 0;

            @Override
            public void run() {

                int rando = random.nextInt(4);
                if (per + rando > 100) {
                    per++;
                } else {
                    per = per + rando;
                }
                target.sendActionBar(MiniMessage.miniMessage().deserialize("<green>Injection: <red>p_22cX3_Exploit <reset>|| <gray>" + per + "%"));
                if (per == 100) {
                    cancel();
                    target.sendActionBar(MiniMessage.miniMessage().deserialize("<red><b>Injection Success!"));
                    sendDemoGUIPacket(target);
                }
            }
        }.runTaskTimerAsynchronously(plugin, random.nextInt(2), random.nextInt(3));
    }

    /**
     * Check if Player can Sit on Animal
     *
     * @param event The Event
     */
    private void sit(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if ((entity instanceof Animals || entity instanceof WaterMob) && (entity.getPassengers().isEmpty() && player.getInventory().getItem(EquipmentSlot.HAND).getType().equals(Material.AIR))) {
            if (entity instanceof Cat || entity instanceof Wolf) {
                return;
            }
            entity.addPassenger(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();


        AFKCommand.getPlayerIDLE().remove(player.getUniqueId());

        if (userManager.hasPersistantValue(player, PlayerValues.AFK)) {
            if (!playerChangedWorld.contains(player.getUniqueId())) {
                if (userManager.getAfkContainer().containsKey(player.getUniqueId()) && userManager.getAfkContainer().get(player.getUniqueId()).distance(event.getTo()) > 2 && !warteschlangenManager.playersQueue.contains(player.getUniqueId())) {
                    Bukkit.dispatchCommand(event.getPlayer(), "afk");
                }
            } else {
                userManager.getAfkContainer().put(player.getUniqueId(), player.getLocation());
                playerChangedWorld.remove(event.getPlayer().getUniqueId());
            }
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
