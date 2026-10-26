package eu.goodyfx.mcraspi.modules.trader.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.utils.Trader;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class TraderStorage {

    private final Plugin plugin;
    private final File folder;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TraderStorage(TraderSubSystem system) {
        this.plugin = system.getPlugin();
        this.folder = new File(plugin.getDataFolder(), "traders");
        if (!this.folder.exists()) {
            folder.mkdir();
        }
    }

    public void saveTrader(Trader trader) {
        if (trader.getUid() == null) return;
        File file = new File(folder, trader.getUid() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(trader, writer);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Konnte Trader " + trader.getName());
        }
    }

    public Trader loadTrader(TraderSubSystem system, String uid) {
        File file = new File(folder, uid + ".json");
        if (!file.exists()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            Trader trader = gson.fromJson(reader, Trader.class);
            trader.init(system);

            return trader;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "");
            return null;
        }
    }

    public boolean removeTrader(String uid) {
        File file = new File(folder, uid + ".json");
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }


}
