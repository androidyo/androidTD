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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;

import models.creatures.Creature;
import models.player.RevenueManager;
import models.player.Player;
import net.ChannelException;
import net.ChannelTCP;
import net.game.server.ServerJeu;

import org.json.JSONException;
import org.json.JSONObject;

import server.registerment.CodeRegisterment;
import server.registerment.RegistrationQuery;
import utils.Configuration;

/**
 * 网络游戏引擎
 * 
 * @author Aurelien Da Campo
 * @version 1.1 | mai 2010
 * @since jdk1.6.0_16
 * 
 * @see serveurDeJeu
 */
public class Game_Server extends Game
{
    /**
     * Connexion au serveur d'enregistrement
     */
    private ChannelTCP canalServeurEnregistrement;
    
    /**
     * Connexions réseaux du serveur
     */
    private ServerJeu serveurDeJeu;
    
    /**
     * Permet de savoir si l'enregistrement au SE a réussi
     */
    private boolean enregistrementReussie = false;
    
    /**
     * Gestionnaire de revenu.
     */
    private RevenueManager gRevenus = new RevenueManager(this);

    /**
     * Temps avant que le serveur incrémente son niveau
     * 
     * Le niveau du jeu influ sur la santé des créatures lancées
     * En effet, la santé des créatures est générer en fonction du niveau du jeu.
     */
    private static final int TEMPS_ENTRE_CHAQUE_LEVEL = 20; // secondes
    
    @Override
    public void demarrer()
    {
        super.demarrer();
        
        gRevenus.demarrer();
        
        
        // gestionnaire des niveaux (applé toutes les secondes de jeu)
        timer.addActionListener(new ActionListener()
        {  
            int secondes = 0;
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                secondes++;
                
                if(secondes == TEMPS_ENTRE_CHAQUE_LEVEL)
                {
                    passerALaProchaineVague();
                    secondes = 0;
                    
                    // TODO effacer et informer les clients ou non.
                    System.out.println("Nouveau level du serveur "+getNumVagueCourante());
                } 
            }
        });
    }
    
    @Override
    synchronized public void creatureHurt(Creature creature, Player tueur)
    {
        // gain de pieces d'or
        tueur.setNbPiecesDOr(tueur.getNbPiecesDOr() + creature.getNbPiecesDOr() / 5.0);
        
        // augmentation du score
        tueur.setScore(tueur.getScore() + creature.getNbPiecesDOr());

        // notification de la mort de la créature
        if(edj != null)
            edj.creatureKilled(creature,tueur);
    }

    /**
     * Permet de savoir si le serveur est 
     * enregistré sur le Serveur d'Enregistrement.
     * 
     * @return true si il l'est, false sinon
     */
    public boolean estEnregisterSurSE()
    {
        return enregistrementReussie;
    }
    
    /**
     * Permet d'établir la connexion du serveur.
     * 
     * @throws IOException 
     */
    public void etablissementDuServeur() throws IOException
    {
        serveurDeJeu = new ServerJeu(this);
    }

    /**
     * Permet de stopper le serveur de jeu
     */
    public void stopperServeurDeJeu()
    {
        serveurDeJeu.stopper();
    }

    //------------------------------
    //-- SERVEUR D'ENREGISTREMENT --
    //------------------------------
    
    /**
     * Permet d'enregistrer le jeu sur le serveur d'enregistrement
     * 
     * @param nomServeur le nom
     * @param nbJoueurs le nombre de joueurs
     * @param nomTerrain le terrain
     * @param mode le mode de jeu
     * @return true = ok, false = erreur
     */
    public boolean enregistrerSurSE(String nomServeur, int nbJoueurs, String nomTerrain, int mode)
    {
        try
        {
            canalServeurEnregistrement = new ChannelTCP(Configuration.getIpSE(), 
                                                      Configuration.getPortSE());
            
            // Création de la requete d'enregistrement
            String requete = RegistrationQuery.getRequeteEnregistrer(
                    nomServeur, Configuration.getPortSJ(), nbJoueurs, nomTerrain, GameMode.getNomMode(mode));

            // Envoie de la requete
            canalServeurEnregistrement.envoyerString(requete);
            
            // Attente du résultat
            String resultat = canalServeurEnregistrement.recevoirString();
            
            try
            {
                // Analyse de la réponse du serveur d'enregistrement
                JSONObject jsonResultat = new JSONObject(resultat);
                
                if(jsonResultat.getInt("status") == CodeRegisterment.OK)
                {
                    enregistrementReussie = true;
                    return true;
                }
                else
                    return false;
            } 
            catch (JSONException e1)
            {
                e1.printStackTrace();
            }
        } 
        catch (ConnectException e){} 
        catch (ChannelException e){}
        
        return false;
    }
    
    /**
     * Permet de surpprimer l'enregistrement du jeu sur le SE 
     */
    public void desenregistrerSurSE()
    {
        // fermeture du canal s'il est ouvert
        if (canalServeurEnregistrement != null && estEnregisterSurSE())
        {
            try
            {
                // désenregistrement du serveur
                canalServeurEnregistrement.envoyerString(RegistrationQuery.DESENREGISTRER);
                canalServeurEnregistrement.recevoirString();

                // fermeture propre du canal
                //canalServeurEnregistrement.envoyerString(RequeteEnregistrement.STOP);
                //canalServeurEnregistrement.recevoirString();
            
                canalServeurEnregistrement.fermer();}
                // il y a eu une erreur... on quitte tout de même
            
            catch (ChannelException ce)
            {
                ce.printStackTrace();
            }
        }
    }
    
    /**
     * Permet de mettre à jour les infos du jeu sur le SE
     */
    public void miseAJourSE()
    {
        if(enregistrementReussie)
        {
            // Création de la requete d'enregistrement
            String requete = RegistrationQuery.getRequeteMiseAJour(terrain.getNbJoueursMax() - getJoueurs().size());
    
            try
            {
                // Envoie de la requete 
                canalServeurEnregistrement.envoyerString(requete);
            
                // Attente du résultat
                canalServeurEnregistrement.recevoirString();
            } 
            catch (ChannelException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void terminer(GameResult resultatJeu)
    {
        if(!estTermine)
        {
            estTermine = true;
            
            arreterTout();
            
            if(edj != null)
                edj.terminatePart(resultatJeu);
        }
    }
}
