/*
  Copyright (C) 2010 Aurelien Da Campo, Romain Poulain, 
  Pierre-Dominique Putallaz
  
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

package net.jeu.server;

/**
 * 该接口包含客户端与服务器间对话的所有必要的常量协议
 * 
 * @author Da Campo Aurélien
 * @author Pierre-Dominique Putallaz
 * @author Romain Poulain
 */
public interface ConstantesServerJeu
{
    
    // COMMUNICATION 0-99 通信
    
    /**
     * 消息：给所有的玩家
     */
    public final int A_TOUS = -1;
    
    /**
     * 消息：从游戏服务器
     */
    public final int DU_SERVEUR = 2;
    
    
    // PARTIE 100-199 部分
    
    /**
	 * Démarrage de la partie
	 * 开始部分
	 * 初始化
	 */
	public final int PARTIE_INITIALISEE = 100;
	
	/**
     * Démarrage de la partie
     * 开始部分
     * 发射
     */
    public final int PARTIE_LANCEE = 101;
	
	/**
	 * Arrêt de la partie
	 */
	public final int PARTIE_FIN = 102;
	
    /**
     * Code pour quitter la partie
     * 退出游戏
     */
    public final int PARTIE_QUITTER = 103;

	/**
     * Type de message : état de la partie
     * 消息类型：游戏状态
     */
    public final int PARTIE_ETAT = 104;
    
    /**
     * Changement d'état de la partie : en jeu
     * 变化的游戏状态：进行中
     */
    public final int PARTIE_TERMINEE = 105;
    
    /**
     * Code pour quitter la partie
     */
    public final int PARTIE_STOPPEE_BRUTALEMENT = 106;
    
    /**
     * Changement d'état de la partie : en pause
     * 改变游戏状态：暂停
     */
    public final int EN_PAUSE = 107;
    
    /**
     * Changement d'état de la partie : en jeu
     */
    public final int EN_JEU = 108;
    
	// SUCCES 200-299
	
	/**
	 * Code de succès
	 */
	public final int OK = 200;
	
	
	// JOUEUR & EQUIPE 300-309
	
	/**
     * Type de message : initialisation d'un joueur
     */
    public final int JOUEUR_INITIALISATION = 300;

    /**
     * Type de message : ajout d'un joueur
     */
    public final int JOUEUR_CHANGER_EQUIPE = 302;
    
	/**
	 * Type de message : état d'un joueur
	 */
	public final int JOUEUR_ETAT = 303;
	
    /**
     * Type de message : un message texte
     */
    public final int JOUEURS_ETAT = 304;
    
    /**
     * Type de message : un message texte
     */
    public final int JOUEUR_PRET = 305;
    
    /**
     * Type de message : un message texte
     */
    public final int JOUEUR_DECONNEXION = 306;
    
	/**
     * Type de message : un message texte
     */
    public final int JOUEUR_MESSAGE = 309;
	
    
    /**
     * Type de message : une equipe a perdue
     */
    public final int EQUIPE_A_PERDUE = 30101;
    
    
    
    
    
	// CREATURE 310-319
	
	/**
	 * Type de message : ajout d'une créature
	 */
	public final int CREATURE_AJOUT = 310;
	
	/**
     * Type de message : etat d'une créature
     */
    public final int CREATURE_ETAT = 311;
	
	/**
     * Type de message : suppression d'une créature
     */
    public final int CREATURE_SUPPRESSION = 312;
	
    /**
     * Type de message : créature est arrivé
     */
    public final int CREATURE_ARRIVEE = 313;
    
    /**
     * Type de message : vague de création
     */
    public final int VAGUE = 314;
    
	
   
	// TOUR 320-329
	
	/**
     * Type de message : nouvelle tour
     */
    public final int TOUR_AJOUT = 320;
    
    /**
     * Type de message : suppression d'une tour
     */
    public final int TOUR_SUPRESSION = 321;
    
    /**
     * Type de message : amélioration d'une tour
     */
    public final int TOUR_AMELIORATION = 322;
    
    /**
     * Type de message : vente tour
     */
    public final int TOUR_VENTE = 323;
	
	
	// ANIMATION 330 - 340
    
	/**
	 * Type de message : état d'une animation
	 */
	public final int ANIMATION_AJOUT = 330;
	

	
	
	// AUTRES 350 - 399
	
	/**
	 * Type de message : un objet
	 */
	public final int OBJET = 350;
	
	

	// ERREURS 400-499
	
	/**
	 * Code d'erreur : pas assez d'argent
	 */
	public final int ARGENT_INSUFFISANT = 400; //ou PAUVRE, a choix :P
	
	/**
	 * Code d'erreur : mauvaise position de l'objet
	 */
	public final int ZONE_INACCESSIBLE = 401;
	
	/**
	 * Code d'erreur : chemin bloqué
	 */
	public final int CHEMIN_BLOQUE = 402;

	/**
     * Code d'erreur : Niveau max de la tour atteint
     */
    public final int NIVEAU_MAX_ATTEINT = 403;
	
    /**
     * Code d'erreur : action non autorisee
     */
    public final int ACTION_NON_AUTORISEE = 404;
    
    /**
     * Code d'erreur : pas de place dans l'equipe
     */
    public final int PAS_DE_PLACE = 405;
    
    /**
     * Code d'erreur : jeu déjà en cours
     */
    public final int JEU_EN_COURS = 406;
    
    /**
     * Code d'erreur : tour inconnue
     */
    public final int TOUR_INCONNUE = 407;
    
    /**
     * Code d'erreur : joueur inconnu
     */
    public final int JOUEUR_INCONNU = 408;
    
    /**
     * Code d'erreur : joueur inconnu
     */
    public final int JOUEUR_HORS_JEU = 409;
    
    /**
     * Code d'erreur : type de tour invalide
     */
    public final int TYPE_TOUR_INVALIDE = 410;
    
	/**
	 * Code d'erreur : erreur quelconque
	 * Code：出错
	 */
	public final int ERREUR = 450;
}
