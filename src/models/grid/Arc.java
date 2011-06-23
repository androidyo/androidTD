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

import java.awt.geom.*;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Fichier : Arc.java
 * 
 * <p>
 * But : Modélise un arc du graphe, en étendant un {@link DefaultWeightedEdge}.
 * <p>
 * Remarques : Pour avoir une {@link Line2D} de l'arc il faut utiliser la
 * méthode dédiée.
 * 
 * @author Lazhar Farjallah
 * @author Pierre-Dominique Putallaz
 * @version 4 déc. 2009
 * @since jdk1.6.0_16
 */
public class Arc extends DefaultWeightedEdge
{
	/**
	 * ID de serialisation.
	 */
	private static final long serialVersionUID = -3342545915708624018L;
	
	/**
	 * Les deux noeuds de départ et d'arrivée.
	 */
	private Node depart, arrivee;

	/**
	 * Construit un arc pondéré non orienté avec les noeuds de départs et
	 * d'arrivée.
	 * 
	 * @param depart
	 *            Le noeud de départ
	 * @param arrivee
	 *            Le noeud d'arrivée
	 */
	public Arc(Node depart, Node arrivee)
	{
		this.depart = depart;
		this.arrivee = arrivee;
	}

	/**
	 * Retourne une représentation sous forme de {@link Line2D} de l'arc.
	 * 
	 * @return une représentation sous forme de {@link Line2D} de l'arc.
	 */
	public Line2D toLine2D()
	{
		return new Line2D()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.geom.Line2D#getP1()
			 */
			@Override
			public Point2D getP1()
			{
				return getDepart();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.geom.Line2D#getP2()
			 */
			@Override
			public Point2D getP2()
			{
				return getArrivee();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.geom.Line2D#getX1()
			 */
			@Override
			public double getX1()
			{
				return getDepart().x;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.geom.Line2D#getX2()
			 */
			@Override
			public double getX2()
			{
				return getArrivee().x;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.geom.Line2D#getY1()
			 */
			@Override
			public double getY1()
			{
				return getDepart().y;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.geom.Line2D#getY2()
			 */
			@Override
			public double getY2()
			{
				return getArrivee().y;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.geom.Line2D#setLine(double, double, double, double)
			 */
			@Override
			public void setLine(double x1, double y1, double x2, double y2)
			{
				throw new IllegalArgumentException("Méthode non implémentée");

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.Shape#getBounds2D()
			 */
			@Override
			public Rectangle2D getBounds2D()
			{
				throw new IllegalArgumentException("Méthode non implémentée");
			}
		};
	}

	/**
	 * Retourne le noeud de départ.
	 * 
	 * @return le noeud de départ.
	 */
	public Node getDepart()
	{
		return depart;
	}

	/**
	 * Retourne le noeud d'arrivée.
	 * 
	 * @return le noeud d'arrivée.
	 */
	public Node getArrivee()
	{
		return arrivee;
	}
}
