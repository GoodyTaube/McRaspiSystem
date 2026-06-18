package eu.goodyfx.mcraspi.core.database;

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

    default RaspiSettings settings() {
        return account().getRaspiSettings();
    }

    boolean isOnline();

}
