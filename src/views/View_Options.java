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

package views;

import i18n.Language;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import models.utils.SoundManagement;

import utils.Configuration;
import views.common.Panel_Table;

public class View_Options extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;

    private static class Panel_OptionsReseau extends JPanel
    {
        private static final long serialVersionUID = 1L;
        private Panel_Table pFormulaire = new Panel_Table();
        private JTextField tfIP_SE = new JTextField(Configuration.getIpSE());
        
        public Panel_OptionsReseau()
        {
            setBackground(LookInterface.COULEUR_DE_FOND_PRI);
            
            pFormulaire.setOpaque(false);
            JLabel lblIPSrv = new JLabel(Language.getTexte(Language.ID_TXT_IP_SRV_ENR)+" :");
            lblIPSrv.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            pFormulaire.add(lblIPSrv,0,0);
            
            tfIP_SE.setPreferredSize(new Dimension(100,25));
            pFormulaire.add(tfIP_SE,1,0);
           
            // TODO check
            tfIP_SE.getDocument().addDocumentListener(new DocumentListener()
            { 
                @Override
                public void removeUpdate(DocumentEvent arg0)
                {
                    Configuration.setIpSE(tfIP_SE.getText());
                }
                
                @Override
                public void insertUpdate(DocumentEvent arg0)
                {
                    Configuration.setIpSE(tfIP_SE.getText());
                }
                
                @Override
                public void changedUpdate(DocumentEvent arg0)
                {
                    Configuration.setIpSE(tfIP_SE.getText());
                }
            });
            
            add(pFormulaire); 
        }
    }
    
    
    private static class Panel_OptionsJeu extends JPanel
    {
        private static final long serialVersionUID = 1L;
        private Panel_Table pFormulaire = new Panel_Table();
        private JTextField tfPseudoJoueur = new JTextField(Configuration.getPseudoJoueur());
       
        public Panel_OptionsJeu()
        {
            setBackground(LookInterface.COULEUR_DE_FOND_PRI);
            
            pFormulaire.setOpaque(false);
            JLabel lPseudo = new JLabel("Pseudo");
            lPseudo.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            pFormulaire.add(lPseudo,0,0);
            
            tfPseudoJoueur.setPreferredSize(new Dimension(100,25));
            pFormulaire.add(tfPseudoJoueur,1,0);
            
            tfPseudoJoueur.getDocument().addDocumentListener(new DocumentListener()
            { 
                @Override
                public void removeUpdate(DocumentEvent arg0)
                {
                    Configuration.setPseudoJoueur(tfPseudoJoueur.getText());
                }
                
                @Override
                public void insertUpdate(DocumentEvent arg0)
                {
                    Configuration.setPseudoJoueur(tfPseudoJoueur.getText());
                }
                
                @Override
                public void changedUpdate(DocumentEvent arg0)
                {
                    Configuration.setPseudoJoueur(tfPseudoJoueur.getText());
                }
            });
            
            
            add(pFormulaire); 
        }
    }
    
    
    private static class Panel_OptionsCommandes extends JPanel 
                                                implements 
                                                ActionListener, 
                                                KeyListener
    {
        
        private static class BoutonKeyCode extends JButton
        {
            private static final long serialVersionUID = 1L;
            private int keyCode;
            private final String PROPRIETE;
            
            
            public BoutonKeyCode(String propriete) 
            {   
                PROPRIETE = propriete;
                
                int keyCode2 = Integer.parseInt(Configuration.getProprety(PROPRIETE));
                
                setKeyCode(keyCode2);
            }
             
            public void setKeyCode(int keyCode) 
            {
                this.keyCode = keyCode;
                
                Configuration.setProperty(PROPRIETE,keyCode+"");
                
                super.setText(KeyEvent.getKeyText(keyCode));
            }
            
            public int getKeyCode()
            {
                return keyCode;
            }  
        }
        
        
        private static final long serialVersionUID = 1L;
        private Panel_Table pFormulaire = new Panel_Table();
        
        private JLabel lDeplHaut = new JLabel(Language.getTexte(Language.ID_TXT_DEPL_HAUT));
        private JLabel lDeplGauche = new JLabel(Language.getTexte(Language.ID_TXT_DEPL_GAUCHE));
        private JLabel lDeplBas = new JLabel(Language.getTexte(Language.ID_TXT_DEPL_BAS));
        private JLabel lDeplDroite = new JLabel(Language.getTexte(Language.ID_TXT_DEPL_DROITE));
        private JLabel lLancerVague = new JLabel(Language.getTexte(Language.ID_TXT_LANCER_VAGUE_SUIVANTE));
        private JLabel lAmeliorerTour = new JLabel(Language.getTexte(Language.ID_TXT_AMELIORER_TOUR));
        private JLabel lVendreTour = new JLabel(Language.getTexte(Language.ID_TXT_VENDRE_TOUR));
        private JLabel lMettreEnPause = new JLabel(Language.getTexte(Language.ID_TXT_METTRE_JEU_EN_PAUSE));
        private JLabel lSuivreCreature = new JLabel(Language.getTexte(Language.ID_TXT_SUIVRE_CREATURE));
        private JLabel lAugmenterVitesse = new JLabel(Language.getTexte(Language.ID_TXT_AUGMENTER_VITESSE_JEU));
        private JLabel lDiminuerVitesse = new JLabel(Language.getTexte(Language.ID_TXT_DIMINUER_VITESSE_JEU));
        private JLabel lZoomer = new JLabel(Language.getTexte(Language.ID_TXT_ZOMMER));
        
        private BoutonKeyCode bDeplHaut = new BoutonKeyCode(Configuration.DEPL_HAUT);
        private BoutonKeyCode bDeplBas = new BoutonKeyCode(Configuration.DEPL_BAS);
        private BoutonKeyCode bDeplDroite = new BoutonKeyCode(Configuration.DEPL_DROITE);
        private BoutonKeyCode bDeplGauche = new BoutonKeyCode(Configuration.DEPL_GAUCHE);
        private BoutonKeyCode bLancerVagueSuivante = new BoutonKeyCode(Configuration.LANCER_VAGUE);
        private BoutonKeyCode bVendre = new BoutonKeyCode(Configuration.VENDRE_TOUR);
        private BoutonKeyCode bAmeliorer = new BoutonKeyCode(Configuration.AMELIO_TOUR);
        private BoutonKeyCode bPause = new BoutonKeyCode(Configuration.PAUSE);
        private BoutonKeyCode bSuivre = new BoutonKeyCode(Configuration.SUIVRE_CREATURE);
        private BoutonKeyCode bAugmenterVitesseJeu = new BoutonKeyCode(Configuration.AUG_VIT_JEU);
        private BoutonKeyCode bDiminuerVitesseJeu = new BoutonKeyCode(Configuration.DIM_VIT_JEU);
        
        private JLabel lZoom = new JLabel(Language.getTexte(Language.ID_TXT_ROULETTE_SOURIS));
        private ArrayList<BoutonKeyCode> boutons = new ArrayList<BoutonKeyCode>();
        private boolean attenteTouche;
        private BoutonKeyCode boutonCourant;
       
        public Panel_OptionsCommandes()
        {  
            setBackground(LookInterface.COULEUR_DE_FOND_PRI);
            
            boutons.add(bDeplHaut);
            boutons.add(bDeplGauche);
            boutons.add(bDeplBas);
            boutons.add(bDeplDroite);
            boutons.add(bLancerVagueSuivante);
            boutons.add(bVendre);
            boutons.add(bAmeliorer);
            boutons.add(bPause);
            boutons.add(bSuivre);
            boutons.add(bAugmenterVitesseJeu);
            boutons.add(bDiminuerVitesseJeu);
            
            for(JButton b : boutons)
            {
                b.addActionListener(this);
                b.addKeyListener(this);
                // désactive l'autovalidation par la touche ESPACE
                b.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
                "doNothing");
            }

            // styles
            lDeplHaut.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lDeplGauche.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lDeplBas.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lDeplDroite.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lLancerVague.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lAmeliorerTour.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lVendreTour.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lMettreEnPause.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lSuivreCreature.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lAugmenterVitesse.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lDiminuerVitesse.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lZoomer.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lZoom.setForeground(LookInterface.COULEUR_TEXTE_PRI);

            int i=0;
            pFormulaire.setOpaque(false);
            pFormulaire.add(lDeplHaut,0,i++);
            pFormulaire.add(lDeplGauche,0,i++);
            pFormulaire.add(lDeplBas,0,i++);
            pFormulaire.add(lDeplDroite,0,i++);
            pFormulaire.add(lLancerVague,0,i++);
            pFormulaire.add(lAmeliorerTour,0,i++);
            pFormulaire.add(lVendreTour,0,i++);
            pFormulaire.add(lMettreEnPause,0,i++);
            pFormulaire.add(lSuivreCreature,0,i++);
            pFormulaire.add(lAugmenterVitesse,0,i++);
            pFormulaire.add(lDiminuerVitesse,0,i++);
            pFormulaire.add(lZoomer,0,i++);
            
            i = 0;
            pFormulaire.add(bDeplHaut,1,i++);
            pFormulaire.add(bDeplGauche,1,i++);
            pFormulaire.add(bDeplBas,1,i++);
            pFormulaire.add(bDeplDroite,1,i++);
            pFormulaire.add(bLancerVagueSuivante,1,i++);
            pFormulaire.add(bAmeliorer,1,i++);
            pFormulaire.add(bVendre,1,i++);
            pFormulaire.add(bPause,1,i++);
            pFormulaire.add(bSuivre,1,i++);
            pFormulaire.add(bAugmenterVitesseJeu,1,i++);
            pFormulaire.add(bDiminuerVitesseJeu,1,i++);
            pFormulaire.add(lZoom,1,i++);
            
            // Style
            for(BoutonKeyCode b : boutons)
                b.setFont(ManageFonts.POLICE_TITRE_CHAMP);
            
            lZoom.setFont(ManageFonts.POLICE_TITRE_CHAMP);
            
            
            add(pFormulaire); 
            
            pFormulaire.setFocusable(true);
            pFormulaire.addKeyListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            attenteTouche = true;
            boutonCourant = (BoutonKeyCode) e.getSource();
              
            // désactivation
            for(BoutonKeyCode b : boutons)
                if(b != boutonCourant)
                    b.setEnabled(false);
        }

        @Override
        public void keyPressed(KeyEvent e){}

        @Override
        public void keyReleased(KeyEvent e)
        {
            if(attenteTouche)
            {
                for(BoutonKeyCode b : boutons)
                {
                    b.setEnabled(true);
                    
                    if(b.getKeyCode() == e.getKeyCode())
                        b.setKeyCode(boutonCourant.getKeyCode());
                }
                
                boutonCourant.setKeyCode(e.getKeyCode());
                
                attenteTouche = false;
            }
        }

        @Override
        public void keyTyped(KeyEvent e){}
    }
 
    
    private static class Panel_OptionsSon extends JPanel implements ActionListener, ChangeListener
    {
        private static final long serialVersionUID = 1L;
        private Panel_Table pFormulaire = new Panel_Table();
        private JButton bSonActif = new JButton(Language.getTexte(Language.ID_TXT_OUI));
        private JSlider sVolumeSon = new JSlider(0,100); // %
        
        public Panel_OptionsSon()
        {
            setBackground(LookInterface.COULEUR_DE_FOND_PRI);
            
            pFormulaire.setOpaque(false);
            
            JLabel lActif = new JLabel(Language.getTexte(Language.ID_TXT_ACTIF)+" ?");
            lActif.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            
            pFormulaire.add(lActif,0,0);
            pFormulaire.add(bSonActif,1,0);
            
            
            sVolumeSon.setValue(SoundManagement.getVolumeSysteme());
            JLabel lVolume = new JLabel(Language.getTexte(Language.ID_TXT_VOLUME));
            lVolume.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            pFormulaire.add(lVolume,0,1);
            pFormulaire.add(sVolumeSon,1,1);

            
            bSonActif.addActionListener(this);
            sVolumeSon.addChangeListener(this);
            
            if(SoundManagement.isVolumeMute())
                bSonActif.setText(Language.getTexte(Language.ID_TXT_NON));
            else
                bSonActif.setText(Language.getTexte(Language.ID_TXT_OUI));

            add(pFormulaire); 
        }

        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            if(SoundManagement.isVolumeMute())
            {
                bSonActif.setText(Language.getTexte(Language.ID_TXT_OUI));
                SoundManagement.setVolumeMute(false);
            }
            else
            {
                bSonActif.setText(Language.getTexte(Language.ID_TXT_NON));
                SoundManagement.setVolumeMute(true);
            }
        }

        @Override
        public void stateChanged(ChangeEvent arg0)
        {
            SoundManagement.setVolumeSysteme(sVolumeSon.getValue());
        }
    }
    
    private static class Panel_OptionsStyle extends JPanel implements ActionListener
    {
        private static final long serialVersionUID = 1L;
        private Panel_Table pFormulaire = new Panel_Table();
        private JButton bCouleurDeFond_Pri = new JButton();
        private JButton bCouleurTexte_Pri = new JButton();
        
        private JButton bCouleurDeFond_Sec = new JButton();
        private JButton bCouleurTexte_Sec = new JButton();
        
        private JButton bCouleurDeFond_Boutons = new JButton();
        private JButton bCouleurTexte_Boutons = new JButton();
        
        private JButton bReinitialiser = new JButton(Language.getTexte(Language.ID_TXT_BTN_REINITIALISER));
        
        private JLabel lCouleurDeFondPri = new JLabel(Language.getTexte(Language.ID_TXT_COULEUR_DE_FOND_PRI));
        private JLabel lCouleurTxtPri = new JLabel(Language.getTexte(Language.ID_TXT_COULEUR_TXT_PRI));
        private JLabel lCouleurDeFondSec = new JLabel(Language.getTexte(Language.ID_TXT_COULEUR_DE_FOND_SEC));
        private JLabel lCouleurTxtSec = new JLabel(Language.getTexte(Language.ID_TXT_COULEUR_TEXTE_SEC));
        private JLabel lCouleurDeFondBtn = new JLabel(Language.getTexte(Language.ID_TXT_COULEUR_DE_FOND_BTN));
        private JLabel lCouleurTxtBtn = new JLabel(Language.getTexte(Language.ID_TXT_COULEUR_TEXTE_BTN));

        
        public Panel_OptionsStyle()
        {
            setBackground(LookInterface.COULEUR_DE_FOND_PRI);
                      
            // style
            lCouleurDeFondPri.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lCouleurTxtPri.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lCouleurDeFondSec.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lCouleurTxtSec.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lCouleurDeFondBtn.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lCouleurTxtBtn.setForeground(LookInterface.COULEUR_TEXTE_PRI);
 
            
            int ln = 0;
            
            pFormulaire.setOpaque(false);
            
            pFormulaire.add(bReinitialiser,1,ln++);
            
            bCouleurDeFond_Pri.setPreferredSize(new Dimension(50,50));
            bCouleurDeFond_Pri.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
            pFormulaire.add(lCouleurDeFondPri,0,ln);
            pFormulaire.add(bCouleurDeFond_Pri,1,ln++);

            bCouleurTexte_Pri.setPreferredSize(new Dimension(50,50));
            bCouleurTexte_Pri.setBackground(LookInterface.COULEUR_TEXTE_PRI);
            pFormulaire.add(lCouleurTxtPri,0,ln);
            pFormulaire.add(bCouleurTexte_Pri,1,ln++);
            
            bCouleurDeFond_Sec.setPreferredSize(new Dimension(50,50));
            bCouleurDeFond_Sec.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
            pFormulaire.add(lCouleurDeFondSec,0,ln);
            pFormulaire.add(bCouleurDeFond_Sec,1,ln++);
  
            bCouleurTexte_Sec.setPreferredSize(new Dimension(50,50));
            bCouleurTexte_Sec.setBackground(LookInterface.COULEUR_TEXTE_SEC);
            pFormulaire.add(lCouleurTxtSec,0,ln);
            pFormulaire.add(bCouleurTexte_Sec,1,ln++);
            
            bCouleurDeFond_Boutons.setPreferredSize(new Dimension(50,50));
            bCouleurDeFond_Boutons.setBackground(LookInterface.COULEUR_DE_FOND_BTN);
            pFormulaire.add(lCouleurDeFondBtn,0,ln);
            pFormulaire.add(bCouleurDeFond_Boutons,1,ln++);

            bCouleurTexte_Boutons.setPreferredSize(new Dimension(50,50));
            bCouleurTexte_Boutons.setBackground(LookInterface.COULEUR_TEXTE_BTN);
            pFormulaire.add(lCouleurTxtBtn,0,ln);
            pFormulaire.add(bCouleurTexte_Boutons,1,ln++);
       
            bCouleurDeFond_Pri.addActionListener(this);
            bCouleurTexte_Pri.addActionListener(this);
            bCouleurDeFond_Sec.addActionListener(this);
            bCouleurTexte_Sec.addActionListener(this);
            bCouleurDeFond_Boutons.addActionListener(this);
            bCouleurTexte_Boutons.addActionListener(this);
            bReinitialiser.addActionListener(this);
            
            add(pFormulaire); 
        }

        @Override
        public void actionPerformed(ActionEvent ae)
        {
            Object src = ae.getSource();
            
            if(src == bCouleurDeFond_Pri)
            {
                Color couleur = JColorChooser.showDialog(null,
                        Language.getTexte(Language.ID_TXT_COULEUR_DE_FOND_PRI),LookInterface.COULEUR_DE_FOND_PRI);
                  
                if(couleur != null)
                {
                    LookInterface.COULEUR_DE_FOND_PRI = couleur;
                    bCouleurDeFond_Pri.setBackground(couleur);
                    Configuration.setProperty(Configuration.COULEUR_DE_FOND_P, couleur.getRGB()+"");
                }
            }
            else if(src == bCouleurTexte_Pri)
            {
                Color couleur = JColorChooser.showDialog(null,
                        Language.getTexte(Language.ID_TXT_COULEUR_TXT_PRI),LookInterface.COULEUR_TEXTE_PRI);
                  
                if(couleur != null)
                {
                    LookInterface.COULEUR_TEXTE_PRI = couleur;
                    bCouleurTexte_Pri.setBackground(couleur);
                    Configuration.setProperty(Configuration.COULEUR_TEXTE_P, couleur.getRGB()+"");
                }
            }   
            else if(src == bCouleurDeFond_Sec)
            {
                Color couleur = JColorChooser.showDialog(null,
                        Language.getTexte(Language.ID_TXT_COULEUR_DE_FOND_SEC),LookInterface.COULEUR_DE_FOND_SEC);
                  
                if(couleur != null)
                {
                    LookInterface.COULEUR_DE_FOND_SEC = couleur;
                    bCouleurDeFond_Sec.setBackground(couleur);
                    Configuration.setProperty(Configuration.COULEUR_DE_FOND_S, couleur.getRGB()+"");
                }
            }
            else if(src == bCouleurTexte_Sec)
            {
                Color couleur = JColorChooser.showDialog(null,
                        Language.getTexte(Language.ID_TXT_COULEUR_TEXTE_SEC),LookInterface.COULEUR_TEXTE_SEC);
                  
                if(couleur != null)
                {
                    LookInterface.COULEUR_TEXTE_SEC = couleur;
                    bCouleurTexte_Sec.setBackground(couleur);
                    Configuration.setProperty(Configuration.COULEUR_TEXTE_S, couleur.getRGB()+"");
                }
            }
            else if(src == bCouleurDeFond_Boutons)
            {
                Color couleur = JColorChooser.showDialog(null,
                        Language.getTexte(Language.ID_TXT_COULEUR_DE_FOND_BTN),LookInterface.COULEUR_DE_FOND_BTN);
                  
                if(couleur != null)
                {
                    LookInterface.COULEUR_DE_FOND_BTN = couleur;
                    bCouleurDeFond_Boutons.setBackground(couleur);
                    Configuration.setProperty(Configuration.COULEUR_DE_FOND_B, couleur.getRGB()+"");
                }
            } 
            else if(src == bCouleurTexte_Boutons)
            {
                Color couleur = JColorChooser.showDialog(null,
                        Language.getTexte(Language.ID_TXT_COULEUR_TEXTE_BTN),LookInterface.COULEUR_TEXTE_BTN);
                  
                if(couleur != null)
                {
                    LookInterface.COULEUR_TEXTE_BTN = couleur;
                    bCouleurTexte_Boutons.setBackground(couleur);
                    Configuration.setProperty(Configuration.COULEUR_TEXTE_B, couleur.getRGB()+"");
                } 
            }
            else if(src == bReinitialiser)
            {
                // COULEUR_DE_FOND_PRI
                LookInterface.COULEUR_DE_FOND_PRI = LookInterface.DEF_COULEUR_DE_FOND_PRI;
                bCouleurDeFond_Pri.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
                Configuration.setProperty(Configuration.COULEUR_DE_FOND_P, LookInterface.COULEUR_DE_FOND_PRI.getRGB()+"");
                
                // COULEUR_TEXTE_PRI
                LookInterface.COULEUR_TEXTE_PRI = LookInterface.DEF_COULEUR_TEXTE_PRI;
                bCouleurTexte_Pri.setBackground(LookInterface.COULEUR_TEXTE_PRI);
                Configuration.setProperty(Configuration.COULEUR_TEXTE_P, LookInterface.COULEUR_TEXTE_PRI.getRGB()+"");   
             
                // COULEUR_DE_FOND_SEC
                LookInterface.COULEUR_DE_FOND_SEC = LookInterface.DEF_COULEUR_DE_FOND_SEC;
                bCouleurDeFond_Sec.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
                Configuration.setProperty(Configuration.COULEUR_DE_FOND_S, LookInterface.COULEUR_DE_FOND_SEC.getRGB()+"");
                
                // COULEUR_TEXTE_SEC
                LookInterface.COULEUR_TEXTE_SEC = LookInterface.DEF_COULEUR_TEXTE_SEC;
                bCouleurTexte_Sec.setBackground(LookInterface.COULEUR_TEXTE_SEC);
                Configuration.setProperty(Configuration.COULEUR_TEXTE_S, LookInterface.COULEUR_TEXTE_SEC.getRGB()+"");   

                // COULEUR_DE_FOND_BTN
                LookInterface.COULEUR_DE_FOND_BTN = LookInterface.DEF_COULEUR_DE_FOND_BTN;
                bCouleurDeFond_Boutons.setBackground(LookInterface.COULEUR_DE_FOND_BTN);
                Configuration.setProperty(Configuration.COULEUR_DE_FOND_B, LookInterface.COULEUR_DE_FOND_BTN.getRGB()+"");  
            
                // COULEUR_TEXTE_BTN
                LookInterface.COULEUR_TEXTE_BTN = LookInterface.DEF_COULEUR_TEXTE_BTN;
                bCouleurTexte_Boutons.setBackground(LookInterface.COULEUR_TEXTE_BTN);
                Configuration.setProperty(Configuration.COULEUR_TEXTE_B, LookInterface.COULEUR_TEXTE_BTN.getRGB()+"");      
            }  
        }
    }
    
    
    private static final ImageIcon I_FENETRE = new ImageIcon("img/icones/wrench.png");
    private static final ImageIcon I_JOUEUR = new ImageIcon("img/icones/user_red.png");
    private static final ImageIcon I_CMD = new ImageIcon("img/icones/keyboard.png");
    private static final ImageIcon I_SON = new ImageIcon("img/icones/sound.png");
    private static final ImageIcon I_RESEAU = new ImageIcon("img/icones/connect.png");
    private static final ImageIcon I_STYLE = new ImageIcon("img/icones/palette.png");
    
    private JTabbedPane onglets;
    private JButton bFermer = new JButton(Language.getTexte(Language.ID_TXT_BTN_FERMER));

    public View_Options()
    {
        super("Options");
        setIconImage(I_FENETRE.getImage());
        setLayout(new BorderLayout());
        setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        
        // titre
        JPanel pTop = new JPanel(new BorderLayout());
        pTop.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
        pTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitre = new JLabel("OPTIONS");
        lblTitre.setForeground(LookInterface.COULEUR_TEXTE_SEC);
        lblTitre.setFont(ManageFonts.POLICE_TITRE);
        
        pTop.add(lblTitre, BorderLayout.NORTH);
        add(pTop, BorderLayout.NORTH);
        
        // onglets
        
        // Background
        onglets = new JTabbedPane();
 
        UIManager.put("TabbedPane.tabAreaBackground", LookInterface.COULEUR_DE_FOND_PRI);
        //SwingUtilities.updateComponentTreeUI(onglets);
        
        onglets.setFocusable(false); // pour keylistener dans option commande
        onglets.setOpaque(true);
        onglets.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
        Panel_OptionsJeu panelOptionsJeu = new Panel_OptionsJeu(); 
        Panel_OptionsReseau panelOptionsReseau = new Panel_OptionsReseau();
        Panel_OptionsCommandes panelOptionsCommandes = new Panel_OptionsCommandes();
        Panel_OptionsSon panelOptionsSon = new Panel_OptionsSon();
        Panel_OptionsStyle panelOptionsStyle = new Panel_OptionsStyle();
        
        
        
        onglets.addTab(Language.getTexte(Language.ID_TXT_JOUEUR)+"  ", I_JOUEUR, panelOptionsJeu);
        onglets.addTab(Language.getTexte(Language.ID_TXT_COMMANDES)+"  ", I_CMD, new JScrollPane(panelOptionsCommandes));
        onglets.addTab(Language.getTexte(Language.ID_TXT_BTN_SON)+"  ", I_SON, panelOptionsSon);
        onglets.addTab(Language.getTexte(Language.ID_TXT_RESEAU)+"  ", I_RESEAU, panelOptionsReseau);
        onglets.addTab(Language.getTexte(Language.ID_TXT_STYLE)+"  ", I_STYLE, new JScrollPane(panelOptionsStyle));
        
        
        add(onglets,BorderLayout.CENTER);
        
        // boutons
        bFermer.addActionListener(this);
        
        JPanel pBoutons = new JPanel();
        pBoutons.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
        
        //pBoutons.add(bValider);
        pBoutons.add(bFermer);
        add(pBoutons,BorderLayout.SOUTH);
        
        setBounds(0, 0, 400, 500);
        setLocationRelativeTo(null);
        setVisible(true);  
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        dispose();
    }
}
