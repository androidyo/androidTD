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
 * 管理爆炸动画的类
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 */
public class Explosion extends Animation
{
	// constantes statiques
    private static final long serialVersionUID         = 1L;
	private static final Image[] EXPLOSION = new Image[4];
	
	// attributs
	private float alpha = 1.0f;
	private int indiceAnim = 0;
	
	static
    { 
	    for(int i=0;i<EXPLOSION.length;i++)
	        EXPLOSION[i] = Toolkit.getDefaultToolkit().getImage("img/animations/explosion/"+i+".png");
    }
	
	/**
	 * Constructeur de l'animation
	 * 
	 * @param largeur largeur de l'anim
	 * @param hauteur hauteur de l'anim
	 */
	public Explosion(int x, int y)
	{
		super(x, y);
	}

	@Override
	public void dessiner(Graphics2D g2)
	{
	    // style
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	    
	    // dessin
		g2.drawImage(EXPLOSION[indiceAnim],x,y,null);
		
		// retabli la transparence
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
	}

    @Override
    public void animer(long tempsPasse)
    {
        if(indiceAnim < EXPLOSION.length-1)
        {
            indiceAnim++;
            alpha -= (1.f / EXPLOSION.length) / 2.0;
        }
        else
            isTerminate = true;
    }
}
