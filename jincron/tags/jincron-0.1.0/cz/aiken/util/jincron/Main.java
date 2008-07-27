/*
 * Main.java - jIncron main class
 *
 * Copyright (c) 2006 Lukas Jelinek, http://www.aiken.cz
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

package cz.aiken.util.jincron;


import javax.swing.*;

/**
 *
 * @author luk
 */
public class Main implements Runnable {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    public void run() {
        ICFrame f = new ICFrame();
        f.setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ICConfig.load();
        Main m = new Main();
        SwingUtilities.invokeLater(m);
    }
    
}
