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

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Cette classe implémente un canal de transmission sur lequel on peut envoyer
 * diverses données ainsi qu'en lire.
 * 
 * @author Lazhar Farjallah
 * 
 */
public class ChannelTCP
{
   
   // La socket qui est associée à ce côté du canal.
   private Socket socket;
   
   // Les flux In/Out sont utilisés pour lire/écrire depuis/sur le canal.
   private ObjectInputStream canalIn;
   private ObjectOutputStream canalOut;
   
   // Pour savoir si on affiche les messages de debug dans la console
   // (pratique...).
   private boolean verbeux = false;
   
   /**
    * L'appelant de ce constructeur veut attendre des connexions entrantes sur
    * le port donné en paramètre ou sinon chercher une connexion qui est déjà en
    * attente. Ce constructeur ne se termine pas tant qu'une connexion n'est pas
    * établie. Si un problème survient, une exception de type CanalException est
    * levée. Il s'agit alors d'une erreur fatale et le programme devrait se
    * terminer.
    * 
    * @param port
    *           Le port associé à ce côté du canal
    * @param afficherMessagesDebug
    *           Pour afficher ou non les messages de debug en console
    * @throws ChannelException
    *            Si un problème de connexion survient
    */
   public ChannelTCP(Port port) throws ChannelException
   {
      try
      {
         if (this.verbeux)
         {
            System.out
                  .print("     Canal: en attente de connexion sur le port <"
                        + port.getNumeroPort() + "> à l'adresse ");
            // Afficher toutes les IP candidates.
            for (NetworkInterface netint : Collections.list(NetworkInterface
                  .getNetworkInterfaces()))
            {
               for (InetAddress inetAddress : Collections.list(netint
                     .getInetAddresses()))
               {
                  if (!inetAddress.toString().contains(":")
                        && !inetAddress.toString().contains("127.0.0.1"))
                  {
                     System.out.print("[" + inetAddress.toString().substring(1)
                           + "] ou ");
                  }
               }
            }
            System.out.println("[127.0.0.1]");
         }
         
         // Accepter une connexion, c'est-à-dire soit prendre une connexion
         // en attente ou
         // alors attendre jusqu'à ce que quelqu'un se connecte. Attention,
         // cette ligne
         // est bloquante jusqu'à ce qu'une connexion soit disponible.
         socket = port.getServerSocket().accept();
         
         log("Canal: connexion établie");
         
         // Configurer les flux entrant/sortant pour la lecture et l'écriture
         // sur le
         // canal de transmission.
         configurerFlux();
      } 
      catch (Exception e){
         logErreur("Survenue pendant l'attente d'une connexion " +
         	  "entrante sur le port " + port.getNumeroPort());
         throw new ChannelException(e);
      }
   }
   
   /**
    * Ce constructeur permet à son appelant de se connecter à l'adresse IP
    * donnée et sur le port donnés en paramètres. Cela attendra que l'autre côté
    * du canal réponde. Si la connexion est refusée, une exception
    * java.net.ConnectException est levée. Ce n'est cependant pas une erreur
    * fatale, et le programme peut tout aussi bien continuer si cette denière
    * est catchée. Si la connexion ne peut être établie pour une raison
    * quelconque, une exception de type CanalException est levée. Cette fois, il
    * s'agit d'une erreur fatale et le programme devrait être interrompu.
    * 
    * @param adresseIp
    *           L'adresse IP vers laquelle on veut créer un canal
    * @param numeroPort
    *           Le numéro de port de l'adresse IP
    * @throws ConnectException
    *            Si la connexion est refusée à cette IP + Port
    * @throws ChannelException
    *            Si une erreur de connexion survient
    */
   public ChannelTCP(String adresseIp, int numeroPort)
         throws ConnectException, ChannelException
   {
      try
      {
         
         log("Canal: tentative de connexion à l'adresse "
                        + adresseIp + " sur le port " + numeroPort);
        
         
         // Créée une connexion, c'est-à-dire une socket vers l'IP et le port
         // donnés et
         // attend qu'il y ait une réponse.
         socket = new Socket(adresseIp, numeroPort);
         
         log("Canal: connexion établie");
         
         
         // Configurer les flux entrant/sortant pour la lecture et l'écriture
         // sur le
         // canal de transmission.
         configurerFlux();
      } catch (ConnectException e)
      {
         // La connexion a été refusée. Pas considéré comme une erreur
         // fatale.
         log("Canal: connexion refusée");
         
         throw e;
      } catch (Exception e)
      {
         // Un problème de connexion est survenu. Considéré comme une erreur
         // fatale.
         logErreur("Survenue lors de la tentative " +
              "de connexion à l'adresse " + adresseIp + " sur le port " + numeroPort);
         throw new ChannelException(e);
      }
   }
   
