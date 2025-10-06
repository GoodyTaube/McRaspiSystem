package eu.goodyfx.system.core.database;

import lombok.Getter;

@Getter
public enum DatabaseTables {

    USER_DATA("user_data"),
    USER_MODERATION("user_moderation"),
    USERNAME_HISTORY("username_history"),
    USER_SETTINGS("user_settings");

    private final String tableName;

    DatabaseTables(String tableName) {
        this.tableName = tableName;
    }

}
