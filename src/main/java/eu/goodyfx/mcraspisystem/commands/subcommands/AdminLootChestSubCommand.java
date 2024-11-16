package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.tasks.AnimationBlockDisplay;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import eu.goodyfx.mcraspisystem.utils.LootChestMenuItems;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

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
        return "Options to Generate or Open a LootChest Menu";
    }

    @Override
    public String getSyntax() {
        return "/admin lootChest <open|generate>";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("open")) {
            Inventory inv = Bukkit.createInventory(null, 9, MiniMessage.miniMessage().deserialize("<green>LootChest - Menu"));
            ItemStack selten = new ItemBuilder(LootChestMenuItems.SELTEN.getType()).displayName(LootChestMenuItems.SELTEN.getTitle()).build();
            ItemStack nicht_so_selten = new ItemBuilder(LootChestMenuItems.NICHT_SO_SELTEN.getType()).displayName(LootChestMenuItems.NICHT_SO_SELTEN.getTitle()).build();
            ItemStack immer = new ItemBuilder(LootChestMenuItems.IMMER.getType()).displayName(LootChestMenuItems.IMMER.getTitle()).build();
            inv.setItem(2, selten);
            inv.setItem(4, nicht_so_selten);
            inv.setItem(6, immer);
            player.getPlayer().openInventory(inv);
        } else if (args.length == 2 && args[1].equalsIgnoreCase("generate")) {
            generate(player, args);
        }
        return true;
    }

    private void generate(RaspiPlayer player, String[] args) {
        Location location = player.getPlayer().getLocation().clone();
        World world = player.getPlayer().getWorld();
        Location locationChest = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        locationChest.add(0.5, 0, 0.5);
        BlockDisplay chest = (BlockDisplay) world.spawnEntity(locationChest, EntityType.BLOCK_DISPLAY);
        chest.getPersistentDataContainer().set(new NamespacedKey(plugin, "special"), org.bukkit.persistence.PersistentDataType.INTEGER, 1);
        chest.setBlock(Bukkit.createBlockData("minecraft:chest"));
        Transformation transformation = chest.getTransformation();
        transformation.getScale().set(new Vector3f(.5f, .5f, .5f));
        transformation.getTranslation().set(new Vector3f(-.25f, 1, -.25f));
        chest.setTransformation(transformation);
        AnimationBlockDisplay.getBlockDisplayList().add(chest);

        Interaction interaction = (Interaction) world.spawnEntity(locationChest, EntityType.INTERACTION);
        interaction.getPersistentDataContainer().set(new NamespacedKey(plugin, "special"), org.bukkit.persistence.PersistentDataType.INTEGER, 1);
        interaction.setInteractionWidth(1f);
        interaction.setInteractionHeight(2f);
        interaction.customName(MiniMessage.miniMessage().deserialize("<green>LootChest"));
        interaction.setCustomNameVisible(true);
    }
}
