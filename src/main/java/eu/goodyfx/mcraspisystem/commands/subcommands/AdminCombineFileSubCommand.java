package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminCombineFileSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminCombineFileSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "filecombine";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "admin combinefile <filepath> <filename> -a";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 4 && args[3].equalsIgnoreCase("-a")) {
            convertFiles(player, args[1], args[2]);
        } else if (args.length == 3 && args[2].equalsIgnoreCase("-a")) {
            convertFiles(player, plugin.getDataFolder().getAbsolutePath(), args[1]);

        } else {
            player.sendMessage(getSyntax());
        }
        return true;
    }

    private void convertFiles(RaspiPlayer player, String path, String name) {
        Set<File> files = scanFiles(path);

        StringBuilder builder = new StringBuilder("<green>Found Files:<reset>").append(" ");
        files.forEach(file -> builder.append(file.getName()).append(" "));

        if (!files.isEmpty()) {
            player.sendMessage(builder.toString());
            combine(files, player, name);
        } else {
            player.sendMessage("<red>Es wurden keine Files gefunden!");
        }

    }

    private Set<File> scanFiles(String path) {
        File dummyFile = new File(path);
        if (!dummyFile.exists() || !dummyFile.isDirectory()) {
            return Collections.emptySet();
        }
        return Stream.of(Objects.requireNonNull(new File(path).listFiles())).filter(file -> !file.isDirectory()).filter(file -> file.getName().endsWith(".yml")).map(File::getAbsoluteFile).collect(Collectors.toSet());
    }

    private void combine(Set<File> files, RaspiPlayer player, String name) {
        File combinedFile = new File(plugin.getDataFolder() + "/combined/", name + ".yml");

        // Zentrale Map für die Kombination aller Konfigurationen
        Map<String, Object> combinedConfig = new HashMap<>();

        for (File per : files) {
            // Laden der aktuellen Konfigurationsdatei
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(per);

            for (String key : configuration.getKeys(true)) {
                // Nur Werte aus der aktuellen Datei zur zentralen Map hinzufügen
                combinedConfig.put(key, configuration.get(key));
            }
        }

        // Schreiben der kombinierten Konfiguration in die neue Datei
        saveCombinedConfig(combinedConfig, combinedFile, player);
    }

    private static void saveCombinedConfig(Map<String, Object> configData, File outputFile, RaspiPlayer player) {
        FileConfiguration combinedConfiguration = YamlConfiguration.loadConfiguration(outputFile);

        // Alle Einträge der kombinierten Map in die Konfigurationsdatei übertragen
        for (Map.Entry<String, Object> entry : configData.entrySet()) {
            combinedConfiguration.set(entry.getKey(), entry.getValue());
        }

        try {
            // Speichern der kombinierten Datei
            combinedConfiguration.save(outputFile);
            player.sendMessage("Kombinierte Konfiguration erfolgreich gespeichert in " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Fehler beim Speichern der kombinierten Konfiguration: ", e);

        }

    }

}
