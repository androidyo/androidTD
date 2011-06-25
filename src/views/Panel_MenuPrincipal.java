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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import views.common.View_HTML;
import views.editorMap.Panel_PartiePersonnalisee;
import views.online.Panel_CreerPartieMulti;
import views.online.Panel_RejoindrePartieMulti;
import views.single.Panel_ModeSingle;
import models.game.Game;

/**
 * 游戏的主菜单.单机与联机游戏模式的选择
 * 
 */
public class Panel_MenuPrincipal extends JPanel implements ActionListener
{
    // static constants
    private final int MARGES_PANEL = 40;
    private static final long serialVersionUID = 1L;
    private static final Image IMAGE_DE_FOND = Toolkit.getDefaultToolkit().getImage("img/interfaces/menuPrincipal.png");
    
    // elements of the form
    private JLabel version;
    // 左边游戏模式选择按钮
    private JButton bPartieSolo = new JButton(Language.getTexte(Language.ID_TXT_BTN_SOLO));
    private JButton bRejoindrePartieMulti = new JButton(Language.getTexte(Language.ID_TXT_BTN_REJOINDRE));
    private JButton bCreerPartieMulti = new JButton(Language.getTexte(Language.ID_TXT_BTN_CREER));
    private JButton bPartiePerso = new JButton(Language.getTexte(Language.ID_TXT_BTN_VOS_PARTIES));
    // 右边其它按钮
    private JButton bRegles = new JButton(Language.getTexte(Language.ID_TXT_BTN_REGLES));
    private JButton bAPropos = new JButton(Language.getTexte(Language.ID_TXT_BTN_A_PROPOS));
    private JButton bOptions = new JButton(Language.getTexte(Language.ID_TXT_BTN_OPTIONS));
    private JButton bQuitter = new JButton(Language.getTexte(Language.ID_TXT_BTN_QUITTER));
    
    private JLabel lblReseau = new JLabel(Language.getTexte(Language.ID_TITRE_RESEAU)+" \"beta\"");
    
    
    private JFrame parent;

    /**
     * Constructeur de la fenetre du menu principal
     */
    public Panel_MenuPrincipal(JFrame parent)
    {
        super(new BorderLayout());
        this.parent = parent;

        // -------------------------------
        // -- preferances de le fenetre --
        // -------------------------------
        parent.setTitle(Language.getTexte(Language.ID_TITRE_MENU_PRINCIPAL)+" - ASD Tower Defense");

        setBorder(new EmptyBorder(new Insets(MARGES_PANEL, MARGES_PANEL,
                MARGES_PANEL, MARGES_PANEL)));
        
        
        // attent que toutes les images soit complementements chargees
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(IMAGE_DE_FOND, 0);

        try { 
            tracker.waitForAll(); 
        } 
        catch (InterruptedException e){ 
            e.printStackTrace(); 
        }
        
        // ---------------------------
        // -- element du formulaire --
        // ---------------------------

        setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(1,280));
        
        add(p, BorderLayout.NORTH);

        
        JPanel pAbsolu = new JPanel(null); // layout absolu
        pAbsolu.setPreferredSize(new Dimension(0, 160));
        pAbsolu.setOpaque(false);

        // partie solo
        bPartieSolo.addActionListener(this);
        bPartieSolo.setBounds(50, 0, 100, 50);
        parent.getRootPane().setDefaultButton(bPartieSolo); // def button
        ManageFonts.setStyle(bPartieSolo);  
        pAbsolu.add(bPartieSolo);
        
        bPartiePerso.addActionListener(this);
        bPartiePerso.setBounds(160, 0, 100, 50); 
        ManageFonts.setStyle(bPartiePerso);  
        pAbsolu.add(bPartiePerso);
        
        // partie multijoueurs
        lblReseau.setBounds(53, 60, 200, 50);
        lblReseau.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lblReseau.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        pAbsolu.add(lblReseau);
        
        bRejoindrePartieMulti.setBounds(50, 100, 100, 50);
        bRejoindrePartieMulti.addActionListener(this);
        ManageFonts.setStyle(bRejoindrePartieMulti);
        pAbsolu.add(bRejoindrePartieMulti);

        bCreerPartieMulti.setBounds(160, 100, 100, 50);
        bCreerPartieMulti.addActionListener(this);
        ManageFonts.setStyle(bCreerPartieMulti);
        pAbsolu.add(bCreerPartieMulti);

        // Regles
        bRegles.addActionListener(this);
        bRegles.setBounds(555, 0, 100, 25);
        ManageFonts.setStyle(bRegles);
        pAbsolu.add(bRegles);

        // A propos
        bAPropos.addActionListener(this);
        bAPropos.setBounds(555, 30, 100, 25);
        ManageFonts.setStyle(bAPropos);
        pAbsolu.add(bAPropos);

        // Options
        bOptions.addActionListener(this);
        bOptions.setBounds(555, 60, 100, 25);
        ManageFonts.setStyle(bOptions);
        pAbsolu.add(bOptions);
        
        // quitter
        bQuitter.addActionListener(this);
        bQuitter.setBounds(555, 100, 100, 50);
        ManageFonts.setStyle(bQuitter);
        pAbsolu.add(bQuitter);

        add(pAbsolu, BorderLayout.CENTER);

        version = new JLabel(Game.getVersion());
        version.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        add(version, BorderLayout.SOUTH);
    }
    
    public void paintComponent(Graphics g)
    {
        g.setColor(LookInterface.COULEUR_DE_FOND_PRI);
        g.fillRect(0, 0, 800, 600);
        
        g.drawImage(IMAGE_DE_FOND, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {
        Object source = ae.getSource();

        if (source == bPartieSolo)
        {//单人游戏
            parent.getContentPane().removeAll();
            // 进入单人游戏模式
            parent.getContentPane().add(new Panel_ModeSingle(parent),
                    BorderLayout.CENTER);
            parent.getContentPane().validate();
        } 
        else if (source == bPartiePerso)
        {
            parent.getContentPane().removeAll();
            parent.getContentPane().add(new Panel_PartiePersonnalisee(parent),
                    BorderLayout.CENTER);
            parent.getContentPane().validate();
        }   
        else if (source == bRejoindrePartieMulti)
        {
            parent.getContentPane().removeAll();
            parent.getContentPane().add(new Panel_RejoindrePartieMulti(parent),
                    BorderLayout.CENTER);
            parent.getContentPane().validate();
        } 
        else if (source == bCreerPartieMulti)
        {
            parent.getContentPane().removeAll();
            parent.getContentPane().add(new Panel_CreerPartieMulti(parent),
                    BorderLayout.CENTER);
            parent.getContentPane().validate();
        }
        else if(source == bRegles)
            new View_HTML(Language.getTexte(Language.ID_TXT_BTN_REGLES), new File(Language.getTexte(Language.ID_ADRESSE_REGLES_DU_JEU)), parent);
       
        else if(source == bAPropos)
            new View_HTML(Language.getTexte(Language.ID_TXT_BTN_A_PROPOS),new File(Language.getTexte(Language.ID_ADRESSE_A_PROPOS)), parent);
        
        else if(source == bOptions)
            new View_Options();
        
        else if (source == bQuitter)
            System.exit(0); // Fermeture correcte du logiciel
    }
}