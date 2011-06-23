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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

import models.utils.Score;

/**
 * Panel d'affichage des étoiles
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class Panel_Star extends JPanel
{
    private static final long serialVersionUID = 1L;
    private static final int LARGEUR = 100;
    private static final Image ETOILE;
    private Score score;
    
    static
    {
        ETOILE = Toolkit.getDefaultToolkit().getImage("img/icones/star.png");
    }
    
    /**
     * Contructeur 
     * 
     * @param score le score
     */
    public Panel_Star(Score score)
    {
        this.score = score;
        setPreferredSize(new Dimension(LARGEUR,ETOILE.getHeight(null)+4));
    }
    
    @Override
    public void paint(Graphics g)
    {
        for(int i=score.getNbEtoiles();i > 0; i--)
            g.drawImage(ETOILE, i*ETOILE.getWidth(null)+i*6, 0, null);
    }
}
