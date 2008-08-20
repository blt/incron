/*
 * Main.java - main LWTT class
 *
 * Copyright (c) 2006, 2007, 2008 Lukas Jelinek, http://www.aiken.cz
 *
 * ==========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ==========================================================================
 */

package cz.aiken.util.lwtt;

import javax.swing.*;

/**
 * This class represents the application itself. It creates the main
 * application frame and makes it visible.
 * @author luk
 */
public class Main implements Runnable {
    
    /**
     * Main class constructor.
     */
    public Main() {
        
    }
    
    /**
     * Creates the main frame and makes it visible. Then it hands control
     * on the main application loop.
     * 
     * <I>This method may be called only by the event-dispatching thread.</I>
     */
    public void run() {
        TaskFrame tf = new TaskFrame();
        tf.setVisible(true);
    } 
    
    /**
     * The main application method. It starts the application by
     * scheduling the initialization for the event-dispatching thread.
     * @param args the command line arguments (currently ignored)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());                
    }
    
}
