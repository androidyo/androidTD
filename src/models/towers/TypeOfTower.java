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

package models.towers;

import exceptions.TowerTypeInvalideException;

/**
 * Classe de gestion des types de tour pour les communications réseaux
 * 
 * @author Aurélien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 * @see Tower
 */
public class TypeOfTower
{
	private static final int TOUR_ARCHER        = 1;
	private static final int TOUR_CANON         = 2;
	private static final int TOUR_AA            = 3;
	private static final int TOUR_DE_GLACE      = 4;
	private static final int TOUR_ELECTRIQUE    = 5;
	private static final int TOUR_DE_FEU        = 6;
	private static final int TOUR_D_AIR         = 7;
	private static final int TOUR_DE_TERRE      = 8;
	
	
	/**
     * Permet de recuperer le type d'une tour
     */
    public static int getTypeDeTour(Tower tour)
    {
        if (tour instanceof Tower_Archer)
            return TOUR_ARCHER;
        else if (tour instanceof Tower_Canon)
            return TOUR_CANON;
        else if (tour instanceof Tower_AntiAerial)
            return TOUR_AA;
        else if (tour instanceof Tower_Ice)
            return TOUR_DE_GLACE;
        else if (tour instanceof Tower_Electric)
            return TOUR_ELECTRIQUE;
        else if (tour instanceof Tower_Fire)
            return TOUR_DE_FEU;
        else if (tour instanceof Tower_Air)
            return TOUR_D_AIR;
        else if (tour instanceof Tower_Earth)
            return TOUR_DE_TERRE;
        
        return -1;
    }


    public static Tower getTour(int typeDeCreature) 
        throws TowerTypeInvalideException
    {
        switch(typeDeCreature)
        {
            case TOUR_ARCHER : 
                return new Tower_Archer();
            case TOUR_AA : 
                return new Tower_AntiAerial();
            case TOUR_CANON :
                return new Tower_Canon();
            case TOUR_D_AIR :
                return new Tower_Air();
            case TOUR_DE_FEU : 
                return new Tower_Fire();
            case TOUR_DE_GLACE : 
                return new Tower_Ice();
            case TOUR_ELECTRIQUE :
                return new Tower_Electric();
            case TOUR_DE_TERRE :
                return new Tower_Earth();
            default : 
                throw new TowerTypeInvalideException(
                        "Le type " + typeDeCreature + " est invalide");  
        }
    }
}
