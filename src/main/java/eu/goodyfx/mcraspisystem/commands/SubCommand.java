package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.goodysutilities.utils.RaspiPlayer;

@SuppressWarnings("unused")
public abstract class SubCommand {

    public abstract String getLabel();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract boolean commandPerform(RaspiPlayer player, String[] args);

}
