package eu.goodyfx.mcraspi.core.commandsold.subcommands;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.ItemBuilder;
import eu.goodyfx.mcraspi.core.utils.SubCommand;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.LootChestSystem;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.tasks.AnimationBlockDisplay;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.utils.LootChest;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.utils.LootChestLoot;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.utils.LootChestMenuItems;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
@Deprecated
public class AdminLootChestSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminLootChestSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "lootChest";
    }

    @Override
    public String getDescription() {
        return "Ein Command um die LootChest auf dem Server zu steuern.";
    }

    @Override
    public String getSyntax() {
        return "/admin lootChest <open|generate|menu>";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("menu")) {
            Inventory inv = Bukkit.createInventory(null, 9, MiniMessage.miniMessage().deserialize("<green>LootChest - Menu"));
            ItemStack selten = new ItemBuilder(LootChestMenuItems.SELTEN.getType()).displayName(LootChestMenuItems.SELTEN.getTitle()).build();
            ItemStack nichtSoSelten = new ItemBuilder(LootChestMenuItems.NICHT_SO_SELTEN.getType()).displayName(LootChestMenuItems.NICHT_SO_SELTEN.getTitle()).build();
            ItemStack immer = new ItemBuilder(LootChestMenuItems.IMMER.getType()).displayName(LootChestMenuItems.IMMER.getTitle()).build();
            inv.setItem(2, selten);
            inv.setItem(4, nichtSoSelten);
            inv.setItem(6, immer);
            player.getPlayer().openInventory(inv);
        } else if (args.length == 2 && args[1].equalsIgnoreCase("generate")) {
            new LootChest(plugin, player.getLocation());
        } else if (args.length == 2 && args[1].equalsIgnoreCase("open")) {
            new LootChestLoot(plugin).openLoot(player);
        } else if (args.length == 2 && args[1].equalsIgnoreCase("kill")) {
            for (LootChest lootChest : LootChestSystem.getLootChestSubSystem().getLootChestTimer().getLootChestDisplay()) {
                lootChest.killAll();
            }
            LootChestSystem.getLootChestSubSystem().getLootChestTimer().getLootChestDisplay().clear();
            AnimationBlockDisplay.getBlockDisplayList().clear();

            player.getPlayer().getNearbyEntities(5, 5, 5).forEach(entity -> {
                if(entity.getType().equals(EntityType.INTERACTION)){
                    entity.remove();
                }
                if(entity.getType().equals(EntityType.BLOCK_DISPLAY)){
                    entity.remove();
                }
                if(entity.getType().equals(EntityType.TEXT_DISPLAY)){
                    entity.remove();
                }
                Raspi.debugger().debug("Removed OLD_Lootchest!");
            });

        }

        return true;
    }


}
