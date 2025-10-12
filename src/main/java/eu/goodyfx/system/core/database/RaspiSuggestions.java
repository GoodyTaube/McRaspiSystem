package eu.goodyfx.system.core.database;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RaspiSuggestions {

    public static CompletableFuture<Suggestions> suggestOnlinePlayers(SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(input)).forEach(builder::suggest);
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestOfflinePlayer(SuggestionsBuilder builder) {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        List<String> cache = plugin.getDatabaseManager().getAllUsernamesCache();
        cache.stream().filter(name -> name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())).limit(20).forEach(builder::suggest);
        return builder.buildFuture();
    }

}
