package eu.goodyfx.mcraspisystem.utils;

public enum OldColors {

    BLACK("&0", "<black>"),
    DARK_BLUE("&1", "<dark_blue>"),
    DARK_GREEN("&2", "<dark_green>"),
    DARK_AQUA("&3", "<dark_aqua>"),
    DARK_RED("&4", "<dark_red>"),
    DARK_PURPLE("&5", "<dark_purple>"),
    GOLD("&6", "<gold>"),
    GRAY("&7", "<gray>"),
    DARK_GRAY("&8", "<dark_gray>"),
    BLUE("&9", "<blue>"),
    GREEN("&a", "<green>"),
    AQUA("&b", "<aqua>"),
    RED("&c", "<red>"),
    LIGHT_PURPLE("&d", "<light_purple>"),
    YELLOW("&e", "<yellow>"),
    WHITE("&f", "<white>"),
    MINE_COIN_GOLD("&g", "<gold>"),
    LILA_BLASS_BLUE("&z", "<#A593E0>"),
    BOLD("&l", "<bold>"),
    RESET("&r", "<reset>"),
    UNDERLINED("&n", "<underlined>"),
    STRIKETHROUGH("&m", "<strikethrough>"),
    RAINBOW("&y", "<rainbow>"),
    ITALIC("&o", "<italic>");


    private final String code;
    private final String colorCode;

    OldColors(String code, String formatted) {
        this.code = code;
        this.colorCode = formatted;
    }

    public String getMinniString() {
        return this.colorCode;
    }

    public static String convert(String message) {
        //INDEXOUTOFMJ CODE OPT
        if (!message.contains("&")) {
            return message;
        }
        message = message.replace("&k", "");
        for (OldColors oldColor : OldColors.values()) {
            message = message.replaceAll(oldColor.code, oldColor.colorCode);
        }
        return message;
    }

    public static String getRawString(String toConvert) {
        toConvert = toConvert.replace("@", "");
        toConvert = toConvert.replace("&k", "");
        toConvert = toConvert.replace("&u", "");
        for (OldColors colors : OldColors.values()) {
            toConvert = toConvert.replaceAll(colors.code, "");
        }
        for (OldColors colors : OldColors.values()) {
            toConvert = toConvert.replaceAll(colors.colorCode, "");
        }
        return toConvert;
    }


}
