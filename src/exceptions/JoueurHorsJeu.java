package exceptions;

@SuppressWarnings("serial")
public class JoueurHorsJeu extends Exception
{
	//游戏播放器关闭
    public JoueurHorsJeu(String message)
    {
        super(message);
    }
}
