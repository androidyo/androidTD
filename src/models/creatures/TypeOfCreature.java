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
/**
 * Classe de gestion de type de créatures pour le passage par le réseau.
 * 管理生物类型
 * 
 * @author Aurélien Da Campo
 * @author Pierre-Dominique Putallaz
 * @version 1.0 | 27 novemenbre 2009
 * @since jdk1.6.0_16
 * @see Creature
 */
public class TypeOfCreature
{
    private static final int MOUTON          = 0;
    private static final int ARAIGNEE        = 1;
    private static final int PIGEON          = 2;
    private static final int RHINOCEROS      = 3;
    private static final int AIGLE           = 4;
    private static final int ELEPHANT        = 5;
    private static final int GRANDE_ARAIGNEE = 6;

	public static Creature getCreature(int typeDeCreature, int noVague, boolean invincible)
	{
	    Creature c = null;
	    switch(typeDeCreature)
        {
	        
            case MOUTON :
                c = new Sheep((long) (WaveOfCreatures.fSante(noVague)), 5, WaveOfCreatures.VITESSE_CREATURE_NORMALE); 
                break;
            case ARAIGNEE : 
                c = new Spider((long) (WaveOfCreatures.fSante(noVague)*1.2), 10, WaveOfCreatures.VITESSE_CREATURE_RAPIDE);
                break;
            case PIGEON :
                c = new Pigeon((long) (WaveOfCreatures.fSante(noVague)*1.4), 15, WaveOfCreatures.VITESSE_CREATURE_NORMALE);    
                break;
            case RHINOCEROS :
                c = new Rhinoceros((long) (WaveOfCreatures.fSante(noVague)*1.6), 30, WaveOfCreatures.VITESSE_CREATURE_RAPIDE);
                break;
            case AIGLE : 
                c = new Eagle((long) (WaveOfCreatures.fSante(noVague)*1.8), 60, WaveOfCreatures.VITESSE_CREATURE_NORMALE); 
                break;
            case ELEPHANT :
                c = new Elephant((long) (WaveOfCreatures.fSante(noVague)*2.0), 100, WaveOfCreatures.VITESSE_CREATURE_LENTE);
                break;
            case GRANDE_ARAIGNEE :
                c = new BigSpider((long) (WaveOfCreatures.fSante(noVague)*2.2), 150, WaveOfCreatures.VITESSE_CREATURE_RAPIDE); 
                break;
            default :
                return null;
        }
	    
	    c.setInvincible(invincible);
	    
        return c;
	}

    public static int getTypeCreature(Creature creature)
    {
        if (creature instanceof Eagle)
            return AIGLE;
        else if (creature instanceof Spider)
            return ARAIGNEE;
        else if (creature instanceof Elephant)
            return ELEPHANT;
        else if (creature instanceof Sheep)
            return MOUTON;
        else if (creature instanceof Pigeon)
            return PIGEON;
        else if (creature instanceof Rhinoceros)
            return RHINOCEROS;
        else if (creature instanceof BigSpider)
            return GRANDE_ARAIGNEE;
        
        return -1;
    }
}
