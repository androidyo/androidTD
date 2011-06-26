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
 * Management class to a team.
 * 
 */
public class Team implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * identifier
     */
    private int id;
    
   /**
    * Team name
    */
   private String name;
   
   /**
    * color
    */
   private Color color;
   
   /**
    * List of players
    */
   private Vector<Player> players = new Vector<Player>();
   
   /**
    * number of lives remaining. 
    * <br>
    * Note: When an enemy reaches the area occurs, the number of lives is decremented.
    */
   private int lifeRemainingNumber;
   
   /**
    * Starting area of enemy creatures
    */
   private ArrayList<Rectangle> zonesDepartCreatures = new ArrayList<Rectangle>();
   
   /**
    * Finish area of enemy creatures
    */
   private Rectangle zoneArrivalCreatures;

   /**
    * storage locations of players
    */
   private ArrayList<PlayerLocation> playerLocations = new ArrayList<PlayerLocation>();
   
   /**
    * Allows to know the length of the path of the team
    */
   private double pathLength = 0.0;
   
   /**
    * Constucteur
    * 
    * @param name
    * @param color
    */
   public Team(int id, String name, Color color)
   {
       this.id = id;
       this.name = name;
       this.color = color;
   }
   
   /**
    * Retrieves the identifier of the team
    * 
    * @return l'identificateur de l'équipe
    */
   public int getId()
   {
       return id;
   }
   
   /**
    * Retrieves the name
    * 
    * @return le nom
    */
   public String getName()
   {
       return name;
   }
   
   /**
    * Retrieves the color
    * 
    * @return la couleur
    */
   public Color getColor()
   {
       return color;
   }

   /**
    * Adds a player on the team at a particular location.
    * 
    * @param player le joueur
    * @param pl l'emplacement
    * @throws PositionOccupationException 
    */
   public void addPlayer(Player player, PlayerLocation pl) 
       throws PositionOccupationException
   {
       if(player == null)
           throw new IllegalArgumentException();
       
       if(pl == null)
           throw new IllegalArgumentException();
       
       if(pl.getPlayer() != null)
           throw new PositionOccupationException("EmplacementJoueur occupé");
       
       // we remove the player from his old team
       if(player.getTeam() != null)
           player.getTeam().removePlayer(player);
       
       // on the addition to the new team
       players.add(player);

       // change team
       player.setTeam(this);
       
       player.setPlayerLocation(pl);
   }
   
   /**
    * To add a player without knowing the location
    * 
    * @param player le joueur a ajouter
    * @throws IllegalArgumentException si le joueur est nul  
    * @throws NoPositionAvailableException 
    */
   public void addPlayer(Player player) throws NoPositionAvailableException
   {
       if(player == null)
           throw new IllegalArgumentException();
          
       PlayerLocation pl = findLocationAvailable();
       
       // emplacement non trouvé
       if(pl == null) 
           throw new NoPositionAvailableException("Aucune place disponible.");
       // location found
       else
           try{
               addPlayer(player, pl);
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
   public PlayerLocation findLocationAvailable()
   {
       // seek location available
       for(PlayerLocation pl : playerLocations)
           if(pl.getPlayer() == null)
               return pl;
  
       return null;
   }
   
   /**
    * Removes a player from the team. 
    * Corollary: The player will also leave its location.
    * 
    * @param player le joueur
    */
   public void removePlayer(Player player)
   {
       // effacement
       players.remove(player);
       
       // quitte l'emplacement
       if(player.getEmplacement() != null)
           player.getEmplacement().retirerJoueur();
       
       // quitte l'equipe
       player.setTeam(null);
   }

   /**
    * Indicates whether the equipment contains a certain player
    * 
    * @param player le joueur
    * @return true si elle le contient, false sinon
    */
   public boolean contains(Player player)
   {
       return players.contains(player);
   }
   
   
   /**
    * Retrieves a collection of players
    * 
    * @return la collection des joueurs
    */
   public Vector<Player> getPlayers()
   {
       return players;
   }
   
   /**
    * Used to retrieve the score of the team which is the sum of the scores of players on the team.
    * 
    * @return le score
    */
   public int getScore()
   {
       int sum = 0;
       
       for(Player player : players)
           sum += player.getScore();
       
       return sum;
   }
   
   /**
    * Used to retrieve the number of lives remaining Team
    * 
    * @return le nombre de vies restantes de l'equipe
    */
   public int getLifeRemainingNumber()
   {
       return lifeRemainingNumber;
   }

   /**
    * Allows you to lose a life Team
    */
    synchronized public void losingALife()
    {
        lifeRemainingNumber--;
    }

    /**
     * Changes the number of lives remaining Team
     * 
     * @param nbViesRestantes le nouveau nombre de vies restantes
     */
    public void setLifeRemainingNumber(int nbViesRestantes)
    {
        this.lifeRemainingNumber = nbViesRestantes;
    }

    /**
     * Adds a starting area of enemy creatures
     * 
     * @param zone la zone
     */
    public void addZoneDepartCreatures(Rectangle zone)
    {
        zonesDepartCreatures.add(zone);
    }
    
    /**
     * Retrieves the starting area of the creatures
     * 
     * @return la zone de départ des créatures
     */
    public Rectangle getZoneDepartCreatures(int index)
    {
        return zonesDepartCreatures.get(index);
    }
    
    /**
     * Adds a zone of arrival of enemy creatures
     * 
     * @param zone la zone
     */
    public void setZoneArrivalCreatures(Rectangle zone)
    {
        zoneArrivalCreatures = zone;
    }
    
    /**
     * Retrieves the finish area of the creatures
     * 
     * @return la zone d'arrivee des créatures
     */
    public Rectangle getZoneArrivalCreatures()
    {
        return zoneArrivalCreatures;
    }
  
    /**
     * Adds a player's location
     * 
     * @param playerLocation l'emlacement
     */
    public void addPlayerLocation(PlayerLocation playerLocation)
    {
        playerLocations.add(playerLocation);
    }

    /**
     * Lets you know the number of slots available to players
     * 
     * @return the number of slots available to players 
     */
    public int getNumberOfAvailablePlayerLocations()
    {
        return playerLocations.size();
    }

    /**
     * Perme de recupérer les emplacements de joueur
     * 
     * @return les emplacements de joueur
     */
    public ArrayList<PlayerLocation> getPlayerLocations()
    {
        return playerLocations;
    }
    
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Retrieves the path length
     *
     * @return la longueur du chemin
     */
    public double getPathLength()
    {
        return pathLength;
    }
    
    /**
     * Changes the path length
     * 
     * @param longueur la longueur du chemin
     */
    public void setPathLength(double longueur)
    {
        this.pathLength = longueur;
    }

    /**
     * Indicates whether the team lost
     * 
     * @return true si elle a perdue, false sinon
     */
    public boolean isLost()
    {
        return lifeRemainingNumber <= 0 || estHorsJeu();
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
        Enumeration<Player> e = players.elements(); 
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
        Enumeration<Player> e = players.elements(); 
        while(e.hasMoreElements())
        {
            joueur = e.nextElement();
            removePlayer(joueur);
        }
                      
        // vide la liste des joueurs
        players.clear();
    }
    
    /**
     * Permet de modifier la couleur
     * 
     * @param couleur la couleur
     */
    public void setCouleur(Color couleur)
    {
        this.color = couleur;
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
        playerLocations.remove(ej);
    }

    /**
     * Retrieves the list of areas starting
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
     * Changes the name of the team
     * 
     * @param name le nom de l'équipe
     */
    public void setName(String name)
    {
        this.name = name;
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
