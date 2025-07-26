package eu.goodyfx.system.lootchest.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public enum LootItems {
    SPONGE(Material.SPONGE, "<green>Super Sponge", 1, null, "<aqua>Absorbation: <gray>x100", "<red>Test"), SWIFT(Material.ENCHANTED_BOOK, "Schnelligkeit 3", 1, Enchantment.SOUL_SPEED, "<gray>Schnelligkeit III"), FLY(Material.GOLDEN_APPLE, "<green>Flug Power", Powers.FLIGHT.getId(), null, "<gray>Aktiviere um zu Fliegen.", "<aqua>Zeit: <gray>" + Powers.FLIGHT.getTime() + " min"), NIGHT(Material.GOLDEN_APPLE, "<green>SAW Power", Powers.NIGHT_VISION.getId(), null, "<gray>Aktiviere um in der Nacht zu Sehen.", "<aqua>Zeit: <gray>" + Powers.NIGHT_VISION.getTime() + " min"),

    COIN(Material.GOLDEN_APPLE, "Raspi Apple", 10, null, "COOLER_RASPI_COIN"), HASTE(Material.GOLDEN_APPLE, "<green>Schnelligkeit Power", Powers.HASTE.getId(), null, "<gray>Aktiviere um Schneller Abzubauen.", "<aqua>Zeit: <gray>" + Powers.HASTE.getTime() + " min"), KNOCK(Material.STICK, "<green>Knockback Stick", 1, null, null),

    Adult_Stick(Material.STICK, "<green>Adult Stick", 2, null, null),

    Baby_Stick(Material.STICK, "<green>Baby Stick", 3, null, null),

    CREEPER_CHARGE(Material.STICK, "<green>Creeper Charge", 4, null, "Gibt einen Creeper [Charge]", "<red>!Noch in Arbeit!"),


    TELEPORTER(Material.POLISHED_BLACKSTONE_BUTTON, "<green>Teleporter", 1, null, "Setze deinen eigenen Teleport"),

    MAP_BLOCK(Material.STONE, "<green>Map label Set", 1, null, null), EXP_MEET(Material.COOKED_BEEF, "Level Fleisch v2", 1, null, "Speicher deine Level und Iss es bei bedarf!");


    private final String label;
    private final Enchantment enchantment;
    private final List<String> lore;
    private final Material material;

    private final int modelID;

    LootItems(Material material, String label, int id, @Nullable Enchantment enchantment, @Nullable String... lore) {
        this.label = label;
        this.material = material;
        this.modelID = id;
        this.enchantment = enchantment;
        if (lore != null) {
            this.lore = List.of(lore);
        } else this.lore = null;
    }

    public Component getLabel() {
        return MiniMessage.miniMessage().deserialize(this.label);
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public List<Component> getLore() {

        if (this.lore == null) {
            return null;
        }

        return this.lore.stream().map(val -> MiniMessage.miniMessage().deserialize(val)).collect(Collectors.toList());
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getModelID() {
        return this.modelID;
    }

}
