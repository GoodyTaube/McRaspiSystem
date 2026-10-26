package eu.goodyfx.mcraspi.modules.trader.utils;

import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.managers.TraderDB;
import eu.goodyfx.mcraspi.modules.trader.managers.TraderManager;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class OLDTraderDBMigration {

    private final TraderDB db;
    private final TraderManager manager;

    public OLDTraderDBMigration(TraderSubSystem system) {
        this.db = new TraderDB();
        this.manager = system.getTraderManager();

        if (db.getFile().exists()) {
            system.getPlugin().getLogger().info("Trader // Found Old Data file. Start Migration");
            migrate();
            system.getPlugin().getLogger().info("Trader // Found Old Data file. Ende Migration");
        }
    }


    private void migrate() {
        db.getTraders().forEach(traderName -> {
            Trader trader = load(traderName);
            if (trader == null) {
                db.remove(traderName);
                return;
            }
            manager.saveTrader(trader);
            db.remove(traderName);
        });

    }

    public Trader load(String name) {
        if (!db.traderExist(name)) {
            return null;
        }
        if (!db.shopExist(name)) {
            db.remove(name);
            return null;
        }
        boolean isRandom = db.isRandom(name);
        List<String> shopIDs = db.getShopIds(name);
        Trader trader = manager.createTrader(name.replace("_", " "));
        trader.setRandom(isRandom);

        for (String id : shopIDs) {
            trader.setRecipe(addRecipe(trader, id));
        }
        return trader;
    }


    public TraderRecipe addRecipe(Trader trader, String id) {
        String name = trader.getName().replace(" ", "_");
        int rid = Integer.parseInt(id);
        ItemStack s1 = db.getItemStack(name, TraderDB.DB_SHOP_ITEM_1, rid);
        ItemStack s2 = db.getItemStack(name, TraderDB.DB_SHOP_ITEM_2, rid);
        ItemStack sR = db.getItemStack(name, TraderDB.DB_SHOP_RES, rid);
        return new TraderRecipe(trader.getNextRecipeID(), sR, s1, s2);
    }


}
