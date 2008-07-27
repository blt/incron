/*
 * Task.java - implementation of tracked task
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

import javax.swing.Timer;
import java.awt.event.*;
import java.math.*;

/**
 * This class represents a tracked task.
 * @author luk
 */
public class Task implements ActionListener, Comparable<Task> {
    private int id;
    
    private String name = "Unnamed task";
    private long consumption = 0L;
    private double price = 1;
    
    private Timer timer = null;
    /**
     * period for updating time values [ms]
     */
    public static final int PERIOD = 10000;
    
    /**
     * time units per hour
     */
    public static final double UNITS_PER_HOUR = 3600000;
    
    private ActionListener listener = null;
    
    private static int nextId = 0;
            
    /**
     * Generates a new task identifier.
     * @return new identifier
     */
    public static int getNewId() {
        int id = nextId;
        nextId++;
        return id;
    }
    
    /**
     * Creates a new instance of Task
     */
    public Task() {
        id = getNewId();
    }
    
    /**
     * Creates an instance for a task which has been already tracked.
     * @param id task identifier
     * @param name task name
     * @param consumption up to now time consumption
     * @param price price per hour
     */
    public Task(int id, String name, long consumption, double price) {
        this.id = id;
        this.name = name;
        this.consumption = consumption;
        this.price = price;
        
        if (id >= nextId)
            nextId = id + 1;
    }
    
    /**
     * Sets the listener where action events should be passed to.
     * @param al action listener
     */
    public void setActionListener(ActionListener al) {
        listener = al;
    }
    
    /**
     * Returns the task identifier.
     * @return task identifier
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the task name.
     * @return task name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the task name.
     * @param name new task name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the current value of cumulative consumption [ms].
     * @return current cumulative consumption
     */
    public long getConsumption() {
        return consumption;
    }
    
    /**
     * Sets the task cumulative time consumption [ms].
     * @param consumption new time consumption value
     */
    public void setConsumption(long consumption) {
        this.consumption = consumption;
    }
    
    /**
     *  Sets the price per hour [currency unit].
     *  Changing this parameter affects the current total price of the task.
     * @param price new price value
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Returns the current price per hour [currency unit].
     * @return current price value
     */
    public double getPrice() {
        return price;
    }
    
    /**
     *  Returns the total price of this task [currency unit].
     * @return total price of this task
     */
    public double getTotalPrice() {
        return ((double) consumption) / UNITS_PER_HOUR * price;
    }
    
    /**
     * Converts the instance to the string representation. It contains
     * the task name and consumption.
     * @return string representation
     */
    @Override
    public String toString() {
        return name + "(" + Long.toString(consumption) + ")";
    }
    
    /**
     * Starts tracking of this task.
     */
    public void start() {
        if (timer != null)
            return;
        
        timer = new Timer(PERIOD, this);
        timer.start();
    }
    
    /**
     * Stops tracking of this task.
     */
    public void stop() {
        if (timer == null)
            return;
        
        timer.stop();
        timer = null;
    }
    
    /**
     * Checks whether the task is running.
     * @return <CODE>true</CODE> if running, <CODE>false</CODE> otherwise
     */
    public boolean isRunning() {
        return timer != null;
    }
    
    /**
     * Updates the time consumption value. Then it creates a new action event
     * and passes it to the assigned listener (if any).
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        consumption += PERIOD;
        
        ActionEvent e2 = new ActionEvent(this, id, "");
        
        if (listener != null)
            listener.actionPerformed(e2);
    }

    /**
     * Compares this task to another one. The comparison result is based
     * on task identifiers.
     * @param t another task
     * @return 1 if this class' id is greater then the given class' id,
     * -1 if both ids are equal and 0 otherwise
     */
    public int compareTo(Task t) {
        if (t == null)
            throw new NullPointerException("cannot compare to null pointer");
        
        if (id > t.getId())
           return 1;
        else if (id == t.getId())
            return 0;
        else
            return -1;
    }    
    
}
