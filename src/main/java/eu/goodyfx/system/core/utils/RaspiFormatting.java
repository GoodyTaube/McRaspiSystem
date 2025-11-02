package eu.goodyfx.system.core.utils;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum RaspiFormatting {

    BLACK("<black>", true, "&0"),
    WHITE("<white>", false, "&f"),
    BLUE("<blue>", false, "&9"),
    RED("<red>", false, "&c"),
    YELLOW("<yellow>", false, "&e"),
    GREEN("<green>", false, "&a"),
    PURPLE("<purple>", false, "&5"),
    GRAY("<gray>", false, "&7"),
    GOLD("<gold>", false, "&6"),
    AQUA("<aqua>", false, "&b"),
    MINE_CON_GOLD("<gold>", false, "&g"),
    LILA_BLASS_BLUE("<#A593E0>", false, "&x"),
    DARK_RED("<dark_red>", false, "&4"),
    DARK_BLUE("<dark_blue>", false, "&1"),
    DARK_GREEN("<dark_green>", false, "&2"),
    DARK_AQUA("<dark_aqua>", false, "&3"),
    DARK_GRAY("<dark_gray>", false, "&8"),
    DARK_PURPLE("<dark_purple>", false, "&5"),
    BOLD("<bold>", true, "&l", "<b>"),
    ITALIC("<italic>", true, "&o", "<i>", "<em>"),
    UNDERLINED("<underlined>", true, "&n", "<u>"),
    OBFUSCATED("<obfuscated>", true, "&k", "<obf>"),
    NEW_LINE("<br>", true, null, "<newline>"),
    RESET("<reset>", true, "&r"),
    STRIKETHROUGH("<strikethrough>", true, "&m", "<st>"),
    RAINBOW("<rainbow>", true, "&z"),
    RANDOM("<random>", false, "&t"),
    HEX("<%s>", false, null);

    private final String value;
    private final boolean blocked_chat;
    private final String legacyValue;
    private final Set<String> aliases;

    /**
     * Raspi Formatting Codes
     *
     * @param value        The Color Paper Value
     * @param blocked_chat Chat Usage Allow
     * @param legacyValue  The actual Minecraft Formatting Value
     */
    RaspiFormatting(String value, boolean blocked_chat, @Nullable String legacyValue, String... aliases) {
        this.value = value;
        this.blocked_chat = blocked_chat;
        this.legacyValue = legacyValue;
        this.aliases = new HashSet<>();
        this.aliases.add(value);
        if (aliases != null) {
            this.aliases.addAll(Arrays.asList(aliases));
        }
    }

    public static Set<RaspiFormatting> chatAllowed() {
        return Arrays.stream(RaspiFormatting.values()).filter(value -> !value.isBlocked_chat()).collect(Collectors.toSet());
    }

    public static String setUpHex(String hexVal) {
        return String.format(HEX.getValue(), hexVal);
    }

    public static String formattingChatMessage(String legacyChatMessage) {

        String backup = legacyChatMessage;
        for (RaspiFormatting blocked : RaspiFormatting.values()) {
            if (blocked.isBlocked_chat()) {
                if (blocked.getLegacyValue() != null) {
                    legacyChatMessage = legacyChatMessage.replace(blocked.getLegacyValue(), "");
                    legacyChatMessage = legacyChatMessage.replace(blocked.getValue(), "");
                }
                for (String alias : blocked.getAliases()) {
                    legacyChatMessage = legacyChatMessage.replace(alias, "");
                }
            }

        }
        for (RaspiFormatting formatting : RaspiFormatting.chatAllowed()) {
            if (formatting.getLegacyValue() != null && !formatting.getValue().isEmpty()) {
                legacyChatMessage = legacyChatMessage.replace(formatting.getLegacyValue(), formatting.getValue());
            }
        }
        return legacyChatMessage;
    }
}
