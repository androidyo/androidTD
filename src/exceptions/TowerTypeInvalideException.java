package exceptions;

@SuppressWarnings("serial")
public class TowerTypeInvalideException extends Exception
{
	//塔的类型无效
    public TowerTypeInvalideException(String message)
    {
        super(message);
    }
}
