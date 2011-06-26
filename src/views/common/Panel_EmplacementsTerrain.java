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

package views.common;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import views.ManageFonts;
import models.map.Field;
import models.player.PlayerLocation;
import models.player.Team;

/**
 * Panel de visualisation des emplacement de jeu
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class Panel_EmplacementsTerrain extends JPanel
{
    private static final long serialVersionUID = 1L;
    private Field terrain;
    private double scaleX = 0.7;
    private static final int xOffsetPseudo = 10, 
                             yOffsetPseudo = 30;
 
    // TODO proportionnel au plus grand coté
    private int largeur;
    
    @SuppressWarnings("unused")
    private int hauteur; 
    
    public Panel_EmplacementsTerrain(Field terrain, int largeur, int hauteur)
    {
        this.terrain = terrain;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.scaleX = 1.0 / terrain.getLargeur() * largeur;
        setPreferredSize(new Dimension(largeur,hauteur));
    }
    
    
    public Panel_EmplacementsTerrain(int largeur, int hauteur)
    {
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        if(terrain != null)
        { 
            g2.scale(scaleX, scaleX);

            // image ou couleur de fond
            if(terrain.getImageDeFond() != null)
            {   
                Image image1 = terrain.getImageDeFond();
                
                for(int l=0;l<terrain.getLargeur();l+=image1.getWidth(null))
                    for(int h=0;h<terrain.getHauteur();h+=image1.getHeight(null))
                        g2.drawImage(image1, l, h, null);
            }
            else
            {
                // couleur de fond
                g2.setColor(terrain.getCouleurDeFond());
                g2.fillRect(0, 0, terrain.getLargeur(), terrain.getHauteur());
            }
            
            // murs
            setTransparence(terrain.getOpaciteMurs(), g2);
            
            g2.setColor(terrain.getCouleurMurs());
            ArrayList<Rectangle> murs = terrain.getMurs();
            g2.setColor(terrain.getCouleurMurs());
            for(Rectangle mur : murs)
                dessinerZone(mur,g2);
            
            setTransparence(1.0f, g2);
            
            
            //---------------------------------------
            //-- affichage des zone de contruction --
            //---------------------------------------
            Rectangle zone;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            for (Team e : terrain.getEquipesInitiales())
                for (PlayerLocation ej : e.getPlayerLocations())
                {
                    zone = ej.getZoneDeConstruction();
    
                    g2.setColor(ej.getCouleur());
                    g2.fillRect(zone.x, zone.y, zone.width, zone.height); 
                }
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
            
            int oldy = -1000;
            int lastOffset = 1;
            for (Team e : terrain.getEquipesInitiales())
                for (PlayerLocation ej : e.getPlayerLocations())
                {
                    zone = ej.getZoneDeConstruction();
                    
                    if(ej.getPlayer() != null)
                    {
                        g2.setFont(ManageFonts.POLICE_TITRE);
                        
                        int x = zone.x+xOffsetPseudo;
                        int y = zone.y+yOffsetPseudo;
                        
                        if(oldy == y)
                        {
                           y = y + 50 * lastOffset;
                           lastOffset *= -1; 
                        } 
                        
                        oldy = y;
                        
                        g2.setColor(e.getColor());
                        g2.drawString(ej.getPlayer().getPseudo(), x, y);
                        
                        g2.setColor(Color.BLACK);
                        g2.drawString(ej.getPlayer().getPseudo(), x+2, y+2);
                    }
                }
        }
        else
        {
            g2.drawString("Pas de terrain", 10, 10); 
        }
    }

    public void setTerrain(Field terrain)
    {
        this.terrain = terrain;
         
        scaleX = 1.0 / terrain.getLargeur() * largeur;
        setPreferredSize(new Dimension((int)(terrain.getLargeur()*scaleX),(int)(terrain.getHauteur()*scaleX)));
        validate();
        revalidate();
        
        
        repaint();
    }
    
    /**
     * Permet de dessiner une zone rectangulaire sur le terrain.
     * 
     * @param zone la zone rectangulaire
     * @param g2 le Graphics2D pour dessiner
     */
    private void dessinerZone(final Rectangle zone, final Graphics2D g2)
    {
        g2.fillRect((int) zone.getX(), 
                    (int) zone.getY(), 
                    (int) zone.getWidth(), 
                    (int) zone.getHeight());
    }
    
    /**
     * Permet de modifier la transparence du Graphics2D
     * 
     * @param tauxTransparence le taux (1.f = 100% opaque et 0.f = 100% transparent)
     * @param g2 le Graphics2D a configurer
     */
    protected void setTransparence(float tauxTransparence, Graphics2D g2)
    {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tauxTransparence));
    }
}
