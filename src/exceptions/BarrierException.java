package exceptions;

@SuppressWarnings("serial")
public class BarrierException extends Exception
{
	//路障
	public BarrierException(String cause)
	{
		super(cause);
	}
}
