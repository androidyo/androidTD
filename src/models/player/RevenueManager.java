/*
  Copyright (C) 2010 Aurelien Da Campo

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

package models.player;

import models.game.Game;
import models.player.Player;

/**
 * 
 * 玩家收入管理类
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 */
public class RevenueManager implements Runnable
{
    public static final double POURCENTAGE_NB_PIECES_OR_CREATURE = 0.01;
    private static final long TEMPS_REVENU_CREATURE = 1000; // ms
    private boolean gestionEnCours;
    private boolean enPause = false;
    private Object pause = new Object();
    private Game jeu;

    /**
     * Constructeur du gestionnaire des creatures
     */
    public RevenueManager(Game jeu)
    {
        this.jeu = jeu;
    }
    
    /**
     * Permet de demarrer la gestion
     */
    public void demarrer()
    {
        Thread thread = new Thread(this);
        thread.start();
    }
 
    @Override
    public void run()
    {
        gestionEnCours = true;
        
        while(gestionEnCours)
        {
            // donne de l'argent
            // pas de problème de concurrence car c'est une copie.
            for(Player joueur : jeu.getJoueurs())
                if(!joueur.aPerdu() && !joueur.estHorsJeu())
                    joueur.donnerRevenu(TEMPS_REVENU_CREATURE);
              
            // gestion de la pause
            try
            {
                synchronized (pause)
                {
                    if(enPause)
                        pause.wait();
                } 
                
                Thread.sleep(TEMPS_REVENU_CREATURE);
            } 
            catch (InterruptedException e){
                 e.printStackTrace();
            }
        }
    }
    
    /**
     * Permet d'arreter toutes les creatures
     */
    public void arreterCreatures()
    {
        gestionEnCours = false;
    }
    
    /**
     * Permet de mettre les créatures en pause.
     */
    public void mettreEnPause()
    {
        enPause = true;
    }
    
    /**
     * Permet de sortir les créatures de la pause.
     */
    public void sortirDeLaPause()
    { 
        synchronized (pause)
        {
            enPause = false;
            pause.notify(); 
        }
    }
}
