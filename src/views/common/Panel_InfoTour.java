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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import views.ManageFonts;
import views.LookInterface;
import models.towers.*;

/**
 * Classe de gestion d'affichage d'information d'une tour.
 * 
 * Le joueur pourra voir les proprietes d'une tour caracterisee par
 * sont prix, ses degats, son rayon de portee, etc.
 * 
 * C'est dans ce panel que le joueur peut ameliorer une tour ou la vendre.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 27 novemenbre 2009
 * @since jdk1.6.0_16
 * @see JPanel
 * @see Tower
 */
public class Panel_InfoTour extends JPanel implements ActionListener
{
	// constantes statiques
    public static final int MODE_SELECTION 	   = 0;
	public static final int MODE_ACHAT 		   = 1;
	private static final long serialVersionUID = 1L;
	//private static final ImageIcon I_AMELIORER = new ImageIcon("img/icones/upgrade.png");
    //private static final ImageIcon I_VENDRE    = new ImageIcon("img/icones/sale.png");
	
	
	
	
    private static final String TXT_AMELIORER  = Langue.getTexte(Langue.ID_TXT_BTN_AMELIORER);
    private static final String TXT_VENDRE     = Langue.getTexte(Langue.ID_TXT_BTN_VENDRE);
    private static final String TXT_PRIX_ACHAT = Langue.getTexte(Langue.ID_TXT_PRIX_ACHAT);
    private static final String TXT_PRIX_TOTAL = Langue.getTexte(Langue.ID_TXT_VALEUR_PRIX);
    private static final Dimension DIMENSION_PANEL = new Dimension(280, 350);
    private static final Dimension DIMENSION_DESCRIPTION = new Dimension(240,120);
    
	// membres graphiques
	private JLabel lNom 			 = new JLabel();
	private JLabel lDegats 			 = new JLabel();
	private JLabel lRayonPortee      = new JLabel();
	private JLabel lPrix 			 = new JLabel();
	private JLabel lTitrePrix 		 = new JLabel();
	private JLabel lCadenceTir	     = new JLabel();
	private JLabel lDPS              = new JLabel();
	private JTextArea taDescrition 	 = new JTextArea();
	private JScrollPane spDescription;
	private JPanel pCaracteristiques = new JPanel(new GridBagLayout());
	private JButton bAmeliorer       = new JButton(TXT_AMELIORER);//I_AMELIORER);
    private JButton bVendre          = new JButton(TXT_VENDRE);//I_VENDRE);
	
    private JLabel lPrixLvlS         = new JLabel();
    private JLabel lTitreLvl         = new JLabel();
    private JLabel lTitreLvlS        = new JLabel();
    private JLabel lDegatsLvlS       = new JLabel();
    private JLabel lRayonPorteeLvlS  = new JLabel();
    private JLabel lCadenceTirLvlS   = new JLabel();
    private JLabel lDPSLvlS          = new JLabel();
      
    private JComboBox cbTypeCiblage = new JComboBox();
    
    // autres membres
	private Tower tour;
	private EcouteurDePanelTerrain edpt;
    private boolean partieTerminee;
    private boolean enPause;
	
