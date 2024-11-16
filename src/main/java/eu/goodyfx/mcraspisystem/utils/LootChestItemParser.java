package eu.goodyfx.mcraspisystem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootChestItemParser {

    private final String itemDisplay;
    private final String type;
    private final String modelID;
    private List<Map<String, String>> lore = new ArrayList<>();

    public LootChestItemParser(Map<String, Object> data) {
        this.itemDisplay = data.get("itemDisplay").toString();
        this.type = data.get("type").toString();
        this.modelID = data.get("modelID").toString();
    }

    public String getItemDisplay() {
        return itemDisplay;
    }

    public int getModelID() {
        if(this.modelID.equalsIgnoreCase("none")){
            return 0;
        }
        return Integer.parseInt(this.modelID);
    }

    public Material getType() {
        return Material.valueOf(this.type.toUpperCase());
    }


}
