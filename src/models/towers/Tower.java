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

package models.towers;

import i18n.Langue;

import java.awt.*;
import java.util.Enumeration;
import models.creatures.Creature;
import models.game.Game;
import models.player.Player;

/**
 * Classe de gestion d'une tour
 * <p>
 * Les tours font parti des elements principaux du jeu Tower Defense. 
 * Ce sont les entites qui permet de blesser et de ralentir les creatures 
 * sur le terrain.
 * <p>
 * Cette classe implemente l'interface Runnable qui permet la tour d'avoir son
 * propre thread et ainsi gerer elle-meme sa cadence de tir.
 * <p>
 * Pour tirer sur une creature du terrain, la creature doit etre a portee et 
 * doit etre correspondre au type d'attaque de la tour (terre ou air).
 * <p>
 * Cette classe est abstraite et doit etre heritee pour etre ensuite instanciee.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 27 novemenbre 2009
 * @since jdk1.6.0_16
 */
public abstract class Tower extends Rectangle
{
	private static final long serialVersionUID      = 1L;
	public static final int TYPE_TERRESTRE_ET_AIR 	= 0;
	public static final int TYPE_TERRESTRE 			= 1;
	public static final int TYPE_AIR 				= 2;
	
	/**
	 * coefficient de prix de vente de la tour. utilisee pour calculer le prix
	 * de vente de la tour.
	 */
	private static final double COEFF_PRIX_VENTE = 0.6;
    
	public static final int CIBLAGE_CREATURE_PLUS_PROCHE  = 0;
	public static final int CIBLAGE_CREATURE_PLUS_LOIN    = 1;
    public static final int CIBLAGE_CREATURE_PLUS_FAIBLE  = 2;
    public static final int CIBLAGE_CREATURE_PLUS_FORTE   = 3;
    
    /**
     * Type de ciblage
     */
    private int typeCiblage = CIBLAGE_CREATURE_PLUS_FAIBLE;
    
	/**
     * Identificateur de la tour
     */
    private int ID;
    private static int idCourant = 0;
    
	/**
	 * couleur de fond de la tour
	 */
	private final Color COULEUR_DE_FOND;

	/**
	 * nom de la tour
	 */
	private final String NOM;

	/**
	 * texte descriptif de la tour
	 */
	protected String description;

	/**
	 * degats de la tour
	 */
	protected long degats;

	/**
	 * niveau actuel de la tour
	 */
	protected int niveau = 1;

	/**
	 * prix achat de la tour ou de son amelioration
	 */
	protected int prixAchat;

	/**
	 * prix total de la tour en compant toutes les ameliorations utilisee pour
	 * calculer le prix de vente de la tour
	 */
	protected int prixTotal;

	/**
	 * rayon de portee de la tour
	 */
	protected double rayonPortee = 100;

	/**
	 * type de la tour
	 * <p>
	 * Peut prendre les 3 valeurs suivantes :
	 * Tour.TYPE_TERRESTRE_ET_AIR
	 * Tour.TYPE_TERRESTRE
	 * Tour.TYPE_AIR
	 */
	protected int type;
	
	/**
	 * image
	 */
	protected Image image;
	
	/**
	 * icone pour les boutons
	 */
	protected final Image ICONE;

	/**
	 * le jeu
	 */
	protected Game jeu;

	/**
	 * Utilise pour savoir si la tour est en jeu.
	 */
	protected boolean enJeu;
	
	/**
	 * Propriétaire de la tour
	 */
	protected Player proprietaire;

	/**
	 * Cadence de tir de la tour. C'est-a-dire le nombre de fois que peut tirer
	 * la tour pendant 1 seconde.
	 */
	private double cadenceTir; // tir(s) / seconde

	
	/**
	 * La tour est trournée ou non en direction de l'ennemi lorsqu'elle tire
	 */
	protected double angle = 0.0;
	
	// initialisation pour que la tour puisse tirer directement
    private long tempsDepuisDernierTir;
    private long tempsDAttenteEntreTirs;
	
