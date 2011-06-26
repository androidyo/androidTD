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
public class SimpleFiveVersus extends Field
{
    private static final long serialVersionUID = 1L;
    public final static Image IMAGE_DE_FOND;
    public final static Image IMAGE_MENU;
    public final static File  FICHIER_MUSIQUE_DE_FOND;
    public final static String NOM = "Simple Versus de 2 à 5 joueurs";

    static
    {
        FICHIER_MUSIQUE_DE_FOND = new File("snd/ambient/Filippo Vicarelli - The War Begins.mp3");
        
        IMAGE_MENU    = Toolkit.getDefaultToolkit().getImage(
                                          "img/cartes/menu_principal/elementTD.png");
    	IMAGE_DE_FOND = Toolkit.getDefaultToolkit().getImage(
    	                                                 "img/cartes/simpleFiveVersus.png");
    }
	
    /**
     * Constructeur d'un terrain ElementTD selon la celebre map de Blizzard.
     */
    public SimpleFiveVersus (Game jeu) 
    {
        super(  jeu,
                520,  // largeur
                500,  // hauteur
                100,  // nbPiecesOrInitiales
                20,   // nbViesInitiales
                0,    // positionMaillageX
                0,    // positionMaillageY
                520,  // largeurMaillage
                500,  // hauteurMaillage
                GameMode.MODE_VERSUS, // mode de jeu
                new Color(197,148,90), // couleur de fond
                new Color(91,123,43),  // couleur des murs
                IMAGE_DE_FOND, // imageDeFond
                NOM  // nom
          );
 
        nomFichier = getClass().getSimpleName()+".map";
        opaciteMurs = 0.f;
        
        // Création des équipes
        Team e1 = new Team(1,"Les Rouges",Color.RED);
        e1.addZoneDepartCreatures(new Rectangle(20, 0, 80, 20));
        e1.setZoneArrivalCreatures(new Rectangle(20, 480, 80, 20));
        e1.addPlayerLocation(new PlayerLocation(1,new Rectangle(20,0,80,500),Color.RED));
        equipes.add(e1);
        
        Team e2 = new Team(2,"Les Bleus",Color.BLUE);
        e2.addZoneDepartCreatures(new Rectangle(120, 0, 80, 20));
        e2.setZoneArrivalCreatures(new Rectangle(120, 480, 80, 20));
        e2.addPlayerLocation(new PlayerLocation(2,new Rectangle(120,0,80,500),Color.BLUE));
        equipes.add(e2);
        
        Team e3 = new Team(3,"Les Verts",Color.GREEN);
        e3.addZoneDepartCreatures(new Rectangle(220, 0, 80, 20));
        e3.setZoneArrivalCreatures(new Rectangle(220, 480, 80, 20));
        e3.addPlayerLocation(new PlayerLocation(3,new Rectangle(220,0,80,500),Color.GREEN));
        equipes.add(e3);
        
        Team e4 = new Team(4,"Les Jaunes",Color.YELLOW);
        e4.addZoneDepartCreatures(new Rectangle(320, 0, 80, 20));
        e4.setZoneArrivalCreatures(new Rectangle(320, 480, 80, 20));
        e4.addPlayerLocation(new PlayerLocation(4,new Rectangle(320,0,80,500),Color.YELLOW));
        equipes.add(e4);
        
        Team e5 = new Team(5,"Les Noirs",Color.BLACK);
        e5.addZoneDepartCreatures(new Rectangle(420, 0, 80, 20));
        e5.setZoneArrivalCreatures(new Rectangle(420, 480, 80, 20));
        e5.addPlayerLocation(new PlayerLocation(5,new Rectangle(420,0,80,500),Color.BLACK));
        equipes.add(e5);
        
        fichierMusiqueDAmbiance = FICHIER_MUSIQUE_DE_FOND;
        
        /*
         * Définition des murs.
         */
        addWall(new Rectangle(0, 0, 20, 500));
        
        addWall(new Rectangle(100, 0, 20, 500));
        addWall(new Rectangle(200, 0, 20, 500));
        addWall(new Rectangle(300, 0, 20, 500));
        addWall(new Rectangle(400, 0, 20, 500));
        addWall(new Rectangle(500, 0, 20, 500));
    }
}
