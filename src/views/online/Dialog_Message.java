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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class Dialog_Message extends JDialog
{
    private final Insets MARGES = new Insets(30, 30, 30, 30);
    
    public Dialog_Message(Frame parent, String titre, String message)
    {
        super(parent,false);
        setTitle(titre);

        JLabel lblMessage = new JLabel(message);
        
        lblMessage.setBorder(new EmptyBorder(MARGES));
        add(lblMessage,BorderLayout.CENTER);
        
        JButton fermer = new JButton("OK");
        add(fermer,BorderLayout.SOUTH);
        
        fermer.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
