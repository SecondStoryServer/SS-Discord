package me.syari.ss.discord.internal.requests;

public class CallbackContext implements AutoCloseable {
    private static final ThreadLocal<Boolean> callback = ThreadLocal.withInitial(() -> false);
    private static final CallbackContext instance = new CallbackContext();

    public static CallbackContext getInstance() {
        startCallback();
        return instance;
    }

    static boolean isCallbackContext() {
        return callback.get();
    }

    private static void startCallback() {
        callback.set(true);
    }

    @Override
    public void close() {
        callback.set(false);
    }
}
