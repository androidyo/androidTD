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

package views.common;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.*;
import javax.swing.*;

import utils.Configuration;
import views.ManageFonts;
import views.LookInterface;
import models.animations.Animation;
import models.creatures.Creature;
import models.game.Game;
import models.grid.Node;
import models.player.Team;
import models.player.Player;
import models.towers.Tower;
import models.utils.Timer;

/**
 * Panel d'affichage du terrain de jeu.
 * <p>
 * Ce panel affiche la zone de jeu avec tous les elements que contient le terrain.
 * <p>
 * Celle-ci affichera les tours avec les créatures et gèrera le positionnement
 * des tours et la selection des tours.
 * 
 * @author Aurelien Da Campo
 * @version 2.2 | mai 2010
 * @since jdk1.6.0_16
 * @see JPanel
 * @see Runnable
 * @see MouseListener
 * @see MouseMotionListener
 */
public class Panel_Terrain extends JPanel implements Runnable, 
													 MouseListener,
													 MouseMotionListener,
													 KeyListener,
													 MouseWheelListener
{
	private static final long serialVersionUID = 1L;
	
	//-------------------------
	//-- proprietes du panel --
	//-------------------------
	private static final int xOffsetPseudo = 10, 
                             yOffsetPseudo = 30;
	
	/**
	 * Hauteur de la barre de vie d'une creature. (en pixels)
	 */
	private static final int HAUTEUR_BARRE_VIE = 4;
	
	/** 
	 * Coefficient de largeur de la barre de vie d'une creature.
	 * (en % de la largeur de la creature)
	 */
	private static final float COEFF_LARGEUR_BARRE_VIE = 1.5f; // 150%

	/**
	 * largeur d'un case du maillage pour le positionnement des tours
	 */
	private static final int CADRILLAGE = 10; // unite du cadriallage en pixel
	
	/**
	 * Coefficient de taille du rendu final du terrain
	 */
	protected double coeffTaille = 1.0;
	
	/**
	 * Taille des marges pour le scrolling automatique sur les bords du panel
	 */
	private final int MARGES_DEPLACEMENT = 20;
	
	/**
     * Décalages du rendu par rapport à la position 0 du panel (scale)
     */
	protected int decaleX = 0,
	            decaleY = 0;
	
	/**
	 * Marges internes du chateau pour coloration du centre (couleur de l'équipe)
	 */
	private static final int MARGES_CHATEAU = 5;
	
	//---------------------------
	//-- preferences de dessin --
	//---------------------------
	/**
	 * Crayon pour un trait tillé
	 */
	// 2 pixels remplis suivi de 2 pixels transparents
	private static final float [] DASHES   = {2.0F, 2.0F};
	
	private static final BasicStroke TRAIT_TILLE = new BasicStroke(
	            1.0f,BasicStroke.CAP_ROUND, 
	            BasicStroke.JOIN_MITER, 
	            10.0F, DASHES, 0.F);
	
	
	private static final float [] DASHES_EPAIS   = {8.0F, 4.0F};
	private static final BasicStroke TRAIT_TILLE_EPAIS = new BasicStroke(
            4.0f,BasicStroke.CAP_ROUND, 
            BasicStroke.JOIN_ROUND, 
            10.0F, DASHES_EPAIS, 0.F);

	Stroke traitTmp;
	
	// 0.0f = 100% transparent et 1.0f vaut 100% opaque.
	private static final float ALPHA_PERIMETRE_PORTEE  = .6f;
	private static final float ALPHA_SURFACE_PORTEE    = .3f;
	private static final float ALPHA_MAILLAGE   	   = .4f;
	private static final float ALPHA_SURFACE_ZONE_DA   = .5f;
	private static final float ALPHA_TOUR_A_AJOUTER    = .7f;
	private static final float ALPHA_CHEMIN_CREATURE   = .5f;
	private static final float ALPHA_SURFACE_MUR_DEBUG = .8f;
	private static final float ALPHA_QUADRILLAGE     = .2f;
	
	private static final Color COULEUR_ZONE_DEPART 	   = Color.GREEN;
	private static final Color COULEUR_ZONE_ARRIVEE    = Color.RED;
	private static final Color COULEUR_MAILLAGE 	   = Color.WHITE;
	private static final Color COULEUR_SANTE 		   = Color.GREEN;
	private static final Color COULEUR_CHEMIN 		   = Color.BLUE;
	private static final Color COULEUR_CREATURE_SANS_IMAGE = Color.YELLOW;
	private static final Color COULEUR_SELECTION	   = Color.WHITE;
	private static final Color COULEUR_POSE_IMPOSSIBLE = Color.RED;
	private static final Color COULEUR_RAYON_PORTEE    = Color.WHITE;
	private static final Color COULEUR_NIVEAU 		   = Color.WHITE;
	private static final Color COULEUR_NIVEAU_PERIMETRE = Color.YELLOW;
    private static final Color COULEUR_QUADRILLAGE      = Color.BLACK;
	
    private static final Image I_CHATEAU = Toolkit.getDefaultToolkit().getImage("img/tours/chateau.png");
    private static final Image I_CADRILLAGE = Toolkit.getDefaultToolkit().getImage("img/animations/cadrillage.png");

	/**
	 * Thread de gestion du rafraichissement de l'affichage
	 */
	private Thread thread;
	
	/**
	 * Temps de repose dans la boucle d'affichage
	 */
	private static final int TEMPS_REPOS_THREAD = 20;

	/**
	 * Marge autour du terrain pour éviter des bugs de déplacements en 
	 * dehors de la zone de dessin
	 */
    private static final int MARGE_UNIVERS = 3000;

    /**
     * Décalage lors de déplacement avec le clavier
     * 
     * TODO faire avec une acceleration
     */
    private static final int DECALAGE_CLAVIER = 6;

	/**
	 * Position exacte de la souris sur le terrain
	 */
    protected int sourisX, sourisY;
		
	/**
	 * Permet de stocker l'endroit du debut de l'agrippage
	 */
	protected int sourisGrabX, sourisGrabY;
	
	/**
	 * Permet de savoir le decalage effectuer depuis le position
	 * du debut de l'agrippage
	 */
	protected int decaleGrabX, decaleGrabY;
	
	/**
	 * Position de la souris sur le cadriallage virtuel
	 */
	protected int sourisCaseX, sourisCaseY;
	
	/**
	 * Permet de savoir si la souris est actuellement sur le panel
	 */
	protected boolean sourisSurTerrain;
	
	/**
	 * Le terrain permet de choisir la tour a poser sur le terrain.
	 * Si cette variable est non nulle et que le joueur clique sur le
	 * terrain, la tour a ajouter sera posée.
	 */
	private Tower tourAAjouter;
	
	/**
	 * Lorsque le joueur clique sur une tour, elle devient selectionnee.
	 * Une fois selectionnee des informations sur la tour apparaissent
	 * dans le menu d'interaction. Le joueur pourra alors améliorer ou 
	 * vendre la tour.
	 */
	private Tower tourSelectionnee;
	
	/**
     * Lorsque le joueur clique sur une creature, elle devient selectionnee.
     * Une fois selectionnee des informations sur la creature apparaissent
     * dans le menu d'interaction.
     */
	private Creature creatureSelectionnee;
	
	/**
	 * Reference vers le jeu a gerer
	 */
	protected Game jeu;
	
	/**
	 * Reference vers la fenetre parent
	 */
	private EcouteurDePanelTerrain edpt;

	/**
	 * Activation du mode debug (affichage des murs et formes primitives)
	 */
	private boolean modeDebug;
		
	/**
	 * Permet d'afficher ou non le maillage et les chemins
	 */
	private boolean afficherMaillage;
	
	/**
     * Permet d'afficher ou non les noms des joueurs
     */
    private boolean afficherZonesJoueurs;
	
	/**
	 * Permet d'afficher ou non tous les rayons de portee des tours
	 */
	private boolean afficherRayonsDePortee;
	
	/**
     * Permet d'afficher ou non la zone de départ et d'arrivée
     */
    private boolean afficherZonesDepartArrivee;
	
    /**
     * Permet d'afficher ou non le quadrillage du terrain
     */
    protected boolean afficherQuadrillage;
    
	/**
	 * Etape d'une echelle de zoom
	 */
	private final double ETAPE_ZOOM = 0.2;

	/**
     * Min zoom
     */
    private final double ZOOM_MIN = 0.2;
	
	/**
	 * Stockage du bouton lors d'un aggripement
	 */
    protected int boutonGrab;

    /**
     * Permet de savoir s'il faut centrer la vue sur la creature selectionnee
     */
    private boolean centrerSurCreatureSelectionnee;

    
    private boolean toucheHautPressee;
    private boolean toucheGauchePressee;
    private boolean toucheBasPressee;
    private boolean toucheDroitePressee;

    /**
     * Permet de répéter l'image de fin de tel sorte à recouvrir 
     * toute la surface du terrain
     */
    private boolean repeterImageDeFond = true;

    // FPS
    private Timer timer;
    private int fps;
    private boolean afficherFps = true;

	// curseurs
	private static Cursor curRedimDroite   = new Cursor(Cursor.E_RESIZE_CURSOR);
	private static Cursor curRedimGauche   = new Cursor(Cursor.W_RESIZE_CURSOR);
	private static Cursor curRedimHaut     = new Cursor(Cursor.N_RESIZE_CURSOR);
	private static Cursor curRedimBas      = new Cursor(Cursor.S_RESIZE_CURSOR);
	private static Cursor curNormal        = new Cursor(Cursor.DEFAULT_CURSOR);
	private static Cursor curMainAgripper;
	
	static
	{
	    /*
	    TODO [INFO] curseur transparent
	    int[] pixels = new int[16 * 16];
	    Image image = Toolkit.getDefaultToolkit().createImage(
	            new MemoryImageSource(16, 16, pixels, 0, 16));
	    curTransparent = Toolkit.getDefaultToolkit().createCustomCursor
	                 (image, new Point(0, 0), "curseurInvisible");
	    */
	    
	    Image iHandGrab = Toolkit.getDefaultToolkit().getImage("img/icones/hand_grab.gif");   
	    curMainAgripper = Toolkit.getDefaultToolkit().createCustomCursor
        (iHandGrab,  new Point(0, 0), "grab"); 
	}

	/**
	 * Constructeur du panel du terrain
	 * 
	 * @param jeu Le jeu a gerer
	 */
	public Panel_Terrain(Game jeu, EcouteurDePanelTerrain edpt)
	{ 
	    // sauvegarde du jeu
        this.jeu      = jeu;
        this.edpt     = edpt;
        
        setPreferredSize(jeu.getTerrain().getTaillePanelTerrain());
        setFocusable(true);
        
        // Centrage sur la zone de construction du joueur
        if(jeu.getJoueurPrincipal() != null)
        {
            if(jeu.getJoueurPrincipal().getEmplacement() != null)
            {
                Rectangle zoneConstruction = jeu.getJoueurPrincipal().getEmplacement().getZoneDeConstruction();
                centrerSur((int)zoneConstruction.getCenterX(),(int)zoneConstruction.getCenterY());
            }
        }
        
        // ajout des ecouteurs
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        
        // demarrage du thread de rafraichissement de l'affichage
        thread = new Thread(this);
        thread.start();
    }
	
	/**
     * Permet de modifier la tour selectionnee depuis l'exterieur de l'objet
     * 
     * @param tour le tour a selectionnee
     */
    public void setTourSelectionnee(Tower tour)
    {
        tourSelectionnee = tour;
    }
	
	/**
	 * Permet de modifier la tour a ajouter depuis l'exterieur de l'objet
	 * 
	 * @param tour la tour sélectionnée
	 */
	public void setTourAAjouter(Tower tour)
	{
		
	    Point p = getCoordoneeSurTerrainOriginal(sourisCaseX, sourisCaseY);
	    
	    // mise a jour de la position de la tour
	    tour.setLocation(p.x, p.y);
		
	    // la tour devient la tour a ajouter
		tourAAjouter = tour;

		// s'il y a un tour a ajouter, il n'y pas de tour selectionnee !
		if(tourAAjouter != null)
			tourSelectionnee = null;
	}
	
	/**
	 * Permet de recuperer la creature selectionnee
	 * 
	 * @return la creature selectionnee
	 */
	public Creature getCreatureSelectionnee()
    {
        return creatureSelectionnee;
    }
	
	/**
     * Permet de modifier la creature selectionnee depuis l'exterieur de l'objet
     * 
     * @param creature la creature a selectionnee
     */
    public void setCreatureSelectionnee(Creature creature)
    {
        creatureSelectionnee = creature;
    }
	
	/**
     * Permet de tout deselectionner
     */
    public void toutDeselectionner()
    {
        tourAAjouter        = null;
        tourSelectionnee    = null;
    }
    
    /**
     * Permet de basculer de l'affichage au non affichage du maillage et vis versa.
     * @return l'etat actuel (true si afficher et false sinon)
     */
    public boolean basculerAffichageMaillage()
    {
        return afficherMaillage = !afficherMaillage;
    }
    
    /**
     * Permet de basculer de l'affichage au non affichage des rayons de portee 
     * et vis versa.
     * @return l'etat actuel (true si afficher et false sinon)
     */
    public boolean basculerAffichageRayonPortee()
    {
        return afficherRayonsDePortee = !afficherRayonsDePortee;
    }
    
    /**
     * Permet de basculer ou non en mode d'affichage des zones 
     * 
     * @return true si le mode est activé, false sinon
     */
    public boolean basculeraffichageZonesDepartArrivee()
    {
        return afficherZonesDepartArrivee = !afficherZonesDepartArrivee;
    }

    /**
     * Permet de basculer ou non en mode debug
     * 
     * @return true si le mode est activé, false sinon
     */
    public boolean basculerModeDebug()
    {
        return modeDebug = !modeDebug;
    }
    
    /**
     * Permet d'activer / désactiver le mode d'affichage des zones des joueurs 
     */
    public boolean basculerAffichageZonesJoueurs()
    {
        return afficherZonesJoueurs = !afficherZonesJoueurs;
    }

    /**
     * Permet d'activer / désactiver l'affichage des fps 
     */
    public boolean basculerAffichageFPS()
    {
        return afficherFps = !afficherFps;
    }

	@Override
	public void paintComponent(Graphics g)
	{
	    Graphics2D g2 = (Graphics2D) g;

	    // proprietes du panel
        final int LARGEUR = jeu.getTerrain().getLargeur();
        final int HAUTEUR = jeu.getTerrain().getHauteur();
	    
	    
	    /** Désactivation de l'anti-aliasing */
	    /*
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	    */
	    /** Demande de rendu rapide */
	    /*
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
	    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
	    g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
	    */
	    
        if(toucheHautPressee)
            decaleY += DECALAGE_CLAVIER;
        if(toucheGauchePressee)
            decaleX += DECALAGE_CLAVIER;
        if(toucheBasPressee)
            decaleY -= DECALAGE_CLAVIER;
        if(toucheDroitePressee)
            decaleX -= DECALAGE_CLAVIER;    
	    
	    // echelle du rendu et positionnement
	    g2.scale(coeffTaille, coeffTaille);
	    g2.translate(decaleX, decaleY);
	    
	    //---------------------------
        //-- affichage de l'espace --
        //---------------------------
	    g2.setColor(LookInterface.COULEUR_DE_FOND_SEC);
	    g2.fillRect(
	            -MARGE_UNIVERS, 
	            -MARGE_UNIVERS, 
	            jeu.getTerrain().getLargeur()+2*MARGE_UNIVERS, 
	            jeu.getTerrain().getHauteur()+2*MARGE_UNIVERS);

		//---------------------------------------------
		//-- affichage de l'image ou couleur de fond --
		//---------------------------------------------
	    if(jeu.getTerrain().getImageDeFond() != null && !modeDebug)
		{	
	        Image image = jeu.getTerrain().getImageDeFond();
	        
			if(repeterImageDeFond)
			    for(int l=0;l<jeu.getTerrain().getLargeur();l+=image.getWidth(null))
			        for(int h=0;h<jeu.getTerrain().getHauteur();h+=image.getHeight(null))
			            g2.drawImage(image, l, h, null);
			else
			    g2.drawImage(image, 0, 0, null);
		}
		else
		{
			// couleur de fond
			g2.setColor(jeu.getTerrain().getCouleurDeFond());
			g2.fillRect(0, 0, LARGEUR, HAUTEUR);
		}

		//-------------------------------------
        //-- affichage des animations au sol --
        //-------------------------------------
        jeu.dessinerAnimations(g2, Animation.HAUTEUR_SOL);
		
		
		//-------------------------------------------------
		//-- Affichage de la zone de depart et d'arrivee --
		//-------------------------------------------------
		
		// affichages des zones de départ et arrivée
	    for(Team equipe : jeu.getEquipes())
	    {
	        Rectangle r;
	        
	        if(modeDebug || afficherZonesDepartArrivee)
	        {
		        // dessin de la zone de depart
		        
		        for(int i=0;i<equipe.getNbZonesDepart();i++)    
		        {
		            r = equipe.getZoneDepartCreatures(i);
		            
		            setTransparence(ALPHA_SURFACE_ZONE_DA, g2);
		            g2.setColor(COULEUR_ZONE_DEPART);
                    dessinerZone(r,g2);
                    
                    // numero
                    setTransparence(1.f, g2);
                    g2.setColor(equipe.getCouleur());
                    g2.drawString(i+"", r.x+r.width/2-5, r.y+r.height/2-5);
                    
                    // tour de couleur
                    Stroke tmp = g2.getStroke();
                    g2.setStroke(TRAIT_TILLE_EPAIS);
                    g2.drawRect(r.x, r.y, r.width, r.height);
                    g2.setStroke(tmp);
		        }
	        }
	        
	        // dessin de la zone d'arrivee
	        if(equipe.getZoneArriveeCreatures() != null)
	        {
	            r = equipe.getZoneArriveeCreatures();
	           
	            if(modeDebug)
	            {
	                g2.setColor(COULEUR_ZONE_ARRIVEE);
	                dessinerZone(r,g2);
	                
	                // tour de couleur
	                setTransparence(1.f, g2);
	                g2.setColor(equipe.getCouleur());
	                g2.drawRect(r.x, r.y, r.width, r.height); 
	            }
	            else
	            {
	                // sol de couleur
                    g2.setColor(equipe.getCouleur());
                    g2.fillRect(r.x+MARGES_CHATEAU, r.y+MARGES_CHATEAU, r.width-(2*MARGES_CHATEAU), r.height-(2*MARGES_CHATEAU)); 
	                g2.drawImage(I_CHATEAU, r.x, r.y, r.width, r.height, null);
	            }
	        }
		}
		
		
		if(modeDebug)
		    setTransparence(ALPHA_SURFACE_MUR_DEBUG, g2);
		else
		    setTransparence(jeu.getTerrain().getOpaciteMurs(), g2);
		
	    ArrayList<Rectangle> murs = jeu.getTerrain().getMurs();
        g2.setColor(jeu.getTerrain().getCouleurMurs());
        for(Rectangle mur : murs)
            dessinerZone(mur,g2);
        
        setTransparence(1.f, g2);
		
		
		
		//-------------------------------------------------
        //-- Affichage de la zone de depart et d'arrivee --
        //-------------------------------------------------
  
        if(afficherZonesJoueurs)
        {
            setTransparence(ALPHA_SURFACE_ZONE_DA, g2);
            
            Stroke tmpStroke = g2.getStroke();
            Font tmpFont = g2.getFont();
            
            g2.setStroke(TRAIT_TILLE_EPAIS);
            g2.setFont(ManageFonts.POLICE_TITRE);
            
            // affichages des zones de départ et arrivée
            for(Player joueur : jeu.getJoueurs())
            {
                Rectangle zoneC = joueur.getEmplacement().getZoneDeConstruction();
                g2.setColor(joueur.getEmplacement().getCouleur());
                g2.drawRect(zoneC.x , zoneC.y, zoneC.width, zoneC.height);
            
                if(joueur.getEquipe().aPerdu())
                {
                    g2.drawLine(zoneC.x , zoneC.y, zoneC.x + zoneC.width, zoneC.y + zoneC.height);
                    g2.drawLine(zoneC.x , zoneC.y + zoneC.height, zoneC.x + zoneC.width, zoneC.y);
                }
                
                g2.drawString(joueur.getPseudo(), zoneC.x+xOffsetPseudo , zoneC.y+yOffsetPseudo);
                g2.setColor(Color.BLACK);
                g2.drawString(joueur.getPseudo(), zoneC.x+xOffsetPseudo+2 , zoneC.y+yOffsetPseudo+2);  
            }
            
            g2.setFont(tmpFont);
            g2.setStroke(tmpStroke); 
            
            setTransparence(1.f, g2);
        }

		//------------------------------------
		//-- Affichage du maillage (graphe) --
		//------------------------------------
		if(afficherMaillage)
		{	
		    // modification de la transparence
		    setTransparence(ALPHA_MAILLAGE, g2);
			
		    // recuperation de la liste des arcs actifs
			Line2D[] arcsActifs = jeu.getTerrain().getArcsActifs();
			
			if(arcsActifs != null)
			{
			    // affichage des arcs actifs
			    g2.setColor(COULEUR_MAILLAGE);
				for(Line2D arc : arcsActifs) 
					g2.drawLine((int)arc.getX1(),(int)arc.getY1(),
							    (int)arc.getX2(),(int)arc.getY2());
			}
	
			for(Node n : jeu.getTerrain().getNoeuds())
			{
			    if(n.isActif())
			        g2.setColor(Color.GREEN);
			    else
			        g2.setColor(Color.RED);
			    
			    g2.fillOval(n.x-1, n.y-1, 2, 2);
			}
			
			/* TODO Affichage de tous les chemins (pour maillage v2.0)
			try
            {
			    g2.setColor(Color.BLUE);
			    for(int i=-5;i<50;i++)
			        for(int j=-5;j<50;j++)
			            afficherCheminPourNoeud(i*10, j*10,g2);
			    
	            
            } 
			catch (IllegalArgumentException e) {}
			*/
			
			// reinitialisation de la transparence
	        setTransparence(1.f, g2);
		}
		
		
		
		//----------------------------------------
		//-- affichage des creatures terrestres --
		//----------------------------------------
		Creature creature;
        Enumeration<Creature> eCreatures = jeu.getCreatures().elements();
        while(eCreatures.hasMoreElements())
        {
            creature = eCreatures.nextElement();
                
            // affichage des creatures terriennes uniquement
            if(creature.getType() == Creature.TYPE_TERRIENNE)
                dessinerCreature(creature,g2);
        }
		
		//-------------------------
		//-- affichage des tours --
		//-------------------------
		for(Tower tour : jeu.getTours())
			dessinerTour(tour,g2,false);
		
	    //--------------------------------------
        //-- affichage des creatures aerienne --
        //--------------------------------------
		eCreatures = jeu.getCreatures().elements();
        while(eCreatures.hasMoreElements())
        {
            creature = eCreatures.nextElement();
            
            // dessine toutes les barres de sante
            dessinerBarreDeSante(creature, g2);
            
            if(creature.getType() == Creature.TYPE_AERIENNE)
                dessinerCreature(creature,g2);
        }
		
        
        traitTmp = g2.getStroke();
        
		//---------------------------------
		//-- entour la tour selectionnee --
		//---------------------------------
		if(tourSelectionnee != null)
		{
			dessinerPortee(tourSelectionnee,g2,COULEUR_RAYON_PORTEE);
			
			g2.setColor(COULEUR_SELECTION);
			g2.setStroke(TRAIT_TILLE);
			g2.drawRect(tourSelectionnee.getXi(), tourSelectionnee.getYi(),
					(int) (tourSelectionnee.getWidth()),
					(int) (tourSelectionnee.getHeight()));
		}
		
		//-------------------------------------
		//-- entour la creature selectionnee --
		//-------------------------------------
		if(creatureSelectionnee != null)
		{
			g2.setColor(COULEUR_SELECTION);
			g2.setStroke(TRAIT_TILLE);
			g2.drawOval((int) (creatureSelectionnee.getX()), 
						(int) (creatureSelectionnee.getY()),
						(int) creatureSelectionnee.getWidth(),
						(int) creatureSelectionnee.getHeight());
			
			// dessine son chemin
			setTransparence(ALPHA_CHEMIN_CREATURE,g2);
			dessinerCheminCreature(creatureSelectionnee,g2);
			
			if(centrerSurCreatureSelectionnee)
			    centrerSur((int) creatureSelectionnee.getCenterX(),
			               (int) creatureSelectionnee.getCenterY());
			
			setTransparence(1.f,g2);
		}
		
		g2.setStroke(traitTmp);
		
		//------------------------------------
		//-- affichage des rayons de portee --
		//------------------------------------
		if(afficherRayonsDePortee)
			for(Tower tour : jeu.getTours())
				dessinerPortee(tour,g2,COULEUR_RAYON_PORTEE);
		
		//------------------------------------------------
		//-- affichage des animations au-dessus de tout --
		//------------------------------------------------
		jeu.dessinerAnimations(g2, Animation.HAUTEUR_AIR);
		
		
		
		// quadrillage
		if(afficherQuadrillage)
		{
            setTransparence(ALPHA_QUADRILLAGE, g2);
            g2.setColor(COULEUR_QUADRILLAGE);
            for(int i=0;i <= jeu.getTerrain().getLargeur();i+=CADRILLAGE)
                g2.drawLine(i, 0, i, jeu.getTerrain().getHauteur());
            
            for(int i=0;i <= jeu.getTerrain().getHauteur();i+=CADRILLAGE)
                g2.drawLine(0, i, jeu.getTerrain().getLargeur(), i);
		}
		
		
		
		
		
		
		//------------------------------------
		//-- affichage de la tour a ajouter --
		//------------------------------------
		if(tourAAjouter != null && sourisSurTerrain)
		{
		    g2.drawImage(I_CADRILLAGE,tourAAjouter.x-40,tourAAjouter.y-40,null);
		    
		    // modification de la transparence
		    setTransparence(ALPHA_TOUR_A_AJOUTER,g2);
		    
		    // dessin de la tour
			dessinerTour(tourAAjouter,g2,false);
			
			// positionnnable ou non
			if(!jeu.laTourPeutEtrePosee(tourAAjouter))
				dessinerPortee(tourAAjouter,g2,COULEUR_POSE_IMPOSSIBLE);
			else
				// affichage du rayon de portee
				dessinerPortee(tourAAjouter,g2,COULEUR_RAYON_PORTEE);
		}
		
		//-----------------------------
        //-- affichage du mode pause --
        //-----------------------------
		if(jeu.estEnPause())
	    {
            setTransparence(0.3f, g2);
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, LARGEUR, HAUTEUR);
            setTransparence(1.0f, g2);
            
            g2.setColor(Color.WHITE);
            Font policeTmp = g2.getFont();
            g2.setFont(ManageFonts.POLICE_TITRE);
            g2.drawString("[ PAUSE ]", LARGEUR / 2 - 80, HAUTEUR / 2 - 50);
            g2.setFont(policeTmp);
	    }
		
		
		if(afficherFps)
        {
		    g2.setColor(Color.BLACK);
		    g2.drawString("fps : "+fps, 0, 12);
		    g2.setColor(Color.WHITE);
		    g2.drawString("fps : "+fps, 1, 12+1);
        }
		
	}
	
	/*
	private void afficherCheminPourNoeud(int x, int y, Graphics2D g2)
    {  
        ArrayList<Point> chemin;
        
        try 
        {
            chemin = jeu.getTerrain().getCheminLePlusCourt(x, y, 0, 0, 0);
            
            Point pPrec = chemin.get(0);
            for(int i=1;i<chemin.size();i++)
            {
                if(i == 2)
                    break;
                
                Point p =  chemin.get(i);

                g2.drawLine((int) pPrec.x, 
                        (int) pPrec.y, 
                        (int) p.x, 
                        (int) p.y);
                
                pPrec = p; 
            }
        } 
        catch (IllegalArgumentException e){} 
        catch (PathNotFoundException e){}
    }
	*/

	/**
	 * Permet de dessiner une zone rectangulaire sur le terrain.
	 * 
	 * @param zone la zone rectangulaire
	 * @param g2 le Graphics2D pour dessiner
	 */
	private void dessinerZone(final Rectangle zone, final Graphics2D g2)
    {
        g2.fillRect((int) zone.getX(), 
                    (int) zone.getY(), 
                    (int) zone.getWidth(), 
                    (int) zone.getHeight());
    }
	
	/**
	 * Permet de dessiner une creature sur le terrain.
	 * 
	 * @param creature la creature a dessiner
	 * @param g2 le Graphics2D pour dessiner
	 */
	private void dessinerCreature(final Creature creature, final Graphics2D g2)
	{  
	    if(creature.getImage() != null)
	    {
	        // rotation des créatures
	        AffineTransform tx = new AffineTransform();
	        
	        tx.translate(creature.getCenterX(), creature.getCenterY());
	        tx.rotate(creature.getAngle()+Math.PI/2);
	        tx.translate(-creature.getWidth()/2, -creature.getHeight()/2);
	        tx.scale(creature.getWidth() / (double) creature.getImage().getWidth(null), creature.getHeight() / (double) creature.getImage().getHeight(null));

	        // dessin de la créature avec rotation
	        g2.drawImage(creature.getImage(), tx, this);
	    }
        else
        {
            // affichage d'un cercle au centre de la position de la creature
            g2.setColor(COULEUR_CREATURE_SANS_IMAGE);
            g2.fillOval((int) creature.getX(), 
                        (int) creature.getY(),
                        (int) creature.getWidth(), 
                        (int) creature.getHeight());
        }
	    
        // affichage du chemin des creatures
        if(afficherMaillage)
        {
            g2.setColor(COULEUR_CHEMIN);
            dessinerCheminCreature(creature,g2);
        }  
	}
	
	
	/**
	 * Permet de dessiner la barre de sante d'une creature.
	 * 
	 * @param creature la creature correspondante
     * @param g2 le Graphics2D pour dessiner
	 */
	private void dessinerBarreDeSante(final Creature creature, final Graphics2D g2)
	{
	    // calculs des proprietes
	    int largeurBarre    = (int) (creature.getWidth() * COEFF_LARGEUR_BARRE_VIE);
        int positionXBarre  = (int) ( creature.getX() - 
                              (largeurBarre - creature.getWidth()) / 2);
        int positionYBarre  = (int)(creature.getY()+creature.getHeight());
        
        // affichage du conteneur
        g2.setColor(creature.getProprietaire().getEquipe().getCouleur());
        
        g2.fillRect(positionXBarre,positionYBarre, 
                    largeurBarre, HAUTEUR_BARRE_VIE);
        
        // affichage du contenu
        g2.setColor(COULEUR_SANTE);
        
        g2.fillRect(positionXBarre+1, positionYBarre+1, 
                (int)(creature.getSante()*(largeurBarre - 2)/creature.getSanteMax()),
                HAUTEUR_BARRE_VIE-2);
	}
	
	/**
	 * Permet de dessiner le chemin d'une creature.
	 * 
	 * @param creature la creature concernee
	 * @param g2 le Graphics2D pour dessiner
	 */
    private void dessinerCheminCreature(final Creature creature, final Graphics2D g2)
    {
    	// recuperation du chemin
        ArrayList<Point> chemin = creature.getChemin();
        
        // s'il est valide
        if(chemin != null && chemin.size() > 0)
        {
            // initialisation du point precedent
            Point PointPrecedent = chemin.get(creature.getIndiceCourantChemin()-1);
            
            // bloque la reference du chemin
            synchronized(chemin)
            {
                // pour chaque point du chemin
                Point point;
                for(int i=creature.getIndiceCourantChemin();i<chemin.size();i++)
                {
                    /* 
                     * affichage du segment de parcours 
                     * entre le point precedent et la suivant
                     */
                    point = chemin.get(i);
                    
                    g2.drawLine(PointPrecedent.x, PointPrecedent.y, 
                                point.x, point.y);
                    PointPrecedent = point;
                }
            }
        }
    }
	
	/**
	 * Permet de dessiner une tour
	 * 
	 * @param tour la tour a dessiner
	 * @param g2 le Graphics2D pour dessiner
	 * @param avecPortee dessiner ou non la portee de la tour ?
	 */
	private void dessinerTour(final Tower tour,
							  final Graphics2D g2,
							  final boolean avecPortee)
	{
		// dessin de l'image
		if(!modeDebug && tour.getImage() != null)
		{
		    AffineTransform tx = new AffineTransform();
	        tx.translate(tour.getCenterX(), tour.getCenterY());
	        tx.rotate(tour.getAngle());
	        tx.translate((int) -tour.getWidth()/2.0, (int) -tour.getHeight()/2.0);

	        
	        setTransparence(.5f, g2);
	        g2.setColor(tour.getPrioprietaire().getEquipe().getCouleur());
	        g2.fillOval(tour.x-1, tour.y-1, (int) tour.getWidth()+2, (int) tour.getHeight()+2);
	        setTransparence(1.f, g2);
	        
		    g2.drawImage(tour.getImage(), tx ,null);
		}
		// dessin d'un forme de couleur
		else
		{	 
			g2.setColor(tour.getCouleurDeFond());
			g2.fillOval(tour.getXi(), tour.getYi(), 
				(int)tour.getWidth(), 
				(int)tour.getHeight());
		}
		
		// dessin du niveau actuelle de la tour (petits carres)
		for(int i=0;i < tour.getNiveau() - 1;i++)
		{
			g2.setColor(COULEUR_NIVEAU_PERIMETRE);
			g2.fillRect(tour.getXi() + 4 * i + 1, 
			        (int) (tour.getYi() + tour.getHeight() - 4), 3,3);
			g2.setColor(COULEUR_NIVEAU);
			g2.fillRect(tour.getXi() + 4 * i + 2, 
			        (int) (tour.getYi() + tour.getHeight() - 3), 1,1);
		}
		// dessin de la portee
		if(avecPortee)
			dessinerPortee(tour,g2,COULEUR_RAYON_PORTEE);
	}
	
	/**
	 * Permet de dessiner le rayon de portee d'une tour
	 * 
	 * @param tour la tour concernee
	 * @param g2 le Graphics2D pour dessiner
	 */
	private void dessinerPortee(Tower tour,Graphics2D g2, Color couleurRayonDePortee)
	{
        // affichage du perimetre du rayon de portee
	    setTransparence(ALPHA_PERIMETRE_PORTEE,g2);
        g2.setColor(couleurRayonDePortee);
		g2.drawOval((int)(tour.getXi() - tour.getRayonPortee() + tour.getWidth()/2), 
					(int)(tour.getYi() - tour.getRayonPortee() + tour.getHeight()/2), 
					(int)tour.getRayonPortee()*2, 
					(int)tour.getRayonPortee()*2);

		// affichage de la surface du rayon de portee
		setTransparence(ALPHA_SURFACE_PORTEE,g2);
        g2.setColor(couleurRayonDePortee);
        g2.fillOval((int)(tour.getXi() - tour.getRayonPortee() + tour.getWidth()/2), 
        			(int)(tour.getYi() - tour.getRayonPortee() + tour.getHeight()/2), 
        			(int)tour.getRayonPortee()*2, 
        			(int)tour.getRayonPortee()*2);
        
        // remet la valeur initial
        setTransparence(1.f,g2);
	}
	
	/**
	 * Permet de modifier la transparence du Graphics2D
	 * 
	 * @param tauxTransparence le taux (1.f = 100% opaque et 0.f = 100% transparent)
	 * @param g2 le Graphics2D a configurer
	 */
	protected void setTransparence(float tauxTransparence, Graphics2D g2)
    {
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tauxTransparence));
    }
	
	/**
	 * Méthode de refraichissement du panel
	 * 
	 * L'implémentation de Runnable nous force à définir cette méthode.
	 * Celle-ci sera appelée lors du démarrage du thread.
	 * 
	 * @see Runnable
	 */
	@Override
	public void run()
	{
	    timer = new Timer();
        timer.start();
        
        long lastFPSlog = 0;
        int frames      = 0;
	    
	    // Tant que la partie est en cours...
		while(!jeu.estDetruit())
		{
			// Raffraichissement du panel
			repaint(); // -> appel paintComponent
			
			//-------------------------------
            //-- compute frames per second --
            //-------------------------------
            if(afficherFps)
            {
                ++frames;
    
                long time = timer.getTime();
                
                if (time > lastFPSlog+1000) 
                {
                    fps         = frames;
                    frames      = 0;
                    lastFPSlog  = time;
                }
            }
			
			// Endore le thread
			try {
				Thread.sleep(TEMPS_REPOS_THREAD);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Métode de gestion des cliques de la souris
	 * 
	 * @param me l'evenement lie a cette action 
	 * @see MouseListener
	 */
	@Override
	public void mousePressed(MouseEvent me)
	{
	    boutonGrab = me.getButton();

	    // clique gauche
	    if (me.getButton() == MouseEvent.BUTTON1)
		{    
	        if(me.getClickCount() == 1)
	        {
	        
    	        sourisGrabX = me.getX();
    	        sourisGrabY = me.getY();
    	        
    	        decaleGrabX = decaleX;
    	        decaleGrabY = decaleY;
    	        
    	        Point positionSurTerrain = getCoordoneeSurTerrainOriginal(sourisX,sourisY);
    	        
    	        
    	        //--------------------------
    	        //-- selection d'une tour --
    	        //--------------------------
    	        
    	        // la selection se fait lors du clique
    			for(Tower tour : jeu.getTours()) // pour chaque tour... 
    				if (tour.intersects(positionSurTerrain.x,positionSurTerrain.y,1,1)) // la souris est dedans ?
    				{	
    					// si le joueur clique sur une tour deja selectionnee
    				    if (tourSelectionnee == tour)
    						tourSelectionnee = null; // deselection
    					else
    					{
    						tourSelectionnee = tour; // la tour est selectionnee
    						// si une tour est selectionnee, il n'y pas d'ajout
    						tourAAjouter = null;
    					}
    				 
    				    // informe la fenetre qu'une tour a ete selectionnee
    				    if(edpt != null)
    				        edpt.tourSelectionnee(tourSelectionnee,
    											Panel_InfoTour.MODE_SELECTION);
    					return;
    				}
    		
    	        // aucun tour trouvee => clique dans le vide.
                tourSelectionnee = null;
                
                //edpt.tourSelectionnee(tourSelectionnee, Panel_InfoTour.MODE_SELECTION);
    			
                //------------------------------
                //-- selection d'une creature --
                //------------------------------
    
    			Creature creature;
    			Vector<Creature> creatures = jeu.getCreatures();
    			
    			// parcours a l'envers car il faut traiter les creatures les plus
                // devant en premier (les derniers affiches)
    			for(int i = creatures.size()-1; i >= 0 ;i--)
    			{
    			    creature = creatures.get(i);
    
    			    if (creature.intersects(positionSurTerrain.x,positionSurTerrain.y,1,1)) // la souris est dedans ?
    				{	
    			        // on enleve le suivi de la creature
    			        centrerSurCreatureSelectionnee = false;
    			        
    			        // si le joueur clique sur une creature deja selectionnee
    			        if (creatureSelectionnee == creature)
    						creatureSelectionnee = null; // deselection
    			        
    					else
    						creatureSelectionnee = creature; // la creature est selectionnee
    					
    			        if(edpt != null)
    			            edpt.creatureSelectionnee(creatureSelectionnee);
    					
    					return;
    				}
    			}
    			
    			creatureSelectionnee = null;
    			
    			// aucune tour et aucune creature trouvee => clique dans le vide.
    			if(tourAAjouter == null && edpt != null)
    			    edpt.deselection();
	        }
	        else // double click 
	        {
	            // remise à l'échelle initiale et recentrage
	            reinitialiserVue();
	        }
		}
		else // clique droit ou autre
		{
			// deselection total
			tourSelectionnee 	 = null;
			tourAAjouter 		 = null;
			creatureSelectionnee = null;
			
			if(edpt != null)
			    edpt.deselection();
		}
	}
	
	/**
	 * Métode de gestion des relachements du clique de la souris
	 * 
	 * @param me l'evenement lie a cette action 
	 */
	@Override
	public void mouseReleased(MouseEvent me)
	{
	    if(!jeu.estEnPause())
        {
    	    // l'ajout se fait lors de la relache du clique
    		if(tourAAjouter != null)
    		{
    		    if(edpt != null)
    		        edpt.acheterTour(tourAAjouter);
    		    
    			setCursor(curNormal);
    			
    			// informe la fenetre qu'une tour a ete selectionnee
                //edpt.tourSelectionnee(tourAAjouter, Panel_InfoTour.MODE_SELECTION);
    		}
        }
	}
	
	/**
	 * Méthode de gestion des deplacements de la souris
	 * 
	 * @param me evenement lie a cette action
	 * @see MouseMotionListener
	 */
	@Override
	public void mouseMoved(MouseEvent me)
	{
		// mise a jour des coordonees de la souris
		sourisX = me.getX();
		sourisY = me.getY();
		
		// mise a jour de la position de la souris sur le grillage vituel
        sourisCaseX = getPositionSurQuadrillage(sourisX);
        sourisCaseY = getPositionSurQuadrillage(sourisY);
		
        
        //---------------------------
        //-- gestion des decalages --
        //---------------------------
        setCursor(curNormal);
		
        // les décalage psr les bordures marchent uniquement lors de l'ajout 
        // d'une tour
        if(tourAAjouter != null)
        {
            int largeurPanel = getWidth();
            int hauteurPanel = getHeight();
            
    		if(sourisX > 0 
    		&& sourisX < MARGES_DEPLACEMENT
    		&& (decaleX != 0 || coeffTaille != 1.0))
    		{
                setCursor(curRedimGauche);
    		    decaleX++;
    		}
    		
    		if(sourisX > largeurPanel-MARGES_DEPLACEMENT 
    		&& sourisX < largeurPanel
    		&& (decaleX != 0 || coeffTaille != 1.0))
    		{
    		    setCursor(curRedimDroite);
    		    decaleX--;
    		} 
    		
    		if(sourisY > 0 
            && sourisY < MARGES_DEPLACEMENT
            && (decaleY != 0 || coeffTaille != 1.0))
    		{
                setCursor(curRedimBas);
                decaleY++;
    		}
    		
    		if(sourisY > hauteurPanel-MARGES_DEPLACEMENT 
            && sourisY < hauteurPanel 
            && (decaleY != 0 || coeffTaille != 1.0))
    		{
                setCursor(curRedimHaut);    
                decaleY--;
    		}
    
		    // mise a jour de la position de la tour à ajoutée   
		    // pour eviter des pertes de précision,on récupère d'abord 
		    // la position sur le terrain de taille normal...
		    Point p = getCoordoneeSurTerrainOriginal(sourisX, sourisY);
		    
		    // ... puis on calcul la position sur le quadrillage
		    tourAAjouter.setLocation(
		        getPositionSurQuadrillage(p.x), 
		        getPositionSurQuadrillage(p.y)
		        );
		}
	}
	
	/**
	 * Permet de recupérer la case sur le quadrillage d'une position.
	 * 
	 * @param position une position X ou Y
	 * @return la case sur le quadrillage carré
	 */
	public int getPositionSurQuadrillage(int position)
	{
	    // CADRILLAGE/2 pour adapter le pointage de la souris
	    return Math.round((position-CADRILLAGE/2)/CADRILLAGE)*CADRILLAGE;
	}
	
	/**
     * Permet de faire correspondre une coordonnée donnée sur la position normale
     * du terrain (sans zoom et décalage)
     * 
	 * @param x la position x du point
	 * @param y la position y du point
	 * @return le point correspondant sur le terrain de taille normal
	 */
	protected Point getCoordoneeSurTerrainOriginal(int x, int y)
	{
	    return new Point((int)(x / coeffTaille - decaleX), 
	                     (int)(y / coeffTaille - decaleY));
	}
	
	/**
     * Permet de faire correspondre une coordonnée donnée sur la position normale
     * du terrain (sans zoom et décalage)
     * 
     * @param p la coordonnnée donnée
     * @return le point correspondant sur le terrain de taille normal
     */
    protected Point getCoordoneeSurTerrainOriginal(Point p)
    {
        return new Point((int)(p.x / coeffTaille - decaleX), 
                         (int)(p.y / coeffTaille - decaleY));
    }
	
	/**
     * Methode de gestion du clique enfoncé de la souris lorsque qu'elle bouge.
     * 
     * @param me evenement lie a cette action
     * @see MouseMotionListener
     */
	@Override
    public void mouseDragged(MouseEvent me)
    {
	    if(boutonGrab == MouseEvent.BUTTON1)
	    {
	        // si il n'y a pas de tour a ajouter, c'est comme si elle bougeait normalement 
	        if(tourAAjouter != null)
	            mouseMoved(me);
	   
            // si rien n'est selectionner, on autorise le grab
            else
            {
                setCursor(curMainAgripper);
                
                decaleX = (int) (decaleGrabX - (sourisGrabX - me.getX()) / coeffTaille);
                decaleY = (int) (decaleGrabY - (sourisGrabY - me.getY()) / coeffTaille);
            }
        }
    }

	/**
	 * Methode de gestion de la souris lorsque qu'elle entre dans le panel
	 * 
	 * @param me evenement lie a cette action
	 * @see MouseMotionListener
	 */
    @Override
	public void mouseEntered(MouseEvent me)
	{
		sourisSurTerrain = true;
		
		//if(tourAAjouter != null)
		//    setCursor(curseurTransparent);

		// recuperation du focus. 
		// /!\ Important pour la gestion des touches clavier /!\
		requestFocusInWindow(true); 
	}
	
	/**
	 * Methode de gestion de la souris lorsque qu'elle sort du panel
	 * 
	 * @param me evenement lie a cette action
	 * @see MouseMotionListener
	 */
	@Override
	public void mouseExited(MouseEvent me)
	{
		sourisSurTerrain = false;
		
		// pas de deplacement lorsque la souris a quittée la zone
		toucheHautPressee     = false;
		toucheBasPressee      = false;
		toucheDroitePressee   = false;
		toucheGauchePressee   = false;
	}
	
	// methodes non redéfinies (voir MouseListener)
	public void mouseClicked(MouseEvent me)
	{}
	
	/**
	 * Methode de gestion des evenements lors de la relache d'une touche
	 */
	@Override
	public void keyReleased(KeyEvent ke)
	{
		int keyCode = ke.getKeyCode();
	    
	    if(!jeu.estEnPause())
		{
    	    // raccourcis des tours
    	    if(tourSelectionnee != null)
    		{
    	        if(edpt != null)
    	        {
        	        // raccourci de vente
        			if(keyCode == Configuration.getKeyCode(Configuration.VENDRE_TOUR))
        				edpt.vendreTour(tourSelectionnee);
        			// raccourci d'amelioration
        			else if(keyCode == Configuration.getKeyCode(Configuration.AMELIO_TOUR))
        			    edpt.ameliorerTour(tourSelectionnee);
    	        }
    		}
    	    
    	    // focus sur la creature
    	    if(keyCode == Configuration.getKeyCode(Configuration.SUIVRE_CREATURE) && creatureSelectionnee != null)
    	        centrerSurCreatureSelectionnee = true; 
		}
		
		if(keyCode == Configuration.getKeyCode(Configuration.DEPL_HAUT))
            toucheHautPressee = false;
        else if(keyCode == Configuration.getKeyCode(Configuration.DEPL_GAUCHE))
            toucheGauchePressee = false;
        else if(keyCode == Configuration.getKeyCode(Configuration.DEPL_BAS))
            toucheBasPressee = false;
        else if(keyCode == Configuration.getKeyCode(Configuration.DEPL_DROITE))
            toucheDroitePressee = false;
	}
	
	@Override
	public void keyPressed(KeyEvent ke)
	{
	    int keyCode = ke.getKeyCode();
	    
	    if(keyCode == Configuration.getKeyCode(Configuration.DEPL_HAUT))
            toucheHautPressee = true;
	    else if(keyCode == Configuration.getKeyCode(Configuration.DEPL_GAUCHE))
            toucheGauchePressee = true;
	    else if(keyCode == Configuration.getKeyCode(Configuration.DEPL_BAS))
            toucheBasPressee = true;
	    else if(keyCode == Configuration.getKeyCode(Configuration.DEPL_DROITE))
            toucheDroitePressee = true;
	}
	
	@Override
	public void keyTyped(KeyEvent ke){}

	@Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        zoomer(e.getWheelRotation());
    }
    
    /**
     * Permet de centrer le terrain sur une coordonnée du terrain de taille
     * normale.
     * 
     * @param x la position sur le terrain normal
     * @param y la position sur le terrain normal
     */
    private void centrerSur(int x, int y)
    { 
        int largeurPanel = getBounds().width;
        if(largeurPanel == 0)  
            largeurPanel = getPreferredSize().width;
        
        if(largeurPanel == 0)  
            largeurPanel = getWidth();
        
        
        int hauteurPanel = getBounds().height;
        if(hauteurPanel == 0)  
            hauteurPanel = getPreferredSize().height;
        if(hauteurPanel == 0)  
            hauteurPanel = getHeight();
        
        decaleX = (int) ((largeurPanel/2.0 - x * coeffTaille) / coeffTaille);
        decaleY = (int) ((hauteurPanel/2.0 - y * coeffTaille) / coeffTaille);
    }

    public void reinitialiserVue()
    {
        // remise à l'échelle initiale et recentrage
        coeffTaille = 1.0;
        centrerSur(jeu.getTerrain().getLargeur()/2, jeu.getTerrain().getHauteur()/2);
    }

    public void zoomer(int i)
    {
        // recupère le point avant le changement d'echelle
        Point pAvant;
        
        if(sourisSurTerrain)      
            pAvant = getCoordoneeSurTerrainOriginal(sourisX, sourisY);
        else
            pAvant = new Point(
                    (int)getPreferredSize().getWidth()/2, 
                    (int)getPreferredSize().getHeight()/2
                    );
        
        // adaptation de l'echelle
        coeffTaille -= i*ETAPE_ZOOM;

        // pas de dézoom
        if(coeffTaille < ZOOM_MIN)
        {
            coeffTaille = ZOOM_MIN;
        }
        else
        {
            if(sourisSurTerrain)
            {
                // recupère le point après le changement d'echelle
                Point pApres = getCoordoneeSurTerrainOriginal(sourisX, sourisY);
                
                // adapte le décalage pour que le point ciblé reste au même
                // endroit en proportion du panel
                decaleX +=  pApres.x - pAvant.x;
                decaleY +=  pApres.y - pAvant.y;
            }
            else
                centrerSur(pAvant.x, pAvant.y);
        }
        
        /*if(!sourisSurTerrain)
            // centrer sur le milieu du terrain
            centrerSur(jeu.getTerrain().getLargeur()/2, jeu.getTerrain().getHauteur()/2);
        */
    }
    
    public void voirToutLeTerrain()
    {
        double maxTaille = Math.max(jeu.getTerrain().getLargeur(),jeu.getTerrain().getHauteur()) * 1.3;
        
        double maxTaillePanel = Math.max(getPreferredSize().width,getPreferredSize().height);
        
        decaleX = decaleY = 0;
        coeffTaille = maxTaillePanel / maxTaille;
    }
}
