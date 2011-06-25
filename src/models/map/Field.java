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

package models.map;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.*;
import javax.swing.*;
import models.creatures.*;
import models.game.Game;
import models.game.GameMode;
import models.grid.*;
import models.player.Team;
import models.towers.Tower;
import models.utils.SoundManagement;
import models.utils.Son;

/**
 * Management class for a playground
 * <p>
 * This class contains all the elements contained in the field, that is to say
 * :<br>
 * - the towers
 * <p>
 * - creatures : the list of creatures and the waves of creatures
 * <p>
 * - meshes : There are two types of meshes, one for the creatures
 * land or the location of the towers has an influence on the way to
 * creatures and other creatures for air or the location of towers
 * has no influence on the path of the creatures (they pass over
 * towers)
 * <p>
 * - Walls specific field: simple inaccessible areas
 * <p>
 * - Zones start and finish of creatures<br>
 * <p>
 * - The background image of the ground
 * <p>
 * <p>
 * Several methods are provided by this class to manage the
 * elements it contains.
 * <p>
 * In addition, this class is abstract, it can not be instantiated as
 * such, but must be inherited.
 * 
 * @author Aurelien Da Campo
 * @version 1.2 | juillet 2010
 * @since jdk1.6.0_16
 * @see Tower
 * @see Creature
 * @see Maillage
 */

public class Field implements Serializable
{
    private static final long serialVersionUID = 1L;

    transient public static final String EXTENSION_FICHIER = "map";

    
    transient public static final String EMPLACEMENT_TERRAINS_SOLO = "maps/solo/";
    transient public static final String EMPLACEMENT_TERRAINS_MULTI = "maps/multi/";
    
    /**
     * breve description
     */
    private String breveDescription;

    /**
     * nombre de vies au debut de la partie
     */
    private int nbViesInitiales;

    /**
     * nombre de pieces d'or au debut de la partie
     */
    private int nbPiecesOrInitiales;

    /**
     * Taille du terrain
     */
    private int largeur, // en pixels
                hauteur; // en pixels

    /**
     * precision du maillage, espace entre deux noeuds
     */
    private final int PRECISION_MAILLAGE = 10; // pixels

    /**
     * Le maillage permet de definir les chemins des creatures sur le terrain.
     * Ici, pour les creatures terriennes avec prise en compte de la position
     * des tours.
     * 
     * @see Maillage
     */
    transient private Maillage MAILLAGE_TERRESTRE;
    transient private Maillage MAILLAGE_AERIEN;
    
    /**
     * Dimention du maillage
     */
    private int largeurMaillage, hauteurMaillage;
    
    /**
     * Offset du maillage
     */
    private int positionMaillageX, positionMaillageY;
    
    /**
     * Les creatures volantes n'ont pas besoins d'une maillage mais uniquement
     * du chemin le plus court entre la zone de depart et la zone d'arrivee
     */
    transient ArrayList<Point> cheminAerien;

    /**
     * Image de fond du terrain. <br>
     * Note : les murs du terrain sont normalement lies a cette image
     */
    transient private Image imageDeFond;
    
    // seule les ImageIcon peuvent etre serialisé
    // utilisation de cette variable pour sauver l'image de fond
    private ImageIcon iconImageDeFond;
    
    /**
     * Pour le mode debug
     */
    private Color couleurMurs = Color.BLACK;
    private Color couleurDeFond = new Color(0, 100, 0);
    
    /**
     * Les murs sont utilises pour empecher le joueur de construire des tours
     * dans certaines zones. Les creatures ne peuvent egalement pas si rendre.
     * En fait, les murs reflettent les zones de la carte non accessible. Les
     * murs ne sont pas affiches. Ils sont simplement utilises pour les controls
     * d'acces de la carte.
     */
    protected ArrayList<Rectangle> murs = new ArrayList<Rectangle>();
  
    /**
     * Permet de spécifier l'affichage des murs par défaut
     */
    protected float opaciteMurs = 1.0f;
    
    /**
     * musique d'ambiance du terrain
     */
    protected File fichierMusiqueDAmbiance;

