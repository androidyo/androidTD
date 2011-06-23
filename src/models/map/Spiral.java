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
 * Classe de gestion d'un terrain en spiral.
 * 
 * Cette classe herite de la classe Terrain de base.
 * 
 * @author Pierre-Dominique Putallaz
 * @author Aurélien Da Campo
 * @author Lazhar Farjallah
 * @version 1.0 | 27 novemenbre 2009
 * @since jdk1.6.0_16
 * @see Field
 */
public class Spiral extends Field
{
    private static final long serialVersionUID = 1L;
    public final static Image IMAGE_DE_FOND;
    public final static Image IMAGE_MENU;
    public final static File  FICHIER_MUSIQUE_DE_FOND;
    public final static String NOM = "Spiral";
 
    static
    {
        FICHIER_MUSIQUE_DE_FOND = new File("snd/ambient/Defnael - Tork.mp3");
        
        IMAGE_MENU    = Toolkit.getDefaultToolkit().getImage(
                                              "img/cartes/menu_principal/spirale.png");
    	IMAGE_DE_FOND = Toolkit.getDefaultToolkit().getImage("img/cartes/spirale.png");
    }
	
	/**
	 * Constructeur du terrain dans le desert
	 */
	public Spiral(Game jeu)
	{
		super(  jeu,
		        480,  // largeur
                500,  // hauteur
                100,  // nbPiecesOrInitiales
                20,   // nbViesInitiales
                0,    // positionMaillageX
                0,    // positionMaillageY
                540,  // largeurMaillage
                500,  // hauteurMaillage
                GameMode.MODE_SOLO, // mode de jeu
                new Color(150,150,150), // couleur de fond
                new Color(140,120,75),  // couleur des murs
                IMAGE_DE_FOND, // imageDeFond
                NOM  // nom
          );
		
		opaciteMurs = 0.f;
		
		// Création des équipes
		Team e = new Team(1,"Equipe par defaut",Color.BLACK);
        e.ajouterZoneDepartCreatures(new Rectangle(500,40,20,80));
        e.setZoneArriveeCreatures(new Rectangle(300,290,40,40));
        e.ajouterEmplacementJoueur(new PlayerLocation(1,new Rectangle(0,0,480,500)));
        equipes.add(e);
		
        fichierMusiqueDAmbiance = FICHIER_MUSIQUE_DE_FOND;
        
		/* definition des murs du labyrinthe :
		 
		 		 1
		|------------------
		|
		|           5
		|	 |-------------
		|	 |	   9	  |
	  2 |	6|  -----|	  |
		|	 |		 | 8  | 4
		|    |-------|	  |
		|		 7		  |
		|-----------------|
				3
		*/
        
		addWall(new Rectangle(20,0,460,20)); 	// 1
		addWall(new Rectangle(0,0,20,500));	 	// 2	
		addWall(new Rectangle(20,480,440,20)); 	// 3
		addWall(new Rectangle(460,140,80,360));  // 4
		addWall(new Rectangle(140,140,340,20));  // 5
		addWall(new Rectangle(120,140,20,240));	// 6
		addWall(new Rectangle(140,360,200,20));	// 7
		addWall(new Rectangle(340,240,20,140));	// 8
		addWall(new Rectangle(240,240,100,20));	// 9
	}
}
