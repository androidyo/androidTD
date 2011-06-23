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
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import models.map.Field;

public class TableCellRenderer_Image implements TableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object terrain,
            boolean isSelected, boolean hasFocus, int row, int column)
    {
        
        Field t = (Field) terrain;

        BufferedImage image = new BufferedImage(t.getLargeur(), t.getHauteur(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = image.createGraphics();
        
        // image ou couleur de fond
        if(t.getImageDeFond() != null)
        {   
            Image image1 = t.getImageDeFond();
            
            for(int l=0;l<t.getLargeur();l+=image1.getWidth(null))
                for(int h=0;h<t.getHauteur();h+=image1.getHeight(null))
                    g2.drawImage(image1, l, h, null);
        }
        else
        {
            // couleur de fond
            g2.setColor(t.getCouleurDeFond());
            g2.fillRect(0, 0, t.getLargeur(), t.getHauteur());
        }
        
        // murs
        setTransparence(t.getOpaciteMurs(),g2);
            
        g2.setColor(t.getCouleurMurs());
        ArrayList<Rectangle> murs = t.getMurs();
        g2.setColor(t.getCouleurMurs());
        for(Rectangle mur : murs)
            dessinerZone(mur,g2);
        
        setTransparence(1.f,g2);

        return new JLabel(new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
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
