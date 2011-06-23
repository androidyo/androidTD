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

package models.player;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import exceptions.NoPositionAvailableException;
import exceptions.PositionOccupationException;

/**
 * Classe de gestion d'une equipe.
 * 
 * @author Aurelien Da Campo
 * @version 1.2 | juillet 2010
 * @since jdk1.6.0_16
 */
public class Team implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Identificateur
     */
    private int id;
    
   /**
    * Nom de l'equipe
    */
   private String nom;
   
   /**
    * Couleur
    */
   private Color couleur;
   
   /**
    * Liste des joueurs
    */
   private Vector<Player> joueurs = new Vector<Player>();
   
   /**
    * nombre de vies restantes. 
    * <br>
    * Note : Lorsque un ennemi atteint la zone d'arrive, le nombre de vies est
    * decremente.
    */
   private int nbViesRestantes;
   
   /**
    * Zone de départ des créatures ennemies
    */
   private ArrayList<Rectangle> zonesDepartCreatures = new ArrayList<Rectangle>();
   
   /**
    * Zone d'arrivée des créatures ennemies
    */
   private Rectangle zoneArriveeCreatures;

   /**
    * Stockage des emplacements de joueurs
    */
   private ArrayList<PlayerLocation> emplacementsJoueur = new ArrayList<PlayerLocation>();
   
   /**
    * Permet de connaitre la longueur du chemin de l'equipe
    */
   private double longueurChemin = 0.0;
   
   /**
    * Constucteur
    * 
    * @param nom
    * @param couleur
    */
   public Team(int id, String nom, Color couleur)
   {
       this.id = id;
       this.nom = nom;
       this.couleur = couleur;
   }
   
   /**
    * Permet de récupérer l'identificateur de l'équipe
    * 
    * @return l'identificateur de l'équipe
    */
   public int getId()
   {
       return id;
   }
   
   /**
    * Permet de récupérer le nom
    * 
    * @return le nom
    */
   public String getNom()
   {
       return nom;
   }
   
   /**
    * Permet de récupérer la couleur
    * 
    * @return la couleur
    */
   public Color getCouleur()
   {
       return couleur;
   }

   /**
    * Permet d'ajouter un joueur dans l'équipe à un emplacement particulier.
    * 
    * @param joueur le joueur
    * @param ej l'emplacement
    * @throws PositionOccupationException 
    */
   public void ajouterJoueur(Player joueur, PlayerLocation ej) 
       throws PositionOccupationException
   {
       if(joueur == null)
           throw new IllegalArgumentException();
       
       if(ej == null)
           throw new IllegalArgumentException();
       
       if(ej.getJoueur() != null)
           throw new PositionOccupationException("EmplacementJoueur occupé");
       
       // on retire le joueur de son ancienne equipe
       if(joueur.getEquipe() != null)
           joueur.getEquipe().retirerJoueur(joueur);
       
       // on l'ajout dans la nouvelle equipe
       joueurs.add(joueur);

       // on modifier sont equipe
       joueur.setEquipe(this);
       
       // on lui attribut le nouvel emplacement
       joueur.setEmplacementJoueur(ej);
   }
   
   /**
    * Permet d'ajouter un joueur sans connaitre l'emplacement
    * 
    * @param joueur le joueur a ajouter
    * @throws IllegalArgumentException si le joueur est nul  
    * @throws NoPositionAvailableException 
    */
   public void ajouterJoueur(Player joueur) throws NoPositionAvailableException
   {
       if(joueur == null)
           throw new IllegalArgumentException();
          
       PlayerLocation ej = trouverEmplacementDiponible();
       
       // emplacement non trouvé
       if(ej == null) 
           throw new NoPositionAvailableException("Aucune place disponible.");
       // emplacement trouvé
       else
           try{
               ajouterJoueur(joueur, ej);
           } 
           catch (PositionOccupationException e){
               e.printStackTrace();
           } 
   }

   /**
    * Permet de trouver le permier emplacement libre de l'équipe
    * 
    * @return l'emplacement ou null
    */
   public PlayerLocation trouverEmplacementDiponible()
   {
       // cherche un emplacement disponible
       for(PlayerLocation ej : emplacementsJoueur)
           if(ej.getJoueur() == null)
               return ej;
  
       return null;
   }
   
   /**
    * Permet de retirer un joueur de l'equipe. 
    * Corollaire : Le joueur quittera egalement son emplacement.
    * 
    * @param joueur le joueur
    */
   public void retirerJoueur(Player joueur)
   {
       // effacement
       joueurs.remove(joueur);
       
       // quitte l'emplacement
       if(joueur.getEmplacement() != null)
           joueur.getEmplacement().retirerJoueur();
       
       // quitte l'equipe
       joueur.setEquipe(null);
   }

   /**
    * Permet de savoir si l'équip contient un certain joueur
    * 
    * @param joueur le joueur
    * @return true si elle le contient, false sinon
    */
   public boolean contient(Player joueur)
   {
       return joueurs.contains(joueur);
   }
   
   
   /**
    * Permet de recuperer la collection des joueurs
    * 
    * @return la collection des joueurs
    */
   public Vector<Player> getJoueurs()
   {
       return joueurs;
   }
   
   /**
    * Permet de recuperer le score de l'equipe qui correspond à la somme des scores
    * de joueurs de l'équipe.
    * 
    * @return le score
    */
   public int getScore()
   {
       int somme = 0;
       
       for(Player joueur : joueurs)
           somme += joueur.getScore();
       
       return somme;
   }
   
   /**
    * Permet de recuperer le nombre de vies restantes de l'equipe
    * 
    * @return le nombre de vies restantes de l'equipe
    */
   public int getNbViesRestantes()
   {
       return nbViesRestantes;
   }

   /**
    * Permet de faire perdre une vie a l'equipe
    */
    synchronized public void perdreUneVie()
    {
        nbViesRestantes--;
    }

    /**
     * Permet de modifier le nombre de vies restantes de l'equipe
     * 
     * @param nbViesRestantes le nouveau nombre de vies restantes
     */
    public void setNbViesRestantes(int nbViesRestantes)
    {
        this.nbViesRestantes = nbViesRestantes;
    }

    /**
     * Permet d'ajouter une zone de départ des créatures ennemies
     * 
     * @param zone la zone
     */
    public void ajouterZoneDepartCreatures(Rectangle zone)
    {
        zonesDepartCreatures.add(zone);
    }
    
    /**
     * Permet de récupérer la zone de départ des créatures
     * 
     * @return la zone de départ des créatures
     */
    public Rectangle getZoneDepartCreatures(int index)
    {
        return zonesDepartCreatures.get(index);
    }
    
    /**
     * Permet d'ajouter une zone d'arrivée des créatures ennemies
     * 
     * @param zone la zone
     */
    public void setZoneArriveeCreatures(Rectangle zone)
    {
        zoneArriveeCreatures = zone;
    }
    
    /**
     * Permet de récupérer la zone d'arrivee des créatures
     * 
     * @return la zone d'arrivee des créatures
     */
    public Rectangle getZoneArriveeCreatures()
    {
        return zoneArriveeCreatures;
    }
  
    /**
     * Permet d'ajouter un emplacement de joueur
     * 
     * @param emplacementJoueur l'emlacement
     */
    public void ajouterEmplacementJoueur(PlayerLocation emplacementJoueur)
    {
        emplacementsJoueur.add(emplacementJoueur);
    }

    /**
     * Permet de savoir le nombre d'emplacements disponibles de joueur
     * 
     * @return le nombre d'emplacements disponibles de joueur 
     */
    public int getNbEmplacements()
    {
        return emplacementsJoueur.size();
    }

    /**
     * Perme de recupérer les emplacements de joueur
     * 
     * @return les emplacements de joueur
     */
    public ArrayList<PlayerLocation> getEmplacementsJoueur()
    {
        return emplacementsJoueur;
    }
    
    @Override
    public String toString()
    {
        return nom;
    }

    /**
     * Permet de récupérer la longueur du chemin
     *
     * @return la longueur du chemin
     */
    public double getLongueurChemin()
    {
        return longueurChemin;
    }
    
    /**
     * Permet de modifier la longueur du chemin
     * 
     * @param longueur la longueur du chemin
     */
    public void setLongueurChemin(double longueur)
    {
        this.longueurChemin = longueur;
    }

    /**
     * Permet de savoir si l'equipe a perdue
     * 
     * @return true si elle a perdue, false sinon
     */
    public boolean aPerdu()
    {
        return nbViesRestantes <= 0 || estHorsJeu();
    }

    /**
     * Permet de savoir si l'équipe est hors jeu.
     * 
     * Si tous les joueurs de l'équipe sont hors jeu, l'équipe est 
     * considérée comme hors jeu.
     * 
     * @return true si elle l'est, false sinon
     */
    public boolean estHorsJeu()
    {
        Player joueur;
        Enumeration<Player> e = joueurs.elements(); 
        while(e.hasMoreElements())
        {
            joueur = e.nextElement();
            
            // si il y au moins un joueur pas hors jeu
            if(!joueur.estHorsJeu())
                return false; 
        }
        
        return true;
    }

    /**
     * Permet de vider l'équipe de tous ses joueurs, 
     * 
     * Ceux-ci perdront leur emplacement.
     */
    public void vider()
    {
        // retire tous les joueurs de leur emplacement
        Player joueur;
        Enumeration<Player> e = joueurs.elements(); 
        while(e.hasMoreElements())
        {
            joueur = e.nextElement();
            retirerJoueur(joueur);
        }
                      
        // vide la liste des joueurs
        joueurs.clear();
    }
    
    /**
     * Permet de modifier la couleur
     * 
     * @param couleur la couleur
     */
    public void setCouleur(Color couleur)
    {
        this.couleur = couleur;
    }

    /**
     * Permet d'ajouter une zone de départ
     * 
     * @param rectangle la zone de départ
     */
    public void ajouterZoneDepart(Rectangle rectangle)
    {
        zonesDepartCreatures.add(rectangle);
    }

    /**
     * Permet de savoir le nombre de zones de départ
     * 
     * @return le nombre de zones de départ
     */
    public int getNbZonesDepart()
    {
        return zonesDepartCreatures.size();
    }

    /**
     * Permet de supprimer un emplacement de joueur
     * 
     * @param ej l'emplacement de joueur à supprimer
     */
    public void supprimerEmplacement(PlayerLocation ej)
    {
        emplacementsJoueur.remove(ej);
    }

    /**
     * Permet de récupérer la liste des zones de départ
     * 
     * @return une copie de la liste des zones de départ
     */
    public Rectangle[] getZonesDepartCreatures()
    {
        Rectangle[] tabZD = new Rectangle[zonesDepartCreatures.size()];
        zonesDepartCreatures.toArray(tabZD);
        return tabZD;
    }

    /**
     * Permet de modifier le nom de l'équipe
     * 
     * @param nom le nom de l'équipe
     */
    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public void suppimerZoneDepart(Rectangle z)
    {
        // TODO Protection ?
        // if(zonesDepartCreatures.size() > 1)
        zonesDepartCreatures.remove(z);
        // else
        //  throw new ... 
    }
}
