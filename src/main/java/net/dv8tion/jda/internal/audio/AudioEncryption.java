

package net.dv8tion.jda.internal.audio;

import net.dv8tion.jda.api.utils.data.DataArray;

public enum AudioEncryption
{
    // these are ordered by priority, lite > suffix > normal
    // we prefer lite because it uses only 4 bytes for its nonce while the others use 24 bytes
    XSALSA20_POLY1305_LITE,
    XSALSA20_POLY1305_SUFFIX,
    XSALSA20_POLY1305;

    private final String key;

    AudioEncryption()
    {
        this.key = name().toLowerCase();
    }

    public String getKey()
    {
        return key;
    }

    public static AudioEncryption getPreferredMode(DataArray array)
    {
        AudioEncryption encryption = null;
        for (Object o : array)
        {
            try
            {
                String name = String.valueOf(o).toUpperCase();
                AudioEncryption e = valueOf(name);
                if (encryption == null || e.ordinal() < encryption.ordinal())
                    encryption = e;
            }
            catch (IllegalArgumentException ignored) {}
        }
        return encryption;
    }
}
