/*
  Copyright (C) 2010 Lazhar Farjallah, Pierre-Dominique Putallaz

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
import java.util.Iterator;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Fichier : Maillage.java
 * <p>
 * Encodage : UTF-8
 * <p>
 * Cette classe permet de représenter différentes opérations sur le maillage
 * (graphe), utilisé dans notre jeu. Elle implémente différentes opérations,
 * notamment la désactivation de zone et surtout le calcule du chemin le plus
 * cours d'une maille à une autre.
 * <p>
 * Remarques : -
 * 
 * @author Pierre-Dominique Putallaz
 * @author Lazhar Farjallah
 * @version 30 nov. 2009
 * @since jdk1.6.0_16
 */
public class Maillage_v1 implements Maillage
{
	
    /*
	 * Constantes
	 */
    private final int NB_NOEUDS;
    
	/**
	 * La largeur en pixel de chaque maille, ou noeud
	 */
	private final int LARGEUR_NOEUD;
	/**
	 * Le poid d'un arc diagonal
	 */
	private final int POIDS_DIAGO;
	/**
	 * La demi-distance entre un point et un autre
	 */
	private final int DEMI_NOEUD;
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
	private final int NOMBRE_NOEUDS_X, NOMBRE_NOEUDS_Y;

	/*
	 * Attributs
	 */
	/**
	 * Le graphe
	 */
	private SimpleWeightedGraph<Node, Arc> graphe = new SimpleWeightedGraph<Node, Arc>(
			new GeneratorArcs());
	/**
	 * Le tableau des noeuds : Noeud[x][y]
	 */
	private Node[][] noeuds;
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
	public Maillage_v1(final int largeurPixels, final int hauteurPixels,
			final int largeurDuNoeud, int xOffset, int yOffset)
			throws IllegalArgumentException
	{
		/*
		 * Test des arguments.
		 */
		testInt(largeurDuNoeud);
		testInt(largeurPixels);
		testInt(hauteurPixels);

		// Assignation de la largeur du noeud (ou de la maille).
		LARGEUR_NOEUD = largeurDuNoeud;

		// Calcule une fois pour toute la distance diagonale
		POIDS_DIAGO = (int) Math.sqrt(2 * LARGEUR_NOEUD * LARGEUR_NOEUD);

		// Largeur du demi noeud
		DEMI_NOEUD = LARGEUR_NOEUD / 2;

		// Assignation de la dimension en pixel unitaire du maillage
		LARGEUR_EN_PIXELS = largeurPixels;
		HAUTEUR_EN_PIXELS = hauteurPixels;

		// Conversion en dimension en maille.
		NOMBRE_NOEUDS_X = (largeurPixels / LARGEUR_NOEUD);
		NOMBRE_NOEUDS_Y = (hauteurPixels / LARGEUR_NOEUD);

		NB_NOEUDS = NOMBRE_NOEUDS_X * NOMBRE_NOEUDS_Y;
		
		
		// Les offsets du décalage
		this.xOffset = xOffset;
		this.yOffset = yOffset;

		// Initialisation du champs de noeuds
		noeuds = new Node[NOMBRE_NOEUDS_X][NOMBRE_NOEUDS_Y];

		// Construction du graphe
		construireGraphe();
		
		// Affichage d'un petit truc dans la console
		//System.out.println(this);
	}

	/**
	 * 
	 * @param largeurPixels
	 * @param hauteurPixels
	 * @param largeurDuNoeud
	 * @throws IllegalArgumentException
	 */
	public Maillage_v1(final int largeurPixels, final int hauteurPixels,
			final int largeurDuNoeud) throws IllegalArgumentException
	{
		this(largeurPixels, hauteurPixels, largeurDuNoeud, 0, 0);
	}

