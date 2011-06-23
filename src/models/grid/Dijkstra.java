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
public class Dijkstra
{
    class InfoNoeud
    {
        int id;
        Node noeud;
        double distArrivee = Integer.MAX_VALUE;
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
    public static int[] dijkstra(GraphePondere graphe, Node arrive) throws IllegalAccessException
    {  
        int nbNoeuds = graphe.getNbNoeuds();
        
        final InfoNoeud[] infoNoeuds = new InfoNoeud[nbNoeuds];
        final Node[] noeuds = graphe.getNoeuds(); // Noeuds
        
        // creation des noeuds d'information
        //for(int i=0;i<nbNoeuds;i++)
        //    infoNoeuds[i] = new InfoNoeud(i,noeuds[i]);
        
        // Mise a jour des voisins
        for(int i=0;i<nbNoeuds;i++)
        {
            Node n = noeuds[i];
            
            // initialisation des voisins
            Node[] voisins = graphe.getVoisins(n);
            for(int j=0;j<voisins.length;i++)
            {
                //ArrayList<Voisins>
                
            }
        }
        
        
        
        
        
        
        // Construction des tableaux
        //final int[] dist = new int[nbNoeuds]; // shortest known distance from "s"
        final int[] pred = new int[nbNoeuds]; // preceeding node in path
        //final boolean[] visited = new boolean[nbNoeuds]; // all false initially

        /*
        // Recherche de l'indice du noeud de départ
        int iNoeudArrive = -1;
        for(int i=0;i<nbNoeuds;i++)
            if(noeuds[i] == arrive)
                iNoeudArrive = i;
        
        if(iNoeudArrive == -1)
            throw new IllegalAccessException();
             
        // Toutes les distances à infini
        //for (int i = 0; i < nbNoeuds; i++)
        //    dist[i] = Integer.MAX_VALUE;
        
        // Sommet de départ à zéro
        infoNoeuds[iNoeudArrive].distArrivee = 0;
        
        
        // Pour chaque noeuds
        for (int i = 0; i < nbNoeuds; i++)
        {
            // Cherche le noeud suivant à traiter
            final InfoNoeud next = minVertex(dist, visited);
            
            // Traitement du noeud
            next.visited = true;


            // Pour tous les voisins du noeud
            //final Noeud [] voisins = graphe.voisins(next);
            for (int j = 0; j < next.voisins.length; j++)
            {
                final Noeud v = voisins[j];
                final int d = dist[next] + graphe.getWeight(next, v);
                if (dist[v] > d)
                {
                    dist[v] = d;
                    pred[v] = next;
                }
            }
        }*/

        return pred; // (ignore pred[s]==0!)
    }
    
    
    
    

    /**
     * Retour l'indice du noeud le dont la distance est la plus faible. 
     * 
     * @param dist les distances depuis le noeud de départ
     * @param visited les noeuds visités
     * @return l'indice du noeud le dont la distance est la plus faible.
     *          ou -1 s'il n'y en a pas
     */
    private static int minVertex(int[] dist, boolean[] visited)
    {
        int x = Integer.MAX_VALUE;
        int y = -1; // graph not connected, or no unvisited vertices
        
        // Pour chaque noeuds
        for (int i = 0; i < dist.length; i++)
        {
            // Si pas visité et la distance est plus faible 
            if (!visited[i] && dist[i] < x)
            {
                y = i;
                x = dist[i];
            }
        }
        
        return y;
    }

    public static void printPath(GraphePondere G, int[] pred, int s, int e)
    {
        /*
        final java.util.ArrayList path = new java.util.ArrayList();
        int x = e;
        while (x != s)
        {
            path.add(0, G.getLabel(x));
            x = pred[x];
        }
        path.add(0, G.getLabel(s));
        System.out.println(path);*/
    }

}
