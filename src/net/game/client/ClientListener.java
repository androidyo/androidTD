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

package net.game.client;

import models.player.Team;
import models.player.Player;

/**
 * Interface permettant de mettre en oeuvre le pattern Observable/ Observé pour la
 * classe ClientJeu.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @see ClientJeu
 */
public interface ClientListener
{
    /**
     * Permet d'informer l'ecouteur que le joueur à été initialisé
     */
    public void joueurInitialise();
    
    /**
     * Permet d'informer l'ecouteur que les joueurs ont été mis à jour
     */
    public void joueursMisAJour();
    
    /**
     * Message recu
     */
    public void messageRecu(String message, Player auteur);

    /**
     * Permet d'informer l'ecouteur de la deconnexion d'un joueur
     */
    public void joueurDeconnecte(Player joueur);

    /**
     * Permet d'informer l'ecouteur qu'une equipe perdue
     */
    public void receptionEquipeAPerdue(Team equipe);
}
