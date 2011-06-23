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

import models.player.Player;

/**
 * 监听生物接口
 * 
 * Permet d'etre renseigne lorsqu'une creature subi des modifications.
 * 允许进行信息修改
 * 
 * @author Aurélien Da Campo
 * @version 1.1 | mai 2010
 * @since jdk1.6.0_16
 * @see Creature
 */
public interface CreatureListener
{
	/**
	 * 一个生物受到伤害
	 * @param creature la creature qui a subie des degats
	 * @param tueur 
	 */
    void creatureHurt(Creature creature, Player tueur);
	
	/**
	 * 一个生物死亡
	 * @param creature la creature qui a ete tuee
	 */
	void creatureDead(Creature creature);
	
	/**
	 * 一个生物到达路的尽头
	 * @param creature la creature qui est arrivee
	 */
	void creatureArriveEndZone(Creature creature);
}
