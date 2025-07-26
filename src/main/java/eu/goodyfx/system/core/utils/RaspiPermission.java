package eu.goodyfx.system.core.utils;

import lombok.Getter;

@Getter
public enum RaspiPermission {

    ADMIN(200, "system.admin"),
    DEV(100, "system.dev"),
    TEAM(10, "system.team"),
    MOD(8, "system.moderator"),
    LONG_PLAYER(4, "system.oldie"),
    SPIELER(2, "system.default");

    private final Integer permissionWeight;
    private final String permissionValue;

    RaspiPermission(Integer permissionWeight, String permissionValue) {
        this.permissionWeight = permissionWeight;
        this.permissionValue = permissionValue;
    }

}
