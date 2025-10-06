package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.managers.RaspiModuleManager;
import org.bukkit.entity.Player;

import java.util.Objects;

public class RaspiMessages {

    private final McRaspiSystem plugin;

    public RaspiMessages(RaspiModuleManager moduleManager) {
        this.plugin = moduleManager.getPlugin();
    }


    public String getPrefix() {
        return get("prefix");
    }

    public String getAFK() {
        return get("afk");
    }

    public String getUsage(String command) {
        String raw = get("usage");
        raw = raw.replace("{command}", command);
        raw = prefix(raw);
        return raw;
    }

    public String getJoin(Player player) {
        String raw = get("join");
        raw = prefix(raw);
        raw = raw.replace("{player}", Raspi.players().get(player).getDisplayName());
        return raw;

    }

    public String getLeave(String player) {
        String raw = get("leave");
        raw = prefix(raw);
        raw = raw.replace("{player}", player);
        return raw;
    }

    public String[] getDisallowNewbie() {
        String raw = get("disallowNewbie");
        return raw.split("%");
    }

    public String getOnlyPlayer() {
        String raw = get("onlyIngame");
        raw = prefix(raw);
        return raw;
    }

    public String getKickMessage() {
        return get("kickMessage");
    }

    public String created() {
        String raw = get("erstellt_spawn");
        raw = prefix(raw);
        return raw;
    }

    public String createdWaiting() {
        String raw = get("erstellt_warteraum");
        raw = prefix(raw);
        return raw;
    }

    public String noSpawnPoint(int value) {
        if (value == 1) {
            String raw = get("noSpawnPoint");
            raw = prefix(raw);
            return raw;
        } else if (value == 2) {
            String raw = get("noWaitingPoint");
            raw = prefix(raw);
            return raw;
        }
        return "";
    }

    public String blocking() {
        String raw = get("blockieren.nachricht");
        raw = prefix(raw);
        return raw;
    }

    public boolean debugMode() {
        return plugin.getModule().getMessageManager().getBoolean("debug");
    }

    public boolean blockChat() {
        return plugin.getModule().getMessageManager().getBoolean("messages.blockieren.chat");
    }

    public boolean blockTeleport() {
        return plugin.getModule().getMessageManager().getBoolean("messages.blockieren.teleport");
    }

    public String refreshed() {
        String raw = get("aktualisiert");
        raw = prefix(raw);
        return raw;
    }

    public String broadcastPlayerInsert(String name) {
        String raw = get("broadcast_message");
        raw = prefix(raw);
        raw = raw.replace("{player}", name);
        return raw;
    }

    public String playerNotOnline(String name) {
        String raw = get("playerNotOnline");
        raw = prefix(raw);
        raw = raw.replace("{player}", name);
        return raw;
    }

    public String refreshedWaiting() {
        String raw = get("aktualisiert_warteraum");
        raw = prefix(raw);
        return raw;
    }


    public String getPosition(int positionId) {
        String raw = get("position");
        raw = prefix(raw);
        raw = raw.replace("{id}", String.valueOf(positionId));
        return raw;
    }

    public String getNoPermission() {
        String raw = get("noPermission");
        raw = prefix(raw);
        return raw;
    }

    public String endWaiting() {
        String raw = get("wartenEnde");
        raw = prefix(raw);
        return raw;
    }

    private String prefix(String raw) {
        return raw.replace("{prefix}", getPrefix());
    }


    public String get(String subPath) {
        return Objects.requireNonNull(OldColors.convert(plugin.getModule().getMessageManager().getString("messages." + subPath)));
    }
}
