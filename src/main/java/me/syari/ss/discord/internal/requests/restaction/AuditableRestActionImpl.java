package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.audit.ThreadLocalReason;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.EncodingUtil;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class AuditableRestActionImpl<T> extends RestActionImpl<T> implements AuditableRestAction<T> {
    protected String reason = null;

    public AuditableRestActionImpl(JDA api, Route.CompiledRoute route) {
        super(api, route);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public AuditableRestAction<T> setCheck(BooleanSupplier checks) {
        return (AuditableRestActionImpl) super.setCheck(checks);
    }

    @Override
    protected CaseInsensitiveMap<String, String> finalizeHeaders() {
        CaseInsensitiveMap<String, String> headers = super.finalizeHeaders();

        if (reason == null || reason.isEmpty()) {
            String localReason = ThreadLocalReason.getCurrent();
            if (localReason == null || localReason.isEmpty())
                return headers;
            else
                return generateHeaders(headers, localReason);
        }

        return generateHeaders(headers, reason);
    }

    @Nonnull
    private CaseInsensitiveMap<String, String> generateHeaders(CaseInsensitiveMap<String, String> headers, String reason) {
        if (headers == null)
            headers = new CaseInsensitiveMap<>();

        headers.put("X-Audit-Log-Reason", uriEncode(reason));
        return headers;
    }

    private String uriEncode(String input) {
        String formEncode = EncodingUtil.encodeUTF8(input);
        return formEncode.replace('+', ' ');
    }
}
