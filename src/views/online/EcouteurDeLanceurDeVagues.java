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

package views.online;

import exceptions.MoneyLackException;
import models.creatures.WaveOfCreatures;


/**
 * Classe Observeur du lancer de vague de créatures
 * 
 * @author Aurélien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public interface EcouteurDeLanceurDeVagues
{
    /**
     * Permet d'informer l'écouteur qu'une vague veut etre lancée
     * 
     * @param vague la vague a lancer
     * @throws MoneyLackException 
     */
    public void lancerVague(WaveOfCreatures vague) throws MoneyLackException;

    /**
     * Permet d'informer l'écouteur qu'une vague coute trop chere
     */
    public void erreurPasAssezDArgent();

}
