

package net.dv8tion.jda.internal.requests.ratelimit;

import net.dv8tion.jda.api.requests.Request;

import java.util.Queue;

public interface IBucket
{
    Queue<Request> getRequests();
}
