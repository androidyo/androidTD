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

package models.creatures;

import i18n.Language;

import java.awt.*;
import java.util.*;

import models.player.*;
import models.towers.Tower;

/**
 * 生物管理类
 * <p>
 * Les creatures sont des bestioles qui attaque le joueur. L'objectif de celles-ci
 * est simple : se rendre le plus vite possible (chemin le plus court) d'une zone
 * A a un zone B. Si la creature arrive a survivre jusqu'a la zone B, le joueur 
 * perdra une de ses precieuses vies.
 * 这些动物是游戏的攻击选手，目标很简单：以最短路径从A	到B，并且使玩家失去一点生命
 * <p>
 * Il existe deux types de creatures, les volantes et les terriennes. Les volantes
 * ne sont pas affecter par les emplacements des tours. Elle volent 
 * simplement de la zone A a la zone B en évitant tout de même les murs du terrain.
 * 有两种类型的生物，地面和飞行。飞行单位不会受塔的位置影响。直接避免墙壁从A区到B区
 * 
 * @author Aurélien Da Campo
 * @version 1.3 | juin 2010
 * @since jdk1.6.0_16
 */
public abstract class Creature extends Rectangle
{
	private static final long serialVersionUID = 1L;
	
	/**
     * 这种生物的唯一标识符
     */
    private int id;
    private static int idCourant = 0;
	
	/**
	 * Permet de stocker sous la forme reelle la position de la creature pour rendre 
	 * fluide les mouvements
	 * 
	 * Notons que Rectangle nous fourni egalement des positions 
	 * mais elles sont entieres
	 */
	private double xReel, yReel;
	
	/**
	 * definition des deux types de creature
	 * 定义两种类型的生物：陆地和飞行
	 */
	public static final int TYPE_TERRIENNE 	= 0;
	public static final int TYPE_AERIENNE 	= 1;

	/**
	 * Temps avant la suppression de la créature si aucune nouvelle n'est recue.
	 * 拆除前的时间，如果没有收到消息的生物
	 * 
	 * Valable pour les clients réseau uniquement.
	 * 只适用于网络客户端
	 */
    private static final long TEMPS_AVANT_SUPPRESSION_SI_AUCUNE_MAJ = 3000;

    /**
     * Propriétaire de la créature
     * 生物的所有者
     */
    private Player proprietaire;
    
	/**
	 * nom de la creature
	 * 生物的名字
	 */
    private final String NOM;
    
    /**
     * type de la creature
     * 生物类型
     */
	private final int TYPE;
	
	/**
	 * chemin actuel de la creature
	 */
	private ArrayList<Point> chemin;
	
	/**
     * position actuelle sur le chemin (toujours > 0)
     * 当前位置在路上（总是>0）
     */
	private int indiceCourantChemin;
	
	/**
	 * 生物的生命，如果<=0，其死亡 
	 * A ce moment la, elle donne au joueur ses pieces d'or
	 */
	private long sante;
	
	/**
	 * sante maximale de la creature. Utilise pour calculer le pourcentage de 
	 * vie restante de la creature.
	 * 生物的生命指数，用于计算剩余寿命的百分比
	 */
	private long santeMax;
	
	/**
	 * 金币数，生物死后玩家获取的金币
	 */
	private int nbPiecesDOr;
	
	/**
     * le prix de la créature
     */
    // TODO private int prixAchat;
	
	/**
	 * 当前生物图像
	 */
	protected Image image;
	
	/**
	 * vitesse de deplacement de la creature sur le terrain en pixel(s) par seconde
	 * 每秒生物在地面上移动的速度 像素／秒
	 */
	protected double vitesseNormale; // en pixel(s) / seconde
	
	/**
     * ralentissement de deplacement de la creature sur le terrain
     * 减缓地面生物运动
     */
    protected double coeffRalentissement; // 0.0 = vitesse normal, 1.0 = 100%

	/**
	 * permet d'informer d'autres entites du programme lorsque la creature
	 * subie des modifications.
	 */
	private ArrayList<CreatureListener> ecouteursDeCreature;
	
