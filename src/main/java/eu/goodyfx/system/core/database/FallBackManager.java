package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
@Getter
public class FallBackManager {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final File file = new File(plugin.getDataFolder(), "UserDB.yml");
    private final FileConfiguration config = YamlConfiguration.loadConfiguration(file);


    public boolean containOld(UUID uuid) {
        return config.contains(String.format("User.%s", uuid.toString()));
    }

    public <T> T get(String path, Class<T> clazz) {
        return clazz.cast(config.get(path));
    }

    public void perform(RaspiUser user) {
        for (FallBackContents contents : FallBackContents.values()) {
            String path = String.format(contents.getPath(), user.getUuid());
            contents.apply(user, path, this);

        }
    }

    public void perform(UserSettings user) {
        for (FallBackContents contents : FallBackContents.values()) {
            String path = String.format(contents.getPath(), user.getUuid());
            contents.apply(user, path, this);

        }
    }

    public void perform(RaspiUsernames user) {
        for (FallBackContents contents : FallBackContents.values()) {
            String path = String.format(contents.getPath(), user.getUuid());
            contents.apply(user, path, this);

        }
    }

    public void perform(RaspiManagement user) {
        for (FallBackContents contents : FallBackContents.values()) {
            String path = String.format(contents.getPath(), user.getUuid());
            contents.apply(user, path, this);

        }
    }

    public void foundDataDebugMessage(String path) {
        plugin.getDebugger().info(String.format("FOUND_OLD_DATA::::%s", path));

    }

    public <T> T getAndRemove(String path, Class<T> type) {
        if (contains(path)) {
            T ty = get(path, type);
            config.set(path, null);
            save();
            return ty;
        }
        return null;
    }


    public boolean contains(String path) {
        return config.contains(path);
    }

    public void save() {

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save FallbackDB", e);
        }

    }



}
