package eu.goodyfx.system.core.tasks;

import eu.goodyfx.system.McRaspiSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TablistAnimator extends BukkitRunnable {


    private final String tTA = "McRaspi.com | Vanilla Minecraft Server";

    private int index;

    public TablistAnimator() {
        runTaskTimerAsynchronously(JavaPlugin.getPlugin(McRaspiSystem.class), 0, 2L);
    }

    @Override
    public void run() {
        Component animated = getAnimatedTitle();
        Component header = Component.empty()
                .append(Component.newline())
                .append(animated)
                .append(Component.newline())
                .append(Component.text("Wartende Spieler: 0 / ~").color(NamedTextColor.GRAY))
                .append(Component.newline());
        sendPlayer(header);
        index++;
        if (index >= tTA.length()) {
            index = 0;
        }
    }

    private Component getAnimatedTitle() {
        java.awt.Color gray = new java.awt.Color(170, 170, 170);  // Hellgrau
        java.awt.Color aqua = new java.awt.Color(0, 255, 255);    // Aqua

        Component full = Component.empty();

        int length = tTA.length();

        // Drehung → index = aktuelle Position des "Licht-Spots"
        int center = index % length;

        for (int i = 0; i < length; i++) {
            char cha = tTA.charAt(i);

            // Abstand im Kreis (wrap-around berücksichtigen!)
            int dist = Math.abs(i - center);
            dist = Math.min(dist, length - dist); // sorgt für kreisförmige Bewegung

            // Stärke: Je näher am Spot, desto aqua
            float ratio = Math.max(0f, 1f - (dist / 3f)); // "3" = Breite des Glüheffekts

            int r = (int) (gray.getRed()   + ratio * (aqua.getRed()   - gray.getRed()));
            int g = (int) (gray.getGreen() + ratio * (aqua.getGreen() - gray.getGreen()));
            int b = (int) (gray.getBlue()  + ratio * (aqua.getBlue()  - gray.getBlue()));

            full = full.append(
                    Component.text(String.valueOf(cha))
                            .color(net.kyori.adventure.text.format.TextColor.color(r, g, b))
            );
        }

        return full;
    }


    public void sendPlayer(Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPlayerListHeader(message);
        }
    }

}
