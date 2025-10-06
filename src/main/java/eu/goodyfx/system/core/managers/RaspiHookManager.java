package eu.goodyfx.system.core.managers;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.DiscordIntegration;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

@Getter
public class RaspiHookManager {

    private final McRaspiSystem system;
    private final Plugin plugin;

    private LuckPerms luckPerms;
    private DiscordIntegration discordIntegration;
    private ProtocolManager protocolManager;

    public RaspiHookManager(McRaspiSystem system, Plugin plugin) {
        this.system = system;
        this.plugin = plugin;
        startHook();
    }

    public void startHook() {
        boolean hook = true;
        hook = hook && luckPermsHook();
        hook = hook && protocolManager();
        if (!hook) {
            plugin.getLogger().log(Level.SEVERE, "Hooking Failed!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        } else {
            discordIntegration = new DiscordIntegration();
        }
    }

    private boolean luckPermsHook() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPerms = provider.getProvider();
            return true;
        }
        plugin.getLogger().info("Plugin LuckPerms not Found!");
        return false;
    }

    private boolean protocolManager() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        if (manager != null) {
            this.protocolManager = manager;
            return true;
        }
        plugin.getLogger().log(Level.SEVERE, "Plugin ProtocolLib not Found!");
        return false;
    }
}
