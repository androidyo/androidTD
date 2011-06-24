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

package net.game.server;

import java.util.ArrayList;
import models.creatures.*;
import models.game.Game;
import models.map.Field;
import models.player.Team;
import models.player.Player;
import models.towers.*;

import org.json.*;

/**
 * 协议定义类.
 * 
 * 它用于创建信息流通的渠道.
 * 
 * @author Aurélien Da Campo
 * @version 1.0 | mai 2010
 */
public class Protocol implements ConstantsServerJeu
{

    //------------------------------
    //-- CONSTRUCTION DE MESSAGES --
    //------------------------------
    
    public static String construireMsgJoueurInitialisation(Player joueur, Field terrain)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", JOUEUR_INITIALISATION);
            msg.put("STATUS", OK);
            msg.put("ID_JOUEUR", joueur.getId());
            msg.put("ID_EMPLACEMENT", joueur.getEmplacement().getId());
            msg.put("ID_EQUIPE", joueur.getEquipe().getId());
            msg.put("NOM_FICHIER_TERRAIN", terrain.getNomFichier());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString(); 
    }
    
    public static String construireMsgJoueursEtat(ArrayList<Player> joueurs)
    {
        JSONObject msg = new JSONObject();
        JSONArray JSONjoueurs = new JSONArray();
        
        try
        {
            msg.put("TYPE", JOUEURS_ETAT);
            
            Player joueur;
            JSONObject JSONjoueur;
              
            for(int j=0; j < joueurs.size();j++)
            {
                // recuperation du joueur
                joueur = joueurs.get(j);
                    
                // construction du joueur
                JSONjoueur = new JSONObject();
                JSONjoueur.put("ID_JOUEUR", joueur.getId());
                JSONjoueur.put("NOM_JOUEUR", joueur.getPseudo());
                JSONjoueur.put("ID_EMPLACEMENT", joueur.getEmplacement().getId());
                JSONjoueur.put("ID_EQUIPE", joueur.getEquipe().getId());
                
                // ajout à la liste des joueurs
                JSONjoueurs.put(JSONjoueur);
            }
            
            msg.put("JOUEURS",JSONjoueurs);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString(); 
    }
    
    
    public static String construireMsgJoueurInitialisation(int etat)
    {
        JSONObject msg = new JSONObject();
        
        try {
            msg.put("TYPE", JOUEUR_INITIALISATION);
            msg.put("STATUS", etat);
        } 
        catch (JSONException jsone){
            jsone.printStackTrace();
        }
        
        return msg.toString();
    }
    
    
    public static String construireMsgPartieChangementEtat(int etat)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", PARTIE_ETAT);
            msg.put("ETAT", etat); 
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString(); 
    }
    
    /**
     * Permet de construire le message d'état d'un joueur
     * 
     * @param joueur le joueur
     * @return Une structure JSONObject
     */
    public static String construireMsgJoueurEtat(Player joueur)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", JOUEUR_ETAT);
            msg.put("ID_JOUEUR", joueur.getId());
            msg.put("NB_PIECES_OR", joueur.getNbPiecesDOr());
            msg.put("NB_VIES_RESTANTES_EQUIPE", joueur.getEquipe().getNbViesRestantes());
            msg.put("REVENU", joueur.getRevenu());
            msg.put("SCORE", joueur.getScore());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString(); 
    }
    
    
    /**
     * Permet de construire le message de demande d'ajout d'une tour
     * 
     * @param tour la tour
     * @return Une structure JSONObject
     */
    public static String construireMsgTourAjout(Tower tour)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", TOUR_AJOUT);
            msg.put("ID_PROPRIETAIRE", tour.getPrioprietaire().getId());
            msg.put("ID_TOUR", tour.getId());
            msg.put("X", tour.x);
            msg.put("Y", tour.y);
            msg.put("TYPE_TOUR", TypeOfTower.getTypeDeTour(tour));
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString();
    }
    
    /**
     * Permet de construire le message de demande d'amélioration d'une tour
     * 
     * @param tour la tour
     * @return Une structure JSONObject
     */
    public static String construireMsgTourAmelioration(Tower tour)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", TOUR_AMELIORATION);
            msg.put("ID_TOUR", tour.getId());
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString();
    }
    
    /**
     * Permet de construire le message de demander la suppression d'une tour
     * 
     * @param tour la tour
     * @return Une structure JSONObject
     */
    public static String construireMsgTourSuppression(Tower tour)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", TOUR_SUPRESSION);
            msg.put("ID_TOUR", tour.getId());
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString();
    }
    
    // CREATURES
    
    /**
     * Permet de construire le message d'ajout d'une créature
     * 
     * @param creature la creature
     * @return Une structure JSONObject
     */
    public static String construireMsgCreatureAjout(Creature creature)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", CREATURE_AJOUT);
            msg.put("TYPE_CREATURE", TypeOfCreature.getTypeCreature(creature));
            msg.put("ID_CREATURE", creature.getId());
            msg.put("ID_PROPRIETAIRE", creature.getProprietaire().getId());
            msg.put("ID_EQUIPE_CIBLEE", creature.getEquipeCiblee().getId());
            msg.put("X", creature.x);
            msg.put("Y", creature.y);
            msg.put("SANTE_MAX", creature.getSanteMax());
            msg.put("NB_PIECES_OR", creature.getNbPiecesDOr());
            msg.put("VITESSE", creature.getVitesseNormale());  
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString();  
    }
    
    /**
     * Permet de construire le message d'état d'une créature
     * 
     * @param creature la creature
     * @return Une structure JSONObject
     */
    public static String construireMsgCreatureEtat(Creature creature)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", CREATURE_ETAT);
            msg.put("ID_CREATURE", creature.getId());
           
            msg.put("X", creature.x);
            msg.put("Y", creature.y);
            msg.put("SANTE", creature.getSante());
            msg.put("ANGLE", creature.getAngle());
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString();
    }
    
    /**
     * Permet de construire le message de suppression d'une créature
     * 
     * @param creature la creature
     * @return Une structure JSONObject
     */
    public static String construireMsgCreatureSuppression(Creature creature,Player joueur)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", CREATURE_SUPPRESSION);
            msg.put("ID_CREATURE", creature.getId());
            msg.put("ID_TUEUR", joueur.getId());
            
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString();  
    }

    public static String construireMsgPartieTerminee(Game jeu)
    {
        JSONObject msg = new JSONObject();
        
        try
        {
            msg.put("TYPE", PARTIE_ETAT);
            msg.put("ETAT", PARTIE_TERMINEE);
            
            // construction des états des équipes
            JSONArray JSONequipes = new JSONArray();  
            for(Team e : jeu.getEquipes())
            {
                JSONObject JSONequipe = new JSONObject();
                
                JSONequipe.put("ID_EQUIPE", e.getId());
                
                if(e.estHorsJeu())
                    JSONequipe.put("NB_VIES_RESTANTES", 0); 
                else
                    JSONequipe.put("NB_VIES_RESTANTES", e.getNbViesRestantes()); 
                
                
                JSONequipes.put(JSONequipe);
            }
            msg.put("EQUIPES", JSONequipes);
            
            // TODO construction des états des joueurs
            // ...
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString(); 
    }

    public static String construireMsgChat(String message, int cible)
    {
        JSONObject msg = new JSONObject();
        
        try 
        {
            msg.put("TYPE", JOUEUR_MESSAGE);
            msg.put("CIBLE", cible);
            msg.put("MESSAGE", message);
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return msg.toString();
    }

    public static String construireMsgChangerEquipe(int etat)
    {
        JSONObject msg = new JSONObject();
        
        try {
            msg.put("TYPE", JOUEUR_CHANGER_EQUIPE);
            msg.put("STATUS", etat);
        }
        catch (JSONException jsone){
            jsone.printStackTrace();
        }
        
        return msg.toString();
    }

    public static String construireMsgCreatureArrivee(Creature creature)
    {
        JSONObject msg = new JSONObject();
        
        try {
            msg.put("TYPE", CREATURE_ARRIVEE);
            msg.put("ID_CREATURE", creature.getId());
        }
        catch (JSONException jsone){
            jsone.printStackTrace();
        }
        
        return msg.toString();
    }
    
    
    public static String construireMsgMessage(int idAuteur, String contenu)
    {
        JSONObject msg = new JSONObject();
        
        try {
            // Construction de la structure JSON
            msg.put("TYPE", JOUEUR_MESSAGE);
            msg.put("ID_JOUEUR", idAuteur);
            msg.put("MESSAGE", contenu);
        }
        catch (JSONException jsone){
            jsone.printStackTrace();
        }
        
        return msg.toString();
    }

    public static String construireMsgJoueurDeconnecte(int idJoueur)
    {
        JSONObject msg = new JSONObject();
        
        try {
            // Construction de la structure JSON
            msg.put("TYPE", JOUEUR_DECONNEXION);
            msg.put("ID_JOUEUR", idJoueur);
        }
        catch (JSONException jsone){
            jsone.printStackTrace();
        }
        
        return msg.toString();
    }

    
    public static String construireMsgEquipeAPerdue(int id)
    {
        JSONObject msg = new JSONObject();
        
        try {
            // Construction de la structure JSON
            msg.put("TYPE", EQUIPE_A_PERDUE);
            msg.put("ID_EQUIPE", id);
        }
        catch (JSONException jsone){
            jsone.printStackTrace();
        }
        
        return msg.toString();
    }
}
