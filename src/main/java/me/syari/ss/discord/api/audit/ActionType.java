package me.syari.ss.discord.api.audit;

public enum ActionType {

    GUILD_UPDATE(1),


    CHANNEL_CREATE(10),


    CHANNEL_UPDATE(11),


    CHANNEL_DELETE(12),


    CHANNEL_OVERRIDE_CREATE(13),


    CHANNEL_OVERRIDE_UPDATE(14),


    CHANNEL_OVERRIDE_DELETE(15),


    KICK(20),


    PRUNE(21),


    BAN(22),


    UNBAN(23),


    MEMBER_UPDATE(24),


    MEMBER_ROLE_UPDATE(25),


    MEMBER_VOICE_MOVE(26),


    MEMBER_VOICE_KICK(27),


    BOT_ADD(28),


    ROLE_CREATE(30),


    ROLE_UPDATE(31),


    ROLE_DELETE(32),


    INVITE_CREATE(40),


    INVITE_UPDATE(41),


    INVITE_DELETE(42),


    WEBHOOK_CREATE(50),


    WEBHOOK_UPDATE(51),


    WEBHOOK_REMOVE(52),


    EMOTE_CREATE(60),


    EMOTE_UPDATE(61),


    EMOTE_DELETE(62),


    MESSAGE_CREATE(70),


    MESSAGE_UPDATE(71),


    MESSAGE_DELETE(72),


    MESSAGE_BULK_DELETE(73),


    MESSAGE_PIN(74),


    MESSAGE_UNPIN(75),


    INTEGRATION_CREATE(80),


    INTEGRATION_UPDATE(81),


    INTEGRATION_DELETE(82),

    UNKNOWN(-1);

    private final int key;

    ActionType(int key) {
        this.key = key;
    }


    public int getKey() {
        return key;
    }


    public static ActionType from(int key) {
        for (ActionType type : values()) {
            if (type.key == key)
                return type;
        }
        return UNKNOWN;
    }
}
