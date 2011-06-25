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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


import models.utils.Tools;


public class View_ChooseLanguage extends JDialog implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private final int MARGES_PANEL = 40;
    private final int MARGES_DRAPEAUX = 20;
    private static final ImageIcon I_FR     = new ImageIcon("lang/fr_FR.jpg");
    private static final ImageIcon I_EN    = new ImageIcon("lang/en_EN.png");
    
    private JButton bFR = new JButton("Français",new ImageIcon(Tools.redimentionner(I_FR.getImage(), 100, 66)));
    private JButton bEN = new JButton("English",new ImageIcon(Tools.redimentionner(I_EN.getImage(), 100, 66)));
    private JButton bQuitter = new JButton("Quit");
    
    private JLabel lblTitre = new JLabel("Select your language");
    private static final ImageIcon I_FENETRE = new ImageIcon(
    "img/icones/icone_pgm.png");
    
    public View_ChooseLanguage()
    {
        super((Frame) null, true);
        setIconImage(I_FENETRE.getImage());
        setResizable(false);
        
        
        JPanel pForm = new JPanel(new BorderLayout());
        
        setContentPane(pForm);
        
        
        setTitle("Select your language - ASD Tower Defense");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        pForm.setBorder(new EmptyBorder(new Insets(MARGES_PANEL, MARGES_PANEL,
                MARGES_PANEL, MARGES_PANEL)));
        
        pForm.setBackground(LookInterface.COULEUR_DE_FOND_PRI);

        JPanel pDrapeaux = new JPanel(new FlowLayout());
        pDrapeaux.setOpaque(false);
        
        lblTitre.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lblTitre.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        pForm.add(lblTitre,BorderLayout.NORTH);
        
        ManageFonts.setStyle(bFR);
        ManageFonts.setStyle(bEN);
        ManageFonts.setStyle(bQuitter);
        
        pDrapeaux.add(bFR);
        pDrapeaux.add(bEN);
        
        bFR.setBorder(new EmptyBorder(new Insets(MARGES_DRAPEAUX, MARGES_DRAPEAUX,
                MARGES_DRAPEAUX / 2, MARGES_DRAPEAUX)));
        bEN.setBorder(new EmptyBorder(new Insets(MARGES_DRAPEAUX, MARGES_DRAPEAUX,
                MARGES_DRAPEAUX / 2, MARGES_DRAPEAUX)));
        
        bFR.setVerticalTextPosition(SwingConstants.BOTTOM);
        bEN.setVerticalTextPosition(SwingConstants.BOTTOM);
        
        bFR.setHorizontalTextPosition(SwingConstants.CENTER);
        bEN.setHorizontalTextPosition(SwingConstants.CENTER);
        
        pForm.add(pDrapeaux,BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.setOpaque(false);
        p2.add(bQuitter,BorderLayout.CENTER);
        
        pForm.add(p2,BorderLayout.SOUTH);
        
        
        bFR.addActionListener(this);
        bEN.addActionListener(this);
        bQuitter.addActionListener(this);
        
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == bFR)
        {
            Language.initaliser("lang/fr_FR.json");
            dispose();
        }
        else if(src == bEN)
        {
            Language.initaliser("lang/en_EN.json");
            dispose();
        } 
        else
        {
            System.exit(0);
        }
    }
}
