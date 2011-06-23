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
import models.animations.Explosion;
import models.creatures.Creature;
import models.game.Game;
import models.towers.Tower;

/**
 * 地球球
 * 
 * Cette classe est une animation qui dessine une boule de feu partant d'une
 * tour vers une creature.
 * 
 * @author Aurelien Da Campo
 * @version 1.1 | 4 mai 2010
 * @since jdk1.6.0_16
 */
public class Earthball extends Attack
{
    // constantes finales
    private static final long serialVersionUID = 1L;
    private static final int DIAMETRE_BOULE = 10;
    private static final int DIAMETRE_BOULE_MAX = 20;
    private static final Image IMAGE_BOULE;

    // attributs membres
    /**
     * Vitesse
     */
    private double vitesse = 0.1; // px / ms
    /**
     * distance entre la tete de la fleche et la tour
     */
    private double distanceCentreBoule = 0;

    /**
     * position de la tete de la fleche
     */
    private double xCentreBoule, yCentreBoule;

    
    private double distanceMax;
    private double distanceMaxInitiale;
    
    static
    {
        IMAGE_BOULE = Toolkit.getDefaultToolkit().getImage(
                "img/animations/attaques/bouleDeTerre.png");
    }

    /**
     * Constructeur de l'attaque
     * 
     * @param terrain le terrain sur lequel l'attaque est lancee
     * @param attaquant la tour attaquante
     * @param cible la creature visee
     */
    public Earthball(Game jeu, Tower attaquant, Creature cible, long degats,
            double rayonImpact)
    {
        super((int) attaquant.getCenterX(), (int) attaquant.getCenterY(), jeu,
                attaquant, cible);

        this.degats = degats;
        this.rayonImpact = rayonImpact;
        
        this.distanceMaxInitiale = calculerDistance();
    }

    @Override
    public void dessiner(Graphics2D g2)
    {
        double xAttaquant = attaquant.getCenterX();
        double yAttaquant = attaquant.getCenterY();

        // calcul de l'angle entre la cible et la tour
        // /!\ Math.atan2(y,x) /!\
        double angle = Math.atan2(cible.getCenterY() - yAttaquant, cible
                .getCenterX()
                - xAttaquant);

        // calcul de la tete et de la queue de la fleche
        xCentreBoule = Math.cos(angle) * distanceCentreBoule + xAttaquant; // x
        yCentreBoule = Math.sin(angle) * distanceCentreBoule + yAttaquant; // y

        
        int diametre = 0;
        // on ne tire pas en cloche tout le temps
        // si l'ennemi est trop proche on tire normal
        if(distanceMaxInitiale > 100.0)
        {
            double p = distanceCentreBoule / (distanceMax/2.0);
            
            if(distanceCentreBoule > distanceMax / 2.0)
                p = 1 - (p - 1);
            
            diametre = (int) (p * DIAMETRE_BOULE_MAX + DIAMETRE_BOULE);
        }
        else
        {
            diametre = DIAMETRE_BOULE;
        }
       

        
        // dessin de la boule de feu
        g2.drawImage(IMAGE_BOULE, (int) xCentreBoule - diametre / 2,
                (int) yCentreBoule - diametre / 2, 
                diametre,
                diametre, null);
    }

    @Override
    public void animer(long tempsPasse)
    {
        if(!isTerminate)
        {
            // la fleche avance
            distanceCentreBoule += tempsPasse * vitesse;
           
            distanceMax = calculerDistance();

            // si cette distance est atteinte ou depassee, l'attaque est
            // terminee
            if (distanceCentreBoule >= distanceMax)
            {
                informAttackListenerTerminate();
                isTerminate = true;
    
                jeu.ajouterAnimation(
                        (new Explosion((int) xCentreBoule - DIAMETRE_BOULE,
                                (int) yCentreBoule - DIAMETRE_BOULE)));
    
                attaquerCibles();
                
                isTerminate = true;
            }
        }
    }
    
    private double calculerDistance()
    {
        // calcul de la distance max de parcours de la fleche
        double diffX = cible.getCenterX() - attaquant.getCenterX();
        double diffY = cible.getCenterY() - attaquant.getCenterY();
        return Math.sqrt(diffX * diffX + diffY * diffY);   
    }
}