	/**
	 * Permet de savoir s'il faut detruire l'animation
	 */
	private boolean aDetruire;
	
	/**
	 * Il s'agit de la cible a attaquer
	 */
	private Team equipeCiblee;

	/**
	 * Stockage de l'angle entre la créature et son prochain noeud
	 */
    private double angle = 0.0;
	
    /**
     * Utiliser pour les clients réseau.
     * 
     * Il ne faut pas que les animations (gérées en local) puissent 
     * blesser les créatures. Donc une maniere rapide de faire 
     * ceci est de rendre les créatures invincibles pour ne pas 
     * la détruire chez le client.
     */
    private boolean invincible = false;

    private final double LARGEUR_MOITIE;
    private final double HAUTEUR_MOITIE;

    
	/**
	 * Constructeur de la creature.
	 * 
	 * @param x la position sur l'axe X de la creature
	 * @param y la position sur l'axe Y de la creature
	 * @param largeur la largeur de la creature
	 * @param hauteur la hauteur de la creature
	 * @param santeMax la sante maximale de la creature
	 * @param nbPiecesDOr le nombre de pieces de la creature
	 * @param vitesse vitesse de deplacement de la creatures
	 * @param type type de creature
	 * @param image image de la creature sur le terrain
	 * @param nom nom de l'espece de creature
	 */
	public Creature(int x, int y, int largeur, int hauteur, 
					long santeMax, int nbPiecesDOr, double vitesse, 
					int type, Image image, String nom)
	{
		super(x,y,largeur,hauteur);
		
		LARGEUR_MOITIE = largeur / 2.0;
		HAUTEUR_MOITIE = hauteur / 2.0;
		
		xReel = x;
		yReel = y;
		
		this.id             = ++idCourant;
		this.nbPiecesDOr 	= nbPiecesDOr;
		this.santeMax		= santeMax;
		sante 				= santeMax;
		this.vitesseNormale = vitesse;
		ecouteursDeCreature = new ArrayList<CreatureListener>();
		this.image 			= image;
		TYPE                = type;
		NOM                 = nom;
	}

	/**
	 * Force les fils de la classe a gerer la copie de la creature.
	 * <p>
	 * Note : cette methode est utilisee lors de la creation d'une vague
	 * de creatures. Au lieu de stocker toutes les creatures de la vague, 
	 * on creer une seule instance de la creature et on la duplique le nombre de
	 * fois souhaite.
	 * 
	 * @return la copie de la creature.
	 */
	abstract public Creature copier();
	
	/**
	 * Permet de recuperer le chemin actuellement suivi par la creature.
	 * @return le chemin actuellement suivi par la creature
	 */
	public ArrayList<Point> getChemin()
	{
		return chemin;
	}

	/**
     * Permet de récupérer l'identificateur de la créature
     * @return l'identifiacteur de la créature
     */
    public int getId()
    {
        return id;
    }
	
	/**
	 * Permet de modifier le propriétaire
	 * 
     * @param proprietaire le proprietaire
     */
    public void setProprietaire(Player proprietaire)
    {
        this.proprietaire = proprietaire;
    }

    /**
     * Permet de récupérer le propriétaire
     * 
     * @return le proprietaire
     */
    public Player getProprietaire()
    {
        return proprietaire;
    }

    /**
	 * Permet de recuperer le type de la creature.
	 * 
	 * @return le type de la creature
	 */
	public int getType()
	{
		return TYPE;
	}
	
	/**
	 * Permet de recuperer la sante de la creature.
	 * 
	 * @return la sante de la creature
	 */
	public long getSante()
	{
		return sante;
	}

	/**
	 * Permet de recuperer la sante maximale de la creature.
	 * 
	 * @return la sante maximale de la creature
	 */
	public long getSanteMax()
	{
		return santeMax;
	}
	
	/**
	 * Permet de recuperer le nombre de pieces d'or de la creature.
	 * 
	 * @return le nombre de pieces d'or de la creature
	 */
	public int getNbPiecesDOr()
	{
		return nbPiecesDOr;
	}
	
