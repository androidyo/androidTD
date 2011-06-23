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

import java.io.IOException;

import org.junit.*;

/**
 * @author lazhar
 * 
 */
public class PortTest
{
   
   Port port;
   
   /**
    * Test method for {@link net.Port#Port(int)}.
    */
   @Test
   public void testPortInt()
   {
      port = new Port(1234);
      assertNotNull(port);
      assertEquals(port.getNumeroPort(), 1234);
   }
   
   /**
    * Test method for {@link net.Port#Port(java.lang.String)}.
    */
   @Test
   public void testPortString()
   {
      port = new Port("1234");
      assertNotNull(port);
      assertEquals(port.getNumeroPort(), 1234);
   }
   
   /**
    * Test method for {@link net.Port#getNumeroPort()}.
    */
   @Test
   public void testGetNumeroPort()
   {
      port = new Port(54321);
      assertEquals(port.getNumeroPort(), 54321);
   }
   
   /**
    * Test method for {@link net.Port#reserver()}.
    */
   @Test
   public void testReserver()
   {
      port = new Port(1234);
      
      try{
          port.reserver();
      } 
      catch (IOException e){
          e.printStackTrace();
      }
      
      assertTrue(port.getServerSocket().isBound());
      assertNotNull(port.getServerSocket());
      port.liberer();
   }
   
   /**
    * Test method for {@link net.Port#liberer()}.
    */
   @Test
   public void testLiberer()
   {
      port = new Port(1234);
      try
      {
        port.reserver();
      } 
      catch (IOException e){
        e.printStackTrace();
      }
      port.liberer();
      assertTrue(port.getServerSocket().isClosed());
   }
   
}
