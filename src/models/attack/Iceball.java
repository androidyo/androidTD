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

import java.awt.*;
import models.creatures.Creature;
import models.game.Game;
import models.towers.Tower;

/**
 * 冰球
 * 
 * Cette classe est une animation qui dessine une boule de feu  partant d'une tour
 * vers une creature.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 30 decembre 2009
 * @since jdk1.6.0_16
 */
public class Iceball extends Attack
{
    // constantes finales
    private static final long serialVersionUID  = 1L;
    private static final int DIAMETRE_BOULE     = 10;
    private static final Image IMAGE_BOULE;
    private static final long DUREE_RALENTISSEMENT = 5000;
    
    // attributs membres
    /**
     * Vitesse
     */
    private double vitesse = 0.2; // px / ms
    /**
     * distance entre la tete de la fleche et la tour
     */
    private double distanceCentreBoule = 0;
    
    /**
     * position de la tete de la fleche
     */
    private double xCentreBoule, yCentreBoule;
    
    static
    {
        IMAGE_BOULE   = Toolkit.getDefaultToolkit().getImage("img/animations/attaques/bouleDeGlace.png");
    }
    
    
    /**
     * Constructeur de l'attaque
     * 
     * @param terrain le terrain sur lequel l'attaque est lancee
     * @param attaquant la tour attaquante
     * @param cible la creature visee
     */
    public Iceball(Game jeu, Tower attaquant, Creature cible, 
                      long degats, double coeffRalentissement)
    {
        super((int) attaquant.getCenterX(),(int) attaquant.getCenterY(), jeu, attaquant, cible);
        
        this.degats                 = degats;
        this.coeffRalentissement    = coeffRalentissement;
    }

    @Override
    public void dessiner(Graphics2D g2)
    {
        double xAttaquant = attaquant.getCenterX();
        double yAttaquant = attaquant.getCenterY();
         
        // calcul de l'angle entre la cible et la tour
        // /!\ Math.atan2(y,x) /!\
        double angle = Math.atan2(
                        cible.getCenterY() - yAttaquant,
                        cible.getCenterX() - xAttaquant); 
        
        // calcul de la tete et de la queue de la fleche
        xCentreBoule = Math.cos(angle)*distanceCentreBoule + xAttaquant; // x
        yCentreBoule = Math.sin(angle)*distanceCentreBoule + yAttaquant; // y
             
        // dessin de la boule de feu
        g2.drawImage(IMAGE_BOULE, 
                    (int) xCentreBoule - DIAMETRE_BOULE / 2, 
                    (int) yCentreBoule - DIAMETRE_BOULE / 2, 
                    DIAMETRE_BOULE, 
                    DIAMETRE_BOULE, null);
    }

    @Override
    public void animer(long tempsPasse)
    {   
        if(!isTerminate)
        {
            // la fleche avance
            distanceCentreBoule += tempsPasse * vitesse;
            
            // calcul de la distance max de parcours de la fleche
            double diffX       = cible.getCenterX() - attaquant.getCenterX();
            double diffY       = cible.getCenterY() - attaquant.getCenterY();  
            double distanceMax = Math.sqrt(diffX * diffX + diffY * diffY);
            
            // si cette distance est atteinte ou depassee, l'attaque est terminee
            if (distanceCentreBoule >= distanceMax)
            {
                informAttackListenerTerminate();
                
                attaquerCibles();
               
                // ajout du ralentissement
                if(cible.getCoeffRalentissement() == 0.0)
                {
                    jeu.ajouterAnimation(new SlowDown(jeu,attaquant,cible,
                            coeffRalentissement,DUREE_RALENTISSEMENT));
                    
                }
  
                isTerminate = true;
            }
        }
    }
}