    /**
     * Stockage du jeu
     */
    transient private Game jeu;
    
    /**
     * Liste des equipes, utilisé pour la définition du terrain.
     * <br>
     * C'est le terrain qui fourni les équipes, on doit pouvoir
     * les sérialiser.
     */
    protected ArrayList<Team> equipes = new ArrayList<Team>();
    
    /**
     * Mode de jeu du terrain, utilisé pour construire les bons formulaires 
     * et affichages
     */
    private int modeDeJeu;
    
    /**
     * Permet de definit la taille du panel du terrain
     */
    protected Dimension taillePanelTerrain = null;

    protected String nomFichier;
    
    /**
     * Constructeur du terrain.
     * 
     * @param largeur la largeur en pixels du terrain (utilisé pour le maillage)
     * @param hauteur la hauteur en pixels du terrain (utilisé pour le maillage)
     * @param nbPiecesOrInitiales le nom de piece d'or en debut de partie
     * @param positionMaillageX position du point 0 du maillage
     * @param positionMaillageY position du point 0 du maillage
     * @param largeurMaillage largeur du maillage en pixel
     * @param hauteurMaillage hauteur du maillage en pixel
     * @param imageDeFond le chemin jusqu'a l'image de fond
     * @param description nom de la zone de jeu
     */
    public Field(Game jeu, int largeur, int hauteur, int nbPiecesOrInitiales,
            int nbViesInitiales, int positionMaillageX, int positionMaillageY,
            int largeurMaillage, int hauteurMaillage, int modeDeJeu, Color couleurDeFond, 
            Color couleurMurs, Image imageDeFond, String description)
    {
        this.jeu = jeu; 
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.nbPiecesOrInitiales = nbPiecesOrInitiales;
        this.nbViesInitiales    = nbViesInitiales;
        this.imageDeFond        = imageDeFond;
        this.iconImageDeFond    = new ImageIcon(imageDeFond);
        
        this.largeurMaillage    = largeurMaillage;
        this.hauteurMaillage    = hauteurMaillage;
        this.positionMaillageX  = positionMaillageX;
        this.positionMaillageY  = positionMaillageY;
        
        this.breveDescription    = description;
        this.couleurDeFond      = couleurDeFond;
        this.couleurMurs        = couleurMurs;
        this.modeDeJeu          = modeDeJeu;   
    }
    
    /**
     * Constructeur de Terrain de base
     * 
     * -> principalement utilisé pour l'éditeur de terrain
     * 
     * @param jeu le jeu
     */
    public Field(Game jeu)
    {
        this.jeu = jeu;
        
        largeur                 = 500;
        hauteur                 = 500;
        nbPiecesOrInitiales     = 100;
        nbViesInitiales         = 20;
        breveDescription         = "";
        modeDeJeu               = GameMode.MODE_SOLO;   
    }

    /**
     * Permet d'initialiser le terrain.
     * 
     * Il s'agit de construire les maillages et d'activer les murs.
     */
    public void initialize()
    {
        // creation des deux maillages
        // TODO Choix du maillage
        MAILLAGE_TERRESTRE = new Maillage_v1(largeurMaillage, hauteurMaillage,
                PRECISION_MAILLAGE, positionMaillageX, positionMaillageY);
        
        MAILLAGE_AERIEN = new Maillage_v1(largeurMaillage, hauteurMaillage,
                PRECISION_MAILLAGE, positionMaillageX, positionMaillageY);  
        
        
        // activation des murs for(Rectangle mur : murs)
        for(Rectangle mur : murs)
        {
            MAILLAGE_TERRESTRE.desactiverZone(mur,false);
            MAILLAGE_AERIEN.desactiverZone(mur,false);
        }
        
        // ajout des points des sorties
        Rectangle zoneArrivee;
        for(Team equipe : equipes)
        {
            zoneArrivee = equipe.getZoneArriveeCreatures();

            MAILLAGE_TERRESTRE.ajouterPointdeSortie((int) zoneArrivee.getCenterX(), (int) zoneArrivee.getCenterY());
            MAILLAGE_AERIEN.ajouterPointdeSortie((int) zoneArrivee.getCenterX(), (int) zoneArrivee.getCenterY());
        }
    }
    
