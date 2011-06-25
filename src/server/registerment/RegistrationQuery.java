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

public class RegistrationQuery
{
    public final static String STOP = "{\"donnees\" :{\"code\" : "
            + CodeRegisterment.STOP + "}}";

    public final static String TEST = "{\"donnees\" :{\"code\" : "
            + CodeRegisterment.TEST + "}}";

    public final static String NOMBRE_PARTIES = "{\"donnees\" :{\"code\" : "
            + CodeRegisterment.NOMBRE_PARTIES + "}}";

    public final static String INFOS_PARTIES = "{\"donnees\" :{\"code\" : "
            + CodeRegisterment.INFOS_PARTIES + "}}";

    public final static String DESENREGISTRER = "{\"donnees\" :{\"code\" : "
            + CodeRegisterment.DESENREGISTRER + "}}";

    /**
     * Permet de generer la requete d'enregistrement
     * 
     * @return la requete
     */
    public static String getRequeteEnregistrer(String nomServeur,
            int numeroPort, int nbJoueurs, String nomTerrain, String mode)
    {
        // Création de la requete d'enregistrement
        return "{\"donnees\" :{\"code\" : " + CodeRegisterment.ENREGISTRER
                + ",\"contenu\" : " + "{" + "\"nomPartie\" :\"" + nomServeur
                + "\"," + "\"numeroPort\" :" + numeroPort + ","
                + "\"capacite\" :" + nbJoueurs + "," + "\"nomTerrain\" :\""
                + nomTerrain + "\"," + "\"mode\" :\"" + mode + "\"" + "}}}";
    }

    /**
     * Permet de generer la requete d'enregistrement
     * 
     * @return la requete
     */
    public static String getRequeteMiseAJour(int placesRestantes)
    {
        // Création de la requete de mise a jour
        return "{\"donnees\" :{\"code\" : " + CodeRegisterment.MISE_A_JOUR
                + ",\"contenu\" : { \"placesRestantes\" :" + placesRestantes+ "}}}";
    }
}
