package eu.goodyfx.mcraspi.core.utils;

import eu.goodyfx.mcraspi.core.api.Raspi;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.annotation.Experimental;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;
    private Float modelID = null;
    private List<Component> lore;

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material, 1);
        this.meta = stack.getItemMeta();
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
        this.meta = stack.getItemMeta();
        this.lore = new ArrayList<>();
    }


    public ItemBuilder setAmount(int amount) {
        if (amount > 64) {
            amount = 64;
        }
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder displayName(Component val) {
        meta.displayName(val);
        return this;
    }

    public ItemBuilder displayName(String val) {
        meta.displayName(MiniMessage.miniMessage().deserialize(val));
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder addLore(Component val) {
        lore.add(val);
        return this;
    }

    public ItemBuilder addLore(String val) {
        lore.add(MiniMessage.miniMessage().deserialize(val));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLimit) {
        if (ignoreLimit) {
            stack.addUnsafeEnchantment(enchantment, level);
        } else {
            stack.addEnchantment(enchantment, level);
        }
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantmentIntegerMap) {
        stack.addEnchantments(enchantmentIntegerMap);
        return this;
    }


    /**
     * Set the custom Model ID
     *
     * @param modelID the id
     * @return This Item Builder
     * @deprecated Please use {@link #setIdentifier(int)}
     */
    @Deprecated()
    public ItemBuilder setModelID(float modelID) {
        this.modelID = modelID;
        return this;
    }


    public ItemStack build() {
        meta.lore(lore);
        stack.setItemMeta(meta);
        if (modelID != null) {
            stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(modelID).build());
        }

        return stack;
    }

    @Experimental
    public ItemBuilder setIdentifier(int id) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey itemKey = Raspi.pluginKeys().getITEM_KEY();
        container.set(itemKey, PersistentDataType.INTEGER, id);
        return this;
    }


}
