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
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import views.ManageFonts;
import views.LookInterface;
import models.game.Game;
import models.towers.*;

/**
 * Panel de sélection d'une tour pour achat
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class Panel_AjoutTour extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
    
    // membres graphiques
    private JButton bTourArcher             = new JButton(new ImageIcon(Tower_Archer.ICONE));
    private JButton bTourCanon              = new JButton(new ImageIcon(Tower_Canon.ICONE));
    private JButton bTourAntiAerienne       = new JButton(new ImageIcon(Tower_AntiAerial.ICONE));
    private JButton bTourDeGlace            = new JButton(new ImageIcon(Tower_Ice.ICONE));
    private JButton bTourDeFeu              = new JButton(new ImageIcon(Tower_Fire.ICONE));
    private JButton bTourDAir               = new JButton(new ImageIcon(Tower_Air.ICONE));
    private JButton bTourDeTerre            = new JButton(new ImageIcon(Tower_Earth.ICONE));
    private JButton bTourElectrique         = new JButton(new ImageIcon(Tower_Electric.ICONE));
    private ArrayList<JButton> boutonsTours = new ArrayList<JButton>();
    private Game jeu;
    private EcouteurDePanelTerrain edpt;

    public Panel_AjoutTour(Game jeu, EcouteurDePanelTerrain edpt, int largeur, int hauteur)
    {  
        this.jeu = jeu;
        this.edpt = edpt;
        
        setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        
        //---------------------
        //-- panel des tours --
        //---------------------
        JPanel pTours = new JPanel(new GridLayout(2,0));
        pTours.setOpaque(false);
        pTours.setPreferredSize(new Dimension(largeur,hauteur));
        
        
        String titrePrixAchat = Language.getTexte(Language.ID_TXT_PRIX_ACHAT);
        
        boutonsTours.add(bTourArcher);
        bTourArcher.setToolTipText(titrePrixAchat+" : "+Tower_Archer.PRIX_ACHAT);
        
        boutonsTours.add(bTourCanon);
        bTourCanon.setToolTipText(titrePrixAchat+" : "+Tower_Canon.PRIX_ACHAT);
        
        boutonsTours.add(bTourAntiAerienne);
        bTourAntiAerienne.setToolTipText(titrePrixAchat+" : "+Tower_AntiAerial.PRIX_ACHAT);
        
        boutonsTours.add(bTourDeGlace);
        bTourDeGlace.setToolTipText(titrePrixAchat+" : "+Tower_Ice.PRIX_ACHAT);
        
        boutonsTours.add(bTourElectrique);
        bTourElectrique.setToolTipText(titrePrixAchat+" : "+Tower_Electric.PRIX_ACHAT);
        
        boutonsTours.add(bTourDeFeu);
        bTourDeFeu.setToolTipText(titrePrixAchat+" : "+Tower_Fire.PRIX_ACHAT);
        
        boutonsTours.add(bTourDAir);
        bTourDAir.setToolTipText(titrePrixAchat+" : "+Tower_Air.PRIX_ACHAT);
        
        boutonsTours.add(bTourDeTerre);
        bTourDeTerre.setToolTipText(titrePrixAchat+" : "+Tower_Earth.PRIX_ACHAT);
        
        for(JButton bTour : boutonsTours)
        {
            bTour.addActionListener(this);
            bTour.setBorder(new EmptyBorder(5,5,5,5));
            ManageFonts.setStyle(bTour);
            pTours.add(bTour);
        }
        
        miseAJour();
        
        add(pTours,BorderLayout.CENTER);
    }

    /**
     * Gestion des événements des divers éléments du 
     * panel (menu, bouttons, etc.).
     * 
     * @param ae l'événement associé à une action
     */
    public void actionPerformed(ActionEvent ae)
    {
        Object source = ae.getSource();
        Tower tour = null;
        
        if(source == bTourArcher)
            tour = new Tower_Archer();
        else if(source == bTourCanon)
            tour = new Tower_Canon();
        else if(source == bTourAntiAerienne)
            tour = new Tower_AntiAerial();
        else if(source == bTourDeGlace)
            tour = new Tower_Ice();
        else if(source == bTourDeFeu)
            tour = new Tower_Fire();
        else if(source == bTourDAir)
            tour = new Tower_Air();
        else if(source == bTourDeTerre)
            tour = new Tower_Earth();
        else if(source == bTourElectrique)
            tour = new Tower_Electric();
        else
            return;
        
        tour.setProprietaire(jeu.getJoueurPrincipal());
        
        edpt.tourSelectionnee(tour, Panel_InfoTour.MODE_ACHAT);
        edpt.setTourAAcheter(tour);
    }

    public void partieTerminee()
    {
        // desactivation des tours
        for(JButton bTour : boutonsTours)
            bTour.setEnabled(false); 
    }
    
    public void miseAJour()
    {
        double nbPiecesOr = jeu.getJoueurPrincipal().getNbPiecesDOr();
        
        bTourArcher.setEnabled(nbPiecesOr >= Tower_Archer.PRIX_ACHAT);
        bTourCanon.setEnabled(nbPiecesOr >= Tower_Canon.PRIX_ACHAT);
        bTourAntiAerienne.setEnabled(nbPiecesOr >= Tower_AntiAerial.PRIX_ACHAT);
        bTourDeGlace.setEnabled(nbPiecesOr >= Tower_Ice.PRIX_ACHAT);
        bTourElectrique.setEnabled(nbPiecesOr >= Tower_Electric.PRIX_ACHAT);
        bTourDeFeu.setEnabled(nbPiecesOr >= Tower_Fire.PRIX_ACHAT);
        bTourDAir.setEnabled(nbPiecesOr >= Tower_Air.PRIX_ACHAT);
        bTourDeTerre.setEnabled(nbPiecesOr >= Tower_Earth.PRIX_ACHAT);
    }
    
    public void setPause(boolean pause)
    {
        if(pause)
            for(JButton bTour : boutonsTours)
                bTour.setEnabled(false);   
        else
            miseAJour();
    }
}
