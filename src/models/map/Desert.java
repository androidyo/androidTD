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
 * Classe de gestion d'un terrain dans le desert.
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
public class Desert extends Field
{
    private static final long serialVersionUID = 1L;
    public final static Image IMAGE_DE_FOND;
	public final static Image IMAGE_MENU;
	public final static File FICHIER_MUSIQUE_DE_FOND;
	public final static String NOM = "Desert";
	
    static
    {
        FICHIER_MUSIQUE_DE_FOND = new File("snd/ambient/Petite Viking - Battle.mp3");
        
        IMAGE_MENU = Toolkit.getDefaultToolkit().getImage(
                                              "img/cartes/menu_principal/objectif.png");
    	IMAGE_DE_FOND = Toolkit.getDefaultToolkit().getImage("img/cartes/objectif.png");
    }
	
	/**
	 * Constructeur du terrain dans le desert
	 */
	public Desert(Game jeu)
	{
		super(  jeu,
		        500,      // largeur
                500,      // hauteur
                140,    // nbPiecesOrInitiales
                20,       // nbViesInitiales
                0,      // positionMaillageX
                0,        // positionMaillageY
                540,      // largeurMaillage
                500,      // hauteurMaillage
                GameMode.MODE_SOLO, // mode de jeu
                new Color(161,72,0), // couleur de fond
                new Color(150,150,150), // couleur des murs
                IMAGE_DE_FOND, // imageDeFond
                NOM       // nom
          );
		
		opaciteMurs = 0.f;
		
		// Création des équipes
		Team e = new Team(1,"Equipe par defaut",Color.BLACK);
        e.ajouterZoneDepartCreatures(new Rectangle(510,40,20,60));
        e.setZoneArriveeCreatures(new Rectangle(0,410,40,40));
        e.ajouterEmplacementJoueur(new PlayerLocation(1,new Rectangle(0,0,500,500)));
        equipes.add(e);

        
        fichierMusiqueDAmbiance = FICHIER_MUSIQUE_DE_FOND;
        
        
		// 地图四周的墙壁
		addWall(new Rectangle(0,0,20,380)); 		// 左
		addWall(new Rectangle(0,0,500,20)); 		// 上
		addWall(new Rectangle(0,480,500,20)); 	// 下
		addWall(new Rectangle(480,120,60,380)); 	// 右
		
		// 城墙的四角成形
		// 左 - 上
		addWall(new Rectangle(120,120,60,20));
		addWall(new Rectangle(120,120,20,60));
		
		// 右 - 上
		addWall(new Rectangle(320,120,60,20));
		addWall(new Rectangle(360,120,20,60));
		
		// 左 - 下
		addWall(new Rectangle(120,320,20,60));
		addWall(new Rectangle(120,360,60,20));
		
		// 右 - 下
		addWall(new Rectangle(360,320,20,60));
		addWall(new Rectangle(320,360,60,20));
	}
}
