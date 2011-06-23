/*
  Copyright (C) 2010 Aurelien Da Campo, Romain Poulain
  
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

package models.towers;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import models.creatures.Creature;

/**
 * Classe de gestion d'une tour a canon.
 * <p>
 * Le tour canon est une tour lente avec de bons degats de zone. 
 * De plus, elle n'attaque que les creatures terrestres
 * 
 * @author Romain Poulain
 * @author Aurélien Da Campo
 * @version 1.0 | 27 novemenbre 2009
 * @since jdk1.6.0_16
 * @see Tower
 */
public class Tower_Ballistics extends Tower
{
	private static final long serialVersionUID = 1L;
	public static final Color COULEUR;
	public static final Image IMAGE;
	public static final Image ICONE;
	public static final int NIVEAU_MAX = 5;
    public static final int PRIX_ACHAT = 15;
    private static final String DESCRIPTION = 
        "La tour canon est une tour avec de bons dégâts mais lente. " +
        "Cette dernière n'attaque que les créatures terrestres";
	
	static
	{
	    COULEUR = new Color(64,64,64);
		IMAGE 	= Toolkit.getDefaultToolkit().getImage("img/tours/tourCanon.png");
		ICONE   = Toolkit.getDefaultToolkit().getImage("img/tours/icone_tourCanon.png");
	}
	
	/**
     * Constructeur de la tour.
     */
	public Tower_Ballistics()
	{
		super(0, 				// x
			  0, 				// y
			  20, 				// largeur
		      20, 				// hauteur
			  COULEUR,			// couleur de fond
			  "Canon",	        // nom
			  PRIX_ACHAT,		// prix achat
			  18,				// degats
			  40,				// rayon de portee
			  1,                // cadence de tir (tirs / sec.)
              Tower.TYPE_TERRESTRE, // type
              IMAGE,            // image sur terrain
              ICONE);           // icone pour bouton			
		
		description = DESCRIPTION;
	}

	public void ameliorer()
	{
        if(peutEncoreEtreAmelioree())
        {
    	    // le prix total est ajouté du prix d'achat de la tour
    		prixTotal 	+= prixAchat;
    		
    		prixAchat 	*= 2;	// + 100%
    		
    		// augmentation des degats
            degats      = getDegatsLvlSuivant();
            
            // augmentation du rayon de portee
            rayonPortee = getRayonPorteeLvlSuivant();
            
            // raccourcissement du temps de preparation du tire
            setCadenceTir(getCadenceTirLvlSuivant());
    		
    		niveau++;
        }
	}
	
	public void tirer(Creature creature)
	{
	    //jeu.getGestionnaireAnimations().ajouterAnimation(new BouleBalistique(jeu,this,creature,degats,RAYON_IMPACT));
	}

	public Tower getCopieOriginale()
	{
		return new Tower_Ballistics();
	}
	
	public boolean peutEncoreEtreAmelioree()
	{
		return niveau < NIVEAU_MAX;
	}
	
    @Override
    public double getCadenceTirLvlSuivant()
    {
        return getCadenceTir() * 1.2;
    }

    @Override
    public long getDegatsLvlSuivant()
    {
        return (long) (degats * 1.5);
    }

    @Override
    public double getRayonPorteeLvlSuivant()
    {
        return rayonPortee + 10;
    }
}
