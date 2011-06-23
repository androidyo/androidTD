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
 * 火球攻击
 * 
 * 从火球塔到生物的动画绘制类
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 30 decembre 2009
 * @since jdk1.6.0_16
 */
public class Fireball extends Attack
{
    // constantes finales
    private static final long serialVersionUID  = 1L;
    private static final int DIAMETRE_BOULET    = 8;
    private static final Image IMAGE_BOULE;
    
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
        IMAGE_BOULE   = Toolkit.getDefaultToolkit().getImage("img/animations/attaques/bouleDeFeu.png");
    }
    
    
    /**
     * Constructeur de l'attaque
     * 
     * @param terrain le terrain sur lequel l'attaque est lancee
     * @param attaquant la tour attaquante
     * @param cible la creature visee
     */
    public Fireball(Game jeu, Tower attaquant, Creature cible, 
                      long degats, double rayonImpact)
    {
        super((int) attaquant.getCenterX(),(int) attaquant.getCenterY(), jeu, attaquant, cible);
        
        this.degats         = degats;
        this.rayonImpact    = rayonImpact;
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
                    (int) xCentreBoule - DIAMETRE_BOULET / 2, 
                    (int) yCentreBoule - DIAMETRE_BOULET / 2, 
                    DIAMETRE_BOULET, 
                    DIAMETRE_BOULET, null);
    }

    @Override
    public void animer(long tempsPasse)
    {
        // si la creature meurt on arrete l'attaque
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
                
                isTerminate = true;
                
                attaquerCibles();
                
                isTerminate = true;
            }
        } 
    }
}
