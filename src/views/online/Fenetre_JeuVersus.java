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

package views.online;

import models.animations.*;
import i18n.Language;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import exceptions.*;
import utils.myTimer;
import views.View_MenuPrincipal;
import views.ManageFonts;
import views.LookInterface;
import views.common.EcouteurDePanelTerrain;
import views.common.View_HTML;
import views.common.Panel_AjoutTour;
import views.common.Panel_InfoTour;
import views.common.Panel_InfosJoueurEtPartie;
import views.common.Panel_Selection;
import views.common.Panel_Terrain;
import models.player.Team;
import models.player.Player;
import models.towers.Tower;
import models.utils.SoundManagement;
import models.utils.Tools;
import models.creatures.*;
import models.game.GameListener;
import models.game.Game_Client;
import models.game.GameMode;
import models.game.GameResult;
import net.ChannelException;
import net.game.client.ClientListener;
import net.game.server.ServerJeu;

/**
 * Fenetre princiale du jeu 1 joueur. 
 * 
 * Elle permet voir le jeu et d'interagir avec en posant des tours sur le terrain 
 * et de les gerer. Elle fournit aussi de quoi gerer les vagues d'ennemis.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 17 mai 2010
 * @since jdk1.6.0_16
 * @see JFrame
 * @see ActionListener
 */
public class Fenetre_JeuVersus extends JFrame implements ActionListener, 
                                                    GameListener,
                                                    EcouteurDeLanceurDeVagues,
                                                    EcouteurDePanelTerrain,
                                                    ClientListener,
                                                    WindowListener,
                                                    KeyListener
                                                    
