package eu.goodyfx.system.core.database;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class RaspiAccount {

    private final UUID uuid;
    private final RaspiUser raspiUser;
    private final UserSettings userSettings;
    private final RaspiManagement raspiManagement;
    private final RaspiUsernames raspiUsernames;

    public RaspiAccount(UUID uuid, RaspiUser raspiUser, UserSettings userSettings, RaspiUsernames usernames, RaspiManagement raspiManagement) {
        this.uuid = uuid;
        this.raspiUser = raspiUser;
        this.userSettings = userSettings;
        this.raspiManagement = raspiManagement;
        this.raspiUsernames = usernames;
    }

    public void save() {
        raspiUser.updateUserData();
        raspiManagement.updateUserData();
        userSettings.update();
        raspiUsernames.update();
    }


}
