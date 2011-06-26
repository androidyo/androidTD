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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import models.utils.Tools;

/**
 * Maillage dans sa version 2.
 * 
 * Utilisation d'un arbre de recouvrement.
 * 
 * TODO commenter
 * 
 * @author Dark
 */
public class Maillage_v2 implements Maillage
{
	
    /*
	 * Constantes
	 */
	/**
	 * La largeur en pixel de chaque maille, ou noeud
	 */
	private final int LARGEUR_NOEUD;
	
	/**
	 * Le poid d'un arc diagonal
	 */
	private final int POIDS_DIAGO;

	/**
	 * La largeur en pixel totale du maillage (axe des x)
	 */
	private final int LARGEUR_EN_PIXELS;
	
	/**
	 * La hauteur en pixel totale du maillage (axe des y)
	 */
	private final int HAUTEUR_EN_PIXELS;
	
	/**
	 * Les dimensions en maille (ou noeuds) du maillage
	 */
	private final int NB_NOEUDS_LARGEUR, NB_NOEUDS_HAUTEUR;

	/*
	 * Attributs
	 */
	// 8 voisins + 1 car tous les noeuds peuvent être une sortie
	private static int NB_VOISINS_MAX_PAR_NOEUD = 9;
	
	private int NB_NOEUDS;
	
	/**
     * Tableau des noeuds
     * <br>
     * Note : l'indice du tableau spécifie le numéro du noeud
     */
    private Node[] noeuds;
	
	/**
	 * Tableau du nombre de voisins d'un noeuds
	 * <br>
	 * Note : l'indice du tableau spécifie le numéro du noeud
	 */
	private int[] nbVoisins;
	
	/**
	 * Tableau des indices des voisins d'un noeuds
	 * <br>
	 * Note : l'indice 1 du tableau spécifie le numéro du noeud
	 */
	private int[][] voisins;
	
	/**
	 * Tableau des poinds jusqu'au voisins d'un noeuds
	 * <br>
     * Note : l'indice 1 du tableau spécifie le numéro du noeud
	 */
	private int[][] poids;
	
	/**
	 * Le decalage de base.
	 */
	private int xOffset, yOffset;
   

	/**
	 * Un maillage dynamique représentant une aire de jeu.
	 * 
	 * @param largeurPixels
	 *            Largeur en pixel de la zone.
	 * @param hauteurPixels
	 *            Hauteur en pixel de la zone.
	 * @param largeurDuNoeud
	 *            La largeur en pixel de chaque maille.
	 * @param xOffset
	 *            Le décalage en x du maillage, en pixels.
	 * @param yOffset
	 *            Le décalage en y du maillage, en pixels.
	 * @return 
	 * @throws IllegalArgumentException
	 *             Levé si les dimensions ne correspondent pas.
	 */
	public Maillage_v2(final int largeurPixels, final int hauteurPixels,
			final int largeurDuNoeud, int xOffset, int yOffset)
			throws IllegalArgumentException
	{

		// Assignation de la largeur du noeud (ou de la maille).
		LARGEUR_NOEUD = largeurDuNoeud;

		// Calcule une fois pour toute la distance diagonale
		POIDS_DIAGO = (int) Math.sqrt(2 * LARGEUR_NOEUD * LARGEUR_NOEUD);
		
		// Assignation de la dimension en pixel unitaire du maillage
		LARGEUR_EN_PIXELS = largeurPixels;
		HAUTEUR_EN_PIXELS = hauteurPixels;

		// Conversion en dimension en maille.
		NB_NOEUDS_LARGEUR = (largeurPixels / LARGEUR_NOEUD);
		NB_NOEUDS_HAUTEUR = (hauteurPixels / LARGEUR_NOEUD);

		
		NB_NOEUDS = NB_NOEUDS_LARGEUR * NB_NOEUDS_HAUTEUR + 1;
		
		
		// Les offsets du décalage
		this.xOffset = xOffset;
		this.yOffset = yOffset;

		// Construction du graphe
		construireGraphe();
		
        contruireArbreDijkstra();
	}

