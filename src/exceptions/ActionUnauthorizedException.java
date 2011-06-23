package exceptions;

@SuppressWarnings("serial")
public class ActionUnauthorizedException extends Exception
{
	//未经授权的行动 异常
    public ActionUnauthorizedException(String message)
    {
        super(message);
    }
}
