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

package utils;

import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * 计时器类.
 * 
 * @author Aurélien Da Campo
 * @version 1.0 | juin 2010
 * @since jdk1.6.0_16
 */
public class myTimer extends Timer
{
    private static final long serialVersionUID = 1L;
    private long temp;
    private long timeStart;
    private boolean pause = false; 

    public myTimer(int delay, ActionListener listener)
    {
        super(delay, listener);
    }

    @Override
    public void start()
    {
        super.start();
        
        timeStart = System.currentTimeMillis();
    }
    
    public void pause()
    {
        pause = true;
        temp += System.currentTimeMillis() - timeStart;
    }
    
    public void play()
    {
        pause = false;
        timeStart = System.currentTimeMillis();
    }
    
    public long getTime()
    {
        if(pause)
            return temp;
        else
            return temp + System.currentTimeMillis() - timeStart;
    }
    
    public int getSeconds()
    {
        return (int) (getTime() / 1000) % 60;
    }
    
    public int getMinutes()
    {
        return (int) (getTime() / 60000) % 60;
    }
    
    public int getHours()
    {
        return (int) (getTime() / 3600000) % 24;
    }
    
    public String toString()
    {
        return String.format("%02d.%02d.%02d", getHours(), 
                                               getMinutes(), 
                                               getSeconds());
    }

    public boolean isPaused()
    {
        return pause;
    }
}
