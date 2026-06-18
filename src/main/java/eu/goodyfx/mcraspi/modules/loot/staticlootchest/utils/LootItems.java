package eu.goodyfx.mcraspi.modules.loot.staticlootchest.utils;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum LootItems {
    SPONGE(Material.SPONGE, "<green>Super Sponge", 1, null, "<aqua>Absorbation: <gray>x100", "<red>Test"), SWIFT(Material.ENCHANTED_BOOK, "Schnelligkeit 3", 1, Enchantment.SOUL_SPEED, "<gray>Schnelligkeit III"), FLY(Material.GOLDEN_APPLE, "<green>Flug Power", Powers.FLIGHT.getId(), null, "<gray>Aktiviere um zu Fliegen.", "<aqua>Zeit: <gray>" + Powers.FLIGHT.getTime() + " min"), NIGHT(Material.GOLDEN_APPLE, "<green>SAW Power", Powers.NIGHT_VISION.getId(), null, "<gray>Aktiviere um in der Nacht zu Sehen.", "<aqua>Zeit: <gray>" + Powers.NIGHT_VISION.getTime() + " min"),

    TELEPORTER(Material.POLISHED_BLACKSTONE_BUTTON, "<green>Teleporter", 1, null, "Setze deinen eigenen Teleport"),
    BAN_HAMMER(Material.MACE, "<red>Ban Hammer", 1, Enchantment.INFINITY, "Sperrt einen Spieler");

    private final String label;
    private final Enchantment enchantment;
    private final List<String> lore;
    private final Material type;

    private final int modelID;

    LootItems(Material type, String label, int id, @Nullable Enchantment enchantment, @Nullable String... lore) {
        this.label = label;
        this.type = type;
        this.modelID = id;
        this.enchantment = enchantment;
        if (lore != null) {
            this.lore = List.of(lore);
        } else this.lore = null;
    }

    public Component getLabel() {
        return MiniMessage.miniMessage().deserialize(this.label);
    }

    public List<Component> getLore() {

        if (this.lore == null) {
            return null;
        }

        return this.lore.stream().map(val -> MiniMessage.miniMessage().deserialize(val)).collect(Collectors.toList());
    }
}
