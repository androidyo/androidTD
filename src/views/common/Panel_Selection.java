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

package views.common;

import i18n.Langue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

import views.ManageFonts;
import views.LookInterface;

import models.creatures.Creature;
import models.game.Game;
import models.towers.*;

/**
 * Classe de gestion d'affichage d'information d'une tour.
 * 
 * Le joueur pourra voir les proprietes d'une tour caracterisee par
 * sont prix, ses degats, son rayon de portee, etc.
 * 
 * C'est dans ce panel que le joueur peut ameliorer une tour ou la vendre.
 * 
 * @author Aurélien Da Campo
 * @version 1.0 | mai 2010
 * @since jdk1.6.0_16
 * @see JPanel
 * @see Tower
 */
public class Panel_Selection extends JPanel
{
    private static final long serialVersionUID = 1L;
	private Panel_InfoTour pInfoTour;
	private Panel_InfoCreature pInfoCreature;
	private Panel_InfoVagues pInfoVagues;

    /**
	 * Constructeur du panel
	 * 
	 * FIXME ATTENTION avec le panel des info vagues en multi
	 * 
	 * @param la fenetre de jeu parent
	 */
	public Panel_Selection(Game jeu,EcouteurDePanelTerrain edpt)
	{
		super(new BorderLayout());
		
		setOpaque(false);
		
		setPreferredSize(new Dimension(280,300));
		//setBackground(LookInterface.COULEUR_DE_FOND_2);
		
		JLabel lTitre = new JLabel(Langue.getTexte(Langue.ID_TITRE_INFO_SELECTION));
		lTitre.setFont(ManageFonts.POLICE_SOUS_TITRE);
		lTitre.setForeground(LookInterface.COULEUR_TEXTE_PRI);
		add(lTitre,BorderLayout.NORTH);
		
		JPanel p = new JPanel(new FlowLayout());
		//p.setOpaque(false);
		p.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
		
		pInfoTour = new Panel_InfoTour(edpt);
		p.add(pInfoTour);
		pInfoTour.setVisible(false);
		
	    pInfoCreature = new Panel_InfoCreature();
        p.add(pInfoCreature);
        pInfoCreature.setVisible(false);
        
        pInfoVagues = new Panel_InfoVagues(jeu);
        p.add(pInfoVagues);
        pInfoVagues.setVisible(true);
        
        add(p,BorderLayout.CENTER);  
	}
	
	/**
     * Permet d'informer de le panel que la partie est terminee.
     * 
     * Bloque tous les boutons.
     */
    public void partieTerminee()
    {
        pInfoTour.partieTerminee();
    }
	
	/**
	 * Permet de changer de tour
	 * 
	 * Met a jour le panel pour afficher les bonnes informations
	 * 
	 * @param tour La tour a gerer
	 */
	public void setSelection(Object selection, int mode)
	{
	    if(selection == null)
	    {
	        System.out.println("selection nulle");
	        
	        pInfoCreature.setVisible(false);
	        pInfoTour.setVisible(false);
	        pInfoVagues.setVisible(true);
	    }
	    // Tour
	    else if(Tower.class.isInstance(selection))
		{
	        pInfoTour.setTour((Tower) selection, mode);
	        pInfoTour.setVisible(true);
	        pInfoCreature.setVisible(false);
	        pInfoVagues.setVisible(false);
		}
	    // Creature
		else if(Creature.class.isInstance(selection))
        {
		    pInfoCreature.setCreature((Creature) selection);
		    pInfoCreature.setVisible(true);
		    pInfoTour.setVisible(false);
		    pInfoVagues.setVisible(false);
        }
	}

	/**
	 * Permet d'informer le panel d'un changement d'état de la pause
	 * 
	 * @param enPause
	 */
    public void setPause(boolean enPause)
    {
        pInfoTour.setPause(enPause);
    }

    /**
     * Permet de recuperer le panel d'info tour
     * 
     * @return le panel d'information d'une tour
     */
    public Panel_InfoTour getPanelInfoTour()
    {
        return pInfoTour;
    }

    /**
     * Permet de recuperer le panel d'information d'une créature
     * 
     * @return le panel d'information d'une créature
     */
    public Panel_InfoCreature getPanelInfoCreature()
    {
        return pInfoCreature;
    }
    
    public void deselection()
    {  
        pInfoTour.setVisible(false);
        pInfoCreature.setVisible(false);
        pInfoVagues.setVisible(true);
    }
}
