
package net.dv8tion.jda.api;

/**
 * Contains information to this specific build of JDA.
 */
public class JDAInfo
{
    public static final int DISCORD_REST_VERSION = 6;
    public static final int AUDIO_GATEWAY_VERSION = 4;
    public static final String GITHUB = "https://github.com/DV8FromTheWorld/JDA";
    public static final String VERSION_MAJOR = "@versionMajor@";
    public static final String VERSION_MINOR = "@versionMinor@";
    public static final String VERSION_REVISION = "@versionRevision@";
    public static final String VERSION_BUILD = "@versionBuild@";
    public static final String VERSION = VERSION_MAJOR.startsWith("@") ? "dev" : String.format("%s.%s.%s_%s", VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION, VERSION_BUILD);
}
