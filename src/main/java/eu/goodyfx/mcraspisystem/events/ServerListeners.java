package eu.goodyfx.mcraspisystem.events;

import com.destroystokyo.paper.profile.PlayerProfile;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.PlayerBanManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.utils.OldColors;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class ServerListeners implements Listener {

    private final McRaspiSystem plugin;
    private final UserManager userManager;
    private final PlayerBanManager playerBanManager;
    private static final Map<UUID, Material> lastBreak = new HashMap<>();

    public ServerListeners(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.playerBanManager = plugin.getModule().getPlayerBanManager();
        this.userManager = plugin.getModule().getUserManager();

        plugin.setListeners(this);
    }

    public static Map<UUID, Material> getLastBreak() {
        return lastBreak;
    }


    @EventHandler
    public void onPrimeTNT(TNTPrimeEvent tntPrimeEvent) {
        Entity entity = tntPrimeEvent.getPrimingEntity();
        if (entity instanceof Player player) {
            int hours = 100;
            if (!player.getWorld().equals(Bukkit.getWorld("world"))) {
                return;
            }
            if (plugin.getConfig().contains("Utilities.tnt_hours")) {
                hours = plugin.getConfig().getInt("Utilities.tnt_hours");
            }
            if (!userManager.hasTimePlayed(player, hours)) {
                tntPrimeEvent.setCancelled(true);
                player.sendActionBar(MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getPrefix() + "<red>TNT gibt es erst ab: <gray>" + hours + " <red>Spielstunden."));
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent breakEvent) {
        lastBreak.put(breakEvent.getPlayer().getUniqueId(), breakEvent.getBlock().getType());
        Player player = breakEvent.getPlayer();
        RaspiPlayer raspiPlayer = plugin.getRaspiPlayer(player);
        Block block = breakEvent.getBlock();

        if (raspiPlayer.isDefault()) {
            breakEvent.setCancelled(true);
            player.sendRichMessage("Du bist nicht registriert.");
            return;
        }


        if (block.getType().equals(Material.PLAYER_HEAD) || block.getType().equals(Material.PLAYER_WALL_HEAD)) {
            Skull skull = (Skull) block.getState();

            PersistentDataContainer skulldata = skull.getPersistentDataContainer();
            if (Objects.requireNonNull(skull.getPlayerProfile()).getId() != null) {

                PlayerProfile pP = skull.getPlayerProfile();
                PlayerProfile playerProfile = Bukkit.createProfile("Voting");
                PlayerTextures textures = pP.getTextures();
                playerProfile.setTextures(textures);
                skull.setPlayerProfile(playerProfile);
                skull.update();
                return;
            }


            if (skulldata.has(new NamespacedKey(plugin, "name"), PersistentDataType.STRING)) {
                String data = skulldata.get(new NamespacedKey(plugin, "name"), PersistentDataType.STRING);
                assert data != null;
                Collection<ItemStack> stacks = breakEvent.getBlock().getDrops();
                for (ItemStack stack1 : stacks) {
                    ItemMeta meta = stack1.getItemMeta();
                    meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(data));
                    stack1.setItemMeta(meta);
                    block.getWorld().dropItem(block.getLocation(), stack1);
                }
                breakEvent.setDropItems(false);
            }

        }


        Collection<Entity> entities = player.getLocation().getNearbyEntities(0, 2, 0);
        entities.forEach(entity -> {
            if (entity.getType().equals(EntityType.INTERACTION)) {
                breakEvent.setCancelled(true);
            }
        });

    }


    @EventHandler
    public void onBlockPlaced(@NotNull BlockPlaceEvent placeEvent) {

        Player player = placeEvent.getPlayer();
        Block block = placeEvent.getBlock();
        Location location = block.getLocation();

        if (plugin.getRaspiPlayer(player).isDefault()) {
            placeEvent.setCancelled(true);
            player.sendRichMessage("Du bist noch nicht registriert.");
            return;
        }

        if ((block.getType().equals(Material.PLAYER_HEAD) || block.getType().equals(Material.PLAYER_WALL_HEAD)) && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().displayName() != null) {
            Skull skull = (Skull) block.getState();
            PersistentDataContainer container = skull.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "name"), PersistentDataType.STRING, LegacyComponentSerializer.legacyAmpersand().serialize(player.getInventory().getItemInMainHand().getItemMeta().displayName()));
            skull.update();
        }

        if (!location.getWorld().getName().equalsIgnoreCase("world")) {
            return;
        }
        if (block.getType().equals(Material.TNT)) {

            int hours = 100;

            if (plugin.getConfig().contains("Utilities.tnt_hours")) {
                hours = plugin.getConfig().getInt("Utilities.tnt_hours");
            }

            if (!userManager.hasTimePlayed(player, hours)) {
                placeEvent.setCancelled(true);

                if (!location.getNearbyEntities(0.1, 0.1, 0.1).isEmpty()) {

                    return;
                }
                BlockDisplay blockDisplay = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
                blockDisplay.setBlock(placeEvent.getBlock().getBlockData());
                blockDisplay.setGlowing(true);
                TextDisplay display = (TextDisplay) location.getWorld().spawnEntity(location.add(0.5, 1.4, 0.5), EntityType.TEXT_DISPLAY);
                display.text(MiniMessage.miniMessage().deserialize("<red>TNT = NONO"));
                display.setAlignment(TextDisplay.TextAlignment.CENTER);
                display.setBillboard(Display.Billboard.CENTER);
                display.setRotation(-player.getEyeLocation().getYaw(), 0);
                display.setDefaultBackground(false);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    display.remove();
                    blockDisplay.remove();
                }, 20L * 5);


            }

        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        userManager.update(player);
        if (!player.isPermissionSet("system.team") && plugin.getConfig().contains("Utilities.wartung") && plugin.getConfig().getBoolean("Utilities.wartung")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, MiniMessage.miniMessage().deserialize("<gray>Wir befinden uns in Wartung.<br><aqua>Bitte um Verständnis."));
        }
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());

        if (playerBanManager.contains(player)) {
            if (System.currentTimeMillis() < playerBanManager.expire(player)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, MiniMessage.miniMessage().deserialize(String.format("<gold>McRaspi.com <gray><b>-</b> <red>Disconnect<br><br><red><b>Du wurdest temporär gesperrt!</b><br>" +
                                "<gray>Du wurdest von: <aqua>%s <gray>für folgendes gesperrt:<br>" +
                                "<yellow>'%s'<br><br>" +
                                "<gray>Du wirst am <aqua>%s <gray>entsperrt.",
                        playerBanManager.performer(player), playerBanManager.reason(player), new SimpleDateFormat("dd-MM-yyyy HH:mm").format(playerBanManager.expire(player)))));
            } else {
                playerBanManager.removeBan(player);
                plugin.getLogger().info(plugin.getModule().getRaspiMessages().getPrefix() + " " + player.getName() + " wurde Entsperrt weil seine sperrzeit abgelaufen ist.");
                event.allow();
            }
        }

        if (player.hasPlayedBefore()) {
            return;
        }


        if (plugin.getConfig().getBoolean("Utilities.kickNewbies")) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MiniMessage.miniMessage().deserialize(String.format("McRaspi.com<br><br>%s<br><br><aqua>%s", "<gray>Du wurdest gekickt.", plugin.getModule().getRaspiMessages().getDisallowNewbie()[0])));
        }
    }

    @EventHandler
    public void onAnvilPre(PrepareAnvilEvent event) {
        ItemStack stack = null;

        if (event.getResult() != null) {
            stack = event.getResult().clone();
        }
        if (stack != null) {
            if (stack.getType().equals(Material.NAME_TAG)) {
                return;
            }
            if (stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                String renameText = event.getView().getRenameText();

                if (renameText != null) {
                    renameText = OldColors.convert(renameText);
                    assert renameText != null;
                    meta.displayName(MiniMessage.miniMessage().deserialize(renameText));
                }
                //meta.setDisplayName(event.getInventory().getRenameText().replace("&", "§"));
                stack.setItemMeta(meta);
                event.setResult(stack);
            }
        }
    }

}
