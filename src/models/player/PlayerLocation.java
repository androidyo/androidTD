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

import java.awt.Color;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 * Un emplacement est une zone ou le joueur peut poser ses tours.
 * <br>
 * Chaque joueur doit posséder un emplacement.
 * <br>
 * Les emplacements sont normalement défini et géré par les équipes.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 */
public class PlayerLocation implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
     * Identificateur
     */
    private int id;
    
    /**
     * Le propriétaire de l'emplacement
     */
    transient Player joueur;
    
    /**
     * Permet de definir ou le joueur peut poser ses tours
     */
    Rectangle zoneDeConstruction;
    
    /**
     * Couleur pour les affichages
     */
    private Color couleur;
 
    /**
     * Constructeur 
     * 
     * @param id identificateur
     * @param zoneDeConstruction la zone de construction
     */
    public PlayerLocation(int id, Rectangle zoneDeConstruction)
    {
        this(id,zoneDeConstruction,Color.BLACK);
    }
    
    /**
     * Constructeur 
     * 
     * @param id identificateur
     * @param zoneDeConstruction la zone de construction
     * @param couleur la couleur
     */
    public PlayerLocation(int id,Rectangle zoneDeConstruction, Color couleur)
    {
        this.zoneDeConstruction = zoneDeConstruction;
        this.couleur = couleur;
        this.id = id;
    }
    
    /**
     * Permet de modifier le joueur.
     * 
     * Attention : la mise a jour de l'emplacement du joueur sera affectué.
     * 
     * @param joueur le nouveau joueur, null pour retirer l'ancien joueur
     */
    public void setJoueur(Player joueur)
    {
        // suppression
        if(joueur == null)
        { 
            // seulement s'il y avait un joueur avant
            if(this.joueur != null)
            {
                this.joueur.setEmplacementJoueur(null);
                this.joueur = null;
            }
        }
        // modification
        else
        {
            // occupe
            if(this.joueur != null)
                throw new IllegalArgumentException("Emplacement occupée");
            // ok
            else
            { 
                // on libère l'ancien emplacement
                if(joueur.getEmplacement() != null)
                    joueur.getEmplacement().setJoueur(null);
                
                // mise a jour
                this.joueur = joueur;
                joueur.setEmplacementJoueur(this);
            }
        }   
    }
    
    /**
     * Permet de recuperer la couleur
     * @return la couleur
     */
    public Color getCouleur()
    {
        return couleur;
    }
    
    /**
     * Permet de recuperer la zone de construction
     * 
     * @return la zone de construction
     */
    public Rectangle getZoneDeConstruction()
    {
        return zoneDeConstruction;
    }

    /**
     * Permet de recuperer le joueur
     * 
     * @return le joueur
     */
    public Player getJoueur()
    {
        return joueur;
    }
    
    /**
     * Permet de recuperer l'identificateur
     * @return l'identificateur
     */
    public int getId()
    {
        return id;
    }
    
    @Override
    public String toString()
    {
        return "Zone "+id;
    }

    /**
     * Permet de retirer le joueur de l'emplacement
     */
    public void retirerJoueur()
    {
        // seulement s'il y a un joueur
        if(this.joueur != null)
        {
            this.joueur.quitterEmplacementJoueur();
            this.joueur = null;
        }
    }

    /**
     * Permet de modifier la couleur de la zone
     * 
     * @param couleur la nouvelle couleur
     */
    public void setCouleur(Color couleur)
    {
       this.couleur = couleur;
    }
}
