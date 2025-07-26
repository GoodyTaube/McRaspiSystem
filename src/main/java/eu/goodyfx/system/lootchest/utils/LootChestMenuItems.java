package eu.goodyfx.system.lootchest.utils;

import org.bukkit.Material;

public enum LootChestMenuItems {

    SELTEN("Selten", Material.DIAMOND, "selten"),
    NICHT_SO_SELTEN("Nicht so Selten", Material.GOLD_BLOCK, "nichtSoSelten"),
    IMMER("Immer", Material.STICK, "immer");

    private final String title;
    private final Material type;
    private final String db_Name;

    LootChestMenuItems(String title, Material type, String database) {
        this.title = title;
        this.type = type;
        this.db_Name = database;
    }

    public String getDatabaseName() {
        return this.db_Name;
    }

    public String getTitle() {
        return this.title;
    }

    public Material getType() {
        return this.type;
    }

}
