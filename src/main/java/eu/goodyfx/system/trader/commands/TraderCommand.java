package eu.goodyfx.system.trader.commands;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class TraderCommand {


    public static final Map<UUID, String> traderEditContainer = new HashMap<>();
    public static final Map<UUID, Integer> traderSAVEContainer = new HashMap<>();
}
