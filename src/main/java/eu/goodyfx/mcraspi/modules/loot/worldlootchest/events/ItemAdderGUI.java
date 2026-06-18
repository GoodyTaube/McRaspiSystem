package eu.goodyfx.mcraspi.modules.loot.worldlootchest.events;

import eu.goodyfx.mcraspi.core.utils.ItemBuilder;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.RandomLootChest;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.managers.DatabaseManager;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils.LoadChances;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ItemAdderGUI implements Listener {

    private final LoadChances lc;
    private final RandomLootChest randomLootChest;
    private final DatabaseManager data;

    public ItemAdderGUI(RandomLootChest randomLootChest) {
        this.lc = new LoadChances(randomLootChest);
        this.randomLootChest = randomLootChest;
        this.data = randomLootChest.getDatabaseManager();
        randomLootChest.setEvents(this);
    }


    public ConfigurationSection itDB() {
        return randomLootChest.getDatabaseManager().getConfig().getConfigurationSection("ItemDatabase");
    }

    public void addOnMap(ItemStack item) {
        for (int i = 0; i < 100000; ++i) {
            if (randomLootChest.getItemsToAdd().get(i) == null) {
                randomLootChest.getItemsToAdd().put(i, item);
                randomLootChest.getItemsToAdd().put(i, item);
                break;
            }
        }

    }

    public void loadItems() {
        data.loadData();

        for (int i = 0; i < 10000 && this.itDB().isConfigurationSection(String.valueOf(i)); ++i) {
            ItemStack item = Objects.requireNonNull(this.itDB().getConfigurationSection(String.valueOf(i))).getItemStack("item");
            int chance = Objects.requireNonNull(this.itDB().getConfigurationSection(String.valueOf(i))).getInt("chance");
            randomLootChest.getItemsToAdd().put(i, item);
            randomLootChest.getChances().put(i, chance);
        }

    }

    public boolean isFull(Inventory inv) {
        for (int i = 0; i < 45; ++i) {
            if (inv.getItem(i) == null) {
                return false;
            }
        }

        return true;
    }

    public void saveItems() {
        data.loadData();
        int toset = 0;
        data.getConfig().set("ItemDatabase", (Object) null);
        data.getConfig().createSection("ItemDatabase");

        for (int i = 0; i < randomLootChest.getItemsToAdd().size(); ++i) {
            if (randomLootChest.getItemsToAdd().get(i) != null) {
                this.itDB().createSection(String.valueOf(toset));
                Objects.requireNonNull(this.itDB().getConfigurationSection(String.valueOf(toset))).set("item", randomLootChest.getItemsToAdd().get(i));
                if (randomLootChest.getChances().get(i) != null && (Integer) randomLootChest.getChances().get(i) != 0) {
                    Objects.requireNonNull(this.itDB().getConfigurationSection(String.valueOf(toset))).set("chance", randomLootChest.getChances().get(i));
                } else {
                    Objects.requireNonNull(this.itDB().getConfigurationSection(String.valueOf(toset))).set("chance", 50);
                }
            } else {
                --toset;
            }

            ++toset;
        }

        data.save();
        this.loadItems();
        this.lc.loadItems();
    }

    public void addChance(int id, int chanceToAdd) {
        int currentChance = (Integer) randomLootChest.getChances().get(id);
        randomLootChest.getChances().put(id, currentChance + chanceToAdd);
    }

    public void remove(int id, int chanceToRemove) {
        int currentChance = (Integer) randomLootChest.getChances().get(id);
        randomLootChest.getChances().put(id, currentChance - chanceToRemove);
    }

    public void addItems(Player player, int page) {
        Inventory inv = player.getOpenInventory().getTopInventory();

        ItemStack arrowBack = new ItemBuilder(Material.ARROW)
                .displayName("<red>Back")
                .build();
        ItemStack arrowNext = new ItemBuilder(Material.ARROW)
                .displayName("<red>Back")
                .build();
        inv.setItem(45, arrowBack);
        inv.setItem(53, arrowNext);

        ItemStack paper = new ItemBuilder(Material.PAPER)
                .displayName("<gold>Info:")
                .addLore("<green>To add items to the chests")
                .addLore("<green>just drop them in here.")
                .addLore("<green>Ti edit the chance of an item")
                .addLore("<green>right click it  and a chance editor")
                .addLore("<green>gui will open.")
                .addLore("<red>If you don't edit the chance it will")
                .addLore("<red>automatically be set to 50.")
                .build();
        inv.setItem(49, paper);
        int maxNumber;
        int firstNumber;
        firstNumber = page * 45 - 45;
        maxNumber = page * 45;
        int slotcounter = 0;

        for (int i = firstNumber; i < maxNumber; ++i) {
            if (randomLootChest.getItemsToAdd().get(i) != null) {
                inv.setItem(slotcounter, (ItemStack) randomLootChest.getItemsToAdd().get(i));
            }

            ++slotcounter;
        }

        ItemStack filler = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).displayName(" ").build();
        inv.setItem(47, filler);
        inv.setItem(48, filler);
        inv.setItem(46, filler);
        inv.setItem(51, filler);
        inv.setItem(52, filler);
        inv.setItem(50, filler);
    }

    public void openPage(Player player, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize(String.format("<dark_gray>Page: %s/5", page)));
        randomLootChest.getAddItem().add(player);
        randomLootChest.getCurrentPage().put(player, page);
        player.openInventory(inv);
        this.addItems(player, page);
    }

    public void openGui(Player player) {
        this.loadItems();
        this.openPage(player, 1);
    }

    public ItemStack item(ItemStack item, String name, int durability) {
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.displayName(MiniMessage.miniMessage().deserialize(name));
        item.setItemMeta(itemmeta);

        item.setDurability((short) durability);
        return item;
    }

    public Integer chanceToAddOrRemove(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return null;
        }

        String name = MiniMessage.miniMessage().serialize(Objects.requireNonNull(item.getItemMeta().displayName()));
        String name1 = name.replace("Add", "");
        String name2 = name1.replace("Remove", "");
        String name3 = name2.replace(" ", "");
        return Integer.parseInt(name3);
    }

    @EventHandler
    public void onClick(InventoryClickEvent clickEvent) {
        if (!(clickEvent.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (randomLootChest.getAddItem().contains(player)) {
            if (clickEvent.getCurrentItem() == null) {
                return;
            }

            if (clickEvent.getSlot() == 53) {
                clickEvent.setCancelled(true);
                if ((Integer) randomLootChest.getCurrentPage().get(player) == 5) {
                    return;
                }

                if (this.isFull(clickEvent.getInventory())) {
                    int nextpage = (Integer) randomLootChest.getCurrentPage().get(player) + 1;
                    player.getOpenInventory().close();
                    this.openPage(player, nextpage);
                } else {
                    player.sendMessage("§cPlease fill this page before moving to the next one.");
                }

                return;
            }

            if (clickEvent.getSlot() == 45) {
                clickEvent.setCancelled(true);
                if ((Integer) randomLootChest.getCurrentPage().get(player) == 1) {
                    return;
                }

                int previousPage = (Integer) randomLootChest.getCurrentPage().get(player) - 1;
                player.getOpenInventory().close();
                this.openPage(player, previousPage);
                return;
            }

            if (clickEvent.getSlot() > 45 && clickEvent.getSlot() < 54) {
                clickEvent.setCancelled(true);
            }

            if (clickEvent.getClick().equals(ClickType.RIGHT) || clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                this.save(clickEvent.getInventory(), (Integer) randomLootChest.getCurrentPage().get(player));
                if (!clickEvent.getCurrentItem().getType().toString().equalsIgnoreCase("AIR") && clickEvent.getSlot() < 45) {
                    clickEvent.setCancelled(true);
                    int itemID;
                    if ((Integer) randomLootChest.getCurrentPage().get(player) == 1) {
                        itemID = clickEvent.getSlot();
                    } else {
                        itemID = (Integer) randomLootChest.getCurrentPage().get(player) * 45 - 45 + clickEvent.getSlot();
                    }

                    this.openChanceEditor(player, itemID, (Integer) randomLootChest.getCurrentPage().get(player));
                }
            }
        } else if (randomLootChest.getIsEditing().containsKey(player)) {
            Inventory inv = clickEvent.getInventory();
            clickEvent.setCancelled(true);
            int id = (Integer) randomLootChest.getIsEditing().get(player);
            int currentchance = (Integer) randomLootChest.getChances().get(id);
            int lastpage = (Integer) randomLootChest.getLastPageNO().get(player);
            if (clickEvent.getCurrentItem().getType().equals(Material.ARROW)) {
                player.closeInventory();
                this.openPage(player, lastpage);
                return;
            }

            if (clickEvent.getCurrentItem().getType().equals(Material.WHITE_STAINED_GLASS_PANE)) {
                if (clickEvent.getCurrentItem().getDurability() == 13) {
                    int chancetoadd = this.chanceToAddOrRemove(clickEvent.getCurrentItem());
                    if (currentchance + chancetoadd > 100) {
                        player.sendMessage("§cThe chance can't be bigger than 100");
                    } else {
                        this.addChance(id, chancetoadd);
                        inv.setItem(13, this.item(new ItemStack(Material.DIAMOND), "§6Current Chance: §c" + randomLootChest.getChances().get(id), 0));
                    }
                } else if (clickEvent.getCurrentItem().getDurability() == 14) {
                    int chancetoremove = this.chanceToAddOrRemove(clickEvent.getCurrentItem());
                    if (currentchance - chancetoremove < 1) {
                        player.sendMessage("§cThe chance can't be less than 1");
                    } else {
                        this.remove(id, chancetoremove);
                        inv.setItem(13, this.item(new ItemStack(Material.DIAMOND), "§6Current Chance: §c" + randomLootChest.getChances().get(id), 0));
                    }
                }
            }
        }

    }

    public void save(Inventory inv, int page) {
        int maplast;
        int mapfirst;
        if (page == 1) {
            maplast = page * 45;
            mapfirst = page * 45 - 45;
        } else {
            mapfirst = page * 45 - 45;
            maplast = page * 45;
        }

        int slotcounter = 0;

        for (int i = mapfirst; i < maplast; ++i) {
            randomLootChest.getItemsToAdd().remove(i);
            if (inv.getItem(slotcounter) != null) {
                this.addOnMap(inv.getItem(slotcounter));
            }

            ++slotcounter;
        }

        this.saveItems();
    }

    public void openChanceEditor(Player player, int itemid, int currentpage) {
        randomLootChest.getIsEditing().put(player, itemid);
        randomLootChest.getLastPageNO().put(player, currentpage);
        Inventory inv = Bukkit.createInventory((InventoryHolder) null, 27, "§aEdit the chance");
        inv.setItem(10, this.item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), "§aAdd 50", 13));
        inv.setItem(11, this.item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), "§aAdd 10", 13));
        inv.setItem(12, this.item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), "§aAdd 1", 13));
        inv.setItem(13, this.item(new ItemStack(Material.DIAMOND), "§6Current Chance: §c" + randomLootChest.getChances().get(itemid), 0));
        inv.setItem(14, this.item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), "§aRemove 1", 14));
        inv.setItem(15, this.item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), "§aRemove 10", 14));
        inv.setItem(16, this.item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), "§aRemove 50", 14));
        inv.setItem(18, this.item(new ItemStack(Material.ARROW), "§cBack", 0));

        for (int i = 0; i < 26; ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, this.item(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), " ", 8));
            }
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (randomLootChest.getAddItem().contains(e.getPlayer())) {
            Player player = (Player) e.getPlayer();
            this.save(e.getInventory(), (Integer) randomLootChest.getCurrentPage().get(player));
            e.getPlayer().sendMessage("§aItem list updated!");
            randomLootChest.getAddItem().remove(player);
            randomLootChest.getCurrentPage().remove(player);
        } else if (randomLootChest.getIsEditing().containsKey(e.getPlayer())) {
            randomLootChest.getIsEditing().remove(e.getPlayer());
            randomLootChest.getLastPageNO().remove(e.getPlayer());
            this.saveItems();
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (randomLootChest.getAddItem().contains(e.getPlayer())) {
            randomLootChest.getAddItem().remove(e.getPlayer());
            randomLootChest.getCurrentPage().remove(e.getPlayer());
        }

    }

}
