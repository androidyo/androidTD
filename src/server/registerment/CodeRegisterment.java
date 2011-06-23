/*
  Copyright (C) 2010 Lazhar Farjallah
  
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

package server.registerment;

public interface CodeRegisterment
{
   // Codes :发送到服务器
   public final int STOP           = 100;
   public final int TEST           = 101;
   public final int ENREGISTRER    = 102;
   public final int DESENREGISTRER = 103;
   public final int NOMBRE_PARTIES = 104;
   public final int INFOS_PARTIES  = 105;
   public final int MISE_A_JOUR    = 106;
   
   // Codes :服务器响应
   public final int OK             = 200;
   public final int ERREUR         = 201;
   
}