    public void reinitialiser()
    {
        initialize();
    }
    
    
    // ------------------------------
    // -- GETTER / SETTER BASIQUES --
    // ------------------------------

    /**
     * Permet de recuperer la largeur du terrain.
     * 
     * @return la largeur du terrain
     */
    public int getLargeur()
    {
        return largeur;
    }

    /**
     * Permet de recuperer la hauteur du terrain.
     * 
     * @return la hauteur du terrain
     */
    public int getHauteur()
    {
        return hauteur;
    }

    /**
     * Permet de recuperer l'image de fond du terrain.
     * 
     * @return l'image de fond du terrain
     */
    public Image getImageDeFond()
    {      
        return imageDeFond;
    }
    
    /**
     * Permet de modifier l'image de fond du terrain
     * 
     * @param imageDeFond l'image de fond
     */
    public void setImageDeFond(Image imageDeFond)
    {     
        // effacer pour l'editeur
        //if(imageDeFond == null)
        //     throw new IllegalArgumentException("Image nulle");
        
        this.imageDeFond = imageDeFond;
        
        if(imageDeFond == null)
            iconImageDeFond = null;
        else
            iconImageDeFond = new ImageIcon(imageDeFond); 
    }
    
    /**
     * Permet de recuperer le nombre de pieces initial
     * 
     * @return le nombre de pieces initiales
     */
    public int getNbPiecesOrInitiales()
    {
        return nbPiecesOrInitiales;
    }

    /**
     * Permet de recuperer le nombre de vie du joueur en debut de partie
     * 
     * @return le nombre de vie du joueur en debut de partie
     */
    public int getNbViesInitiales()
    {
        return nbViesInitiales;
    }

    /**
     * Permet de recuperer le nom du terrain
     * 
     * @return le nom du terrain
     */
    public String getBreveDescription()
    {
        return breveDescription;
    }
    
    /**
     * Permet de recuperer le mode de jeu du terrain
     * 
     * @return le mode de jeu
     */
    public int getMode()
    {
        return modeDeJeu;
    }

    /**
     * Permet de recuperer la taille voulue pour le panel du terrain
     * 
     * @return la taille voulue pour le panel du terrain
     */
    public Dimension getTaillePanelTerrain()
    {
        if(taillePanelTerrain != null)
            return taillePanelTerrain;
        else  
            return new Dimension(largeur,hauteur);
    }
    
    // ----------------------
    // -- 管理墙壁 --
    // ----------------------

    /**
     * 在地面上增加墙壁.
     * 
     * @param wall 增加的墙壁
     */
    public void addWall(Rectangle wall)
    {
        // c'est bien un mur valide ?
        if (wall == null)
            throw new IllegalArgumentException("Mur nul");

        // desactive la zone dans le maillage qui correspond au mur
        if(MAILLAGE_TERRESTRE != null)
            MAILLAGE_TERRESTRE.desactiverZone(wall,false);
        
        if(MAILLAGE_AERIEN != null)
            MAILLAGE_AERIEN.desactiverZone(wall,false);

        // ajout du mur
        murs.add(wall);

        /*
         * Recalculation du chemin des créatures volantes
         */
        // TODO adapter pour chemin aérien
        /*
        try
        {
            cheminAerien = MAILLAGE_TERRESTRE.plusCourtChemin((int) ZONE_DEPART
                    .getCenterX(), (int) ZONE_DEPART.getCenterY(),
                    (int) ZONE_ARRIVEE.getCenterX(), (int) ZONE_ARRIVEE
                            .getCenterY());

        } catch (PathNotFoundException e)
        {
            // ne peut pas arriver, au pire on l'affiche
            e.printStackTrace();
        }*/
    }

    /**
     * Permet de recuperer les murs du terrain
     * 
     * @return les murs du terrain
     */
    public ArrayList<Rectangle> getMurs()
    {
        return murs;
    }

