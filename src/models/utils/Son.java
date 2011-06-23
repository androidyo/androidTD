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

package models.utils;

import java.io.*;
import java.util.*;
import static org.jouvieje.fmodex.defines.FMOD_INITFLAGS.*;
import static org.jouvieje.fmodex.defines.FMOD_MODE.*;
import static org.jouvieje.fmodex.defines.VERSIONS.*;
import static org.jouvieje.fmodex.enumerations.FMOD_CHANNELINDEX.*;
import static org.jouvieje.fmodex.utils.BufferUtils.*;
import static org.jouvieje.fmodex.utils.SizeOfPrimitive.SIZEOF_INT;

import java.nio.ByteBuffer;
import org.jouvieje.fmodex.*;
import org.jouvieje.fmodex.System;
import org.jouvieje.fmodex.enumerations.*;
import org.jouvieje.fmodex.structures.*;

import static java.lang.System.out;

/**
 * Fichier : Son.java
 * <p>
 * Encodage : UTF-8
 * <p>
 * Cette classe interne privee permet de representer du son (flux).
 * <p>
 * Remarques : <br>
 * Cette classe ne permet plus de lire plusieurs fichiers son en concurrence.
 * Si on a besoin de ça, par exemple pour introduire les musiques d'effets, il faudra
 * développer une autre classe utilisant la librairie JLayer (jl1.0.1.jar), mais avec
 * le risque d'avoir des bugs comme auparavant.
 * 
 * @author Lazhar Farjallah
 * @version 17 dec. 2009
 * @since jdk1.6.0_16
 */
public class Son {

   // Le nombre de fois qu'on veut repeter le son.
   private int nombreRepetitions = 1;
   // Le fichier ou est stocke la musique a lire.
   private File fichier;
   // Pour savoir si le son actuel est à l'arret.
   private boolean arret = false;
   // Ecouteur de son
   private ArrayList<SoundListener> ecouteursDeSon = new ArrayList<SoundListener>();
   
   private System system;
   private Sound sound;
   private ByteBuffer soundBuffer;
   private FMOD_CREATESOUNDEXINFO exinfo;
   private Channel channel;
   private FMOD_RESULT result;
   private ByteBuffer buffer;

   /**
    * Ce constructeur permet de creer un objet Son en fonction d'un fichier
    * donne ainsi que d'un nombre de repetitions donne.
    * 
    * @param fichier
    *           Le fichier a lire.
    * @param repetitions
    *           Le nombre de fois que la musique doit etre jouee. Si cette
    *           valeur vaut 0, la musique est repetee a l'infini.
    */
   public Son(File fichier) {
      this.fichier = fichier;
      try {
         init();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Cette methode permet de lire le son une fois.
    */
   public void lire() {
      try {
         play();
      }
      catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * Cette methode permet de lire la musique courante, en fonction du nombre de
    * repetitions donne. Si <tt>nombreRepetitions</tt> vaut 0, on repete la
    * musique $ l'infini, sinon on repete la musique <tt>nombreRepetitions</tt>
    * fois.
    * 
    * @param nombreRepetitions
    *           Le nombre de fois que la musique doit etre jouee. Si ce
    *           parametre vaut 0, la musique est jouee a l'infini (ne s'arrete
    *           jamais).
    */
   public void lire(int nombreRepetitions) {
      this.nombreRepetitions = nombreRepetitions == 0 ? Integer.MAX_VALUE
            : nombreRepetitions;
      try {
         play();
      }
      catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * Cette methode permet d'arreter toutes les occurrences de cette musique qui
    * sont en train d'etre lues. En bref, elle stoppe tous les threads en train
    * de lire cette musique.
    */
   public void arreter() {
      // On passe en mode arret.
      arret = true;
      
      channel.stop();

      // informe les ecouteurs que le son est termine
      for (SoundListener ecouteurDeSon : ecouteursDeSon) {
         ecouteurDeSon.estTerminee(this);
      }
   }

   /**
    * Cette methode permet de determiner si toutes les occurrences de lecture de
    * la musique courante sont terminees ou non.
    * 
    * @return True si toutes les occurrences sont terminees, false sinon.
    */
   public boolean estTerminee() {
      return arret;
   }

   /**
    * Permet d'ajouter un ecouteur de son
    * 
    * @param eds
    *           l'ecouteur de son
    */
   public void ajouterEcouteurDeSon(SoundListener eds) {
      ecouteursDeSon.add(eds);
   }

   /**
    * Permet de recuperer le fichier du son
    * 
    * @return le fichier du son
    */
   public File getFichier() {
      return fichier;
   }
   
   /**
    * Play a sound for the example
    */
   private void play() throws Exception {
      system = new System();
      sound = new Sound();
      soundBuffer = Media.loadMediaIntoMemory(fichier.toString());
      exinfo = FMOD_CREATESOUNDEXINFO.allocate();
      channel = new Channel();
      result = FmodEx.System_Create(system);
      buffer = newByteBuffer(SIZEOF_INT);
      
      arret = false;
      result = system.getVersion(buffer.asIntBuffer());
      result = system.init(32, FMOD_INIT_NORMAL, null);
      exinfo.setLength(soundBuffer.capacity());
      result = system.createSound(soundBuffer, FMOD_HARDWARE | FMOD_OPENMEMORY, exinfo, sound);
      soundBuffer = null;
      exinfo.release();
      
      // Infinite loop mode
      sound.setMode(FMOD_LOOP_NORMAL);
      sound.setLoopCount(nombreRepetitions - 1);
      // Play sound
      result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
      // To change the volume
      channel.setVolume(1.0f);

      // Loop that checks if an error occurs
      /*
      do {
         try {
            system.update();
            if (errorCheck(result)) {
               out.printf("FMOD error! (%d) %s\n", result.asInt(),
                     FmodEx.FMOD_ErrorString(result));
            }
            Thread.sleep(1000);
         }
         catch (InterruptedException e) {
            e.printStackTrace();
         }
      } while (true);
      */
   }

   /**
    * Checks if any error occurs
    * @param result
    * @return
    */
   private static boolean errorCheck(FMOD_RESULT result) {
      return result != FMOD_RESULT.FMOD_OK;
   }

   /**
    * 
    * @throws Exception
    */
   private static void init() throws Exception {
      // NativeFmodEx libraries initialization
      Init.loadLibraries();
      // Checking NativeFmodEx version
      if (NATIVEFMODEX_LIBRARY_VERSION != NATIVEFMODEX_JAR_VERSION) {
         throw new Exception("Error! NativeFmodEx library version is different to jar version!\n");
      }
   }

}
