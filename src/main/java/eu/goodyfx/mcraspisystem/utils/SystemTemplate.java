package eu.goodyfx.mcraspisystem.utils;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class SystemTemplate {

    public boolean enabled;
    public String name;
    public SystemStartUp startUp;
    public List<CommandExecutor> commandsContainer = new ArrayList<>();
    public List<Listener> eventsContainer = new ArrayList<>();
    public List<BukkitRunnable> tasksContainer = new ArrayList<>();

    protected SystemTemplate(SystemStartUp startUp) {
        this.startUp = startUp;
    }

    public abstract void setActive();

    public abstract void events();

    public abstract void commands();

    public abstract void tasks();

}
