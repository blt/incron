/*
 * InotifyEvent.java - inotify event class
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

import java.util.*;

/**
 *
 * @author luk
 */
public class InotifyEvent {
    
    private int mask = 0;
    private int flags = 0;
    
    public static final int IN_ACCESS           = 0x00000001;
    public static final int IN_MODIFY           = 0x00000002;
    public static final int IN_ATTRIB           = 0x00000004;
    public static final int IN_CLOSE_WRITE      = 0x00000008;
    public static final int IN_CLOSE_NOWRITE    = 0x00000010;
    public static final int IN_OPEN             = 0x00000020;
    public static final int IN_MOVED_FROM       = 0x00000040;
    public static final int IN_MOVED_TO         = 0x00000080;
    public static final int IN_CREATE           = 0x00000100;
    public static final int IN_DELETE           = 0x00000200;
    public static final int IN_DELETE_SELF      = 0x00000400;
    public static final int IN_MOVE_SELF        = 0x00000800;
    
    public static final int IN_ONLYDIR          = 0x01000000;
    public static final int IN_DONT_FOLLOW      = 0x02000000;
    
    public static final int IN_ONESHOT          = 0x80000000;
    
    public static final int IN_CLOSE = IN_CLOSE_WRITE | IN_CLOSE_NOWRITE;
    public static final int IN_MOVE = IN_MOVED_FROM | IN_MOVED_TO;
    
    public static final int IN_ALL_EVENTS = IN_ACCESS | IN_MODIFY | IN_ATTRIB | IN_CLOSE
            | IN_OPEN | IN_MOVE | IN_CREATE | IN_DELETE | IN_DELETE_SELF | IN_MOVE_SELF;
    
    public static final int IN_NO_LOOP = 0x00000001;
    
    private static HashMap<String, Integer> nameToMask = new HashMap<String, Integer>();
    private static HashMap<String, Integer> flagsToMask = new HashMap<String, Integer>();
    
    static {
        nameToMask.put("IN_ACCESS", IN_ACCESS);
        nameToMask.put("IN_MODIFY", IN_MODIFY);
        nameToMask.put("IN_ATTRIB", IN_ATTRIB);
        nameToMask.put("IN_CLOSE_WRITE", IN_CLOSE_WRITE);
        nameToMask.put("IN_CLOSE_NOWRITE", IN_CLOSE_NOWRITE);
        nameToMask.put("IN_OPEN", IN_OPEN);
        nameToMask.put("IN_MOVED_FROM", IN_MOVED_FROM);
        nameToMask.put("IN_MOVED_TO", IN_MOVED_TO);
        nameToMask.put("IN_CREATE", IN_CREATE);
        nameToMask.put("IN_DELETE", IN_DELETE);
        nameToMask.put("IN_DELETE_SELF", IN_DELETE_SELF);
        nameToMask.put("IN_MOVE_SELF", IN_MOVE_SELF);
        nameToMask.put("IN_ONLYDIR", IN_ONLYDIR);
        nameToMask.put("IN_DONT_FOLLOW", IN_DONT_FOLLOW);
        nameToMask.put("IN_ONESHOT", IN_ONESHOT);
        nameToMask.put("IN_CLOSE", IN_CLOSE);
        nameToMask.put("IN_MOVE", IN_MOVE);
        nameToMask.put("IN_ALL_EVENTS", IN_ALL_EVENTS);
        
        flagsToMask.put("IN_NO_LOOP", IN_NO_LOOP);
    }
    
    /** Creates a new instance of InotifyEvent */
    public InotifyEvent(int mask, int flags) {
        this.mask = mask;
        this.flags = flags;
    }
    
    public InotifyEvent(InotifyEvent src) {
        this(src.getMask(), src.getFlags());
    }
    
    public static InotifyEvent parse(String text) {
        int mask = 0;
        int flags = 0;
        
        try {
            mask = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            String sa[] = text.split(",");
            for (int i=0; i<sa.length; i++) {
                String s = sa[i].trim();
                mask |= getMaskByName(s);
                flags |= getFlagsByName(s);
            }
        }
        
        return new InotifyEvent(mask, flags);
    }
    
