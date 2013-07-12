/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.awt.Image;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.map.tiles.AttackedTile;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;



/**
 * Trieda TileItem dediaca od predmetu nam zarucuje ze sa da s instanciami od nej pracovat
 * ako s predmetom. Spristupnene su vsetky metody a premenne z triedy Item, co znamena ze aj
 * metody z triedy Entity. TileItem instance maju nastaveny typ predmetu (itemType) na TILE. Subtyp tohoto typu
 * je dalej urceny enumom TileType v tejto triede. V tejto dobe je trieda je len ako
 * rozlisovac medzi roznymi typmi predmetov s tym ze defaultne operacie, ako je napriklad
 * ci sa da predmet polozit naspat na mapu ako dlazdica.
 * <p>
 * Dlazdicovy predmet je ale zaujimavy v jednej veci a to v tom ze sa da vytvori bud priamo z dlazdice
 * alebo z resource ako ostatne predmety. Pri vytvoreni z dlazdice sa snazime ziskat
 * EntityResource z id dlazdice. Pri nepodareni nastavime zakladne vlastnosti pre dlazdicu
 * a obrazok na zobrazenie je len jednoduchy jeden obrazok. <br>
 * Naopak pri existencii resource ziskame obrazky ako pri inych predmetoch a to
 * priamo z resource metodou getEntitySprites. <br>
 * Pri navrate obrazku metodou getTypeImage potom rozhodujeme ci vratime len jednoduchy obrazok
 * alebo volame metodu getTypeImage z triedy Item.
 * </p> 
 * @see Item
 * @author kirrie
 */
public class TileItem extends Item {
      
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/enumy ">
    /**
     * Enum s vypisom typov pre dlazdice.
     */ 
    public enum TileType implements TypeofItems{
        TILE(StringResource.getResource("tileitem"));

        private String value;
        
        private TileType(String value) {
            this.value = value;
        }
        
        @Override
        public String getValue() {
            return value;
        }        
        
        @Override
        public String toString() {
            return value;
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final String TILESTRING = "tile:";        
    private Image itemImg;
    private Tile tile;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor pre spravnu externalizaciu.
     */
    public TileItem() {
        
    }
    
    /**
     * Konstruktor ktory vytvori dlazdicovy predmet s menom zadanym v parametre <b>name</b>.
     * Dlazdicu si ziska z parametru <b>tile</b>. Pri vytvoreni nastavujeme zakladne vlastnosti 
     * predmetu.
     * @param name Meno dlazdicoveho predmetu
     * @param tile Dlazdica z ktorej vytvarame predmet
     */
    public TileItem(String name, AttackedTile tile) {
        this.tile = tile.getOriginTile();
        this.name = name == null ? tile.getOriginTile().getName() : name;         
        this.placeable = true;
        this.res = EntityResource.getResource(Integer.toString(tile.getId()));
        this.itemType = ItemType.TILE;
        this.itemType.setSubType(TileType.TILE);
        if (res == null) {
            this.id = Integer.toString(tile.getId());
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemImg = this.tile.getImage(0);
        } else {
            this.id = res.getId();
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemSprites = res.getEntitySprites().get(spriteType);
        } 
        
        //System.out.println("Konstruktor");
    }
            
    /**
     * Konstruktor ktory vytvori dlazdicovy predmet s menom zadanym v parametre <b>name</b>.
     * Dlazdicu si ziska z parametru <b>res</b>. Pri vytvoreni nastavujeme zakladne vlastnosti 
     * predmetu z tohoto resource najprv volanim super konstruktoru, potom doplnenim
     * dalsich premennych ako je dlazdica ktora reprezentuje tento predmet.
     * @param name Meno dlazdicoveho predmetu
     * @param res Resource pre dlazdicu.
     */
    public TileItem(String name, EntityResource res) {
        super(name, res);  
        this.itemType = ItemType.TILE;
        this.itemType.setSubType(TileType.TILE);
        this.tile = Tile.getTile(id);
    }
    
    /**
     * Konstruktor ktory vytvori dlazdicovy predmet s menom zadanym v parametre <b>name</b>.
     * Dlazdicu si ziska z parametru <b>tile</b>. Pri vytvoreni nastavujeme zakladne vlastnosti 
     * predmetu z tohoto resource ktory ziskame podla id dlazdice. Ked je resource nulovy
     * tak prenechavame vytvorenie predmetu z TileResource inak vytvarame predmet rovnako
     * ako v triede Item.     
     * @param name Meno dlazdicoveho predmetu
     * @param tile Dlazdica ktora reprezentuje predmet.
     */
    public TileItem(String name, Tile tile) {
        this.tile = tile;
        this.name = name == null ? tile.getName() : name;
        this.placeable = true;
        this.res = EntityResource.getResource(Integer.toString(tile.getId()));
        this.itemType = ItemType.TILE;
        this.itemType.setSubType(TileType.TILE);
        if (res == null) {
            this.id = Integer.toString(tile.getId());
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemImg = tile.getImage(0);
        } else {
            this.id = res.getId();
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemImg = res.getEntitySprites().get(spriteType).get(0).getSprite();
        } 
        
        //System.out.println("Konstruktor");
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora inicializuje predmet podla udajov z resource. Volana je super
     * metoda initialize. Nakonci nastavujeme TileType tohoto predmetu a urcime co sa da s 
     * predmetom robit
     */
    @Override
    public void initialize() {
        super.initialize();
        this.itemType = ItemType.TILE;
        this.itemType.setSubType(TileType.TILE);  
        this.placeable = true;      
    }
    
    /**
     * Metoda ktora reinicializuje predmet po nacitani z disku. Volame super metodu 
     * ktora reinicializuje vsetky informacie o predmete. Jedine co nam zostane v tomto useku
     * je nastavit dlazdicu a obrazok ktory sa bude zobrazovat
     */
    @Override
    public void reinitialize() {
        super.reinitialize();
        if (res == null) {      
            this.tile = Tile.getTile(id);
            this.itemImg = this.tile.getImage(0);
        } else {                     
            this.itemImg = itemSprites.get(0).getSprite();
        }         
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati dlazdicu ktoru dostat z tohoto predmetu.
     * @return Dlazdicu z predmetu
     */
    public Tile getTile() {
        return tile;
    }
    
    /**
     * Metoda ktora vrati obrazok pre predmet. Ked je predmet vytvoreny z resource
     * tak volame super metodu. Inak vratime obrazok priamo.
     * @return Obrazok pre dlazdicovy predmet
     */
    @Override
    public Image getTypeImage() {
        if (res != null) {
            return super.getTypeImage();
        }
        return itemImg;        
    }
    
    /**
     * Metoda ktora vrati id dlazdice. Rozdiel oproti getId je ten ze getId vrati
     * id z resource, toto vrati id z dlazdice ktoru sme definovali v konstruktore
     * alebo v metodach ktore nastavili dlazdicu dodatocne
     * @return Id-cko dlazdice z predmetu
     */
    public int getTileId() {
        return tile.getId();
    }           
    
    // </editor-fold>
    
}
