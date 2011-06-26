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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import models.game.Game;
import models.game.GameMode;
import models.player.PlayerLocation;
import models.player.Team;

/**
 * Classe de gestion du fameux terrain Element TD en Versus 4 joueurs
 * 
 * Cette classe herite de la classe Terrain de base.
 * 
 * @author Aurelien Da Campo
 * @author Lazhar Farjallah
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 * @see Field
 */
public class ElementTD_Versus_4 extends Field
{
    private static final long serialVersionUID = 1L;
    public final static Image IMAGE_DE_FOND;
    public final static Image IMAGE_MENU;
    public final static File  FICHIER_MUSIQUE_DE_FOND;
    public final static String NOM = "Element TD Versus";

    static
    {
        FICHIER_MUSIQUE_DE_FOND = new File("snd/ambient/Filippo Vicarelli - The War Begins.mp3");
        
        IMAGE_MENU    = Toolkit.getDefaultToolkit().getImage(
                                          "img/cartes/menu_principal/elementTD.png");
    	IMAGE_DE_FOND = Toolkit.getDefaultToolkit().getImage(
    	                                                 "img/cartes/elementTDVersus4.jpg");
    }
	
    /**
     * Constructeur d'un terrain ElementTD selon la celebre map de Blizzard.
     */
    public ElementTD_Versus_4 (Game jeu) 
    {
        super(  jeu,
                960,  // largeur
                1000,  // hauteur
                100,  // nbPiecesOrInitiales
                10,   // nbViesInitiales
                0,    // positionMaillageX
                0,    // positionMaillageY
                960,  // largeurMaillage
                1000,  // hauteurMaillage
                GameMode.MODE_VERSUS, // mode de jeu
                new Color(197,148,90), // couleur de fond
                new Color(91,123,43),  // couleur des murs
                IMAGE_DE_FOND, // imageDeFond
                NOM  // nom
          );
 
        
        taillePanelTerrain = new Dimension(480,500);
        
        nomFichier = getClass().getSimpleName()+".map";
        opaciteMurs = 0.f;
        
        // Création des équipes
        Team e1 = new Team(1,"Les verts",Color.GREEN);
        e1.addZoneDepartCreatures(new Rectangle(110, 0, 80, 20));
        e1.setZoneArrivalCreatures(new Rectangle(230, 0, 80, 20));
        e1.addPlayerLocation(new PlayerLocation(1,new Rectangle(0,0,480,500),Color.GREEN));
        teams.add(e1);
        
        Team e2 = new Team(2,"Les rouges",Color.RED);
        e2.addZoneDepartCreatures(new Rectangle(590, 0, 80, 20));
        e2.setZoneArrivalCreatures(new Rectangle(710, 0, 80, 20));
        e2.addPlayerLocation(new PlayerLocation(2,new Rectangle(480,0,480,500),Color.RED));
        teams.add(e2);
        
        Team e3 = new Team(3,"Les bleus",Color.BLUE);
        e3.addZoneDepartCreatures(new Rectangle(110, 500, 80, 20));
        e3.setZoneArrivalCreatures(new Rectangle(230, 500, 80, 20));
        e3.addPlayerLocation(new PlayerLocation(3,new Rectangle(0,500,480,500),Color.BLUE));
        teams.add(e3);
        
        Team e4 = new Team(4,"Les jaunes",Color.YELLOW);
        e4.addZoneDepartCreatures(new Rectangle(590,500, 80, 20));
        e4.setZoneArrivalCreatures(new Rectangle(710, 500, 80, 20));
        e4.addPlayerLocation(new PlayerLocation(4,new Rectangle(480,500,480,500),Color.YELLOW));
        teams.add(e4);
        
        
        // musique
        fichierMusiqueDAmbiance = FICHIER_MUSIQUE_DE_FOND;
         
        // création des murs
        creerMurs(0,0);
        creerMurs(480,0);
        creerMurs(0,500);
        creerMurs(480,500);  
    }

    private void creerMurs(int decalageX, int decalageY)
    {
        /*
         * Définition des murs du labyrinthe.
         */
        addWall(new Rectangle(20+decalageX, 0+decalageY, 80, 20));
        addWall(new Rectangle(0+decalageX, 0+decalageY, 20, 500));
        addWall(new Rectangle(20+decalageX, 480+decalageY, 440, 20));
        addWall(new Rectangle(460+decalageX, 0+decalageY, 20, 500));
        addWall(new Rectangle(320+decalageX, 0+decalageY, 140, 20));
        
        addWall(new Rectangle(200+decalageX, -0+decalageY, 20, 100));
        
        addWall(new Rectangle(120+decalageX, 100+decalageY, 240, 20));
        addWall(new Rectangle(120+decalageX, 120+decalageY, 20, 20));
        addWall(new Rectangle(340+decalageX, 120+decalageY, 20, 260));
        addWall(new Rectangle(120+decalageX, 360+decalageY, 220, 20));
        addWall(new Rectangle(20+decalageX, 240+decalageY, 220, 20));
        addWall(new Rectangle(220+decalageX, 220+decalageY, 20, 20));
    }
}
