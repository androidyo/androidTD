package exceptions;

@SuppressWarnings("serial")
public class NoLocationAvailableException extends Exception
{
	//无地点可用
    public NoLocationAvailableException(String message)
    {
        super(message);
    }
}
