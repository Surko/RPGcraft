/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Trieda ktorej instancia vytvara model pre SwingImageList. Model v sebe obsahuje
 * definovany ListCursor dediaci od Cursor. Pomocou metod mozme prechadzat kurzorom
 * a vyberat jednotlive data z modelu. Pomimo prechadzanie kurzoru mozme do modelu
 * pridavat a odoberat data po riadkoch. Instanciu vytvarame pomocou konstruktoru
 * ListModel s parametrom columns, ktore tvoria stlpce jednotlivych riadkov.
 */
public class ListModel {    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // List s datami (2D pole)
    private ArrayList<Object[]> data;
    // Kurzor priradeny k list modelu
    private Cursor c;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Metoda ktora vytvori ListModel z dat a stlpcov zadane v parametroch.
     * Konstruktor taktiez inicializuje listovy cursor podla tychto zadanych stlpcov.
     * @param data Data ktore su v modeli
     * @param columns Stlpce jednotlivych riadkov
     */
    public ListModel(Object[][] data, String[] columns) {
        c = new ListCursor(columns);
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        for (Object[] _data : data) {
            this.data.add(_data);
        }
    }
    
    /**
     * Konstruktor pre vytvorenie modelu bez blizsie urcenych stlpcov. Inicializujeme prazdny
     * listovy cursor.
     * @param data Data ktore su v modeli.
     */
    public ListModel(Object[][] data) {
        c = new ListCursor();
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        for (Object[] _data : data) {
            this.data.add(_data);
        }
    }    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ListCursor pre Model ">
    /**
     * Trieda ListCursor ktora implementuje abstraktnu triedu Cursor. Instancia
     * nam poskytuje prechadzanie dat v tomto modeli a vyberanie jednotlivych udajov
     * podla definovych stlpcov. Na prehladavanie modelovych dat nam sluzi iterator.
     * Takties trieda poskytuje moznost skoku na urcitu poziciu pomocou metody moveToPosition.
     * @see Cursor
     */
    public class ListCursor extends Cursor { 
        // Iterator pre prechadzanie jednotlivych riadkov modelu
        Iterator<Object[]> iterator;
        
        /**
         * Konstruktor ListCursoru podla riadkov.
         * @param columns Nazvy stlpcov jednotlivych riadkov
         */
        public ListCursor(String[] columns) {
            super(columns);
        }    
        
        /**
         * Prazdny konstruktor na vytvorenie ListModel.
         */
        public ListCursor() {}
        
        /**
         * Metoda ktora posunie poziciu kurzoru na poziciu <b>position</b>
         * @param position Pozicia na ktoru sa posuvame
         */
        @Override
        public void moveToPosition(int position) {
            if (data != null && position < data.size()) {
                this.position = position;
                rowData = data.get(position);
            }
        }                                            
        
        /**
         * Meteda ktora vrati pocet dat v modeli.
         * @return Pocet dat
         */
        @Override
        public int getCount() {
            return data.size();
        }
        
        /**
         * Metoda ktora vytvara iterator prechadzajuci jednotlive riadky v modeli.
         */
        @Override
        public void makeIterator() {
            iterator = data.iterator();
        }
        
        /**
         * Metoda ktora posuva iterator.
         */
        @Override
        public void next() {
            rowData = iterator.next();            
        }
        
        /**
         * Metoda ktora vrati true/false podla toho ci je iterator nakonci.
         * @return True/false podla koncu iteratoru.
         */
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
    }
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Metody modelu ">
    /**
     * Metoda ktora prida do listu s datami novy riadok/pole objektov zadany parametrom
     * <b>objects</b>. Pridavame ho na poziciu <b>i</b>
     * @param i Pozicia kam pridavame riadok
     * @param objects Riadok/pole objektov ktore pridavame do modelu
     */
    public void add(int i, Object[] objects) {
        data.add(i, objects);
    }
    
    /**
     * Metoda ktora odoberie riadok z modelu na pozicii <b>i</b>.
     * @param i Index riadku odkial odstranujeme data
     */
    public void remove(int i) {
        if (i < data.size()) {
            data.remove(i);
        }
    }
    
    /**
     * Metoda ktora vrati velkost dat v tomto modeli.
     * @return Velkost modelu.
     */
    public int getSize() {
        return data.size();
    }
    
    /**
     * Metoda ktora vrati kurzor tohoto modelu. Kurzorom mozme prechadzat jednotlive
     * data v modeli.
     * @return Cursor tohoto modelu.
     * @see ListCursor
     */
    public Cursor getCursor() {
        return c;
    }
    
    /**
     * Metoda ktora nastavi nove stlpce tohoto modelu. Prenastavujeme 
     * stlpce v kurzore.
     * @param columns Nove stlpce pre model.
     */
    public void setColumns(String[] columns) {
        c.columns = columns;
    }
    // </editor-fold>
    
}
