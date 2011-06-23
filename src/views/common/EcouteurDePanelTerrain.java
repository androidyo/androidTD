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

package views.common;

import models.creatures.Creature;
import models.towers.Tower;

/**
 * Interface permettant de mettre en oeuvre le pattern Observable/ Observé pour la
 * classe Panel_Terrain.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | avril 2010
 * @see Panel_Terrain
 */
public interface EcouteurDePanelTerrain
{
    /**
     * Permet d'informer l'ecouteur qu'une tour a ete selectionnee
     * 
     * @param tour la tour selectionnee
     * @param mode le mode de selection
     */
    public void tourSelectionnee(Tower tour,int mode);
    
    /**
     * Permet d'informer l'ecouteur qu'une creature a ete selectionnee
     * 
     * @param creature la creature selectionnee
     */
    public void creatureSelectionnee(Creature creature);
    
    /**
     * Permet d'informer l'ecouteur que le joueur veut acheter une tour
     * 
     * @param tour la tour voulue
     */
    public void acheterTour(Tower tour);
    
    /**
     * Permet d'informer l'ecouteur que le joueur veut vendre une tour
     * 
     * @param tour la tour a ameliorer
     */
    public void vendreTour(Tower tour);
    
    
    /**
     * Permet d'informer l'ecouteur que le joueur veut ameliorer une tour
     * 
     * @param tour la tour a ameliorer
     */
    public void ameliorerTour(Tower tour);

    
    /**
     * Permet de mettre a jour les infos du jeu
     */
    public void miseAJourInfoJeu();
    
    /**
     * Permet de demander une mise a jour des informations de la vague suivante
     */
    public void ajouterInfoVagueSuivanteDansConsole();
    
    
    /**
     * Permet d'informer la fenetre que le joueur veut lancer une vague de
     * creatures.
     */
    public void lancerVagueSuivante();
    
    
    /**
     * Permet d'informer la fenetre qu'on change la tour a acheter
     * 
     * @param tour la nouvelle tour a acheter
     */
    public void setTourAAcheter(Tower tour);

    
    /**
     * Permet d'informer l'écouteur d'une désélection
     */
    public void deselection();
}