    @Override
	synchronized public void addPointOfExit(int x, int y)
	{
	    int tmp = getIndiceNoeud(x, y);
	   
        ajouterArc(tmp,0,0);
        ajouterArc(0,tmp,0);
        
        contruireArbreDijkstra();
	}

	/**
	 * Permet de trouver l'indice d'un noeud le plus proche d'une coordonnée.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	synchronized private int getIndiceNoeud(int x, int y)
    { 
        if(noeuds.length == 0)
            throw new IllegalArgumentException("Pas de noeud");
        
        Node n;
        int iNoeudLePlusProche = -1;
        double distance;
        double distMax = Integer.MAX_VALUE;
        Point cible = new Point(x,y);
        
        for(int i=1;i<NB_NOEUDS;i++)
        {
            n = noeuds[i];
            
            distance = cible.distance(n);
            
            if(distance < distMax)
            {
                iNoeudLePlusProche = i;
                distMax = distance;
            }
        }
             
        return iNoeudLePlusProche;
    }

    synchronized private void construireGraphe()
    {
	    // allocation mémoire
	    noeuds     = new Node[NB_NOEUDS];   
	    nbVoisins  = new int[NB_NOEUDS];
        voisins    = new int[NB_NOEUDS][NB_VOISINS_MAX_PAR_NOEUD];
        poids      = new int[NB_NOEUDS][NB_VOISINS_MAX_PAR_NOEUD];
        
        // FIXME creer des tableaux plutot que des noeuds
        //x          = new int[NB_NOEUDS];
        //y          = new int[NB_NOEUDS];
        //pred       = new int[NB_NOEUDS];
        //dist       = new int[NB_NOEUDS];
        //actif      = new int[NB_NOEUDS];
        //visite     = new int[NB_NOEUDS];
        
        // par defaut, pas de voisins
        for(int i=0;i<NB_NOEUDS;i++)
        {
            nbVoisins[i] = 0;
        
            for(int j=0;j<NB_VOISINS_MAX_PAR_NOEUD;j++)
                poids[i][j] = Integer.MAX_VALUE;
        }
 
        // noeud de sortie
        noeuds[0] = new Node(0,0,1);
        noeuds[0].setActif(true);
        
        // construction du graphe
        Node n;
        int iNoeud = 1; // on saute le noeud de sortie
        for(int i=0;i<NB_NOEUDS_LARGEUR;i++)
        {
            for(int j=0;j<NB_NOEUDS_HAUTEUR;j++)
            {
                //--------------------------
                //-- mise a jour du noeud --
                //--------------------------
                n = new Node(i*LARGEUR_NOEUD+xOffset,j*LARGEUR_NOEUD+yOffset,LARGEUR_NOEUD);
                n.setActif(true);
                noeuds[iNoeud] = n;
                
                //--------------------
                //-- ajout des arcs --
                //--------------------
                // pas la derniere colonne
                if(i != NB_NOEUDS_LARGEUR-1 /*&& boolAlea()*/)
                {
                    ajouterArc(iNoeud, iNoeud+NB_NOEUDS_HAUTEUR, LARGEUR_NOEUD);
                    ajouterArc(iNoeud+NB_NOEUDS_HAUTEUR, iNoeud, LARGEUR_NOEUD);
                }
                
                // pas la derniere ligne
                if(j != NB_NOEUDS_HAUTEUR-1 /*&& boolAlea()*/)
                {
                    ajouterArc(iNoeud, iNoeud+1, LARGEUR_NOEUD);
                    ajouterArc(iNoeud+1, iNoeud, LARGEUR_NOEUD);
                }
                    
                // pas la derniere ligne et la derniere colonne
                if(i != NB_NOEUDS_LARGEUR-1 && j != NB_NOEUDS_HAUTEUR-1 /*&& boolAlea()*/)
                {
                    ajouterArc(iNoeud, iNoeud+NB_NOEUDS_HAUTEUR+1, POIDS_DIAGO);
                    ajouterArc(iNoeud+NB_NOEUDS_HAUTEUR+1, iNoeud, POIDS_DIAGO);
                }
                
                // pas la première ligne et la derniere colonne
                if(j != 0 && i != NB_NOEUDS_LARGEUR-1 /*&& boolAlea()*/)   
                {
                    ajouterArc(iNoeud, iNoeud+NB_NOEUDS_HAUTEUR-1, POIDS_DIAGO);
                    ajouterArc(iNoeud+NB_NOEUDS_HAUTEUR-1, iNoeud, POIDS_DIAGO);
                }
                
                iNoeud++;
            } 
        }
    }  
	
	synchronized private void ajouterArc(int iNoeud, int voisin, int poidsArc)
    {
	    if(nbVoisins[iNoeud] == NB_VOISINS_MAX_PAR_NOEUD)
	        throw new IllegalArgumentException("Nombre de voisins max atteint");
	    
	    if(poidsArc == Integer.MAX_VALUE)
	        throw new IllegalArgumentException("Poids impossible");
	    
	    voisins[iNoeud][nbVoisins[iNoeud]] = voisin;
	    poids[iNoeud][nbVoisins[iNoeud]] = poidsArc;
	    
	    nbVoisins[iNoeud]++;
    }

    @SuppressWarnings("unused")
    private boolean boolAlea()
    {
        return Tools.tirerNombrePseudoAleatoire(0, 2) >= 1;
    }

	@Override
    public synchronized ArrayList<Point> plusCourtChemin(int xDepart,
			int yDepart, int xArrivee, int yArrivee)
			throws PathNotFoundException, IllegalArgumentException
	{
		
	    ArrayList<Point> ps = new ArrayList<Point>();
	    
	    int in = getIndiceNoeud(xDepart, yDepart);
	
	    ps.add(new Point(noeuds[in]));
	    
        while(in != -1)
        {
            int pred = infoNoeuds[in].pred;
            
            if(pred == -1)
                break;
            
            ps.add(new Point(noeuds[pred]));

            in = pred;
        }  
        
        if(ps.size() == 1)
            throw new PathNotFoundException("Le chemin n'existe pas!");
        
        // efface le point de sortie commun
        ps.remove(ps.size()-1);
        
	    return ps;
	}

	@Override
	synchronized public void activerZone(Rectangle rectangle, boolean miseAJour)
			throws IllegalArgumentException
	{
	    Node n;
        // on touche pas au noeud de sorti
        for(int i=1;i<NB_NOEUDS;i++)
        {
            n = noeuds[i];
            
            if(rectangle.contains(n))
            {
                n.setActif(true);
                
                //System.out.println("noeud ["+n.x+","+n.y+"] activé");
                
                // pour tous les arcs
                /*
                for(int j=0;j<nbVoisins[i];j++)
                {
                    poids[i][j] = Integer.MAX_VALUE;
                    nbArcsActifs-=2; // bidirection
                }*/           
            }
        }
        
        if(miseAJour)
            contruireArbreDijkstra();
	}
	
	synchronized public void miseAJourTDA()
	{
	    contruireArbreDijkstra();
	}
	
	@Override
	synchronized public void disableZone(Rectangle rectangle, boolean miseAJour)
			throws IllegalArgumentException
	{ 
	    Node n;
	    int min = Integer.MAX_VALUE;
	    // on touche pas au noeud de sorti
	    for(int i=1;i<NB_NOEUDS;i++)
	    {
	        n = noeuds[i];

	        if(rectangle.contains(n))
	        {
	            n.setActif(false);
	            
	            
	            // TODO test optimisation
	            //if(infoNoeuds[i].distArrivee < min)
	            //    min = infoNoeuds[i].distArrivee;
	            
	            
	            //System.out.println("noeud ["+n.x+","+n.y+"] désactivé");
	            
	            // pour tous les arcs
	            /*
	            for(int j=0;j<nbVoisins[i];j++)
	            {
	                poids[i][j] = Integer.MAX_VALUE;
	                //nbArcsActifs-=2; // bidirection
	            }*/          
	        }
	    }
	    
	    if(miseAJour)
	        contruireArbreDijkstra();
	        
	    
	    // TODO test optimisation
	    //if(miseAJour)
	    //    contruireArbreDijkstraOptimise(min);
	    
	}

	@Override
	public int getLargeurPixels()
	{
		return LARGEUR_EN_PIXELS;
	}

	@Override
	public int getHauteurPixels()
	{
		return HAUTEUR_EN_PIXELS;
	}

	@Override
	synchronized public Node[] getNoeuds()
	{
		return noeuds;
	}

	@Override
	synchronized public Line2D[] getArcs()
	{    
	    ArrayList<Line2D> arcs = new ArrayList<Line2D>();

	    Arc arc;
	    for(int i=0;i<NB_NOEUDS;i++)
	        if(noeuds[i].isActif())
    	        for(int j=0;j<nbVoisins[i];j++)
    	        {      
    	            // les arcs de poids infinis sont inexistants.
    	            if(poids[i][j] != Integer.MAX_VALUE 
    	            && noeuds[voisins[i][j]].isActif()
    	            && voisins[i][j] != 0) // prends pas les arc du point de sorti commun
    	            {
    	                arc = new Arc(noeuds[i], noeuds[voisins[i][j]]);
    	                arcs.add(arc.toLine2D()); 
    	            }
    	        }
	    
	    Line2D[]tabArcs = new Line2D[arcs.size()];
	    arcs.toArray(tabArcs);
	    
	    return tabArcs;
	}

	/**
	 * Calcul la distance entre chaque point
	 * 
	 * @param chemin une collection de point
	 * @return la longueur du chemin
	 */
    public double getLongueurChemin(ArrayList<Point> chemin)
    {
        return 0.0;
    }
    
    
    /**
     * Arbre des chemins les plus courts.
     */
    private InfoNoeud[] infoNoeuds;
       
    /**
     * Dijkstra's algorithm to find shortest path from iNoeudArrive 
     * to all other nodes
     * 
     * @param iNoeudArrive
     */
    synchronized private void contruireArbreDijkstra()
    {
        if(infoNoeuds == null)
        {
            infoNoeuds = new InfoNoeud[NB_NOEUDS];
            
            // creation des noeuds d'information
            for(int i=0;i<NB_NOEUDS;i++)
                infoNoeuds[i] = new InfoNoeud(i,noeuds[i]);
        }
        else
            for(int i=0;i<NB_NOEUDS;i++)
                infoNoeuds[i].reset();
                
        // Sommet de départ à zéro
        infoNoeuds[0].distArrivee = 0;
        
        // Pour chaque noeuds
        for (int i = 0; i < NB_NOEUDS; i++)
        {
            if(noeuds[i].isActif())
            {
                // Cherche le noeud suivant à traiter
                final int next = minVertex(infoNoeuds);
                
                if(next != -1)
                {
                    // Traitement du noeud
                    infoNoeuds[next].visite = true;

                    // Pour tous les voisins du noeud
                    for (int j = 0; j < nbVoisins[next]; j++)
                    {
                        if(noeuds[voisins[next][j]].isActif())
                        {
                            final int iVoisin = voisins[next][j];
                            final int distArrivee = infoNoeuds[next].distArrivee + poids[next][j];
                            if (infoNoeuds[iVoisin].distArrivee > distArrivee)
                            {
                                infoNoeuds[iVoisin].distArrivee = distArrivee;
                                infoNoeuds[iVoisin].pred = next;
                            }
                        }
                    }
                }
            }
        }

        //this.infoNoeuds = infoNoeuds; // (ignore pred[s]==0!)
    }
    
    
    
    
    
    /**
     * Dijkstra's algorithm to find shortest path from iNoeudArrive 
     * to all other nodes
     * 
     * @param iNoeudArrive
     */
    synchronized private void contruireArbreDijkstraOptimise(int poidsMin)
    {
        if(infoNoeuds == null)
        {
            infoNoeuds = new InfoNoeud[NB_NOEUDS];
            
            // creation des noeuds d'information
            for(int i=0;i<NB_NOEUDS;i++)
                infoNoeuds[i] = new InfoNoeud(i,noeuds[i]);
        }
        else
            for(int i=0;i<NB_NOEUDS;i++)
            {
                if(infoNoeuds[i].distArrivee < poidsMin-15)
                {
                    // Les noeuds avant gardent les mêmes prédécésseurs
                    // et sont déjà traités
                    infoNoeuds[i].visite = true;
                }
                else
                {
                    if(infoNoeuds[i].distArrivee >= poidsMin)
                        infoNoeuds[i].distArrivee = Integer.MAX_VALUE;
                    
                    //infoNoeuds[i].pred = -1;
                    infoNoeuds[i].visite = false;
                }
            }
                
                
        // Sommet de départ à zéro
        infoNoeuds[0].distArrivee = 0;
        
        // Pour chaque noeuds
        for (int i = 0; i < NB_NOEUDS; i++)
        {
            if(noeuds[i].isActif())
            {
                // Cherche le noeud suivant à traiter
                final int next = minVertex(infoNoeuds);
                
                if(next != -1)
                {
                    // Traitement du noeud
                    infoNoeuds[next].visite = true;

                    // Pour tous les voisins du noeud
                    for (int j = 0; j < nbVoisins[next]; j++)
                    {
                        if(noeuds[voisins[next][j]].isActif())
                        {
                            final int iVoisin = voisins[next][j];
                            final int distArrivee = infoNoeuds[next].distArrivee + poids[next][j];
                            if (infoNoeuds[iVoisin].distArrivee > distArrivee)
                            {
                                infoNoeuds[iVoisin].distArrivee = distArrivee;
                                infoNoeuds[iVoisin].pred = next;
                            }
                        }
                    }
                }
            }
        }

        //this.infoNoeuds = infoNoeuds; // (ignore pred[s]==0!)
    }
    
    
    
   
    /**
     * Retour l'indice du noeud non visité dont la distance est la plus faible avec
     * l'arrivée. 
     * 
     * @param dist les distances depuis le noeud de départ
     * @param visite les noeuds visités
     * @return l'indice du noeud le dont la distance est la plus faible.
     *          ou -1 s'il n'y en a pas
     */
    private int minVertex(InfoNoeud[] infoNoeuds)
    {
        int x = Integer.MAX_VALUE;
        int y = -1; // graph not connected, or no unvisited vertices
        
        // Pour chaque noeuds
        for (int i = 0; i < NB_NOEUDS; i++)
        {
            if(noeuds[i].isActif())
            {
                // Si pas visité et la distance est plus faible 
                if (!infoNoeuds[i].visite && infoNoeuds[i].distArrivee < x)
                {
                    y = i;
                    x = infoNoeuds[i].distArrivee;
                }
            }
        }
 
        return y;
    }

    @Override
    public int getNbNoeuds()
    {
        return NB_NOEUDS;
    }

    
    public static class InfoNoeud
    {
        int id;
        Node noeud;
        int distArrivee = Integer.MAX_VALUE;
        int pred = -1;
        boolean visite = false;
        
        public InfoNoeud(int id,Node noeud)
        {
            this.id     = id;
            this.noeud  = noeud;
        }
        
        public void reset()
        {
            distArrivee = Integer.MAX_VALUE;
            pred = -1;
            visite = false;
        }
        
    }
}