    /**
     * Permet de récupérer la couleur de fond (mode debug)
     * @return la couleur de fond
     */
    public Color getCouleurDeFond()
    {
        return couleurDeFond;
    }

    /**
     * Permet de récupérer la couleur des murs (mode debug)
     * @return la couleur des murs
     */
    public Color getCouleurMurs()
    {
        return couleurMurs;
    }
    
    /**
     * Permet de recuperer les équipes initiales du terrain
     * 
     * @return les équipes initiales
     */
    public ArrayList<Team> getEquipesInitiales()
    {
        return equipes;
    }
    
    /**
     * Permet de recuperer les nombres joueurs max que peut accueillir le terrain
     * 
     * @return le nombre de joueur max
     */
    public int getNbJoueursMax()
    {
        int somme = 0;
           
        for(Team e : equipes)
            somme += e.getNbEmplacements();
        
        return somme;
    }

    // -----------------------
    // -- GESTION DES TOURS --
    // -----------------------

    /**
     * Permet de savoir si une tour peut etre posee a un certain endroit sur le
     * terrain.
     * 
     * @param tour la tour a posee
     * @return true si la tour peut etre posee, false sinon
     */
    public boolean laTourPeutEtrePosee(Tower tour)
    {
        // c'est une tour valide ?
        if (tour == null)
            return false;

        // elle est bien dans le terrain
        if (tour.getX() < 0 || tour.getX() > largeur-tour.width
         || tour.getY() < 0 || tour.getY() > hauteur-tour.height)
            return false;
            
        // il n'y a pas un mur
        synchronized (murs)
        {
            for (Rectangle mur : murs)
                if (tour.intersects(mur))
                    return false;
        }

        // il n'y a pas les zones de depart ou d'arrivee
        for(Team e : jeu.getEquipes())
        {
            // zones de départ
            for(int i=0;i<e.getNbZonesDepart();i++)
                if(tour.intersects(e.getZoneDepartCreatures(i)))
                    return false;
            
            // zone d'arrivee
            if (tour.intersects(e.getZoneArriveeCreatures()))
                return false;
        }

        // rien empeche la tour d'etre posee
        return true;
    }

    /**
     * Permet de savoir si apres la pose d'une tour en parametre le chemin
     * deviendra bloque ?
     * 
     * @param tour la tour a testee si elle bloquera le chemin
     * @return true si elle le bloquera lors de la pose, false sinon
     */
    public boolean laTourBloqueraLeChemin(Tower tour)
    {
        // c'est une tour valide ?
        if (tour == null)
            return false;

        // si l'on construit la tour, il existe toujours un chemin
        desactiverZone(tour, false);

        try {
            
            Team equipe = tour.getPrioprietaire().getEquipe();

            // FIXME on part du principe que le joueur ne peu blocker que son chemin
            // car il construit sur son troncon... A VOIR!
            
            // TODO gérer plusieurs zone de depart
            Rectangle zoneDepart = equipe.getZoneDepartCreatures(0);
            Rectangle zoneArrivee = equipe.getZoneArriveeCreatures();
            
            // calcul du chemin et attente une exception
            // PathNotFoundException s'il y a un probleme
            ArrayList<Point> chemin = getCheminLePlusCourt((int) zoneDepart.getCenterX(),
                    (int) zoneDepart.getCenterY(), (int) zoneArrivee
                            .getCenterX(), (int) zoneArrivee.getCenterY(),
                    Creature.TYPE_TERRIENNE);

            double longueur = MAILLAGE_TERRESTRE.getLongueurChemin(chemin);
            
            
            // mise a jour du chemin
            equipe.setLongueurChemin(longueur);
            
            
            // il existe un chemin, donc elle ne bloque pas.
            activerZone(tour, false); // on reactive la zone
            return false;

        } 
        catch (PathNotFoundException e) {
            // il n'existe pas de chemin, donc elle bloque le chemin.
            activerZone(tour, false); // on reactive la zone
            return true;
        }
    }
    
    // ---------------------------
    // -- GESTION DES CREATURES --
    // ---------------------------
    
