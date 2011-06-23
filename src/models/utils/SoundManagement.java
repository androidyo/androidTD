/*
  Copyright (C) 2010 Aurelien Da Campo, Lazhar Farjallah, 
  Pierre-Dominique Putallaz

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

package models.utils;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Port;

/**
 * Cette classe permet de gerer tous les sons de l'application.
 * <p>
 * Celle-ci est entierement statique ce qui permet de gerer les sons depuis n'importe
 * ou dans la structure.
 * <p>
 * Remarques : <br>
 * Les sons sont "threades", c'est-a-dire qu'on peut sans autre lire plusieurs
 * fois le meme son dans le code.
 * 
 * @author Pierre-Dominique Putallaz
 * @author Aurelien Da Campo
 * @author Lazhar Farjallah
 * @version 17 dec. 2009
 * @since jdk1.6.0_16
 */
public class SoundManagement
{
    // liste des sons
    private static Vector<Son> sons = new Vector<Son>();
    // controle d'entree pour le volume.
    private static Control ctrlIn;
    // ecouteur de son de la classe
    private static SoundListener eds;
    // Pour les ports de sortie audio.
    private static Port lineOut;
    
    public static final int VOLUME_PAR_DEFAUT = 20;
    
    static
    {
       /**
        * On commence par initialiser une fois pour toutes les controles sonores.
        */
       try {
          // Le systeme possede une sortie LINE_OUT.
          if (AudioSystem.isLineSupported(Port.Info.LINE_OUT))
          {
              lineOut = (Port) AudioSystem.getLine(Port.Info.LINE_OUT);
              // On ouvre ce port.
              lineOut.open();
          }
          // Le systeme possede une sortie HEADPHONE.
          else if (AudioSystem.isLineSupported(Port.Info.HEADPHONE))
          {
              lineOut = (Port) AudioSystem.getLine(Port.Info.HEADPHONE);
              // On ouvre ce port.
              lineOut.open();
          }
          // Le systeme possede une sortie SPEAKER.
          else if (AudioSystem.isLineSupported(Port.Info.SPEAKER))
          {
              lineOut = (Port) AudioSystem.getLine(Port.Info.SPEAKER);
              // On ouvre ce port.
              lineOut.open();
          }
          // Le systeme ne possede pas de sortie audio (triste!).
          else
          {
             // FIXME: Pas exactement...
             // System.out.println("Impossible d'avoir une sortie audio!");
          }
       }
       catch (Exception erreur) // Une erreur liee au volume est survenue.
       {
           erreur.printStackTrace();
       }
       
        // une fois le son terminee, on le supprime de la collection des sons
        eds = new SoundListener(){
            @Override
            public void estTerminee(Son son)
            {
                sons.remove(son);
            }
        };
    }
    
    /**
     * Permet d'ajouter un son
     * 
     * @param son le son a ajouter
     */
    public static void ajouterSon(Son son)
    {
        son.ajouterEcouteurDeSon(eds); // ajout de l'ecouteur
        sons.add(son);
    }
    
    /**
     * Arrete tous les sons actuellement en lecture
     */
    public static void arreterTousLesSons()
    {
        synchronized (sons)
        {
            Enumeration<Son> eSons = sons.elements();
            
            Son son;
            while(eSons.hasMoreElements())
            {
                son = eSons.nextElement();
                if(!son.estTerminee())
                    son.arreter();
            }
     
            sons.clear();
        }
    }
    
    /**
     * Arrete tous les sons du meme fichier actuellement en lecture
     * 
     * @param fichier le fichier des sons a arreter
     */
    public static void arreterTousLesSons(File fichier)
    {
        synchronized (sons)
        {
            Iterator<Son> iSons = sons.iterator();
            Son son;
            
            /* 
             * TODO lève une java.util.ConcurrentModificationException 
             * lors du lancement du jeu, je sais pas pourquoi...
             */
            try 
            {
                while(iSons.hasNext())
                {
                    son = iSons.next();
                    
                    if(son.getFichier() == fichier)
                    {
                        son.arreter();
                        
                        
                            iSons.remove();
                    }
                   
                }
             }
            catch(java.util.ConcurrentModificationException cme)
            {}
        }
    }
    
    /**
     * Permet de recuperer le nombre de sons actuellement en lecture
     *  
     * @return le nombre de sons actuellement en lecture
     */
    public static int getNbSonsEnLecture()
    {
        int nbSons = 0;
        
        for(Son son : sons)
            if(!son.estTerminee())
                nbSons++;
        
        return nbSons;
    }
    
    /**
     * Permet de recuper le nombre de sons du meme fichier actuellement en lecture
     * 
     * @param fichier le fichier des sons compter
     * 
     * @return le nombre de sons du meme fichier actuellement en lecture
     */
    public static int getNbSonsEnLecture(File fichier)
    {
        int nbSons = 0;
    
        synchronized (sons)
        {
            for(Son son : sons)
                if(son.getFichier() == fichier && !son.estTerminee())
                    nbSons++;
        
        }
        return nbSons;
    }
    
    /**
     * Cette methode permet de savoir (en pourcentage) quel est le volume actuel
     * du systeme.
     * 
     * @return Le pourcentage du volume du systeme actuel.
     */
    public static int getVolumeSysteme()
    {
       if (lineOut != null) {
          // On recupere le controle de l'intensite du volume.
          ctrlIn = lineOut.getControl(FloatControl.Type.VOLUME);
          
          return (int) (100 * 
                  ((FloatControl) ctrlIn).getValue() / 
                  ((FloatControl) ctrlIn).getMaximum());
       }
       return 0;
    }
    
    /**
     * Permet de savoir si le volume est en mode muet.
     * 
     * @return True si le volume systeme est en mode muet.
     */
    public static boolean isVolumeMute() {
       if (lineOut != null) {
          // On recupere le controle mute du volume.
          ctrlIn = lineOut.getControl(BooleanControl.Type.MUTE);
          return ((BooleanControl) ctrlIn).getValue();
       }
       return true;
    }
    
    /**
     * Permet de mettre le volume systeme en mode muet.
     * 
     * @param volumeMute Indiquer si le volume systeme doit etre muet.
     */
    public static void setVolumeMute(boolean volumeMute) {
       if (lineOut != null) {
          // On recupere le controle mute du volume.
          ctrlIn = lineOut.getControl(BooleanControl.Type.MUTE);
          // On le met a mute.
          ((BooleanControl) ctrlIn).setValue(volumeMute);
       }
    }

    /**
     * Cette methode permet de regler le volume du systeme en fonction d'un
     * pourcentage donne.
     * 
     * @param volumePourcent Le pourcentage du volume qu'on veut appliquer.
     */
    public static void setVolumeSysteme(int volumePourcent)
    {
       if (lineOut != null) {
          // On recupere le controle de l'intensite du volume.
          ctrlIn = lineOut.getControl(FloatControl.Type.VOLUME);
          // On change le volume du systeme avec celui donne en parametre.
          ((FloatControl) ctrlIn).setValue(volumePourcent / 100.0f);
       }
    }
}
