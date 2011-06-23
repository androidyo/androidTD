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

package views.common;

import i18n.Langue;

import java.io.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.*;

import views.ManageFonts;
import views.LookInterface;

import java.net.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Fenetre premettant d'afficher une page HTML.
 * 
 * @author Aurelien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class View_HTML extends JFrame implements ActionListener
{

    private JEditorPane epHTML;
    private static final long serialVersionUID = 1L;
    private JButton bFermer = new JButton(Langue.getTexte(Langue.ID_TXT_BTN_FERMER));

    /**
     * Constructeur
     * 
     * @param titre le titre de la fenetre
     * @param fichier le fichier html local
     * @param parent la fenêtre parent
     */
    public View_HTML(String titre, File fichier, JFrame parent)
    {

        super(titre);
        setIconImage(parent.getIconImage());
        getContentPane().setBackground(LookInterface.COULEUR_DE_FOND_PRI);

        // contenu HTML
        epHTML = new JEditorPane();
        epHTML.setEditable(false);
        epHTML.setBorder(new EmptyBorder(-20, 0, 0, 0));

        URL url = null;

        try
        {
            url = new URL("file:" + fichier.getPath());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        try
        {
            epHTML.setPage(url);
        } catch (Exception e)
        {
        }

        // ajout du fichier
        epHTML.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent event)
            {
                try
                {
                    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                    {
                        epHTML.setPage(event.getURL());
                    }
                } catch (IOException e)
                {
                }
            }
        });

        getContentPane().add(new JScrollPane(epHTML), BorderLayout.CENTER);

        // bouton fermer
        bFermer.addActionListener(this);
        
        ManageFonts.setStyle(bFermer);
        
        
        getContentPane().add(bFermer, BorderLayout.SOUTH);

        // dernier réglages
        setSize(550, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == bFermer)
            this.dispose();
    }
}