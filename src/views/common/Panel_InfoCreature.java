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

import i18n.Language;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import views.ManageFonts;
import views.LookInterface;

import models.creatures.*;

/**
 * Classe de gestion d'affichage d'information d'une creature.
 * 
 * Le joueur pourra voir les proprietes d'une creature caracterisee par
 * sa sante, son type (terrestre ou aerienne), sa vitesse, etc.
 * 
 * @author Aurélien Da Campo
 * @version 1.0 | 18 decembre 2009
 * @since jdk1.6.0_16
 * @see Creature
 */
public class Panel_InfoCreature extends JPanel
{
    // constante statiques
    private static final long serialVersionUID = 1L;
    private static final ImageIcon I_SANTE     = new ImageIcon("img/icones/heart.png");
	private static final ImageIcon I_VITESSE   = new ImageIcon("img/icones/running_man.gif");
	private static final ImageIcon I_GAIN      = new ImageIcon("img/icones/coins_add.png");
	private static final Dimension DIMENSION_PANEL = new Dimension(280, 220);
    private static final Dimension DIMENSION_IMAGE_ET_NOM = new Dimension(110,80);
    private static final Border BORDURE_IMAGE_ET_NOM = new EmptyBorder(-5,-5,-5,-5);

    
	// attributs
	private JLabel lTitreType       = new JLabel("Type");
	private JLabel lTitreSante      = new JLabel(Language.getTexte(Language.ID_TXT_SANTE),I_SANTE,JLabel.LEFT);
	private JLabel lTitreVitesse    = new JLabel(Language.getTexte(Language.ID_TXT_VITESSE),I_VITESSE,JLabel.LEFT);
	private JLabel lTitreGain       = new JLabel(Language.getTexte(Language.ID_TXT_GAIN),I_GAIN,JLabel.LEFT);
	private JLabel lSante           = new JLabel();
	private JLabel lVitesse         = new JLabel();
	private JLabel lGain            = new JLabel();
	private JLabel lImage           = new JLabel();
    private JLabel lNom             = new JLabel();
    private JPanel pInfos;
    private Creature creature;
		
	/**
	 * Constructeur du panel
	 */
	public Panel_InfoCreature()
	{
		// construction du panel
		super(new BorderLayout());
		setOpaque(false);
		setPreferredSize(DIMENSION_PANEL);
		
		//JLabel lblTitre = new JLabel("Creature selectionnee");
        //lblTitre.setFont(GestionnaireDesPolices.POLICE_SOUS_TITRE);
        //add(lblTitre,BorderLayout.NORTH);
		
		lNom.setFont(ManageFonts.POLICE_TITRE_CHAMP);
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.setOpaque(false);
		p.setPreferredSize(DIMENSION_IMAGE_ET_NOM);
		p.setBorder(BORDURE_IMAGE_ET_NOM);
		p.add(lImage);
		p.add(lNom);
		
		
		lSante.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lVitesse.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lGain.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		
		JPanel p2 = new JPanel();
		p2.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
		p2.setPreferredSize(new Dimension(260,100));
		
		
		pInfos = new JPanel(new GridLayout(0,2));
		pInfos.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		pInfos.setPreferredSize(new Dimension(260,100));
		pInfos.setOpaque(false);
		pInfos.add(p);
		pInfos.add(lTitreType);
		pInfos.add(lTitreSante);
        pInfos.add(lSante);
        pInfos.add(lTitreVitesse);
        pInfos.add(lVitesse);
        pInfos.add(lTitreGain);
        pInfos.add(lGain);
        
        p2.add(pInfos);
        add(p2,BorderLayout.WEST);
        
        pInfos.setVisible(false);
	}
	
	/**
	 * Permet d'effacer la creature
	 */
	public void effacerCreature()
	{
		setCreature(null);
	}
	
	/**
	 * Permet de changement la creature courante
	 * 
	 * Met a jour le panel pour afficher les bonnes informations
	 * 
	 * @param creature La creature a afficher
	 */
	public void setCreature(Creature creature)
	{
	    this.creature = creature;
	    
	    // creature ou pas ?
		if(creature != null)
		{
			// mise a jour des champs
		    lImage.setIcon(new ImageIcon(creature.getImage()));
			lNom.setText(creature.getNom());
			lTitreType.setText(" ["+creature.getNomType()+"]");
			lGain.setText(" : "+creature.getNbPiecesDOr());
			
			miseAJourInfosVariables();

			// affichage du panel
			pInfos.setVisible(true);
		}
		// mode sans creature selectionnee
		else
		    pInfos.setVisible(false);
	}

	/**
	 * Permet de mettre a jour les informations variable de la creature
	 */
    public void miseAJourInfosVariables()
    {
        if(creature != null)
        {
            lSante.setText(" : "+formaterSante(creature.getSante())+" / "+
                                 formaterSante(creature.getSanteMax()));

            // vitesse
            if(creature.getCoeffRalentissement() > 0.0)
            {
                lVitesse.setForeground(Color.BLUE);
                lVitesse.setText(" : "+String.format("%.1f",creature.getVitesseReelle())
                                +" (-"+(creature.getCoeffRalentissement() * 100.0)+"%)");
            }
            else
            {
                lVitesse.setForeground(Color.BLACK);
                lVitesse.setText(" : "+String.format("%.1f",creature.getVitesseNormale()));
            }
        }
    }
    
    static final long KILO  = 1000L;
    static final long MEGA  = 1000000L;
    static final long GIGA  = 1000000000L;
    static final long TERRA = 1000000000000L;
    
    /**
     * Permet de formatter la sante des creature afin de minimiser la place.
     * 
     * @param sante 
     * 
     * @return la sante sous la forme 5000 = 5M, 3000000 = 3G
     */
    private String formaterSante(long sante)
    { 
        long tmp;
        if(sante >= KILO && sante < MEGA)
        {    
            tmp = sante / KILO;
            return tmp + "." + ((sante % KILO) / (KILO / 10)) + "K"; // kilo
        }
        else if(sante >= MEGA && sante < GIGA)
        {    
            tmp = sante / MEGA;
            return tmp + "." + ((sante % MEGA) / (MEGA / 10)) + "M"; // mega
        }
        else if(sante >= GIGA && sante < TERRA)
        {
            tmp = sante / GIGA;
            return tmp + "." + ((sante % GIGA) / (GIGA / 10)) + "G"; // giga
        }
        else if(sante >= TERRA)
        {
            tmp = sante / TERRA;
            return tmp + "." + ((sante % TERRA) / (TERRA / 10)) + "T"; // giga
        }
        else
            return sante+"";
    }
}
