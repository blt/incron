/*
 * ICConfig.java - jIncron configuration management
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

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.JOptionPane;

/**
 *
 * @author luk
 */
public class ICConfig {
    
    private static final String cfgFile = ".jincron.xml";
    private static Properties props = new Properties();
    
    static {
        init();
    }
    
    public static void load() {
        File f = new File(System.getProperty("user.home"), cfgFile);
        if (!f.exists())
            return;
        
        try {
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
            try {
                props.loadFromXML(is);
            } finally {
                is.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot load configuration: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void save() {
        File f = new File(System.getProperty("user.home"), cfgFile);
        
        try {
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
            try {
                props.storeToXML(os, "jIncron configuration file");
            } finally {
                os.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot save configuration: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void init() {
        props.setProperty("window.width", Integer.toString(500));
        props.setProperty("window.height", Integer.toString(300));
        props.setProperty("window.x", Integer.toString(100));
        props.setProperty("window.y", Integer.toString(100));
    }
    
    
    
    public static int getInt(String key, int dflt) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (NumberFormatException e) {
            return dflt;
        }
    }
    
    public static void setInt(String key, int val) {
        props.setProperty(key, Integer.toString(val));
    }
    
    
    
    public static Dimension getWindowSize() {
        return new Dimension(getInt("window.width", 0), getInt("window.height", 0));
    }
    
    public static Point getWindowLocation() {
        return new Point(getInt("window.x", 0), getInt("window.y", 0));
    }
    
    
    
    public static void setWindowSize(Dimension d) {
        setInt("window.width", d.width);
        setInt("window.height", d.height);
    }
    
    public static void setWindowLocation(Point p) {
        setInt("window.x", p.x);
        setInt("window.y", p.y);
    }
}
