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

import models.animations.Animation;
import models.creatures.Creature;
import models.creatures.WaveOfCreatures;
import models.player.Team;
import models.player.Player;
import models.towers.Tower;

/**
 * Interface permettant de s'abonner au jeu pour recevoir des notifications 
 * lorsque le jeu change d'état.
 * 接口接收游戏状态改变通知
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 * @see Game
 */
public interface GameListener
{
    
	// 在play状态
    
    /**
     * Permet d'informer l'écouteur que la partie à été initialisée
     * 初始化部分
     */
    public void initializationPart();
    
    /**
     * Permet d'informer l'écouteur que la partie a démarrée
     * 开始监听
     */
    public void startPart();
    
    /**
     * Permet d'informer l'écouteur que la partie est terminee
     * 游戏结束
     */
    public void terminatePart(GameResult resultatJeu);
    
    /**
     * Permet d'informer l'écouteur que le joueur a gagné une étoile
     * 取得星级
     */
    public void winStar();
    
    /**
     * Permet d'informer l'écouteur que la vitesse du jeu à été modifiée
     * 游戏速度被改变
     */
    public void velocityChanged(double coeffVitesse);
    
    // JOUEURS
    // 玩家
    
    /**
     * Permet d'informer l'écouteur qu'un joueur a rejoint la partie
     * 玩家已加入
     */
    public void playerJoin(Player joueur);
    
    /**
     * Permet d'informer l'écouteur qu'un joueur a été mis à jour
     * 玩家更新
     */
    public void joueurMisAJour(Player joueur);
    
    /**
     * Permet d'informer l'écouteur qu'une equipe a perdue
     * 一队输了
     */
    public void teamLost(Team equipe);
    
    
    // TOURS
    // 防御塔
    
    /**
     * Permet d'informer l'écouteur qu'une tour à été posée
     * 一个塔被安装
     */
    public void towerPlaced(Tower tour);
    
    /**
     * Permet d'informer l'écouteur qu'une tour à été vendue
     * 一个塔售出
     */
    public void towerSold(Tower tour);
    
    /**
     * Permet d'informer l'écouteur qu'une tour à été améliorée
     * 一个塔已升级
     */
    public void towerUpgrade(Tower tour);
    
    
    // VAGUES
    // 攻击波
    
    /**
     * Permet d'informer l'écouteur du lancement de vague termine
     * 一轮攻击完成
     */
    public void waveAttackFinish(WaveOfCreatures vague);

    
    // CREATURES
    // 生物
    
    /**
     * Permet d'informer l'écouteur qu'une créature à été blessée
     * 添加一个生物
     */
    public void creatureAjoutee(Creature creature);
    
    
    /**
     * Permet d'informer l'écouteur qu'une créature à été blessée
     * 动物受伤
     */
    public void creatureInjured(Creature creature);

    /**
     * Permet d'informer l'écouteur de la mort d'une créature
     * 生物死亡
     */
    public void creatureKilled(Creature creature,Player tueur);
    
    /**
     * Permet d'informer l'écouteur l'arrivée d'une créature
     * 生物到达终点
     */
    public void creatureArriveEndZone(Creature creature);
    
    
    // ANIMATIONS
    // 动画
    
    /**
     * Permet d'informer l'écouteur qu'une animation à été ajoutée
     * 动画被添加
     */
    public void animationAjoutee(Animation animation);
    
    /**
     * Permet d'informer l'écouteur qu'une animation à été terminée
     * 动画完成
     */
    public void animationTerminee(Animation animation);

}
