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

package views.single;

import i18n.Langue;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import utils.Configuration;
import views.ManageFonts;
import views.LookInterface;
import models.utils.HighScores;

/**
 * Fenetre qui informe le joueur que la partie est terminee (il a perdu).
 * 
 * Permet aussi d'ajouter un score au fichier des meilleurs scores.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 17 decembre 2009
 * @since jdk1.6.0_16
 * @see HighScores
 */
public class View_DialogGameOver extends JDialog implements ActionListener
{
    // constantes statiques
    private static final long serialVersionUID      = 1L;
    private static final String TITRE_FORM          = Langue.getTexte(Langue.ID_TITRE_PARTIE_TERMINEE);
    
    // membrea graphiques
    private JButton bOk             = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_OK));
    private JButton bAnnuler        = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_FERMER));
    private JTextField tfPseudo  = new JTextField();
    private JPanel pFormulaire;
    private String nomTerrain;
    
    // autres membres
    private int score;
    private long dureePartie;
    
    /**
     * Constructeur de la fenetre
     * @param score le score a ajouter
     */
    public View_DialogGameOver(Frame fenParent, int score, long dureePartie, String nomTerrain)
    {
        // modal preferences de la fenetre
        super(fenParent,Langue.getTexte(Langue.ID_TITRE_PARTIE_TERMINEE),true); 
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(LookInterface.COULEUR_DE_FOND_PRI);
        
        // init attributs membres
        this.score      = score;
        this.dureePartie = dureePartie;
        this.nomTerrain = nomTerrain;
        
        // constructeur du formulaire
        pFormulaire = new JPanel(new GridLayout(0,2));
        pFormulaire.setOpaque(false);
        pFormulaire.setBorder(new EmptyBorder(20,20,20,20));
        
        pFormulaire.add(new JLabel(Langue.getTexte(Langue.ID_TXT_SCORE_OBTENU)+" : "));
        pFormulaire.add(new JLabel(score+""));
        
        pFormulaire.add(new JLabel(Langue.getTexte(Langue.ID_TXT_VOTRE_PSEUDO)+" : "));
        tfPseudo.setText(Configuration.getPseudoJoueur());
        pFormulaire.add(tfPseudo);
        pFormulaire.add(bAnnuler);
        bAnnuler.addActionListener(this);
        ManageFonts.setStyle(bAnnuler);
        pFormulaire.add(bOk);
        
        getRootPane().setDefaultButton(bOk); // def button
        ManageFonts.setStyle(bOk);
        bOk.addActionListener(this);
        
        
        JPanel conteneurTitre = new JPanel(new FlowLayout());
        conteneurTitre.setOpaque(false);
        JLabel lTitreForm = new JLabel(TITRE_FORM);
        lTitreForm.setFont(ManageFonts.POLICE_TITRE);
        conteneurTitre.add(lTitreForm);
        
        getContentPane().add(conteneurTitre,BorderLayout.NORTH);
        getContentPane().add(pFormulaire,BorderLayout.SOUTH);
        
        // derniers parametres de la fenetre
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
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
        
        if(source == bOk)
        {
            // le nom n'est pas vide
            if(!tfPseudo.getText().isEmpty())
            {
                // mise à jour du pseudo par defaut
                Configuration.setPseudoJoueur(tfPseudo.getText());
                
                // ajout du nouveau score
                HighScores ms = new HighScores(nomTerrain);
                ms.ajouterMeilleurScore(tfPseudo.getText(), score, dureePartie);
                
                dispose(); // fermeture
                
                // ouverture de la fenetre des meilleurs scores
                new View_TopScores(nomTerrain,this);
            } 
        }
        else
        {
            dispose(); // fermeture
        }
    }
}
