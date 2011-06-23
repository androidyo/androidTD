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

import models.utils.Tools;

/**
 * 污点血动画管理类，怪物被击中时流血动画
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 */
public class HitInjured extends Animation
{
	// constantes statiques
    private static final long serialVersionUID = 1L;
	private static final Image[] TACHES = new Image[3];
	private static final long DUREE_DE_VIE = 3000;
	
	// attributs
	private float alpha = 1.0f;
    private Image image;
    private static final int DECALAGE = 4;
    private long tempsPasse = 0;
    
	static
    { 
	    // chargement des images
	    for(int i=0;i<TACHES.length;i++)
	        TACHES[i] = Toolkit.getDefaultToolkit().getImage("img/animations/tachesDeSang/"+i+".png");
    }
	
	/**
	 * Constructeur de l'animation
	 * 
	 * @param largeur largeur de l'anim
	 * @param hauteur hauteur de l'anim
	 */
	public HitInjured(int centerX, int centerY)
	{
	    super(centerX + Tools.tirerNombrePseudoAleatoire(-DECALAGE, DECALAGE), 
		        centerY +  Tools.tirerNombrePseudoAleatoire(-DECALAGE, DECALAGE));
		
		hauteur = Animation.HAUTEUR_SOL;
		
		image = TACHES[Tools.tirerNombrePseudoAleatoire(0, TACHES.length-1)];
	}

	@Override
	public void dessiner(Graphics2D g2)
	{
	    // style
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	    
	    // dessin
		g2.drawImage(image,x-image.getWidth(null)/2,y-image.getHeight(null)/2,null);
		
		// retabli la transparence
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
	}

    @Override
    public void animer(long tempsPasse)
    {
        this.tempsPasse += tempsPasse;
        
        // temps de vie passé
        if(this.tempsPasse > DUREE_DE_VIE)
            isTerminate = true;
        else
            // fondu
            alpha = 1.f - (float) this.tempsPasse / (float) DUREE_DE_VIE;
    }
}
