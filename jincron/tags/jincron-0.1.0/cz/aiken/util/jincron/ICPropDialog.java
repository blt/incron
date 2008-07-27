/*
 * ICPropDialog.java - incron table rule properties dialog
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author luk
 */
public class ICPropDialog extends JDialog implements ActionListener, UndoableEditListener {
    
    private static final String title = "Rule properties";
    
    private boolean changed = false;
    private boolean commit = false;
    
    private JTextField path = new JTextField();
    private JButton pathBut = new JButton("Browse...");
    private JPanel events = new JPanel();
    private JPanel cmd = new JPanel();
    private JTextField cmdText = new JTextField();
    
    private InotifyEvent evt = null;
    private HashMap<Integer, JCheckBox> mask = new HashMap<Integer, JCheckBox>();
    private HashMap<Integer, JCheckBox> flags = new HashMap<Integer, JCheckBox>();
    
    /** Creates a new instance of ICPropDialog */
    public ICPropDialog(ICRow row) {
        super();
        init(row);
    }
    
    protected void init(ICRow row) {
        Container c = getContentPane();
        c.setLayout(null);
        setSize(480, 470);
        setLocation(200, 200);
        setModal(true);
        setResizable(false);
        updateTitle();
        
        JLabel lab = new JLabel("Path");
        lab.setSize(55, 20);
        lab.setLocation(20, 20);
        c.add(lab);
        
        path.setSize(280, 20);
        path.setLocation(60, 20);
        path.setText(row.getPath());
        path.getDocument().addUndoableEditListener(this);
        path.setToolTipText("Specify the path to be watched");
        c.add(path);
        
        pathBut.setSize(100, 20);
        pathBut.setLocation(350, 20);
        pathBut.setActionCommand("path");
        pathBut.addActionListener(this);
        pathBut.setToolTipText("Browse the filesystem for a watch path");
        c.add(pathBut);
        
        
        evt = new InotifyEvent(row.getEvent());
        
        events.setSize(435, 200);
        events.setLocation(20, 50);
        events.setLayout(null);
        events.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Events"));
        c.add(events);
        
        createCheckbox("all events", 130, 20, 20, 20, "IN_ALL_EVENTS", "Watch all events", events, mask, InotifyEvent.IN_ALL_EVENTS);
        
        createCheckbox("access", 130, 20, 20, 45, "IN_ACCESS", "Watch accesses to file(s)", events, mask, InotifyEvent.IN_ACCESS);
        createCheckbox("modify", 130, 20, 20, 70, "IN_MODIFY", "Watch file(s) modifications", events, mask, InotifyEvent.IN_MODIFY);
        createCheckbox("attr. change", 130, 20, 20, 95, "IN_ATTRIB", "Watch attribute (metadata) changes", events, mask, InotifyEvent.IN_ATTRIB);
        createCheckbox("close", 130, 20, 20, 120, "IN_CLOSE", "Watch close events", events, mask, InotifyEvent.IN_CLOSE);
        createCheckbox("close (write)", 130, 20, 20, 145, "IN_CLOSE_WRITE", "Watch close events with writing", events, mask, InotifyEvent.IN_CLOSE_WRITE);
        createCheckbox("close (no write)", 130, 20, 20, 170, "IN_CLOSE_NOWRITE", "Watch close events without writing", events, mask, InotifyEvent.IN_CLOSE_NOWRITE);

        createCheckbox("open", 130, 20, 160, 45, "IN_OPEN", "Watch open events", events, mask, InotifyEvent.IN_OPEN);
        createCheckbox("move", 130, 20, 160, 70, "IN_MOVE", "Watch file moves", events, mask, InotifyEvent.IN_MOVE);
        createCheckbox("moved from", 130, 20, 160, 95, "IN_MOVED_FROM", "Watch events on file(s) moved out", events, mask, InotifyEvent.IN_MOVED_FROM);
        createCheckbox("moved to", 130, 20, 160, 120, "IN_MOVED_TO", "Watch events on file(s) moved in", events, mask, InotifyEvent.IN_MOVED_TO);
        createCheckbox("create", 130, 20, 160, 145, "IN_CREATE", "Watch creations", events, mask, InotifyEvent.IN_CREATE);
        createCheckbox("delete", 130, 20, 160, 170, "IN_DELETE", "Watch deletions (unlinks)", events, mask, InotifyEvent.IN_DELETE);
        
        createCheckbox("delete self", 130, 20, 300, 45, "IN_DELETE_SELF", "Watch the watched path is destroyed", events, mask, InotifyEvent.IN_DELETE_SELF);
        createCheckbox("dir only", 130, 20, 300, 70, "IN_ONLYDIR", "Watch only directory events", events, mask, InotifyEvent.IN_ONLYDIR);
        createCheckbox("don't follow", 130, 20, 300, 95, "IN_DONT_FOLLOW", "Don't follow symbolic links", events, mask, InotifyEvent.IN_DONT_FOLLOW);
        createCheckbox("oneshot", 130, 20, 300, 120, "IN_ONESHOT", "Watch exactly one event", events, mask, InotifyEvent.IN_ONESHOT);
        createCheckbox("no loop", 130, 20, 300, 170, "IN_NO_LOOP", "Avoid looping", events, flags, InotifyEvent.IN_NO_LOOP);
        
        updateCheckboxes();
        
        cmd.setSize(435, 130);
        cmd.setLocation(20, 260);
        cmd.setLayout(null);
        cmd.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Command"));
        c.add(cmd);
        
        cmdText.setSize(390, 20);
        cmdText.setLocation(20, 30);
        cmdText.setText(row.getCommand());
        cmdText.getDocument().addUndoableEditListener(this);
        cmdText.setToolTipText("Specify a command here");
        cmd.add(cmdText);
        
        JButton but = new JButton("Watch path");
        but.setSize(190, 20);
        but.setLocation(20, 60);
        but.setActionCommand("watch_path");
        but.addActionListener(this);
        but.setToolTipText("Insert a symbol for watch path");
        cmd.add(but);
        
        but = new JButton("Event name");
        but.setSize(190, 20);
        but.setLocation(220, 60);
        but.setActionCommand("event_name");
        but.addActionListener(this);
        but.setToolTipText("Insert a symbol for watch name");
        cmd.add(but);
        
        but = new JButton("Mask (symbolic)");
        but.setSize(190, 20);
        but.setLocation(20, 90);
        but.setActionCommand("symbolic_mask");
        but.addActionListener(this);
        but.setToolTipText("Insert a symbol for symbolic event mask");
        cmd.add(but);
        
        but = new JButton("Mask (numeric)");
        but.setSize(190, 20);
        but.setLocation(220, 90);
        but.setActionCommand("numeric_mask");
        but.addActionListener(this);
        but.setToolTipText("Insert a symbol for numeric event mask");
        cmd.add(but);
        
        
        but = new JButton("OK");
        but.setSize(80, 20);
        but.setLocation(150, 400);
        but.setActionCommand("ok");
        but.addActionListener(this);
        but.setToolTipText("Apply changes if any");
        c.add(but);
        
        but = new JButton("Cancel");
        but.setSize(80, 20);
        but.setLocation(240, 400);
        but.setActionCommand("cancel");
        but.addActionListener(this);
        but.setToolTipText("Leave the dialog without applying changes");
        c.add(but);
    }
    
