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

import java.io.Serializable;
import java.util.Date;

/**
 * Fichier : Score.java
 * <p>
 * Encodage : UTF-8
 * <p>
 * Cette classe permet de representer le score d'un joueur en fonction de son nom, de
 * son score obtenu ainsi que de la date d'obtention du score.
 * <p>
 * Remarques : <br>
 * Cette classe implemente deux interfaces : {@link Serializable} et
 * {@link Comparable}
 * 
 * @author Lazhar Farjallah
 * @version 16 dec. 2009
 * @since jdk1.6.0_16
 */
public class Score implements Serializable, Comparable<Score>
{
   
   // La version de serialisation.
   private static final long serialVersionUID = 1L;
   
   // La valeur du score du joueur.
   private int valeur;
   // Le nom du joueur.
   private String nomJoueur;
   // La date a laquelle le score est cree.
   private Date date;
   // La duree de la parties en secondes
   private long dureePartie;
   
   
   /**
    * Ce constructeur permet de creer un objet de type Score en fonction d'un nom de
    * joueur ainsi qu'une valeur de score obtenu.
    * 
    * @param nomJoueur
    *        Le nom du joueur qui a obtenu un score.
    * @param valeur
    *        La valeur du score obtenue par le joueur.
    */
   public Score (String nomJoueur, int valeur, long dureePartie)
   {
      this.valeur = valeur;
      this.nomJoueur = nomJoueur;
      this.dureePartie = dureePartie;
    
      // new Date() cree automatiquement la date courante.
      date = new Date();
   }
   
   /**
    * Ce constructeur permet de creer un score en fonction d'un autre score donne.
    * 
    * @param score
    *        Le score a partir duquel on veut creer un nouvel objet Score.
    */
   public Score (Score score)
   {
      valeur        = score.valeur;
      nomJoueur     = score.nomJoueur;
      dureePartie   = score.dureePartie;
      date          = score.date;
   }
   
   public Score()
   {
       nomJoueur = "";
   }

/**
    * Getter pour le champ <tt>valeur</tt>
    * 
    * @return La valeur du champ <tt>valeur</tt>
    */
   public int getValeur ()
   {
      return valeur;
   }
   
   /**
    * Getter pour le champ <tt>nomJoueur</tt>
    * 
    * @return La valeur du champ <tt>nomJoueur</tt>
    */
   public String getNomJoueur ()
   {
      return nomJoueur;
   }
   
   /**
    * Getter pour le champ <tt>dureePartie</tt>
    * 
    * @return La valeur du champ <tt>dureePartie</tt>
    */
   public long getDureePartie()
   {
      return dureePartie;
   }
   
   /**
    * Getter pour le champ <tt>date</tt>
    * 
    * @return La valeur du champ <tt>date</tt>
    */
   public Date getDate()
   {
      return new Date(date.getTime());
   }
   
   /**
    * Cette methode permet d'appliquer une politique de comparaison entre objets de
    * type Score. Cette politique sera ensuite appliquee lors du tri d'une collection
    * quelconque contenant des objets de type Score. Ici, on applique la politique
    * suivante : on veut qu'un score plus eleve qu'un autre soit "plus grand" que
    * lui, c'est-a-dire qu'un score plus eleve est classe avant un score moins eleve.
    * En bref, on applique ici une politique de tri en ordre decroissant.
    * 
    * @see Comparable#compareTo(Object)
    */
   @Override
   public int compareTo (Score aComparer)
   {
      // Le score a comparer est plus eleve que le score courant.
      if (aComparer.valeur > valeur)
         return 1;
      // Le score a comparer equivaut le score courant.
      else if (aComparer.valeur == valeur)
         return 0;
      // Le score a comparer est plus petit que le score courant.
      else
         return -1;
   }
   
   /**
    * @see Object#toString()
    */
   @Override
   public String toString ()
   {
      return nomJoueur + " - " + valeur + " - " + date;
   }
   
   /*
   // Affiche les scores parfaits (sans erreur)
   int score = 0;  
      for(int i=1;i<=50;i++)
      {
          VagueDeCreatures v = VagueDeCreatures.genererVagueStandard(i);
          score += v.getNbCreatures() * v.getNouvelleCreature().getNbPiecesDOr();
          System.out.println(i+" "+score);
      }
   */
   
   /**
    * Permet de recuperer le nombre d'étoiles relative au score
    * Limiter par MAX_ETOILES
    * 
    * @return le nombre d'étoiles
    */
   public int getNbEtoiles()
   {
       if(valeur >= 3349)
           return 5;
       if(valeur >= 2215)
           return 4;
       else if(valeur >= 1295)
           return 3;
       else if(valeur >= 624)
           return 2;
       else if(valeur >= 205)
           return 1;
       else
           return 0;
   }

    /**
    * Permet de modifier la valeur du score
    * 
    * @param score la nouvelle valeur
    */
    public void setValeur(int score)
    {
        valeur = score;   
    }
    
    /**
     * Permet de récupérer le nombre de secondes de la partie
     * 
     * @return le nombre de secondes de la partie
     */
    private int getSecondes()
    {
        return (int) (dureePartie) % 60;
    }
    
    /**
     * Permet de récupérer le nombre de minutes de la partie
     * 
     * @return le nombre de minutes de la partie
     */
    private int getMinutes()
    {
        return (int) (dureePartie / 60) % 60;
    }
    
    /**
     * Permet de récupérer le nombre d'heures de la partie
     * 
     * @return le nombre d'heures de la partie
     */
    private int getHours()
    {
        return (int) (dureePartie / 3600) % 24;
    }
    
    /**
     * Permet de récupérer la durée de la partie sous la forme H:M:S
     * 
     * @return la durée de la partie sous la forme H:M:S
     */
    public String getHMS()
    {
        return String.format("%02d:%02d:%02d", getHours(), 
                                               getMinutes(), 
                                               getSecondes());
    }
}
