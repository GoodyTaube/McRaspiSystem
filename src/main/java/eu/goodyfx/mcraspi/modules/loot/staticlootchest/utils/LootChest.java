package eu.goodyfx.mcraspi.modules.loot.staticlootchest.utils;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.managers.LocationManager;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.LootChestSystem;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.tasks.AnimationBlockDisplay;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class LootChest {

    private TextDisplay timeDisplay;
    private Interaction lootInteraction;
    private BlockDisplay chestAnimation;
    private final McRaspiSystem plugin;
    private final Location chestLocation;
    private final World world;
    private LocationManager locationManager;

    public LootChest(McRaspiSystem plugin, Location location) {
        locationManager = plugin.getModule().getLocationManager();
        locationManager.set(location, "lootchest");
        this.plugin = plugin;
        this.world = location.getWorld();
        this.chestLocation = new Location(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.chestLocation.add(0.5, 0, 0.5);
        chest();
        display();
        interaction();
        LootChestSystem.getLootChestSubSystem().getLootChestTimer().getLootChestDisplay().add(this);
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
        this.chestAnimation.setPersistent(true);

    }

    private void display() {
        this.timeDisplay = (TextDisplay) world.spawnEntity(chestLocation.clone().add(0, 2, 0), EntityType.TEXT_DISPLAY);
        this.timeDisplay.text(MiniMessage.miniMessage().deserialize("<green>LootChest"));
        this.timeDisplay.getPersistentDataContainer().set(new NamespacedKey(plugin, "special"), PersistentDataType.INTEGER, 1);
        this.timeDisplay.setBillboard(Display.Billboard.CENTER);
        this.timeDisplay.setPersistent(true);
        AnimationBlockDisplay.getTextDisplayList().add(this.timeDisplay);
    }

    private void interaction() {
        this.lootInteraction = (Interaction) world.spawnEntity(chestLocation, EntityType.INTERACTION);
        this.lootInteraction.getPersistentDataContainer().set(new NamespacedKey(plugin, "special"), org.bukkit.persistence.PersistentDataType.INTEGER, 1);
        this.lootInteraction.setInteractionWidth(1f);
        this.lootInteraction.setInteractionHeight(2f);
        this.lootInteraction.setPersistent(true);
    }

    public void killAll() {
        this.lootInteraction.remove();
        this.chestAnimation.remove();
        this.timeDisplay.remove();

    }

}
