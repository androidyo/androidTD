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
import java.util.List;

// avec matrice d'adjacence
//邻接矩阵
public class GraphePondere_MA
{

    private Node[] noeuds;
    private int[][] arcs;
    private int nbArcs = 0;

    public GraphePondere_MA(int nbNoeuds)
    {
        noeuds = new Node[nbNoeuds];

        System.out.println((long) nbNoeuds * nbNoeuds * 32 / 8 / 1024 / 1024
                + " Méga de RAM !");

        arcs = new int[nbNoeuds][nbNoeuds];

        for (int i = 0; i < nbNoeuds; i++)
        {
            noeuds[i] = new Node(0, 0, 1);

            for (int j = 0; j < nbNoeuds; j++)
                arcs[i][j] = Integer.MAX_VALUE;
        }
    }

    public void setNoeud(int i, Node n)
    {
        noeuds[i] = n;
    }

    public void ajouterArc(int i, int j, int poids)
    {
        // si il n'y avait pas d'arcs
        if (this.arcs[i][j] == Integer.MAX_VALUE)
            nbArcs++;

        // non orienté
        this.arcs[i][j] = poids;
        this.arcs[j][i] = poids;
    }

    public int getNbNoeuds()
    {
        return noeuds.length;
    }

    public int getNbArcs()
    {
        return nbArcs;
    }

    public Node[] getNoeuds()
    {
        return noeuds.clone();
    }

    public Arc[] getArcs()
    {
        ArrayList<Arc> arcs = new ArrayList<Arc>();

        for (int i = 0; i < noeuds.length; i++)
            for (int j = 0; j < noeuds.length; j++)
                if (this.arcs[i][j] != Integer.MAX_VALUE)
                    arcs.add(new Arc(getNoeud(i), getNoeud(j)));

        Arc arcsArray[] = new Arc[arcs.size()];
        return arcs.toArray(arcsArray);
    }

    public int getPoids(int idNoeud1, int idNoeud2)
    {
        return arcs[idNoeud1][idNoeud2];
    }

    public int[] getIdVoisins(int idNoeud)
    {
        ArrayList<Integer> neighbors = new ArrayList<Integer>();

        for (int i = 0; i < noeuds.length; i++)
        {
            // si l'arc existe
            if (getPoids(idNoeud, i) != Integer.MAX_VALUE)
                neighbors.add(i);
        }

        return convertIntegers(neighbors);
    }

    public Node getNoeud(int i)
    {
        return noeuds[i];
    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public Arc getArc(int idNoeud1, int idNoeud2)
    {
        return new Arc(getNoeud(idNoeud1), getNoeud(idNoeud2));
    }
}
