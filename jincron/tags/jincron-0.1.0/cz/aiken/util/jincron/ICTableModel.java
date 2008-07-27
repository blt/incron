/*
 * ICTableModel.java - jIncron table model class
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

import javax.swing.table.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author luk
 */
public class ICTableModel extends AbstractTableModel {
    
    private ArrayList<ICRow> rows = new ArrayList<ICRow>();
    
    /** Creates a new instance of ICTableModel */
    public ICTableModel() {
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ICRow r = rows.get(rowIndex);
        Object o = null;
        
        switch (columnIndex) {
            case 0: o = r.getPath();
                break;
            case 1: o = r.getEvent().toString();
                break;
            case 2: o = r.getCommand();
                break;
            default:;
        }
        
        return o;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return ICRow.getFieldCount();
    }

    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Path";
            case 1: return "Events";
            case 2: return "Command";
            default: return "";
        }
    }
    
    public ICRow getRow(int index) {
        return rows.get(index);
    }

    public void updateRow(int index, ICRow row) {
        rows.set(index, row);
        fireTableRowsUpdated(index, index);
    }
    
    public void addRow(ICRow r) {
        rows.add(r);
        int index = rows.size() - 1;
        fireTableRowsInserted(index, index);
    }
    
    public void removeRows(int start, int end) {
        for (int i=end; i>=start; i--)
            rows.remove(i);
        fireTableRowsDeleted(start, end);
    }
    
    public void clear() {
        int size = rows.size();
        rows.clear();
        fireTableRowsDeleted(0, size - 1);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<ICRow> it = rows.iterator();
        String sep = System.getProperty("line.separator");
        
        while (it.hasNext()) {
            ICRow r = it.next();
            sb.append(r.toString());
            sb.append(sep);
        }
        
        return sb.toString();
    }
    
    public void exportTable(Writer w) throws IOException {
        w.write(toString());
    }
    
    public void importTable(Reader r) throws IOException {
        BufferedReader br = new BufferedReader(r);
        int end = rows.size() - 1;
        if (end > -1) {
            rows.clear();
            fireTableRowsDeleted(0, end);
        }
        String s = "";
        while ((s = br.readLine()) != null) {
            ICRow row = ICRow.parse(s);
            if (row != null)
                rows.add(row);
        }
        
        end = rows.size() - 1;
        if (end > -1) {
            fireTableRowsInserted(0, end);
        }
    }
    
    public void exportToFile(File f) throws IOException {
        FileWriter fw = new FileWriter(f);
        exportTable(fw);
        fw.close();
    }
    
    public void importFromFile(File f) throws IOException {
        FileReader fr = new FileReader(f);
        importTable(fr);
        fr.close();
    }
}
