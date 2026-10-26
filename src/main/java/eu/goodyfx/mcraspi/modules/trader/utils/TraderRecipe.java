package eu.goodyfx.mcraspi.modules.trader.utils;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class TraderRecipe {

    private final Map<String, Object> result;
    private final Map<String, Object> currency_slot1;
    private Map<String, Object> currency_slot2 = null;
    private final int id;

    public TraderRecipe(@NotNull Integer id, @NotNull ItemStack result, @NotNull ItemStack slot1, @Nullable ItemStack slot2) {
        this.result = result.serialize();
        if (slot2 != null) {
            this.currency_slot2 = slot2.serialize();
        }
        this.currency_slot1 = slot1.serialize();
        this.id = id;
    }

    public ItemStack getResult(){
        return result != null? ItemStack.deserialize(result) : null;
    }

    public ItemStack getCurrency_slot1(){
        return currency_slot1 != null ? ItemStack.deserialize(currency_slot1) : null;
    }

    public ItemStack getCurrency_slot2(){
        return currency_slot2 != null ? ItemStack.deserialize(currency_slot2) : null;
    }


}
