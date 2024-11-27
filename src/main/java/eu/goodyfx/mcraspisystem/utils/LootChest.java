package eu.goodyfx.mcraspisystem.utils;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.tasks.AnimationBlockDisplay;
import eu.goodyfx.mcraspisystem.tasks.LootChestTimer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class LootChest {

    private TextDisplay timeDisplay;
    private Interaction lootInteraction;
    private BlockDisplay chestAnimation;
    private final McRaspiSystem plugin;
    private final Location chestLocation;
    private final World world;

    public LootChest(McRaspiSystem plugin, Location location) {
        this.plugin = plugin;
        this.world = location.getWorld();
        this.chestLocation = new Location(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.chestLocation.add(0.5, 0, 0.5);
        chest();
        display();
        interaction();
        plugin.getLootChestTimer().getLootChestDisplay().add(this);
    }


    private void chest() {
        this.chestAnimation = (BlockDisplay) world.spawnEntity(chestLocation, EntityType.BLOCK_DISPLAY);
        this.chestAnimation.getPersistentDataContainer().set(new NamespacedKey(plugin, "special"), org.bukkit.persistence.PersistentDataType.INTEGER, 1);
        this.chestAnimation.setBlock(Bukkit.createBlockData("minecraft:chest"));
        Transformation transformation = this.chestAnimation.getTransformation();
        transformation.getScale().set(new Vector3f(.5f, .5f, .5f));
        transformation.getTranslation().set(new Vector3f(-.25f, 1, -.25f));
        this.chestAnimation.setTransformation(transformation);
        AnimationBlockDisplay.getBlockDisplayList().add(this.chestAnimation);
    }

    private void display() {
        this.timeDisplay = (TextDisplay) world.spawnEntity(chestLocation.clone().add(0, 2, 0), EntityType.TEXT_DISPLAY);
        this.timeDisplay.text(MiniMessage.miniMessage().deserialize("<green>LootChest"));
        this.timeDisplay.setBillboard(Display.Billboard.CENTER);
    }

    private void interaction() {
        this.lootInteraction = (Interaction) world.spawnEntity(chestLocation, EntityType.INTERACTION);
        this.lootInteraction.getPersistentDataContainer().set(new NamespacedKey(plugin, "special"), org.bukkit.persistence.PersistentDataType.INTEGER, 1);
        this.lootInteraction.setInteractionWidth(1f);
        this.lootInteraction.setInteractionHeight(2f);
    }

    public TextDisplay getTimeDisplay() {
        return this.timeDisplay;
    }

    public Interaction getLootInteraction() {
        return this.lootInteraction;
    }

    public BlockDisplay getChestAnimation() {
        return this.chestAnimation;
    }

    public void killAll() {
        this.lootInteraction.remove();
        this.chestAnimation.remove();
        this.timeDisplay.remove();

    }

}
