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
//路径算法
public class Dijkstra_MA
{
    public static class InfoNoeud
    {
        int id;
        Node noeud;
        int distArrivee = Integer.MAX_VALUE;
        int pred = -1;
        boolean visited = false;
        int[] voisins;
        
        public InfoNoeud(int id,Node noeud)
        {
            this.id     = id;
            this.noeud  = noeud;
        }
    }
    
    // Dijkstra's algorithm to find shortest path from s to all other nodes
    public static InfoNoeud[] dijkstra(GraphePondere_MA graphe, int iNoeudArrive) throws IllegalAccessException
    {  
        int nbNoeuds = graphe.getNbNoeuds();
        
        final InfoNoeud[] infoNoeuds = new InfoNoeud[nbNoeuds];

        // creation des noeuds d'information
        for(int i=0;i<nbNoeuds;i++)
            infoNoeuds[i] = new InfoNoeud(i,graphe.getNoeud(i));

        
        // Sommet de départ à zéro
        infoNoeuds[iNoeudArrive].distArrivee = 0;
        
        // Pour chaque noeuds
        for (int i = 0; i < nbNoeuds; i++)
        {
            // Cherche le noeud suivant à traiter
            final int next = minVertex(infoNoeuds);
            
            if(next != -1)
            {
                // Traitement du noeud
                infoNoeuds[next].visited = true;
    
                // Pour tous les voisins du noeud
                final int [] voisins = graphe.getIdVoisins(next);
                for (int j = 0; j < voisins.length; j++)
                {
                    final int v = voisins[j];
                    final int d = infoNoeuds[next].distArrivee + graphe.getPoids(next, v);
                    if (infoNoeuds[v].distArrivee > d)
                    {
                        infoNoeuds[v].distArrivee = d;
                        infoNoeuds[v].pred = next;
                    }
                }
            }
        }

        return infoNoeuds; // (ignore pred[s]==0!)
    }
   
    /**
     * Retour l'indice du noeud le dont la distance est la plus faible. 
     * 
     * @param dist les distances depuis le noeud de départ
     * @param visite les noeuds visités
     * @return l'indice du noeud le dont la distance est la plus faible.
     *          ou -1 s'il n'y en a pas
     */
    private static int minVertex(InfoNoeud[] infoNoeuds)
    {
        int x = Integer.MAX_VALUE;
        int y = -1; // graph not connected, or no unvisited vertices
        
        // Pour chaque noeuds
        for (int i = 0; i < infoNoeuds.length; i++)
        {
            // Si pas visité et la distance est plus faible 
            if (!infoNoeuds[i].visited && infoNoeuds[i].distArrivee < x)
            {
                y = i;
                x = infoNoeuds[i].distArrivee;
            }
        }
        
        return y;
    }
}
