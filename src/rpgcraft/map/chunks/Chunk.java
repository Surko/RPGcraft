/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.chunks;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import rpgcraft.map.tiles.DefaultTiles;

/**
 * Trieda ktora v sebe obsahuje zakladnu pracu s obsahom chunku, ukladanim chunkov
 * na disk a meneni jednotlivych dlazdic aj s meta datami v chunku. Pre ukladanie a
 * nacitanie chunkov trieda implementuje Externalizable interface. Na vytvorenie
 * instancie je nutne volat jeden z konstruktorov Chunk pre inicializovanie
 * pozicie chunku vo svete a s datami ulozenymi v chunku.
 */
public class Chunk implements Externalizable {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * UID pre serializaciu
     */
    private static final long serialVersionUID = 912804676578087866L;
    /**
     * Sirka a vyska chunku v podobe dlazdic
     */
    private static final int CHUNK_SIZE = 16;
    /**
     * Hlbka chunku
     */
    private static final int DEPTH = 128;

    /**
     * Konstanta na predelovanie bitovymi operaciami.
     */
    public static final int CHUNKMOD = CHUNK_SIZE - 1;

    /**
     * Obsah chunku
     */
    private ChunkContent blocks;
    /**
     * X-ova pozicia chunku vo svete
     */
    private int x;
    /**
     * Y-ova pozicia chunku vo svete
     */
    private int y;
    /**
     * Ci je chunk nacitany z disku
     */
    private boolean loaded = false;

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor pre vytvorenie chunku volany pri nacitavani Chunkov z metodou
     * readExternal.
     */
    public Chunk() {
        this.blocks = new ChunkContent();
    }

    /**
     * Konstruktor na vytvoreni instancie Chunku. Inicializujeme x a y-ove pozicie chunku
     * s vytvorenim novej instancie ChunkContent v ktorom sa bude nachadzat obsah
     * s id-ckami a meta datami pre dlazdice.
     * @param x X-ova pozicia chunku
     * @param y Y-ova pozicia chunku
     */
    public Chunk(int x, int y) {
        this.blocks = new ChunkContent();
        this.x = x;
        this.y = y;
    }   

