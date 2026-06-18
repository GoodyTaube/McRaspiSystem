package eu.goodyfx.mcraspi.modules.trader.utils;

import eu.goodyfx.mcraspi.core.exceptions.AllReadyExistException;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.managers.TraderDB;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataContainer;

public class TraderBuilder {

    private final String traderUID;
    private Component name;
    private final Location location;
    private final PersistentDataContainer persistentDataContainer;
    private Villager villager;
    private final NamespacedKey key;
    private final TraderDB traderDB;

    public TraderBuilder(String traderUID, Location spawnLocation, TraderSubSystem subSystem) throws AllReadyExistException {
        this.key = subSystem.getTraderKey();
        this.traderDB = subSystem.getTraderDB();
        this.traderUID = traderUID;
        this.location = spawnLocation;

        if(traderDB.traderExist(traderUID)){
            throw new AllReadyExistException("Trader existiert bereits in der DB!");

        }

        this.villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        this.persistentDataContainer = villager.getPersistentDataContainer();
        this.name = MiniMessage.miniMessage().deserialize(String.format("<green>%s", traderUID));
        configureDefaultTrader();
    }


    private void configureDefaultTrader() {
        villager.setAI(false);
        villager.setCustomNameVisible(true);
        villager.setGravity(false);
        villager.setCollidable(false);
        villager.setInvulnerable(true);
        villager.customName(name);
    }


    public Villager build() throws AllReadyExistException {
        traderDB.create(traderUID);
        return villager;
    }


    public TraderBuilder overrideName(String formattedName) {
        this.name = MiniMessage.miniMessage().deserialize(formattedName);
        return this;
    }

    public TraderBuilder setJob(Villager.Profession profession) {
        villager.setProfession(profession);
        return this;
    }

    public TraderBuilder hasAI(boolean ai) {
        villager.setAI(ai);
        return this;
    }

    public TraderBuilder allowCollide(boolean collide) {
        villager.setCollidable(collide);
        return this;
    }

}
