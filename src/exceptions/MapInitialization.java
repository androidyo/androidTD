package exceptions;

@SuppressWarnings("serial")
public class MapInitialization extends RuntimeException
{
    public MapInitialization(String message)
    {
        super(message);
    }
}
