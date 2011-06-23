package exceptions;

@SuppressWarnings("serial")
public class ReachMaxLevelException extends Exception
{
	//达到最大等级
    public ReachMaxLevelException(String message)
    {
        super(message);
    }
}
