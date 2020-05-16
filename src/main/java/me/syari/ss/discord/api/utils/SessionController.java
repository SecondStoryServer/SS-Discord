package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ForRemoval;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.internal.utils.tuple.Pair;

import javax.annotation.Nonnull;


public interface SessionController {

    int IDENTIFY_DELAY = 5;


    void appendSession(@Nonnull SessionConnectNode node);


    void removeSession(@Nonnull SessionConnectNode node);


    long getGlobalRatelimit();


    void setGlobalRatelimit(long ratelimit);


    @Nonnull
    String getGateway(@Nonnull JDA api);


    @Nonnull
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.0.0")
    @ReplaceWith("getShardedGateway(api)")
    @SuppressWarnings("DeprecatedIsStillUsed")
    Pair<String, Integer> getGatewayBot(@Nonnull JDA api);


    @Nonnull
    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    default ShardedGateway getShardedGateway(@Nonnull JDA api) {
        Pair<String, Integer> tuple = getGatewayBot(api);
        return new ShardedGateway(tuple.getLeft(), tuple.getRight());
    }


    class ShardedGateway {
        private final String url;
        private final int shardTotal;


        public ShardedGateway(String url, int shardTotal) {
            this.url = url;
            this.shardTotal = shardTotal;
        }


        public String getUrl() {
            return url;
        }


        public int getShardTotal() {
            return shardTotal;
        }
    }


    interface SessionConnectNode {


        @Nonnull
        JDA getJDA();


        void run(boolean isLast) throws InterruptedException;
    }
}
