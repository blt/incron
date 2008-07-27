/*
 * ICFrame.java - jIncron application GUI frame
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
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *
 * @author luk
 */
public class ICFrame extends JFrame implements ActionListener, ListSelectionListener {
    
    private static final String pgmName = "jIncron";
    
    private JMenuBar menubar = new JMenuBar();
    private JMenuItem editRowItem = null;
    private JMenuItem removeRowItem = null;
    
    private JButton addBut = new JButton("Add...");
    private JButton editBut = new JButton("Edit...");
    private JButton removeBut = new JButton("Remove");
    
    private JPanel butPanel = new JPanel();
    private ICTableModel model = new ICTableModel();
    private JTable table = null;
    
    private boolean changed = false;
    
    /** Creates a new instance of ICFrame */
    public ICFrame() {
        super();
        init();
    }
    
    protected void init() {
        setSize(ICConfig.getWindowSize());
        setLocation(ICConfig.getWindowLocation());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        updateTitle();
        
        initMenus();
        
        initButtons();
        
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        JScrollPane jsp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JSplitPane spl = new JSplitPane(JSplitPane.VERTICAL_SPLIT, butPanel, jsp);
        setContentPane(spl);

        load();
        
        updateMenus();
        updateButtons();
    }
    
    protected void initButtons() {
        butPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        initButton(addBut, "add", "Add a new rule");
        initButton(editBut, "edit", "Edit the selected rule");
        initButton(removeBut, "remove", "Remove the selected rule(s)");
    }
    
    protected void initButton(JButton but, String cmd, String tooltip) {
        but.setActionCommand(cmd);
        but.addActionListener(this);
        but.setToolTipText(tooltip);
        butPanel.add(but);
    }
    
