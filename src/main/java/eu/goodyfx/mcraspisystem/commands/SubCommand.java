package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

/**
 * Abstract class representing a sub-command that can be executed by a player.
 *
 * Subclasses should implement methods to define the command's label,
 * description, syntax, and execution logic.
 */
@SuppressWarnings("unused")
public abstract class SubCommand {

    public abstract String getLabel();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract boolean commandPerform(RaspiPlayer player, String[] args);

}