    /**
     * Konstruktor na vytvorenie instancie Chunku. Inicializujeme obsah chunku,
     * x a y-ove pozicie chunku priamo z parametrov
     * @param blocks Obsah chunku
     * @param x X-ova pozicia chunku
     * @param y Y-ova pozicia chunku
     */
    public Chunk(ChunkContent blocks, int x, int y) {
        this.blocks = blocks;
        this.x = x;
        this.y = y;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati sirku chunku
     * @return Sirka chunku
     */
    public static int getSize() {
       return CHUNK_SIZE;
    }
    
    /**
     * Metoda ktora vrati vysku chunku
     * @return Hlabka chunku
     */
    public static int getDepth() {
        return DEPTH;
    }

    /**
     * Metoda ktora vrati x-ovu poziciu chunku vo svete
     * @return X-ova pozicia chunku
     */
    public int getX() {
        return x;
    }

    /**
     * Metoda ktora vratiy-ovu poziciu chunku vo svete
     * @return Y-ova pozicia chunku
     */
    public int getY() {        
        return y;
    }
    
    
    /**
     * Metoda ktora vrati dlazdicu na danych poziaich
     * @param layer Vyska odkial chceme dlazdicu
     * @param x X-ova pozicia dlazdice
     * @param y Y-ova pozicia dlazdice
     * @return Id dlazdice na poziciach
     */
    public int getTile(int layer, int x, int y) {            
        return blocks.getIntOnPosition(layer, x & 15, y & 15);
    }

    /**
     * Metoda ktora vrati meta data na danych poziaich
     * @param layer Vyska odkial chceme data
     * @param x X-ova pozicia data
     * @param y Y-ova pozicia data
     * @return Meta data na poziciach
     */
    public int getMetaData(int layer, int x, int y) {
        return blocks.getMetaData(layer, x & 15, y & 15);
    }

    /**
     * Metoda ktora vrati z obsahu meta vrstvu vo vyske <b>layer</b>.
     * @param layer Vyska vrstvy ktoru chceme
     * @return Meta vrstva chunku
     */
    public int[][] getMetaDataLayer(int layer) {
        return blocks.getMetaDataLayer(layer);
    }

    /**
     * Metoda ktora vrati z obsahu vrstvu vo vyske <b>layer</b>.
     * @param layer Vyska vrstvy ktoru chceme
     * @return Vrstva chunku
     */
    public int[][] getLayer(int layer) {
        return blocks.getLayer(layer);
    }
    
    /**
     * Metoda ktora vrati ci je chunk nacitany
     * @return True/false ci je chunk nacitany
     */
    public boolean getLoaded() {
        return loaded;
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Metoda ktora nastavi obsah chunku podla parametru <b>content</b>.
     * @param content Obsah chunku.
     */
    public void setContent(ChunkContent content) {
        this.blocks = content;
    }
    
    /**
     * Metoda ktora nastavi meta hodnotu na poziciach zadanych parametrami
     * @param layer Vyska kde menime hodnotu
     * @param x X-ova pozicia kde menime hodnotu
     * @param y Y-ova pozicia kde menime hodnotu
     * @param value Hodnota na ktoru nastavujeme hodnotu
     */
    public void setMeta(int layer, int x, int y, int value) {            
        blocks.setMetaData(layer, x & 15, y & 15, value);
    }

    /**
     * Metoda ktora nastavi hodnotu dlazdice aj meta na poziciach zadanych parametrami
     * @param layer Vyska kde menime hodnotu
     * @param x X-ova pozicia kde menime hodnotu
     * @param y Y-ova pozicia kde menime hodnotu
     * @param value Hodnota na ktoru nastavujeme hodnotu
     * @return True/false ci sa podarila zmena
     */
    public boolean setTile(int layer, int x, int y, int value, int meta) {            
        return blocks.setTileOnPosition(layer, x, y, value, meta);            
    }

    /**
     * Metoda ktora nastavi meta hodnotu dlazdice na poziciach zadanych parametrami
     * @param layer Vyska kde menime hodnotu
     * @param x X-ova pozicia kde menime hodnotu
     * @param y Y-ova pozicia kde menime hodnotu
     * @param value Hodnota na ktoru nastavujeme hodnotu
     * @return True/false ci sa podarila zmena
     */
    public boolean setTile(int layer, int x, int y, int value) {            
        return blocks.setIntOnPosition(layer, x & 15, y & 15, value);
    }

    /**
     * Metoda ktora nastavi ci je chunk nacitany z disku
     * @param loaded True/false podla toho ci je nacitany.
     */
    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Metoda ktora znici dlazdicu na danych poziaich. Na nicenie dlazdic pouzivame
     * metodu replaceIntOnPosition, ktora netestuje ci sa nachadza nieco na poziciach.
     * @param layer Vyska kde nicime dlazdicu
     * @param x X-ova pozicia kde nicime dlazdicu
     * @param y Y-ova pozicia kde nicime dlazdicu
     */
    public void destroyTile(int layer, int x, int y) {
        blocks.replaceIntOnPosition(layer, x & 15, y & 15, DefaultTiles.BLANK_ID);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    /**
     * Metoda ktora zapise chunk do vystupneho suboru zadaneho parametrom <b>out</b>
     * Zapisujeme x-ovu, y-ovu poziciu chunku a nasledne pole s id-ckami dlazdic
     * a meta dat.
     * @param out Vystupny subor
     * @throws IOException Vynimka pri zapise do suboru
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {    
        out.writeInt(x);
        out.writeInt(y);
        for (int k = 0; k < DEPTH; k++) {
            for (int i = 0; i < CHUNK_SIZE; i++ ) {
                for (int j = 0; j< CHUNK_SIZE; j++) {                    
                    out.writeInt(blocks.getIntOnPosition(k, i, j));
                    out.writeInt(blocks.getMetaData(k, i, j));
                }
            }
        }
    }

    /**
     * Metoda ktora nacita chunk z vstupneho suboru zadaneho parametrom <b>in</b>
     * Nacitavame x-ovu, y-ovu poziciu chunku a nasledne pole s id-ckami dlazdic
     * a meta dat.
     * @param in Vstupny subor
     * @throws IOException Vynimka pri nacitavani zo suboru
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {                
        this.x = in.readInt();       
        this.y = in.readInt();
        for (int k = 0; k < DEPTH; k++) {
            for (int i = 0; i < CHUNK_SIZE; i++ ) {
                for (int j = 0; j< CHUNK_SIZE; j++) {
                        blocks.setIntOnPosition(k, i, j, in.readInt());
                        blocks.setMetaData(k, i, j, in.readInt());
                }
            }
        }                

    }
    // </editor-fold>
        
}
