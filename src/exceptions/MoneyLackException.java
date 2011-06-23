package exceptions;

/**
 * Exception 当玩家没有足够的金钱，取消该操作
 * action
 * 
 * @author Pierre-Do
 * 
 */
public class MoneyLackException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7348304222504565854L;

	/**
	 * 简单的构造函数调用
	 * @param cause 关于异常的简短错误信息
	 */
	public MoneyLackException(String cause)
	{
		super(cause);
	}
}