	/* (non-Javadoc)
     * @see models.maillage.MaillageI#plusCourtChemin(int, int, int, int)
     */
	public synchronized ArrayList<Point> plusCourtChemin(int xDepart,
			int yDepart, int xArrivee, int yArrivee)
			throws PathNotFoundException, IllegalArgumentException
	{
		/*
		 * Test des arguments
		 */
		if (xDepart >= LARGEUR_EN_PIXELS-xOffset || xArrivee >= LARGEUR_EN_PIXELS-xOffset
				|| xDepart-xOffset < 0 || xArrivee-xOffset < 0)
			throw new IllegalArgumentException("Valeur invalide en x");

		if (yDepart-yOffset >= HAUTEUR_EN_PIXELS || yArrivee-yOffset >= HAUTEUR_EN_PIXELS
				|| yDepart-yOffset < 0 || yArrivee-yOffset < 0)
			throw new IllegalArgumentException("Valeur invalide en y");

		/*
		 * Calcul par Dijkstra du chemin le plus cours d'un point à un autre.
		 */
		GraphPath<Node, Arc> dijkstraChemin;
		try
		{
			dijkstraChemin = (new DijkstraShortestPath<Node, Arc>(
					graphe,
					noeudContenantLePoint(xDepart - xOffset, yDepart - yOffset),
					noeudContenantLePoint(xArrivee - xOffset, yArrivee
							- yOffset))).getPath();
		} catch (IllegalArgumentException e)
		{
			// Retour de null en cas de levée d'exception de la part du graphe.
			throw new PathNotFoundException("Le chemin n'est pas valide.");
		}
		/*
		 * S'il n'y a pas de chemin
		 */
		if (dijkstraChemin == null)
			throw new PathNotFoundException("Le chemin n'existe pas!");

		// Retourne l'ArrayList des points.
		return new ArrayList<Point>(Graphs.getPathVertexList(dijkstraChemin));
	}

	/* (non-Javadoc)
     * @see models.maillage.MaillageI#activerZone(java.awt.Rectangle)
     */
	synchronized public void activerZone(Rectangle rectangle, boolean miseAJour)
			throws IllegalArgumentException
	{
		zoneActive(rectangle, true);
	}

	/* (non-Javadoc)
     * @see models.maillage.MaillageI#desactiverZone(java.awt.Rectangle)
     */
	synchronized public void disableZone(Rectangle rectangle, boolean miseAJour)
			throws IllegalArgumentException
	{
		zoneActive(rectangle, false);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Largeur du maillage : " + LARGEUR_EN_PIXELS + " pixels\n"
				+ "Hauteur du maillage : " + HAUTEUR_EN_PIXELS + " pixels\n"
				+ "Représentation      : 1 noeud = " + LARGEUR_NOEUD + "x"
				+ LARGEUR_NOEUD + " pixels\n" + "Nombre de noeuds    : "
				+ graphe.vertexSet().size() + "\n" + "Nombre d'arcs       : "
				+ graphe.edgeSet().size() + "\nDécalage            : X="
				+ xOffset + " Y=" + yOffset
				+"\n";
	}

	/* (non-Javadoc)
     * @see models.maillage.MaillageI#getLargeurPixels()
     */
	public int getLargeurPixels()
	{
		return LARGEUR_EN_PIXELS;
	}

	/* (non-Javadoc)
     * @see models.maillage.MaillageI#getHauteurPixels()
     */
	public int getHauteurPixels()
	{
		return HAUTEUR_EN_PIXELS;
	}

	/* (non-Javadoc)
     * @see models.maillage.MaillageI#getNoeuds()
     */
	public Node[] getNoeuds()
	{
	    Node[] tabNoeuds = new Node[NB_NOEUDS];
	    
	    int iNoeud = 0;
		for (Node[] ligne : noeuds)
			for (Node noeud : ligne)
			    tabNoeuds[iNoeud++] = new Node(noeud);
		
		return tabNoeuds;
	}

	/* (non-Javadoc)
     * @see models.maillage.MaillageI#getArcs()
     */
	public Line2D[] getArcs()
	{
	    Line2D[] arcs = new Line2D[graphe.edgeSet().size()];
	   
	    int iArc = 0;
		for (Arc edge : graphe.edgeSet())
		    arcs[iArc++] = edge.toLine2D();
		
		return arcs;
	}

