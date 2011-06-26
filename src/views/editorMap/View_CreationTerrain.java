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

package views.editorMap;

import i18n.Language;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import views.View_MenuPrincipal;
import views.LookInterface;
import views.common.EcouteurDePanelTerrain;
import views.common.View_HTML;
import views.single.View_JeuSolo;
import exceptions.*;
import models.creatures.Creature;
import models.game.Game;
import models.game.Game_Single;
import models.map.Field;
import models.player.PlayerLocation;
import models.player.Team;
import models.player.Player;
import models.towers.Tower;
import models.utils.SoundManagement;

/**
 * Fenetre de creation et d'edition de terrain de jeu.
 * 
 * Cette fenetre permet de creer des terrains de jeu. Un terrain de jeu est 
 * compose d'une zone de jeu et d'equipes.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juillet 2010
 * @since jdk1.6.0_16
 * @see Field
 */
public class View_CreationTerrain extends    JFrame 
                                     implements EcouteurDePanelTerrain, 
                                                EcouteurDePanelCreationTerrain, 
                                                ActionListener
{
    private static final long serialVersionUID = 1L;
    
    // Images
    private static final ImageIcon I_FENETRE = new ImageIcon("img/icones/map_edit.png");
    private static final ImageIcon I_MAIN = new ImageIcon("img/icones/hand.png");
    private static final ImageIcon I_MURS = new ImageIcon("img/icones/shape_square_edit.png");
    private static final ImageIcon I_TESTER = new ImageIcon("img/icones/cog.png");
    private static final ImageIcon I_ENREGISTRER = new ImageIcon("img/icones/disk.png");
    private static final ImageIcon I_ENREGISTRER_SOUS = new ImageIcon("img/icones/disk_multiple.png");
    private static final ImageIcon I_NOUVEAU = new ImageIcon("img/icones/page_white_star.png");
    private static final ImageIcon I_OUVRIR = new ImageIcon("img/icones/folder_explore.png");
    private static final ImageIcon I_SUPPRIMER = new ImageIcon("img/icones/shape_square_delete.png");
    private static final ImageIcon I_QUITTER = new ImageIcon("img/icones/door_out.png");
    private static final ImageIcon I_MAISON = new ImageIcon("img/icones/application_home.png");
    private static final ImageIcon I_AIDE = new ImageIcon("img/icones/help.png");
    
    // Boutons
    private JButton bMain           = new JButton(I_MAIN);
    private JButton bMurs           = new JButton(I_MURS);
    private JButton bNouveau        = new JButton(I_NOUVEAU);
    private JButton bOuvrir         = new JButton(I_OUVRIR);
    private JButton bEnregistrer    = new JButton(I_ENREGISTRER);
    private JButton bSupprimer      = new JButton(I_SUPPRIMER);
    private JButton bTester         = new JButton(I_TESTER);
    
    // Style
    private final Insets INSETS     = new Insets(5, 5, 5, 5);
    private final Color C_BTN_SEL   = LookInterface.COULEUR_DE_FOND_SEC;
     
    /**
     * Panel de creation du terrain
     */
    private Panel_CreationTerrain panelCreationTerrain;
    
    /**
     * Panel de gestion des options du terrain
     */
    private Panel_OptionsTerrain panelOptionsTerrain;
    
    /**
     * Panel de creation des equipes
     */
    private Panel_CreationEquipes panelCreationEquipes;
    
    // Menu
    private final JMenuBar  menuPrincipal   = new JMenuBar();
    private final JMenu     menuFichier     = new JMenu(Language.getTexte(Language.ID_TXT_BTN_FICHIER));
    private final JMenu     menuEdition     = new JMenu(Language.getTexte(Language.ID_TXT_BTN_EDITION));
    private final JMenu     menuAide        = new JMenu(Language.getTexte(Language.ID_TXT_BTN_AIDE));
    private final JMenuItem itemNouveau      = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_NOUVEAU),I_NOUVEAU);
    private final JMenuItem itemOuvrir      = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_OUVRIR)+"..",I_OUVRIR);
    private final JMenuItem itemEnregistrer = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_ENREGISTRER),I_ENREGISTRER);
    private final JMenuItem itemEnregistrerSous = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_ENREGISTRER_SOUS)+"...",I_ENREGISTRER_SOUS);
    private final JMenuItem itemQuitter      = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_QUITTER),I_QUITTER);
    private final JMenuItem itemMenuPrincipal      = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_RETOUR_MENU_P),I_MAISON);
    private final JMenuItem itemTester      = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_TESTER),I_TESTER);
    
    // TODO Traduire
    private final JMenuItem itemCommentUtiliserLEditeur      = new JMenuItem("Comment marche l'éditeur ?",I_AIDE);
    
    
    /**
     * Le jeu
     */
    private Game jeu;
    
    /**
     * Le fichier map en cours de traitement
     */
    private File fichierCourant;
    
    /**
     * Etat du fichier
     */
    //private boolean sauve = false;
    
    /**
     * Etat de l'editeur
     */
    private JLabel lblEtat = new JLabel(Language.getTexte(Language.ID_TXT_PRET));
    
    // Chercheurs de fichiers
    private JFileChooser fcOuvrir = new JFileChooser("./maps");
    private JFileChooser fcSauver = new JFileChooser("./maps");
    private static FileFilter filtreFichier = new FileFilter()
    {
        public String getDescription()
        {
            return "Terrain ."+Field.EXTENSION_FICHIER;
        }
        
        public boolean accept(File file)
        {
            if(file.isDirectory()) 
                 return true; 

            return file.getName().toLowerCase().endsWith("."+Field.EXTENSION_FICHIER);
        }
    };
    
    /**
     * Constructeur
     */
    public View_CreationTerrain()
    {
        super(Language.getTexte(Language.ID_TITRE_EDITEUR_DE_TERRAIN));
        
        setIconImage(I_FENETRE.getImage());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // creation du jeu
        jeu = new Game_Single();
        jeu.setField(new Field(jeu));
        
        // selectionneur de fichiers
        fcSauver.addChoosableFileFilter(filtreFichier);   
        fcSauver.setDialogType(JFileChooser.SAVE_DIALOG);
        fcSauver.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fcOuvrir.addChoosableFileFilter(filtreFichier);   
        fcOuvrir.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // menu
        itemNouveau.addActionListener(this);
        itemOuvrir.addActionListener(this);
        itemEnregistrer.addActionListener(this);
        itemEnregistrerSous.addActionListener(this);
        itemMenuPrincipal.addActionListener(this);
        itemQuitter.addActionListener(this);
        itemTester.addActionListener(this);
        itemCommentUtiliserLEditeur.addActionListener(this);
        
        // Raccourcis clavier
        itemNouveau.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, Event.CTRL_MASK));
        itemOuvrir.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, Event.CTRL_MASK));
        itemEnregistrer.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, Event.CTRL_MASK));
        itemQuitter.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_W, Event.CTRL_MASK));
        itemTester.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, Event.CTRL_MASK));
        itemCommentUtiliserLEditeur.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        
        menuFichier.add(itemNouveau);
        menuFichier.add(itemOuvrir);
        menuFichier.add(itemEnregistrer);
        menuFichier.add(itemEnregistrerSous);
        menuFichier.addSeparator();
        menuFichier.add(itemMenuPrincipal);
        menuFichier.add(itemQuitter);
        menuEdition.add(itemTester);
        menuPrincipal.add(menuFichier);
        menuPrincipal.add(menuEdition);
        menuPrincipal.add(menuAide);
        menuAide.add(itemCommentUtiliserLEditeur);
        
        setJMenuBar(menuPrincipal);
        
        // barre d'outils
        JToolBar tbPrincipale = new JToolBar();
        
        tbPrincipale.add(bNouveau);
        tbPrincipale.add(bOuvrir);
        tbPrincipale.add(bEnregistrer);
        tbPrincipale.addSeparator();
        tbPrincipale.add(bMain);
        tbPrincipale.add(bMurs);
        activerBouton(bMurs);
        tbPrincipale.addSeparator();
        tbPrincipale.add(bSupprimer);
        tbPrincipale.addSeparator();
        tbPrincipale.add(bTester);
        
        bNouveau.addActionListener(this);
        bOuvrir.addActionListener(this);
        bEnregistrer.addActionListener(this);
        bMain.addActionListener(this);
        bMurs.addActionListener(this);
        bSupprimer.addActionListener(this);
        bTester.addActionListener(this);
 
        bNouveau.setToolTipText(Language.getTexte(Language.ID_TXT_BTN_NOUVEAU));
        bOuvrir.setToolTipText(Language.getTexte(Language.ID_TXT_BTN_OUVRIR)+"...");
        bEnregistrer.setToolTipText(Language.getTexte(Language.ID_TXT_BTN_ENREGISTRER));
        bMain.setToolTipText("Déplacements");
        bMurs.setToolTipText("Edition de zone");
        bSupprimer.setToolTipText("Supprimer la zone sélectionnée");
        bTester.setToolTipText(Language.getTexte(Language.ID_TXT_BTN_TESTER));
        
        add(tbPrincipale,BorderLayout.NORTH);
        
        // Onglets de droits
        JTabbedPane panelOnglets = new JTabbedPane();
        
        // Background
        UIManager.put("TabbedPane.tabAreaBackground", LookInterface.COULEUR_DE_FOND_PRI);
        //SwingUtilities.updateComponentTreeUI(panelSelectionEtVague);
     
        panelOnglets.setOpaque(true);
        //panelOnglets.setPreferredSize(new Dimension(300,420));
        panelOnglets.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
        
        panelCreationTerrain = new Panel_CreationTerrain(jeu,this);
        
        JPanel panelConteneurPCT = new JPanel(new BorderLayout());
        panelConteneurPCT.setBorder(new EmptyBorder(10,10,10,10));
        panelConteneurPCT.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
        panelConteneurPCT.add(panelCreationTerrain,BorderLayout.CENTER);
        
        panelOptionsTerrain = new Panel_OptionsTerrain(jeu);
        panelCreationEquipes = new Panel_CreationEquipes(jeu, panelCreationTerrain);
        
        panelOnglets.add(Language.getTexte(Language.ID_TXT_PROPRIETES),panelOptionsTerrain );
        panelOnglets.add(Language.getTexte(Language.ID_TXT_EQUIPES),panelCreationEquipes);
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(panelOnglets,BorderLayout.CENTER);
        add(p,BorderLayout.EAST);
        
        panelCreationTerrain.setEcouteurDeCreationTerrain(this);
        panelCreationTerrain.basculeraffichageZonesDepartArrivee();
        add(panelConteneurPCT,BorderLayout.CENTER);
        
        add(lblEtat,BorderLayout.SOUTH);
        
        //nouveauTerrain();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public View_CreationTerrain(Field terrain,File fichierTerrain) {
        this();
        
        fichierCourant = fichierTerrain;
        changerTerrain(terrain);
    }

    /**
     * On peut aussi lancer seulement l'editeur de terrain. (debug)
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        // essaye de mettre le nouveau look and feel "Nimbus" fourni par Java
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
            if ("Nimbus".equals(laf.getName())) 
                try 
                {
                    UIManager.setLookAndFeel(laf.getClassName());
                } 
                catch (Exception e) 
                {
                    /* 
                     * On fait rien, c'est pas grave. 
                     * C'est juste le look and feel qui n'est pas installe.
                     */ 
                } 
        
        // Anglais uniquement
        Language.initaliser("lang/en_EN.json");
                      
        new View_CreationTerrain();
    }

    @Override
    public void acheterTour(Tower tour){}

    @Override
    public void ajouterInfoVagueSuivanteDansConsole(){}

    @Override
    public void ameliorerTour(Tower tour){}

    @Override
    public void creatureSelectionnee(Creature creature){}

    @Override
    public void deselection(){}

    @Override
    public void lancerVagueSuivante(){}

    @Override
    public void miseAJourInfoJeu(){}

    @Override
    public void setTourAAcheter(Tower tour){}

    @Override
    public void tourSelectionnee(Tower tour, int mode){}

    @Override
    public void vendreTour(Tower tour){}

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == bNouveau || src == itemNouveau)
            nouveauTerrain();
        else if(src == bOuvrir || src == itemOuvrir)
            ouvrirTerrain();
        else if(src == bEnregistrer || src == itemEnregistrer)
            enregistrerTerrain();
        else if(src == itemEnregistrerSous)
            enregistrerTerrainSous();
        else if(src == itemQuitter)
            System.exit(0);
        else if(src == itemMenuPrincipal)
        {
            new View_MenuPrincipal();
            dispose();
        }
        else if(src == bMain)
        {
            bMain.setBorder(new LineBorder(C_BTN_SEL,2));
            bMurs.setBorder(new EmptyBorder(INSETS));
            
            panelCreationTerrain.activerModeDeplacement();
        }
        else if(src == bMurs)
        {
            bMurs.setBorder(new LineBorder(C_BTN_SEL,2));
            bMain.setBorder(new EmptyBorder(INSETS));
            
            panelCreationTerrain.activerModeCreationMurs();
        }
        else if(src == bSupprimer)
        {
            jeu.getTerrain().supprimerMur(panelCreationTerrain.getRecEnTraitement());
            panelCreationTerrain.deselectionnerRecEnTraitement(); 
        }
        else if(src == bTester || src == itemTester)
            tester();
        else if(src == itemCommentUtiliserLEditeur)
            // TODO Traduire
            new View_HTML("Aide", new File("donnees/aide/editeurDeTerrains/aide_editeurDeTerrain_en.html"), this);
    }
    
    
    private void activerBouton(JButton b)
    {
        bMurs.setBorder(new EmptyBorder(INSETS));
        bMain.setBorder(new EmptyBorder(INSETS));
       
        b.setBorder(new LineBorder(C_BTN_SEL,2));
    }

    private void nouveauTerrain()
    {
        Field t = new Field(jeu);
        jeu.setField(t);
        
        // creation de la premiere equipe
        Team equipe = new Team(1, Language.getTexte(Language.ID_TXT_EQUIPE)+" 1", Color.BLACK);
        equipe.ajouterZoneDepart(new Rectangle(40,40,40,40));
        equipe.setZoneArrivalCreatures(new Rectangle(140,140,40,40));
        equipe.addPlayerLocation(new PlayerLocation(1, new Rectangle(0,0,jeu.getTerrain().getLargeur(),jeu.getTerrain().getHauteur())));
        jeu.ajouterEquipe(equipe);
        
        fichierCourant = null;
        
        panelCreationTerrain.deselectionnerRecEnTraitement();
        panelOptionsTerrain.miseAJour();
        panelCreationEquipes.miseAJour();
        
        // TODO Traduire
        lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
        lblEtat.setText("Fichier chargé");
    }

    private void enregistrerTerrainSous()
    {
        // Save as...
        if (fcSauver.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
        {
            fichierCourant = fcSauver.getSelectedFile();
            
            // rename if extension not wrote
            if(!fichierCourant.getName().toLowerCase().endsWith("."+Field.EXTENSION_FICHIER))
                fichierCourant = new File(fichierCourant.getAbsolutePath()+"."+Field.EXTENSION_FICHIER);
        
            try
            {
                jeu.getTerrain().setLargeurMaillage(jeu.getTerrain().getLargeur());
                jeu.getTerrain().setHauteurMaillage(jeu.getTerrain().getHauteur());
                
                Field.serialiser(jeu.getTerrain(),fichierCourant/*new File("maps/"+jeu.getTerrain().getNom()+"."+Terrain.EXTENSION_FICHIER)*/);
            } 
            catch (IOException e)
            {
                // TODO Traduire
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText("Erreur lors de la sauvegarde!");
            }
        }
    }

    /**
     * Permet de tester le terrain courant
     */
    private void tester()
    {
        try
        {
            // sauvegarde et chargement du terrain
            enregistrerTerrain();
            
            // si la sauvegarde a joué (si première sauvegarde)
            if(fichierCourant != null)
            {
                Field t = Field.charger(fichierCourant);
                
                Game j = new Game_Single();
                j.setField(t);
                t.setJeu(j);
                
                Player joueur = new Player("Joueur Test");
                j.setKeyPlayer(joueur);
                j.ajouterJoueur(joueur);
     
                t.initialize();
                j.initialize();
                
                new View_JeuSolo(j)
                {
                    private static final long serialVersionUID = 35425L;

                    /**
                     * Permet de retourner au menu principal
                     */
                    protected void retourAuMenuPrincipal()
                    {
                        SoundManagement.arreterTousLesSons();
                         
                        dispose(); // destruction de la fenetre
                        System.gc(); // passage du remasse miette
                    }
                    
                    protected void quitter()
                    {
                        // FIXME fait planter l'éditeur après fermeture
                        //jeu.terminer();
                        //jeu.detruire();
                        dispose();
                    } 
                };
            }
        }
        catch (ClassCastException e1)
        {
            // TODO Traduire
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText("Fichier invalide");
        } 
        catch (IOException e1)
        {
            // TODO Traduire
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText("Fichier invalide");
        } 
        catch (ClassNotFoundException e1)
        {
            // TODO Traduire
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText("Fichier invalide");
        }
        catch (CurrentGameException e1)
        {
            // TODO Traduire
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText("Le jeu est en cours!");
        } 
        catch (NoPositionAvailableException e1)
        {
            // TODO Traduire
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText("Il n'y a aucun emplacement de joueur!");
        }
    }
    
    /**
     * Permet de sauvegarder le Terrain en traitement
     */
    private void enregistrerTerrain()
    {
        if(fichierCourant == null)
        {
            // Save as...
            if (fcSauver.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
            {
                fichierCourant = fcSauver.getSelectedFile();
                
                // rename if extension not wrote
                if(!fichierCourant.getName().toLowerCase().endsWith("."+Field.EXTENSION_FICHIER))
                    fichierCourant = new File(fichierCourant.getAbsolutePath()+"."+Field.EXTENSION_FICHIER);
            }
        }
        
        if(fichierCourant != null)
        {
            try
            {
                jeu.getTerrain().setLargeurMaillage(jeu.getTerrain().getLargeur());
                jeu.getTerrain().setHauteurMaillage(jeu.getTerrain().getHauteur());
                
                Field.serialiser(jeu.getTerrain(),fichierCourant/*new File("maps/"+jeu.getTerrain().getNom()+"."+Terrain.EXTENSION_FICHIER)*/);
            
                // TODO Traduire
                lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
                lblEtat.setText("Fichier correctement sauvegardé!");
            } 
            catch (IOException e)
            {
                e.printStackTrace();
                
                // TODO Traduire
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText("Erreur lors de la sauvegarde!");
            } 
        }
    }

    private void changerTerrain(Field t)
    {
        jeu.setField(t);
        t.setJeu(jeu);
        
        panelCreationTerrain.deselectionnerRecEnTraitement();
        panelOptionsTerrain.miseAJour();
        panelCreationEquipes.miseAJour();
        
        // TODO Traduire
        lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
        lblEtat.setText("Fichier chargé");
    }
    
    
    /**
     * Permet d'ouvir un Terrain sérialisé
     */
    private void ouvrirTerrain()
    {
        int returnVal = fcOuvrir.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fichier = fcOuvrir.getSelectedFile();
            
            try
            {
                Field t = Field.charger(fichier);
                
                fichierCourant = fichier;
                
                changerTerrain(t);
            } 
            catch (ClassCastException e1)
            {
                // TODO Traduire
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText("Fichier invalide");
            } 
            catch (IOException e1)
            {
                // TODO Traduire
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText("Fichier invalide");
            } 
            catch (ClassNotFoundException e1)
            {
                // TODO Traduire
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText("Fichier invalide");
            }
        }
    }

    @Override
    public void zoneModifiee(Rectangle zone)
    {
        lblEtat.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        lblEtat.setText("x:"+zone.x+" y:"+zone.y+" w:"+zone.width+" h:"+zone.height); 
    }

    @Override
    public void zoneSelectionnee(Rectangle zone)
    {
        if(zone == null)
            lblEtat.setText(" ");
        else
        {
            lblEtat.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lblEtat.setText("x:"+zone.x+" y:"+zone.y+" w:"+zone.width+" h:"+zone.height);
        }
    }
}
