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

import net.*;

import org.json.*;



/**
 * 
 * @author lazhar
 *
 */
public class SEConnexion implements Runnable, CodeRegisterment {
   
   private ChannelTCP canal;
   private Registerment enregisrementCourant;
   private JSONObject messageJsonRecu;
   private JSONObject contenu;
   private int code;
   private String jsonString;
   private String messageRecu;
   
   /**
    * 
    * @param canal
    * @param SEInscription
    */
   public SEConnexion(ChannelTCP canal)
   {
      this.canal = canal;
   }
   
   /**
    * 
    */
   @Override
   public void run() {
      
          
      try 
      {   
          bouclePrincipale: 
          
          while (true)
          {
        
            messageRecu = canal.recevoirString();
            messageJsonRecu = new JSONObject(messageRecu);
            code = messageJsonRecu.getJSONObject("donnees").getInt("code");
            
            switch(code)
            {
               case STOP :
                  canal.envoyerString("{\"status\" :" + OK + "}");
                  canal.fermer();
                  break bouclePrincipale;
                  
               case TEST :
                  canal.envoyerString("{\"status\" :" + OK + "}");
                  break;
                  
               case ENREGISTRER :
                  contenu = messageJsonRecu.getJSONObject("donnees")
                                           .getJSONObject("contenu");
                  enregisrementCourant = new Registerment(
                              contenu.getString("nomPartie"),
                              canal.getIpClient(),
                              new Port(contenu.getInt("numeroPort")),
                              contenu.getInt("capacite"),
                              contenu.getString("nomTerrain"),
                              contenu.getString("mode"));
                  
                  if (SEInscription.ajouterEnregistrement(enregisrementCourant))
                  {
                      canal.envoyerString("{\"status\" :" + OK + "}");
                  }
                  else
                  {
                     canal.envoyerString("{\"status\" : " + ERREUR + "," +
                                         "\"message\" : \"Cette partie existe deja!\"}");
                  }
                  break;
                  
               case DESENREGISTRER :
                  if (enregisrementCourant != null)
                  {
                     SEInscription.enleverEnregistrement(enregisrementCourant);
                     canal.envoyerString("{\"status\" :" + OK + "}");
                     break bouclePrincipale;
                  }
                  canal.envoyerString("{\"status\" : " + ERREUR + "," +
                                      "\"message\" : \"Aucun enregistrement n'a ete fait!\"}");
                  break;
                  
               case NOMBRE_PARTIES :
                  canal.envoyerString("{\"status\" : " + OK + "," +
                                      "\"nombreParties\" : " + 
                                          SEInscription.getNombreEnregistrements() + "}");
                  break;
                  
               case INFOS_PARTIES :
                  
                  if (SEInscription.getNombreEnregistrements() > 0) 
                  {
                     jsonString = "{\"status\" : " + OK + ", \"parties\" : [";
                     for (Registerment e : SEInscription.getJeuxEnregistres())
                     {
                        jsonString = jsonString.concat("{");
                        jsonString = jsonString.concat("\"nomPartie\" : \"" + e.getNomPartie() + "\",");
                        jsonString = jsonString.concat("\"adresseIp\" : \"" + e.getAdresseIp() + "\",");
                        jsonString = jsonString.concat("\"numeroPort\" : " + e.getPort().getNumeroPort() + ",");
                        jsonString = jsonString.concat("\"capacite\" : " + e.getCapacite() + ",");
                        jsonString = jsonString.concat("\"placesRestantes\" : " + e.getPlacesRestantes() + ",");
                        jsonString = jsonString.concat("\"nomTerrain\" : \"" + e.getNomTerrain() + "\",");
                        jsonString = jsonString.concat("\"mode\" : \"" + e.getMode() + "\"");
                        jsonString = jsonString.concat("},");
                     }
                     
                     jsonString = jsonString.concat("]}");
                     
                     System.out.println("\n\n\n" + jsonString + "\n\n\n");
                     
                     canal.envoyerString(jsonString);
                     break;
                  }
                  canal.envoyerString("{\"status\" : " + ERREUR + "," +
                                      "\"message\" : \"Aucun enregistrement n'a ete fait!\"}");
                  break;
                  
               case MISE_A_JOUR :
                  if (enregisrementCourant != null)
                  {
                     canal.envoyerString("{\"status\" :" + OK + "}");
                     contenu = messageJsonRecu.getJSONObject("donnees")
                                              .getJSONObject("contenu");
                     enregisrementCourant.setPlacesRestantes(contenu.getInt("placesRestantes"));
                     break;
                  }
                  canal.envoyerString("{\"status\" : " + ERREUR + "," +
                                      "\"message\" : \"Aucun enregistrement n'a ete fait!\"}");
                  break;
                  
               default :
                  canal.envoyerString("{\"status\" : " + ERREUR +"," +
                                      "\"message\" : \"Code errone!\"}");
                  break;
            }
         }
      }
      catch (JSONException e1) 
      {
         e1.printStackTrace();
      } 
      catch (ChannelException e)
      {
         // canal erroné, on tue le client
      }
   }
}
