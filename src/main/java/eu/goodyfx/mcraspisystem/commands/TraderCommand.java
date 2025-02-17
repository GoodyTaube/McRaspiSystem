package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.exceptions.AllReadyExistException;
import eu.goodyfx.mcraspisystem.managers.TraderDB;
import eu.goodyfx.mcraspisystem.utils.InventoryBuilder;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class TraderCommand implements CommandExecutor, TabCompleter {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final TraderDB traderDB = plugin.getModule().getTraderDB();

    public static final Map<UUID, String> traderEditContainer = new HashMap<>();
    public static final Map<UUID, Integer> traderSAVEContainer = new HashMap<>();


    private static final String OPERATION_EDIT = "edit";
    private static final String OPERATION_SPAWN = "spawn";
    private static final String OPERATION_REMOVE = "remove";
    private static final String OPERATION_TRADER_NAME = "<traderName>";


    public TraderCommand() {
        plugin.setCommand("trader", this, this);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of(OPERATION_TRADER_NAME, OPERATION_SPAWN, OPERATION_EDIT, OPERATION_REMOVE);
        }
        if (args.length == 2) {
            return plugin.getModule().getTraderDB().getTraders();
        }
        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player dummy)) {
            sender.sendRichMessage("ONLY _ PLAYER _ COMMAND");
            return true;
        }
        RaspiPlayer player = plugin.getRaspiPlayer(dummy);

        if (args.length == 1) {
            String name = args[0];

            //INIT DB
            try {
                traderDB.create(name);
                //SPAWN VILLAGER
                spawnVillager(player.getLocation(), name, traderDB.getUUID(name));
            } catch (AllReadyExistException e) {
                player.sendMessage(String.format("%s<red>Der Trader: <yellow>%s <red>existiert bereits.", plugin.getModule().getRaspiMessages().getPrefix(), name));
            }
            return true;
        }
        edit(args, player);
        spawn(args, player);
        remove(args, player);
        return false;
    }

    private void remove(String[] args, RaspiPlayer player) {
        if (args.length == 2 && args[0].equalsIgnoreCase(OPERATION_REMOVE)) {
            traderDB.remove(args[1]);
            player.sendMessage("<green>Removed " + args[1] + " aus der Datenbank.");
        }
    }

    private void edit(String[] args, RaspiPlayer player) {
        traderDB.reload();
        if (args.length == 2 && args[0].equalsIgnoreCase(OPERATION_EDIT)) {
            if (traderDB.shopExist(args[1])) {
                openRecipes(player, args[1]);
                traderEditContainer.put(player.getUUID(), args[1]);
                return;
            }

            traderEditContainer.put(player.getUUID(), args[1]);
            player.openInventory(getEditInventory());

        }

    }

    public static Inventory getEditInventory() {
        //OPEN INV TO PLACE ORDER
        ItemStack delete = new ItemBuilder(Material.BARRIER).displayName("<red>Rezept Löschen").setModelID(1).addLore("Lösche dieses Rezept").addLore("<red><b>PERMANENT").build();
        ItemStack buy = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("<green>Item zum Bezahlen.").addLore("<green>Bezahl Item").addLore("max 64x Item").build();
        ItemStack sell = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).displayName("<green>Item zum Kaufen.").addLore("<green>Kauf Item").addLore("max 64x Item").build();
        ItemStack arrow = new ItemBuilder(Material.MAGENTA_GLAZED_TERRACOTTA).displayName("<gray>Wird zu-->").build();
        ItemStack save = new ItemBuilder(Material.SLIME_BLOCK).displayName("<green>Speichern.").setModelID(0).addLore("<green>Speichere das Rezept.").build();

        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        Map<Integer, ItemStack> itemContainer = new HashMap<>();
        itemContainer.put(0, buy);
        itemContainer.put(1, buy);
        itemContainer.put(2, arrow);
        itemContainer.put(3, sell);
        itemContainer.put(6, delete);
        itemContainer.put(17, save);
        return new InventoryBuilder("Trader Settings", 18).filler(filler, 9, 10, 12).setItems(itemContainer).build();
    }


    private void openRecipes(RaspiPlayer player, String trader) {
        List<String> shopRecipes = traderDB.getShopIds(trader);
        Inventory inventory;
        if (shopRecipes.size() < 9) {
            inventory = new InventoryBuilder("Trader Rezepte", 9).build();
        } else {
            inventory = new InventoryBuilder("Trader Rezepte", InventoryType.CHEST.getDefaultSize()).build();
        }

        //Rezept Hinzufügen Item
        ItemStack stack = new ItemBuilder(Material.GOLD_INGOT).setModelID(100).displayName("<green><b>Rezept Hinzufügen").build();
        for (String id : traderDB.getShopIds(trader)) {
            //Rezepte Anzeigen im Inventar
            ItemStack result = traderDB.getItemStack(trader, TraderDB.DB_SHOP_RES, Integer.parseInt(id));
            ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER).setModelID(Integer.parseInt(id));
            itemBuilder.addLore("Verkauf: " + result.getType().name());
            if (result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
                itemBuilder.addLore(result.displayName());
            }
            itemBuilder.displayName("<gray>Rezept: <aqua>" + id);
            inventory.addItem(itemBuilder.build());
        }
        inventory.setItem(inventory.getSize() - 1, stack);
        player.openInventory(inventory);
    }


    private void spawn(String[] args, RaspiPlayer player) {
        if (args.length == 2 && args[0].equalsIgnoreCase(OPERATION_SPAWN)) {
            TraderDB traderDB = plugin.getModuleManager().getTraderDB();
            String name = args[1];
            if (!traderDB.traderExist(name)) {
                traderDB.reload();
                player.getPlayer().performCommand("trader " + name);
                return;
            }
            spawnVillager(player.getLocation(), traderDB.getTraderName(name), traderDB.getUUID(name));
        }
    }

    private void spawnVillager(Location location, String name, String uuid) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomNameVisible(true);
        villager.customName(MiniMessage.miniMessage().deserialize("<green>" + name));
        villager.setAI(false);
        villager.setAdult();
        villager.setInvulnerable(true);
        PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
        dataContainer.set(plugin.getNameSpaced("trader"), PersistentDataType.STRING, uuid);
        //OUTPUT CONSOLE
        String log = String.format("Villager %s spawned in %s x=%s y=%s z=%s", name, location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        plugin.getLogger().info(log);
    }

}
