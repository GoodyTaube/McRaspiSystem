package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;

public class MessageManager {

    private final UtilityFileManager manager;
    private final McRaspiSystem plugin;

    public MessageManager(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.manager = new UtilityFileManager(plugin, "messages");
        setUpCheck();
    }

    private void setUpCheck() {
        if (manager.file().exists()) {
            return;
        }
        plugin.saveResource("messages.yml", false);
    }

    public String getString(String path) {
        return manager.get(path, String.class);
    }

    public boolean getBoolean(String path) {
        return manager.get(path, Boolean.class);
    }

    public boolean contains(String path) {
        return manager.contains(path);
    }


}
