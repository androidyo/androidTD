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

package models.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Class utilitaire
 * <p>
 * Elle fournie plusieurs methodes pratiques et souvent inclassables.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | 15 decembre 2009
 * @since jdk1.6.0_16
 */
public class Tools
{
	/**
	 * Methode utilitaire pour changer la taille d'une image
	 * 
	 * @param src l'image originale
	 * @param largeur largeur de l'image reduite
	 * @param hauteur hauteur de l'image reduite
	 * @return une copie redimentionnée de l'image originale
	 * @see Image
	 */
	public static Image redimentionner(Image src, int largeur, int hauteur) 
	{
        // creation d'une nouvelle image de la taille souhaitee
		BufferedImage dst 	= new BufferedImage(largeur, hauteur,BufferedImage.TYPE_INT_ARGB);
        
		// dessin de l'image originale dans le graphics de la nouvelle image
		Graphics2D g2 = dst.createGraphics();
        g2.drawImage(src, 0, 0, largeur, hauteur, null);
        g2.dispose(); // restauration memoire 
        
        return dst;
    }
	
	/**
	 * Permet d'arrondir une valeur reelle a un certain nombre de decimales
	 * 
	 * @param nombre le nombre a arrondir
	 * @param nbDecimales le nombre de decimales souhaitees
	 * @return le nombre arrondi a nbDecimal decimales
	 */
	public static double arrondir(double nombre, int nbDecimales)
	{
	    int mult10 = (int) Math.pow(10,nbDecimales);
	    return (double) Math.round(nombre * mult10) / mult10;
	}
	
    /**
     * Permet de tirer un nombre entier aleatoire entre min et max 
     * (bornes comprises)
     * 
     * @param min la borne inferieure du tirage
     * @param max la borne superieure du tirage
     * @return un nombre aleatoire entre min et max (bornes comprises)
     */
    public static int tirerNombrePseudoAleatoire(int min, int max)
    {
        return min + (int) Math.round(Math.random() * (max - min)); 
    }
    
    /**
     * Color -> hexa color
     */
    public static String ColorToHexa(Color color)
    {
        return Integer.toHexString(color.getRGB() & 0x00ffffff); 
    } 
}
