package opm.luftwaffe.api.util.discord;

import opm.luftwaffe.Luftwaffe;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DiscordManager {
    private static DiscordManager INSTANCE;

    private final long applicationId;
    private final RichPresence richPresence;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> restartTask;

    private final long initialRestartDelay = 10; // Base delay in seconds
    private final int maxBackoffMultiplier = 5; // Maximum multiplier for backoff

    private boolean autoRestart = true;
    private int maxRestartAttempts = 5;
    private long restartDelay = initialRestartDelay; // seconds

    private int currentRestartAttempts = 0;
    private boolean reachedMaxAttempts = false;
    private boolean wasReconnecting = false;

    private boolean enabled = false;

    private DiscordManager(long applicationId) {
        this.applicationId = applicationId;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.richPresence = new RichPresence();

        // Set custom error handler
        DiscordIPC.setOnError(this::handleError);
    }

    public static DiscordManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DiscordManager(1395805402767032350L); //application id here doick
        }
        return INSTANCE;
    }

    // Getters and setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setAutoRestart(boolean autoRestart) {
        this.autoRestart = autoRestart;
    }

    public void setMaxRestartAttempts(int maxRestartAttempts) {
        this.maxRestartAttempts = maxRestartAttempts;
    }

    public long getRestartDelay() {
        return restartDelay;
    }

    public void setRestartDelay(long restartDelay) {
        this.restartDelay = restartDelay;
    }

    public boolean start() {
        return start(true);
    }

    public boolean start(boolean isInitialStart) {
        if (enabled) return true;

        if (isInitialStart) {
            resetRetryState();
        }

        if (reachedMaxAttempts) {
            Luftwaffe.LOGGER.warn("Not starting Discord RPC - maximum restart attempts already reached");
            return false;
        }

        boolean success = DiscordIPC.start(applicationId, this::onReady);

        if (success) {
            enabled = true;
            updatePresence();
            Luftwaffe.LOGGER.info("Discord RPC started successfully");
            wasReconnecting = currentRestartAttempts > 0;
        } else {
            Luftwaffe.LOGGER.error("Failed to start Discord RPC");
            if (autoRestart) scheduleRestart();
        }

        return success;
    }

    public void stop() {
        enabled = false;
        cancelRestartTask();
        DiscordIPC.stop();
        Luftwaffe.LOGGER.info("Discord RPC stopped");
    }

    public void setDetails(String details) {
        richPresence.setDetails(details);
        updatePresence();
    }

    public void setState(String state) {
        richPresence.setState(state);
        updatePresence();
    }

    public void setLargeImage(String key, String text) {
        richPresence.setLargeImage(key, text);
        updatePresence();
    }

    public void setSmallImage(String key, String text) {
        richPresence.setSmallImage(key, text);
        updatePresence();
    }

    public void setStartTimestamp(long timestamp) {
        richPresence.setStart(timestamp);
        updatePresence();
    }

    public void setStartTimestampToNow() {
        setStartTimestamp(Instant.now().getEpochSecond());
    }

    public void setEndTimestamp(long timestamp) {
        richPresence.setEnd(timestamp);
        updatePresence();
    }

    public void updatePresence() {
        if (enabled && DiscordIPC.isConnected()) {
            DiscordIPC.setActivity(richPresence);
        }
    }

    private void handleError(int code, String message) {
        Luftwaffe.LOGGER.error("Discord RPC error {}: {}", code, message);
        enabled = false;

        if (reachedMaxAttempts) {
            Luftwaffe.LOGGER.warn("Maximum Discord RPC restart attempts already reached. Not retrying.");
            return;
        }

        if (autoRestart && (maxRestartAttempts < 0 || currentRestartAttempts < maxRestartAttempts)) {
            scheduleRestart();
        } else if (currentRestartAttempts >= maxRestartAttempts) {
            reachedMaxAttempts = true;
            Luftwaffe.LOGGER.warn("Maximum Discord RPC restart attempts reached ({})", maxRestartAttempts);
        }
    }

    private void scheduleRestart() {
        cancelRestartTask();

        currentRestartAttempts++;

        // Calculate exponential backoff
        int backoffFactor = Math.min(currentRestartAttempts, maxBackoffMultiplier);
        restartDelay = initialRestartDelay * backoffFactor;

        Luftwaffe.LOGGER.info("Scheduling Discord RPC restart in {} seconds (attempt {}/{})",
                restartDelay, currentRestartAttempts, maxRestartAttempts < 0 ? "unlimited" : maxRestartAttempts);

        restartTask = executor.schedule(() -> {
            Luftwaffe.LOGGER.info("Attempting to restart Discord RPC");
            DiscordIPC.stop();
            start(false);
        }, restartDelay, TimeUnit.SECONDS);
    }

    private void cancelRestartTask() {
        if (restartTask != null && !restartTask.isDone()) {
            restartTask.cancel(false);
            restartTask = null;
        }
    }

    private void onReady() {
        Luftwaffe.LOGGER.info("Discord RPC connected as {}", DiscordIPC.getUser().username);

        // Only reset retry state if this was a successful reconnection after failures
        if (wasReconnecting) {
            Luftwaffe.LOGGER.info("Discord RPC reconnected successfully after {} attempts", currentRestartAttempts);
            resetRetryState();
        }
    }

    public void resetRetryState() {
        currentRestartAttempts = 0;
        reachedMaxAttempts = false;
        restartDelay = initialRestartDelay;
        wasReconnecting = false;
    }

    public void shutdown() {
        stop();
        executor.shutdown();
    }
}
