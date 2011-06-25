/*
  Copyright (C) 2010 Aurelien Da Campo, Pierre-Dominique Putallaz
  
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

package net.game.server;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import net.*;

import org.json.JSONException;

import utils.Configuration;
import exceptions.*;
import models.animations.Animation;
import models.creatures.*;
import models.game.*;
import models.player.Team;
import models.player.Player;
import models.towers.*;

/**
 * Cette classe contiendra le serveur de jeu sur lequel se connecteront tout les
 * cliens.
 * 该类包含所有连接到服务器端的客户端连接
 * 
 * @author Pierre-Do
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 */
public class ServerJeu implements ConstantsServerJeu, GameListener, Runnable
{
	/**
	 * La version courante du serveur
	 */
	public static final String VERSION = "2_0_beta";

	/**
     * Temps de rafraichissement des éléments
     */
	private long TEMPS_DE_RAFFRAICHISSEMENT = 80;
	
	/**
	 * Fanion pour le mode debug
	 */
	private static final boolean verbeux = false;

	/**
	 * Liste des clients enregistrés sur le serveur
	 */
	private HashMap<Integer, RemotePlayer> clients = new HashMap<Integer, RemotePlayer>();

	/**
	 * Lien vers le module coté serveur du jeu
	 */
	private Game jeuServeur;

	
	/**
	 * Canal d'attente de connexion de joueur
	 */
	private ChannelTCP canalAttenteConnexion = null;
	
	/**
	 * Port du canal
	 */
	private Port port;
	
	/**
	 * Référence vers le créateur du jeu
	 */
	private Player createur;
	
	/**
	 * 
	 * @param jeuServeur
	 * @throws IOException
	 */
	public ServerJeu(final Game jeuServeur) throws IOException
	{
		// Assignation du serveur
		this.jeuServeur = jeuServeur;
		
		// le serveur ecoute le jeu
		jeuServeur.setEcouteurDeJeu(this);
		
        // Réservation du port d'écoute
        port = new Port(Configuration.getPortSJ());
        
        // reservation du port
        port.reserver();
          
		// Lancement du thread serveur.
		(new Thread(this)).start();
	}

	@Override
	public void run()
	{  
        try
        {
    	    // Boucle d'attente de connections
            while (true)
            {
                try
                {
                    // On attend qu'un joueur se présente
                    log("Ecoute sur le port " + Configuration.getPortSJ());
                    
                    // Bloquant en attente d'une connexion
                    canalAttenteConnexion = new ChannelTCP(port);
                    
                    String ip = canalAttenteConnexion.getIpClient();
                    
                    // Log
                    log("Récéption de " + ip); 
                    
                    // Récéption du pseudo du joueur
                    String pseudo = canalAttenteConnexion.recevoirString();
                    
                    // Création du joueur
                    Player joueur = new Player(pseudo);
                    
                    enregistrerClient(joueur, canalAttenteConnexion);
                } 
                catch (CurrentGameException e){
    
                    log("Joueur refusé - jeu est en cours");
                    
                    // Envoye de la réponse
                    canalAttenteConnexion.envoyerString(Protocol.construireMsgJoueurInitialisation(JEU_EN_COURS));   
                }
                catch (NoPositionAvailableException e){
                    
                    log("Joueur refusé - aucune place disponible");
    
                    // Envoye de la réponse
                    canalAttenteConnexion.envoyerString(Protocol.construireMsgJoueurInitialisation(PAS_DE_PLACE));
                }
            }
        }  
        catch (ChannelException e)
        {
            canalErreur(e);
        }       
	}

