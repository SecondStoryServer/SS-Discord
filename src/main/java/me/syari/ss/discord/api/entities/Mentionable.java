package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.utils.MiscUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

public interface Mentionable extends ISnowflake {
    @NotNull
    String getAsMention();
}
