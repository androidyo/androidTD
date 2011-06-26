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

package models.grid;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;

import models.utils.Tools;

@SuppressWarnings("serial")
public class Window_RepresentationGrid extends JFrame
{
    class Panel_Maillage extends JPanel implements ActionListener
    {
        private final int NB_CHEMINS_VISIBLES = 100;
        private Maillage m;
        private JButton bRecalculer;
        private JButton bCalculerDijsktra;
        
        public Panel_Maillage(Maillage maillage)
        {
            m = maillage;
            
            // boutons
            bCalculerDijsktra = new JButton("Calculer une fois Dijkstra");
            bCalculerDijsktra.addActionListener(this);
            add(bCalculerDijsktra);
            
            bRecalculer = new JButton("Recalculer "+NB_CHEMINS_VISIBLES+" chemins");
            bRecalculer.addActionListener(this);
            add(bRecalculer);

            setPreferredSize(new Dimension(m.getLargeurPixels(),m.getHauteurPixels())); 
        }

        @Override
        public void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g;
            
            // background
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, 2000, 2000);
            
            // affichage des arcs
            g2.setColor(Color.yellow);
            
            for(Line2D arc : m.getArcs())
                dessinerArc(arc, g2);
            
            // affichage des noeuds
            g2.setColor(Color.RED);
            for(Node noeud : m.getNoeuds())
                g2.fillOval(noeud.x - 1, noeud.y - 1, 2, 2);
            
            g2.setColor(Color.GREEN);
            for(int i=0;i<NB_CHEMINS_VISIBLES;i++)
                afficherCheminPourNoeud(
                        Tools.tirerNombrePseudoAleatoire(0, m.getLargeurPixels()),
                        Tools.tirerNombrePseudoAleatoire(0, m.getHauteurPixels()),
                        g2);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            
            if(source == bCalculerDijsktra)
            {
                m.disableZone(new Rectangle(Tools.tirerNombrePseudoAleatoire(0, m.getLargeurPixels()),
                        Tools.tirerNombrePseudoAleatoire(0, m.getHauteurPixels()),
                        Tools.tirerNombrePseudoAleatoire(20, 200),
                        Tools.tirerNombrePseudoAleatoire(20, 200)),true);
            }

            repaint();
        }
        
        private void afficherCheminPourNoeud(int x, int y, Graphics2D g2)
        {  
            ArrayList<Point> chemin;
            
            try 
            {
                chemin = m.plusCourtChemin(x, y, 0, 0);
                
                if(chemin.size() > 0)
                {
                    Point pPrec = chemin.get(0);
                    for(int i=1;i<chemin.size();i++)
                    {
                        Point p =  chemin.get(i);
 
                        g2.drawLine((int) pPrec.x, 
                                (int) pPrec.y, 
                                (int) p.x, 
                                (int) p.y);
                        
                        pPrec = p; 
                    }
                }
                else
                    System.out.println("Pas de chemin");
            } 
            catch (IllegalArgumentException e){
                e.printStackTrace();
            } 
            catch (PathNotFoundException e){
                e.printStackTrace();
            }
        }

        private void dessinerArc(Line2D arc, Graphics2D g2)
        {
            g2.drawLine((int) arc.getX1(), 
                    (int) arc.getY1(), 
                    (int) arc.getX2(), 
                    (int) arc.getY2());
        }
    }
    
    public Window_RepresentationGrid(Maillage maillage)
    {
        super("Représentation du maillage"); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new Panel_Maillage(maillage));
        
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) 
    {
        int largeur = 1000;
        int hauteur = 800;
        
        //Maillage maillage_v1 = new Maillage_v1(largeur, hauteur, 10);
        Maillage m = new Maillage_v2(largeur, hauteur, 10, 0, 0);
        
        // desactivation de zone
        m.disableZone(new Rectangle(largeur / 2 - 20,0,20,hauteur),false);
        m.disableZone(new Rectangle(0,largeur / 2 - 20,largeur,20),false);
        
        // ajout des points de sorties
        m.addPointOfExit(largeur / 4, hauteur / 4);
        m.addPointOfExit(largeur / 4 * 3, hauteur / 4);
        m.addPointOfExit(largeur / 4, hauteur / 4 * 3);
        m.addPointOfExit(largeur / 4 * 3, hauteur / 4 * 3);

        new Window_RepresentationGrid(m);
    }
}
