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

package utils;

/**
 * 配置信息的管理类.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juin 2010
 */
public class Configuration
{
    private static final String CFG = "cfg/config.cfg";
    private static ConfigurationFile config = new ConfigurationFile(CFG);
    
    // COMMANDES
    public static final String DEPL_HAUT            = "KC_DEPL_HAUT";
    public static final String DEPL_BAS             = "KC_DEPL_BAS";
    public static final String DEPL_DROITE          = "KC_DEPL_DROITE";
    public static final String DEPL_GAUCHE          = "KC_DEPL_GAUCHE"; 
    public static final String LANCER_VAGUE         = "KC_LANCER_VAGUE"; 
    public static final String VENDRE_TOUR          = "KC_VENDRE_TOUR"; 
    public static final String AMELIO_TOUR          = "KC_AMELIO_TOUR"; 
    public static final String PAUSE                = "KC_PAUSE"; 
    public static final String SUIVRE_CREATURE      = "KC_SUIVRE_CREATURE"; 
    public static final String AUG_VIT_JEU          = "KC_AUG_VIT_JEU"; 
    public static final String DIM_VIT_JEU          = "KC_DIM_VIT_JEU"; 
    
    // STYLES
    public static final String COULEUR_DE_FOND_P    = "COULEUR_DE_FOND_P";
    public static final String COULEUR_DE_FOND_S    = "COULEUR_DE_FOND_S";
    public static final String COULEUR_DE_FOND_B    = "COULEUR_DE_FOND_B";
    public static final String COULEUR_TEXTE_P      = "COULEUR_TEXTE_P";
    public static final String COULEUR_TEXTE_S      = "COULEUR_TEXTE_S";
    public static final String COULEUR_TEXTE_B      = "COULEUR_TEXTE_B";
    
    // RESEAU
    private static String IP_SE;
    private static int PORT_SE;
    private static int PORT_SJ;
    private static int PORT_SJ_JD;
  
    // JOUEUR
    private static String PSEUDO_JOUEUR;
    //private final static String LANGUE = "FR";
    
    static
    {
        IP_SE           = config.getProperty("IP_SE");
        PORT_SE         = Integer.parseInt(config.getProperty("PORT_SE"));
        PORT_SJ         = Integer.parseInt(config.getProperty("PORT_SJ"));
        PORT_SJ_JD      = Integer.parseInt(config.getProperty("PORT_SJ_JD"));
        PSEUDO_JOUEUR   = config.getProperty("PSEUDO_JOUEUR");
    }

    public static String getIpSE()
    {
        return IP_SE;
    }
    
    public static int getPortSE()
    {
        return PORT_SE;
    }
    
    public static int getPortSJ()
    {
        return PORT_SJ;
    }
    
    public static String getPseudoJoueur()
    {
        return PSEUDO_JOUEUR;
    }
    
    public static void setIpSE(String iP_SE)
    {
        IP_SE = iP_SE;
        
        config.setProperty("IP_SE", IP_SE);
    }
    
    public static void setPortSE(int pORT_SE)
    {
        PORT_SE = pORT_SE;
        
        config.setProperty("PORT_SE", PORT_SE+"");
    }

    public static void setPortSJ(int PORT_SJ_2)
    {
        PORT_SJ = PORT_SJ_2;
        
        config.setProperty("PORT_SJ", PORT_SJ+"");
    }
    
    public static void setPseudoJoueur(String PSEUDO_JOUEUR_2)
    {
        PSEUDO_JOUEUR = PSEUDO_JOUEUR_2;
        
        config.setProperty("PSEUDO_JOUEUR", PSEUDO_JOUEUR);
    }

    public static void setPortSJ_JD(int PORT_SJ_JD_2)
    {
        PORT_SJ_JD = PORT_SJ_JD_2;
        
        config.setProperty("PORT_SJ_JD", PORT_SJ_JD+"");
    }

    public static int getPortSJ_JD()
    {
        return PORT_SJ_JD;
    }

    public static int getCmdAmeliorer()
    {
        return config.getProperty("KC_AMELIO_TOUR").charAt(0);
    }
    
    public static String getProprety(String cle)
    {
        return config.getProperty(cle);
    }
    
    public static void setProperty(String cle, String valeur)
    {
        config.setProperty(cle,valeur);
    }
    
    public static int getKeyCode(String cle)
    {
        return Integer.parseInt(config.getProperty(cle));
    }
}