	/**
	 * Permet de recuperer la vitesse normale (sans ralentissement) 
	 * de la creature
	 * 
	 * @return la vitesse de la creature
	 */
	public double getVitesseNormale()
	{
		return vitesseNormale;
	}
	
	/**
     * Permet de recuperer la vitesse reelle de la creature (avec ralentissement)
     * 
     * @return la vitesse reelle de la creature
     */
    public double getVitesseReelle()
    {
        return vitesseNormale - vitesseNormale * coeffRalentissement;
    }
    
    /**
     * Permet de recuperer le coefficient de ralentissement
     * @return
     */
    public double getCoeffRalentissement()
    {
        return coeffRalentissement;
    }
    
    /**
     * Permet de rendre une créature invincible ou non
     * 
     * @param invincible l'état
     */
    public void setInvincible(boolean invincible)
    {
        this.invincible = invincible;
    }
    
    /**
     * Permet de modifier le coefficient de ralentissement
     * @param coeffRalentissement le nouveau coefficient de ralentissement
     */
    public void setCoeffRalentissement(double coeffRalentissement)
    {
        if(coeffRalentissement > 1.0)
            coeffRalentissement = 1.0;
        else if(coeffRalentissement < 0.0)
            coeffRalentissement = 0.0;
        else  
            this.coeffRalentissement = coeffRalentissement;
    }
    
	/**
	 * Permet de recuperer l'image actuelle de la creature
	 * 
	 * @return l'image actuelle de la creature
	 */
	public Image getImage()
	{
		return image;
	}
	
	/**
     * Permet de recuperer le type de la creature sous forme textuelle
     * 
     * @return le type de la creature sous forme textuelle
     */
    public String getNomType()
    {
        if(TYPE == TYPE_TERRIENNE)
            return Language.getTexte(Language.ID_TXT_TERRIENNE);
        else
            return Language.getTexte(Language.ID_TXT_AERIENNE);
    }

    /**
     * Permet de recuperer le nom de la creature
     * @return le nom de la creature
     */
    public String getNom()
    {
        return NOM;
    }
	
    /**
     * Permet de recuperer l'angle entre la creature et le prochain noeud
     * [angle de déplcement]
     * 
     * @return l'angle entre la creature et le prochain noeud
     */
    public double getAngle()
    {
        return angle;
    }
    
	/**
	 * Permet de recuperer la position sur l'axe X de la creature
	 * 
	 * @param x la position sur l'axe X de la creature
	 */
	public void setX(int x)
	{
		this.x = x;
		this.xReel = x;
	}
	
	/**
	 * Permet de recuperer la position sur l'axe Y de la creature
	 * 
	 * @param x la position sur l'axe Y de la creature
	 */
	public void setY(int y)
	{
		this.y = y;
		this.yReel = y;
	}

	/**
	 * Permet de modifier le chemin actuel de la creature
	 * 
	 * @param chemin le nouveau chemin
	 */
	public void setChemin(ArrayList<Point> chemin)
	{
	    // on est deja au point 0, on ne vas donc pas y aller...
        // (i) corrige un petit bug de retour en arriere.
        indiceCourantChemin = 1; 
	    
	    this.chemin = chemin;
	}
	
	/**
	 * Permet de recuperer l'indice du point courant sur le chemin
	 * 
	 * @return l'indice du point courant sur le chemin
	 */
	public int getIndiceCourantChemin()
	{
	    return indiceCourantChemin;
	}
	
	/**
	 * Cette methode est appelee pour dire a la creature d'effectuee des actions
	 * 
	 * @param tempsPasse le temps passé à prendre en considération pour les calculs
	 */
	public void action(long tempsPasse)
	{
	    // avance la creature
	    avancerSurChemin(tempsPasse);
	    
	    // la creature est arrivee a destination !
        if(chemin != null && indiceCourantChemin == chemin.size() 
           && !aDetruire && !estMorte())
        {
            aDetruire = true;

            // informe les ecouteurs que la creature est arrivee 
            // a la fin du parcours
            for(CreatureListener edc : ecouteursDeCreature)
                edc.creatureArriveEndZone(this);
        }
	}
	
