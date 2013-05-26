/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components;

/**
 *
 * @author kirrie
 */
public abstract class Cursor {
    protected Object[] rowData;
    protected String[] columns;
    protected int position;
    
    public abstract void moveToPosition(int position);
    public abstract int getCount();    
    public abstract void makeIterator();
    public abstract void next();
    public abstract boolean hasNext();
    
    public Cursor() {}
    
    public Cursor(String[] columns) {
        this.columns = columns;
    }        
    
    public int getColumnIndex(String colName) {
        for (int i = 0;i<columns.length;i++) {
            if (columns[i].equals(colName)) return i;
        }
        return -1;    
    }
    
    public int getPosition() {
        return position;
    }
    
    public String getColumnName(int index) {
        return columns[index];
    }
    
    public String[] getColumns() {
        return columns;
    }
    
    public int getColumnCount() {
        return columns.length;
    }        
    
    /**
     * Metoda vracia hodnotu pozadovaneho stlpca ako textovy retazec.
     * Vysledok a ci metoda vyhodi vynimku (napriklad pri hodnote stlpca null alebo ked typ stlpca
     * nie je String) zavisi od dalsej implementacie.
     * @param index Index pozadovaneho stlpca
     * @return Hodnotu v rowData pod indexom
     */
    public String getString(int index) {
        return (String)rowData[index];
    }
}
