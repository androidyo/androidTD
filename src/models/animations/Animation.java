/*
  Copyright (C) 2010 Aurelien Da Campo

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package models.animations;

import java.awt.*;

/**
 * 动画管理类
 * <p>
 * 这个类是用来显示动画的
 * <p>
 * 它是抽象的，必须继承并且实例化
 * 
 * @author Aurelien Da Campo
 * @version 1.1 | mai 2010
 * @since jdk1.6.0_16
 */
public abstract class Animation extends Point
{
    public static final int HAUTEUR_SOL = 0;
    public static final int HAUTEUR_AIR = 1;
    
    private static final long serialVersionUID = 1L;
	protected boolean isTerminate;
    protected int hauteur = HAUTEUR_AIR;
    
	/**
	 * Constructeur de l'animation.
	 * 
	 * @param x position initial x
	 * @param y position initial y
	 */
	public Animation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * 绘制动画
	 * 
	 * @param g2 le Graphics2D pour dessiner
	 */
	abstract public void dessiner(Graphics2D g2);

	/**
     * Permet d'animer l'animation
     */
    abstract public void animer(long timePass);
	
	/**
	 * 指示动画是否完成
	 * 
	 * 当动画完成后将被销毁.
	 * 
	 * @return true 如果完成，否则返回false.
	 */
    public boolean isTerminate()
    {
        return isTerminate;
    }
     
    /**
     * 检索动画的高度
     * 
     * @return 高度
     */
    public int getHauteur()
    {
        return hauteur;
    }
}
