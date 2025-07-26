package eu.goodyfx.system.reise.utils;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;

public class ReiseDisplayBuilder {

    private final McRaspiSystem plugin;
    private final Location location;
    private EntityType entityType = EntityType.BLOCK_DISPLAY;

    private Material material = Material.WHITE_STAINED_GLASS;
    private int time = 60;

    public ReiseDisplayBuilder(McRaspiSystem plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    public ReiseDisplayBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public ReiseDisplayBuilder setEntityType(EntityType type) {
        this.entityType = type;
        return this;
    }

    public ReiseDisplayBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public void buildBlockDisplay() {
        Location location1 = new Location(location.getWorld(), location.getBlockX(), location.getY(), location.getBlockZ(), 0f, 0f);
        BlockDisplay entity = (BlockDisplay) location.getWorld().spawnEntity(location1.subtract(1, 0, 1), EntityType.BLOCK_DISPLAY);
        entity.setBlock(Bukkit.createBlockData(material));
        Transformation transformation = entity.getTransformation();
        transformation.getScale().set(3);
        entity.setTransformation(transformation);
        entity.setGlowing(true);
        entity.setPersistent(false);
        Bukkit.getScheduler().runTaskLater(plugin, entity::remove, time * 20L);
    }

    public void buildEntity() {
        BlockDisplay entity = (BlockDisplay) location.getWorld().spawnEntity(location, entityType);

        entity.setBlock(Bukkit.createBlockData(material));

        Bukkit.getScheduler().runTaskLater(plugin, entity::remove, time * 20L);
    }

}
