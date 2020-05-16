package me.syari.ss.discord.api;

import javax.annotation.Nonnull;


public enum Region {
    AMSTERDAM("amsterdam", "Amsterdam"),
    BRAZIL("brazil", "Brazil"),
    EUROPE("europe", "Europe"),
    EU_CENTRAL("eu-central", "EU Central"),
    EU_WEST("eu-west", "EU West"),
    FRANKFURT("frankfurt", "Frankfurt"),
    HONG_KONG("hongkong", "Hong Kong"),
    JAPAN("japan", "Japan"),
    LONDON("london", "London"),
    RUSSIA("russia", "Russia"),
    INDIA("india", "India"),
    SINGAPORE("singapore", "Singapore"),
    SOUTH_AFRICA("southafrica", "South Africa"),
    SYDNEY("sydney", "Sydney"),
    US_CENTRAL("us-central", "US Central"),
    US_EAST("us-east", "US East"),
    US_SOUTH("us-south", "US South"),
    US_WEST("us-west", "US West"),

    VIP_AMSTERDAM("vip-amsterdam", "Amsterdam (VIP)"),
    VIP_BRAZIL("vip-brazil", "Brazil (VIP)"),
    VIP_EU_CENTRAL("vip-eu-central", "EU Central (VIP)"),
    VIP_EU_WEST("vip-eu-west", "EU West (VIP)"),
    VIP_FRANKFURT("vip-frankfurt", "Frankfurt (VIP)"),
    VIP_JAPAN("vip-japan", "Japan (VIP)"),
    VIP_LONDON("vip-london", "London (VIP)"),
    VIP_SINGAPORE("vip-singapore", "Singapore (VIP)"),
    VIP_SOUTH_AFRICA("vip-southafrica", "South Africa (VIP)"),
    VIP_SYDNEY("vip-sydney", "Sydney (VIP)"),
    VIP_US_CENTRAL("vip-us-central", "US Central (VIP)"),
    VIP_US_EAST("vip-us-east", "US East (VIP)"),
    VIP_US_SOUTH("vip-us-south", "US South (VIP)"),
    VIP_US_WEST("vip-us-west", "US West (VIP)"),

    UNKNOWN("", "Unknown Region");

    private final String key;
    private final String name;

    Region(String key, String name) {
        this.key = key;
        this.name = name;
    }


    @Nonnull
    public String getName() {
        return name;
    }


    @Nonnull
    public String getKey() {
        return key;
    }


    @Override
    public String toString() {
        return getName();
    }
}
