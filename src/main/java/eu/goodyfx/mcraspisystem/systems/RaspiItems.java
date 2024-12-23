package eu.goodyfx.mcraspisystem.systems;

import eu.goodyfx.mcraspisystem.commands.RaspiItemsCommand;
import eu.goodyfx.mcraspisystem.events.RaspiItemsListener;
import eu.goodyfx.mcraspisystem.utils.SystemStartUp;
import eu.goodyfx.mcraspisystem.utils.SystemTemplate;

public class RaspiItems extends SystemTemplate {


    public RaspiItems(SystemStartUp startUp) {
        super(startUp);
    }

    @Override
    public void setActive() {
        this.enabled = startUp.isEnabled("RaspiItems");
    }

    @Override
    public void events() {
        new RaspiItemsListener(startUp.plugin);
    }

    @Override
    public void commands() {
        new RaspiItemsCommand(startUp.plugin);
    }

    @Override
    public void tasks() {

    }
}
