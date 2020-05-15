

package me.syari.ss.discord.api.audit;

public enum AuditLogOption
{

    COUNT("count"),


    MESSAGE("message_id"),


    CHANNEL("channel_id"),


    USER("user_id"),


    ROLE("role_id"),


    ROLE_NAME("role_name"),


    TYPE("type"),


    ID("id"),


    DELETE_MEMBER_DAYS("delete_member_days"),


    MEMBERS_REMOVED("members_removed");

    private final String key;

    AuditLogOption(String key)
    {
        this.key = key;
    }


    public String getKey()
    {
        return key;
    }

    @Override
    public String toString()
    {
        return name() + '(' + key + ')';
    }
}