{
	// constantes statiques
    private final int MARGES_PANEL = 20;
    private static final long serialVersionUID = 1L;
    private static final ImageIcon I_RETOUR = new ImageIcon("img/icones/arrow_undo.png");
	private static final ImageIcon I_QUITTER = new ImageIcon("img/icones/door_out.png");
	private static final ImageIcon I_AIDE = new ImageIcon("img/icones/help.png");
	private static final ImageIcon I_REGLES = new ImageIcon("img/icones/script.png");
	private static final ImageIcon I_ACTIF = new ImageIcon("img/icones/tick.png");
	private static final ImageIcon I_INACTIF = null;
	private static final ImageIcon I_FENETRE = new ImageIcon("img/icones/icone_pgm.png");
	private static final ImageIcon I_SON_ACTIF = new ImageIcon("img/icones/sound.png");
	private static final ImageIcon I_ENVOYER_MSG = new ImageIcon("img/icones/msg_go.png"); 
	private static final String FENETRE_TITRE = "ASD - Tower Defense";
	private static final int VOLUME_PAR_DEFAUT = 20;
	private static final int LARGEUR_MENU_DROITE = 280;
	
	
	//---------------------------
	//-- declaration des menus --
	//---------------------------
	private final JMenuBar 	menuPrincipal 	= new JMenuBar();
	private final JMenu 	menuFichier 	= new JMenu(Language.getTexte(Language.ID_TXT_BTN_FICHIER));
	private final JMenu 	menuEdition 	= new JMenu(Language.getTexte(Language.ID_TXT_BTN_EDITION));
	private final JMenu     menuSon         = new JMenu(Language.getTexte(Language.ID_TXT_BTN_SON));
	private final JMenu 	menuAide 		= new JMenu(Language.getTexte(Language.ID_TXT_BTN_AIDE));
	private final JMenuItem itemRegles      = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_REGLES)+"...",I_REGLES);
	private final JMenuItem itemAPropos	    = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_A_PROPOS)+"...",I_AIDE);

	
	
	private final JMenuItem itemActiverDesactiverSon 
	    = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_ACTIVE_DESACTIVE),I_SON_ACTIF); 
	private final JMenuItem itemAfficherRayonsPortee	    
		= new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_RAYONS_DE_PORTEE));
	private final JMenuItem itemAfficherZonesJoueurs       
	    = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_AFFICHER_ZONES_JOUEURS));
	private final JMenuItem itemQuitter	    
	    = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_QUITTER),I_QUITTER);
	private final JMenuItem itemRetourMenu  
	    = new JMenuItem(Language.getTexte(Language.ID_TXT_BTN_RETOUR_MENU_P),I_RETOUR);
	
	private JLabel lblEtat = new JLabel(" ");
	
	//----------------------------
	//-- declaration des panels --
	//----------------------------
	/**
	 * panel contenant le terrain de jeu
	 */
	private Panel_Terrain panelTerrain;
	
	// TODO commenter
	private Panel_InfosJoueurEtPartie panelInfoJoueurEtPartie;
	private Panel_Selection panelSelection;
	private Panel_AjoutTour panelAjoutTour;
	private JTabbedPane panelSelectionEtVague;
	
    /**
     * Console d'affichages
     */
    private JEditorPane taConsole = new JEditorPane("text/html","");
    private JScrollPane scrollConsole;
    private JTextField tfSaisieMsg = new JTextField();
    private JButton bEnvoyerMsg = new JButton(I_ENVOYER_MSG);
    
    
    /**
     * Formulaire principale de la fenêtre
     */
    private JPanel pFormulaire = new JPanel(new BorderLayout());
    
    /**
     * Timer pour gérer le temps de jeu
     */
    private myTimer timer = new myTimer(1000,null);
    
    /**
     * Lien vers le jeu
     */
	private Game_Client jeu;
	
	/**
	 * Panel de cröation d'une vague
	 */
    private Panel_CreationVague panelCreationVague;

	/**
	 * Constructeur de la fenetre. Creer et affiche la fenetre.
	 * 
	 * @param jeu le jeu a gerer
	 */
	public Fenetre_JeuVersus(Game_Client jeu)
	{
	    this.jeu = jeu;
	    jeu.setEcouteurDeClientJeu(this);
	    

	    //-------------------------------
		//-- preferances de le fenetre --
		//-------------------------------
		setTitle(FENETRE_TITRE);
		setIconImage(I_FENETRE.getImage());
		//setResizable(false);
		//setPreferredSize(new Dimension(1024,768));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		getContentPane().setBackground(LookInterface.COULEUR_DE_FOND_PRI);
		
		pFormulaire.setOpaque(false);
		//pFormulaire.setBackground(LookInterface.COULEUR_DE_FOND_2);
		pFormulaire.setBorder(new EmptyBorder(new Insets(MARGES_PANEL, MARGES_PANEL,
                MARGES_PANEL, MARGES_PANEL)));
		
		//--------------------
		//-- menu principal --
		//--------------------
		// menu Fichier
		menuFichier.add(itemRetourMenu);
		menuFichier.add(itemQuitter);
		menuPrincipal.add(menuFichier);
		
		// menu Edition
		menuEdition.add(itemAfficherRayonsPortee);
		menuEdition.add(itemAfficherZonesJoueurs);
		menuPrincipal.add(menuEdition);
		
		// menu Jeu
		//menuPrincipal.add(menuJeu);
		
		// menu Son
		menuSon.add(itemActiverDesactiverSon);
		menuPrincipal.add(menuSon);

		// menu Aide
		menuAide.add(itemRegles);
		menuAide.add(itemAPropos);
		menuPrincipal.add(menuAide);
		
		// ajout des ecouteurs
		itemRetourMenu.addActionListener(this);
		itemQuitter.addActionListener(this);
		itemAfficherRayonsPortee.addActionListener(this);
		itemAfficherZonesJoueurs.addActionListener(this);
		itemActiverDesactiverSon.addActionListener(this);
		itemRegles.addActionListener(this);
		itemAPropos.addActionListener(this);
		
		// ajout du menu
		setJMenuBar(menuPrincipal); 
		
		//------------------------
        //-- Eléments de gauche --
        //------------------------
		
		JPanel pGauche = new JPanel(new BorderLayout());
		pGauche.setOpaque(false);
		
		//-------------
        //-- Console --
        //-------------
		// style
        taConsole.setFont(ManageFonts.POLICE_CONSOLE);
        taConsole.setEditable(false);
        
        JPanel pConsole = new JPanel(new BorderLayout());
        pConsole.setOpaque(false);
        
        scrollConsole = new JScrollPane(taConsole);
        scrollConsole.setPreferredSize(new Dimension(0,50));
        pConsole.add(scrollConsole,BorderLayout.NORTH);
       
        // Saisie et bouton envoyer
        bEnvoyerMsg.addActionListener(this);
        getRootPane().setDefaultButton(bEnvoyerMsg); // bouton par def.
        
        JPanel pSaisieMsgEtBEnvoyer = new JPanel(new BorderLayout());
        pSaisieMsgEtBEnvoyer.setOpaque(false);
        pSaisieMsgEtBEnvoyer.add(tfSaisieMsg,BorderLayout.CENTER);
        pSaisieMsgEtBEnvoyer.add(bEnvoyerMsg,BorderLayout.EAST);
        pConsole.add(pSaisieMsgEtBEnvoyer,BorderLayout.SOUTH);
        
        pGauche.add(pConsole,BorderLayout.SOUTH);
		
		//-------------
		//-- Terrain --
		//-------------
		
        // creation des panels
		JPanel conteneurTerrain = new JPanel(new BorderLayout());
		conteneurTerrain.setBorder(new LineBorder(Color.BLACK,4));
		panelTerrain = new Panel_Terrain(jeu, this);
		panelTerrain.addKeyListener(this);
		conteneurTerrain.setOpaque(false);
		conteneurTerrain.add(panelTerrain,BorderLayout.CENTER);

	    // ajout
        JPanel pMargeTerrain = new JPanel(new BorderLayout());
        pMargeTerrain.setBorder(new EmptyBorder(5, 5, 5, 5));
        pMargeTerrain.setOpaque(false);
        pMargeTerrain.add(conteneurTerrain);
        pGauche.add(pMargeTerrain,BorderLayout.CENTER);
		
        // affichage des znoes et joueurs
        if(panelTerrain.basculerAffichageZonesJoueurs())
            itemAfficherZonesJoueurs.setIcon(I_ACTIF);
        else
            itemAfficherZonesJoueurs.setIcon(I_INACTIF);
        
        
        pFormulaire.add(pGauche,BorderLayout.CENTER);
        
        //--------------------
        //-- Menu de droite --
        //--------------------
		
        // Info jeu et joueur
		panelInfoJoueurEtPartie = new Panel_InfosJoueurEtPartie(jeu, GameMode.MODE_VERSUS);
		
		// Ajout de tour
		panelAjoutTour = new Panel_AjoutTour(jeu, this, LARGEUR_MENU_DROITE, 80);
		
		// Selection (tour et créature)
        panelSelection = new Panel_Selection(jeu, this);
        
        // Conteneur en onglets
		panelSelectionEtVague = new JTabbedPane();
		
		// Background
        UIManager.put("TabbedPane.tabAreaBackground", LookInterface.COULEUR_DE_FOND_PRI);
        //SwingUtilities.updateComponentTreeUI(panelSelectionEtVague);
     
        panelSelectionEtVague.setOpaque(true);
		panelSelectionEtVague.setPreferredSize(new Dimension(LARGEUR_MENU_DROITE,420));
		panelSelectionEtVague.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        panelSelectionEtVague.add(Language.getTexte(Language.ID_TITRE_INFO_SELECTION), panelSelection);
           
        // panel de création de vagues
        panelCreationVague = new Panel_CreationVague(jeu,jeu.getJoueurPrincipal(),this);
        JScrollPane jsCreationVague = new JScrollPane(panelCreationVague);
        jsCreationVague.setOpaque(false);
        jsCreationVague.setPreferredSize(new Dimension(LARGEUR_MENU_DROITE,300));
        panelSelectionEtVague.add(Language.getTexte(Language.ID_TXT_LANCEUR_DE_CREATURES), jsCreationVague);
        
		
		JPanel pN1 = new JPanel(new BorderLayout());
		pN1.setOpaque(false);

		pN1.add(panelInfoJoueurEtPartie,BorderLayout.NORTH);
		
		JPanel pN2 = new JPanel(new BorderLayout());
		pN2.setOpaque(false);
        pN2.add(panelAjoutTour,BorderLayout.NORTH);
		
        JPanel pN3 = new JPanel(new BorderLayout());
        pN3.setOpaque(false);
        
        JPanel pPourCentrer = new JPanel();
        pPourCentrer.setOpaque(false);
        pPourCentrer.add(lblEtat);
        
        pN3.add(pPourCentrer,BorderLayout.NORTH);
		 
        
        JPanel pN4 = new JPanel(new BorderLayout());
        pN4.setOpaque(false);
        pN4.add(panelSelectionEtVague,BorderLayout.NORTH);
        
        pN3.add(pN4,BorderLayout.CENTER);
        pN2.add(pN3,BorderLayout.CENTER);
        pN1.add(pN2,BorderLayout.CENTER);
        
       // pN2.add(lblEtat,BorderLayout.NORTH);
        
        
        
        
        /*
        JPanel ppp = new JPanel();
        ppp.setOpaque(false);
        ppp.add(lblEtat,BorderLayout.EAST);
        
        pN1.add(ppp,BorderLayout.SOUTH);*/
        
        
		pFormulaire.add(pN1,BorderLayout.EAST);
		add(pFormulaire,BorderLayout.CENTER);
		
	    //----------------------
        //-- demarrage du jeu --
        //----------------------
		// TODO faire un 5.. 4.. 3.. 2.. 1..
		
		// on demarre la musique au dernier moment
        jeu.getTerrain().demarrerMusiqueDAmbiance();
        
		jeu.demarrer();
		timer.start();
		jeu.setEcouteurDeJeu(this);
	
		//---------------------------------------
		//-- dernieres propietes de la fenetre --
		//---------------------------------------
		pack(); // adapte la taille de la fenetre a son contenu
		setVisible(true); // tu es toute belle, affiche toi !
		setLocationRelativeTo(null); // centrage de la fenetre
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
		
		if (source == itemActiverDesactiverSon) 
		   if (SoundManagement.isVolumeMute()) 
		   {
		      SoundManagement.setVolumeMute(false);
		      SoundManagement.setVolumeSysteme(VOLUME_PAR_DEFAUT);
		   }
		   else
		   {
		       SoundManagement.setVolumeMute(true); 
		   }

		// quitter
		else if(source == itemQuitter)
			quitter();
		
		// retour au menu principal
		else if(source == itemRetourMenu)
		    demanderRetourAuMenuPrincipal();  
		
		// règles
		else if(source == itemRegles)
		    new View_HTML(Language.getTexte(Language.ID_TXT_BTN_REGLES), new File(Language.getTexte(Language.ID_ADRESSE_REGLES_DU_JEU)), this);

		// a propos
		else if(source == itemAPropos)
			new View_HTML(Language.getTexte(Language.ID_TXT_BTN_A_PROPOS),new File(Language.getTexte(Language.ID_ADRESSE_A_PROPOS)),this);

		// basculer affichage des rayons de portee
		else if(source == itemAfficherRayonsPortee)
		    if(panelTerrain.basculerAffichageRayonPortee())
		        itemAfficherRayonsPortee.setIcon(I_ACTIF);
		    else
		        itemAfficherRayonsPortee.setIcon(I_INACTIF);
		
		else if(source == itemAfficherZonesJoueurs)
            if(panelTerrain.basculerAffichageZonesJoueurs())
                itemAfficherZonesJoueurs.setIcon(I_ACTIF);
            else
                itemAfficherZonesJoueurs.setIcon(I_INACTIF);

		else if(source == bEnvoyerMsg)
		{
		    try{
                
		        // on envoie pas de chaines vides
		        if(!tfSaisieMsg.getText().trim().equals(""))
                {
                    try
                    {
                        jeu.envoyerMsgChat(tfSaisieMsg.getText(), ServerJeu.A_TOUS);
                    
                        tfSaisieMsg.setText("");
                        tfSaisieMsg.requestFocus();
                    } 
                    catch (MessageChatInvalide e)
                    {
                        ajouterTexteHTMLDansConsole("<font color='red'>"+Language.getTexte(Language.ID_TXT_HTML_INTERDIT)+"</font> <br/>");
                    }
                    
                }
            } 
		    catch (ChannelException e)
            {
                e.printStackTrace();
            }
		}   
	}

	private void deconnexionDuJoueur()
	{
	    // on envoie la deconnexion
        try
        {
            jeu.annoncerDeconnexion();
        } 
        catch (ChannelException e)
        {
            e.printStackTrace();
        }
	    
	}
	
	/**
	 * Permet de proposer au joueur s'il veut quitter le programme
	 */
	private void quitter()
    {
	    if(JOptionPane.showConfirmDialog(this, 
	            Language.getTexte(Language.ID_TXT_DIALOG_QUITTER_JEU), "", 
	            JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
	    {
	        deconnexionDuJoueur();
	        
	        jeu.detruire();
	        
	        System.exit(0); // Fermeture correcte du logiciel
	    }
    }

	/**
     * Permet de demander pour retourner au menu principal
     */
	private void demanderRetourAuMenuPrincipal()
    {
	    if(JOptionPane.showConfirmDialog(this, 
	            Language.getTexte(Language.ID_TXT_DIALOG_ARRETER_PARTIE), "", 
	            JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
	        retourAuMenuPrincipal();
        }
    }
	
    /**
	 * Permet de retourner au menu principal
	 */
	private void retourAuMenuPrincipal()
    {
	    deconnexionDuJoueur();
	    
	    SoundManagement.arreterTousLesSons();
        
	    jeu.terminer();
        jeu.detruire();
        
        dispose(); // destruction de la fenetre
        System.gc(); // passage du remasse miette
        new View_MenuPrincipal();  
    }

    @Override
	public void acheterTour(Tower tour)
	{
	    try
	    {
	        jeu.poserTour(tour);
	        
	        panelTerrain.toutDeselectionner();
	          
            Tower nouvelleTour = tour.getCopieOriginale();
            nouvelleTour.setProprietaire(tour.getPrioprietaire());
            setTourAAcheter(nouvelleTour);
            panelSelection.setSelection(tour, Panel_InfoTour.MODE_ACHAT);
            panelSelectionEtVague.setSelectedIndex(0);
            
            
            lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
            lblEtat.setText(Language.getTexte(Language.ID_TXT_TOUR_POSEE));
	    }
	    catch(Exception e)
	    {
	        lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
	        lblEtat.setText(e.getMessage());
	    }
	}
	
	@Override
	public void ameliorerTour(Tower tour)
	{
	    try
        {
	        jeu.ameliorerTour(tour);
	          
	        panelSelection.setSelection(tour, Panel_InfoTour.MODE_SELECTION);
	        
	        lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
            lblEtat.setText(Language.getTexte(Language.ID_TXT_TOUR_AMELIOREE));
        }
	    catch(Exception e)
        {
	        lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
	        lblEtat.setText(e.getMessage());
        }
	}
	
	@Override
    public void vendreTour(Tower tour)
    {
        try
        {
            jeu.vendreTour(tour);
            panelSelection.deselection();
            panelTerrain.setTourSelectionnee(null);
            
            jeu.ajouterAnimation(
                    new GetGold((int)tour.getCenterX(),(int)tour.getCenterY(), 
                            tour.getPrixDeVente())
                    );
            
            lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
            lblEtat.setText(Language.getTexte(Language.ID_TXT_TOUR_VENDUE));
        } 
        catch (ActionUnauthorizedException e)
        {
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText(e.getMessage());
        }
    }
	
    /**
     * Permet d'ajouter du text HTML dans la console
     * 
     * @param texte le texte a ajouter
     */
    public void ajouterTexteHTMLDansConsole(String texte)
    {
        String s = taConsole.getText();
        taConsole.setText( s.substring(0,s.indexOf("</body>")) 
                           + texte + 
                           s.substring(s.indexOf("</body>")));
        
        // reposition le curseur en fin 
        taConsole.setCaretPosition( taConsole.getDocument().getLength() - 1 );
    }
    
	@Override
	public void tourSelectionnee(Tower tour,int mode)
	{
	    if(tour == null)
	        // selection de l'onglet création de vague
	        panelSelectionEtVague.setSelectedIndex(1);
	    else
	        // selection de l'onglet selection
	        panelSelectionEtVague.setSelectedIndex(0);
	    
	    panelSelection.setSelection(tour, mode);
	    lblEtat.setText(" ");
	}
	
	/**
     * Permet d'informer la fenetre qu'une creature a ete selectionnee
     * 
     * @param creature la creature selectionnee
     */
    public void creatureSelectionnee(Creature creature)
    {
        if(creature == null)
            // selection de l'onglet création de vague
            panelSelectionEtVague.setSelectedIndex(1);
        else
            // selection de l'onglet selection
            panelSelectionEtVague.setSelectedIndex(0);
        
        panelSelection.setSelection(creature, 0);
        lblEtat.setText(" ");
    }

	/**
     * Permet d'informer la fenetre qu'on change la tour a acheter
     * 
     * @param tour la nouvelle tour a acheter
     */
	public void setTourAAcheter(Tower tour)
	{
		panelTerrain.setTourAAjouter(tour);
		panelSelection.setSelection(tour, Panel_InfoTour.MODE_ACHAT);
		lblEtat.setText(" ");
	}

	
	
    @Override
	public void creatureInjured(Creature creature)
	{
	    panelSelection.setSelection(creature, 0);
	}

	@Override
	public void creatureKilled(Creature creature,Player tueur)
	{
	    // on efface la creature des panels d'information
        if(creature == panelTerrain.getCreatureSelectionnee())
        {
            panelSelection.deselection();
            panelTerrain.setCreatureSelectionnee(null);
        }

        jeu.ajouterAnimation(new GetGold((int)creature.getCenterX(),
                (int)creature.getCenterY() - 2,
                creature.getNbPiecesDOr()));
	}

	/**
     * methode regissant de l'interface EcouteurDeCreature
     * 
     * Permet d'etre informe lorsqu'une creature est arrivee en zone d'arrivee.
     */
	public void creatureArriveEndZone(Creature creature)
	{
	    // creation de l'animation de blessure du joueur
        jeu.ajouterAnimation(new RedWarn(jeu.getTerrain().getLargeur(),jeu.getTerrain().getHauteur())) ;

        // si c'est la creature selectionnee
        if(panelTerrain.getCreatureSelectionnee() == creature)
        {
            panelSelection.deselection();
            panelTerrain.setCreatureSelectionnee(null);
        }
	}

    @Override
    public void waveAttackFinish(WaveOfCreatures vagueDeCreatures)
    {}
 
    @Override
    public void windowActivated(WindowEvent e)
    {}

    @Override
    public void windowClosed(WindowEvent e)
    {}

    @Override
    public void windowClosing(WindowEvent e)
    {
       quitter(); 
    }

    @Override
    public void windowDeactivated(WindowEvent e){}

    @Override
    public void windowDeiconified(WindowEvent e){}

    @Override
    public void windowIconified(WindowEvent e){}

    @Override
    public void windowOpened(WindowEvent e){}

    @Override
    public void keyPressed(KeyEvent ke){}

    @Override
    public void keyTyped(KeyEvent e){}
    
    @Override
    public void keyReleased(KeyEvent e){}

    @Override
    public void lancerVague(WaveOfCreatures vague) throws MoneyLackException
    {
        jeu.lancerVague(jeu.getJoueurPrincipal(), jeu.getEquipeSuivanteNonVide(jeu.getJoueurPrincipal().getEquipe()),vague);
    }

    @Override
    public void ajouterInfoVagueSuivanteDansConsole(){}

    @Override
    public void lancerVagueSuivante() {}

    @Override
    public void terminatePart(GameResult resultatJeu)
    {
        panelSelection.partieTerminee();
        panelAjoutTour.partieTerminee();
        
        Team equipeGagnante = resultatJeu.getEquipeGagnante();
        
        // FIXME continuer...
        if(equipeGagnante == null)
            new Dialog_Message (this, "Draw!", "Nobody won !");         
        else if(equipeGagnante == jeu.getJoueurPrincipal().getEquipe())
        {
            new Dialog_Message (this, "Won!", "You win :) Nice!");
            ajouterTexteHTMLDansConsole("<b>You win !</b><br/>"); 
        }
        else
        {
            new Dialog_Message (this, "Results", 
                    " Team \""+equipeGagnante.getNom()+"\" " +
                    " win!");  
            
            String couleurHexa = Tools.ColorToHexa(equipeGagnante.getCouleur());
            ajouterTexteHTMLDansConsole("Team \"<b><font color='#"+couleurHexa+"'>"+equipeGagnante.getNom()+"</font></b>\" wins!<br/>"); 
        }
    }

    @Override
    public void winStar(){}

    @Override
    public void towerUpgrade(Tower tour){}

    @Override
    public void towerPlaced(Tower tour){}

    @Override
    public void towerSold(Tower tour){}

    @Override
    public void animationAjoutee(Animation animation){}

    @Override
    public void animationTerminee(Animation animation){}

    @Override
    public void creatureAjoutee(Creature creature){}

    @Override
    public void playerJoin(Player joueur){}

    @Override
    public void startPart(){}

    @Override
    public void joueurMisAJour(Player joueur)
    {
        if(jeu.getJoueurPrincipal() == joueur)
        {
            panelCreationVague.miseAJour();
            panelAjoutTour.miseAJour();
            panelInfoJoueurEtPartie.miseAJour();
        }
    }

    @Override
    public void initializationPart(){}

    @Override
    public void erreurPasAssezDArgent()
    {
        lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
        lblEtat.setText("Vague trop chère");
    }

    @Override
    public void miseAJourInfoJeu(){}

    @Override
    public void joueurInitialise(){}

    @Override
    public void joueursMisAJour(){}

    @Override
    public void messageRecu(String message, Player auteur)
    {
        String couleurHexa = Tools.ColorToHexa(auteur.getEquipe().getCouleur());
        
        ajouterTexteHTMLDansConsole(String.format(Language.getTexte(Language.ID_TXT_PSEUDO_DIT_MESSAGE), "<b><font color='#"+couleurHexa+"'>"+auteur.getPseudo()+"</font></b>",message)+"<br />");
    }

    @Override
    public void joueurDeconnecte(Player joueur)
    {
        ajouterTexteHTMLDansConsole("<font color='#FF0000'>"+String.format(Language.getTexte(Language.ID_TXT_PSEUDO_EST_PARTI), joueur.getPseudo())+"</font><br />");
    }

    @Override
    public void deselection()
    {
        panelSelection.deselection();
        
        lblEtat.setText(" ");
    }

    @Override
    public void receptionEquipeAPerdue(Team equipe)
    {
        if(equipe == jeu.getJoueurPrincipal().getEquipe())
        {
            new Dialog_Message (this, "Lost!", "You lose :(");
            ajouterTexteHTMLDansConsole("<b>You lose!</b><br />");
        }
        else
        {   
            String couleurHexa = Tools.ColorToHexa(equipe.getCouleur());
            ajouterTexteHTMLDansConsole("Team \"<b><font color='#"+couleurHexa+"'>"+equipe.getNom()+"</font></b>\" loses!<br />");
        }
    }

    @Override
    public void teamLost(Team equipe)
    {
        // ca vient du jeu... on s'en fou, c'est les infos du serveur qui comptent.
    }

    @Override
    public void velocityChanged(double coeffVitesse)
    {
        // pas utilisé en mode multi
    }
}
