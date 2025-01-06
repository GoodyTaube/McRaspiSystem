package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.managers.ReiseLocationManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class ReiseListSubCommand extends SubCommand {


    @Override
    public String getLabel() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/reise list [range]";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(getIDsByRange(0, 10));
            return true;
        } else if (args.length == 2) {
            try {
                if (args[1].length() <= 2) {

                    if (args[1].equalsIgnoreCase("id")) {
                        sender.sendMessage(ReiseLocationManager.getIDSArray());
                        return true;
                    }

                    int number = Integer.parseInt(args[1]);
                    if (number != 0) {
                        number = number - 1;
                    }
                    sender.sendMessage(getIDsByRange(0, number));
                    return true;

                } else if (args[1].contains("-")) {
                    String[] seperate = args[1].split("-");
                    if (seperate.length == 2) {
                        int min = Integer.parseInt(seperate[0]);
                        int max = Integer.parseInt(seperate[1]);
                        if (min == 0) {
                            min++;
                        }
                        sender.sendMessage(getIDsByRange(min, max));
                        return true;

                    }
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("<red>Wert <range> muss eine Nummer sein!");
                return true;
            }

        }
        return false;
    }

    public String getIDsByRange(int min, int max) {
        int times = 0;
        int val = 0;
        if (min == 0) {
            min++;
        }
        StringBuilder builder = new StringBuilder("<gold>Liste von ID's:<br>");
        Map<Integer, String> ids = ReiseLocationManager.getPOS();

        if (max > ids.size()) {
            max = ids.size();
        }

        for (int i = min; i <= max; i++) {
            builder.append(ids.get(i)).append("<br>");
            times++;
            if (times == 10) {
                builder.setLength(builder.length() - 4);
                builder.append(getStringNext(i, i + 10));
                break;
            }


        }

        builder.setLength(builder.length() - 4);

        return builder.toString();
    }

    public String getStringNext(int from, int to) {
        from++;
        return String.format("<br><gold>>>>>>>>> <click:run_command:'/reise list %s-%s'>%s - %s <<<<<<<<<br>", from, to, from, to);
    }

}
