
package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.exceptions.AccountTypeException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.entities.SelfUser;
import me.syari.ss.discord.api.managers.AccountManager;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.managers.AccountManagerImpl;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.ReentrantLock;

public class SelfUserImpl extends UserImpl implements SelfUser
{
    protected final ReentrantLock mngLock = new ReentrantLock();
    protected volatile AccountManager manager;

    private boolean verified;
    private boolean mfaEnabled;

    //Client only
    private String email;
    private String phoneNumber;
    private boolean mobile;
    private boolean nitro;

    public SelfUserImpl(long id, JDAImpl api)
    {
        super(id, api);
    }

    @Override
    public boolean hasPrivateChannel()
    {
        return false;
    }

    @Override
    public PrivateChannel getPrivateChannel()
    {
        throw new UnsupportedOperationException("You cannot get a PrivateChannel with yourself (SelfUser)");
    }

    @Nonnull
    @Override
    public RestAction<PrivateChannel> openPrivateChannel()
    {
        throw new UnsupportedOperationException("You cannot open a PrivateChannel with yourself (SelfUser)");
    }

    @Override
    public boolean isVerified()
    {
        return verified;
    }

    @Override
    public boolean isMfaEnabled()
    {
        return mfaEnabled;
    }

    @Nonnull
    @Override
    public String getEmail() throws AccountTypeException
    {
        if (getJDA().getAccountType() != AccountType.CLIENT)
            throw new AccountTypeException(AccountType.CLIENT, "Email retrieval can only be done on CLIENT accounts!");
        return email;
    }

    @Override
    public String getPhoneNumber() throws AccountTypeException
    {
        if (getJDA().getAccountType() != AccountType.CLIENT)
            throw new AccountTypeException(AccountType.CLIENT, "Phone number retrieval can only be done on CLIENT accounts!");
        return this.phoneNumber;
    }

    @Override
    public boolean isMobile() throws AccountTypeException
    {
        if (getJDA().getAccountType() != AccountType.CLIENT)
            throw new AccountTypeException(AccountType.CLIENT, "Mobile app retrieval can only be done on CLIENT accounts!");
        return this.mobile;
    }

    @Override
    public boolean isNitro() throws AccountTypeException
    {
        if (getJDA().getAccountType() != AccountType.CLIENT)
            throw new AccountTypeException(AccountType.CLIENT, "Nitro status retrieval can only be done on CLIENT accounts!");
        return this.nitro;
    }

    @Override
    public long getAllowedFileSize()
    {
        if (this.nitro) // by directly accessing the field we don't need to check the account type
            return Message.MAX_FILE_SIZE_NITRO;
        else
            return Message.MAX_FILE_SIZE;
    }

    @Nonnull
    @Override
    public AccountManager getManager()
    {
        AccountManager mng = manager;
        if (mng == null)
        {
            mng = MiscUtil.locked(mngLock, () ->
            {
                if (manager == null)
                    manager = new AccountManagerImpl(this);
                return manager;
            });
        }
        return mng;
    }

    public SelfUserImpl setVerified(boolean verified)
    {
        this.verified = verified;
        return this;
    }

    public SelfUserImpl setMfaEnabled(boolean enabled)
    {
        this.mfaEnabled = enabled;
        return this;
    }

    public SelfUserImpl setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public SelfUserImpl setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public SelfUserImpl setMobile(boolean mobile)
    {
        this.mobile = mobile;
        return this;
    }

    public SelfUserImpl setNitro(boolean nitro)
    {
        this.nitro = nitro;
        return this;
    }
}
