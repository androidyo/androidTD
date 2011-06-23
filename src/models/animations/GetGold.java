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
import java.awt.Color;
import java.awt.Graphics2D;

import models.utils.HighScores;

/**
 * 获得金币动画管理类
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 22 decembre 2009
 * @since jdk1.6.0_16
 * @see HighScores
 */
public class GetGold extends Animation
{
	// constantes statiques
    private static final long serialVersionUID         = 1L;
	private static final Color COULEUR_GAIN_PIECES_OR  = Color.GREEN;
	private static final float ETAPE_ALPHA             = .01f;
	
	// attributs
	private int nbPiecesOr;
	private float alpha = 1.0f;
	
	/**
	 * Constructeur de l'animation
	 * 
	 * @param x position initiale x
	 * @param y position initiale y
	 * @param nbPiecesOr nombre de pieces d'or gagne
	 */
	public GetGold(int x, int y, int nbPiecesOr)
	{
		super(x, y);
		this.nbPiecesOr = nbPiecesOr;
	}

	@Override
	public void dessiner(Graphics2D g2)
	{
	    // style
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	    g2.setColor(COULEUR_GAIN_PIECES_OR);
	    
	    // dessin
		g2.drawString("+"+nbPiecesOr,x,y);
		
		// retabli la transparence
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
	}

    @Override
    public void animer(long tempsPasse)
    {
        // diminution de la transparence
        alpha -= ETAPE_ALPHA;
        
        // si invisible
        if (alpha <= .0f)
        {
            alpha       = .0f;
            isTerminate = true;
            return;
        }
        
        // l'animation monte
        y--;
    }
}
