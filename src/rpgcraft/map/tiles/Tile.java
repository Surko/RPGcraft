/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.awt.Image;
import java.util.HashMap;
import rpgcraft.entities.Entity;
import rpgcraft.entities.TileItem;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.SpriteSheet;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.TileResource;

/**
 * Trieda Tile, ktora pri vytvoreni instancie zdruzuje informacie o dlazdici z TileResource.
 * Informacie ako obrazok pre dlazdicu, ci sila dlazdice, meno dlazdice a dalsie atributy
 * ktore sa nachadzaju v TileResource. Vacsinou sa instancie pre kazdu dlazdicu vyskytuju iba raz v nacitanych
 * dlazdiciach tiles. Nacitavanie musi prebehnut pred zacatim hlavnej hry pomocou metody initializeTiles
 * ktora najprv inicializuje zakladne dlazdice a hned nato tie nacitane z TileResource. 
 * Mapa vyuziva iba id-cka tychto dlazdic, ktore su zaroven klucom v tejto hashmape.
 */
public class Tile {    
    // <editor-fold defaultstate="collapsed" desc=" Premenne "
    /**
     * HashMapa so vsetkymi dlazdicami
     */
    public static HashMap<Integer,Tile> tiles;
    /**
     * Text s id nepriechodnej dlazdice
     */
    private static final String IMPASS = "_cross";
    /**
     * Text na parsovanie dlazdice z textu
     */
    private static final String DELIM = ":";
       
    /**
     * Obrazok dlazdice ako hu vnimame z vrchu
     */
    private Image upperImage;
    
    /**
     * Ci sa da plavat cez dlazdicu
     */
    protected boolean swimable;
    /**
     * Ci je dlazdicu mozne znicit
     */
    protected boolean destroyable;
    
    /**
     * HashMapa so Spritami
     */
    protected HashMap<Integer,Sprite> _sprites;
    /**
     * Id dlazdice
     */
    protected Integer id;
    /**
     * Meno dlazdice
     */
    protected String name;       
    /**
     * Sheet v ktorom je obrazok dlazdice
     */
    protected SpriteSheet sheet;
    /**
     * Ako dokaze dlazdica ublizit hracovi alebo inej entite
     */
    protected int damage;
    /**
     * Zivot dlazdice
     */
    protected int health;
    /**
     * Sila dlazdice ktoru musime vykonat na ustedrenie poskodenia
     */
    protected int tileStrength;
    /**
     * Typ predmetu ktory musime pouzit na ustedrenie poskodenia
     */
    protected int itemType;        
    
    /**
     * Cislo aktualnej sprite ktoru vykreslujeme
     */
    protected int sprNum;
    /**
     * Cas kedy sme naposledy zmenili sprite dlazdice
     */
    protected long waypointTime;
    /**
     * Aktualny cas dlazdice
     */
    protected long currentTime;
    /**
     * Maximalny cas zobrazenia dlazdice a nasledneho prehodenia na dalsi sprite
     */
    protected long maxTime;
    // </editor-fold>
    
    // PUBLIC METHODS
 
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktor vytvor instanciu dlazdice s id-ckom. Udaje o  dlazdici
     * inicializujeme z TileResource ako su meno, zivot, sila, sprity pre dlazdicu.
     * @param id
     * @param res 
     */
    public Tile(Integer id, TileResource res) {
        this.id = id;
        this.upperImage = ImageResource.getResource(IMPASS).getBackImage();
        if (res != null) {
            this.itemType = res.getItemType();
            this.tileStrength = res.getTileStrength();
            this._sprites = res.getTileSprites();            
            this.tileStrength = res.getTileStrength();
            this.health = res.getHealth();
            this.damage = res.getDamage();    
            this.name = res.getName();
        }
    }
    // </editor-fold>
    
    /**
     * Metoda ktora nastavi meno dlazdice
     * @param name Meno dlazdice
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Metoda ktora nastavi id dlazdice
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Metoda vracia ci sa cez dlazdicu musi preplavat
     * @return premenna swimable [type:BOOLEAN]
     */    
    public boolean isSwimable() {
        return swimable;
    }
    
    /**
     * Metoda vracia ID jednej dlazdice
     * @return premenna id [type:INTEGER]
     */    
    public int getId() {
        return id;
    }
    
    /**
     * Metoda vracia meno jednej dlazdice
     * @return premenna name [type:STRING]
     */    
    public String getName() {
        return name;
    }      
    
    /**
     * Metoda vracia zivot dlazdice
     * @return Zivot dlazdice
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Metoda vrati silu utoku dlazdice na entitu
     * @return Sila utoku
     */
    public int getDamage() {
        return damage;                       
    }
    
    /**
     * Metoda vrati odolnost entity
     * @return Odolnost entity
     */
    public int getTileStrength() {
        return tileStrength;
    }
    
    /**
     * Metoda vrati type predmetu ktory dokaze rozbit dlazdicu
     * @return Predmet v ciselnej podobe z WeaponType nutny na rozbitie
     */
    public int getItemType() {
        return itemType;
    }
    
    /**
     * Metoda ktora vrati ci je dlazdicu mozne znicit
     * @return True/false ci je dlazdicu mozne znicit
     */
    public boolean isDestroyable() {
        return destroyable;
    }
    
    /**
     * Metoda ktora vrati obrazok dlazdice podla urciteho meta data.
     * @param meta Meta data obrazku dlazdice ktory chceme
     * @return Obrazok dlazdice pre urcite meta data
     */
    public Image getImage(int meta) {        
        return _sprites == null ? null : 
                (_sprites.containsKey(meta) ? _sprites.get(meta).getSprite() : _sprites.get(0).getSprite());
    }
    
    /**
     * Metoda ktora vrati vrchny obrazok dlazdice. To je obrazok ktory vidime ked sme
     * o jeden level menej ako dlazdica.
     * @return Vrchny obrazok dlazdice
     */
    public Image getUpperImage() {
        return upperImage;
    }

    /**
     * Metoda ktora posunie entitu do dlazdice a vykona nejake akcie.
     * @param e Entita ktoru sme posunuli do dlazdice.
     */
    public void moveInto(Entity e) {
        
    }    
    
    /**
     * Metoda ktora vrati ci sa dve dlazdice rovnaju porovnavanim id-cok
     * @param tile Dlazdica s ktorou porovnavame aktualnu dlazdicu
     * @return True/false ci sa dlazdice rovanju
     */
    public boolean equalsTo(Tile tile) {        
        return this.id == tile.id ? true : false;
    }
    
    /**
     * Metoda ktora vrati dlazdicu z dlazdicoveho predmetu
     * @param item Predmet z ktoreho chceme vratit dlazdicu
     * @return Dlazdica ktoru sme dostali z predmetu
     */
    public static Tile getTile(TileItem item) {
        return tiles.get(item.getTileId());
    }
    
    /**
     * Metoda ktora vrati dlazdicu podla <b>id</b>.
     * @param id Id dlazdice ktory chceme
     * @return Dlazdica s urcitym id.
     */
    public static Tile getTile(String id) {
        return tiles.get(Integer.parseInt(id));
    }
    
    /**
     * Metoda ktora inicializuje dlazdice pri zacati hry. Nacitavame zakladne dlazdice
     * a knim tie nacitane z TileResource.
     */
    public static void initializeTiles() {        
        tiles = DefaultTiles.createDefaultTiles();
        LoadedTiles.createLoadedTiles(tiles);        
    }        
}