	/**
	 * Constructeur du panel
	 * 
	 * @param la fenetre de jeu parent
	 */
	public Panel_InfoTour(EcouteurDePanelTerrain edpt)
	{
		// construction du panel
		super(new BorderLayout());
		setOpaque(false);
		
		setPreferredSize(DIMENSION_PANEL);
		
		this.edpt = edpt;
		JPanel pConteneurCaract = new JPanel();
		pConteneurCaract.setOpaque(false);
		int nbChamps = 0;
		pCaracteristiques.setOpaque(false);
		
		
		// champ nom
		lNom.setFont(ManageFonts.POLICE_TITRE_CHAMP);
		ajouterChamp(pCaracteristiques, lNom, 0, nbChamps++, 3);
		
		// champ description
        taDescrition.setEditable(false);
        taDescrition.setLineWrap(true);
        taDescrition.setWrapStyleWord(true);
        taDescrition.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        //taDescrition.setBorder(new EmptyBorder(1, 1, 1, 1));
        taDescrition.setFocusable(false);
        taDescrition.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
        taDescrition.setForeground(LookInterface.COULEUR_TEXTE_PRI);

        spDescription = new JScrollPane(taDescrition);
        spDescription.setPreferredSize(DIMENSION_DESCRIPTION);
        ajouterChamp(pCaracteristiques, spDescription, 0, nbChamps++, 3);
		
		lTitreLvl.setFont(ManageFonts.POLICE_SOUS_TITRE);
		lTitreLvlS.setFont(ManageFonts.POLICE_SOUS_TITRE);
		lTitreLvl.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lTitreLvlS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		ajouterChamp(pCaracteristiques, lTitreLvl, 1, nbChamps, 1);
		ajouterChamp(pCaracteristiques, lTitreLvlS, 2, nbChamps++, 1);
		
		// champ prix
		lTitrePrix.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lTitrePrix.setFont(ManageFonts.POLICE_TITRE_CHAMP);
		ajouterChamp(pCaracteristiques, lTitrePrix, 0, nbChamps, 1);
		lPrix.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lPrixLvlS.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lPrix.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lPrixLvlS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		ajouterChamp(pCaracteristiques, lPrix, 1, nbChamps, 1);
        ajouterChamp(pCaracteristiques, lPrixLvlS, 2, nbChamps++, 1);
		
		// champ degats
        JLabel lTitreDegats = new JLabel(Langue.getTexte(Langue.ID_TXT_DEGATS));
        lTitreDegats.setForeground(LookInterface.COULEUR_TEXTE_SEC);
        lTitreDegats.setFont(ManageFonts.POLICE_TITRE_CHAMP);
        
		ajouterChamp(pCaracteristiques, lTitreDegats, 0, nbChamps, 1);
		lDegats.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lDegatsLvlS.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lDegats.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lDegatsLvlS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		ajouterChamp(pCaracteristiques, lDegats, 1, nbChamps, 1);
		ajouterChamp(pCaracteristiques, lDegatsLvlS, 2, nbChamps++, 1);
		
		// champ rayon de portee
		JLabel lTitrePortee = new JLabel(Langue.getTexte(Langue.ID_TXT_PORTEE));
		lTitrePortee.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lTitrePortee.setFont(ManageFonts.POLICE_TITRE_CHAMP);
		
		ajouterChamp(pCaracteristiques, lTitrePortee, 0, nbChamps, 1);
		lRayonPortee.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lRayonPorteeLvlS.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lRayonPortee.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lRayonPorteeLvlS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		ajouterChamp(pCaracteristiques, lRayonPortee, 1, nbChamps, 1);
		ajouterChamp(pCaracteristiques, lRayonPorteeLvlS, 2, nbChamps++, 1);
		
		// champ cadence de tir
		JLabel lTitreCadenceTir = new JLabel(Langue.getTexte(Langue.ID_TXT_TIRS_SEC));
		lTitreCadenceTir.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lTitreCadenceTir.setFont(ManageFonts.POLICE_TITRE_CHAMP);
		
		ajouterChamp(pCaracteristiques, lTitreCadenceTir, 0, nbChamps, 1);
		lCadenceTir.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lCadenceTirLvlS.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lCadenceTir.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lCadenceTirLvlS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		ajouterChamp(pCaracteristiques, lCadenceTir, 1, nbChamps, 1);
		ajouterChamp(pCaracteristiques, lCadenceTirLvlS, 2, nbChamps++, 1);
		
		// champ DPS : dégats par seconde
		JLabel lTitreDPS = new JLabel(Langue.getTexte(Langue.ID_TXT_DPS));
		lTitreDPS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lTitreDPS.setFont(ManageFonts.POLICE_TITRE_CHAMP);
		
		ajouterChamp(pCaracteristiques, lTitreDPS, 0, nbChamps, 1);
		lDPS.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lDPSLvlS.setFont(ManageFonts.POLICE_VALEUR_CHAMP);
		lDPS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
		lDPSLvlS.setForeground(LookInterface.COULEUR_TEXTE_SEC);
        ajouterChamp(pCaracteristiques, lDPS, 1, nbChamps, 1);
        ajouterChamp(pCaracteristiques, lDPSLvlS, 2, nbChamps++, 1);
		
		// les boutons
        ajouterChamp(pCaracteristiques, bVendre, 0, nbChamps, 2);
        ajouterChamp(pCaracteristiques, bAmeliorer, 2, nbChamps++, 1);
        
        Font f = new Font("", Font.BOLD, 9);
        
        bAmeliorer.setFont(f);
        bVendre.setFont(f);
        bAmeliorer.setPreferredSize(new Dimension(50,30));
        bVendre.setPreferredSize(new Dimension(50,30));
        ManageFonts.setStyle(bAmeliorer);
        ManageFonts.setStyle(bVendre);
        bAmeliorer.addActionListener(this);
        bVendre.addActionListener(this);
		
       
        JPanel pCiblage = new JPanel();
        pCiblage.setOpaque(false);
        JLabel lTypeCiblage = new JLabel(Langue.getTexte(Langue.ID_TXT_ATTAQUE_LA_CREATURE));
        pCiblage.add(lTypeCiblage);
        lTypeCiblage.setForeground(LookInterface.COULEUR_TEXTE_SEC);
        cbTypeCiblage.addItem(Langue.getTexte(Langue.ID_TXT_LA_PLUS_PROCHE));
        cbTypeCiblage.addItem(Langue.getTexte(Langue.ID_TXT_LA_PLUS_LOIN));
        cbTypeCiblage.addItem(Langue.getTexte(Langue.ID_TXT_LA_PLUS_FAIBLE));
        cbTypeCiblage.addItem(Langue.getTexte(Langue.ID_TXT_LA_PLUS_FORTE));
        cbTypeCiblage.addActionListener(this);
        ManageFonts.setStyle(cbTypeCiblage);
        
        pCiblage.add(cbTypeCiblage);
        ajouterChamp(pCaracteristiques, pCiblage, 0, nbChamps++, 3);
        
		pConteneurCaract.add(pCaracteristiques,BorderLayout.CENTER);
		
		JPanel pConteneurCaraEtBoutons = new JPanel(new BorderLayout());
		pConteneurCaraEtBoutons.setPreferredSize(new Dimension(260,160));
		pConteneurCaraEtBoutons.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
		pConteneurCaraEtBoutons.add(pConteneurCaract,BorderLayout.NORTH);
		//pConteneurCaraEtBoutons.add(pBoutons,BorderLayout.SOUTH);
		
		add(pConteneurCaraEtBoutons,BorderLayout.WEST);

		// initialisation a vide
		effacerTour();
	}
	
