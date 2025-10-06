package eu.goodyfx.system.core.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import eu.goodyfx.system.McRaspiSystem;
import io.papermc.paper.ban.BanListType;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

@Getter
@Setter
public class RaspiFreischalten {

    private int age = 0;
    private long timestamp_start = 0L;
    private boolean read_rules = false;
    private String allower_name = "SYSTEM";
    private RaspiPlayer player;
    private boolean sessionRun = false;

    public RaspiFreischalten(RaspiPlayer player) {
        this.player = player;
        this.timestamp_start = System.currentTimeMillis();
        this.sessionRun = true;
    }

    public boolean isOk() {
        return age >= 18 && read_rules;
    }

    public void handleChat(RaspiPlayer player, Component message) {
        String serialized = PlainTextComponentSerializer.plainText().serialize(message);
        if (age == 0) {
            player.sendMessage("<gray><italic>Bitte erzähl uns wie alt du bist.", true);
            try {
                setAge(Integer.parseInt(serialized));
                player.sendMessage("<gray>Hast du die Schilder und das Buch gelesen?<br><gray><italic>Ja | Nein | J | N");
            } catch (NumberFormatException e) {
                player.sendMessage("<red>Du musst schon eine Zahl benutzen <yellow>zB 16", true);
                player.sendMessage("<gray>Also, wie Alt bist du noch gleich?", true);

            }
        }
        if (!read_rules) {
            if (serialized.equalsIgnoreCase("Ja") || serialized.equalsIgnoreCase("J")) {
                setRead_rules(true);
                player.sendMessage("<green>Danke! <gray><italic>Verifizierung wird bearbeitet.", true);
                if (!isOk()) {
                    disallow();
                } else {
                    freischalten();
                    setSessionRun(false);
                }
            }
        }

    }


    public void freischalten() {
        if (age >= 18 && read_rules) {
            JavaPlugin.getPlugin(McRaspiSystem.class).getHookManager().getDiscordIntegration().send(String.format("%s wurde vom System Freigeschaltet! Alter: %02d", player.getPlayer().getName(), age));

        } else {
            disallow();
            JavaPlugin.getPlugin(McRaspiSystem.class).getHookManager().getDiscordIntegration().send(String.format("%s wurde vom System Abgelehnt. Alter: %02d Regeln Gelesen %s", player.getPlayer().getName(), age, read_rules));

        }
    }

    public void disallow() {
        BanList<PlayerProfile> banList = Bukkit.getServer().getBanList(BanListType.PROFILE);
        banList.addBan(player.getPlayer().getPlayerProfile(), "Freischaltung Fehlgeschlagen!", (Date) null, "System");
    }
}
