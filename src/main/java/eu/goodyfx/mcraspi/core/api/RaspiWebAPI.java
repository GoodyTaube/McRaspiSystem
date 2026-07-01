package eu.goodyfx.mcraspi.core.api;

import com.google.gson.Gson;
import eu.goodyfx.mcraspi.McRaspiSystem;
import io.javalin.Javalin;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RaspiWebAPI {

    private final McRaspiSystem plugin;
    private final RaspiAccountService accountService;
    private final Gson gson = new Gson();
    private Javalin app;


    private RaspiWebAPI(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.accountService = plugin.getRaspiAccountService();
    }

    private static final String API_LOCATION = "/api/player/{name}";


    public void start(int port) {
        app.get(API_LOCATION, context -> {
            String name = context.pathParam("name");

            context.future(() -> CompletableFuture.supplyAsync(() -> {
                UUID uuid = Bukkit.getPlayerUniqueId(name);
                if (uuid == null) {
                    context.status(404).result("{\"error\": \"Spieler existiert nicht.\"}");
                    return null;
                }
                return uuid;
            }, plugin.getAsyncExecutor()).thenCompose(uuid -> {
                if (uuid == null) return CompletableFuture.completedFuture(null);
                boolean isOnline = accountService.isCached(uuid);
                return accountService.getRaspiAccount(uuid, isOnline);
            }).thenAccept(account -> {
                if (account == null) return;
                context.contentType("application/json");
                context.result(gson.toJson(account));
            }));


        });
    }


    public void stop() {
        if (app != null) app.stop();
    }

}