    /**
     * Methode qui permet de recuperer la vague de creatures suivantes
     * 
     * Note : cette méthode peut etre redéfinie par les fils de Terrain
     * 
     * @return la vague de creatures suivante
     */
    public WaveOfCreatures getVagueDeCreatures(int noVague)
    {
        return WaveOfCreatures.genererVagueStandard(noVague);
    }

    /**
     * Permet de recuperer la description vague suivante
     * 
     * @return la description de la vague suivante
     */
    public String getDescriptionVague(int noVague)
    {   
        // récupération de la vague suivante
        WaveOfCreatures vagueSuivante = getVagueDeCreatures(noVague);
        
        // s'il y a une description, on la retourne
        String descriptionVague = vagueSuivante.getDescription();
        if (!descriptionVague.isEmpty())
            return descriptionVague;

        // sinon on genere une description
        return vagueSuivante.toString();
    }
    
    
    // -------------------------
    // -- GESTION DU MAILLAGE --
    // -------------------------

    /**
     * Permet d'activer ou reactiver un zone rectangulaire du maillage.
     * 
     * @param zone la zone rectangulaire a activer
     * @param miseAJourDesCheminsDesCreatures faut-il mettre a jour les chemins
     *            des creatures ?
     */
    public void activerZone(Rectangle zone,
            boolean miseAJourDesCheminsDesCreatures)
    {
        // activation de la zone
        if(MAILLAGE_TERRESTRE != null)
        {
            try
            {
                MAILLAGE_TERRESTRE.activerZone(zone, true);
            }
            catch(IllegalArgumentException e)
            {}
            
            // mise a jour des chemins si necessaire
            if (miseAJourDesCheminsDesCreatures)
                miseAJourDesCheminsDesCreatures();
        }
    }

    /**
     * Permet de desactiver un zone rectangulaire du maillage.
     * 
     * @param zone la zone rectangulaire a desactiver
     * @param miseAJourDesCheminsDesCreatures faut-il mettre a jour les chemins
     *            des creatures ?
     */
    public void desactiverZone(Rectangle zone,
            boolean miseAJourDesCheminsDesCreatures)
    {
        // desactivation de la zone
        if(MAILLAGE_TERRESTRE != null)
            MAILLAGE_TERRESTRE.desactiverZone(zone, true);
        
        // mise a jour des chemins si necessaire
        if (miseAJourDesCheminsDesCreatures)
            miseAJourDesCheminsDesCreatures();
    }

    /**
     * Permet de mettre a jour les chemins des creatures lors de la modification
     * du maillage.
     */
    synchronized private void miseAJourDesCheminsDesCreatures()
    {
        // Il ne doit pas y avoir de modifications sur la collection
        // durant le parcours.
        Creature creature;
        Enumeration<Creature> eCreatures = jeu.getCreatures().elements();
        while(eCreatures.hasMoreElements())
        {
            creature = eCreatures.nextElement();
        
            // les tours n'affecte que le chemin des creatures terriennes
            if (creature.getType() == Creature.TYPE_TERRIENNE)   
            {
                Rectangle zoneArrivee = creature.getEquipeCiblee().getZoneArriveeCreatures();
                
                try
                { 
                    creature.setChemin(getCheminLePlusCourt((int) creature
                            .getCenterX(), (int) creature.getCenterY(),
                            (int) zoneArrivee.getCenterX(),
                            (int) zoneArrivee.getCenterY(), creature
                                    .getType()));
                }
                catch (PathNotFoundException e)
                {
                    /*
                     *  s'il n'y a pas de chemin, 
                     *  on essaye depuis le noeud precedent
                     */
                    try
                    {
                        ArrayList<Point> chemin = creature.getChemin();
                        
                        if(chemin != null)
                        {
                            // recuperation du noeud precedent sur le chemin
                            Point noeudPrecedent;
                            
                            if(creature.getIndiceCourantChemin() > 0) // pas au depart
                                noeudPrecedent = chemin.get(creature.getIndiceCourantChemin()-1);
                            else
                                noeudPrecedent = new Point(zoneArrivee.x, zoneArrivee.y);
         
                            // calcul du nouveau chemin
                            creature.setChemin(getCheminLePlusCourt(
                                    (int) noeudPrecedent.x, 
                                    (int) noeudPrecedent.y,
                                    (int) zoneArrivee.getCenterX(),
                                    (int) zoneArrivee.getCenterY(), 
                                    creature.getType())); 
                        } 
                    }
                    catch (PathNotFoundException e2)
                    {
                        // s'il n'y a toujours pas de chemin, on garde l'ancien.
                    }
                }
            }
        }
    }

