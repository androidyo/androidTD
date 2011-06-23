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

package views.online;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import views.ManageFonts;

/**
 * Classe de gestion d'une console HTML
 * 
 * @author Aurélien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class Console extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JEditorPane taConsole = new JEditorPane("text/html","");
    private JScrollPane scrollConsole;
    
    
    public Console(int largeur,int hauteur)
    {
        super(new BorderLayout());
        
        // style
        taConsole.setFont(ManageFonts.POLICE_CONSOLE);
        taConsole.setEditable(false);
        
        scrollConsole = new JScrollPane(taConsole);
        scrollConsole.setPreferredSize(new Dimension(largeur,hauteur));
        
 
        add(scrollConsole,BorderLayout.CENTER);
    }
    
    /**
     * Permet d'ajouter du text HTML dans la console
     * 
     * @param texte le texte a ajouter
     */
    public void ajouterTexteHTMLDansConsole(String texte)
    {
        String s = taConsole.getText();
        taConsole.setText( s.substring(0,s.indexOf("</body>")) 
                           + texte + 
                           s.substring(s.indexOf("</body>")));
        
        // reposition le curseur en fin 
        taConsole.setCaretPosition( taConsole.getDocument().getLength() - 1 );
    }
}
