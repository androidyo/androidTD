package exceptions;

@SuppressWarnings("serial")
public class NoPositionAvailableException extends Exception
{
    public NoPositionAvailableException(String message)
    {
        super(message);
    }
}