    protected void createCheckbox(String text, int width, int height, int x, int y, String cmd, String tooltip, Container c, HashMap<Integer, JCheckBox> map, int mapVal) {
        JCheckBox cb = new JCheckBox(text);
        cb.setLocation(x, y);
        cb.setSize(width, height);
        cb.addActionListener(this);
        cb.setActionCommand(cmd);
        cb.setToolTipText(tooltip);
        c.add(cb);
        map.put(new Integer(mapVal), cb);
    }
    
    protected void browsePath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Watch path");
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File(path.getText()));
        
        int res = fc.showDialog(this, "Select");
        if (res == JFileChooser.APPROVE_OPTION) {
            path.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }
    
    protected void addCmdText(String txt) {
        String s = cmdText.getText();
        
        int start = cmdText.getSelectionStart();
        int end = cmdText.getSelectionEnd();
        if (start != -1 || end != -1) {
            cmdText.setText(s.substring(0, start) + txt + s.substring(end));
            cmdText.setCaretPosition(start + txt.length());
        }
        else {
            int pos = cmdText.getCaretPosition();
            if (pos != -1) {
                cmdText.setText(s.substring(0, pos) + txt + s.substring(pos));
                cmdText.setCaretPosition(pos + txt.length());
            }
            else {
                cmdText.setText(s + txt);
                cmdText.setCaretPosition(s.length() + txt.length());
            }
        }
        
        cmdText.requestFocusInWindow();
    }
    
    protected void updateTitle() {
        if (changed)
            setTitle(title + " (modified)");
        else
            setTitle(title);
    }
    
    protected void updateCheckboxes() {
        Iterator<Integer> it = mask.keySet().iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            JCheckBox cb = mask.get(i);
            if (cb != null) {
                cb.getModel().setSelected(evt.hasMask(i.intValue()));
            }
        }
        
        it = flags.keySet().iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            JCheckBox cb = flags.get(i);
            if (cb != null) {
                cb.getModel().setSelected(evt.hasFlags(i.intValue()));
            }
        }
    }
    
    protected void modifyEvent(int val, boolean fl) {
        if (fl) {
            JCheckBox cb = flags.get(val);
            if (cb != null) {
                evt.changeFlags(val, cb.getModel().isSelected());
            }
        }
        else {
            JCheckBox cb = mask.get(val);
            if (cb != null) {
                evt.changeMask(val, cb.getModel().isSelected());
            }
        }
        
        updateCheckboxes();
        
        changed = true;
        updateTitle();
    }
    
    public ICRow getData() {
        return new ICRow(path.getText(), evt, cmdText.getText());
    }
    
    public boolean isModified() {
        return changed;
    }
    
    public boolean isCommitted() {
        return commit;
    }
    
    protected boolean validateData() {
        if (path.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "The watch path must not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!(new File(path.getText())).exists()) {
            JOptionPane.showMessageDialog(this, "The watch path does not exist.\nThis is not an error but incrond will ignore such rules.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        
        if (evt.getMask() == 0) {
            JOptionPane.showMessageDialog(this, "At least one event type must be watched.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (cmdText.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "The command must not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        
        if (s.equals("path")) {
            browsePath();
        }
        else if (s.equals("watch_path")) {
            addCmdText("$@");
        }
        else if (s.equals("event_name")) {
            addCmdText("$#");
        }
        else if (s.equals("symbolic_mask")) {
            addCmdText("$%");
        }
        else if (s.equals("numeric_mask")) {
            addCmdText("$&");
        }
        else if (s.equals("ok")) {
            if (validateData()) {
                commit = true;
                setVisible(false);
            }
        }
        else if (s.equals("cancel")) {
            setVisible(false);
        }
        else {
            int i = InotifyEvent.getMaskByName(s);
            if (i != 0) {
                modifyEvent(i, false);
                return;
            }
            
            i = InotifyEvent.getFlagsByName(s);
            if (i != 0) {
                modifyEvent(i, true);
            }
        }
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        changed = true;
        updateTitle();
    }       
}
