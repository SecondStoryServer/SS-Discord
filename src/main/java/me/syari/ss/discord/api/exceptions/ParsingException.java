

package me.syari.ss.discord.api.exceptions;

public class ParsingException extends IllegalStateException
{

    public ParsingException(String message)
    {
        super(message);
    }

    public ParsingException(Exception cause)
    {
        super(cause);
    }
}
