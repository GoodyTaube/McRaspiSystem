package eu.goodyfx.mcraspisystem.systems;

import eu.goodyfx.mcraspisystem.events.LootChestListeners;
import eu.goodyfx.mcraspisystem.utils.SystemStartUp;
import eu.goodyfx.mcraspisystem.utils.SystemTemplate;

public class LootChest extends SystemTemplate {


    public LootChest(SystemStartUp startUp) {
        super(startUp);
    }

    @Override
    public void setActive() {
        this.enabled = startUp.isEnabled("Systems.LootChest");
    }

    @Override
    public void events() {
        new LootChestListeners(startUp.plugin);
    }

    @Override
    public void commands() {

    }

    @Override
    public void tasks() {

    }

}