    protected void initMenus() {
        JMenu menu = new JMenu("File");
        initMenuItem("New", 'n', KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK), "new", this, menu);
        initMenuItem("Load", 'l', KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK), "load", this, menu);
        initMenuItem("Save", 's', KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "save", this, menu);
        menu.addSeparator();
        initMenuItem("Import...", 'i', KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK), "import", this, menu);
        initMenuItem("Export...", 'x', KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK), "export", this, menu);
        menu.addSeparator();
        initMenuItem("Quit", 'q', KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK), "quit", this, menu);
        menubar.add(menu);
        
        menu = new JMenu("Action");
        initMenuItem("Add...", 'a', KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), "add", this, menu);
        editRowItem = initMenuItem("Edit...", 'e', KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK), "edit", this, menu);
        removeRowItem = initMenuItem("Remove", 'v', KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), "remove", this, menu);
        menubar.add(menu);
        
        menu = new JMenu("Help");
        initMenuItem("About", 'b', KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK), "about", this, menu);
        menubar.add(menu);
        
        setJMenuBar(menubar);
    }
    
    protected JMenuItem initMenuItem(String text, int mnemo, KeyStroke accel, String cmd, ActionListener al, JMenu menu) {
        JMenuItem it = new JMenuItem(text, mnemo);
        it.setAccelerator(accel);
        it.setActionCommand(cmd);
        it.addActionListener(al);
        menu.add(it);
        return it;
    }
    
    protected void updateTitle() {
        if (changed) {
            setTitle(pgmName + " (modified)");
        }
        else {
            setTitle(pgmName);
        }
    }
    
    protected void updateButtons() {
        int cnt = table.getSelectedRowCount();
        editBut.setEnabled(cnt == 1);
        removeBut.setEnabled(cnt > 0);
    }
    
    protected void updateMenus() {
        int cnt = table.getSelectedRowCount();
        editRowItem.setEnabled(cnt == 1);
        removeRowItem.setEnabled(cnt > 0);
    }
    
    protected void setModified(boolean b) {
        changed = b;
        updateTitle();
    }
    
    protected void load() {
        ProcessBuilder pb = new ProcessBuilder("incrontab", "--list");
        try {
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            model.importTable(br);
            br.close();
            boolean end = false;
            while (!end) {
                try {
                    p.waitFor();
                    end = true;
                } catch (InterruptedException ex) {}
            }
            setModified(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Loading incron table failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void save() {
        ProcessBuilder pb = new ProcessBuilder("incrontab", "-");
        try {
            Process p = pb.start();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            model.exportTable(bw);
            bw.close();
            boolean end = false;
            while (!end) {
                try {
                    int ev = p.waitFor();
                    if (ev != 0)
                        throw new IOException("incrontab return value: " + ev);
                    end = true;
                } catch (InterruptedException ex) {}
            }
            JOptionPane.showMessageDialog(this, "Table successfully saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
            setModified(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Saving incron table failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void doExport() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Export incron table");
            fc.setDialogType(JFileChooser.SAVE_DIALOG);
            fc.setMultiSelectionEnabled(false);
            int res = fc.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                if (f.exists()) {
                    res = JOptionPane.showConfirmDialog(this, "Selected file already exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (res != JOptionPane.YES_OPTION)
                        return;
                }
                model.exportToFile(f);
                JOptionPane.showMessageDialog(this, "Table successfully exported.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            else if (res == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(this, "Error occurred while selecting file for export.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot export table to file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void doImport() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Import incron table");
            fc.setDialogType(JFileChooser.OPEN_DIALOG);
            fc.setMultiSelectionEnabled(false);
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                model.importFromFile(f);
                JOptionPane.showMessageDialog(this, "Table successfully imported.", "Success", JOptionPane.INFORMATION_MESSAGE);
                setModified(true);
            }
            else if (res == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(this, "Error occurred while selecting file for import.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot import table from file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            setModified(true);
        }        
    }
    
    protected void addRow() {
        ICPropDialog dg = new ICPropDialog(new ICRow("", new InotifyEvent(0, 0), ""));
        dg.setVisible(true);
        dg.dispose();
        if (dg.isCommitted() && dg.isModified()) {
            model.addRow(dg.getData());
            setModified(true);
        }
    }
    
    protected void editRow() {
        int ia[] = table.getSelectedRows();
        if (ia.length == 1) {
            ICPropDialog dg = new ICPropDialog(model.getRow(ia[0]));
            dg.setVisible(true);
            dg.dispose();
            if (dg.isCommitted() && dg.isModified()) {
                model.updateRow(ia[0], dg.getData());
                setModified(true);
            }
        }
    }
    
    protected void removeRow() {
        int ia[] = table.getSelectedRows();
        if (ia.length > 0) {
            model.removeRows(ia[0], ia[ia.length-1]);
            setModified(true);
        }
    }
    
    protected void doDispose() {
        ICConfig.setWindowSize(getSize());
        ICConfig.setWindowLocation(getLocation());
        ICConfig.save();
        super.dispose();
    }
    
    public void dispose() {
        if (changed) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Table data has been modified but not saved.\nDo you want to save it before closing?", "Unsaved data",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            switch (opt) {
                case JOptionPane.YES_OPTION:
                    save();
                case JOptionPane.NO_OPTION:
                    doDispose();
                default:;
            }
        }
        else {
            doDispose();
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("new")) {
            model.clear();
            setModified(true);
        }
        else if (s.equals("load")) {
            load();
        }
        else if (s.equals("save")) {
            save();
        }
        else if (s.equals("import")) {
            doImport();
        }
        else if (s.equals("export")) {
            doExport();
        }
        else if (s.equals("quit")) {
            dispose();
        }
        else if (s.equals("add")) {
            addRow();
        }
        else if (s.equals("edit")) {
            editRow();
        }
        else if (s.equals("remove")) {
            removeRow();
        }
        else if (s.equals("about")) {
            JOptionPane.showMessageDialog(this, "jIncron 0.1.0", "About jIncron", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void valueChanged(ListSelectionEvent e) {
        updateMenus();
        updateButtons();
    }
}