	/**
     * Permet de faire avancer la creature sur son chemin.
     * 
     * Celle-ci avance sur le chemin en fonction du temps écoulé.
     * 
     * @param tempsEcoule le temps ecoule depuis le dernier appel
     */
    protected void avancerSurChemin(long tempsEcoule)
    {
        // si la creature a un chemin et que le chemin n'est pas terminee, 
        // elle avance...
        if(chemin != null && indiceCourantChemin < chemin.size())
        {   
            // calcul de la distance a parcourir sur le chemin
            double distanceAParcourir = getVitesseReelle() * ((double) tempsEcoule / 1000.0);
            
            //---------------------------------------------
            //-- calcul de la position apres deplacement --
            //---------------------------------------------
            // tant que la créature n'a pas parcourue toute la distance
            // qu'elle doit parcourir et que le chemin n'est pas terminé
            while(distanceAParcourir > 0 && indiceCourantChemin < chemin.size())
            {
                // calcul du centre de la creature
                double centreX = xReel + LARGEUR_MOITIE;
                double centreY = yReel + HAUTEUR_MOITIE;
                
                // recuperation des noeuds
                Point pSuivant   = chemin.get(indiceCourantChemin);
            
                // calcul de l'angle entre la creature et le noeud suivant
                // /!\ Math.atan2(y,x) /!\
                angle = Math.atan2(centreY - pSuivant.y,centreX - pSuivant.x);
     
                //--------------------------
                //-- calcul des distances --
                //--------------------------
                // pour savoir si la creature a depassée le point suivant du chemin
                
                // calcul de la distance entre le noeud precedent et suivant
                double distanceCreatureNoeudSuivant = Point.distance(
                        centreX, centreY,
                        pSuivant.x, pSuivant.y);

                // noeud suivant atteint
                if(distanceAParcourir >= distanceCreatureNoeudSuivant)
                {
                    // il prend la position du noeud suivant
                    xReel = pSuivant.x - LARGEUR_MOITIE;
                    yReel = pSuivant.y - HAUTEUR_MOITIE;
                    
                    // le prochain noeud devient le noeud suivant
                    indiceCourantChemin++;
                    
                    // diminution de la distance parcouru jusqu'au point
                    distanceAParcourir -= distanceCreatureNoeudSuivant;
                }
                // la créature n'arrive pas jusqu'au noeud suivant
                else
                {
                    // calcul la position apres mouvement de la creature
                    xReel -= Math.cos(angle)*distanceAParcourir; // x
                    yReel -= Math.sin(angle)*distanceAParcourir; // y
                    
                    // toute la distance a été parcourue
                    distanceAParcourir = 0;
                }
            }
            
            // mise a jour des coordonnees entieres
            x = (int) Math.round(xReel);
            y = (int) Math.round(yReel);
        }
    }
	
	/**
	 * Permet de faire subir des degats sur la creature
	 * 
	 * L'attaque pouvant venir de plusieurs tours en meme temps, cette 
	 * methode doit etres synchronisee.
	 * 
	 * @param degats les degats recus
	 */
	synchronized public void blesser(long degats, Player joueur)
	{
		// si pas deja morte et que le joueur peu la tuer
		if(!estMorte() && joueur.getEquipe() == equipeCiblee)
		{
			// diminution de la sante
			if(!invincible) // pour les clients...
			    sante -= degats;
			
			// appel des ecouteurs de la creature
			for(CreatureListener edc : ecouteursDeCreature)
				edc.creatureDead(this);
			
			// est-elle morte ?
			if(estMorte())
				mourrir(joueur);
		}
	}
	
	/**
	 * Permet savoir si la creature est morte
	 */
	public boolean estMorte()
	{
		return sante <= 0;
	}
	
	/**
	 * Permet de tuer la creature
	 */
	public void mourrir(Player tueur)
	{ 
	    sante = 0;
		
		// appel des ecouteurs de la creature
		for(CreatureListener edc : ecouteursDeCreature)
			edc.creatureHurt(this,tueur);
		
		aDetruire = true;
	}

