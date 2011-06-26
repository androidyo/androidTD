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

import java.awt.*;
import java.awt.event.*;

import views.common.*;
import models.game.Game;
import models.map.Field;
import models.player.Team;

/**
 * Panel de conception graphique du terrain.
 * 
 * Ce panel hérite du panel d'affichage du terrain de jeu durant la partie.
 * 
 * @author Aurelien Da Campo
 * @version 1.1 | mars 2011
 * @since jdk1.6.0_16
 * @see Field
 */
public class Panel_CreationTerrain extends Panel_Terrain {
    private static final long serialVersionUID = 1L;
    private static final int MODE_DEPLACEMENT = 0;
    private static final int MODE_TRAITEMENT_REC = 1;

    // private Terrain terrain;
    private int mode = MODE_TRAITEMENT_REC;
    private Rectangle recEnTraitement;

    /**
     * largeur d'un case du maillage pour le positionnement des tours
     */
    private static final int CADRILLAGE = 10; // unite du cadriallage en pixel

    public Panel_CreationTerrain(Game jeu, EcouteurDePanelTerrain edpt) {
        super(jeu, edpt);

        afficherQuadrillage = true;
    }

    private int taillePoignee = 6;
    private int taillePoigneeSur2 = taillePoignee / 2;
    private boolean redimGrab;

    private static final int POIGNEE_DROITE = 0;
    private static final int POIGNEE_GAUCHE = 1;
    private static final int POIGNEE_BAS = 2;
    private static final int POIGNEE_HAUT = 3;
    private static final int POIGNEE_DROITE_HAUT = 4;
    private static final int POIGNEE_GAUCHE_HAUT = 5;
    private static final int POIGNEE_GAUCHE_BAS = 6;
    private static final int POIGNEE_DROITE_BAS = 7;

