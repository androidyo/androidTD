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

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

import views.ManageFonts;
import views.LookInterface;
import models.game.Game;
import models.game.GameMode;

/**
 * Panel d'affichage des informations du joueur et de la partie en cours
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class Panel_InfosJoueurEtPartie extends JPanel
{
    // constantes finales
    private static final long serialVersionUID      = 1L;
    private static final ImageIcon I_PIECES         = new ImageIcon("img/icones/coins.png");
    private static final ImageIcon I_VIES           = new ImageIcon("img/icones/heart.png");
    private static final ImageIcon I_ETOILE         = new ImageIcon("img/icones/star.png");
    private static final ImageIcon I_TEMPS          = new ImageIcon("img/icones/time.png");
    private static final ImageIcon I_SCORE          = new ImageIcon("img/icones/cup.png");
    private static final ImageIcon I_REVENU         = new ImageIcon("img/icones/income.png");

    private JLabel lTimer                   = new JLabel();
    private JLabel lTitreTimer              = new JLabel(I_TEMPS);
    private JLabel lScore                   = new JLabel();
    private JLabel lTitreScore              = new JLabel(I_SCORE);
    private JLabel lVies                    = new JLabel();
    private JLabel lTitreVies               = new JLabel(I_VIES);
    private JLabel lNbPiecesOr              = new JLabel();
    private JLabel lTitrePiecesOr           = new JLabel(I_PIECES);
    private JLabel lEtoiles                 = new JLabel();
    private JLabel lTitreEtoiles            = new JLabel(I_ETOILE);
    private JLabel lRevenu                  = new JLabel();
    private JLabel lTitreRevenu             = new JLabel(I_REVENU);
    
    // autres membres
    private Game jeu;
    
    public Panel_InfosJoueurEtPartie(final Game jeu, int modeDeJeu)
    {
        this.jeu = jeu;
              
        setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        
        jeu.getTimer().addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                lTimer.setText(jeu.getTimer().toString());
            }
        });
        
        JPanel pGlobalInfo = new JPanel();
        pGlobalInfo.setOpaque(false);
        
        //timer
        pGlobalInfo.add(lTitreTimer);
        pGlobalInfo.add(lTimer);
        
        lTitreTimer.setToolTipText("Time spent");
        lTimer.setToolTipText("Time spent");
        lTimer.setText("00.00.00");
        lTimer.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lTimer.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        
        // etoiles gagnées
        if(modeDeJeu == GameMode.MODE_SOLO)
        {
            pGlobalInfo.add(lTitreEtoiles);
            pGlobalInfo.add(lEtoiles);
            lTitrePiecesOr.setToolTipText("Stars won");
            lEtoiles.setToolTipText("Stars won");
            lEtoiles.setFont(ManageFonts.POLICE_SOUS_TITRE);
            lEtoiles.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            miseAJourNbEtoiles();
        }
        
        // revenu
        if(modeDeJeu != GameMode.MODE_SOLO)
        {
            pGlobalInfo.add(lTitreRevenu);
            pGlobalInfo.add(lRevenu);
            lTitreRevenu.setToolTipText("Income par second");
            lRevenu.setToolTipText("Income par second");
            lRevenu.setFont(ManageFonts.POLICE_SOUS_TITRE);
            lRevenu.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            miseAJourRevenu();
        }
        
        //------------------------------------------
        //-- panel des donnees du joueur          --
        //-- (score, nb pieces or, vies restante) --
        //------------------------------------------
        JPanel pJoueur = new JPanel();
        pJoueur.setOpaque(false);
        
        // score
        pJoueur.add(lTitreScore);
        lTitreScore.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lTitreScore.setToolTipText("Score");
        pJoueur.add(lScore);
        lScore.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lScore.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        lScore.setToolTipText("Score");
        miseAJourScore();
        
        // pieces d'or
        pJoueur.add(lTitrePiecesOr);
        pJoueur.add(lNbPiecesOr);
        lTitrePiecesOr.setToolTipText("Coins");
        lNbPiecesOr.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lNbPiecesOr.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        lNbPiecesOr.setToolTipText("Coins");
        miseAJourNbPiecesOr();
        
        // vies restantes 
        pJoueur.add(lTitreVies);
        pJoueur.add(lVies);
        lTitreVies.setToolTipText("Remaining lives");
        lVies.setToolTipText("Remaining lives");
        lVies.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lVies.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        miseAJourNbViesRestantes();
            
        JPanel pToursEtJoueur = new JPanel(new BorderLayout());
        pToursEtJoueur.setOpaque(false);

        JPanel pAlignADroite = new JPanel(new BorderLayout());
        pAlignADroite.setOpaque(false);
        pAlignADroite.add(pGlobalInfo,BorderLayout.EAST);
        pToursEtJoueur.add(pAlignADroite,BorderLayout.NORTH);
        
        JPanel pAlignADroite2 = new JPanel(new BorderLayout());
        pAlignADroite2.setOpaque(false);
        pAlignADroite2.add(pJoueur,BorderLayout.EAST);
        pToursEtJoueur.add(pAlignADroite2,BorderLayout.CENTER);
        
        miseAJour();
        
        add(pToursEtJoueur,BorderLayout.NORTH); 
    }
    
    /**
     * Permet de demander une mise a jour du nombre de vies restantes du joueur
     */
    private void miseAJourNbViesRestantes()
    {
        lVies.setText(String.format("%02d",jeu.getJoueurPrincipal().getEquipe().getNbViesRestantes()));
    }
    
    /**
     * Permet de demander une mise a jour du nombre de vies restantes du joueur
     */
    private void miseAJourRevenu()
    {
        lRevenu.setText(String.format("%02.2f",jeu.getJoueurPrincipal().getRevenu()));
    }

    /**
     * Permet de demander une mise a jour du nombre d'étoiles gagnées
     */
    private void miseAJourNbEtoiles()
    {
        lEtoiles.setText(String.format("%02d",jeu.getJoueurPrincipal().getNbEtoiles()));
    }
    
    /**
     * Permet de demander une mise a jour du score du joueur
     */
    private void miseAJourScore()
    {
        lScore.setText(String.format("%05d",jeu.getJoueurPrincipal().getScore()));
        miseAJourNbEtoiles();
    }
    
    /**
     * Permet de demander une mise a jour du nombre de pieces du joueur
     */
    private void miseAJourNbPiecesOr()
    {
        double nbPiecesOr = jeu.getJoueurPrincipal().getNbPiecesDOr();
        lNbPiecesOr.setText(String.format("%05d",  (int) nbPiecesOr)); // tronc
    }
    
    public void miseAJour()
    {
        miseAJourNbViesRestantes();
        miseAJourScore();
        miseAJourNbPiecesOr();
        miseAJourNbEtoiles(); 
        miseAJourRevenu();
    }

    public void setPause(boolean enPause)
    {
        // TODO Auto-generated method stub
    }

    public void partieTerminee()
    {
        // TODO Auto-generated method stub 
    }
}
