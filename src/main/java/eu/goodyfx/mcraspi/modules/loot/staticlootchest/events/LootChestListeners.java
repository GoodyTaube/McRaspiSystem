package eu.goodyfx.mcraspi.modules.loot.staticlootchest.events;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.InventoryBuilder;
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.LootChestSystem;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.tasks.AnimationBlockDisplay;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.utils.LootChestMenuItems;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class LootChestListeners implements Listener {

    private final McRaspiSystem plugin;

    public LootChestListeners(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setListeners(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent clickEvent) {
        outsideCheck(clickEvent);
        lootChest(clickEvent);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent openEvent) {
        String title = PlainTextComponentSerializer.plainText().serialize(openEvent.getView().title());
        if (title.equalsIgnoreCase("Loot")) {
            if (LootChestSystem.getLootChestSubSystem().getLootChestTimer().isLootChestReady()) {
                LootChestSystem.getLootChestSubSystem().getLootChestTimer().resetTimer();
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    Bukkit.getScheduler().runTask(plugin, () -> openEvent.getPlayer().closeInventory());
                    LootChestSystem.getLootChestSubSystem().getLootChestTimer().resetTimer();
                }, 20L * 60);
            }
        }
    }

    private void gettingLoot(InventoryCloseEvent closeEvent) {
        if (PlainTextComponentSerializer.plainText().serialize(closeEvent.getView().title()).equalsIgnoreCase("Loot") && LootChestSystem.getLootChestSubSystem().getLootChestTimer().isLootChestReady()) {
            LootChestSystem.getLootChestSubSystem().getLootChestTimer().resetTimer();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent closeEvent) {
        gettingLoot(closeEvent);
        if (isLootChest(closeEvent.getView()) && !isLootChestMenu(closeEvent.getView())) {
            Inventory inventory = closeEvent.getInventory();
            StringBuilder builder = new StringBuilder("Content:").append(" ");
            AtomicInteger sizer = new AtomicInteger();
            List<ItemStack> content = new ArrayList<>();
            for (ItemStack stack : inventory.getContents()) {
                if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getType().equals(Material.SLIME_BALL)) {
                    continue;
                }
                if (stack != null) {
                    builder.append(stack.getType().name()).append(",");
                    sizer.getAndIncrement();
                    content.add(stack);
                }
            }


            closeEvent.getPlayer().sendRichMessage(builder.toString());
            String catName = PlainTextComponentSerializer.plainText().serialize(closeEvent.getView().title()).replace("LootChest - ", "").replace(" ", "_").toUpperCase();
            closeEvent.getPlayer().sendRichMessage(catName);
            plugin.getModule().getLootChestManager().prepareWriting(content, LootChestMenuItems.valueOf(catName));


        }
    }

    private void lootChest(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        if (isLootChest(clickEvent.getView())) {
            handleLootChestMenuClick(clickEvent, raspiPlayer);
            handleBackItem(clickEvent, raspiPlayer);
        }

    }

    private void handleBackItem(InventoryClickEvent clickEvent, RaspiPlayer player) {
        if (checkNonNull(clickEvent) && Objects.requireNonNull(clickEvent.getCurrentItem()).getType().equals(Material.SLIME_BALL)) {
            player.getPlayer().performCommand("admin lootChest menu");
            clickEvent.setCancelled(true);
        }
    }

    private void handleLootChestMenuClick(InventoryClickEvent clickEvent, RaspiPlayer player) {
        innerMenu(clickEvent, player);
    }

    private void innerMenu(InventoryClickEvent clickEvent, RaspiPlayer player) {
        for (LootChestMenuItems item : LootChestMenuItems.values()) {
            if (checkNonNull(clickEvent) && Objects.requireNonNull(clickEvent.getCurrentItem()).getType().equals(item.getType())) {
                clickEvent.setCancelled(true);
                Inventory inventory = new InventoryBuilder("<green>LootChest - " + item.getTitle(), 9).addBack().build();
                List<ItemStack> itemsToAdd = plugin.getModule().getLootChestManager().getItems(item);
                for (ItemStack itemStack : itemsToAdd) {
                    inventory.addItem(itemStack);
                }
                player.getPlayer().openInventory(inventory);
                player.playSound(RaspiSounds.SUCCESS);
                break;
            }
        }
    }

    private boolean checkNonNull(InventoryClickEvent clickEvent) {
        return clickEvent.getCurrentItem() != null;
    }


    private boolean isLootChest(InventoryView inventory) {
        return PlainTextComponentSerializer.plainText().serialize(inventory.title()).contains("LootChest -");
    }

    private boolean isLootChestMenu(InventoryView inventory) {
        return PlainTextComponentSerializer.plainText().serialize(inventory.title()).contains("LootChest - Menu");
    }


    private void outsideCheck(InventoryClickEvent clickEvent) {
        if (isLootChest(clickEvent.getView()) && clickEvent.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
            clickEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void loadLootChestAnimation(ChunkLoadEvent loadEvent) {
        Entity[] entities = loadEvent.getChunk().getEntities();
        for (Entity entity : entities) {

            PersistentDataContainer container = entity.getPersistentDataContainer();
            if (container.has(new NamespacedKey(plugin, "special"))) {
                if (entity.getType().equals(EntityType.BLOCK_DISPLAY)) {
                    Raspi.debugger().debug("FOUND LOOTCHEST CHEST. CHECKING IF ENABLED");
                    if (!AnimationBlockDisplay.getBlockDisplayList().contains((BlockDisplay) entity)) {
                        AnimationBlockDisplay.getBlockDisplayList().add((BlockDisplay) entity);
                    }

                }
                if (entity.getType().equals(EntityType.TEXT_DISPLAY)) {
                    Raspi.debugger().debug("FOUND LOOTCHEST CHEST. CHECKING IF ENABLED");

                    if (!AnimationBlockDisplay.getTextDisplayList().contains((TextDisplay) entity)) {
                        AnimationBlockDisplay.getTextDisplayList().add((TextDisplay) entity);
                    }

                }
            }

        }
    }


}
