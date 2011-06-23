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

package views;

import java.awt.Color;

import utils.Configuration;

/**
 * Classe de gestion des styles graphique
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class LookInterface
{
    // COULEURS PAR DEFAUT
    public static final Color DEF_COULEUR_DE_FOND_PRI     = new Color(62,140,28);
    public static final Color DEF_COULEUR_TEXTE_PRI       = new Color(20,60,0);
    
    public static final Color DEF_COULEUR_DE_FOND_SEC     = new Color(90,180,50);
    public static final Color DEF_COULEUR_TEXTE_SEC       = new Color(20,60,0);
    
    public static final Color DEF_COULEUR_DE_FOND_BTN     = new Color(20, 20, 20);
    public static final Color DEF_COULEUR_TEXTE_BTN       = Color.WHITE;
    
    // COULEURS
    public static Color COULEUR_DE_FOND_PRI     = DEF_COULEUR_DE_FOND_PRI;
    public static Color COULEUR_TEXTE_PRI       = DEF_COULEUR_TEXTE_PRI;
    
    public static Color COULEUR_DE_FOND_SEC     = DEF_COULEUR_DE_FOND_SEC;
    public static Color COULEUR_TEXTE_SEC       = DEF_COULEUR_TEXTE_SEC;
    
    public static Color COULEUR_DE_FOND_BTN     = DEF_COULEUR_DE_FOND_BTN;
    public static Color COULEUR_TEXTE_BTN       = DEF_COULEUR_TEXTE_BTN;
    
    public static Color COULEUR_ERREUR          = Color.ORANGE;
    public static Color COULEUR_SUCCES          = new Color(0,200,20);
    
    static
    {
        COULEUR_DE_FOND_PRI   = new Color(Integer.parseInt(Configuration.getProprety(Configuration.COULEUR_DE_FOND_P)));
        COULEUR_TEXTE_PRI     = new Color(Integer.parseInt(Configuration.getProprety(Configuration.COULEUR_TEXTE_P)));
        
        COULEUR_DE_FOND_SEC   = new Color(Integer.parseInt(Configuration.getProprety(Configuration.COULEUR_DE_FOND_S)));
        COULEUR_TEXTE_SEC     = new Color(Integer.parseInt(Configuration.getProprety(Configuration.COULEUR_TEXTE_S)));
        
        COULEUR_DE_FOND_BTN   = new Color(Integer.parseInt(Configuration.getProprety(Configuration.COULEUR_DE_FOND_B)));
        COULEUR_TEXTE_BTN     = new Color(Integer.parseInt(Configuration.getProprety(Configuration.COULEUR_TEXTE_B)));
    
        /*
            System.out.println(Outils.ColorToHexa(COULEUR_DE_FOND_PRI));
            System.out.println(Outils.ColorToHexa(COULEUR_TEXTE_PRI));
            System.out.println(Outils.ColorToHexa(COULEUR_DE_FOND_SEC));
        */ 
    }
}