    private synchronized void enregistrerClient(Player joueur, ChannelTCP canal) 
        throws CurrentGameException, NoPositionAvailableException
	{
        try
        {
            // Ajout du joueur à l'ensemble des joueurs
            jeuServeur.ajouterJoueur(joueur);
            
            // Log
            log("Nouveau joueur ! ID : " + joueur.getId());
            
            // On vérifie que l'ID passé en paramêtre soit bien unique
    		if (clients.containsKey(joueur.getId()))
    		{
    			log("ERROR : Le client " + joueur.getId() + " est déjà dans la partie");
    			
    			// FIXME On déconnecte le client; 
                canal.fermer();
    		} 
    		else
    		{
    		    // le premier joueur qui se connect est admin
                if(createur == null)
                    createur = joueur;
    		    
    		    // Envoye de la réponse
                canal.envoyerString(Protocol.construireMsgJoueurInitialisation(joueur, jeuServeur.getTerrain()));
    
    		    // On inscrit le joueur à la partie
                RemotePlayer jd = new RemotePlayer(joueur, canal, this);
    			clients.put(joueur.getId(), jd);
    			
    			// Notification des clients 
    	        envoyerATous(Protocol.construireMsgJoueursEtat(jeuServeur.getJoueurs()));
    		}
		
        } 
        catch (ChannelException e)
        {
            canalErreur(e);
        }
	}

    /**************** NOTIFICATIONS **************/

	@Override
	public void creatureArriveEndZone(Creature creature)
	{
	    envoyerATous(Protocol.construireMsgCreatureArrivee(creature));
	}

	@Override
	public void creatureInjured(Creature creature)
	{
	    // detectable par les clients lors de la mise a jour par l'état d'une creature
	}


	@Override
	public void creatureKilled(Creature creature,Player tueur)
	{
	    // Multicast aux clients
	    envoyerATous(Protocol.construireMsgCreatureSuppression(creature,tueur));
	}

    @Override
	public void winStar(){}

	
	@Override
	public void terminatePart(GameResult resultatJeu)
	{
	    envoyerATous(Protocol.construireMsgPartieTerminee(jeuServeur));
	}

	@Override
	public void waveAttackFinish(WaveOfCreatures vague){}

	@Override
	public void animationAjoutee(Animation animation){}

	@Override
	public void animationTerminee(Animation animation){}
	
	@Override
	public void creatureAjoutee(Creature creature)
	{ 
	    // Multicast aux clients
	    envoyerATous(Protocol.construireMsgCreatureAjout(creature));
	}

    @Override
	public void playerJoin(Player joueur){}
    
	@Override
	public void startPart()
	{
        // Notification des joueurs
        
        //envoyerATous(Protocole.construireMsgPartieChangementEtat(PARTIE_LANCEE));
        envoyerATous(Protocol.construireMsgJoueursEtat(getJoueurs()));
      
        creerTacheDeMiseAJour();
	}

