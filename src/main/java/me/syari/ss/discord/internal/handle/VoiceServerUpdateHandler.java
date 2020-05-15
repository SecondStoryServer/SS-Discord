

package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.hooks.VoiceDispatchInterceptor;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;

import me.syari.ss.discord.internal.requests.WebSocketClient;

public class VoiceServerUpdateHandler extends SocketHandler
{
    public VoiceServerUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        return null;
    }
}
