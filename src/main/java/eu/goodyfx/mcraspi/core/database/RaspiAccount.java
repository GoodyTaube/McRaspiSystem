package eu.goodyfx.mcraspi.core.database;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RaspiAccount {

    private final UUID uuid;
    private final RaspiUser raspiUser;
    private final RaspiSettings raspiSettings;
    private final RaspiManagement raspiManagement;
    private final RaspiUsernames raspiUsernames;

    public RaspiAccount(UUID uuid, RaspiUser raspiUser, RaspiSettings raspiSettings, RaspiUsernames usernames, RaspiManagement raspiManagement) {
        this.uuid = uuid;
        this.raspiUser = raspiUser;
        this.raspiSettings = raspiSettings;
        this.raspiManagement = raspiManagement;
        this.raspiUsernames = usernames;
    }

    public void save() {
        raspiUser.updateUserData();
        raspiManagement.updateUserData();
        raspiSettings.update();
        raspiUsernames.update();
    }


}
