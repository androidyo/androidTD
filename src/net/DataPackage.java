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

package net;

/**
 * Cette classe représente un paquet qui peut être envoyé sur un canal de
 * communication. Un paquet contient un entier représentant l'en-tête (index) du
 * paquet ainsi qu'un tableau d'octets (bytes).
 * 
 * @author lazhar
 * 
 */
public class DataPackage implements java.io.Serializable
{
   
   private static final long serialVersionUID = 1L;
   private int enTete;
   private byte[] octets;
   
   /**
    * 
    * @param enTete
    * @param octets
    */
   public DataPackage(int enTete, byte[] octets)
   {
      this.enTete = enTete;
      this.octets = octets;
   }
   
   /**
    * 
    * @return
    */
   public byte[] getOctets()
   {
      return octets;
   }
   
   /**
    * @see java.io.Serializable#toString()
    */
   @Override
   public String toString()
   {
      if (octets != null)
      {
         return "Paquet avec en-tête " + enTete + " et " + octets.length + " octets.";
      }
      
      return "Paquet avec en-tête " + enTete + " et 0 octets (paquet vide).";
   }
   
   /**
    * @return the enTete
    */
   public int getEnTete()
   {
      return enTete;
   }
}
