package eu.goodyfx.mcraspi.core.database;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RaspiAccount {

    private final UUID uuid;
    private final RaspiUser raspiUser; //Prefix, color, votes, etc
    private final RaspiSettings raspiSettings; //User Settings like AutoAFK and ChatFeatures
    private final RaspiManagement raspiManagement; //User Moderation like Ban, expire and mute
    private final RaspiUsernames raspiUsernames; //Known Usernames of user (because Mojang API is offline forever)

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
