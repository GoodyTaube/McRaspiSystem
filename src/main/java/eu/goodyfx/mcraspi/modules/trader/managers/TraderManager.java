package eu.goodyfx.mcraspi.modules.trader.managers;

import com.google.gson.Gson;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.utils.Trader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class TraderManager {

    private final Map<String, Trader> traderCache = new ConcurrentHashMap<>();
    private final TraderSubSystem system;
    private final TraderStorage storage;

    public TraderManager(TraderSubSystem system) {
        this.system = system;
        this.storage = new TraderStorage(system);
        loadAll();
    }

    private void loadAll() {
        File folder = new File(system.getPlugin().getDataFolder(), "traders");
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles(((dir, subName) -> subName.endsWith(".json")));
        if (files == null) return;

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                Trader trader = new Gson().fromJson(reader, Trader.class);
                if (trader != null) {
                    trader.init(system);
                    traderCache.put(trader.getUid(), trader);
                }
            } catch (IOException e) {
                system.getPlugin().getLogger().log(Level.SEVERE, "error while reading " + file.getName() + ".json", e);
            }
        }
    }

    public Trader getOrLoadTrader(String uID) {
        if (uID == null || uID.isEmpty()) return null;
        if (traderCache.containsKey(uID)) {
            return traderCache.get(uID);
        }

        Trader trader = storage.loadTrader(system, uID);
        if (trader != null) {
            traderCache.put(uID, trader);
            return trader;
        }
        return null;
    }

    public Trader getTraderByName(String name) {
        if (name == null || name.isEmpty()) return null;

        for (Trader trader : traderCache.values()) {
            if (trader.getName().equalsIgnoreCase(name)) {
                return trader;
            }
        }

        File folder = new File(system.getPlugin().getDataFolder(), "traders");
        if (!folder.exists() || !folder.isDirectory()) {
            return null;
        }

        File[] files = folder.listFiles(((dir, subName) -> subName.endsWith(".json")));
        if (files == null) return null;

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                Trader trader = new Gson().fromJson(reader, Trader.class);
                if (trader != null) {
                    trader.init(system);
                    traderCache.put(trader.getUid(), trader);
                    if (trader.getName().equalsIgnoreCase(name)) {
                        return trader;
                    }
                }
            } catch (IOException e) {
                system.getPlugin().getLogger().log(Level.SEVERE, "error while reading trader.json", e);
            }
        }

        return null;
    }

    public boolean traderExists(String name) {
        return getTraderByName(name) != null;
    }


    public Trader createTrader(String name) {
        String uid = generateNext();

        Trader trader = new Trader(system, name, uid);
        traderCache.put(uid, trader);
        storage.saveTrader(trader);
        return trader;
    }

    public String generateNext() {
        int id = 1;
        while (true) {
            String var = "Trader" + id;
            File file = new File(system.getPlugin().getDataFolder(), "traders/" + var + ".json");
            if (!file.exists()) {
                return var;
            }
            id++;
        }
    }

    public boolean delete(String uid) {
        traderCache.remove(uid);
        return storage.removeTrader(uid);
    }


    public void saveAllSync() {
        for (Trader trader : traderCache.values()) {
            storage.saveTrader(trader);
        }
    }

    public void saveTrader(Trader trader) {

        storage.saveTrader(trader);

    }


    public List<String> getActiveNames() {
        return traderCache.values().stream().map(Trader::getName).toList();
    }

}
