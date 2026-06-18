package eu.goodyfx.mcraspi.modules.trader.utils;

import lombok.Getter;

@Getter
public enum TraderMenuItems {

    SAVE_ITEM(1, "<green>Speichern", "Speichere das Item"),
    INFO_RECIPE_1(2, "<gray>Wird zu --> "),
    INFO_RECIPE_2(3, ""),
    SETTING_RANDOM(4, ""),
    SETTING_BANKER(5, "");

    private Integer id;
    private String name;
    private String[] lore;

    TraderMenuItems(Integer id, String name, String... info) {
        this.id = id;
        this.name = name;
        this.lore = info;
    }


}