	/**
	 * Méthode de service pour activer ou désactiver une zone.
	 * 
	 * @param rectangle
	 *            La zone concernée.
	 * @param active
	 *            True s'il faut l'activer, False s'il faut la désactiver.
	 * @throws IllegalArgumentException
	 *             Levé si la zone est hors champs.
	 */
	private void zoneActive(final Rectangle rectangle, final boolean active)
			throws IllegalArgumentException
	{
		// Vérification de la validité du rectangle
		rectangleEstDansLeTerrain(rectangle);

		/*
		 * Pour chaque noeuds on vérifie s'il intersect avec la zone concernée.
		 */
		for (Node[] ligne : noeuds)
			for (Node noeud : ligne)
			{
				if (rectangle.intersects(new Rectangle(noeud.x - DEMI_NOEUD,
						noeud.y - DEMI_NOEUD, LARGEUR_NOEUD, LARGEUR_NOEUD)))
					if (active)
						activer(noeud);
					else if (noeud.isActif())
						desactiver(noeud);
			}
	}

	/**
	 * Active l'ensemble des arcs d'un noeud, marque le noeud comme actif, puis
	 * ajoute le noeud dans le graphe.
	 * 
	 * @param noeud
	 *            Le noeud dont on active les arcs.
	 * @throws IllegalArgumentException
	 *             Levé si le noeud est null ou s'il est déjà actif.
	 */
	private void activer(Node noeud) throws IllegalArgumentException
	{
		// Vérifie si le noeud n'est pas null
		if (noeud == null)
			throw new IllegalArgumentException(
					"Le noeud passé en paramêtre est null");
		// Vérifie si le noeud n'est pas déjà actif.
		if (noeud.isActif())
			throw new IllegalArgumentException(
					"Impossible d'activer un noeud déjà actif.");
		// Activation du noeud
		noeud.setActif(true);
		// Replanter le noeud dans le graphe, s'il n'est pas déjà présent
		graphe.addVertex(noeud);

		/*
		 * Ajouter les arcs manquants
		 */
		int[] xy = Node.coordonnee(noeud, xOffset, yOffset);
		int x, y;
		Node cible;
		Arc arc;
		for (int i = -1; i <= 1; i++)
		{
			x = xy[0] + i;
			// Si le noeud n'est pas hors cadre
			if (x < 0 || x >= noeuds.length)
				continue;
			for (int j = -1; j <= 1; j++)
			{
				y = xy[1] + j;
				// Si le noeud n'est pas hors cadre
				if (y < 0 || y >= noeuds[x].length)
					continue;
				/*
				 * Extraction de la cible
				 */
				cible = noeuds[x][y];
				if (cible == null)
					throw new IllegalArgumentException(
							"Le noeud ciblé ne peut pas être nul");
				// Si le noeud cible n'est pas actif ou s'il s'agit du noeud
				// courant
				if (!cible.isActif() || cible.equals(noeud))
					continue;
				// Ajout du noeud à l'ensemble. La méthode test si le noeud est
				// déjà présent
				graphe.addVertex(cible);
				// Calcul du nouvel arc
				arc = graphe.addEdge(noeud, cible);
				// Ajout du poids à l'arc
				graphe.setEdgeWeight(arc,
						(Math.abs(i) != Math.abs(j)) ? LARGEUR_NOEUD
								: POIDS_DIAGO);
			}
		}
	}

	/**
	 * Désactive l'ensemble des arcs du noeud, le marque comme inactif et le
	 * retire du graphe.
	 * 
	 * @param noeud
	 *            Le noeud dont on désactive les arcs.
	 * @throws IllegalArgumentException
	 *             Levé si le noeud est null ou s'il est déjà inactif.
	 */
	private void desactiver(Node noeud) throws IllegalArgumentException
	{
		// Vérifie si le noeud n'est pas null
		if (noeud == null)
			throw new IllegalArgumentException(
					"Le noeud passé en paramêtre est null");
		// Vérifie si le noeud n'est pas déjà inactif.
		if (!noeud.isActif())
			throw new IllegalArgumentException(
					"Impossible de désactiver un noeud déjà inactif.");
		// Désactivation du noeud
		noeud.setActif(false);
		// Supprimer le noeud ainsi que tous les arcs relatifs
		graphe.removeVertex(noeud);
	}

