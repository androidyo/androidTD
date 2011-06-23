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
import javax.swing.table.DefaultTableModel;

import models.game.Game_Client;
import models.player.Team;
import models.player.Player;
import net.*;
import net.jeu.client.EcouteurDeClientJeu;

import org.json.*;
import exceptions.NoLocationAvailableException;
import server.registerment.*;
import utils.Configuration;
import views.ManageFonts;
import views.LookInterface;
import views.Panel_MenuPrincipal;

/**
 * Panel pour rejoindre une partie réseau.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | mai 2010
 */
@SuppressWarnings("serial")
public class Panel_RejoindrePartieMulti extends JPanel implements
        ActionListener, KeyListener, MouseListener, EcouteurDeClientJeu
{  
    private final int MARGES_PANEL = 40;
    private JFrame parent;

    private DefaultTableModel model = new DefaultTableModel();
    private JTable tbServeurs;
    private ArrayList<ServeurInfo> serveurs = new ArrayList<ServeurInfo>();

    private String filtre = "";
    private static final String FILTRE_DEFAUT = Langue.getTexte(Langue.ID_TXT_FILTRE);
    private JTextField tfFiltre = new JTextField(FILTRE_DEFAUT);

    private JLabel lblConnexionParIP = new JLabel(Langue.getTexte(Langue.ID_TITRE_CONN_PAR_IP));
    private JTextField tfConnexionParIP = new JTextField("127.0.0.1",10);

    private JLabel lblPseudo = new JLabel(Langue.getTexte(Langue.ID_TITRE_PSEUDO));
    private JTextField tfPseudo = new JTextField("",10);

    private JButton bRejoindre = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_REJOINDRE));
    private JButton bRafraichir = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_RAFRAICHIR));
    
    private JLabel lblEtat = new JLabel();

    private JButton bRetour = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_RETOUR));

    private ChannelTCP canalServeurEnregistrement;
    
    private Game_Client jeu;
    private Player joueur;
    
    
    /**
     * Classe interne pour stocker les informations d'un serveur
     */
    private class ServeurInfo
    {

        private String nom, IP, Mode, nomTerrain;
        private int port, nbPlaces = 0, placesLibres = 0;

        /**
         * Constructeur
         * 
         * @param nom le nom
         * @param IP l'adresse IP
         * @param port le numéro du port
         * @param Mode le mode de jeu
         * @param nomTerrain le nom du terrain
         * @param nbPlaces le nombre de joueurs
         * @param placesLibres les places restantes
         */
        public ServeurInfo(String nom, String IP, int port, String Mode,
                String nomTerrain, int nbPlaces, int placesLibres)
        {
            this.nom = nom;
            this.IP = IP;
            this.port = port;
            this.Mode = Mode;
            this.nomTerrain = nomTerrain;
            this.nbPlaces = nbPlaces;
            this.placesLibres = placesLibres;
        }

        /**
         * Permet de savoir si l'un des champs du serveur sontient une chaine
         * particulière.
         * 
         * @param s le pattern
         * @return true si le serveur contient bien la chaine, sinon false.
         */
        public boolean contientLaChaine(String s)
        {
            s = s.toLowerCase();

            if (nom.toLowerCase().indexOf(s) != -1)
                return true;
            if (IP.toLowerCase().indexOf(s) != -1)
                return true;
            if (Mode.toLowerCase().indexOf(s) != -1)
                return true;
            if (nomTerrain.toLowerCase().indexOf(s) != -1)
                return true;

            return false;
        }

        /**
         * Permet de recuperer les informations sous la forme d'un tableau de
         * String pour les mettre ensuite dans une JTable
         * 
         * @return un tableau de String pour une JTable
         */
        String[] toStringArray()
        {
            return new String[] { nom, IP, port+"", Mode, nomTerrain,
                    placesLibres + " / " + nbPlaces };
        }
    }

    /**
     * Constructeur
     * 
     * @param parent le fenetre parent
     */
    public Panel_RejoindrePartieMulti(JFrame parent)
    {
        // initialisation
        super(new BorderLayout());
        this.parent = parent;
        parent.setTitle(Langue.getTexte(Langue.ID_TITRE_REJOINDRE_UNE_PARTIE_MULTI));
        setBorder(new EmptyBorder(new Insets(MARGES_PANEL, MARGES_PANEL,
                MARGES_PANEL, MARGES_PANEL)));
        setBackground(LookInterface.COULEUR_DE_FOND_PRI);
           
        // ---------
        // -- TOP --
        // ---------
        JPanel pTop = new JPanel(new BorderLayout());
        pTop.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        
        JLabel titre = new JLabel(Langue.getTexte(Langue.ID_TITRE_REJOINDRE_UNE_PARTIE_MULTI));
        titre.setFont(ManageFonts.POLICE_TITRE);
        titre.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        pTop.add(titre, BorderLayout.NORTH);
        

        // filtre
        JPanel pADroite = new JPanel(new BorderLayout());
        pADroite.setBackground(LookInterface.COULEUR_DE_FOND_PRI);

        tfFiltre.setPreferredSize(new Dimension(100, 25));
        tfFiltre.addKeyListener(this);
        tfFiltre.addMouseListener(this);

        pADroite.add(tfFiltre, BorderLayout.WEST);
        pTop.add(pADroite, BorderLayout.CENTER);
        ManageFonts.setStyle(bRafraichir);
        pTop.add(bRafraichir, BorderLayout.EAST);
        bRafraichir.addActionListener(this);
         
        add(pTop, BorderLayout.NORTH);

        // ------------
        // -- CENTER --
        // ------------

        // création de la table avec boquage des editions
        tbServeurs = new JTable(model)
        {
            public boolean isCellEditable(int rowIndex, int colIndex)
            {
                return false; // toujours désactivé
            }
        };

        // Simple selection
        tbServeurs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // nom de colonnes
        model.addColumn(Langue.getTexte(Langue.ID_TXT_NOM));
        model.addColumn(Langue.getTexte(Langue.ID_TXT_IP));
        model.addColumn(Langue.getTexte(Langue.ID_TXT_PORT));
        model.addColumn(Langue.getTexte(Langue.ID_TXT_MODE));
        model.addColumn(Langue.getTexte(Langue.ID_TXT_TERRAIN));
        model.addColumn(Langue.getTexte(Langue.ID_TXT_PLACES_DISPO));

        // Création du canal avec le serveur d'enregistrement
        try
        {
            canalServeurEnregistrement = new ChannelTCP(
                    Configuration.getIpSE(),
                    Configuration.getPortSE());
            
            mettreAJourListeDesServeurs();
        } 
        catch (ConnectException e)
        {
            connexionSEImpossible();          
        } 
        catch (ChannelException e) 
        {
            connexionSEImpossible();   
        }
        
        // ajout dans le panel
        add(new JScrollPane(tbServeurs), BorderLayout.CENTER);

        // ------------
        // -- BOTTOM --
        // ------------
        JPanel pBottom = new JPanel(new BorderLayout());
        pBottom.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        
        bRetour.addActionListener(this);
        ManageFonts.setStyle(bRetour);
        bRetour.setPreferredSize(new Dimension(80,50));
        pBottom.add(bRetour, BorderLayout.WEST);

        JPanel bottomCenter = new JPanel();
        bottomCenter.setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        
        // connexion par IP 
        lblConnexionParIP.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lblConnexionParIP.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        bottomCenter.add(lblConnexionParIP);
        tfConnexionParIP.setPreferredSize(new Dimension(100, 25));
        bottomCenter.add(tfConnexionParIP);
        tfConnexionParIP.addMouseListener(this);

        // pseudo
        JPanel pPseudo = new JPanel();
        JPanel pTmp = new JPanel();
        
        lblPseudo.setFont(ManageFonts.POLICE_SOUS_TITRE);
        lblPseudo.setForeground(LookInterface.COULEUR_TEXTE_PRI);
        bottomCenter.add(lblPseudo);
        
        tfPseudo.setText(Configuration.getPseudoJoueur());
        bottomCenter.add(tfPseudo);
        
        pPseudo.add(pTmp, BorderLayout.EAST);
        pBottom.add(bottomCenter, BorderLayout.CENTER);

        // bouton rejoindre
        bRejoindre.setPreferredSize(new Dimension(100, 50));
        ManageFonts.setStyle(bRejoindre);
        pBottom.add(bRejoindre, BorderLayout.EAST);
        bRejoindre.addActionListener(this);

        pBottom.add(lblEtat, BorderLayout.SOUTH);

        add(pBottom, BorderLayout.SOUTH);
    }

    /**
     * Permet d'informer l'utilisateur que la connexion n'a pas été établie
     */
    private void connexionSEImpossible()
    {
        tbServeurs.setEnabled(false);
        bRafraichir.setEnabled(false);
        tfFiltre.setEnabled(false);
        lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
        lblEtat.setText(Langue.getTexte(Langue.ID_ERREUR_CON_SRV_CENTRAL_IMP_ENREZ_IP)); 
    }

    /**
     * Permet de demander la liste des serveurs au serveur d'enregistrement
     * et de mettre a jour la liste des serveurs
     */
    private void mettreAJourListeDesServeurs()
    {
        if(canalServeurEnregistrement != null)
        {
            // vidage de la table
            viderTable();
            
            try
            {
                // envoie de la requete d'enregistrement
                canalServeurEnregistrement.envoyerString(RequeteEnregistrement.INFOS_PARTIES);
                
                // attente du résultat
                String resultat = canalServeurEnregistrement.recevoirString();
        
                // mise à jour de la liste des serveurs
                mettreAJourListeDepuisJSON(resultat);
            } 
            catch (ChannelException e)
            {
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText(Langue.getTexte(Langue.ID_ERREUR_CON_SRV_CENTRAL_INVALIDE));          
            } 
        }
    }

    /**
     * Permet de mettre a jour la liste des serveurs avec une réponse JSON
     * 
     * @param resultatJSON le résultat du serveur d'enregistrement
     */
    private void mettreAJourListeDepuisJSON(String resultatJSON)
    {
        try
        {
            // Analyse de la réponse du serveur d'enregistrement
            JSONObject jsonResultat = new JSONObject(resultatJSON);
            
            // on vide la liste des serveurs
            serveurs.clear();
            
            if(jsonResultat.getInt("status") == CodeRegisterment.OK)
            {
                // sélection des serveurs de jeu
                JSONArray jsonArray = jsonResultat.getJSONArray("parties");
                
                // ajout des serveurs de jeu
                int i = 0;
                for(;i < jsonArray.length(); i++)
                {
                    JSONObject serveur = jsonArray.getJSONObject(i);
                    
                    ajouterServeur(serveur.getString("nomPartie"), 
                                   serveur.getString("adresseIp"),
                                   serveur.getInt("numeroPort"), 
                                   serveur.getString("mode"), 
                                   serveur.getString("nomTerrain"),
                                   serveur.getInt("capacite"), 
                                   serveur.getInt("placesRestantes"));
  
                }
                
                if(i > 0)
                    tbServeurs.setRowSelectionInterval(0, 0);
                
                lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
                lblEtat.setText(Langue.getTexte(Langue.ID_TXT_CON_SRV_CENTRAL_ETABLIE)); 
            }
            else
            {
                lblEtat.setForeground(LookInterface.COULEUR_SUCCES);
                lblEtat.setText(Langue.getTexte(Langue.ID_TXT_CON_SRV_CENTRAL_ETABLIE)+" ["+Langue.getTexte(Langue.ID_TXT_AUCUN_SRV_DISPONIBLE)+"]");
            }
        } 
        catch (JSONException e1)
        {
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText("Format de réponse du serveur incorrect!");
        }
    }

    /**
     * Permet d'ajouter un serveur
     * 
     * @param nom le nom
     * @param IP l'adresse IP
     * @param port le numéro du port
     * @param Mode le mode de jeu
     * @param nomTerrain le nom du terrain
     * @param nbPlaces le nombre de joueurs
     * @param placesLibres les places restantes
     */
    public void ajouterServeur(String nom, String IP, int port, String Mode,
            String nomTerrain, int nbPlaces, int placesLibres)
    {

        ServeurInfo srvInfo = new ServeurInfo(nom, IP, port, Mode, nomTerrain,
                nbPlaces, placesLibres);

        // ajout à la liste des serveurs
        serveurs.add(srvInfo);

        // ajout au tableau s'il correspond au filtre
        if (filtre.isEmpty() || !filtre.isEmpty()
                && srvInfo.contientLaChaine(filtre))
            model.addRow(srvInfo.toStringArray());
    }

    /**
     * Permet de mettre à jour la table en fonction de la liste des serveurs
     */
    private void miseAJourListe()
    {
        // nettoyage de la table
        viderTable();

        // recuperation du filtre
        filtre = tfFiltre.getText();

        // ajout des serveurs dans la table s'il respect le filtre
        int nbLignes = 0;
        for (ServeurInfo srvInfo : serveurs)
            if (filtre.equals(FILTRE_DEFAUT) || srvInfo.contientLaChaine(filtre))
            {
                model.addRow(srvInfo.toStringArray());
                nbLignes++;
            }
        
        if(nbLignes > 0)
            tbServeurs.setRowSelectionInterval(0, 0);
    }

    /**
     * Permet de vider la table des serveurs
     */
    private void viderTable()
    {
        // nettoyage de la table
        while (model.getRowCount() != 0)
            model.removeRow(0);
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();

        if (src == bRejoindre)
        {
            lblEtat.setText("");

            try
            {
                if (tfPseudo.getText().trim().isEmpty())
                    throw new Exception(Langue.getTexte(Langue.ID_ERREUR_PSEUDO_VIDE));

                Configuration.setPseudoJoueur(tfPseudo.getText());
                
                connexion(recupererIP(),recupererPort());
            } 
            catch (Exception exception)
            {
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText(exception.getMessage());
            }
        } 
        else if(src == bRafraichir)
        {
            mettreAJourListeDesServeurs();  
        }
        else if (src == bRetour)
        {
            parent.getContentPane().removeAll();
            parent.getContentPane().add(new Panel_MenuPrincipal(parent),
                    BorderLayout.CENTER);
            parent.getContentPane().validate();
            
            // fermeture du canal s'il est ouvert
            if(canalServeurEnregistrement != null)
            {
                try
                {
                    // fermeture propre du canal
                    canalServeurEnregistrement.envoyerString(RequeteEnregistrement.STOP);
                    canalServeurEnregistrement.recevoirString();
               
                    canalServeurEnregistrement.fermer();  
                } 
                catch (ChannelException e1)
                {
                    // l'utilisateur a déjà quitté le formulaire.
                }
            }
        }
    }

    /**
     * Permet de recupérer l'IP en fonction de l'état des champs du formulaire
     * 
     * @return l'adresse IP du serveur selectionné par l'utilisateur
     * @throws Exception s'il y des erreurs de saisie
     */
    private String recupererIP() throws Exception
    {
        // si selectionné
        if (tbServeurs.getSelectedRow() != -1)
            return (String) model.getValueAt(tbServeurs.getSelectedRow(), 1);
        
        // sinon on retourne l'ip manuelle si elle est valide
        else if (tfConnexionParIP.getText().isEmpty())
            throw new Exception(Langue.getTexte(Langue.ID_ERREUR_SEL_SRV_OU_IP));
        
        else if(!checkIp(tfConnexionParIP.getText()))
            throw new Exception(Langue.getTexte(Langue.ID_ERREUR_IP_INCORRECT));
        else
            return tfConnexionParIP.getText();
    }
    
    /**
     * Permet de controler si une ip est valide
     * 
     * @param ip
     * @return true si elle est correcte false sinon
     */
    public static boolean checkIp (String ip)
    {
        String [] parts = ip.split("\\.");
        
        if(parts.length != 4)
            return false;
        
        for (String s : parts)
        {
            int i = Integer.parseInt (s);

            if (i < 0 || i > 255)
                return false;
        }
        return true;
    }

    /**
     * Permet de recupérer le port en fonction de l'état des champs du formulaire
     * 
     * @return le port du serveur selectionné par l'utilisateur
     * @throws Exception s'il y des erreurs de saisie
     */
    private int recupererPort() throws Exception
    {
        if (tbServeurs.getSelectedRow() != -1)
            return Integer.parseInt((String) model.getValueAt(tbServeurs.getSelectedRow(),2));  
        else
            return Configuration.getPortSJ();
    }
    
    /**
     * Etablisssement d'une connexion avec le serveur
     * 
     * @param IP l'adresse ip du serveur
     */
    private void connexion(String IP, int port)
    {
        bRejoindre.setText(Langue.getTexte(Langue.ID_TXT_CONNEXION)+"...");
        bRejoindre.setEnabled(false);

        joueur = new Player(tfPseudo.getText());
        jeu = new Game_Client(joueur);
        jeu.setEcouteurDeClientJeu(this);
        
        try
        {
            lblEtat.setForeground(LookInterface.COULEUR_TEXTE_PRI);
            lblEtat.setText(Langue.getTexte(Langue.ID_TXT_TENTATIVE_DE_CONNEXION)+"...");
            
            try
            { 
                jeu.connexionAvecLeServeur(IP,port);
            } 
            catch (NoLocationAvailableException e)
            {
                lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
                lblEtat.setText(Langue.getTexte(Langue.ID_ERREUR_PAS_DE_PLACE));
                
                bRejoindre.setText(Langue.getTexte(Langue.ID_TXT_BTN_REJOINDRE));
                bRejoindre.setEnabled(true);
            }
        }
        catch (ConnectException e)
        {
            bRejoindre.setText(Langue.getTexte(Langue.ID_TXT_BTN_REJOINDRE));
            bRejoindre.setEnabled(true);
            
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText(Langue.getTexte(Langue.ID_ERREUR_CONNEXION_IMPOSSIBLE));
        } 
        catch (ChannelException e)
        {
            bRejoindre.setText(Langue.getTexte(Langue.ID_TXT_BTN_REJOINDRE));
            bRejoindre.setEnabled(true);
            
            lblEtat.setForeground(LookInterface.COULEUR_ERREUR);
            lblEtat.setText(Langue.getTexte(Langue.ID_ERREUR_CONNEXION_IMPOSSIBLE));
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        miseAJourListe();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Object src = e.getSource();

        if (src == tfFiltre)
        {
            if (tfFiltre.getText().equals(FILTRE_DEFAUT))
                tfFiltre.setText("");
        } else if (src == tfConnexionParIP)
        {
            tbServeurs.clearSelection();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void joueurInitialise()
    {
        // initialisation effectuee, 
        // on passe au formulaire d'attente du debut de la partie...
        
        // connexion réussie
        parent.getContentPane().removeAll();
        parent.getContentPane().add(new Panel_AttendreJoueurs(parent, jeu),
                BorderLayout.CENTER);
        parent.getContentPane().validate();
    }

    @Override
    public void joueursMisAJour()
    {
        // On ne peut rien faire. (traité par le formulaire d'attente de joueurs)
    }

    @Override
    public void messageRecu(String message, Player auteur)
    {
        // On peut pas recevoir de message dans cette partie
    }

    @Override
    public void joueurDeconnecte(Player joueur)
    {
        // On ne peut rien faire. (traité par le formulaire d'attente de joueurs)
    }

    @Override
    public void receptionEquipeAPerdue(Team equipe){}
}
