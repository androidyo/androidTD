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
 * Classe de gestion du terrain WaterWorld.
 * 
 * Cette classe herite de la classe Terrain de base.
 * 
 * @author Pierre-Dominique Putallaz
 * @author Aurelien Da Campo
 * @author Lazhar Farjallah
 * @version 1.0 | 14 décembre 2009
 * @since jdk1.6.0_16
 * @see Field
 */
public class WaterWorld extends Field
{
    
    private static final long serialVersionUID = 1L;
    public static final Image IMAGE_DE_FOND;
	public static final Image IMAGE_MENU;
	public final static File FICHIER_MUSIQUE_DE_FOND;
	public final static String NOM = "WaterWorld";
	
    static
    {
        FICHIER_MUSIQUE_DE_FOND = new File("snd/ambient/Defnael - Combat.mp3");
        
        IMAGE_MENU    = Toolkit.getDefaultToolkit().getImage(
                                              "img/cartes/menu_principal/water.png");
    	IMAGE_DE_FOND = Toolkit.getDefaultToolkit().getImage("img/cartes/water.png");
    }

    /**
     * Constructeur d'un terrain TerrainEau.
     */
    public WaterWorld (Game jeu) {
        super(jeu,
              500,  // largeur
              500,  // hauteur
              150,  // nbPiecesOrInitiales
              20,   // nbViesInitiales
              -40,    // positionMaillageX
              0,    // positionMaillageY
              540,  // largeurMaillage
              500,  // hauteurMaillage
              GameMode.MODE_SOLO, // mode de jeu
              new Color(150,150,150), // couleur de fond
              new Color(63,131,140), // couleur des murs
              IMAGE_DE_FOND, // imageDeFond
              NOM   // nom
        );

        opaciteMurs = 0.f;
        
        // Création des équipes
        Team e = new Team(1,"Equipe par defaut",Color.BLACK);
        e.addZoneDepartCreatures(new Rectangle(-30, 30, 20, 80));
        e.setZoneArrivalCreatures(new Rectangle(460, 410, 40, 40));
        e.addPlayerLocation(new PlayerLocation(1,new Rectangle(0,0,500,500)));
        teams.add(e);
        
        fichierMusiqueDAmbiance = FICHIER_MUSIQUE_DE_FOND;
        
        /*
         * Définition des murs du terrain.
         */
        addWall(new Rectangle(0, 0, 220, 20));
        addWall(new Rectangle(220, 0, 60, 90));
        addWall(new Rectangle(280, 0, 220, 20));
        addWall(new Rectangle(480, 20, 20, 200));
        addWall(new Rectangle(410, 220, 90, 60));
        addWall(new Rectangle(480, 280, 20, 100));
        addWall(new Rectangle(280, 480, 220, 20));
        addWall(new Rectangle(220, 410, 60, 90));
        addWall(new Rectangle(0, 480, 220, 20));
        addWall(new Rectangle(20, 220, 70, 60));
        addWall(new Rectangle(-40, 120, 60, 380));
        addWall(new Rectangle(130, 220, 240, 60));
        addWall(new Rectangle(220, 130, 60, 90));
        addWall(new Rectangle(220, 280, 60, 90));
    }
}
