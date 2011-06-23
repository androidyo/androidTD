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

import java.net.ServerSocket;
import java.io.IOException;

/**
 * Cette classe représente un port sur l'ordinateur depuis lequel le programme
 * est lancé. Si un port est réservé, les connexions peuvent être faites dessus.
 * Un port ne peut être réservé que par un processus à la fois (un port par
 * processus), il ne faut donc pas oublier de libérer les ports lorsqu'on ne les
 * utilise plus.
 * 
 * @author lazhar
 * 
 */
public class Port {
   
   // Le numéro de port à réserver.
   private int numeroPort;
   // Le socket à associer avec un port.
   private ServerSocket serverSocket;
   
   /**
    * Constructeur d'un Port en fonction du numéro de port souhaité.
    * 
    * @param numeroPort
    *           Le numéro de port souhaité.
    */
   public Port(int numeroPort) {
      this.numeroPort = numeroPort;
   }
   
   /**
    * 
    * @param numeroPort
    * @throws RuntimeException
    */
   public Port(String numeroPort) throws RuntimeException {
      if (numeroPort != null)
         this.numeroPort = Integer.valueOf(numeroPort);
      else
         throw new RuntimeException("Aucun numéro de port spécifié.");
   }
   
   /**
    * 
    * @return
    */
   public int getNumeroPort() {
      return numeroPort;
   }
   
   /**
    * 
    * @return
    */
   public ServerSocket getServerSocket() {
      return serverSocket;
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "Port [numeroPort=" + numeroPort + ", serverSocket="
            + serverSocket + "]";
   }
   
   /**
    * Permet de réserver un port en l'associant avec un socket.
 * @throws IOException 
    */
   public void reserver() throws IOException 
   {
      // Demander le port et l'associer avec un socket.
      serverSocket = new ServerSocket(numeroPort);
   }
   
   /**
    * Permet de libérer un port réservé préalablement afin qu'il puisse à
    * nouveau être réservé par d'autre processus.
    */
   public void liberer() {
      // Libérer le port en fermant son socket associé. Cela signifie que le
      // port peut
      // maintenant être réservé par un autre processus.
      try {
         serverSocket.close();
      } catch (IOException e) {
         System.out
               .println("Une exception a été levée lors la libération du port "
                     + numeroPort);
         e.printStackTrace();
         throw new RuntimeException();
      }
   }
}
