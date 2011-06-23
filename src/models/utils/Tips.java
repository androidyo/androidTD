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

package models.utils;

import i18n.Langue;

/**
 * Classe de gestion des astuces
 * 
 * Les astuces font leur apparitions dans le formulaire de selection d'un terrain en
 * mode solo. Elle est là pour donner des informations diverses au joueur.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 */
public class Tips
{
    private static final String[] astuces = new String[] {
        Langue.getTexte(Langue.ID_TXT_ASTUCE_1),
        Langue.getTexte(Langue.ID_TXT_ASTUCE_2),
        Langue.getTexte(Langue.ID_TXT_ASTUCE_3),
        Langue.getTexte(Langue.ID_TXT_ASTUCE_4),
        Langue.getTexte(Langue.ID_TXT_ASTUCE_5),
        Langue.getTexte(Langue.ID_TXT_ASTUCE_6),
        Langue.getTexte(Langue.ID_TXT_ASTUCE_7),
        Langue.getTexte(Langue.ID_TXT_ASTUCE_8),
    };
     
    /**
     * Permet de recuperer une astuces aleatoirement
     * 
     * @return l'astuce
     */
    public static String getAstuceAleatoirement()
    {
        int i = Tools.tirerNombrePseudoAleatoire(0, astuces.length - 1);
        return astuces[i];   
    }
}
