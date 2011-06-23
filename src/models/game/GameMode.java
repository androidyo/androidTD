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

/**
 * 游戏模式的常量 存储类
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 */
public class GameMode
{
    public static final int MODE_SOLO = 0;
    public static final int MODE_VERSUS = 1;
    public static final int MODE_COOP = 2;
    
    public static String getNomMode(int mode)
    {
        switch(mode)
        {
            case 0 : return "Solo";
            case 1 : return "Versus";
            case 2 : return "Coop";
        }
        
        return "Inconnu";
    }
    
    public static int getMode(String mode)
    {
        if(mode.equals("Solo"))
            return MODE_SOLO;
        else if(mode.equals("Versus"))
            return MODE_VERSUS;
        else if(mode.equals("Coop"))
            return MODE_COOP;
        else
            return -1;
    }
    
    
    
}
