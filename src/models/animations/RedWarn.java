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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * 生命损失动画管理类，屏幕红色警告
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 22 decembre 2009
 * @since jdk1.6.0_16
 */
public class RedWarn extends Animation
{
	// constantes statiques
    private static final long serialVersionUID         = 1L;
	private static final float ETAPE_ALPHA             = .1f;
	private static final Image BLESSURE;
	
	// attributs
	private float alpha = 1.0f;
	private int largeur;
	private int hauteur;
	
	static
    {
        BLESSURE = Toolkit.getDefaultToolkit().getImage("img/animations/perteVie.png");
    }
	
	/**
	 * Constructeur de l'animation
	 * 
	 * @param largeur largeur de l'anim
	 * @param hauteur hauteur de l'anim
	 */
	public RedWarn(int largeur, int hauteur)
	{
		super(0, 0);
		this.largeur = largeur;
		this.hauteur = hauteur;
	}

	@Override
	public void dessiner(Graphics2D g2)
	{
	    // style
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	    
	    // dessin
		g2.drawImage(BLESSURE,x,y,largeur,hauteur,null);
		
		// retabli la transparence
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
	}

    @Override
    public void animer(long tempsPasse)
    {
        if(alpha >= ETAPE_ALPHA)
            alpha -= ETAPE_ALPHA;
       
        if(alpha < ETAPE_ALPHA)
            isTerminate = true;
    }
}
