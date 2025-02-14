package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.TraderDB;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class TraderListeners implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final TraderDB traderDB = plugin.getModuleManager().getTraderDB();

    public TraderListeners() {
        plugin.setListeners(this);
    }

    @EventHandler
    public void onTraderClick(PlayerInteractAtEntityEvent entityEvent) {
        Entity entity = entityEvent.getRightClicked();
        if (entity.getType().equals(EntityType.VILLAGER)) {
            Villager villager = (Villager) entity;
            RaspiPlayer player = plugin.getRaspiPlayer(entityEvent.getPlayer());
            PersistentDataContainer container = villager.getPersistentDataContainer();
            if (container.has(plugin.getNameSpaced("trader"))) {
                String traderUID = container.get(plugin.getNameSpaced("trader"), PersistentDataType.STRING);
                String traderName = String.format("<green>%s", traderDB.getTraderName(traderDB.getTraderByID(traderUID)));
                //Generate Merchant and OPEN by Type
                entityEvent.setCancelled(true);
                player.openInventory(MenuType.MERCHANT.builder().merchant(generateMerchant(traderUID)).title(MiniMessage.miniMessage().deserialize(traderName)).build(player.getPlayer()));
            }

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent clickEvent) {
        if (clickEvent.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
            return;
        }
        if (LegacyComponentSerializer.legacyAmpersand().serialize(clickEvent.getView().title()).equalsIgnoreCase("Trader Settings")) {
            //clickEvent.setCancelled(true);
            ItemStack stack = clickEvent.getCurrentItem();
            if (stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 0) {
                clickEvent.getWhoClicked().closeInventory();
                clickEvent.getWhoClicked().sendRichMessage("Saved");
            }
        }

    }

    private Merchant generateMerchant(String traderType) {
        Merchant merchant = Bukkit.createMerchant();
        MerchantRecipe recipe = new MerchantRecipe(new ItemBuilder(Material.GOLDEN_APPLE).displayName("<dark_purple>RASPI_COIN").build(), 999);
        recipe.addIngredient(new ItemBuilder(Material.GLASS_BOTTLE).displayName("<red>Tränen des Owners").build());
        recipe.addIngredient(new ItemBuilder(Material.ENDER_CHEST).setAmount(2).build());

        MerchantRecipe recipe1 = new MerchantRecipe(new ItemBuilder(Material.GOLD_NUGGET).displayName("<dark_purple>RASPI_COIN").build(), 999);
        recipe1.addIngredient(new ItemBuilder(Material.GLASS_BOTTLE).displayName("<red>Tränen des Owners").build());
        recipe1.addIngredient(new ItemBuilder(Material.ENDER_CHEST).setAmount(2).build());


        merchant.setRecipes(List.of(recipe, recipe1));
        return merchant;
    }


}
