package eu.goodyfx.system.core.database;

import eu.goodyfx.system.core.utils.PlayerNameController;

import java.util.UUID;

public interface RaspiUserContext {

    UUID getUUID();

    RaspiAccount account();

    default RaspiUser userData() {
        return account().getRaspiUser();
    }

    default RaspiManagement userManagement() {
        return account().getRaspiManagement();
    }

    default RaspiUsernames usernames() {
        return account().getRaspiUsernames();
    }

    default UserSettings settings() {
        return account().getUserSettings();
    }

    boolean isOnline();

}
