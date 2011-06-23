package exceptions;

@SuppressWarnings("serial")
public class CurrentGameException extends Exception
{
	//当前的游戏
    public CurrentGameException(String message)
    {
        super(message);
    }
}