	/**
	 * Constructeur de la tour.
	 * 
	 * @param x
	 *            la position sur l'axe X de la tour
	 * @param y
	 *            la position sur l'axe Y de la tour
	 * @param largeur
	 *            la largeur de la tour
	 * @param hauteur
	 *            la hauteur de la tour
	 * @param couleurDeFond
	 *            la couleur de fon de la tour
	 * @param nom
	 *            le nom de la tour
	 * @param prixAchat
	 *            le prix d'achat de la tour
	 */
	public Tower(int x, int y, int largeur, int hauteur, Color couleurDeFond,
			String nom, int prixAchat, long degats, double rayonPortee, 
			double cadenceTir, int type, Image image, Image icone)
	{
	    this.ID             = ++idCourant;
	    this.x              = x;
	    this.y              = y;
		width               = largeur;
		height              = hauteur;
		NOM			        = nom;
		COULEUR_DE_FOND 	= couleurDeFond;
		this.prixAchat 		= prixAchat;
		prixTotal 			= prixAchat;
		this.degats 		= degats;
		this.rayonPortee 	= rayonPortee;
		this.cadenceTir 	= cadenceTir;
		this.type		 	= type;
		this.image 			= image;
		ICONE               = icone;
		
		tempsDAttenteEntreTirs = (long) (1000.0 / cadenceTir);
		
		// pour que la tour tire directement après sa création
		tempsDepuisDernierTir = tempsDAttenteEntreTirs; 
	}
	
	/**
	 * methode qui prend les decisions concernant les améliorations de la tour
	 * de la tour.
	 */
	public abstract void ameliorer();

	/**
	 * methode qui prend les decision concernant les attaques de la tour.
	 */
	protected abstract void tirer(Creature creature);

	/**
     * Permet de recuperer une copie de l'originale de la tour actuelle
     * 
     * @return une tour ayant comme parent la classe Tour
     */
    public abstract Tower getCopieOriginale();

    /**
     * Permet de savoir si la tour peut encore etre ameliorer
     * 
     * @return true si elle peut encore l'etre, false sinon
     */
    public abstract boolean peutEncoreEtreAmelioree();
	
    /**
     * Permet de recuperer les dégats du niveau suivant
     */
    public abstract long getDegatsLvlSuivant();
    
    /**
     * Permet de recuperer le rayon de portee du niveau suivant
     */
    public abstract double getRayonPorteeLvlSuivant();
    
    /**
     * Permet de recuperer la cadence de tir du niveau suivant
     */
    public abstract double getCadenceTirLvlSuivant();
    
    /**
     * permet de recuperer le temps de preparation d'un tir
     * @return le temps de preparation d'un tir en miliseconde
     */
    public double getCadenceTir()
    {
        return cadenceTir;
    }
    
	/**
	 * Permet de modifier le jeu
	 * 
	 * @param jeu le jeu
	 */
	public void setJeu(Game jeu)
	{
		this.jeu = jeu;
	}

	/**
	 * Permet de récupérer l'identifiacteur de la tour
	 * @return l'identifiacteur de la tour
	 */
	public int getId()
    {
        return ID;
    }
	
	/**
	 * Permet de recuperer la couleur de fond d'une tour
	 * 
	 * @return la couleur de fond
	 */
	public Color getCouleurDeFond()
	{
		return COULEUR_DE_FOND;
	}

    /**
	 * Permet de recuperer le nom de la tour
	 * 
	 * @return
	 */
	public String getNom()
	{
		return NOM;
	}

	/**
	 * Permet de recuperer la position de la tour sur l'axe X
	 * 
	 * @return la position de la tour sur l'axe X
	 */
	public int getXi()
	{
		return x;
	}

	/**
	 * Permet de recuperer la position de la tour sur l'axe Y
	 * 
	 * @return la position de la tour sur l'axe Y
	 */
	public int getYi()
	{
		return y;
	}

	/**
	 * Permet de recuperer la description de la tour
	 * 
	 * @return la description de la tour
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Permet de recuperer le prix d'achat de la tour ou de son niveau suivant
	 * 
	 * @return le prix d'achat de la tour ou de son niveau suivant
	 */
	public int getPrixAchat()
	{
		return prixAchat;
	}

	/**
	 * Permet de recuperer le rayon de portee de la tour
	 * 
	 * @return le rayon de portee de la tour
	 */
	public double getRayonPortee()
	{
		return rayonPortee;
	}

	/**
	 * Permet de recuperer le prix de vente de la tour
	 * 
	 * @return
	 */
	public int getPrixDeVente()
	{
		return (int) (prixTotal * COEFF_PRIX_VENTE);
	}

	/**
	 * Permet de recuperer le prix total de la tour
	 * 
	 * @return le prix total de la tour
	 */
	public int getPrixTotal()
	{
		return prixTotal;
	}

	/**
	 * Permet de recuperer l'image de la tour
	 * 
	 * @return l'image de la tour
	 */
	public Image getImage()
	{
		return image;
	}
	
	/**
     * Permet de recuperer l'icone de la tour
     * 
     * @return l'icone de la tour
     */
    public Image getIcone()
    {
        return ICONE;
    }
    

    /**
     * Permet de recuperer le niveau actuel de la tour
     * 
     * @return le niveau actuel de la tour
     */
    public int getNiveau()
    {
        return niveau;
    }

