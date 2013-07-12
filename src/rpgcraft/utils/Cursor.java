/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

/**
 * Abstraktna trieda Cursor (velmi podobna tomu v AndroidAPI). Vytvara nam interface 
 * pre rozne kurzory. Zatial ho implementuje iba trieda ListCursor nachadzajuca sa v ListModeli.
 * Instancia takeho Cursoru poskytuje metody na ziskanie pozicii, dat ktore sa v kurzore nachadzaju 
 * a stlpcov reprezentujuce jednotlive data v poli.
 * 
 */
public abstract class Cursor {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Data v riadkoch
    protected Object[] rowData;
    // Mena stlpcov
    protected String[] columns;
    // Aktualna pozicia kurzoru
    protected int position;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    /**
     * Metoda sa ma posunut aktualnu poziciu kurzoru
     * @param position Pozicia na aku sa posuvame
     */
    public abstract void moveToPosition(int position);
    /**
     * Metoda ktora vrati velkost kurzoru
     * @return Pocet dat v kurzore
     */
    public abstract int getCount();    
    /**
     * Metoda ktora vytvori iterator z dat v kurzore
     */
    public abstract void makeIterator();
    /**
     * Metoda ktora sa posunie vo vytvorenom iteratore o jednu poziciu
     */
    public abstract void next();
    /**
     * Metoda ktora vrati true/false podla toho ci sa mozme dalej posunut v iteratore.
     * @return True/false ci sa da posunut dalej v iteratore.
     */
    public abstract boolean hasNext();
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Zakladny konstruktor pre vytvorenie instancie Cursoru.
     */
    public Cursor() {}
    
    /**
     * Konstruktor ktory vytvori instanciu Cursor s nastavenim stlpcov
     * z parametru. Mozne volat iba z podedenych tried
     * @param columns Stlpce v kurzoru.
     */
    public Cursor(String[] columns) {
        this.columns = columns;
    }        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati index stlpca s nejakym menom. 
     * @param colName Meno stlpcu ktory hladame
     * @return Index stlpcu s menom
     */
    public int getColumnIndex(String colName) {
        for (int i = 0;i<columns.length;i++) {
            if (columns[i].equals(colName)) return i;
        }
        return -1;    
    }
    
    /**
     * Metoda vrati aktualnu poziciu kurzoru
     * @return Pozicia kurzoru
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Metoda ktora vrati index stlpcu
     * @param index Index stlpca ktory chceme.
     * @return Stlpec pod indexom.
     */
    public String getColumnName(int index) {
        return columns[index];
    }
    
    /**
     * Metoda ktora vrati vsetky stlpce v poli.
     * @return Stlpce v poli.
     */
    public String[] getColumns() {
        return columns;
    }
    
    /**
     * Metoda ktora vrati pocet stlpcov.
     * @return Pocet stlpcov
     */
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
    // </editor-fold>
}