	/**
	 * Permet d'ajouter un champ dans un GridBagLayout
	 * 
	 * @param panel le GridBagLayout
	 * @param composant le composant a ajouter
	 * @param gridx position x dans la grille
	 * @param gridy position y dans la grille
	 * @param gridwidth largeur de la grille
	 */
	private GridBagConstraints gbc = new GridBagConstraints();
	public void ajouterChamp(JPanel panel, Component composant, int gridx, int gridy, int gridwidth)
	{
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.insets  = new Insets(1, 8, 1, 8);
		gbc.gridx 	= gridx;
		gbc.gridy 	= gridy;
		gbc.gridwidth = gridwidth;
		
		panel.add(composant,gbc);
	}
	
	/**
	 * Permet de deselectionner la tour affichee
	 */
	public void effacerTour()
	{
		setTour(null, 0);
	}
	
	/**
     * Permet d'informer de le panel que la partie est terminee.
     * 
     * Bloque tous les boutons.
     */
    public void partieTerminee()
    {
        bAmeliorer.setEnabled(false);
        bVendre.setEnabled(false);
        partieTerminee = true;
    }
	
	/**
	 * Permet de changer de tour
	 * 
	 * Met a jour le panel pour afficher les bonnes informations
	 * 
	 * @param tour La tour a gerer
	 */
	public void setTour(Tower tour, int mode)
	{
		// tour ou pas ?
		if(tour != null)
		{
			// mise a jour des caracteristiques
		    lNom.setIcon(new ImageIcon(tour.getIcone()));
		    lNom.setForeground(tour.getCouleurDeFond());
            lNom.setText(tour.getNom()+" lvl. "+tour.getNiveau()+" ["+tour.getTexteType()+"]");
            lTitreLvl.setText("lvl. "+tour.getNiveau());
            lDegats.setText(tour.getDegats()+"");
			lRayonPortee.setText(String.format("%.1f", tour.getRayonPortee()));
			lCadenceTir.setText(String.format("%.1f", tour.getCadenceTir()));
			lDPS.setText(String.format("%.1f", tour.getCadenceTir()*tour.getDegats()));
			taDescrition.setText(tour.getDescription());
			
			// reset des scroll bars
			repaint();
			JScrollBar verticalScrollBar = spDescription.getVerticalScrollBar();
		    JScrollBar horizontalScrollBar = spDescription.getHorizontalScrollBar();
		    verticalScrollBar.setValue(verticalScrollBar.getMinimum());
		    horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
		    updateUI();
		    
			// Améliorations
			if(tour.peutEncoreEtreAmelioree())
			{
			    lTitreLvlS.setText("lvl. "+(tour.getNiveau()+1));
			    lPrixLvlS.setText(tour.getPrixAchat()+"");
			    lDegatsLvlS.setText(tour.getDegatsLvlSuivant()+"");
			    lRayonPorteeLvlS.setText(String.format("%.1f",tour.getRayonPorteeLvlSuivant()));
			    lCadenceTirLvlS.setText(String.format("%.1f",tour.getCadenceTirLvlSuivant()));
			    lDPSLvlS.setText(String.format("%.1f", tour.getCadenceTirLvlSuivant()*tour.getDegatsLvlSuivant()));
			}
			else
			{
			    lTitreLvlS.setText("");
			    lPrixLvlS.setText("");
			    lDegatsLvlS.setText("");
                lRayonPorteeLvlS.setText("");
                lCadenceTirLvlS.setText("");
                lDPSLvlS.setText(""); 
			}
			
			// tour selectionnee pour information
			if(mode == 0)
			{
				// adaptation des champs
				lTitrePrix.setText(TXT_PRIX_TOTAL);
				lPrix.setText(tour.getPrixTotal()+"");

				if(!partieTerminee)
				{
    				if(enPause)
    				{
    				    bAmeliorer.setEnabled(false);
    				    bVendre.setEnabled(false);
    				}
				    // adaptation des boutons
    				else if(tour.peutEncoreEtreAmelioree())
    				{
    					bAmeliorer.setEnabled(true);
    					bAmeliorer.setText(TXT_AMELIORER+" ["+tour.getPrixAchat()+"]");
    				}
    				else
    				{
    					bAmeliorer.setText("[niveau max]");
    					bAmeliorer.setEnabled(false);
    				}
				}
				
				bVendre.setText(TXT_VENDRE+"["+tour.getPrixDeVente()+"]");
	
				bVendre.setVisible(true);
				bAmeliorer.setVisible(true);
			}
			// tour selectionnee pour achat
			else if(mode == 1)
			{
				// adaptation des champs
				lTitrePrix.setText(TXT_PRIX_ACHAT);
				lPrix.setText(tour.getPrixAchat()+"");
				
				// adaptation des boutons
				bVendre.setVisible(false);
                bAmeliorer.setVisible(false);
			}
			
			// sauvegarde de la tour pour les operations
			this.tour = tour;
			
			cbTypeCiblage.setSelectedIndex(tour.getTypeCiblage());
			
			pCaracteristiques.setVisible(true);
		}
		// mode sans tour selectionnee
		else
		{
			pCaracteristiques.setVisible(false);
			bVendre.setVisible(false);
            bAmeliorer.setVisible(false);
		}
	}

    /**
     * Gestionnaire des evenements. 
     * <p>
     * Cette methode est appelee en cas d'evenement
     * sur un objet ecouteur de ActionListener
     * 
     * @param ae l'evenement associe
     */
	public void actionPerformed(ActionEvent ae)
	{
		Object source = ae.getSource();
		
		if(source == bAmeliorer)
			edpt.ameliorerTour(tour);
		
		else if(source == bVendre)
			edpt.vendreTour(tour);
		
		else if(source == cbTypeCiblage)
		{
		    tour.setTypeCiblage(cbTypeCiblage.getSelectedIndex());
		}
	}

    public void setPause(boolean enPause)
    {
        this.enPause = enPause;
        
        bAmeliorer.setEnabled(!enPause);
        bVendre.setEnabled(!enPause);
    }
}