    /**
     * Permet de recuperer les degats infliges par l'attaque de la tour
     * 
     * @return les degats infliges par l'attaque de la tour
     */
    public long getDegats()
    {
        return degats;
    }

    /**
     * Permet de recuperer le type de la tour sous la forme d'une chaine
     * de caracteres.
     * 
     * @return le type de la tour sous forme verbeuse.
     */
    public String getTexteType()
    {
        if(type == TYPE_TERRESTRE_ET_AIR)
            return Langue.getTexte(Langue.ID_TXT_TERRE)+" + "+Langue.getTexte(Langue.ID_TXT_AIR);
        else if(type == TYPE_TERRESTRE)
            return Langue.getTexte(Langue.ID_TXT_TERRE);
        else
            return Langue.getTexte(Langue.ID_TXT_AIR);
    }
    
    /**
     * Permet de recuperer le type de la tour.
     * 
     * @return le type de la tour.
     */
    public int getType()
    {
        return type;
    }
    
	/**
	 * Permet de mettre la tour en jeu. Cette methode demarre le thread de la
	 * tour. Des lors, elle tirera sur les creatures.
	 */
	public void mettreEnJeu()
	{
		enJeu = true;
	}

	/**
	 * Permet d'arreter la tour, de la sortir du jeu
	 */
	public void arreter()
	{
		enJeu = false;
	}

	/**
	 * Permet de savoir si la tour est en jeu
	 * 
	 * @return true si la tour est en jeu, false sinon
	 */
	public boolean estEnJeu()
	{
	    return enJeu;
	}
	
	/**
	 * Permet de modifier la cadence de tir de la tour
	 * 
	 * En place pour eviter de recalculer le tempsDAttenteEntreTirs
	 * 
	 * @param cadenceTir la nouvelle cadence de tir
	 */
	public void setCadenceTir(double cadenceTir) 
	{
	    this.cadenceTir = cadenceTir;
	    tempsDAttenteEntreTirs = (long) (1000.0 / cadenceTir);
	}

	/**
	 * Permet de faire des actions de la tour
	 * 
	 * Cette methode est normalement appelee par un thread commun a toutes les
	 * tours et permet d'effectuer les actions de la tour, c-a-d tirer.
	 */
	public void action(long tempsPasse)
	{  
        // on calcul le temps depuis le dernier tir
        tempsDepuisDernierTir += tempsPasse;

        // on a attendu assez pour pouvoir tirer
        if(tempsDepuisDernierTir >= tempsDAttenteEntreTirs)
        {
            // Selection de la cible
            Creature creature = null;
	        switch(typeCiblage)
	        {
	            case CIBLAGE_CREATURE_PLUS_PROCHE :
                    creature = getCreatureLaPlusProcheOuLoin(true);
	                break;
	            case CIBLAGE_CREATURE_PLUS_LOIN :
	                creature = getCreatureLaPlusProcheOuLoin(false);
                    break;
	            case CIBLAGE_CREATURE_PLUS_FAIBLE :
	                creature = getCreatureLaPlusFaibleOuForte(true);
	                break;
	            case CIBLAGE_CREATURE_PLUS_FORTE :
	                creature = getCreatureLaPlusFaibleOuForte(false);
                    break;
	            default :
	                System.err.println("Type de ciblage inconnu");
	        }
             
            // si la cible existe...
            if (creature != null)
            {
                // attaque la creature ciblee
                tirer(creature);
                
                /* 
                 * on soustrait le tempsDAttenteEntreTirs pour
                 * garder le temps supplémentaire
                 */
                tempsDepuisDernierTir -= tempsDAttenteEntreTirs;
            }
            else 
                /* 
                 * on incrémente pas plus le temps car ca sert a rien 
                 * et ca risquerai d'etre cyclique.
                 */
                tempsDepuisDernierTir = tempsDAttenteEntreTirs;
        }
    }
	