    public int getMask() {
        return mask;
    }
    
    public void setMask(int mask) {
        this.mask = mask;
    }
    
    public boolean hasMask(int mask) {
        return (mask & this.mask) == mask;
    }
    
    public void changeMask(int mask, boolean add) {
        if (add) {
            this.mask |= mask;
        }
        else {
            this.mask &= ~mask;
        }
    }
    
    public int getFlags() {
        return flags;
    }
    
    public void setFlags(int flags) {
        this.flags = flags;
    }
    
    public boolean hasFlags(int flags) {
        return (flags & this.flags) == flags;
    }
    
    public void changeFlags(int flags, boolean add) {
        if (add) {
            this.flags |= flags;
        }
        else {
            this.flags &= ~flags;
        }
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if ((mask & IN_ALL_EVENTS) == IN_ALL_EVENTS) {
            if (sb.length() > 0)
                sb.append(',');
            sb.append("IN_ALL_EVENTS");
        }
        else {
            if ((mask & IN_MOVE) == IN_MOVE) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_MOVE");
            }
            else {
                if ((mask & IN_MOVED_FROM) == IN_MOVED_FROM) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append("IN_MOVED_FROM");
                }
                else if ((mask & IN_MOVED_TO) == IN_MOVED_TO) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append("IN_MOVED_TO");
                }
            }
            
            if ((mask & IN_CLOSE) == IN_CLOSE) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_CLOSE");
            }
            else {
                if ((mask & IN_CLOSE_WRITE) == IN_CLOSE_WRITE) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append("IN_CLOSE_WRITE");
                }
                else if ((mask & IN_CLOSE_NOWRITE) == IN_CLOSE_NOWRITE) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append("IN_CLOSE_NOWRITE");
                }
            }
            
            if ((mask & IN_ACCESS) == IN_ACCESS) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_ACCESS");
            }
            
            if ((mask & IN_MODIFY) == IN_MODIFY) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_MODIFY");
            }
            
            if ((mask & IN_ATTRIB) == IN_ATTRIB) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_ATTRIB");
            }
            
            if ((mask & IN_OPEN) == IN_OPEN) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_OPEN");
            }
            
            if ((mask & IN_CREATE) == IN_CREATE) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_CREATE");
            }
            
            if ((mask & IN_DELETE) == IN_DELETE) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_DELETE");
            }
            
            if ((mask & IN_DELETE_SELF) == IN_DELETE_SELF) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_DELETE_SELF");
            }
            
            if ((mask & IN_MOVE_SELF) == IN_MOVE_SELF) {
                if (sb.length() > 0)
                    sb.append(',');
                sb.append("IN_MOVE_SELF");
            }
        }
        
        if ((mask & IN_ONLYDIR) == IN_ONLYDIR) {
            if (sb.length() > 0)
                sb.append(',');
            sb.append("IN_ONLYDIR");
        }
        
        if ((mask & IN_DONT_FOLLOW) == IN_DONT_FOLLOW) {
            if (sb.length() > 0)
                sb.append(',');
            sb.append("IN_DONT_FOLLOW");
        }
        
        if ((mask & IN_ONESHOT) == IN_ONESHOT) {
            if (sb.length() > 0)
                sb.append(',');
            sb.append("IN_ONESHOT");
        }
        
        if ((flags & IN_NO_LOOP) == IN_NO_LOOP) {
            if (sb.length() > 0)
                sb.append(',');
            sb.append("IN_NO_LOOP");
        }
            
        return sb.toString();
    }
    
    public static int getMaskByName(String name) {
        Integer i = nameToMask.get(name);
        return i != null ? i.intValue() : 0;
    }
    
    public static int getFlagsByName(String name) {
        Integer i = flagsToMask.get(name);
        return i != null ? i.intValue() : 0;
    }
}


