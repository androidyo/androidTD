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
import java.net.ConnectException;

import net.ChannelException;
import net.jeu.client.ClientJeu;
import net.jeu.client.EcouteurDeClientJeu;
import exceptions.*;
import models.creatures.*;
import models.player.*;
import models.towers.Tower;

/**
 * 游戏客户端，可以连接到服务器
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * 
 * @see ClientJeu
 */
public class Game_Client extends Game
{
    private ClientJeu clientJeu;
    
    public Game_Client(Player joueur)
    {
        setJoueurPrincipal(joueur);
        
        clientJeu = new ClientJeu(this);
    }

    @Override
    public void poserTour(Tower tour) throws MoneyLackException, ZoneInaccessibleException
    {
        try
        {
            clientJeu.demanderPoseTour(tour);
        } 
        catch (ChannelException e)
        {
            erreurCanal(e);
        }
    }

    @Override
    public void vendreTour(Tower tour) throws ActionUnauthorizedException
    {
        try
        {
            clientJeu.demanderVenteTour(tour);
        }
        catch (ChannelException e)
        {
           erreurCanal(e);
        }
    }

    @Override
    public void ameliorerTour(Tower tour) throws MoneyLackException, 
    ActionUnauthorizedException, ReachMaxLevelException, JoueurHorsJeu
    {
        try
        {
            clientJeu.demanderAmeliorationTour(tour);
        } 
        catch (ChannelException e)
        {
            erreurCanal(e);
        }
    }

    @Override
    public void lancerVague(Player joueur, Team equipe, WaveOfCreatures vague) throws MoneyLackException
    {
        try
        {
            clientJeu.envoyerVague(vague);
        } 
        catch (ChannelException e)
        {
            erreurCanal(e);
        } 
    }
    
    /**
     * Permet d'établir la connexion avec le serveur.
     * 
     * @param IP l'IP du serveur distant
     * @param port le port du serveur distant
     * 
     * @throws ConnectException
     * @throws ChannelException
     * @throws NoLocationAvailableException
     */
    public void connexionAvecLeServeur(String IP, int port) 
        throws ConnectException, ChannelException, NoLocationAvailableException
    {
        clientJeu.etablirConnexion(IP, port);
    }

    /**
     * Permet de poser une tour directement (sans contrôle)
     * 
     * @param tour la tour
     */
    public void poserTourDirect(Tower tour)
    {
        if(tour != null)
        {
            tour.mettreEnJeu();
            tour.setJeu(this);
            
            gestionnaireTours.ajouterTour(tour);
        }
    }
    
    /**
     * Permet de supprimer une tour directement (sans contrôle)
     * 
     * @param tour la tour
     */
    public void supprimerTourDirect(Tower tour)
    {
        if(tour != null)
            gestionnaireTours.supprimerTour(tour);
    }

    /**
     * Permet d'améliorer une tour directement (sans contrôle)
     * 
     * @param tour la tour
     */
    public void ameliorerTourDirect(Tower tour)
    {
        if(tour != null)
            tour.ameliorer();
    }

    /**
     * Permet d'ajouter une créature directement (sans contrôle)
     * 
     * @param creature la creature
     */
    public void ajouterCreatureDirect(Creature creature)
    {
        if(creature != null)
            gestionnaireCreatures.addCreature(creature);
    }

    /**
     * Permet de supprimer une créature directement (sans contrôle)
     * 
     * @param creature la creature
     */
    public void supprimerCreatureDirect(Creature creature)
    {
        if(creature != null)
            gestionnaireCreatures.removeCreature(creature);
    }

    /**
     * Permet de demander un changement d'equipe
     * 
     * @param joueur le joueur qui veut changer
     * @param equipe l'equipe dans laquelle il veut aller
     * @throws NoLocationAvailableException
     */
    public void changerEquipe(Player joueur, Team equipe) throws NoLocationAvailableException
    {
        try
        {
            clientJeu.demanderChangementEquipe(joueur,equipe);
        }
        catch (ChannelException e)
        {
            erreurCanal(e);
        } 
    }
    
    /**
     * Permet d'annoncer une erreur du canal
     * 
     * @param e l'exception
     */
    private void erreurCanal(Exception e)
    {
        System.err.println("Jeu_Client.erreurCanal()");
        e.printStackTrace();
    }

    /**
     * Permet de vider les équipes
     */
    public void viderEquipes()
    {
        synchronized (equipes)
        {
            // vide toutes les equipes
            for(Team e : equipes)
                e.vider(); 
        }
    }

    /**
     * Permet de modifier l'écouteur du client jeu
     * 
     * @param edcj l'écouteur du client jeu
     */
    public void setEcouteurDeClientJeu(
            EcouteurDeClientJeu edcj)
    {
        clientJeu.setEcouteurDeClientJeu(edcj);
    }

    /**
     * Permet d'annoncer le serveur que le joueur souhaite se déconnecter
     * @throws ChannelException
     */
    public void annoncerDeconnexion() throws ChannelException
    {
        clientJeu.annoncerDeconnexion();
    }

    /**
     * Permet d'envoyer un message chat
     * 
     * @param message le message
     * @param cible la cible
     * @throws ChannelException
     * @throws MessageChatInvalide 
     */
    public void envoyerMsgChat(String message, int cible) throws ChannelException, MessageChatInvalide
    {
        clientJeu.envoyerMessage(message, cible);
    } 
}