	/**
	 * Permet de recuperer la creature la plus proche et a portee de la tour.
	 * 
	 * @param laPlusProche vrai si on veut la créature plus proche sinon false, 
	 * la créature plus loin
	 * 
	 * @return la creature la plus proche et a portee de la tour ou <b>null s'il
	 *         n'y a pas de creature a portee</b>
	 */
	private Creature getCreatureLaPlusProcheOuLoin(boolean laPlusProche)
	{
	    // le terrain a bien ete setter ?
		if (jeu == null)
			return null;

		// variables temporaires pour calcul
		Creature creatureLaPlusProche = null;
		
		double distanceMinMax = -1;
		
		// distance la plus proche
		if(laPlusProche)
		    distanceMinMax = java.lang.Double.MAX_VALUE ;
		
		double tmpDistance = 0;

		// bloque la reference vers la collection des creatures
		Creature creature;
        Enumeration<Creature> eCreatures = jeu.getCreatures().elements();
        while(eCreatures.hasMoreElements())
        {
            try{
                
                creature = eCreatures.nextElement();
                
        		// si la creature est accessible
        	    if (creature.peutEtreAttaquee(this))
                {
        		    // calcul de la distance entre la tour et la creature
        			tmpDistance = getDistance(creature);
        
        			// est-elle a portee ?
        			if (tmpDistance <= rayonPortee)
        			{
        				// la creature actuelle est-elle plus proche que la derniere
        				// creature a portee testee ?
        				if (creatureLaPlusProche == null 
        				|| laPlusProche && tmpDistance < distanceMinMax
        				|| !laPlusProche && tmpDistance > distanceMinMax)
        				{ 
        				    // nouvelle creature plus proche trouvee!
        					creatureLaPlusProche = creature;
        					distanceMinMax = tmpDistance;
        				}
        			}
                }
        	}
    	    catch(java.util.NoSuchElementException nsee)
    	    {
    	        nsee.printStackTrace();
    	    }
        }
		
		return creatureLaPlusProche;
	}
	
	/**
     * Permet de recuperer la creature la plus faible et a portee de la tour.
     * 
     * @return la creature la plus faible et a portee de la tour ou <b>null s'il
     *         n'y a pas de creature a portee</b>
     */
    private Creature getCreatureLaPlusFaibleOuForte(boolean plusFaible)
    {
        // le terrain a bien ete setter ?
        if (jeu == null)
            return null;

        // variables temporaires pour calcul
        Creature creatureLaPlusFaible   = null;
        
        long santeMinMax = 0;
        if(plusFaible) 
            santeMinMax = Long.MAX_VALUE;

        // bloque la reference vers la collection des creatures
        Creature creature;
        Enumeration<Creature> eCreatures = jeu.getCreatures().elements();
        while(eCreatures.hasMoreElements())
        {
            try{
                
                creature = eCreatures.nextElement();
                
                // si la creature est accessible
                if (creature.peutEtreAttaquee(this))
                {
                    // est-elle a portee ?
                    if (getDistance(creature) <= rayonPortee)
                    {
                        // la creature actuelle est-elle plus proche que la derniere
                        // creature a portee testee ?
                        if (plusFaible && creature.getSante() < santeMinMax
                        || !plusFaible && creature.getSante() > santeMinMax)
                        {
                            // nouvelle creature plus proche trouvee!
                            creatureLaPlusFaible = creature;
                            santeMinMax = creature.getSante();
                        }
                    }
                }
            }
            catch(java.util.NoSuchElementException nsee)
            {
                nsee.printStackTrace();
            }
        }
        
        return creatureLaPlusFaible;
    }
	
	

	/**
	 * Calcul la distance en vol d'oiseau entre la tour et une creature
	 * 
	 * @param creature
	 *            la creature
	 * @return la distance en vol d'oiseau entre la tour et une creature
	 */
	protected double getDistance(Creature creature)
	{
		return Point.distance(x, y, creature.x, creature.y);
	}
	
    /**
     * Permet de recuperer le temps ecouler depuis le dernier appel de cette meme 
     * fonction
     * @return le temps en milliseconde entre chaque appel de cette fonction
     *         si c'est le premier appel, retourne 0.
     */
    protected long getTempsAppel()
    {
        // initialisation du temps actuel
        long maintenant = System.currentTimeMillis(); 
        
        // si c'est la premiere fois qu'on passe
        if(tempsDernierAppel == 0)
        {
            tempsDernierAppel = maintenant;
            return 0;
        }
        
        // temps passe depuis le dernier appel
        long tempsEcoule = maintenant - tempsDernierAppel;
        tempsDernierAppel = maintenant;
        return tempsEcoule;
    }
    private long tempsDernierAppel;
    

    /**
     * Permet de recuperer le propriétaire de la tour
     * 
     * @return le propriétaire de la tour
     */
    public Player getPrioprietaire()
    {
        return proprietaire;
    }
    
    /**
     * Permet de modifier le propriétaire de la tour
     * 
     * @param JoueurDistant le propriétaire de la tour
     */
    public void setProprietaire(Player proprietaire)
    {
        this.proprietaire = proprietaire;
    }

    /**
     * Permet de recuperer l'angle de la tour
     */
    public double getAngle()
    {
        return angle;
    }

    public void setId(int id)
    {
       this.ID = id;
    }

    public void setTypeCiblage(int typeCiblage)
    {
        this.typeCiblage = typeCiblage;
    }

    public int getTypeCiblage()
    { 
        return typeCiblage;
    }
}
