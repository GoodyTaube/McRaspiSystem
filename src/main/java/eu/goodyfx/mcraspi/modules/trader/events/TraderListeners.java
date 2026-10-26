package eu.goodyfx.mcraspi.modules.trader.events;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.inventories.RecipeEditor;
import eu.goodyfx.mcraspi.modules.trader.inventories.TraderSettings;
import eu.goodyfx.mcraspi.modules.trader.managers.TraderManager;
import eu.goodyfx.mcraspi.modules.trader.utils.Trader;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderMenuItem;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderRecipe;
import io.papermc.paper.event.player.PlayerNameEntityEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TraderListeners implements Listener {


    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private static final String TRADER_NAME = "traderKey";
    private final TraderSubSystem system;
    private final TraderManager traderManager;

    public TraderListeners(TraderSubSystem subSystem) {
        this.traderManager = subSystem.getTraderManager();
        this.system = subSystem;
        plugin.setListeners(this);
    }

    @EventHandler
    public void onNameChange(PlayerNameEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Villager villager) {
            PersistentDataContainer container = villager.getPersistentDataContainer();
            if (container.has(system.getTraderKey())) {
                event.setCancelled(true);
                event.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize("<red>\"Ah ah ah, you didn't say the magic word\""));
            }
        }

    }


    @EventHandler
    public void onTraderClick(PlayerInteractAtEntityEvent entityEvent) {
        if (!(entityEvent.getRightClicked() instanceof Villager villager)) {
            return;
        }
        RaspiPlayer player = Raspi.playerLifeCycleService().getRaspiPlayer(entityEvent.getPlayer());
        PersistentDataContainer container = villager.getPersistentDataContainer();
        if (!container.has(system.getTraderKey(), PersistentDataType.STRING)) {
            return;
        }
        entityEvent.setCancelled(true);

        String traderUID = container.get(system.getTraderKey(), PersistentDataType.STRING);
        if (traderUID == null) {
            return;
        }

        Trader trader = system.getTraderManager().getOrLoadTrader(traderUID);
        if (trader == null) {
            return;
        }
        if (player.getPlayer().getGameMode().equals(GameMode.CREATIVE) && player.getPlayer().isSneaking()) {
            String commandPerform = String.format("trader edit %s", trader.getName());
            player.getPlayer().performCommand(commandPerform);
            return;
        }
        String traderName = String.format("<green>%s", trader.getName());
        player.openInventory(MenuType.MERCHANT.builder().merchant(generateMerchant(trader)).title(MiniMessage.miniMessage().deserialize(traderName)).build(player.getPlayer()));

    }


    @EventHandler
    public void onCollideVehicle(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) {
            return;
        }
        PersistentDataContainer container = villager.getPersistentDataContainer();
        if (!container.has(system.getTraderKey())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent clickEvent) {
        if (clickEvent.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
            return;
        }
        RaspiPlayer player = Raspi.playerLifeCycleService().getRaspiPlayer((Player) clickEvent.getWhoClicked());

        int slot = clickEvent.getSlot();
        Inventory inventory = clickEvent.getClickedInventory();
        if (inventory != null && inventory.getHolder(false) instanceof RecipeEditor editor) {
            //Recipe Editor
            int recipeID = editor.getId();
            if (editor.getItemBySlot(slot) != null) {
                TraderMenuItem traderMenuItem = editor.getItemBySlot(slot);
                player.sendDebugMessage(traderMenuItem.name());
                if (traderMenuItem == TraderMenuItem.RECIPE_REMOVE) {
                    editor.getTrader().removeRecipe(recipeID);
                    player.getPlayer().closeInventory();
                    editor.getTrader().openTraderSettings(player);
                    return;
                }
                if (traderMenuItem == TraderMenuItem.RECIPE_SAVE) {
                    editor.saveRecipe(player);
                    player.getPlayer().closeInventory();
                    editor.getTrader().openTraderSettings(player);
                    traderManager.saveTrader(editor.getTrader());
                    return;
                }
                clickEvent.setCancelled(true);

            }

        }

        if (inventory != null && inventory.getHolder(false) instanceof TraderSettings settings) {
            //Trader Settings
            Trader trader = settings.getTrader();
            if (settings.getItemBySlot(slot) != null) {
                TraderMenuItem menuItem = settings.getItemBySlot(slot);
                if (menuItem == TraderMenuItem.SETTING_RANDOM_SET) {
                    trader.setRandom(true);
                    traderManager.saveTrader(trader);
                    settings.getSlotContainer().put(slot, TraderMenuItem.SETTING_RANDOM_REMOVE);
                    clickEvent.getInventory().setItem(slot, TraderMenuItem.SETTING_RANDOM_REMOVE.getItem());
                }

                if (menuItem == TraderMenuItem.SETTING_RANDOM_REMOVE) {
                    trader.setRandom(false);
                    traderManager.saveTrader(trader);
                    settings.getSlotContainer().put(slot, TraderMenuItem.SETTING_RANDOM_SET);
                    clickEvent.getInventory().setItem(slot, TraderMenuItem.SETTING_RANDOM_SET.getItem());
                }

                //Rezept hinzufügen
                if (menuItem == TraderMenuItem.SETTING_ADD_RECIPE) {
                    player.openInventory(trader.recipeEditor(trader.getNextRecipeID()));
                }
            }
            //Rezept bearbeiten
            ItemStack stack = clickEvent.getCurrentItem();
            if (stack != null && stack.hasItemMeta()) {
                PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
                if (container.has(settings.getRecipeIDKey(), PersistentDataType.INTEGER)) {
                    Integer recipeID = container.get(settings.getRecipeIDKey(), PersistentDataType.INTEGER);
                    if (recipeID == null) {
                        player.getPlayer().closeInventory();
                        trader.openTraderSettings(player);
                        return;
                    }
                    player.getPlayer().closeInventory();
                    player.openInventory(trader.recipeEditor(recipeID));
                }
            }
            clickEvent.setCancelled(true);
        }
    }


    private Merchant generateMerchant(Trader trader) {

        Merchant merchant = Bukkit.createMerchant();
        List<MerchantRecipe> recipes = new ArrayList<>();
        List<TraderRecipe> list = new ArrayList<>(trader.getRecipes());

        //RANDOM

        if (trader.isRandom()) {
            TraderRecipe traderRecipe = list.get(plugin.getRandom().nextInt(list.size()));
            MerchantRecipe merchantRecipe = new MerchantRecipe(traderRecipe.getResult(), 999);
            merchantRecipe.addIngredient(traderRecipe.getCurrency_slot1());
            if (traderRecipe.getCurrency_slot2() != null) {
                merchantRecipe.addIngredient(traderRecipe.getCurrency_slot2());
            }
            recipes.add(merchantRecipe);
            merchant.setRecipes(recipes);
            return merchant;
        }


        for (TraderRecipe traderRecipe : list) {
            MerchantRecipe merchantRecipe = new MerchantRecipe(traderRecipe.getResult(), 999);
            merchantRecipe.addIngredient(traderRecipe.getCurrency_slot1());
            if (traderRecipe.getCurrency_slot2() != null) {
                merchantRecipe.addIngredient(traderRecipe.getCurrency_slot2());
            }
            recipes.add(merchantRecipe);
        }
        merchant.setRecipes(recipes);
        return merchant;

    }


}
