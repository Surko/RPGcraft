/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author kirrie
 */
public class ListModel {    
    private ArrayList<Object[]> data;
    private Cursor c;
    
    public ListModel(Object[][] data, String[] columns) {
        c = new ListCursor(columns);
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        for (Object[] _data : data) {
            this.data.add(_data);
        }
    }
    
    public ListModel(Object[][] data) {
        c = new ListCursor();
    }    
    
    public class ListCursor extends Cursor {                
        Iterator<Object[]> iterator;
        
        public ListCursor(String[] columns) {
            super(columns);
        }    
        
        public ListCursor() {}
        
        @Override
        public void moveToPosition(int position) {
            rowData = data.get(position);
        }                                                
        
        @Override
        public int getCount() {
            return data.size();
        }
        
        @Override
        public void makeIterator() {
            iterator = data.iterator();
        }
        
        @Override
        public void next() {
            rowData = iterator.next();            
        }
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
    }
            
    public void add(int i, Object[] objects) {
        data.add(i, objects);
    }
    
    public Cursor getCursor() {
        return c;
    }
    
    public void setColumns(String[] columns) {
        c.columns = columns;
    }
    
    
}
