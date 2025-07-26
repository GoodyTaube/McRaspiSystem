package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.core.utils.RaspiTimes;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class PlayerBanManager {

    private final UserManager userManager;

    public PlayerBanManager(RaspiModuleManager moduleManager) {
        this.userManager = moduleManager.getUserManager();
    }

    /**
     * TempBan Target and write into DB
     *
     * @param target    the ban target
     * @param reason    the ban reason
     * @param time      the time in minutes
     * @param performer the ban command Performer
     */
    public void tempBanPlayerStandard(OfflinePlayer target, StringBuilder reason, long time, @Nullable String performer) {
        userManager.reloadFile();
        userManager.set(target, "ban.tempBaned", true);
        userManager.set(target, "ban.expire", time + System.currentTimeMillis());
        userManager.set(target, "ban.reason", reason.toString());

        if (performer != null) {
            userManager.set(target, "ban.performer", performer);
        }
    }


    /**
     * TempBan Target and write into DB
     *
     * @param target     the ban target
     * @param reason     the ban reason
     * @param timeFormat the Time format
     * @param performer  the ban command Performer
     * @param multiplier the multiplier of Time
     */
    public void tempBanPlayer(OfflinePlayer target, StringBuilder reason, RaspiTimes.MilliSeconds timeFormat, int multiplier, String performer) {
        userManager.reloadFile();

        userManager.set(target, "ban.tempBaned", true);
        userManager.set(target, "ban.expire", (multiplier * timeFormat.getTime()) + System.currentTimeMillis());
        userManager.set(target, "ban.reason", reason.toString());
        userManager.set(target, "ban.performer", performer);
    }

    /**
     * Del TempBan status from target
     *
     * @param target the target
     */
    public void removeBan(OfflinePlayer target) {
        userManager.reloadFile();

        userManager.remove("ban.tempBaned", target);
        userManager.remove("ban.expire", target);
        userManager.remove("ban.reason", target);
        userManager.remove("ban.performer", target);
        userManager.remove("ban", target);
    }


    public String performer(OfflinePlayer target) {
        return (String) userManager.get("ban.performer", target);
    }

    public Long expire(OfflinePlayer target) {
        return (Long) userManager.get("ban.expire", target);
    }

    public String reason(OfflinePlayer target) {
        userManager.reloadFile();
        String reason = (String) userManager.get("ban.reason", target);
        return reason.replace("@", " ");
    }


    public boolean contains(OfflinePlayer target) {
        return userManager.contains("ban", target);
    }

}
