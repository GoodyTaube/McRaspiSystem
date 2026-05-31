package eu.goodyfx.system.core.utils;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Settings {

    AUTO_AFK("auto-afk", "auto_afk", "Auto AFK"),
    ADVANCED_CHAT("chat-extras", "opt_chat", "Chat Extras"),
    MESSAGES("servernachrichten", "server_messages", "Server Nachrichten");

    private final String db_column;
    private final String displayName;
    private final String commandKey;

    Settings(String commandKey, String db_column, String displayName) {
        this.db_column = db_column;
        this.displayName = displayName;
        this.commandKey = commandKey;
    }

    /**
     * Get setting from String input
     *
     * @param input String input e.G CommandArgument
     * @return The Setting or NULL if non-existent
     */
    public static Settings fromCommandInput(String input) {
        return Arrays.stream(values()).filter(setting -> setting.getCommandKey().equalsIgnoreCase(input)).findFirst().orElse(null);
    }

}