   /**
    * Cette méthode est appelée quand une socket a été établie entre les 2
    * extrémités connectées. Elle configure les flux d'entrée/sortie pour le
    * canal courant. Si un problème est rencontré, une exception de type
    * CanalException est levée.
    * 
    * @throws ChannelException
    *            Si un problème de flux survient.
    */
   private void configurerFlux() throws ChannelException
   {
      try
      {
         // Récupérer les flux déjà associés à la socket créée.
         InputStream generalIn = socket.getInputStream();
         OutputStream generalOut = socket.getOutputStream();
         
         // Nous voulons écrire toutes sortes d'informations sur le canal. Le
         // filtre le
         // plus approprié est donc Object.
         // On crée d'abord le flux de sortie puis on le flush() avant de
         // créer le flux
         // d'entrée, sinon le constructeur du flux d'entrée peut bloquer
         // (voir la doc
         // de l'API pour plus d'infos). Cela ne convient qu'aux flux
         // d'objets, et non
         // aux flux de données (Data Streams).
         // Il ne faut enfin pas oublier d'envoyer des objets sur le flux de
         // sortie qui
         // sont sérialisés (ils doivent implémenter java.io.Serializable).
         canalOut = new ObjectOutputStream(generalOut);
         canalOut.flush();
         canalIn = new ObjectInputStream(generalIn);
      } catch (Exception e)
      {
         logErreur("Survenue pendant la configuration des flux In/Out");
         throw new ChannelException(e);
      }
   }
   
   /**
    * Cette méthode envoie un message sur le canal de transmission sous forme de
    * String.
    * 
    * @param message
    *           Le String à envoyer.
    * @throws ChannelException
    *            Si un problème de transmission survient.
    */
   public void envoyerString(String message) throws ChannelException
   {
      log("Canal: envoi du String " + message);
      
      try
      {
         canalOut.writeUTF(message);
         // Ne pas oublier de vider le flux de sortie après avoir écrit
         // dessus!
         canalOut.flush();
      } catch (Exception e)
      {
         logErreur("Survenue pendant l'envoi du String");
         throw new ChannelException(e);
      }
   }
   
   /**
    * Cette méthode permet d'attendre de recevoir un message de type String
    * venant du canal de transmission.
    * 
    * @return
    * @throws ChannelException
    */
   public String recevoirString() throws ChannelException
   {
      String message = null;
      
      log("Canal: en attente d'un String...");
      
      try
      {
         message = canalIn.readUTF();
      }
      catch (Exception e)
      {
    	 // On laisse le choix d'affichage au programme client dans la gestion des
    	 // exceptions associée.
         throw new ChannelException(e);
      }
      
      log("Canal: réception du String " + message);
      
      return message;
   }
   
   /**
    * Cette méthode envoie un message sur le canal de transmission sous forme de
    * int.
    * 
    * @param i
    * @throws ChannelException
    */
   public void envoyerInt(int i) throws ChannelException
   {
      log("Canal: envoi de l'int " + i);
      
      try
      {
         canalOut.writeInt(i);
         // Ne pas oublier de vider le flux de sortie après avoir écrit
         // dessus!
         canalOut.flush();
      } 
      catch (Exception e)
      {
         logErreur("Survenue pendant l'envoi de l'int");
         throw new ChannelException(e);
      }
   }

   /**
    * Cette méthode permet d'attendre de recevoir un message de type int venant
    * du canal de transmission.
    * 
    * @return
    * @throws ChannelException
    */
   public int recevoirInt() throws ChannelException
   {
      int intRecu = 0;
      
      log("Canal: en attente d'un int...");
      
      try
      {
         intRecu = canalIn.readInt();
      } 
      catch (Exception e)
      {
         logErreur("Survenue pendant l'attente de réception d'un int");
         throw new ChannelException(e);
      }
      
      log("Canal: réception de l'int " + intRecu);
      
      return intRecu;
   }
   
   /**
    * Cette méthode envoie un message sur le canal de transmission sous forme de
    * double.
    * 
    * @param d
    * @throws ChannelException
    */
   public void envoyerDouble(double d) throws ChannelException
   {
      log("Canal: envoi du double " + d);
      
      try
      {
         canalOut.writeDouble(d);
         // Ne pas oublier de vider le flux de sortie après avoir écrit
         // dessus!
         canalOut.flush();
      } catch (Exception e)
      {
         logErreur("Survenue pendant l'envoi du double");
         throw new ChannelException(e);
      }
   }
   
