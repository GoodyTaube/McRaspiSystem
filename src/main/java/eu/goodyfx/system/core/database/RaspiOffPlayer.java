package eu.goodyfx.system.core.database;

import java.util.UUID;

public class RaspiOffPlayer implements RaspiUserContext {

    private final UUID uuid;
    private final RaspiAccount raspiAccount;

    public RaspiOffPlayer(UUID uuid, RaspiAccount raspiAccount) {
        this.raspiAccount = raspiAccount;
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public RaspiAccount account() {
        return this.raspiAccount;
    }

    @Override
    public boolean isOnline() {
        return false;
    }


}
