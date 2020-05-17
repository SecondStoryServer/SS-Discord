package me.syari.ss.discord.api.utils;

@FunctionalInterface
public interface ChunkingFilter {
    ChunkingFilter ALL = (x) -> true;

    boolean filter(long guildId);

}
