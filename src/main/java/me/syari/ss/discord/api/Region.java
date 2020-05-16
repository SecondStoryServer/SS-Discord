package me.syari.ss.discord.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public enum Region {
    AMSTERDAM("amsterdam", "Amsterdam", false),
    BRAZIL("brazil", "Brazil", false),
    EUROPE("europe", "Europe", false),
    EU_CENTRAL("eu-central", "EU Central", false),
    EU_WEST("eu-west", "EU West", false),
    FRANKFURT("frankfurt", "Frankfurt", false),
    HONG_KONG("hongkong", "Hong Kong", false),
    JAPAN("japan", "Japan", false),
    LONDON("london", "London", false),
    RUSSIA("russia", "Russia", false),
    INDIA("india", "India", false),
    SINGAPORE("singapore", "Singapore", false),
    SOUTH_AFRICA("southafrica", "South Africa", false),
    SYDNEY("sydney", "Sydney", false),
    US_CENTRAL("us-central", "US Central", false),
    US_EAST("us-east", "US East", false),
    US_SOUTH("us-south", "US South", false),
    US_WEST("us-west", "US West", false),

    VIP_AMSTERDAM("vip-amsterdam", "Amsterdam (VIP)", true),
    VIP_BRAZIL("vip-brazil", "Brazil (VIP)", true),
    VIP_EU_CENTRAL("vip-eu-central", "EU Central (VIP)", true),
    VIP_EU_WEST("vip-eu-west", "EU West (VIP)", true),
    VIP_FRANKFURT("vip-frankfurt", "Frankfurt (VIP)", true),
    VIP_JAPAN("vip-japan", "Japan (VIP)", true),
    VIP_LONDON("vip-london", "London (VIP)", true),
    VIP_SINGAPORE("vip-singapore", "Singapore (VIP)", true),
    VIP_SOUTH_AFRICA("vip-southafrica", "South Africa (VIP)", true),
    VIP_SYDNEY("vip-sydney", "Sydney (VIP)", true),
    VIP_US_CENTRAL("vip-us-central", "US Central (VIP)", true),
    VIP_US_EAST("vip-us-east", "US East (VIP)", true),
    VIP_US_SOUTH("vip-us-south", "US South (VIP)", true),
    VIP_US_WEST("vip-us-west", "US West (VIP)", true),

    UNKNOWN("", "Unknown Region", false);

    private final String key;
    private final String name;
    private final boolean vip;

    Region(String key, String name, boolean vip) {
        this.key = key;
        this.name = name;
        this.vip = vip;
    }


    @Nonnull
    public String getName() {
        return name;
    }


    @Nonnull
    public String getKey() {
        return key;
    }


    public boolean isVip() {
        return vip;
    }


    @Nonnull
    public static Region fromKey(@Nullable String key) {
        for (Region region : values()) {
            if (region.getKey().equals(key)) {
                return region;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return getName();
    }
}
