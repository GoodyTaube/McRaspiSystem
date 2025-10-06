package eu.goodyfx.system.raspievents.utils;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum EventItems {

    SCHOKO("Schokolade", Material.COCOA_BEANS, 1),
    SCHOKO_BITTER("Zartbitter Schokolade", Material.COCOA_BEANS, 2),
    SCHOKO_WHITE("Weisse Schokolade", Material.COCOA_BEANS, 3),
    SCHOKO_DUBAI("Dubai Schokolade", Material.COCOA_BEANS, 4),
    SCHOKO_COOKIE("Schokowunder Cookie", Material.COCOA_BEANS, 5),
    SCHOKO_BERRY("Schoko Rosine", Material.COCOA_BEANS, 6),
    SCHOKO_ALP("Vollmilch Schokolade", Material.COCOA_BEANS, 7),
    SCHOKO_ALP_COOKIE("Alpenmilch Schokolade", Material.COCOA_BEANS, 8),
    MELTED_SCHOKO("Geschmolzene Schokolade", Material.BOWL, 1),

    KEBAB_CHICKEN_ALL("Döner Hähnchen (ALLES)", Material.BREAD, 1),
    KEBAB_CHICKEN_MEAT("Döner Hähnchen (Nur Fleisch)", Material.BREAD, 2),
    KEBAB_CHICKEN_STRIPES("Hähnchen Kebab Fleisch", Material.COOKED_CHICKEN, 1),

    KEBAB_LAMB_ALL("Döner Lamm (ALLES)", Material.BREAD, 3),
    KEBAB_LAMB_MEAT("Döner Lamm (Nur Fleisch)", Material.BREAD, 4),
    KEBAB_LAMB_STRIPES("Geschnittenes Lamm Fleisch", Material.COOKED_MUTTON, 1),

    SMOOTHIE_BERRY("Smoothie - Berry Dream", Material.GLASS_BOTTLE, 1),
    SMOOTHIE_CACTUS("Smoothie - Cactus Spike", Material.GLASS_BOTTLE, 2),
    SMOOTHIE_CARROT("Smoothie - Rabbit Carrot", Material.GLASS_BOTTLE, 3),
    SMOOTHIE_GREEN("Smoothie - Gesunder Wirbel", Material.GLASS_BOTTLE, 4),
    SMOOTHIE_APPLE("Smoothie - Apple Crush", Material.GLASS_BOTTLE, 5),

    CAKE_APPLE("Apfel Kuchen", Material.CAKE, 1),
    CAKE_SUGAR_CRUSH("Bienenstich Kuchen", Material.CAKE, 2),
    CAKE_SCHOKI("Schoko Kuchen", Material.CAKE, 3),
    CAKE_CARROT("Karotten Kuchen", Material.CAKE, 4),

    ICE_BERRY("Beeren Eis", Material.MILK_BUCKET, 1),
    ICE_MILK("Milcheis", Material.MILK_BUCKET, 2),
    ICE_CACTUS("Kaktus Eis", Material.MILK_BUCKET, 3),
    ICE_SCHOKO("Schoko Eis", Material.MILK_BUCKET, 4),

    SUGAR_CANDY("Zuckerwatte", Material.COBWEB, 1),
    SUGAR_APPLE("Paradies Apfel", Material.APPLE, 1);


    private final String display;
    private final Material type;
    private final int modelData;
    private final String[] lore;

    EventItems(String display, Material outputMaterial, int modelData, String... lore) {
        this.display = display;
        this.type = outputMaterial;
        this.modelData = modelData;
        this.lore = lore;
    }


}
