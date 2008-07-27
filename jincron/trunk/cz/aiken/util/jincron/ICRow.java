/*
 * ICRow.java - jIncron table row class
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

import java.util.regex.*;

/**
 *
 * @author luk
 */
public class ICRow {
    
    protected static final Pattern splitter = Pattern.compile("[^\\\\](\\s)+");
    
    private String path = "";
    private InotifyEvent event = null;
    private String command = "";

    ICRow(String path, InotifyEvent event, String command) {
        this.path = path;
        this.event = event;
        this.command = command;
    }

    String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }
    
    InotifyEvent getEvent() {
        return event;
    }
    
    void setEvent(InotifyEvent event) {
        this.event = event;
    }
    
    String getCommand() {
        return command;
    }
    
    void setCommand(String command) {
        this.command = command;
    }
    
    static int getFieldCount() {
        return 3;
    }
    
    static ICRow parse(String text) {
        if (text == null || text.equals(""))
            return null;
        
        Matcher m = splitter.matcher(text);
        
        if (!m.find())
            return null;
        int pos = m.start() + 1;
        String path = text.substring(0, pos).trim().replace("\\ ", " ").replace("\\\\", "\\");
        
        if (!m.find())
            return null;
        int oldpos = pos;
        pos = m.start() + 1;
        
        InotifyEvent evt = InotifyEvent.parse(text.substring(oldpos, pos));
        if (evt == null)
            return null;
        
        String cmd = text.substring(pos).trim();
        
        return new ICRow(path, evt, cmd);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPath().replace("\\", "\\\\").replace(" ", "\\ "));
        sb.append(' ');
        sb.append(getEvent().toString());
        sb.append(' ');
        sb.append(getCommand());        
        return sb.toString();
    }
}