   /**
    * Cette méthode permet d'attendre de recevoir un message de type double
    * venant du canal de transmission.
    * 
    * @return
    * @throws ChannelException
    */
   public double recevoirDouble() throws ChannelException
   {
      double doubleRecu = 0.0;
      
      log("Canal: en attente d'un double...");
      
      try
      {
         doubleRecu = canalIn.readDouble();
      } catch (Exception e)
      {
         logErreur("Survenue pendant l'attente de réception d'un double");
         throw new ChannelException(e);
      }
      
      log("Canal: réception du double " + doubleRecu);
      
      return doubleRecu;
   }
   
   /**
    * Cette méthode envoie un message sur le canal de transmission sous forme de
    * tableau de bytes.
    * 
    * @param b
    * @throws ChannelException
    */
   public void envoyerBytes(byte[] b) throws ChannelException
   {
      log("Canal: envoi de " + b.length + " bytes");
      
      try
      {
         canalOut.write(b);
         // Ne pas oublier de vider le flux de sortie après avoir écrit
         // dessus!
         canalOut.flush();
      } catch (Exception e)
      {
         logErreur("Survenue pendant l'envoi des bytes");
         throw new ChannelException(e);
      }
   }
   
   /**
    * Cette méthode permet d'attendre de recevoir un message de type tableau de
    * bytes venant du canal de transmission.
    * 
    * @param size
    * @return
    * @throws ChannelException
    */
   public byte[] recevoirBytes(int size) throws ChannelException
   {
      byte[] bytesRecus = new byte[size];
      
      log("Canal: en attente de " + size + " bytes ...");
      
      try
      {
         canalIn.readFully(bytesRecus);
      } catch (Exception e)
      {
         logErreur("Survenue pendant l'attente de réception des bytes");
         throw new ChannelException(e);
      }

      log("Canal: réception de " + bytesRecus.length+ " bytes");
   
      return bytesRecus;
   }
   
   /**
    * Cette méthode envoie un message sur le canal de transmission sous forme de
    * Paquet (objet de la classe Paquet).
    * 
    * @param p
    * @throws ChannelException
    */
   public void envoyerPaquet(DataPackage p) throws ChannelException
   {
      log("Canal: envoi du paquet " + p);

      try
      {
         canalOut.writeObject(p);
         // Ne pas oublier de vider le flux de sortie après avoir écrit
         // dessus!
         canalOut.flush();
      }

      catch (Exception e)
      {
         logErreur("Survenue pendant l'envoi du paquet");
         throw new ChannelException(e);
      }
   }
   
   /**
    * Cette méthode permet d'attendre de recevoir un message de type Paquet
    * (objet de la classe Paquet) venant du canal de transmission.
    * 
    * @return
    * @throws ChannelException
    */
   public DataPackage recevoirPaquet() throws ChannelException
   {
      DataPackage paquetRecu = null;

      log("Canal: en attente d'un paquet ...");
      
      try
      {
         paquetRecu = (DataPackage) (canalIn.readObject());
         
         if (paquetRecu != null)
         {
             log("Canal: réception du paquet "
                  + paquetRecu
                  + " contenant "
                  + (paquetRecu.getOctets() == null ? 0 : paquetRecu
                        .getOctets().length) + " bytes");
         }
      } catch (Exception e)
      {
         logErreur("Survenue pendant l'attente de réception d'un paquet");
         throw new ChannelException(e);
      }
      return paquetRecu;
   }
   
   public String getIpClient()
   {
      return socket.getInetAddress().toString().substring(1);
   }

   /**
    * Cette méthode permet de fermer correctement le canal en fermant les flux
    * d'entrée/ sortie ainsi que les deux sockets de chaque côté du canal.
    * 
    * @throws ChannelException
    *            Si un problème de fermeture des flux/Sockets survient.
    */
   public void fermer() throws ChannelException
   {
      try
      {
         canalIn.close();
         canalOut.close();
         socket.close();
      } 
      catch (Exception e)
      {
         logErreur("Survenue pendant la fermeture du canal");
         throw new ChannelException(e);
      }
   }

   /**
    * @return the socket
    */
   public Socket getSocket()
   {
      return socket;
   }

   /**
    * @return the canalIn
    */
   public ObjectInputStream getCanalIn()
   {
      return canalIn;
   }

   /**
    * @return the canalOut
    */
   public ObjectOutputStream getCanalOut()
   {
      return canalOut;
   }

   /**
    * @return the afficherMessagesDebug
    */
   public boolean isAfficherMessagesDebug()
   {
      return verbeux;
   }
   
   public void log(String msg)
   {
       if(verbeux)
           System.out.println("    "+msg);
   }
   
   /**
    * Permet d'afficher des message log d'erreur
    * 
    * @param msg le message
    */
   private void logErreur(String msg)
   {
       System.err.println("[CANAL][ERREUR]" + msg);
   }
}
