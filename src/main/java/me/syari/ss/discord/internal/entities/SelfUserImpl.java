package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.entities.SelfUser;
import me.syari.ss.discord.api.managers.AccountManager;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.managers.AccountManagerImpl;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.ReentrantLock;

public class SelfUserImpl extends UserImpl implements SelfUser {
    protected final ReentrantLock mngLock = new ReentrantLock();
    protected volatile AccountManager manager;

    private boolean verified;

    public SelfUserImpl(long id, JDAImpl api) {
        super(id, api);
    }

    @Override
    public boolean hasPrivateChannel() {
        return false;
    }

    @Override
    public PrivateChannel getPrivateChannel() {
        throw new UnsupportedOperationException("You cannot get a PrivateChannel with yourself (SelfUser)");
    }

    @Nonnull
    @Override
    public AccountManager getManager() {
        AccountManager mng = manager;
        if (mng == null) {
            mng = MiscUtil.locked(mngLock, () ->
            {
                if (manager == null)
                    manager = new AccountManagerImpl(this);
                return manager;
            });
        }
        return mng;
    }

    public SelfUserImpl setVerified(boolean verified) {
        this.verified = verified;
        return this;
    }
}
