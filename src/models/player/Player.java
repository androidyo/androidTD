package models.player;

import models.utils.Score;

/**
 * Le joueur est un élément très important du jeu.
 * 
 * Il est stocké dans une équipe et possède un emplacement de construction.
 * 
 * Il a également des pièces d'or et un score. 
 * Les vies restantes sont gérées par son équipe.
 * 
 * Il possède aussi un revenu qui lui est distribué par le gestionnaire de revenu.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 * @see RevenueManager
 */
public class Player
{
    /**
     * Compteur pour générer les identificateurs.
     */
    private static int idCourant = 0;
    
    /**
     * Identificateur du joueur
     */
    private int id;
    
    /**
     * Pseudo
     */
    private String pseudo;
    
    /**
     * Nombre de pieces d'or du joueur.
     * <br>
     * Cette variable fluctue en fonction des creatures tuees et de 
     * l'achat et vente des tours.
     * 
     * Elle est en double pour gérer les revenus flottant (voir gestionnaireDeRevenus)
     */
    private double nbPiecesDOr = 0;
    
    /**
     * Equipe du joueur
     */
    private Team team;
   
    /**
     * score courant du joueur. Cette valeur equivaux a la somme 
     * de toutes les pieces d'or amassee par le joueur durant la partie.
     */
    private Score score = new Score();
    
    /**
     * Emplacement du joueur sur le terrain
     * 
     * Permet de définir les zones de construction du joueur
     */
    private PlayerLocation emplacement;

    /**
     * Permet de notifier l'ecouteur de joueur
     */
    private PlayerListener edj;

    /**
     * Revenu périodique par seconde
     */
    private double revenu = 1.0;
    
    /**
     * Le joueur est hors jeu.
     * 
     * Si tout les joueurs de l'équipe son hors jeu, l'équipe 
     * sera hors jeu et aura perdu.
     */
    private boolean estHorsJeu;

    /**
     * Constructeur
     */
    public Player(String pseudo)
    {
        this.pseudo = pseudo;
        this.id = ++idCourant;
    }

    /**
     * Permet de recuperer l'id
     * @return l'id
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * Permet de recuperer le score du joueur
     * 
     * @return le score
     */
    public int getScore()
    {
        return score.getValeur();
    }
    
    /**
     * Permet savoir si le joueur est hors jeu
     * 
     * @return true s'il l'est, false sinon.
     */
    public boolean estHorsJeu()
    {
        return estHorsJeu;
    }

    /**
     * Permet mettre le joueur hors jeu suite à 
     * une déconnexion
     * 
     * @return true s'il l'est, false sinon.
     */
    public void mettreHorsJeu()
    {
        estHorsJeu = true;
    }
    
    /**
     * Permet de modifier le nombre de pieces d'or.
     * 
     * @param nbPiecesDOr le nouveau nombre de pieces d'or
     */
    public void setNbPiecesDOr(double nbPiecesDOr)
    {
        this.nbPiecesDOr = nbPiecesDOr;
        
        if(edj != null)
            edj.joueurMisAJour(this);
    }

    /**
     * Permet de récupérer le nombre de pieces d'or du joueur
     * 
     * @return le nombre de pieces d'or du joueur
     */
    public double getNbPiecesDOr()
    {
        return nbPiecesDOr;
    }

    /**
     * Permet de récupérer le score du joueur
     * 
     * @return le score du joueur
     */
    public void setScore(int score)
    {
        this.score.setValeur(score);
        
        if(edj != null)
            edj.joueurMisAJour(this);
    }
    
    /**
     * Permet de récupérer le nombre d'étoiles du joueur
     * 
     * @return le nombre d'étoiles du joueur
     */
    public int getNbEtoiles()
    {
        return this.score.getNbEtoiles();
    }
    
    /**
     * Used to retrieve the player's team
     * 
     * @return l'équipe du joueur
     */
    public Team getTeam()
    {
        return team;
    }
    
    /**
     * Permet de récupérer l'emplacement du joueur
     * 
     * @return l'emplacement du joueur
     */
    public PlayerLocation getEmplacement()
    {
        return emplacement;
    }
     
    /**
     * Permet de savoir si le joueur a perdu
     * 
     * @return true s'il a perdu, false sinon
     */
    public boolean aPerdu()
    {
        return team.getLifeRemainingNumber() <= 0;
    }

    /**
     * Permet de récupérer le pseudo
     * 
     * @return le pseudo
     */
    public String getPseudo()
    {
        return pseudo;
    }
    
    /**
     * Allows you to change the player's team
     * 
     * @param team la nouvelle équipe du joueur
     */
    public void setTeam(Team team)
    {
        // si le joueur avait une equipe qui le contenait
        if(this.team != null && this.team.contains(this))
            this.team.removePlayer(this);
        
        this.team = team;
        
        // FIXME Décommenter et voir les conscéquences !
        //equipe.ajouterJoueur(this);
    }
    
    /**
     * Allows you to change the location of the player
     * 
     * @param emplacementJoueur l'emplacement du joueur
     */
    public void setPlayerLocation(PlayerLocation emplacementJoueur)
    {
        if(emplacementJoueur != null 
        && emplacementJoueur.getPlayer() != this) // fin de récursion de maj
            emplacementJoueur.setJoueur(this);
        
        this.emplacement = emplacementJoueur;
    }

    /**
     * Permet de quitter l'emplacement
     */
    public void quitterEmplacementJoueur()
    {
        // fin de récursion de maj
        if(emplacement != null && emplacement.getPlayer() != this) 
            emplacement.retirerJoueur();
        
        emplacement = null;
    }

    /**
     * Permet de modifier l'id du joueur
     * 
     * @param id le nouvel id du joueur
     */
    public void setId(int id)
    {
        this.id = id; 
    }

    /**
     * Permet de modifier l'écouteur de joueur
     * 
     * @param edj l'écouteur de joueur
     */
    public void setEcouteurDeJoueur(PlayerListener edj)
    {
        this.edj = edj;
    }

    /**
     * Permet de recuperer le revenu du joueur
     * 
     * @return le revenu actuel
     */
    public double getRevenu()
    {
        return revenu;
    }
    
    /**
     * Permet d'ajouter une somme au revenu
     * 
     * @param somme la somme a ajouter 
     */
    public void ajouterRevenu(double somme)
    {
        revenu += somme;
    }
    
    public void setRevenu(double revenu)
    {
        this.revenu = revenu;
    }
    
    /**
     * Permet de donner le revenu au joueur
     * 
     * @param temps le temps en ms ecoulees (pour calcul du revenu / sec) 
     */
    public synchronized void donnerRevenu(long temps)
    {
        setNbPiecesDOr(getNbPiecesDOr() + revenu * (temps / 1000.0));
    }

    
}
