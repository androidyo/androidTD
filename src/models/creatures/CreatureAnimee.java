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

package models.creatures;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * Classe de gestion d'une creature.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 27 novemenbre 2009
 * @since jdk1.6.0_16
 * @see Creature
 */
public class CreatureAnimee extends Creature
{
	private static final long serialVersionUID = 1L;
	private static final Image[] IMAGES;
	
	static
	{
		IMAGES = new Image[]
		{
			Toolkit.getDefaultToolkit().getImage("img/creatures/carapaceKoopa/carapaceKoopa_0.gif"),
			Toolkit.getDefaultToolkit().getImage("img/creatures/carapaceKoopa/carapaceKoopa_1.gif"),
			Toolkit.getDefaultToolkit().getImage("img/creatures/carapaceKoopa/carapaceKoopa_2.gif"),
			Toolkit.getDefaultToolkit().getImage("img/creatures/carapaceKoopa/carapaceKoopa_3.gif")
		};
	}
	
	/**
	 * Constructeur de base.
	 * 
	 * @param santeMax la sante max de cette creature
	 * @param nbPiecesDOr le nombre de pieces d'or de cette creature
	 * @param vitesse vitesse de la creature
	 */
	public CreatureAnimee(long santeMax, int nbPiecesDOr, double vitesse)
	{
		this(0, 0, santeMax, nbPiecesDOr,vitesse);
	}
	
	/**
	 * Constructeur avec position initiale.
	 * 
	 * @param x la position sur l'axe X de la creature
	 * @param y la position sur l'axe Y de la creature
	 * @param santeMax la sante max de cette creature
	 * @param nbPiecesDOr le nombre de pieces d'or de cette creature
	 * @param vitesse vitesse de la creature
	 */
	public CreatureAnimee(int x, int y, long santeMax, int nbPiecesDOr, double vitesse)
	{
		super(x, y, 14, 14, santeMax,nbPiecesDOr,vitesse,
		        Creature.TYPE_TERRIENNE ,IMAGES[0],"Rhinoceros");
		
		// TODO mieux gerer l'animation
		/*Thread animation = new Thread( new Runnable()
        {
            public void run()
            {
                int i = 1;
                
                // tant que la creature est en jeu et vivante
                while(!aDetruire() && !estMorte())
                {
                    // image suivante
                    i %= (IMAGES.length);
                    image = IMAGES[i++];

                    try{
                        Thread.sleep(100);
                    } 
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
		animation.start();*/
	}

	/**
	 * permet de copier la creature
	 */
	public Creature copier()
	{
		return new CreatureAnimee(x,y,getSanteMax(),getNbPiecesDOr(),getVitesseNormale());
	}
}