	private void construireGraphe()
	{
		/*
		 * Ajouter les noeuds au graphe.
		 */
		for (int x = 0; x < NOMBRE_NOEUDS_X; x++)
			for (int y = 0; y < NOMBRE_NOEUDS_Y; y++)
				noeuds[x][y] = new Node((x * LARGEUR_NOEUD) + xOffset,
						(y * LARGEUR_NOEUD) + yOffset, LARGEUR_NOEUD);

		/*
		 * Active tout les noeuds (et calcul les vertex)
		 */
		for (Node[] ligne : noeuds)
			for (Node noeud : ligne)
				activer(noeud);
	}

	/**
	 * Méthode de service pour faire la relation Point -> Noeud.
	 * 
	 * Retourne le noeud actif le plus proche de la position x,y.
	 * 
	 * @param p
	 *            Le point à chercher
	 * @return Le noeud correspondant.
	 * @throws IllegalArgumentException
	 *             Levé si le noeud demandé est hors champs.
	 */
	private Node noeudContenantLePoint(int x, int y)
	{
		// Calcul des coordonnées.
		int x_nodale = Node.pixelANodale(x, LARGEUR_NOEUD);
		int y_nodale = Node.pixelANodale(y, LARGEUR_NOEUD);
		// Vérification de la plausibilité des coordonnées.
		if (x_nodale < 0 || x_nodale > noeuds.length)
			throw new IllegalArgumentException(
					"Noeud hors champs. Coordonnée x invalide : " + x);
		if (y_nodale < 0 || y_nodale > noeuds[x_nodale].length)
			throw new IllegalArgumentException(
					"Noeud hors champs. Coordonnée y invalide : " + y);
		// Retour du noeud correspondant
		return noeuds[x_nodale][y_nodale];
	}

	/**
	 * Méthode de service pour tester si le rectangle passé en paramètre est
	 * dans le champs.
	 * 
	 * @param rectangle
	 *            Le rectangle à tester.
	 * @throws IllegalArgumentException
	 *             Levé si le rectangle est hors chamos.
	 */
	private void rectangleEstDansLeTerrain(Rectangle rectangle)
			throws IllegalArgumentException
	{
		if (rectangle == null)
			throw new IllegalArgumentException("Argument null");
		/*if (rectangle.getX() + rectangle.getWidth() > LARGEUR_EN_PIXELS)
			throw new IllegalArgumentException("Largeur hors cadre");
		if (rectangle.getY() + rectangle.getHeight() > HAUTEUR_EN_PIXELS)
			throw new IllegalArgumentException("Hauteur hors cadre");*/
	}

	/**
	 * Test si la valeur est valude.
	 * 
	 * @param valeur
	 *            La valeur à tester
	 * @throws IllegalArgumentException
	 *             Levé si la valeur est négative.
	 */
	private void testInt(int valeur) throws IllegalArgumentException
	{
		if (valeur < 0)
			throw new IllegalArgumentException("Valeur invalide (négative) : "
					+ valeur);
	}

	/**
	 * Calcul la distance entre chaque point
	 * 
	 * @param chemin une collection de point
	 * @return la longueur du chemin
	 */
    public double getLongueurChemin(ArrayList<Point> chemin)
    {
        double longueur = 0.0;
        
        Point pCourant;
        Iterator<Point> i = chemin.iterator();
        
        // premier point
        if(i.hasNext())
            pCourant = i.next();
        else
            return 0;
        
        // pour tous les autres...
        Point pSuivant;
        while(i.hasNext())
        {
            pSuivant = i.next();
            longueur += pCourant.distance(pSuivant);
            pCourant = pSuivant;
        }
        
        return longueur;
    }

    @Override
    public int getNbNoeuds()
    {
        return NB_NOEUDS;
    }

    @Override
    public void addPointOfExit(int x, int y)
    {
        // pas utilisé
    }

    @Override
    public void miseAJourTDA()
    {
        // pas utilisé
    }
}
