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

import java.util.ArrayList;

public class GraphePondere {

    // TODO ajouté
    private ArrayList<Node> noeuds; 
    private ArrayList<Arc> arcs; 
    
    public GraphePondere () 
    { 
        noeuds = new ArrayList<Node>();
        arcs   = new ArrayList<Arc>();
    }

    // TODO ajouté
    public void ajouterNoeud(Node n)
    {
        noeuds.add(n);
    }
    
    // TODO ajouté
    public void ajouterArc(Arc a)
    {
        arcs.add(a);
    }
    
    // TODO ajouté
    public int getNbNoeuds()
    {
        return noeuds.size();
    }
    
    // TODO ajouté
    public int getNbArcs()
    {
        return arcs.size();
    }
    
    // TODO ajouté
    public Node[] getNoeuds()
    { 
        Node noeudArray[] = new Node[noeuds.size()];
        return noeuds.toArray(noeudArray);
    }
    
    // TODO ajouté
    public Arc[] getArcs()
    { 
        Arc arcsArray[] = new Arc[arcs.size()];
        return arcs.toArray(arcsArray);
    }

    // TODO ajouté
    public Node[] getVoisins(Node n) 
    {
        ArrayList<Node> voisins = new ArrayList<Node>();
        
        for(Arc a : arcs)
            if(a.getDepart() == n)
                voisins.add(a.getArrivee());
        
        Node noeudArray[] = new Node[voisins.size()];
        return voisins.toArray(noeudArray);
    }

    public void print () {
        for(Arc a : arcs)
            System.out.println(a); 
    }

    public Node getNoeud(int i)
    {
        return noeuds.get(i);
    }
 }
