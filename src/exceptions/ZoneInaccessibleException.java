package exceptions;

@SuppressWarnings("serial")
public class ZoneInaccessibleException extends Exception
{
	//区域无法访问
	public ZoneInaccessibleException(String cause)
	{
		super(cause);
	}
}
