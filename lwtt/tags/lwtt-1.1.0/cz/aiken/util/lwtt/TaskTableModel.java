/*
 * TaskTableModel.java - implementation of tracked tasks table model
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
import javax.swing.table.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.math.*;
import java.text.*;

/**
 * This class represents the task table model.
 * @author luk
 */
public class TaskTableModel extends AbstractTableModel implements ActionListener {
    
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private javax.swing.Timer timer = new javax.swing.Timer(300000, this);
    
    private MessageFormat timeFormat = new MessageFormat("{0,number}:{1,number,00}");
    
    private TaskFrame taskFrame = null;
    
    /**
     * Creates a new instance of TaskTableModel
     * @param tf task frame instance
     */
    public TaskTableModel(TaskFrame tf) {
        taskFrame = tf;
        loadFromFile();
        timer.start();
    }

    /**
     * Returns the value at the given coordinates.
     * @param rowIndex row index
     * @param columnIndex column index
     * @return appropriate cell value; if the arguments are invalid
     * it returns <CODE>null</CODE>
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return tasks.get(rowIndex).getName();
            case 1:
                long mins = tasks.get(rowIndex).getConsumption() / 60000;
                BigDecimal hm[] = new BigDecimal((int) mins).divideAndRemainder(new BigDecimal(60));
                return timeFormat.format(hm);
            default: return null;
        }
    }

    /**
     * Returns the row count.
     * @return row count
     */
    public int getRowCount() {
        return tasks.size();
    }

    /**
     * Returns the column count (currently 2).
     * @return column count
     */
    public int getColumnCount() {
        return 2;
    }

    /**
     * Sets a new value of the given cell. If at least one of the
     * coordinates is invalid it does nothing.
     * @param aValue new value
     * @param rowIndex row index
     * @param columnIndex column index
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: tasks.get(rowIndex).setName((String) aValue);
                break;
        }
    }

    /**
     * Returns the name of the given column.
     * @param column column index
     * @return column name; if the index is invalid it returns an empty
     * string
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Task name";
            case 1: return "Time consumption [h:min]";
            default: return "";
        }
    }

    /**
     * Returns the class of the given column.
     * @param columnIndex column index
     * @return appropriate class object; if the column index is invalid
     * it returns <CODE>Void.class</CODE>.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return String.class;
            case 1: return StringBuffer.class;
            default: return Void.class;
        }
    }

    /**
     * Checks whether the given cell is editable.
     * @param rowIndex row index
     * @param columnIndex column index
     * @return <CODE>true</CODE> for the first column (index 0),
     * <CODE>false</CODE> otherwise
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
    
    /**
     * Creates a new task.
     */
    public void addNewTask() {
        Task t = new Task();
        t.setActionListener(this);
        tasks.add(t);
        
        int row = tasks.size()-1;
        fireTableRowsInserted(row, row);
    }
    
    /**
     * Removes the given tasks.
     * @param start start index
     * @param end end index (including)
     */
    public void removeTasks(int start, int end) {
        for (int i=end; i>=start; i--) {
            Task t = tasks.remove(i);
            t.stop();
            t.setActionListener(null);
        }
        
        fireTableRowsDeleted(start, end);
    }
    
    /**
     * Starts the given tasks.
     * @param start start index
     * @param end end index (including)
     */
    public void startTasks(int start, int end) {
        for (int i=start; i<=end; i++) {
            Task t = tasks.get(i);
            t.start();
            fireTableCellUpdated(i, 1);
        }        
    }
    
    /**
     * Stops the given tasks.
     * @param start start index
     * @param end end index (including)
     */
    public void stopTasks(int start, int end) {
        for (int i=start; i<=end; i++) {
            Task t = tasks.get(i);
            t.stop();
            fireTableCellUpdated(i, 1);
        }
    }
    
    /**
     * Stops all tasks.
     */
    public void stopAllTasks() {
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            it.next().stop();
        }
        
        fireTableDataChanged();
    }
    
    /**
     * Resets the given tasks.
     * @param start index of the first resetted task
     * @param end index of the first NOT resetted task (the first task beyond the interval)
     */
    public void resetTasks(int start, int end) {
        for (int i=start; i<=end; i++) {
            Task t = tasks.get(i);
            t.setConsumption(0);
            fireTableCellUpdated(i, 1);
        }
    }
    
    /**
     * Destroys the timer controlling automatic data saving.
     */
    public void cancelAutoSave() {
        timer.stop();
    }
    
    /**
     * Returns the absolute path to the directory where LWTT data should be
     * saved.
     * @return directory path
     */
    public static File getDir() {
        Properties sys = System.getProperties();
        return new File(sys.getProperty("user.home"), ".lwtt");
    }
    
    /**
     * Returns the absolute path to the file where LWTT data should be
     * saved.
     * @return absolute file path
     */
    public static File getPath() {
        return new File(getDir(), "data.xml");
    }
    
    /**
     * Checks whether the given task is running.
     * @param index task index
     * @return <CODE>true</CODE> for running task,
     * <CODE>false</CODE> otherwise
     */
    public boolean isRunning(int index) {
        return tasks.get(index).isRunning();
    }
    
    /**
     * Loads application's data from the file.
     */
    public synchronized void loadFromFile() {
        tasks.clear();
        
        File file = getPath();
        if (!file.exists())
            return;
        
        try {
            FileInputStream is = new FileInputStream(file);
            Properties props = new Properties();
            props.loadFromXML(is);
            is.close();
            
            taskFrame.setStartSettings(props);
            
            Iterator<Object> it = props.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.endsWith(".name")) {
                    String ids = key.substring(0, key.length() - 5);
                    String name = props.getProperty(ids + ".name");
                    String cons = props.getProperty(ids + ".consumption");
                    try {
                        int id = Integer.parseInt(ids);
                        long cn = Long.parseLong(cons);
                        Task t = new Task(id, name, cn);
                        t.setActionListener(this);
                        tasks.add(t);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Cannot load data from file (bad format).", "Error", JOptionPane.ERROR_MESSAGE);
                    }                    
                }
            }
            
            Collections.sort(tasks);
            
            fireTableDataChanged();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Saves application's data to the file.
     */
    public synchronized void saveToFile() {
        File dir = getDir();
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                JOptionPane.showMessageDialog(null, "Cannot save data to file (cannot create data directory).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        Properties props = new Properties();
        props.setProperty("window.location.x", Integer.toString(taskFrame.getX()));
        props.setProperty("window.location.y", Integer.toString(taskFrame.getY()));
        props.setProperty("window.size.w", Integer.toString(taskFrame.getWidth()));
        props.setProperty("window.size.h", Integer.toString(taskFrame.getHeight()));
        for (int i=0; i<tasks.size(); i++) {
            Task t = tasks.get(i);
            String id = Integer.toString(t.getId());
            props.setProperty(id + ".name", t.getName());
            props.setProperty(id + ".consumption", Long.toString(t.getConsumption()));
        }
        
        try {
            FileOutputStream os = new FileOutputStream(getPath());
            props.storeToXML(os, "LWTT task data");
            os.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot save data to file (" + e.getLocalizedMessage() + ").", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Processes an action event.
     * 
     * If the event has been generated by the auto-save timer it saves
     * the data. Otherwise (a button action occurred) it updates
     * the appropriate table cell.
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == timer) {
            saveToFile();
        }
        else {
            int row = tasks.indexOf(src);
            fireTableCellUpdated(row, 1);
        }
    }
    
}
