package eu.goodyfx.system.lootchest.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootChestItemParser {

    private final Object itemDisplay;
    private final String type;
    private final String modelID;
    private final String lore;

    public LootChestItemParser(Map<String, Object> data) {
        this.itemDisplay = data.get("itemDisplay");
        this.type = data.get("type").toString();
        this.modelID = data.get("modelID") != null ? data.get("modelID").toString() : null;
        this.lore = data.get("lore") != null ? data.get("lore").toString() : null;

    }


    public List<Component> getLore() {
        List<Component> loreFinal = new ArrayList<>();
        if (this.lore == null) {
            return loreFinal;
        }
        String[] loreSplit = this.lore.split("@");
        for (String line : loreSplit) {
            loreFinal.add(GsonComponentSerializer.gson().deserialize(line));
        }
        return loreFinal;
    }


    public Component getItemDisplay() {
        if (itemDisplay != null) {
            return GsonComponentSerializer.gson().deserialize(String.valueOf(this.itemDisplay));
        }
        return null;
    }

    public int getModelID() {
        if (this.modelID == null) {
            return 0;
        }
        return Integer.parseInt(this.modelID.replace(".0", ""));
    }

    public Material getType() {
        return Material.valueOf(this.type.toUpperCase());
    }


}
