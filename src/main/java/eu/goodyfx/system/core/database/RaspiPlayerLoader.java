package eu.goodyfx.system.core.database;

import java.util.UUID;

public class RaspiPlayerLoader {

    public record Data(RaspiUser user, RaspiManagement management, UserSettings userSettings) {
    }

    public void save(UUID uuid, RaspiUser raspiUser, RaspiManagement raspiManagement, UserSettings userSettings) {
        raspiUser.updateUserData();
        raspiManagement.updateUserData();
        userSettings.update();

    }

}
