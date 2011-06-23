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

package models.attack;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import models.creatures.Creature;
import models.game.Game;
import models.towers.Tower;

/**
 * Attaque glacon.
 * 
 * 管理生物减速
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 30 decembre 2009
 * @since jdk1.6.0_16
 */
public class SlowDown extends Attack
{
	// constantes statiques
    private static final long serialVersionUID  = 1L;
    private static final Image IMAGE;
    private final long DUREE_RALENTISSEMENT;
    private long tempsPasse = 0L;
    
    static
    {
        IMAGE = Toolkit.getDefaultToolkit().getImage("img/animations/attaques/glacon.png");
    }
    
	/**
	 * Constructeur de l'animation
	 * 
	 * @param x position initiale x
	 * @param y position initiale y
	 * @param nbPiecesOr nombre de pieces d'or gagne
	 */
	public SlowDown(Game jeu, Tower attaquant, Creature cible, double coeffRalentissement,  long dureeRalentissement)
	{
		super((int)attaquant.getX(), (int) attaquant.getY(), jeu, attaquant, cible);
		
		cible.setCoeffRalentissement(coeffRalentissement);
		
		DUREE_RALENTISSEMENT = dureeRalentissement;
	}

	@Override
	public void dessiner(Graphics2D g2)
	{
        // dessin de la boule de feu
        g2.drawImage(IMAGE, 
                    (int) cible.getX(), 
                    (int) cible.getY(), 
                    (int) cible.getWidth(), 
                    (int) cible.getHeight(), null);
	}

    @Override
    public void animer(long tempsPasse)
    { 
        this.tempsPasse += tempsPasse;
        
        if(cible.estMorte())
            isTerminate = true;
        else if(this.tempsPasse > DUREE_RALENTISSEMENT)
        {
            cible.setCoeffRalentissement(0.0);
            isTerminate = true;
        }
    }
}