	private void creerTacheDeMiseAJour()
    {
	    //--------------------------------------
        //-- tache de mise a jour des clients --
        //--------------------------------------
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(!jeuServeur.estTermine())
                {
                    for(Creature creature : jeuServeur.getCreatures())
                    {
                        if(!creature.estMorte())
                            envoyerATous(Protocol.construireMsgCreatureEtat(creature));
                    }
                    
                    try{
                        Thread.sleep(TEMPS_DE_RAFFRAICHISSEMENT);
                    } 
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        
        t.start();
    }

    @Override
	public void towerUpgrade(Tower tour)
	{
	    // Multicast aux clients
        envoyerATous(Protocol.construireMsgTourAmelioration(tour).toString());
	}

	@Override
	public void towerPlaced(Tower tour)
	{
	    // Multicast aux clients
        envoyerATous(Protocol.construireMsgTourAjout(tour).toString());
	}

	@Override
	public void towerSold(Tower tour)
	{
	    // Multicast aux clients
        envoyerATous(Protocol.construireMsgTourSuppression(tour).toString());
	}

	@Override
    public void joueurMisAJour(Player joueur)
    {
	    // Multicast aux clients
        envoyerATous(Protocol.construireMsgJoueurEtat(joueur));
    }
	
	/**
	 * Supprime un joueur de la partie
	 * 
	 * @param ID
	 *            l'ID du joueur à supprimer
	 * @throws ChannelException 
	 */
	private synchronized void supprimerJoueur(Player joueur)
	{
		if(joueur != null)
		{ 
		    envoyerATous(Protocol.construireMsgJoueurDeconnecte(joueur.getId()));
		    
		    joueur.getEquipe().retirerJoueur(joueur);
		    
		    envoyerATous(Protocol.construireMsgJoueursEtat(jeuServeur.getJoueurs()));
		}
		else
		    logErreur("Joueur inconnu");
	}

	/************************** ACTIONS DES JOUEURS ************************/

	/**
	 * Lance une vague de créatures
	 * 
	 * @param typeVague
	 * @return
	 */
	public synchronized int lancerVague(Player joueur, int nbCreatures, int typeCreature)
	{
	    if(joueur == null)
	        return JOUEUR_INCONNU;
	    
	    // TODO les créatures disponibles dervaient être dans le terrain
	    Creature creature = TypeOfCreature.getCreature(typeCreature, jeuServeur.getNumVagueCourante(), false);
    
        log("Le joueur " + joueur.getPseudo() + " désire lancer une vague de "+nbCreatures+" créatures de type"
                + creature.getNom());
        
	    // si le joueur n'a pas perdu
        if(joueur.getEquipe().aPerdu())
            return JOUEUR_HORS_JEU; 
	    
	    synchronized (joueur)
        {
		    double argentApresAchat = joueur.getNbPiecesDOr() - creature.getNbPiecesDOr() * nbCreatures;
		    
		    if( (int) argentApresAchat >= 0)
		    {
		        WaveOfCreatures vague = new WaveOfCreatures(nbCreatures, creature, WaveOfCreatures.getTempsLancement(creature.getVitesseNormale()));

		        joueur.setNbPiecesDOr(argentApresAchat);
	            try
                {
                    jeuServeur.lancerVague(joueur, jeuServeur.getEquipeSuivanteNonVide(joueur.getEquipe()),vague);
                } 
	            catch (MoneyLackException e)
                {
                    // impossible que ca arrive... 
	                // c'est pas très propre mais j'en avais besoins pour 
                }
	            
	            return OK;
		    }
		    else
		        return ARGENT_INSUFFISANT;
        }  
	    
	    
	}
	
    //-----------------------
    //-- GESTION DES TOURS --
    //-----------------------

	/**
	 * Appelée lors d'un demande d'ajout d'une tour
	 * 
	 * @param idJoueur le joueur qui souhaite améliorer
	 * @param typeTour le type de la tour a ajouter
	 * @param x la position x de la tour
	 * @param y la position y de la tour
	 * @return l'état de l'action
	 */
	public synchronized int poserTour(Player joueur, int typeTour, int x, int y)
	{
		log("Le joueur " + joueur.getPseudo() + " veut poser une tour de type "
				+ typeTour);
		
        // si le joueur n'a pas perdu
        if(joueur.getEquipe().aPerdu())
            return JOUEUR_HORS_JEU;
		
		// Selection de la tour cible
		Tower tour = null;
        try
        {
            tour = TypeOfTower.getTour(typeTour);
            
            // Assignation des paramêtres
            tour.x = x;
            tour.y = y;
            
            // Assignation du propriétaire
            tour.setProprietaire(joueur);
            
			// Tentative de poser la tour
			jeuServeur.poserTour(tour);
			
		} 
		catch (TowerTypeInvalideException e1)
        {
            return TYPE_TOUR_INVALIDE;
        }
		// Pas assez d'argent 
		catch (MoneyLackException e){
			return ARGENT_INSUFFISANT;
		} 
		// Pose dans une zone non accessible
		catch (ZoneInaccessibleException e){
			return ZONE_INACCESSIBLE; 
		} 
		// Chemin bloqué.
		catch (BarrierException e){
			return CHEMIN_BLOQUE; 
		} 
  
		return OK;
	}

	/**
	 * Appelée lors d'un demande d'amélioration d'une tour
	 * 
	 * @param idJoueur le joueur qui souhaite améliorer
	 * @param idTour la tour a améliorer
	 * 
	 * @return l'état de l'action
	 */
	public synchronized int ameliorerTour(Player joueur, int idTour)
	{
		log("Le joueur " + joueur.getPseudo() + " désire améliorer la tour " + idTour);
		
		// si le joueur n'a pas perdu
        if(joueur.getEquipe().aPerdu())
            return JOUEUR_HORS_JEU; 
		
		// Récupération de la tour à améliorer
		Tower tour = jeuServeur.getTour(idTour);
		
		if (tour == null)
			return TOUR_INCONNUE;
		
		// si le joueur est bien le propriétaire de la tour
		if(tour.getPrioprietaire().getId() != joueur.getId())
		    return ACTION_NON_AUTORISEE;
		
		// On effectue l'action
		try {
		    jeuServeur.ameliorerTour(tour);  
		} 
		catch (MoneyLackException aie){
			return ARGENT_INSUFFISANT;
		}
		catch (ReachMaxLevelException e){
		    return NIVEAU_MAX_ATTEINT;
        } 
		catch (ActionUnauthorizedException e)
        {
		    return ACTION_NON_AUTORISEE;
        } catch (JoueurHorsJeu e)
        {
            return JOUEUR_HORS_JEU;
        }
		
		return OK;
	}

    /**
	 * 
	 * @param tourCibleDel
	 * @return
	 */
	public synchronized int vendreTour(Player joueur, int tourCible)
	{
		log("Le joueur " + joueur.getPseudo() + " désire supprimer la tour " + tourCible);
		
        // si le joueur n'a pas perdu
        if(joueur.getEquipe().aPerdu())
            return JOUEUR_HORS_JEU;
		
		
		// Repérage de la tour à supprimer
		Tower tour = jeuServeur.getTour(tourCible);
		
		if (tour == null)
			return ERREUR;
		
		// seul le proprio peut vendre la tour
		if(tour.getPrioprietaire().getId() != joueur.getId())
		    return ACTION_NON_AUTORISEE;
		
		// On effectue l'action
		try
        {
            jeuServeur.vendreTour(tour);
        } 
		catch (ActionUnauthorizedException e){}
		
		return OK;
	}

	/**
	 * Envoi un message texte à l'ensemble des clients connectés.
	 * 
	 * @param IDFrom
	 *            L'ID de l'expéditeur.
	 * @param message
	 *            Le message à envoyer.
	 * @throws ChannelException 
	 * @throws JSONException 
	 */
	public synchronized void envoyerMessageChatPourTous(int idJoueur, String message) throws JSONException, ChannelException
	{
		log("Le joueur " + idJoueur + " dit : " + message);
		
		for (Entry<Integer, RemotePlayer> joueur : clients.entrySet())
			joueur.getValue().envoyerSurCanalMAJ(Protocol.construireMsgMessage(idJoueur, message));
	}

	/**
	 * Envoi un message texte à un client en particulier.
	 * 
	 * @param IDFrom
	 *            L'ID de l'expéditeur
	 * @param IDTo
	 *            L'ID du destinataire
	 * @param message
	 *            Le message à envoyer.
	 * @throws ChannelException 
	 * @throws JSONException 
	 */
	public synchronized void envoyerMsgClient(int idJoueur, int IDTo, String message) throws JSONException, ChannelException
	{
		log("Le joueur " + idJoueur + " désire envoyer un message à " + IDTo
				+ "(" + message + ")");
		clients.get(IDTo).envoyerSurCanalMAJ(Protocol.construireMsgMessage(idJoueur, message));
	}

	/**
	 * Permet de Mutli-caster a tous les clients
	 * 
	 * @param message le message à diffuser
	 * @throws ChannelException 
	 */
	private synchronized void envoyerATous(String message)
	{   
	    ArrayList<Integer> joueurSupprimes = new ArrayList<Integer>();
	    
	    synchronized(clients)
	    {
	        for (Entry<Integer, RemotePlayer> joueur : clients.entrySet())
                try
                {
                    joueur.getValue().envoyerSurCanalMAJ(message);
                }
                catch (ChannelException e)
                {
                    // le joueur à un canal corrompu
                    joueurSupprimes.add(joueur.getValue().getId());
                }
                

            // pour chaque suppression on indique aux autres joueurs 
            // la deconnexion du joueur
            for (Integer integer : joueurSupprimes) 
            {
                Player joueur = jeuServeur.getJoueur(integer);
                
                joueurDeconnecte(joueur);
            }
        }
	}
	
	
    public void joueurDeconnecte(Player joueur)
    { 
        // si il est pas déjà deconnecte ?
        if(clients.containsKey(joueur.getId()))
        {
            clients.remove(joueur.getId());
            
            // l'hote met fin à la partie
            //if(joueur.getId() == createur.getId())
            //    envoyerATous(Protocole.construireMsgPartieChangementEtat(PARTIE_STOPPEE_BRUTALEMENT));
            
            if(jeuServeur.estDemarre())
                mettreHorsJeu(joueur);
            else
                supprimerJoueur(joueur);  
        }
    }
    

    public String changerEquipe(int idJoueur, int idEquipe)
    {
        Player joueur   = jeuServeur.getJoueur(idJoueur);
        Team equipe   = jeuServeur.getEquipe(idEquipe);
        
        String message = null;
        
        try {
            
            equipe.ajouterJoueur(joueur);

            // SUCCES
            message = Protocol.construireMsgChangerEquipe(OK); 
            
            envoyerATous(Protocol.construireMsgJoueursEtat(getJoueurs()));
        }
        catch (NoPositionAvailableException e)
        {
            // ECHEC
            message = Protocol.construireMsgChangerEquipe(PAS_DE_PLACE);
        } 

        return message;
    }

    @Override
    public void initializationPart()
    {
        envoyerATous(Protocol.construireMsgPartieChangementEtat(PARTIE_INITIALISEE));
    }
    
    protected synchronized static void log(String msg)
    {
        if(verbeux)
            System.out.println("[SERVEUR] "+ msg);
    }

    public void stopper()
    {
        port.liberer();
        
        envoyerATous(Protocol.construireMsgPartieChangementEtat(PARTIE_STOPPEE_BRUTALEMENT));
    }
    
    private void canalErreur(Exception e)
    {
        System.out.println("ServeurJeu.canalErreur");
        e.printStackTrace();
        
        // libération du port
        port.liberer(); 
    }

    public ArrayList<Player> getJoueurs()
    { 
        return jeuServeur.getJoueurs();
    }

    public int getIdCreateur()
    {
        return createur.getId();
    }
    
    /**
     * Permet d'afficher des message log d'erreur
     * 
     * @param msg le message
     */
    private void logErreur(String msg)
    {
        System.out.println("[SERVEUR][ERREUR] "+ msg);
    }
    
    /**
     * Permet d'afficher des message log d'erreur
     * 
     * @param msg le message
     */
    @SuppressWarnings("unused")
    private void logErreur(String msg,Exception e)
    {
        System.out.println("[SERVEUR][ERREUR] "+ msg);
        
        e.printStackTrace();
    }

    private void mettreHorsJeu(Player joueur)
    {
        joueur.mettreHorsJeu();
        
        envoyerATous(Protocol.construireMsgJoueurDeconnecte(joueur.getId()));
    }

    @Override
    public void teamLost(Team equipe)
    {
        envoyerATous(Protocol.construireMsgEquipeAPerdue(equipe.getId()));
    }

    @Override
    public void velocityChanged(double coeffVitesse)
    {
        // NOP
    }
}
