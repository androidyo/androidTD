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

import static org.junit.Assert.*;
import org.junit.*;

/**
 * @author lazhar
 * 
 */
public class DataPackageTest
{

   byte[] b = new byte[100];
   DataPackage p = new DataPackage(10, b);

   
   /**
    * Test method for {@link net.DataPackage#Paquet(int, byte[])}.
    */
   @Test
   public void testPaquet()
   {
      b = new byte[100];
      p = new DataPackage(10, b);
      assertEquals(p.getEnTete(), 10);
      assertArrayEquals(new byte[100], b);
   }
   
   /**
    * Test method for {@link net.DataPackage#getOctets()}.
    */
   @Test
   public void testGetOctets()
   {
      b = new byte[30];
      p = new DataPackage(7, b);
      assertEquals(p.getOctets(), b);
   }
   
   
   /**
    * Test method for {@link net.DataPackage#getEnTete()}.
    */
   @Test
   public void testGetEnTete()
   {
      b = new byte[430];
      p = new DataPackage(17, b);
      assertEquals(p.getEnTete(), 17);
   }
   
}
