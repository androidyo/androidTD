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

import i18n.Language;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import views.ManageFonts;

/**
 * 获得星星动画管理类
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 */
public class GetStar extends Animation
{
	// constantes statiques
    private static final long serialVersionUID         = 1L;
	private static final float ETAPE_ALPHA             = .02f;
	private static final Image ETOILE;
	
	// attributs
	private float alpha = 1.0f;
	private int largeur;
	private int hauteur;
	
	static
    {
        ETOILE = Toolkit.getDefaultToolkit().getImage("img/icones/star.png");
    }
	
	/**
	 * Constructeur de l'animation
	 * 
	 * @param largeur largeur de l'anim
	 * @param hauteur hauteur de l'anim
	 */
	public GetStar(int largeur, int hauteur)
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
	    Font ancienne = g2.getFont();
	    g2.setFont(ManageFonts.POLICE_TITRE);
	    g2.setColor(Color.WHITE);
	    
	    int xOffset = -100;
	    g2.drawString(Language.getTexte(Language.ID_TITRE_ETOILE_GAGNEE), largeur/2+xOffset, hauteur/2);
		g2.drawImage(ETOILE,largeur/2+xOffset-50,hauteur/2-50,50,50,null);
		
		// retabli la transparence
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
		g2.setFont(ancienne);
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
