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

import i18n.Langue;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.ChannelException;
import net.game.client.ClientListener;
import net.game.server.ServeurJeu;

import exceptions.MessageChatInvalide;

import views.View_MenuPrincipal;
import views.ManageFonts;
import views.LookInterface;
import views.Panel_MenuPrincipal;
import views.common.Panel_EmplacementsTerrain;
import views.common.Panel_GridBag;
import models.animations.Animation;
import models.creatures.Creature;
import models.creatures.WaveOfCreatures;
import models.game.*;
import models.player.*;
import models.towers.Tower;
import models.utils.SoundManagement;
import models.utils.Tools;

/**
 * Formulaire d'attente de joueurs
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 */
public class Panel_AttendreJoueurs extends JPanel implements 
    ActionListener, GameListener, ClientListener
{
    private static final long serialVersionUID = 1L;
    private final int MARGES_PANEL = 40;
    private final boolean ADMIN;
    private JFrame parent;
    private JButton bDemarrerMaintenant = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_DEMARRER));
    private JLabel lblEtat = new JLabel();
    private JButton bDeconnecter = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_SE_DECONNECTER));
    private Game_Server jeuServeur;
    private Game_Client jeuClient;
    private Panel_EmplacementsTerrain pEmplacementsTerrain;
    private JLabel lEtat = new JLabel();
    private JPanel pTmp;
    private Panel_GridBag pJoueurs;
    
    private Console console;
    private JTextField tfSaisieMsg = new JTextField();
    private static final ImageIcon I_ENVOYER_MSG = new ImageIcon("img/icones/msg_go.png"); 
    private JButton bEnvoyerMsg = new JButton(I_ENVOYER_MSG);
    
    /**
     * Constructeur de créateur de partie
     * 
     * @param parent la fenetre parent
     * @param jeu le jeu
     * @param joueur le joueur
     */
    public Panel_AttendreJoueurs(JFrame parent, Game_Server jeuServeur, Game_Client jeuClient)
    {
        this.parent     = parent;
        this.ADMIN      = true;
        this.jeuServeur = jeuServeur;
        this.jeuClient  = jeuClient;
        
        jeuClient.setEcouteurDeClientJeu(this);
        
        initialiserForm();
    }

    /**
     * Constructeur du joueur qui rejoint
     * 
     * @param parent la fenetre parent
     * @param jeu le jeu du client
     * @param joueur le joueur
     */
    public Panel_AttendreJoueurs(JFrame parent, Game_Client jeu)
    {
        this.parent     = parent;
        this.ADMIN      = false;
        this.jeuClient  = jeu;

        jeuClient.setEcouteurDeClientJeu(this);
        
        initialiserForm();
    }
    
    /**
     * Permet d'initialiser le formulaire
     */
    private void initialiserForm()
    {
        jeuClient.setEcouteurDeJeu(this);
        
        // initialisation
        setLayout(new BorderLayout());

        parent.setTitle(Langue.getTexte(Langue.ID_TITRE_ATTENTE_DE_JOUEURS));
        setBorder(new EmptyBorder(new Insets(MARGES_PANEL, MARGES_PANEL,
                MARGES_PANEL, MARGES_PANEL)));
        setBackground(LookInterface.COULEUR_DE_FOND_PRI);

        // ---------
        // -- TOP --
        // ---------
        JPanel pTop = new JPanel(new BorderLayout());
        pTop.setOpaque(false);

        JLabel lblTitre = new JLabel(Langue.getTexte(Langue.ID_TITRE_ATTENTE_DE_JOUEURS));
        lblTitre.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        lblTitre.setFont(ManageFonts.POLICE_TITRE);
        pTop.add(lblTitre, BorderLayout.NORTH);

        add(pTop, BorderLayout.NORTH);

        // ------------
        // -- CENTER --
        // ------------
        
        pJoueurs = contruirePanelEmplacementsJoueur();

        JPanel pCenter = new JPanel(new BorderLayout());
        pCenter.setOpaque(false);

        pEmplacementsTerrain = new Panel_EmplacementsTerrain(jeuClient.getTerrain(),300,300);
       
        JScrollPane spEmplacement = new JScrollPane(pEmplacementsTerrain);
        spEmplacement.setOpaque(false);
        spEmplacement.setBorder(null);

        //spEmplacement.setPreferredSize(new Dimension(350, 350));
        pCenter.add(spEmplacement, BorderLayout.EAST);

        
        JPanel pJoueursEtat = new JPanel(new BorderLayout());
        pJoueursEtat.setOpaque(false);
        pJoueursEtat.add(lEtat, BorderLayout.CENTER);
        
 
        
        pTmp = new JPanel(new BorderLayout());
        pTmp.setOpaque(false);

        JScrollPane js = new JScrollPane(pJoueurs);
        js.setOpaque(false);
        js.setBorder(null);
        pTmp.add(js, BorderLayout.NORTH);
        
        
        //pTmp.add(bTmpJConn, BorderLayout.SOUTH);
        //bTmpJConn.addActionListener(this);
        
        
        pJoueursEtat.add(pTmp, BorderLayout.NORTH);
        
        pCenter.add(pJoueursEtat, BorderLayout.WEST);

        
        
   
        add(pCenter, BorderLayout.CENTER);
        
        

        
        

        // ------------
        // -- BOTTOM --
        // ------------

        
        JPanel pBottom = new JPanel(new BorderLayout());
        pBottom.setOpaque(false);

        
        // CONSOLE
        console = new Console(0,80);
        console.setOpaque(false);
        
        JPanel pChat = new JPanel(new BorderLayout());
        pChat.setOpaque(false);
        
        pChat.add(console,BorderLayout.NORTH);
        
        bEnvoyerMsg.addActionListener(this);
        parent.getRootPane().setDefaultButton(bEnvoyerMsg); // bouton par def.
        JPanel pSaisieMsgEtBEnvoyer = new JPanel(new BorderLayout());
        pSaisieMsgEtBEnvoyer.setOpaque(false);
        pSaisieMsgEtBEnvoyer.add(tfSaisieMsg,BorderLayout.CENTER);
        pSaisieMsgEtBEnvoyer.add(bEnvoyerMsg,BorderLayout.EAST);
        pChat.add(pSaisieMsgEtBEnvoyer,BorderLayout.SOUTH);
        
        pBottom.add(pChat,BorderLayout.CENTER);
        

        // bouton démarrer
        if (ADMIN)
        {
            bDemarrerMaintenant.setPreferredSize(new Dimension(150, 50));
            ManageFonts.setStyle(bDemarrerMaintenant);
            
            JPanel pSud = new JPanel(new BorderLayout());
            pSud.setOpaque(false);
            pSud.add(bDemarrerMaintenant,BorderLayout.SOUTH);
            
            pBottom.add(pSud, BorderLayout.EAST);
            bDemarrerMaintenant.addActionListener(this);
           
            try
            {
                String s = Langue.getTexte(Langue.ID_TXT_VOS_ADRESSES_IP)+" : ";
                
                for (NetworkInterface netint : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    for (InetAddress inetAddress : Collections.list(netint.getInetAddresses())) {
                       if (!inetAddress.toString().contains(":") && !inetAddress.toString().contains("127.0.0.1"))
                       {
                          s += "[ <b>"+inetAddress.toString().substring(1) + "</b> ] ";
                       }
                    }
                 }
                
                console.ajouterTexteHTMLDansConsole(s+"<br />");
            } 
            catch (SocketException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            console.ajouterTexteHTMLDansConsole(Langue.getTexte(Langue.ID_TXT_DESCR_CONSOLE_CHAT));
        }
        
        bDeconnecter.addActionListener(this);
        bDeconnecter.setPreferredSize(new Dimension(120, 50));
        ManageFonts.setStyle(bDeconnecter);
        
        JPanel pSud = new JPanel(new BorderLayout());
        pSud.setOpaque(false);
        pSud.add(bDeconnecter,BorderLayout.SOUTH);
        
        
        pBottom.add(pSud, BorderLayout.WEST);

        if (ADMIN)
            if (jeuServeur.estEnregisterSurSE())
            {
                lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
                lblEtat.setText(Langue.getTexte(Langue.ID_TXT_CON_SRV_CENTRAL_ETABLIE)); 
            } 
            else
            {
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText(Langue.getTexte(Langue.ID_ERREUR_ENREGISTREMENT_AU_SRV_CENTRAL_ECHOUE));
            }

        pBottom.add(lblEtat, BorderLayout.SOUTH);

        add(pBottom, BorderLayout.SOUTH);
    }

    /**
     * Permte de remplir la combobox
     * 
     * @param cbEmplacements la combobox
     * @param joueur pour pré-sélection
     */
    private void remplirCombo(JComboBox cbEmplacements, Player joueur)
    {
        synchronized(cbEmplacements)
        {  
            // vidage
            cbEmplacements.removeAllItems();

            // Emplacements de l'equipe
            Team equipe = joueur.getEquipe();
            for (int j = 0; j < equipe.getEmplacementsJoueur().size(); j++)
            {
                PlayerLocation ej = equipe.getEmplacementsJoueur().get(j);
    
                cbEmplacements.addItem(ej.toString());
   
                if (joueur.getEmplacement() == ej)
                    cbEmplacements.setSelectedIndex(j);
            }
            
            // si seulement 1 zone, on efface le combobox
            cbEmplacements.setVisible(cbEmplacements.getItemCount() > 1);
            
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();

        if (src == bDemarrerMaintenant)
        {
            if (ADMIN)
            {
                jeuServeur.desenregistrerSurSE();
 
                // reférence différente entre client et serveur
                jeuServeur.initialiser();
                jeuServeur.demarrer();  
            }
        }
        else if (src == bDeconnecter)
        {
            if (ADMIN)
            {
                jeuServeur.desenregistrerSurSE();
                jeuServeur.stopperServeurDeJeu();
            }
            else
            {
                try {
                    jeuClient.annoncerDeconnexion();
                } 
                catch (ChannelException e1){
                    // on peut rien faire...
                }
            }

            // retour
            parent.getContentPane().removeAll();
            parent.getContentPane().add(new Panel_MenuPrincipal(parent),
                    BorderLayout.CENTER);
            parent.getContentPane().validate();
        }
        else if(src == bEnvoyerMsg)
        {
            try{
                
                // on envoie pas de chaines vides
                if(!tfSaisieMsg.getText().trim().equals(""))
                {
                    try
                    {
                        jeuClient.envoyerMsgChat(tfSaisieMsg.getText(), ServeurJeu.A_TOUS);
                        
                        tfSaisieMsg.setText("");
                        tfSaisieMsg.requestFocus();
                    }
                    catch (MessageChatInvalide e1)
                    {
                       console.ajouterTexteHTMLDansConsole("<font color='red'>"+Langue.getTexte(Langue.ID_TXT_HTML_INTERDIT)+"</font> <br/>");
                    }
                }
            } 
            catch (ChannelException e1)
            {
                e1.printStackTrace();
            }
        }   
    }

    private void ajouterJoueur(final Player joueur, Panel_GridBag pJoueurs, int pos)
    {
        final JComboBox cbEmplacements = new JComboBox();
        final JComboBox cbEquipes = new JComboBox();

        // styles
        ManageFonts.setStyle(cbEmplacements);
        ManageFonts.setStyle(cbEquipes);
        
        final JLabel lPseudo = new JLabel(joueur.getPseudo());
        lPseudo.setFont(ManageFonts.POLICE_SOUS_TITRE);
        pJoueurs.add(lPseudo, 1, pos, 1);
        lPseudo.setForeground(joueur.getEquipe().getCouleur());

        ArrayList<Team> equipes = jeuClient.getEquipes();
        
        if(ADMIN || jeuClient.getJoueurPrincipal() == joueur)
        {
            // Liste des équipes

            // Remplissage
            for (int j = 0; j < equipes.size(); j++)
            {
                Team tmpEquipe = equipes.get(j);

                // ajout de l'equipe
                cbEquipes.addItem(tmpEquipe);

                if (joueur.getEquipe() == tmpEquipe)
                    cbEquipes.setSelectedIndex(j);
            }

            // Action de la liste des équipes
            cbEquipes.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        lEtat.setText("");
                        Team equipe = (Team) cbEquipes
                                .getSelectedItem();
                        
                        jeuClient.changerEquipe(joueur, equipe);
 
                        // mise a jour de la liste des emplacements
                        remplirCombo(cbEmplacements,joueur);

                        lPseudo.setForeground(joueur.getEquipe().getCouleur());
                    } 
                    catch (Exception iae)
                    {
                        // on reselectionne l'ancienne sélection
                        cbEquipes.setSelectedItem(joueur.getEquipe());

                        lEtat.setForeground(LookInterface.COULEUR_ERREUR);
                        lEtat.setText(iae.getMessage());
                    }

                    pEmplacementsTerrain.repaint();
                }
            });

            pJoueurs.add(cbEquipes, 2, pos, 1);

            // remplissage de la combobox
            remplirCombo(cbEmplacements,joueur);

            // Action de la liste des emplacements
            cbEmplacements.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        lEtat.setText("");
                        
                        if(cbEmplacements.getSelectedIndex() != -1)
                        {
                            PlayerLocation ej = joueur.getEquipe().getEmplacementsJoueur().get(cbEmplacements.getSelectedIndex());
                            
                            joueur.setEmplacementJoueur(ej);
                            pEmplacementsTerrain.repaint();
                        }
                    } 
                    catch (IllegalArgumentException iae)
                    {
                        // on reselectionne l'ancienne sélection
                        for(int i=0;i<cbEmplacements.getItemCount();i++)
                            if(joueur.getEmplacement().toString().equals(cbEmplacements.getItemAt(i)))
                                cbEmplacements.setSelectedIndex(i);
                        
                        lEtat.setForeground(LookInterface.COULEUR_ERREUR);
                        lEtat.setText(iae.getMessage());
                    }
                }
            });

            // ajout de l'emplacement
            pJoueurs.add(cbEmplacements, 3, pos, 1);
        }
    }
    
    public Panel_GridBag contruirePanelEmplacementsJoueur()
    {
        ArrayList<Player> joueurs = jeuClient.getJoueurs();
        int maxJoueurs = jeuClient.getTerrain().getNbJoueursMax();

        Panel_GridBag pJoueurs = new Panel_GridBag(new Insets(2, 2, 2, 2));
        pJoueurs.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        pJoueurs.setPreferredSize(new Dimension(350, 150));

        for (int i = 0; i < maxJoueurs; i++)
        {
            /*
            JLabel lNo = new JLabel((i + 1) + ". ");
            lNo.setFont(GestionnaireDesPolices.POLICE_SOUS_TITRE);
            pJoueurs.add(lNo, 0, i, 1);
            */

            // joueur trouvé
            if (i < joueurs.size())
                ajouterJoueur(joueurs.get(i), pJoueurs, i);
            // inconnu
            else 
            {
                JLabel lInconnu = new JLabel("???");
                lInconnu.setFont(ManageFonts.POLICE_SOUS_TITRE);
                pJoueurs.add(lInconnu, 1, i, 1);
            }
        }
        
        return pJoueurs;
    }

    @Override
    public void animationAjoutee(Animation animation){}

    @Override
    public void animationTerminee(Animation animation){}

    @Override
    public void creatureAjoutee(Creature creature){}

    @Override
    public void creatureArriveEndZone(Creature creature){}

    @Override
    public void creatureInjured(Creature creature){}

    @Override
    public void creatureKilled(Creature creature,Player tueur){}

    @Override
    public void winStar(){}

    @Override
    public void playerJoin(Player joueur){}

    @Override
    public void joueurMisAJour(Player joueur){}

    @Override
    public void startPart(){}

    @Override
    public void initializationPart()
    {
        switch(jeuClient.getTerrain().getMode())
        {
            case GameMode.MODE_VERSUS :
                new Fenetre_JeuVersus(jeuClient);
                break;
                 
            /*
            TODO implémenter le mode de jeu coopératif
            case ModeDeJeu.MODE_COOP :
                new Fenetre_JeuCoop(jeuClient);
                break;
            */
        }
        
        SoundManagement.arreterTousLesSons(View_MenuPrincipal.FICHIER_MUSIQUE_MENU);
        
        parent.dispose();
    }

    @Override
    public void terminatePart(GameResult resultatJeu){}

    @Override
    public void towerUpgrade(Tower tour){}

    @Override
    public void towerPlaced(Tower tour){}

    @Override
    public void towerSold(Tower tour){}

    @Override
    public void waveAttackFinish(WaveOfCreatures vague){}

    @Override
    public void joueurInitialise()
    {
        initialiserForm();
    }

    @Override
    public void joueursMisAJour()
    {
        pTmp.removeAll();
        pJoueurs = contruirePanelEmplacementsJoueur();
        JScrollPane js = new JScrollPane(pJoueurs);
        js.setOpaque(false);
        js.setBorder(null);
        pTmp.add(js, BorderLayout.NORTH);
        pTmp.revalidate();
        pEmplacementsTerrain.repaint();
        
        // mise a jour de la partie sur le serveur d'enregistrement
        if(ADMIN)
            jeuServeur.miseAJourSE();
    }

    @Override
    public void messageRecu(String message, Player auteur)
    {
        String couleurHexa = Tools.ColorToHexa(auteur.getEquipe().getCouleur());
        
        console.ajouterTexteHTMLDansConsole(String.format(Langue.getTexte(Langue.ID_TXT_PSEUDO_DIT_MESSAGE), "<b><font color='#"+couleurHexa+"'>"+auteur.getPseudo()+"</font></b>",message)+"<br />");
    }

    @Override
    public void joueurDeconnecte(Player joueur)
    {
        console.ajouterTexteHTMLDansConsole("<font color='#FF0000'>"+String.format(Langue.getTexte(Langue.ID_TXT_PSEUDO_EST_PARTI), joueur.getPseudo())+"</font><br />");
    }

    @Override
    public void receptionEquipeAPerdue(Team equipe){}

    @Override
    public void teamLost(Team equipe)
    {
    }

    @Override
    public void velocityChanged(double coeffVitesse)
    {
        // NOP
    }
}
