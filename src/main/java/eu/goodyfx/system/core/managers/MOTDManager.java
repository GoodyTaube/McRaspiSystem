package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.McRaspiSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Random;

public class MOTDManager {

    private final UtilityFileManager fileManager;

    private final String upperTextPath = "upperText";
    private final String downTextPath = "randomText";
    private final Random random = new Random();

    public MOTDManager(McRaspiSystem plugin) {
        this.fileManager = new UtilityFileManager(plugin, "MOTDMessages.yml");
    }


    public void set() {
        Bukkit.getServer().motd(getMessage());
    }

    public Component getMessage() {
        reload();
        if (fileManager.exist() && fileManager.contains(upperTextPath) && Boolean.TRUE.equals(fileManager.contains(downTextPath))) {
            String upperText = fileManager.get(upperTextPath, String.class);
            upperText = upperText.replace("%version%",Bukkit.getServer().getMinecraftVersion());
            return MiniMessage.miniMessage().deserialize(upperText + "\n" + getRandom());
        }
        return Component.empty();
    }

    public String getRandom() {
        if (Boolean.TRUE.equals(fileManager.contains(downTextPath))) {
            List<String> getStringList = fileManager.getStringList(downTextPath);
            return String.format("<dark_red>\"%s\"<reset>", getStringList.get(random.nextInt(getStringList.size())));
        }
        return "";
    }

    public void reload() {
        fileManager.reload();
    }


}
