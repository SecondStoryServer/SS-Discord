package me.syari.ss.discord.internal.utils.compress;

import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.slf4j.Logger;

import org.jetbrains.annotations.Nullable;
import java.util.zip.DataFormatException;

public interface Decompressor {
    Logger LOG = JDALogger.getLog(Decompressor.class);

    Compression getType();

    void reset();

    void shutdown();

    @Nullable
    String decompress(byte[] data) throws DataFormatException;
}
