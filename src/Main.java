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

import javax.swing.UIManager;

import models.utils.SoundManagement;
import views.View_ChooseLanguage;
import views.View_MenuPrincipal;

/**
 * Tower Defense游戏主类.
 * 
 * 包含入口方法.
 * 配置图形界面风格
 * 打开主程序菜单.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 27 novemenbre 2009
 * @since jdk1.6.0_16
 */
public class Main
{
   /**
    * Programme principal.
    * 
    * 设置界面风格，打开主菜单.
    * 
    * @param args 提供程序参数.
    */
   public static void main(String[] args) 
   {
       // essaye de mettre le nouveau look and feel "Nimbus" fourni par Java
       for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
           if ("Nimbus".equals(laf.getName())) 
               try 
               {
                   UIManager.setLookAndFeel(laf.getClassName());
               }
               catch (Exception e) 
               {
                   /* 
                    * On fait rien, c'est pas grave. 
                    * C'est juste le look and feel qui n'est pas installe.
                    */ 
               }    
             
      //Langue.creerFichiersDeLangue();
      
      SoundManagement.setVolumeSysteme(SoundManagement.VOLUME_PAR_DEFAUT);       
   	  //语言选择dialog
      new View_ChooseLanguage();
      
      // 创建主菜单view
      new View_MenuPrincipal();
   }
}
