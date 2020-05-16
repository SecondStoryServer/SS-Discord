package me.syari.ss.discord.internal.utils.compress;

import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.zip.DataFormatException;

public interface Decompressor {
    Logger LOG = JDALogger.getLog(Decompressor.class);

    Compression getType();

    void reset();

    void shutdown();

    @Nullable
        // returns null when the decompression isn't done, for example when no Z_SYNC_FLUSH was present
    String decompress(byte[] data) throws DataFormatException;
}
