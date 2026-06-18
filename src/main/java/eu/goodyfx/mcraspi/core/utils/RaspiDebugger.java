package eu.goodyfx.mcraspi.core.utils;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;
import java.util.zip.GZIPOutputStream;

public class RaspiDebugger extends Logger {

    private final String pluginName;
    private File logFolder;

    private Handler latestHandler;
    private Handler debugHandler;
    private Handler errorHandler;

    private AsyncLogWorker asyncWorker;

    public RaspiDebugger(@NotNull Plugin context) {
        super(context.getClass().getSimpleName(), null);

        String prefix = context.getDescription().getPrefix();
        pluginName = prefix != null
                ? "[" + prefix + "] "
                : "[" + context.getDescription().getName() + "] ";

        setParent(context.getServer().getLogger());
        setLevel(Level.ALL);

        setupLogFolder(context);
        rotateLatestLog();
        setupAsyncLogging();
    }

    // =========================
    // 📂 SETUP
    // =========================

    private void setupLogFolder(Plugin plugin) {
        logFolder = new File(plugin.getDataFolder(), "raspi-logs");
        if (!logFolder.exists()) logFolder.mkdirs();
    }

    private void setupAsyncLogging() {
        try {
            // latest.log (reset bei Start)
            latestHandler = new FileHandler(
                    new File(logFolder, "latest.log").getPath(), false
            );
            latestHandler.setFormatter(new SimpleFormatter());
            latestHandler.setLevel(Level.ALL);

            // debug.log (append)
            debugHandler = new FileHandler(
                    new File(logFolder, "debug.log").getPath(), true
            );
            debugHandler.setFormatter(new SimpleFormatter());
            debugHandler.setFilter(r -> r.getLevel() == Level.FINE);

            // error.log (append)
            errorHandler = new FileHandler(
                    new File(logFolder, "error.log").getPath(), true
            );
            errorHandler.setFormatter(new SimpleFormatter());
            errorHandler.setFilter(r -> r.getLevel().intValue() >= Level.SEVERE.intValue());

            asyncWorker = new AsyncLogWorker(latestHandler, debugHandler, errorHandler);
            asyncWorker.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rotateLatestLog() {
        File logFile = new File(logFolder, "latest.log");
        if (!logFile.exists()) return;

        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            File gzFile = new File(logFolder, timestamp + ".log.gz");

            try (
                    FileInputStream fis = new FileInputStream(logFile);
                    FileOutputStream fos = new FileOutputStream(gzFile);
                    GZIPOutputStream gzos = new GZIPOutputStream(fos)
            ) {
                fis.transferTo(gzos);
            }

            Files.delete(logFile.toPath());

            cleanupOldLogs();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanupOldLogs() {
        File[] files = logFolder.listFiles((dir, name) -> name.endsWith(".gz"));
        if (files == null) return;

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        int maxFiles = 10;
        for (int i = maxFiles; i < files.length; i++) {
            files[i].delete();
        }
    }

    @Override
    public void log(@NotNull LogRecord record) {

        record.setMessage(pluginName + record.getMessage());

        // Paper Console
        super.log(record);

        if (record.getLevel() != Level.FINE) {
            super.log(record); // → nur INFO, WARNING, SEVERE
        }

        // Async File Logging
        if (asyncWorker != null) {
            asyncWorker.log(record);
        }
    }

    public void debug(String msg) {
        log(Level.FINE, msg);
    }

    /**
     * Write to file with prefix
     * The prefix don't need []
     * @param msg The Debug Message
     * @param prefix The raw Prefix without []
     */
    public void debug(String msg, String prefix) {
        log(Level.FINE, String.format("[%s] %s", prefix, msg));
    }

    @Override
    public void info(String msg) {
        log(Level.INFO, msg);
    }

    public void warn(String msg) {
        log(Level.WARNING, msg);
    }

    public void error(String msg, Throwable t) {
        log(Level.SEVERE, msg, t);
    }

    public void shutdown() {
        if (asyncWorker != null) {
            asyncWorker.shutdown();
        }
    }

    private static class AsyncLogWorker extends Thread {

        private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>(10000);
        private final Handler[] handlers;
        private volatile boolean running = true;

        public AsyncLogWorker(Handler... handlers) {
            this.handlers = handlers;
            setName("Raspi-Logger-Thread");
            setDaemon(true);
        }

        public void log(LogRecord record) {
            queue.offer(record);
        }

        @Override
        public void run() {
            while (running || !queue.isEmpty()) {
                try {
                    LogRecord record = queue.take();

                    for (Handler handler : handlers) {
                        if (handler.isLoggable(record)) {
                            handler.publish(record);
                        }
                    }

                } catch (InterruptedException ignored) {
                }
            }
        }

        public void shutdown() {
            running = false;
            interrupt();
        }
    }

    public String formatLocation(Location location) {
        return String.format("@[world=%s X=%s Y=%s Z=%s YAW=%s PITCH=%s]",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

}