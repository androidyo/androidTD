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

package models.game;

import i18n.Language;

import java.awt.Graphics2D;
import java.util.*;

import utils.myTimer;
import exceptions.*;
import models.animations.*;
import models.creatures.*;
import models.map.*;
import models.player.*;
import models.towers.*;

/**
 * 游戏（发动机）主要类
 * 
 * 它封装了游戏所有的元素并且进行管理.
 * 
 * @author Aurelien Da Campo
 * @version 2.1 | mai 2010
 * @since jdk1.6.0_16
 * @see Field
 * @see ManagerTowers
 * @see ManagerCreatures
 * @see ManageAnimations
 */
public abstract class Game implements PlayerListener,
                                     CreatureListener, 
                                     EcouteurDeVague
{
	/**
	 * version of the game
	 */
    private static final String VERSION 
        = "ASD - Tower Defense v2.0 (beta 4) | nov 2010 | heig-vd";
    
    /**
     * Positioning mode centered creatures in the starting area
     * 集中定位在起步区的生物
     */
    public static final int MODE_POSITIONNNEMENT_CENTRE = 0;
    
    /**
     * Positioning mode random creatures in the starting area.
     * 起步区生物随机位置
     */
    public static final int MODE_POSITIONNNEMENT_ALETOIRE = 1;
    
    /**
     * Positioning mode power creatures in the starting area
     * 起步区定位模式
     */
    private static final int MODE_DE_POSITIONNEMENT = MODE_POSITIONNNEMENT_ALETOIRE;

    /**
     * Step increment of the coefficient of speed of the game
     * 游戏速度
     */
    private static final double ETAPE_COEFF_VITESSE = 0.5;

    /**
     * Maximum coefficient of the speed of the game
     * 最高游戏速度系数
     */
    private static final double MAX_COEFF_VITESSE = 5.0;
    
    /**
     * Minimum coefficient of the speed of the game
     * 最低游戏速度系数
     */
    private static final double MIN_COEFF_VITESSE = 0.1; // /!\ >0 /!\
      
	/**
	 * The playing field that contains all the main elements:
	 * - Les tours
	 * - Les creatures
	 * - Le maillage
	 * 游戏地图，包含所有的主要内容：
	 * -塔
	 * -生物
	 *- 网络
	 */
    protected Field terrain;
	
	/**
	 * Collection teams in game
	 */
	protected ArrayList<Team> teams= new ArrayList<Team>();
	
    /**
     * The towers are placed on the ground and can kill the creatures.
     * 
     * @see Tower
     */
	protected ManagerTowers managerTowers;

    /**
     * The creatures move on the ground of an area of ​​a starting zone
     * of arrival.
     * 
     * @see Creature
     */
	protected ManagerCreatures managerCreatures;

    /**
     * Management tool animations
     */
	protected ManageAnimations managerAnimations;

    /**
     * Variable d'etat de la pause
     */
	protected boolean enPause;
  
    /**
     * Gestion des vagues de creatures. C'est le joueur que decident le moment
     * ou il veut lancer une vague de creatures. Une fois que toutes les vagues
     * de creatures ont ete detruites, le jeu est considere comme termine.
     */
    protected int indiceVagueCourante = 1;
	
    /**
     * Stockage de la vagues courante
     */
    WaveOfCreatures vagueCourante;
    
    
    /**
     * Permet de savoir si la partie est initialisée
     */
    protected boolean estInitialise;
    
    /**
     * Permet de savoir si la partie est à été démarrée
     */
    private boolean estDemarre;
    
    /**
     * Permet de savoir si la partie est terminée
     */
    protected boolean estTermine;
    
    /**
     * Permet de savoir si la partie est détruite
     */
    protected boolean estDetruit;
    
    /**
     * Pour notifications (observable)
     */
    protected GameListener edj;
    
    /**
     * Joueur principal  
     */
    protected Player joueur;

    /**
     * Timer pour gérer le temps de jeu
     */
    protected myTimer timer = new myTimer(1000,null);

    /**
     * Coefficient de vitesse de déroulement de la partie.
     * 
     * Permet de résoudre les problèmes de lenteur du jeu.
     */
    private double coeffVitesse;

    /**
     * Constructeur
     */
    public Game()
    {
        managerTowers      = new ManagerTowers(this);
        managerCreatures  = new ManagerCreatures(this);
        managerAnimations = new ManageAnimations(this);
    }
    
    /**
     * Permet d'initialiser la partie avant le commencement
     * 
     * @param joueur 
     */
    synchronized public void initialiser()
    {
        if(terrain == null)
            throw new IllegalStateException("Terrain nul");
        
        if(teams.size() == 0)
            throw new IllegalStateException("Aucune équipe inscrite");
        
        // le joueur principal
        //if(joueur != null)
        //    setJoueurPrincipal(joueur);
        
        // attributs
        indiceVagueCourante = 1;
        enPause             = false;
        estDemarre          = false;
        estTermine          = false;
        estDetruit          = false;
        vagueCourante       = null;
        coeffVitesse        = 1.0;
        
        // initialisation des valeurs par defaut
        for(Team equipe : teams)
        {
            // initialisation des vies restantes
            equipe.setNbViesRestantes(terrain.getNbViesInitiales());
            
            // initialisation des pieces d'or des joueurs
            for(Player j : equipe.getJoueurs())
            {
                j.setScore(0);
                j.setNbPiecesDOr(terrain.getNbPiecesOrInitiales());
            }
        }  
        
        estInitialise = true;
        
        if(edj != null)
            edj.initializationPart();
    }
    
    public void reinitialiser()
    {
        terrain.arreterMusiqueDAmbiance();
        
        // réinitialisation du terrain
        terrain.reinitialiser();
        
        estInitialise = false;
        estDemarre = false;
        
        initialiser();
        
        
        // arret des gestionnaires
        managerTowers.arreterTours();
        managerCreatures.arreterCreatures();
        managerAnimations.stopAnimations();
        
        managerTowers = new ManagerTowers(this);
        managerCreatures = new ManagerCreatures(this);
        managerAnimations = new ManageAnimations(this);
        
        
        
        // ajout de tous les joueurs
        for(Team e : teams)
        {
            e.setNbViesRestantes(terrain.getNbViesInitiales());
            
            for(Player j : e.getJoueurs())    
            {
                j.setNbPiecesDOr(terrain.getNbPiecesOrInitiales());
                j.setScore(0);
            }
        }
        
        // arret du timer
        timer.stop();
        timer = new myTimer(1000,null);
    }

    /**
     * Permet de démarrer la partie
     */
    public void demarrer()
    {
        if(terrain == null)
            throw new IllegalStateException("Terrain nul");
        
        if(!estInitialise)
            throw new IllegalStateException("Le jeu n'est pas initialisé");
            
        if(estDemarre)
            throw new IllegalStateException("Le jeu est déjà démarré");
        
        // demarrage des gestionnaires
        managerTowers.demarrer();
        managerCreatures.demarrer();
        managerAnimations.start();
        
        timer.start();
        
        estDemarre = true;
        
        // notification
        if(edj != null)
            edj.startPart();
    }
    
    /**
     * Indique au jeu qu'une vague veut etre lancée
     * 
     * @param vague la vague
     * @throws MoneyLackException 
     */
    public void lancerVague(Player joueur, Team cible, WaveOfCreatures vague) throws MoneyLackException
    { 
        managerCreatures.lancerVague(vague, joueur, cible, this, this);
    }
    
    /**
     * Permet de poser un tour
     * 
     * @param tour la tour
     * @throws Exception si c'est pas possible
     */
    public void poserTour(Tower tour) throws MoneyLackException, ZoneInaccessibleException, BarrierException
    {
        // c'est bien une tour valide ?
        if (tour == null)
            throw new IllegalArgumentException("Tour nulle");

        // suffisemment d'argent ?
        if(!laTourPeutEtreAchetee(tour))    
            throw new MoneyLackException(Language.getTexte(Language.ID_ERROR_POSE_IMPOSSIBLE_PAS_ASSEZ_D_ARGENT));
        
        // si elle peut pas etre posee
        if (!laTourPeutEtrePosee(tour))
            throw new ZoneInaccessibleException(Language.getTexte(Language.ID_ERROR_POSE_IMPOSSIBLE_ZONE_INACCESSIBLE));

        // si elle bloque le chemin de A vers B
        if (terrain.laTourBloqueraLeChemin(tour))
            throw new BarrierException(Language.getTexte(Language.ID_ERROR_POSE_IMPOSSIBLE_CHEMIN_BLOQUE));
        
        // desactive la zone dans le maillage qui correspond a la tour
        terrain.desactiverZone(tour, true);

        // ajout de la tour
        managerTowers.ajouterTour(tour);
        
        // mise a jour du jeu de la tour
        tour.setJeu(this);
        
        // mise en jeu de la tour
        tour.mettreEnJeu();
        
        // debit des pieces d'or
        tour.getPrioprietaire().setNbPiecesDOr(
                tour.getPrioprietaire().getNbPiecesDOr() - tour.getPrixAchat());
    
       
        //ajouterAnimation(new Fumee((int)tour.getCenterX(),(int)tour.getCenterY()));
        
        if(edj != null)
            edj.towerPlaced(tour);
    }
    
    
    /**
     * Permet de vendre une tour.
     * 
     * @param tour la tour a vendre
     * @throws ActionUnauthorizedException 
     */
    public void vendreTour(Tower tour) throws ActionUnauthorizedException
    {
        // supprime la tour
        managerTowers.supprimerTour(tour);
        
        // debit des pieces d'or
        tour.getPrioprietaire().setNbPiecesDOr(
                tour.getPrioprietaire().getNbPiecesDOr() + tour.getPrixDeVente());
    
        ajouterAnimation(new Smoke((int)tour.getCenterX(),(int)tour.getCenterY()));
        
        if(edj != null)
            edj.towerSold(tour);
    }
 
    
    /**
     * Permet d'ameliorer une tour.
     * 
     * @param tour la tour a ameliorer
     * @return vrai si operation realisee avec succes, sinon faux 
     * @throws MoneyLackException si pas assez d'argent 
     * @throws ReachMaxLevelException si niveau max de la tour atteint
     * @throws ActionUnauthorizedException 
     * @throws JoueurHorsJeu 
     */
    public void ameliorerTour(Tower tour) throws ReachMaxLevelException, MoneyLackException, ActionUnauthorizedException, JoueurHorsJeu
    {
        if(!tour.peutEncoreEtreAmelioree())
            throw new ReachMaxLevelException(Language.getTexte(Language.ID_ERROR_AMELIORATON_IMPOSSIBLE_NIVEAU_MAX_ATTEINT));
        
        if(tour.getPrioprietaire().getNbPiecesDOr() < tour.getPrixAchat())
            throw new MoneyLackException(Language.getTexte(Language.ID_ERROR_AMELIORATON_IMPOSSIBLE_PAS_ASSEZ_D_ARGENT));

        // debit des pieces d'or
        tour.getPrioprietaire().setNbPiecesDOr(tour.getPrioprietaire().getNbPiecesDOr() - tour.getPrixAchat());
     
        // amelioration de la tour
        tour.ameliorer();
        
        if(edj != null)
            edj.towerUpgrade(tour);
    }
    
    /**
     * Permet de recuperer la version du jeu.
     * 
     * @return la version du jeu.
     */
    public static String getVersion()
    {
        return VERSION;
    }

	/**
	 * Permet de lancer une nouvelle vague de creatures.
	 */
	public void lancerVagueSuivante(Player joueur, Team cible)
	{
	    // lancement de la vague
	    WaveOfCreatures vagueCourante = terrain.getVagueDeCreatures(indiceVagueCourante);
        
	    passerALaProchaineVague();
	    
	    managerCreatures.lancerVague(vagueCourante, joueur, cible, this, this);
	}
	
	/**
     * Permet de savoir si la partie est terminée
     * 
     * @return true si elle l'est false sinon
     */
	public boolean estTermine()
	{
	    return estTermine;
	}
	

    /**
     * Permet de terminer la partie en cours
     */
    public void terminer()
    {
        if(!estTermine)
        {
            estTermine = true;
            
            arreterTout();
              
            Team equipeGagnante = null;
            int maxScore = -1;
            
            // FIXME gestion des égalités !

            // selection des equipes en jeu
            ArrayList<Team> equipesEnJeu = new ArrayList<Team>();
            for(Team equipe : teams)
                if(!equipe.aPerdu())
                {
                    // selection de l'equipe gagnante
                    if(equipe.getScore() > maxScore)
                    {
                        equipeGagnante = equipe;
                        maxScore = equipe.getScore();
                    }
                    
                    equipesEnJeu.add(equipe);
                }
            
            if(edj != null)
                edj.terminatePart(new GameResult(equipeGagnante)); // TODO check
        }
    }

    /**
     * To initialize the playing field
     * 
     * @param field le terrain
     * @throws IllegalArgumentException si le terrain à déjà été initialisé.
     */
    public void setField(Field field) throws IllegalArgumentException
    {
        // J'ai mis en commentaire! pour le chargement dans l'editeur de niveau
        //if(this.terrain != null) 
        //    throw new TerrainDejaInitialise("Terrain déjà initialisé");

        teams = field.getEquipesInitiales();
        
        this.terrain  = field;  
    }

    /**
     * Permet de recuperer le terrain
     * 
     * @return le terrain
     * @throws Exception si le terrain est nul
     */
    public Field getTerrain()
    {
        if(terrain == null)
            throw new NullPointerException("Le terrain ne doit jamais etre nul !");
        
        return terrain;
    }
    
    /**
     * Permet de recuperer le gestionnaire de creatures
     * 
     * @return le gestionnaire de creatures
     */
    public Vector<Creature> getCreatures()
    {
        return managerCreatures.getCreatures();
    }

    /**
     * Permet de stope tous les threads des elements
     */
    protected void arreterTout()
    {
        // arret de toutes les tours
        managerTowers.arreterTours();

        // arret de toutes les creatures
        managerCreatures.arreterCreatures();

        // arret de toutes les animations
        managerAnimations.stopAnimations();
        
        // arret du timer
        timer.stop();
    }

    /**
     * Permet de mettre en pause le jeu.
     * 
     * @return true si le jeu est en pause après l'appel false sinon
     */
    public boolean togglePause()
    {
        if(enPause)
        {
            managerTowers.sortirDeLaPause();
            managerCreatures.sortirDeLaPause();
            managerAnimations.sortirDeLaPause();
            timer.play();
        }
        else
        {
            managerTowers.mettreEnPause();
            managerCreatures.mettreEnPause();
            managerAnimations.enablePause();
            timer.pause();
        }
        
        return enPause = !enPause;  
    }

    /**
     * Permet de savoir si le jeu est en pause
     * 
     * @return true s'il l'est false sinon
     */
    public boolean estEnPause()
    {
        return enPause;
    }

    /**
     * Permet de recupérer la collection des équipes
     * 
     * @return la collection des équipes
     */
    public ArrayList<Team> getEquipes()
    {
        return teams;
    }

    /**
     * Retourne une collection avec tous les joueurs fesant partie
     * d'une des équipes du jeu.
     * 
     * @return les joueurs
     */
    public ArrayList<Player> getJoueurs()
    {
        // création de la collection
        ArrayList<Player> joueurs = new ArrayList<Player>();
        
        // ajout de tous les joueurs
        for(Team e : teams)
            for(Player j : e.getJoueurs())
                joueurs.add(j);
        
        // retour
        return joueurs;
    }
    
    /**
     * Permet d'ajouter un joueur dans le premier emplacement disponible
     * 
     * @param joueur le joueur
     * @throws CurrentGameException Si la partie à déjà démarrée
     * @throws NoPositionAvailableException Aucune place disponible dans les équipes.
     */
    public void ajouterJoueur(Player joueur) throws CurrentGameException, NoPositionAvailableException
    {
        // si la partie est en court
        if(estDemarre)
            throw new CurrentGameException("La partie à déjà démarrée");
        
        // ajout du joueur dans le premier emplacement disponible
        for(int i=0;i<teams.size();i++)
        {
            try
            {              
                // on tente l'ajout...
                teams.get(i).ajouterJoueur(joueur);
                
                // ajout de l'ecouteur
                joueur.setEcouteurDeJoueur(this);
                
                // notification
                if(edj != null)
                    edj.playerJoin(joueur);
  
                return; // équipe trouvée
            }
            catch (NoPositionAvailableException e)
            {
                // on essaye encore...
            }
        }
        
        throw new NoPositionAvailableException("Aucune place disponible.");
    }

    /**
     * Permet de modifier l'écouteur de jeu
     * 
     * @param edj l'écouteur de jeu
     */
    public void setEcouteurDeJeu(GameListener edj)
    {
        this.edj = edj;
    }
    
    /**
     * Permet de recuperer le joueur principal du jeu
     * 
     * @param joueur le joueur principal du jeu
     */
    public Player getJoueurPrincipal()
    { 
        return joueur;
    }
    
    /**
     * Permet de modifier le joueur principal du jeu
     * 
     * @param joueur le joueur principal du jeu
     */
    public void setJoueurPrincipal(Player joueur)
    {
        this.joueur = joueur;
        
        // mis à jour de l'écouteur
        joueur.setEcouteurDeJoueur(this);
    }
    
    @Override
    public void creatureDead(Creature creature)
    {
        if(edj != null)
            edj.creatureInjured(creature);
        
        ajouterAnimation(new HitInjured((int)creature.getCenterX(),(int) creature.getCenterY()));
    }

    @Override
    synchronized public void creatureHurt(Creature creature, Player tueur)
    {
        // gain de pieces d'or
        tueur.setNbPiecesDOr(tueur.getNbPiecesDOr() + creature.getNbPiecesDOr());
        
        // nombre d'etoile avant l'ajout du score
        int nbEtoilesAvantAjoutScore = tueur.getNbEtoiles();
        
        // augmentation du score
        tueur.setScore(tueur.getScore() + creature.getNbPiecesDOr());

        // nouvelle étoile ?
        if(nbEtoilesAvantAjoutScore < tueur.getNbEtoiles())
            if(edj != null)  
                edj.winStar();
 
        // notification de la mort de la créature
        if(edj != null)
            edj.creatureKilled(creature,tueur);
    }

    @Override
    synchronized public void creatureArriveEndZone(Creature creature)
    {
        Team equipe = creature.getEquipeCiblee();
        
        // si pas encore perdu
        if(!equipe.aPerdu())
        {
            equipe.perdreUneVie();
            
            if(edj != null)
            {
                edj.creatureArriveEndZone(creature);
                
                // FIXME IMPORTANT faire plutot une mise a jour des donnees de l'equipe 
                // -> ajout au protocole EQUIPE_ETAT
                // et appeler plutot edj.equipeMiseAJour(equipe) 
                // pour tous les joueurs de l'equipe
                for(Player joueur : equipe.getJoueurs())
                    edj.joueurMisAJour(joueur);
            }
            
            // controle de la terminaison du jeu.
            if(equipe.aPerdu())
            {
                if(edj != null)
                    edj.teamLost(equipe);
                
                // s'il il reste au moins deux equipes en jeu
                // la partie n'est pas terminée
                int nbEquipesRestantes = 0;
                for(Team tmpEquipe : teams)
                    if(!tmpEquipe.aPerdu())
                        nbEquipesRestantes++;
             
                // fin de la partie
                if(nbEquipesRestantes <= 1)
                    terminer();
            }
        } 
    }

    @Override
    public void vagueEntierementLancee(WaveOfCreatures vagueDeCreatures)
    {
        if(edj != null)
            edj.waveAttackFinish(vagueDeCreatures); 
    }
    
    @Override
    public void joueurMisAJour(Player joueur)
    {
        if(edj != null)
            edj.joueurMisAJour(joueur);
    }

    /**
     * Permet de savoir si une tour peut etre posee.
     * 
     * Controle de l'intersection avec les tours.
     * Controle de l'intersection avec les creatures.
     * Controle de l'intersection avec les zones du terrain. (murs et depart / arrive)
     * 
     * @param tour la tour a posee
     * @return true si la tour peut etre posee, false sinon
     */
    public boolean laTourPeutEtrePosee(Tower tour)
    {
        return managerTowers.laTourPeutEtrePosee(tour);
    }
    
    /**
     * Permet de savoir si une tour peut etre achetee.
     * 
     * @param tour la tour a achetee
     * @return true si le joueur a assez de pieces d'or, false sinon
     */
    public boolean laTourPeutEtreAchetee(Tower tour)
    {  
        return managerTowers.laTourPeutEtreAchetee(tour);
    }

    /**
     * Permet de recuperer une copie de la collection des tours
     */
    public Vector<Tower> getTours()
    {
        return managerTowers.getTours();
    }

    /**
     * Permet de récupérer les créatures qui intersectent un cercle
     *  
     * @param centerX centre x du cercle
     * @param centreY centre y du cercle
     * @param rayon rayon du cercle
     * @return Une collection de créatures
     */
    public Vector<Creature> getCreaturesQuiIntersectent(int centerX, int centreY,
            int rayon)
    {
        return managerCreatures.getCreaturesQuiIntersectent(centerX, centreY, rayon);
    }

    /**
     * Permet de nofifier le jeu qu'un créature à été ajoutée
     * @param creature
     */
    public void creatureAjoutee(Creature creature)
    {
        if(edj != null)
            edj.creatureAjoutee(creature);
    }

    /**
     * Permet d'ajouter une animation
     * 
     * @param animation l'animation à ajouter
     */
    public void ajouterAnimation(Animation animation)
    {
        managerAnimations.addAnimation(animation);
        
        if(edj != null)
            edj.animationAjoutee(animation);
    }

    /**
     * Permet de dessiner toutes les animations
     * 
     * @param g2 le Graphics2D
     * @param hauteur la hauteur des animations
     * @see Animation.HAUTEUR_SOL
     * @see Animation.HAUTEUR_AIR
     */
    public void dessinerAnimations(Graphics2D g2, int hauteur)
    {
        managerAnimations.dessinerAnimations(g2,hauteur);
    }
    
    /**
     * Permet de recuperer un joueur grace a son identificateur
     * 
     * @param idJoueur identificateur du joueur
     * 
     * @return le joueur ou null
     */ 
    public Player getJoueur(int idJoueur)
    {
        ArrayList<Player> joueurs = getJoueurs();

        for(Player joueur : joueurs)
            if(joueur.getId() == idJoueur)
                return joueur;
        
        return null;
    }

    /**
     * Permet de recuperer une équipe grace a son identificateur
     * 
     * @param idEquipe identificateur de l'équipe
     * 
     * @return l'équipe ou null
     */
    public Team getEquipe(int idEquipe)
    {
        for(Team equipe : teams)
            if(equipe.getId() == idEquipe)
                return equipe;
        
        return null;
    }
    
    /**
     * Permet de recuperer un emplacement grace a son identificateur
     * 
     * @param idEmplacement identificateur de l'emplacement
     * 
     * @return l'emplacement ou null
     */
    public PlayerLocation getEmplacementJoueur(int idEmplacement)
    {
        for(Team equipe : teams)
            for(PlayerLocation ej : equipe.getEmplacementsJoueur())
                if(ej.getId() == idEmplacement)
                    return ej;

        return null;
    }
    
    /**
     * Permet de recuperer une tour à l'aide de son identificateur
     * 
     * @param idTour l'identificateur de la tour
     * @return la tour trouvée ou null
     */
    public Tower getTour(int idTour)
    {
        for (Tower tour : getTours())
            if (tour.getId() == idTour)
                return tour;
        
        return null;
    }
    
    /**
     * Permet de recuperer une créature grace a son identificateur
     * 
     * @param idCreature identificateur de la créature
     * 
     * @return la créature ou null
     */
    public Creature getCreature(int idCreature)
    {
        return managerCreatures.getCreature(idCreature);
    }

    /**
     * Permet de recuperer l'equipe valide suivante 
     * (qui contient au moins un joueur)
     * 
     * Les équipes qui ont perdue ne sont pas prises en compte.
     * 
     * S'il n'y a pas d'équipe valide suivante, l'équipe en paramètre 
     * est retournée.
     * 
     * @param equipe l'équipe suivante de quelle équipe ?
     * @return l'équipe suivant qui peut-être la même équipe (si seule)
     */
    public Team getEquipeSuivanteNonVide(Team equipe)
    {
        // on trouve l'equipe directement suivante
        int i = (teams.indexOf(equipe)+1) % teams.size();
        
        // tant qu'il n'y a pas de joueur ou que l'équipe a perdue
        // on prend la suivante...
        // au pire on retombera sur la même equipe qu'en argument
        while(teams.get(i).getJoueurs().size() == 0 || teams.get(i).aPerdu())
            i = ++i % teams.size();
        
        return teams.get(i);
    }

    /**
     * Permet de savoir si le jeu a été initialisé
     * 
     * @return true s'il l'est false sinon
     */
    public boolean estInitialise()
    {
        return estInitialise;
    }
    
    /**
     * Permet de savoir si le jeu a été démarré
     * 
     * @return true s'il l'est false sinon
     */
    public boolean estDemarre()
    {
        return estDemarre;
    }

    /**
     * Permet de recuperer le numero de la vague courante
     * 
     * @return le numero de la vague courante
     */
    public int getNumVagueCourante()
    {
        return indiceVagueCourante;
    }
    
    /**
     * Permet de passer à la prochaine vague
     */
    public void passerALaProchaineVague()
    {
        ++indiceVagueCourante;
    }

    /**
     * Permet de recuperer le timer
     * 
     * @return
     */
    public myTimer getTimer()
    {
        return timer;
    }

    /**
     * Permet de détruire le jeu.
     * 
     * Détruit tous les objets du jeu.
     */
    public void detruire()
    {
        estDetruit = true;
        
        managerCreatures.detruire();
        managerTowers.detruire();
        managerAnimations.destroy();
    }
    
    /**
     * Permet de savoir si le jeu a été détruit
     * 
     * @return true s'il l'est false sinon
     */
    public boolean estDetruit()
    {
        return estDetruit;
    }

    /**
     * Permet de récupérer le coefficient de vitesse du jeu
     * 
     * @return le coefficient de vitesse du jeu
     */
    public double getCoeffVitesse()
    {
        return coeffVitesse;
    }

    /**
     * Permet d'augmenter le coefficient de vitesse du jeu.
     * 
     * @return retour la nouvelle valeur du coefficient
     */
    public synchronized double augmenterCoeffVitesse()
    {
        if(coeffVitesse + ETAPE_COEFF_VITESSE <= MAX_COEFF_VITESSE)
        {    
            coeffVitesse += ETAPE_COEFF_VITESSE;
            
            if(edj != null)
                edj.velocityChanged(coeffVitesse);
        }
        
        return coeffVitesse;
    }

    /**
     * Permet de diminuer le coefficient de vitesse du jeu.
     * 
     * @return retour la nouvelle valeur du coefficient
     */
    synchronized public double diminuerCoeffVitesse()
    {
        if(coeffVitesse - ETAPE_COEFF_VITESSE >= MIN_COEFF_VITESSE)
        { 
            coeffVitesse -= ETAPE_COEFF_VITESSE;
         
            if(edj != null)
                edj.velocityChanged(coeffVitesse);
        }    
        return coeffVitesse;
    }
    
    /**
     * Permet de modifier directement coefficient de vitesse du jeu.
     * @param value le nouveau coefficient de vitesse du jeu.
     */
    public void setCoeffVitesse(double value)
    {
        if(coeffVitesse - ETAPE_COEFF_VITESSE < MIN_COEFF_VITESSE
        && coeffVitesse + ETAPE_COEFF_VITESSE > MAX_COEFF_VITESSE)
            throw new IllegalArgumentException(
                    "Le coefficient de vitesse non authorisé");
            
        coeffVitesse = value;
        
        if(edj != null)
            edj.velocityChanged(coeffVitesse);
    }
    
    /**
     * Permet de récupérer le type de positionnement des créatures dans 
     * la zone de départ
     * 
     * @return le type de positionnement des créatures dans la zone de départ
     */
    public int getModeDePositionnnementDesCreatures()
    {
        return MODE_DE_POSITIONNEMENT;
    }
    
    /**
     * Permet d'ajouter une equipe (utilisé pour l'éditeur de terrain)
     * 
     * @param equipe la nouvelle equipe
     */
    public void ajouterEquipe(Team equipe)
    {
        teams.add(equipe);
    }
 
    /**
     * Permet de supprimer une equipe (utilisé pour l'éditeur de terrain)
     * 
     * @param equipe l'équipe à supprimer
     */
    public void supprimerEquipe(Team equipe)
    {
        teams.remove(equipe);
    }
}
