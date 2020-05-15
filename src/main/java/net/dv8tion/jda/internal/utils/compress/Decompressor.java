

package net.dv8tion.jda.internal.utils.compress;

import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.zip.DataFormatException;

public interface Decompressor
{
    Logger LOG = JDALogger.getLog(Decompressor.class);

    Compression getType();

    void reset();

    void shutdown();

    @Nullable // returns null when the decompression isn't done, for example when no Z_SYNC_FLUSH was present
    String decompress(byte[] data) throws DataFormatException;
}