	/**
	 * Permet d'ajouter un ecouteur de la creature
	 * 
	 * @param edc une classe implementant EcouteurDeCreature
	 */
	public void ajouterEcouteurDeCreature(CreatureListener edc)
	{
		ecouteursDeCreature.add(edc);
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
     * Permet d'informer un instance superieure qu'il faut detruire la creature
     * 
     * @return true s'il faut la detruire, false sinon
     */
    public boolean aDetruire()
    {
        return aDetruire;
    }

    /**
     * Permet de récupérer l'équipe ennemie ciblée
     * 
     * @return l'équipe ennemie ciblée
     */
    public Team getEquipeCiblee()
    {
        return equipeCiblee;
    }

    /**
     * Permet de modifier l'equipe ciblée par la créature
     * 
     * @param equipeCiblee le nouvelle équipe ciblée
     */
    public void setEquipeCiblee(Team equipeCiblee)
    {
        this.equipeCiblee = equipeCiblee;
    }

    /**
     * Permet de modifier l'id 
     * 
     * @param id le nouvel id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Permet de modifier la sante
     * 
     * @param sante la nouvelle sante
     */
    public void setSante(int sante)
    {
        this.sante = sante;
    }

    /**
     * Permet de modifier l'angle
     * 
     * @param angle le nouvel angle
     */
    public void setAngle(double angle)
    {
        this.angle = angle;
    }
    
    /**
     * Permet de savoir si une creature peut etre blessee.
     * 
     * Il s'agit en fait d'une verification des types et des equipes.
     * 
     * @param creature le créature a testee
     * @return true si la creature peut etre blessee, false sinon
     */
    public boolean peutEtreAttaquee(Tower tour)
    {
        // si c'est pas une créature ennemie
        if(tour.getPrioprietaire().getEquipe() != equipeCiblee)
            return false;
        
        // elle est blessable
        int typeTour = tour.getType();
        return typeTour == Tower.TYPE_TERRESTRE_ET_AIR 
            || (typeTour == Tower.TYPE_TERRESTRE && TYPE == Creature.TYPE_TERRIENNE) 
            || (typeTour == Tower.TYPE_AIR && TYPE == Creature.TYPE_AERIENNE);
    }

    /**
     * Permet de modifier la santé maximale.
     * 
     * @param santeMax la nouvelle santé maximale
     */
    public void setSanteMax(long santeMax)
    {
        this.santeMax = santeMax;
    }

    /**
     * Permet de modifier le nombre de pièces d'or
     * 
     * @param nbPiecesDOr le nouveau nombre de pièces d'or
     */
    public void setNbPiecesDOr(int nbPiecesDOr)
    {
        this.nbPiecesDOr = nbPiecesDOr;
    }

    /**
     * Permet de modifier la vitesse
     * 
     * @param vitesse le nouvelle vitesse
     */
    public void setVitesse(double vitesse)
    {
        this.vitesseNormale = vitesse;
    }

    long tempsDerniereMAJ = 0;
    /**
     * Permet d'indiquer une mise à jour de la créature
     */
    public void misAJour()
    {
        tempsDerniereMAJ = new Date().getTime();   
    }

    /**
     * Permet d'effacer la créature si elle n'a pas été mise a jour depuis 
     * un certain temps.
     * <br><br>
     * Cette méthode résout un bug réseau lorsque le message de mort n'arrive pas
     * au client. Pour le client la créature existe toujours mais elle n'exite plus 
     * sur le serveur. 
     */
    public void effacerSiPasMisAJour()
    {
        long maintenant = new Date().getTime(); 
        
        if(tempsDerniereMAJ != 0) // seulement si mis a jour une fois
        {
            if(maintenant - tempsDerniereMAJ > TEMPS_AVANT_SUPPRESSION_SI_AUCUNE_MAJ)
            {
                System.out.println("CREATURE TUEE PAR FORCE APRES "+
                       (TEMPS_AVANT_SUPPRESSION_SI_AUCUNE_MAJ / 1000.0)+
                       " SEC. D'INACTIVITE ! (id : "+id+" )");
                
                mourrir(null);
            }
        }
    }
}