    /**
     * la poignee qui est agripee
     */
    private int poigneeGrab;
    private Rectangle recEnTraitementOriginal;
    private boolean deplGrab;
    private EcouteurDePanelCreationTerrain edpct;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (recEnTraitement != null) {
            setTransparence(1.0f, g2);

            // dessin du tour (selection)
            g2.setColor(Color.WHITE);
            g.drawRect(recEnTraitement.x, recEnTraitement.y,
                    recEnTraitement.width, recEnTraitement.height);

            // dessin des poignées
            g2.setColor(Color.RED);
            Rectangle poignee;
            for (int i = 0; i < 8; i++) {
                poignee = getPoignee(recEnTraitement, i);
                g2.fillRect(poignee.x, poignee.y, poignee.width, poignee.height);
            }
        }
    }

    private Rectangle getPoignee(Rectangle rectangle, int i) {
        final int L_SUR_2 = recEnTraitement.width / 2;
        final int H_SUR_2 = recEnTraitement.height / 2;

        taillePoignee = (int) (6 / coeffTaille);
        taillePoigneeSur2 = taillePoignee / 2;

        switch (i) {
        case POIGNEE_DROITE:
            return new Rectangle(recEnTraitement.x + recEnTraitement.width
                    - taillePoigneeSur2, recEnTraitement.y + H_SUR_2
                    - taillePoigneeSur2, taillePoignee, taillePoignee);
        case POIGNEE_GAUCHE:
            return new Rectangle(recEnTraitement.x - taillePoigneeSur2,
                    recEnTraitement.y + H_SUR_2 - taillePoigneeSur2,
                    taillePoignee, taillePoignee);
        case POIGNEE_HAUT:
            return new Rectangle(recEnTraitement.x + L_SUR_2
                    - taillePoigneeSur2, recEnTraitement.y
                    + recEnTraitement.height - taillePoigneeSur2,
                    taillePoignee, taillePoignee);
        case POIGNEE_BAS:
            return new Rectangle(recEnTraitement.x + L_SUR_2
                    - taillePoigneeSur2, recEnTraitement.y - taillePoigneeSur2,
                    taillePoignee, taillePoignee);

        case POIGNEE_DROITE_HAUT:
            return new Rectangle(recEnTraitement.x + recEnTraitement.width
                    - taillePoigneeSur2, recEnTraitement.y
                    + recEnTraitement.height - taillePoigneeSur2,
                    taillePoignee, taillePoignee);
        case POIGNEE_GAUCHE_HAUT:
            return new Rectangle(recEnTraitement.x - taillePoigneeSur2,
                    recEnTraitement.y + recEnTraitement.height
                            - taillePoigneeSur2, taillePoignee, taillePoignee);
        case POIGNEE_GAUCHE_BAS:
            return new Rectangle(recEnTraitement.x - taillePoigneeSur2,
                    recEnTraitement.y - taillePoigneeSur2, taillePoignee,
                    taillePoignee);
        case POIGNEE_DROITE_BAS:
            return new Rectangle(recEnTraitement.x + recEnTraitement.width
                    - taillePoigneeSur2, recEnTraitement.y - taillePoigneeSur2,
                    taillePoignee, taillePoignee);
        }

        return null;
    }

    @Override
    public void mousePressed(MouseEvent me) {
        boutonGrab = me.getButton();

        switch (mode) {
        case MODE_DEPLACEMENT:
            super.mousePressed(me);
            break;

        case MODE_TRAITEMENT_REC:

            sourisGrabX = me.getX();
            sourisGrabY = me.getY();

            decaleGrabX = decaleX;
            decaleGrabY = decaleY;

            redimGrab = false;
            deplGrab = false;

            Point p = getCoordoneeSurTerrainOriginal(me.getPoint());

            if (recEnTraitement != null) {
                // Contact avec une poignée ?
                Rectangle poignee;
                for (int i = 0; i < 8; i++) {
                    poignee = getPoignee(recEnTraitement, i);

                    if (poignee.contains(p)) {
                        redimGrab = true;
                        poigneeGrab = i;
                        return;
                        // recEnTraitement.width+=10;
                    }
                }
            }

            // si il y une zone de départ ou d'arrivee
            for (Team e : jeu.getTeams()) {

                // zone de départ
                for (int i = 0; i < e.getNbZonesDepart(); i++) {
                    Rectangle r = e.getZoneDepartCreatures(i);

                    if (r.contains(p)) {
                        setRecEnTraitement(r);
                        return;
                    }
                }

                // zone d'arrive
                if (e.getZoneArrivalCreatures().contains(p)) {
                    setRecEnTraitement(e.getZoneArrivalCreatures());
                    return;
                }
            }

            // si il a un mur
            for (Rectangle r : jeu.getTerrain().getMurs())
                if (r.contains(p)) {
                    setRecEnTraitement(r);

                    if (edpct != null)
                        edpct.zoneSelectionnee(r);

                    return;
                }

            // si il y une zone de construction
            /*
             * for(Equipe e : jeu.getEquipes()) { for(EmplacementJoueur ej :
             * e.getEmplacementsJoueur()) {
             * if(ej.getZoneDeConstruction().contains(me.getPoint())) {
             * setRecEnTraitement(ej.getZoneDeConstruction()); return; } } }
             */

            break;
        }

    }

    @Override
    public void mouseMoved(MouseEvent me) {
        switch (mode) {
            case MODE_DEPLACEMENT:
                super.mouseMoved(me);
                break;
    
            case MODE_TRAITEMENT_REC:
                super.mouseMoved(me);
                
                if(recEnTraitement != null)
                {
                    sourisGrabX = me.getX();
                    sourisGrabY = me.getY();

                    decaleGrabX = decaleX;
                    decaleGrabY = decaleY;

                    redimGrab = false;
                    deplGrab = false;

                    Point p = getCoordoneeSurTerrainOriginal(me.getPoint());

                    if (recEnTraitement != null) {
                       
                        // Contact avec une poignée ?
                        Rectangle poignee;
                        for (int i = 0; i < 8; i++) {
                            poignee = getPoignee(recEnTraitement, i);

                            if (poignee.contains(p)) {
                                
                                switch(i)
                                {
                                    case POIGNEE_GAUCHE:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                                        return;
                                    case POIGNEE_DROITE:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                                        return;
                                    case POIGNEE_BAS:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                                        return;
                                    case POIGNEE_HAUT:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                                        return;
                                    case POIGNEE_DROITE_HAUT:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                                        return;
                                    case POIGNEE_GAUCHE_HAUT:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                                        return;
                                    case POIGNEE_GAUCHE_BAS:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                                        return;
                                    case POIGNEE_DROITE_BAS:
                                        setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                                        return;
                                
                                }
                                // recEnTraitement.width+=10;
                            }
                        }
                        
                        // Contact avec la surface d'un mur
                        if(recEnTraitement.contains(p))
                        {
                            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        }
                        
                    } 
                    
                }
                
                break;
        }
    }

    /**
     * Methode de gestion du clique enfoncé de la souris lorsque qu'elle bouge.
     * 
     * @param me evenement lie a cette action
     * @see MouseMotionListener
     */
    @Override
    public void mouseDragged(MouseEvent me) {
        switch (mode) {
        case MODE_DEPLACEMENT:
            super.mouseDragged(me);
            break;

        case MODE_TRAITEMENT_REC:

            Point p = getCoordoneeSurTerrainOriginal(me.getPoint());
            Point sourisGrab = getCoordoneeSurTerrainOriginal(sourisGrabX,
                    sourisGrabY);
            
            if (boutonGrab == MouseEvent.BUTTON1) {
                if (recEnTraitement != null) {
                    if (redimGrab) {
                        Rectangle tmpRec = new Rectangle(recEnTraitement);

                        Boolean g = false, d = false, h = false, b = false;

                        switch (poigneeGrab) {
                        case POIGNEE_GAUCHE:
                            g = true;
                            break;
                        case POIGNEE_DROITE:
                            d = true;
                            break;
                        case POIGNEE_BAS:
                            b = true;
                            break;
                        case POIGNEE_HAUT:
                            h = true;
                            break;
                        case POIGNEE_DROITE_HAUT:
                            d = true;
                            h = true;
                            break;
                        case POIGNEE_GAUCHE_HAUT:
                            g = true;
                            h = true;
                            break;
                        case POIGNEE_GAUCHE_BAS:
                            g = true;
                            b = true;
                            break;
                        case POIGNEE_DROITE_BAS:
                            d = true;
                            b = true;
                            break;
                        }

                        if (g) {
                            recEnTraitement.width = getLongueurSurGrillage(sourisGrab.x
                                    - p.x + recEnTraitementOriginal.width);
                            recEnTraitement.x = recEnTraitementOriginal.x
                                    + (recEnTraitementOriginal.width - recEnTraitement.width);
                        }

                        if (d) {
                            recEnTraitement.width = getLongueurSurGrillage(p.x
                                    - sourisGrab.x
                                    + recEnTraitementOriginal.width);
                        }

                        if (b) {
                            recEnTraitement.height = getLongueurSurGrillage(sourisGrab.y
                                    - p.y + recEnTraitementOriginal.height);
                            recEnTraitement.y = recEnTraitementOriginal.y
                                    + (recEnTraitementOriginal.height - recEnTraitement.height);
                        }

                        if (h) {
                            recEnTraitement.height = getLongueurSurGrillage(p.y
                                    - sourisGrab.y
                                    + recEnTraitementOriginal.height);
                        }

                        // pas de taille négative
                        if (recEnTraitement.width <= 0) {
                            recEnTraitement.width = tmpRec.width;
                            recEnTraitement.x = tmpRec.x;
                        }
                        if (recEnTraitement.height <= 0) {
                            recEnTraitement.height = tmpRec.height;
                            recEnTraitement.y = tmpRec.y;
                        }

                        if (edpct != null)
                            edpct.zoneModifiee(recEnTraitement);
                    } else if (deplGrab) {
                        Point pSouris = getCoordoneeSurTerrainOriginal(me
                                .getPoint());

                        recEnTraitement.x = getPositionSurQuadrillage(recEnTraitementOriginal.x
                                + pSouris.x - sourisGrab.x);
                        recEnTraitement.y = getPositionSurQuadrillage(recEnTraitementOriginal.y
                                + pSouris.y - sourisGrab.y);

                        if (edpct != null)
                            edpct.zoneModifiee(recEnTraitement);
                    }
                }
            }

            break;
        }
    }

    private int getLongueurSurGrillage(int longueur) {
        return longueur - longueur % CADRILLAGE;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (mode) {
        case MODE_DEPLACEMENT:
            super.mouseReleased(e);
            break;

        case MODE_TRAITEMENT_REC:

            // On clique sans opération particulière
            // -> sélection d'un élément
            if (!deplGrab && !redimGrab) {
                Point p = getCoordoneeSurTerrainOriginal(e.getPoint());

                // si il a un mur
                for (Rectangle r : jeu.getTerrain().getMurs())
                    if (r.contains(p)) {
                        setRecEnTraitement(r);

                        if (edpct != null)
                            edpct.zoneSelectionnee(r);

                        return;
                    }

                if(e.getButton() == MouseEvent.BUTTON1)
                {
                    // creation d'un mur si pas de mur
                    setRecEnTraitement(new Rectangle(
                            getPositionSurQuadrillage(p.x),
                            getPositionSurQuadrillage(p.y), 20, 20));
    
                    if (edpct != null)
                        edpct.zoneSelectionnee(recEnTraitement);
    
                    jeu.getTerrain().addWall(recEnTraitement);
                }
                else if(e.getButton() == MouseEvent.BUTTON3)
                {
                    deselectionnerRecEnTraitement();
                    
                    if (edpct != null)
                        edpct.zoneSelectionnee(null);
                }
            }

            // MISE A JOUR DU RECTANGLE ORIGINAL
            else if (deplGrab) {
                // Après un déplacement, la position du rectangle original est
                // mis à jour
                recEnTraitementOriginal.x = recEnTraitement.x;
                recEnTraitementOriginal.y = recEnTraitement.y;
            } else if (redimGrab) {
                // Après un redimentionnement, la position et la taille
                // du rectangle original est mis à jour
                recEnTraitementOriginal.x = recEnTraitement.x;
                recEnTraitementOriginal.y = recEnTraitement.y;
                recEnTraitementOriginal.width = recEnTraitement.width;
                recEnTraitementOriginal.height = recEnTraitement.height;
            }

            break;
        }
    }

    void setRecEnTraitement(Rectangle r) {
        recEnTraitement = r;
        deplGrab = true;
        recEnTraitementOriginal = new Rectangle(recEnTraitement);
        mode = MODE_TRAITEMENT_REC;
    }

    void deselectionnerRecEnTraitement() {
        recEnTraitement = null;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        switch (mode) {
        case MODE_DEPLACEMENT:
            super.keyReleased(ke);
            break;

        case MODE_TRAITEMENT_REC:

            if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
                if (recEnTraitement != null) {
                    jeu.getTerrain().supprimerMur(recEnTraitement);
                    recEnTraitement = null;
                }
            }

            break;
        }
    }

    public void activerModeCreationMurs() {
        mode = MODE_TRAITEMENT_REC;
    }

    public void activerModeDeplacement() {
        recEnTraitement = null;

        mode = MODE_DEPLACEMENT;
    }

    public void setEcouteurDeCreationTerrain(
            EcouteurDePanelCreationTerrain edpct) {
        this.edpct = edpct;
    }

    public Rectangle getRecEnTraitement() {
        return recEnTraitement;
    }
}
