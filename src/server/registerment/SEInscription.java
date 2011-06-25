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

import java.io.IOException;
import java.util.ArrayList;

import net.*;

/**
 * Management class to a registration server parts networks
 * 
 */
public class SEInscription
{
   
   private static ArrayList<Registerment> jeuxEnregistres = new ArrayList<Registerment>();
   private Port port;
   private static final boolean debug = true;
   private ChannelTCP canal;
   
   /**
    * Constructeur
    * 
    * @param port
    * @param debug
    */
   public SEInscription(Port port)
   {
      this.port = port;
   }
   
   /**
    * Permet de lancer le serveur
    * 
    * 1) reservation du port
    * 2) attente de connexion d'un client
    *   2.1) Creation d'une tache de traitement du client
    */
   public void lancer()
   {
      try
      {
         port.reserver();
         
         System.out.println("Le serveur d'enregistrement a bien ete lance.");
         System.out.println("Attente de connexions...");
         
         while (true)
         {
             // Fonction bloquante qui attend que quelqu'un se connecte
            creerCanal();
            
            if(debug)
                System.out.println("\n+ Connexion d'un client!");
            
            (new Thread(new SEConnexion(canal))).start();
         }
      } 
      catch (IOException e)
      {
         System.err.println("Serveur d'enregistrement deja lance !");
      }
   }
   
   /**
    * Permet de creer un canal
    * 
    * Methode bloquante sur l'arrive d'un client
    */
   private void creerCanal()
   {
      try{
         canal = new ChannelTCP(port);
      } 
      catch (ChannelException ce){
        
          System.err.println("\tProbleme de connexion : " + ce.getMessage());
      }
   }
   
   /**
    * Permet d'ajouter un serveur de jeu.
    * 
    * @param e l'enregistrement (serveur de jeu)
    * @return
    */
   public static synchronized boolean ajouterEnregistrement(Registerment e)
   {
      // TODO : surcharger les contains() pour pas que ce soit les réf. qui sont
      // comparées
      if (!jeuxEnregistres.contains(e))
      {
         jeuxEnregistres.add(e);
         
         if(debug)
             System.out.println("+ Ajout d'un enregistrement, nb enr. : " 
                     + jeuxEnregistres.size());
         
         return true;
      }
      return false;
   }
   
   /**
    * Permet de supprimer un enregistrement
    * 
    * @param e l'enregistrement a supprimer
    */
   public static synchronized void enleverEnregistrement(Registerment e)
   {
      if(debug)
          System.out.println("- Suppression d'un enregistrement");
      
      jeuxEnregistres.remove(e);
   }
   
   /**
    * Permet de recuperer le nombre d'enregistrements
    * 
    * @return le nombre d'enregistrements
    */
   public static synchronized int getNombreEnregistrements()
   {
      return jeuxEnregistres.size();
   }
   
   /**
    * Permet de recuperer les jeux enregistres
    * 
    * @return les jeux enregistres
    */
   public static synchronized ArrayList<Registerment> getJeuxEnregistres()
   {
      return jeuxEnregistres;
   }
}
