/*
  Copyright (C) 2010 Aurelien Da Campo, Lazhar Farjallah, 
  Pierre-Dominique Putallaz

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

package models.map;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import models.game.Game;
import models.game.GameMode;
import models.player.PlayerLocation;
import models.player.Team;

/**
 * Classe de gestion du fameux terrain Element TD repris de chez Blizzard.
 * 
 * Cette classe herite de la classe Terrain de base.
 * 
 * @author Pierre-Dominique Putallaz
 * @author Aurelien Da Campo
 * @author Lazhar Farjallah
 * @version 1.0 | 13 decembre 2009
 * @since jdk1.6.0_16
 * @see Field
 */
public class ElementTD extends Field
{
    private static final long serialVersionUID = 1L;
    public final static Image IMAGE_DE_FOND;
    public final static Image IMAGE_MENU;
    public final static File  FICHIER_MUSIQUE_DE_FOND;
    public final static String NOM = "ElementTD";

    static
    {
        FICHIER_MUSIQUE_DE_FOND = new File("snd/ambient/Filippo Vicarelli - The War Begins.mp3");
        
        IMAGE_MENU    = Toolkit.getDefaultToolkit().getImage(
                                          "img/cartes/menu_principal/elementTD.png");
    	IMAGE_DE_FOND = Toolkit.getDefaultToolkit().getImage(
    	                                                 "img/cartes/elementTD.png");
    }
	
    /**
     * Constructeur d'un terrain ElementTD selon la celebre map de Blizzard.
     */
    public ElementTD (Game jeu) 
    {
        super(  jeu,
                480,  // largeur
                500,  // hauteur
                100,  // nbPiecesOrInitiales
                20,   // nbViesInitiales
                0,    // positionMaillageX
                -40,    // positionMaillageY
                480,  // largeurMaillage
                540,  // hauteurMaillage
                GameMode.MODE_SOLO, // mode de jeu
                new Color(197,148,90), // couleur de fond
                new Color(91,123,43),  // couleur des murs
                IMAGE_DE_FOND, // imageDeFond
                NOM  // nom
          );
        
        opaciteMurs = 0.f;
        
        // Création des équipes
        Team e = new Team(1,"Equipe par defaut",Color.BLACK);
        e.addZoneDepartCreatures(new Rectangle(110, -40, 80, 20));
        e.setZoneArrivalCreatures(new Rectangle(250, 0, 40, 40));
        e.addPlayerLocation(new PlayerLocation(1,new Rectangle(0,0,480,500)));
        equipes.add(e);
        
        
        fichierMusiqueDAmbiance = FICHIER_MUSIQUE_DE_FOND;
        
        /*
         * 定义地图中的墙壁.
         */
        addWall(new Rectangle(0, -40, 100, 60));
        addWall(new Rectangle(0, 20, 20, 480));
        addWall(new Rectangle(20, 480, 440, 20));
        addWall(new Rectangle(460, 20, 20, 480));
        addWall(new Rectangle(320, -40, 160, 60));
        addWall(new Rectangle(200, -40, 20, 140));
        addWall(new Rectangle(120, 100, 240, 20));
        addWall(new Rectangle(120, 120, 20, 20));
        addWall(new Rectangle(340, 120, 20, 260));
        addWall(new Rectangle(120, 360, 220, 20));
        addWall(new Rectangle(20, 240, 220, 20));
        addWall(new Rectangle(220, 220, 20, 20));
    }
}
