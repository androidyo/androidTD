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

import org.jgrapht.*;

/**
 * Fichier : GenerateurDArcs.java
 * 
 * <p>
 * But : Permet de générer les arcs du graphique à partir de deux noeuds.
 * <p>
 * Remarques : Nécessaire au fonctionnement relatif à JGrapht, voir
 * {@link EdgeFactory}.
 * 
 * @author Pierre-Dominique Putallaz
 * @author Lazhar Farjallah
 * @version 5 déc. 2009
 * @since jdk1.6.0_16
 */
public class GeneratorArcs implements EdgeFactory<Node, Arc>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgrapht.EdgeFactory#createEdge(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Arc createEdge(Node sourceVertex, Node targetVertex)
	{
		return new Arc(sourceVertex, targetVertex);
	}
}
