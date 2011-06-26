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

public interface Maillage
{

    /**
     * Méthode public pour calculer le chemin le plus court d'un point à un
     * autre. Les points sont données en pixels exactes dans le champs.
     * 公共方法来计算从一个点到另一个的最短路线。这些点是在正确的字段像素数据。
     * 
     * @param xDepart
     *            La coordonnée x du point de départ.
     * @param yDepart
     *            La coordonnée y du point de départ.
     * @param xArrivee
     *            La coordonnée x du point d'arrivée.
     * @param yArrivee
     *            La coordonnée y du point d'arrivée.
     * @return Une liste de points qui représente le chemin à parcourir.
     * @throws PathNotFoundException
     *             Levé si aucun chemin n'est trouvé.
     * @throws IllegalArgumentException
     *             Levé si les coordonnées ne sont pas dans le champs.
     */
    public abstract ArrayList<Point> plusCourtChemin(int xDepart, int yDepart,
            int xArrivee, int yArrivee) throws PathNotFoundException,
            IllegalArgumentException;

    /**
     * Active une zone rectangulaire dans le champs.
     * 
     * @param rectangle
     *            La zone a activer
     * @throws IllegalArgumentException
     *             Si la zone à activer est hors champs.
     */
    public abstract void activerZone(Rectangle rectangle, boolean miseAJour)
            throws IllegalArgumentException;

    /**
     * Disable a zone in the fields
     * 
     * @param rectangle
     *            la zone a désactiver.
     * @throws IllegalArgumentException
     *             Levé si le rectangle est hors champs.
     */
    public abstract void disableZone(Rectangle rectangle, boolean miseAJour)
            throws IllegalArgumentException;

    /**
     * Getter pour le champ <tt>largeurPixels</tt>
     * 
     * @return La valeur du champ <tt>largeurPixels</tt>
     */
    public abstract int getLargeurPixels();

    /**
     * Getter pour le champ <tt>hauteurPixels</tt>
     * 
     * @return La valeur du champ <tt>hauteurPixels</tt>
     */
    public abstract int getHauteurPixels();

    /**
     * Retourne la liste complète des copies des noeuds du maillage, actifs ou
     * inactifs.
     * 
     * @return Les noeuds du maillage.
     */
    public abstract Node[] getNoeuds();

    /**
     * Retourne la liste complète des arcs du maillage.
     * 
     * @return La liste complète des arcs du maillage.
     */
    public abstract Line2D[] getArcs();

    /**
     * Calcul la distance entre chaque point
     * 
     * @param chemin une collection de point
     * @return la longueur du chemin
     */
    public abstract double getLongueurChemin(ArrayList<Point> chemin);

    
    /**
     * Permet de recuperer le nombre de noeuds du maillage
     * 
     * @return le nombre de noeuds du maillage
     */
    public abstract int getNbNoeuds();
    
    /**
     * Adds an exit point
     * 
     * @param x le point en x
     * @param y le point en y
     */
    public void addPointOfExit(int x, int y);
    
    /**
     * Permet de forcer la mise a jour du TDA
     */
    public void miseAJourTDA();
    
}