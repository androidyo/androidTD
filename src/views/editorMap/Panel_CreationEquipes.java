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

import i18n.Langue;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import views.ManageFonts;
import views.LookInterface;
import views.common.Panel_Table;
import models.game.Game;
import models.map.Field;
import models.player.PlayerLocation;
import models.player.Team;

/**
 * Panel de création d'équipes.
 * 
 * Ce panel permet de gerer la creation des equipes. Le terrain contient les 
 * equipes qu'il peut accueillir pour une partie.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juillet 2010
 * @since jdk1.6.0_16
 * @see Field
 * @see Team
 */
public class Panel_CreationEquipes extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
    
    // Images
    private static final ImageIcon I_AJOUTER_EQUIPE = new ImageIcon("img/icones/flag_add.gif");
    private static final ImageIcon I_AJOUTER = new ImageIcon("img/icones/add.png");
    private static final ImageIcon I_SUPPRIMER = new ImageIcon("img/icones/delete.png");
    private static final ImageIcon I_PARAMETRES = new ImageIcon("img/icones/wrench.png");
    private static final ImageIcon I_SELECTION_ZONE = new ImageIcon("img/icones/shape_handles.png");
    
    /**
     * Le jeu
     */
    private Game jeu;
     
    /**
     * id d'autoincrementation des equipes
     */
    private int idAutoIncrEquipe;
    
    /**
     * Tableau des equipes (visualisation)
     */
    private Panel_Table pTabEquipes = new Panel_Table();   
    
    /**
     * id d'autoincrementation des emplacements de joueurs
     */
    private int idAutoIncrEJ;
    
    /**
     * Le panel de creation du terrain (pour modification direct)
     * 
     * Ce serait peut-etre mieux de faire un ecouteur...
     */
    private Panel_CreationTerrain panelCreationTerrain;
    
    /**
     * Bouton de creation d'une equipe
     */
    private JButton bCreerEquipe = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_CREER),I_AJOUTER_EQUIPE);
    
    /**
     * Constructeur
     * 
     * @param jeu le jeu
     * @param panelCreationTerrain le panel de creation du terrain
     */
    public Panel_CreationEquipes(Game jeu,Panel_CreationTerrain panelCreationTerrain)
    {
        super(new BorderLayout());
        setOpaque(false);
        
        this.jeu = jeu;
        this.panelCreationTerrain = panelCreationTerrain;
        
        // Initialisation des id au cas ou le terrain du jeu contient deja des equipes
        initIds();

        // Bouton d'ajout d'une equipe
        ManageFonts.setStyle(bCreerEquipe);
        bCreerEquipe.addActionListener(this);
        bCreerEquipe.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(bCreerEquipe,BorderLayout.NORTH);
        
        // Tableau des equipes
        pTabEquipes.setBackground(LookInterface.COULEUR_DE_FOND_SEC);
        JScrollPane jsTest = new JScrollPane(pTabEquipes);
        jsTest.setBorder(new EmptyBorder(0,0,0,0));
        jsTest.setOpaque(false); 
        add(jsTest,BorderLayout.CENTER);
        
        // Remplissage du panel des equipes
        construirePanelEquipes();
        
        // Taille du panel
        setPreferredSize(new Dimension(400,200));
    }

    /**
     * Permet de contruire le panel des equipes.
     * 
     * Le panel est dabord vide puis reconstruit
     */
    private void construirePanelEquipes()
    {
        // vidage
        pTabEquipes.removeAll();

        // ligne courante
        int ligne=0;
        
        // reinitialisation des ids des emplacements de joueurs
        idAutoIncrEJ = 1;
        
        // Pour chaque equipe du jeu...
        for(final Team equipe : jeu.getEquipes())
        {
            // Separateur
            JPanel lTrait = new JPanel();
            lTrait.setBorder(new LineBorder(LookInterface.COULEUR_DE_FOND_PRI, 4));
            lTrait.setPreferredSize(new Dimension(380,8));
            pTabEquipes.add(lTrait,0,ligne++,5,1);
            
            // Nom de l'équipe
            final JLabel lNomEquipe = new JLabel(equipe.getNom());
            lNomEquipe.setFont(ManageFonts.POLICE_TITRE);
            lNomEquipe.setForeground(equipe.getCouleur());
            pTabEquipes.add(lNomEquipe,0,ligne);
            
            // Edition de l'equipe
            final JButton bEditerEquipe = new JButton(I_PARAMETRES);
            ManageFonts.setStyle(bEditerEquipe);
            final Panel_CreationEquipes pce = this;
            bEditerEquipe.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    new View_EditionEquipe(pce,equipe);
                }
            });
            pTabEquipes.add(bEditerEquipe,1,ligne);
            
            // Suppression de l'equipe
            final JButton bSupprimerEquipe = new JButton(I_SUPPRIMER);
            ManageFonts.setStyle(bSupprimerEquipe);
            pTabEquipes.add(bSupprimerEquipe,3,ligne);
            
            if(jeu.getEquipes().size() < 2) // Minimum 1 equipe
                bSupprimerEquipe.setEnabled(false);
            else
            {
                bSupprimerEquipe.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        jeu.supprimerEquipe(equipe);
                        
                        construirePanelEquipes();
                    } 
                });
            }
            
            ligne++;
            
            // Arrivee et departs
            // TODO Traduire
            JLabel lZonesDepart = new JLabel("Arrivee et departs");
            lZonesDepart.setFont(ManageFonts.POLICE_SOUS_TITRE);
            pTabEquipes.add(lZonesDepart,0,ligne);
            
            JButton bAjouterZonesDepart = new JButton(I_AJOUTER);
            ManageFonts.setStyle(bAjouterZonesDepart);
            bAjouterZonesDepart.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    equipe.ajouterZoneDepartCreatures(new Rectangle(0,0,40,40));
                    
                    construirePanelEquipes();
                }
            });
            
            pTabEquipes.add(bAjouterZonesDepart,1,ligne);
            
            ligne++;
            
            
            // Zone d'arrivée
            final JLabel bZoneArrive = new JLabel(Langue.getTexte(Langue.ID_TXT_BTN_ARRIVEE));
            ManageFonts.setStyle(bZoneArrive);
            pTabEquipes.add(bZoneArrive,0,ligne);
            
            final JButton bSelectionZoneArrive = new JButton(I_SELECTION_ZONE);
            ManageFonts.setStyle(bSelectionZoneArrive);
            pTabEquipes.add(bSelectionZoneArrive,2,ligne);
            
            bSelectionZoneArrive.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    panelCreationTerrain.setRecEnTraitement(equipe.getZoneArriveeCreatures());
                }
            });

            ligne++;
            
            
            // Pour chaque zones de depart
            for(int noZD=0;noZD<equipe.getNbZonesDepart();noZD++)
            {
                final Rectangle z = equipe.getZoneDepartCreatures(noZD);

                // Zone de depart
                final JLabel bZoneDepart = new JLabel(Langue.getTexte(Langue.ID_TXT_BTN_DEPART) + " - " + noZD);
                ManageFonts.setStyle(bZoneDepart);
                pTabEquipes.add(bZoneDepart,0,ligne);                
                
                final JButton bSelectionZoneDepart = new JButton(I_SELECTION_ZONE);
                ManageFonts.setStyle(bSelectionZoneDepart);
                pTabEquipes.add(bSelectionZoneDepart,2,ligne);
                
                bSelectionZoneDepart.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        panelCreationTerrain.setRecEnTraitement(z);
                    }
                });
              
                final JButton bSupprimerZoneDepart = new JButton(I_SUPPRIMER);
                ManageFonts.setStyle(bSupprimerZoneDepart);
                pTabEquipes.add(bSupprimerZoneDepart,3,ligne);
                
                if(equipe.getNbZonesDepart() == 1)
                    bSupprimerZoneDepart.setEnabled(false);
                
                bSupprimerZoneDepart.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        if(equipe.getNbZonesDepart() > 1)
                        {
                            equipe.suppimerZoneDepart(z);
                            
                            construirePanelEquipes();
                        }     
                    }
                });
                
                ligne++;
            }      
            ligne++;  
            
            // -----------------------------
            // -- Emplacements de joueurs --
            // -----------------------------
            JLabel lEmplacementsDeJoueurs = new JLabel("Empl. Joueurs");
            lEmplacementsDeJoueurs.setFont(ManageFonts.POLICE_SOUS_TITRE);
            pTabEquipes.add(lEmplacementsDeJoueurs,0,ligne);
            
            final JButton bNouvelEmplacement = new JButton(I_AJOUTER);
            ManageFonts.setStyle(bNouvelEmplacement);
            pTabEquipes.add(bNouvelEmplacement,1,ligne);
            
            bNouvelEmplacement.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    equipe.ajouterEmplacementJoueur(new PlayerLocation(idAutoIncrEJ++, new Rectangle(0,0,jeu.getTerrain().getLargeur(),jeu.getTerrain().getHauteur())));
                
                    construirePanelEquipes();
                }
            });
            ligne++;

            for(final PlayerLocation ej : equipe.getEmplacementsJoueur())
            { 
                
                JLabel lNomEmplacement = new JLabel(Langue.getTexte(Langue.ID_TXT_ZONE_JOUEUR)+ej.getId());
                ManageFonts.setStyle(lNomEmplacement);
                pTabEquipes.add(lNomEmplacement,0,ligne);
                
                // Couleur
                final JButton bCouleurEmplacement = new JButton(I_PARAMETRES);
                ManageFonts.setStyle(bCouleurEmplacement);
                pTabEquipes.add(bCouleurEmplacement,1,ligne);
                bCouleurEmplacement.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        Color couleur = JColorChooser.showDialog(
                                null,"",ej.getCouleur());
                          
                        if(couleur != null)
                        {
                            ej.setCouleur(couleur);
                            bCouleurEmplacement.setBackground(couleur);
                        } 
                    } 
                });
                
                // Selection
                final JButton bSelectionnerEmplacement = new JButton(I_SELECTION_ZONE);
                ManageFonts.setStyle(bSelectionnerEmplacement);
                pTabEquipes.add(bSelectionnerEmplacement,2,ligne);
                bSelectionnerEmplacement.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        panelCreationTerrain.setRecEnTraitement(ej.getZoneDeConstruction());
                    } 
                });
                
                if(ej.getId() >= idAutoIncrEJ)
                    idAutoIncrEJ = ej.getId() + 1;
                
                
                // Suppression
                final JButton bSupprimerEmplacement = new JButton(I_SUPPRIMER);
                ManageFonts.setStyle(bSupprimerEmplacement);
                pTabEquipes.add(bSupprimerEmplacement,3,ligne);
                
                if(equipe.getNbEmplacements() == 1)
                    bSupprimerEmplacement.setEnabled(false);
                
                bSupprimerEmplacement.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        equipe.supprimerEmplacement(ej);
                        
                        construirePanelEquipes();
                    } 
                });
                
                ligne++;
            }
        }

        pTabEquipes.repaint();
        pTabEquipes.revalidate();
        pTabEquipes.validate();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == bCreerEquipe)
        {
            Team equipe = new Team(idAutoIncrEquipe, Langue.getTexte(Langue.ID_TXT_EQUIPE)+" "+idAutoIncrEquipe, Color.BLACK);
            
            jeu.ajouterEquipe(equipe);
            
            // TODO positionner au milieu du viewport
            equipe.ajouterZoneDepart(new Rectangle(40,40,40,40));
            equipe.setZoneArriveeCreatures(new Rectangle(140,140,40,40));
            
            equipe.ajouterEmplacementJoueur(new PlayerLocation(idAutoIncrEJ++, new Rectangle(0,0,jeu.getTerrain().getLargeur(),jeu.getTerrain().getHauteur())));

            idAutoIncrEquipe++;
            
            construirePanelEquipes(); 
        }
    }

    /**
     * Permet de mettre a jour tout le panel
     */
    public void miseAJour()
    {
        initIds();
        construirePanelEquipes();
    }

    /**
     * Initilise les ids en fonction de l'etat actuel du jeu
     */
    private void initIds() {
        
        // initialisation de l'id courant -> recupere l'id le plus grand + 1
        idAutoIncrEquipe = 0;
        for(Team e : jeu.getEquipes())
            if(e.getId() > idAutoIncrEquipe)
                idAutoIncrEquipe = e.getId();
        idAutoIncrEquipe++;
    }
}
