

package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Invite;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.InviteAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class InviteActionImpl extends AuditableRestActionImpl<Invite> implements InviteAction
{
    private Integer maxAge = null;
    private Integer maxUses = null;
    private Boolean temporary = null;
    private Boolean unique = null;

    public InviteActionImpl(final JDA api, final String channelId)
    {
        super(api, Route.Invites.CREATE_INVITE.compile(channelId));
    }

    @Nonnull
    @Override
    public InviteActionImpl setCheck(BooleanSupplier checks)
    {
        return (InviteActionImpl) super.setCheck(checks);
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public InviteActionImpl setMaxAge(final Integer maxAge)
    {
        if (maxAge != null)
            Checks.notNegative(maxAge, "maxAge");

        this.maxAge = maxAge;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public InviteActionImpl setMaxAge(final Long maxAge, @Nonnull final TimeUnit timeUnit)
    {
        if (maxAge == null)
            return this.setMaxAge(null);

        Checks.notNegative(maxAge, "maxAge");
        Checks.notNull(timeUnit, "timeUnit");

        return this.setMaxAge(Math.toIntExact(timeUnit.toSeconds(maxAge)));
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public InviteActionImpl setMaxUses(final Integer maxUses)
    {
        if (maxUses != null)
            Checks.notNegative(maxUses, "maxUses");

        this.maxUses = maxUses;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public InviteActionImpl setTemporary(final Boolean temporary)
    {
        this.temporary = temporary;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public InviteActionImpl setUnique(final Boolean unique)
    {
        this.unique = unique;
        return this;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject object = DataObject.empty();

        if (this.maxAge != null)
            object.put("max_age", this.maxAge);
        if (this.maxUses != null)
            object.put("max_uses", this.maxUses);
        if (this.temporary != null)
            object.put("temporary", this.temporary);
        if (this.unique != null)
            object.put("unique", this.unique);

        return getRequestBody(object);
    }

    @Override
    protected void handleSuccess(final Response response, final Request<Invite> request)
    {
        request.onSuccess(this.api.getEntityBuilder().createInvite(response.getObject()));
    }
}
