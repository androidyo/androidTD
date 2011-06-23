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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

/**
 * Use to create vertical form.
 * 
 * @author Aurelien Da Campo
 */
public class Panel_GridBag extends JPanel
{
	private static final long serialVersionUID = 1L;
	private GridBagLayout gbl 			= new GridBagLayout();
   	private GridBagConstraints ct;
   	
	/**
	 * Constructor without params
	 */
	public Panel_GridBag(Insets insets)
	{
		setLayout(gbl);

		ct = new GridBagConstraints(0, 0, 3, 1, 1, 1, 
		                            GridBagConstraints.CENTER, 
		                            GridBagConstraints.NONE,
		                            insets, 0, 0);
	}

	
	public void add(Component composant, 
	                int gridx, int gridy, 
	                int gridwidth)
    {
	    ct.fill        = GridBagConstraints.HORIZONTAL;
	    ct.insets      = new Insets(1, 8, 1, 8);
	    ct.gridx       = gridx;
	    ct.gridy       = gridy;
	    ct.gridwidth   = gridwidth;
        
        add(composant,ct);
    }
}