    /**
     * Permet de recuperer le chemin le plus court entre deux points sur le
     * terrain.
     * 
     * Cette methode fait appel au maillage pour decouvrir ce chemin
     * 
     * @param xDepart la position x du point de depart
     * @param yDepart la position y du point de depart
     * @param xArrivee la position x du point d'arrivee
     * @param yArrivee la position y du point d'arrivee
     * @return le chemin sous la forme d'un ArrayList de java.awt.Point ou
     *         <b>null si aucun chemin ne relie les deux points</b>.
     * @throws PathNotFoundException
     * @throws IllegalArgumentException
     * @see Maillage
     */
    public ArrayList<Point> getCheminLePlusCourt(int xDepart, int yDepart,
            int xArrivee, int yArrivee, int typeCreature)
            throws IllegalArgumentException, PathNotFoundException
    {
        // TODO adapter pour chemin aérien
        if (typeCreature == Creature.TYPE_TERRIENNE)
            return MAILLAGE_TERRESTRE.plusCourtChemin(xDepart, yDepart,
                    xArrivee, yArrivee);
        else
            return MAILLAGE_AERIEN.plusCourtChemin(xDepart, yDepart,
                    xArrivee, yArrivee);
    }

    /**
     * Permet de recuperer la liste des arcs actifs du maillage terrestre.
     * 
     * @return une collection de java.awt.geom.Line2D representant les arcs
     *         actifs du maillage
     */
    public Line2D[] getArcsActifs()
    {
        return MAILLAGE_TERRESTRE.getArcs();
    }

    /**
     * Permet de recuperer les noeuds du maillage terrestre
     * 
     * @return Une collection de noeuds
     */
    public Node[] getNoeuds()
    {
        return MAILLAGE_TERRESTRE.getNoeuds();
    }

    // -------------
    // -- MUSIQUE --
    // -------------

    /**
     * Permet de demarrer la lecture de la musique d'ambiance
     */
    public void demarrerMusiqueDAmbiance()
    {
        if (fichierMusiqueDAmbiance != null)
        {
            Son musiqueDAmbiance = new Son(fichierMusiqueDAmbiance);

            SoundManagement.ajouterSon(musiqueDAmbiance);
            musiqueDAmbiance.lire(0); // lecture infinie
        }
    }

    /**
     * Permet d'arreter la lecture de la musique d'ambiance
     */
    public void arreterMusiqueDAmbiance()
    {
        if (fichierMusiqueDAmbiance != null)
            SoundManagement.arreterTousLesSons(fichierMusiqueDAmbiance);
    }

    /**
     * Permet de sérialisé un Terrain
     * 
     * @param terrain le terrain à sérialiser
     * @param fichier le fichier de destination
     * @throws IOException
     */
    public static void serialiser(Field terrain, File fichier)
            throws IOException
    {
        // File fichier = new File("maps/"+terrain.getNom()+".map");

        // Ouverture d'un flux de sortie vers le fichier FICHIER.
        FileOutputStream fluxSortieFichier = new FileOutputStream(fichier);

        // Creation d'un "flux objet" avec le flux fichier.
        ObjectOutputStream fluxSortieObjet = new ObjectOutputStream(
                fluxSortieFichier);
        try
        {
            // Serialisation : ecriture de l'objet dans le flux de sortie.
            fluxSortieObjet.writeObject(terrain);

            // On vide le tampon.
            fluxSortieObjet.flush();
        } finally
        {
            // Fermeture des flux (important!).
            try
            {
                fluxSortieObjet.close();
            } finally
            {
                fluxSortieFichier.close();
            }
        }
    }
    
