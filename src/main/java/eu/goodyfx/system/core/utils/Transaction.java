package eu.goodyfx.system.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaspiCoinsUtil {

    public static Map<Map<UUID, UUID>, Long> transactions = new HashMap<>();

    public static void processTransactions() {
        for (Map<UUID, UUID> entry : transactions.keySet()) {
            Long value = transactions.get(entry);

        }
    }


}
