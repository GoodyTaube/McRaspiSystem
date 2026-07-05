package eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils;

import lombok.Getter;
import org.bukkit.World;

@Getter
public enum WorldNameCompiler {

    WORLD(World.Environment.NORMAL, "Oberwelt"), THE_NETHER(World.Environment.NETHER, "Nether"), THE_END(World.Environment.THE_END, "Ende");

    private final World.Environment environment;
    private final String label;

    WorldNameCompiler(World.Environment environment, String label) {
        this.environment = environment;
        this.label = label;
    }

    public static String compile(World world) {
        return switch (world.getEnvironment()) {
            case NORMAL -> WORLD.label;
            case NETHER -> THE_NETHER.label;
            case THE_END -> THE_END.label;
            default -> null;
        };
    }


}