    /**
     * Permet de charger un terrain serialisé
     * 
     * @param fichier le fichier Terrain serialisé
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public static Field charger(File fichier) throws IOException, ClassNotFoundException, ClassCastException
    {
       
      // Ouverture d'un flux d'entree depuis le fichier FICHIER.
      FileInputStream fluxEntreeFichier = new FileInputStream(fichier);
      // Creation d'un "flux objet" avec le flux d'entree.
      ObjectInputStream fluxEntreeObjet = new ObjectInputStream(fluxEntreeFichier);
      try
      {
         // Deserialisation : lecture de l'objet depuis le flux d'entree
         // (chargement des donnees du fichier).
          
         Field terrain = (Field) fluxEntreeObjet.readObject();
         
         terrain.setNomFichier(fichier.getName());
         
         // seule les ImageIcon peuvent etre serialisée 
         // donc la on met a jour l'image de font avec une ImageIcon
         if(terrain.iconImageDeFond != null)
             terrain.imageDeFond = terrain.iconImageDeFond.getImage();
        
         return terrain ;
      }
      finally
      {
         // On ferme les flux (important!).
         try{
            fluxEntreeObjet.close();
         }
         finally
         {
            fluxEntreeFichier.close();
         }
      }
    }

    public void setJeu(Game jeu)
    {
        this.jeu = jeu;
    }

    public void setCouleurDeFond(Color couleurDeFond)
    {
        this.couleurDeFond = couleurDeFond;
    }

    public void setLargeurMaillage(int largeurMaillage)
    {
        if(largeurMaillage <= 0)
            throw new IllegalArgumentException("la largeur doit être > 0");
        
        this.largeurMaillage = largeurMaillage;
    }

    public void setHauteurMaillage(int hauteurMaillage)
    {
        if(hauteurMaillage <= 0)
            throw new IllegalArgumentException("la hauteur doit être > 0");
        
        this.hauteurMaillage = hauteurMaillage;
    }

    public void supprimerMur(Rectangle mur)
    {
        murs.remove(mur);
    }

    public String getNomFichier()
    {
        return nomFichier;
    }
    
    public void setNomFichier(String nomFichier)
    {
        this.nomFichier = nomFichier;
    }

    public void setCouleurMurs(Color couleur)
    {
        couleurMurs = couleur;
    }

    public void setBreveDescription(String breveDescription)
    {
       this.breveDescription = breveDescription;   
    }
    
    
    public void setLargeur(int largeur)
    {
        if(largeur <= 0)
            throw new IllegalArgumentException("la largeur doit être > 0");

        this.largeur = largeur;
    }

    public void setHauteur(int hauteur)
    {
        if(hauteur <= 0)
            throw new IllegalArgumentException("la hauteur doit être > 0");
        
        this.hauteur = hauteur;
    }

    public void setNbPiecesOrInitiales(int nbPiecesOrInitiales)
    {
        if(nbPiecesOrInitiales < 0)
            throw new IllegalArgumentException("la nombre de pieces d'or doit être >= 0");
        
        this.nbPiecesOrInitiales = nbPiecesOrInitiales;
    }

    public void setNbViesInitiales(int nbViesInitiales)
    {
        if(nbViesInitiales <= 0)
            throw new IllegalArgumentException("la nombre de vies doit être > 0");
 
        this.nbViesInitiales = nbViesInitiales;
    }
    
    public float getOpaciteMurs()
    {
        return opaciteMurs;
    }
    
    public void setOpaciteMurs(float opaciteMurs)
    {
        this.opaciteMurs = opaciteMurs;
    }

    public void setModeDeJeu(int modeDeJeu)
    {
        this.modeDeJeu = modeDeJeu;
    }

    public void setFichierMusiqueDAmbiance(File fichierMusiqueDAmbiance) {
        this.fichierMusiqueDAmbiance = fichierMusiqueDAmbiance;
    }
}