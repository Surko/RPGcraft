/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.errors.CorruptedFile;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.SpriteSheet;
import rpgcraft.manager.PathManager;
import rpgcraft.map.tiles.Tile;
import rpgcraft.xml.TilesXML;

/**
 * TileResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit tile resources z xml suborov, ktore sa daju pouzit pri vkladani na mapy ako dlazdice (tiles).
 * Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public class TileResource extends AbstractResource<TileResource> {
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/enumy ">
    /**
     * Odolnost dlazdice. Toto su len definovane nazvy s priradenymi cislami ktore sa daju pouzit v xmlku.
     * Odolnosti sa daju zapisovat aj priamo cislami     
     */
    public enum Durability {
        HAND(1),
        WOODEN(2),
        STONE(4),
        IRON(8),
        GOLD(16),
        ORCISH(32),
        ADAMANTITE(64),
        DIAMOND(128),
        DARKDIAMOND(256),
        PLASMA(512),
        MATTER(1024);
           
        private int value;

        private Durability(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
       
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static Sprite _sprite = null;
    private static Sprite.Type _type = null;
    private static int _globalW = 0, _globalH = 0;
    private static int iSprite = 0;
    
    private boolean swimable;
    private boolean destroyable;
    private ItemLevelType materialType;
    
    private HashMap<Integer,Sprite> _sprites;
    private Integer id;
    private String name;       
    private SpriteSheet sheet;
    private int damage;
    private int health;
    private int tileStrength, itemType;
    
    private static HashMap<Integer, TileResource> tileResources = new HashMap<>();
    private static final boolean selfDefining = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    
    /**
     * Konstruktor (privatny) ktory vytvori instanciu TileResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme dlazdicovy resource a vlozime ho k ostatnym
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private TileResource(Element elem) {
        _sprites = new HashMap();
        parse(elem);
        validate();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati TileResource podla parametru <b>id</b>.
     * @param id Id tile resource
     * @return TileResource s danym menom
     */
    public static TileResource getResource(Integer id) {
        return tileResources.get(id);
    }
    
    /**
     * Metoda ktora vrati vsetky definovane dlazdicove resources.     
     * @return Vsetky TileResource
     */
    public static HashMap<Integer, TileResource> getResources() {
        return tileResources;       
    }
    
    /**
     * Metoda ktora vrati TileResource podla parametru <b>id</b>.
     * @param id Meno TileResource ktore hladame
     * @return TileResource s danym menom
     */
    public static TileResource getResource(String id) {
        return tileResources.get(Integer.parseInt(id));
    }
    
    /**
     * Metoda ktora vytvori novy objekt typu TileResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu TileResource.
     * @param elem Element z ktoreho vytvarame dlazdicove resource
     * @return TileResource z daneeho elementu
     */
    public static TileResource newBundledResource(Element elem) {
        return new TileResource(elem);                
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (TileXML.ANIM -> z atributov ziskame info co za animaciu nacitavame. Nasledne volame
     * rekurzivne metodu parse na ziskanie samostatneho obrazku.
     * Pri navrate pridame vytvoreny sprite do spritov.
     * @param elem Element z xml ktory rozparsovavame do TileResource
     */
    @Override
    protected void parse(Element elem) {
        
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {
                case TilesXML.ANIM : {
                    String sType = ((Element)eNode).getAttribute(TilesXML.TYPE);
                    
                    Sprite.Type type = Sprite.Type.TILE; 
                    try {
                        type = Sprite.Type.valueOf(sType);                        
                    } catch (Exception e) {
                        
                    }
                    
                    _sprite = new Sprite(type);
                    //System.out.println("Loading sprite" + ++iSprite );

                    // Globalne nastavene vysky a sirky sprite 

                    parse((Element)eNode);
                    _sprite.setImagefromSheet(sheet, _globalW, _globalH);
                    _sprites.put(type.getValue(), _sprite);
                    //System.out.println("Done Loading");                                        
                } break;
                case TilesXML.NAME : {
                    name = eNode.getTextContent();
                } break;
                case TilesXML.HEALTH : {
                    health = Integer.parseInt(eNode.getTextContent());
                } break;
                case TilesXML.DAMAGE : {
                    damage = Integer.parseInt(eNode.getTextContent());
                } break;
                case TilesXML.DURABILITY : {
                    try {
                        tileStrength = Durability.valueOf(eNode.getTextContent()).getValue();  
                        break;
                    } catch (Exception e) {
                        
                    }
                    try {
                        tileStrength = Integer.parseInt(eNode.getTextContent());
                        break;
                    } catch (DOMException | NumberFormatException e) {
                        
                    }
                } break;
                case TilesXML.ITEMTYPE : {                    
                    try {
                        itemType = Integer.parseInt(eNode.getTextContent());
                        break;
                    } catch (DOMException | NumberFormatException e) {
                        
                    }
                } break;
                case TilesXML.SHEET : {
                try {
                    String sheetName = eNode.getTextContent();
                    sheet = SpriteSheet.getSheet(sheetName);
                    if (sheet == null) {
                        sheet = new SpriteSheet(ImageIO.read(new File(PathManager.getInstance().getSheetPath(),
                                sheetName)));
                        SpriteSheet.putSheet(sheetName, sheet);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
                }
                } break;    
                case TilesXML.ID : {
                    //System.out.println(eNode.getTextContent());
                    id = Integer.parseInt(eNode.getTextContent());                    
                } break;
                case TilesXML.DURATION : {
                    //System.out.println(eNode.getTextContent());
                    _sprite.setDuration(Integer.parseInt(eNode.getTextContent()));
                } break;
                case TilesXML.X : {                    
                    //System.out.println(eNode.getTextContent());
                    _sprite.setSpriteX(Integer.parseInt(eNode.getTextContent()));
                } break;
                case TilesXML.Y : {
                    //System.out.println(eNode.getTextContent());
                    _sprite.setSpriteY(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case TilesXML.W : {                    
                    _sprite.setSpriteW(Integer.parseInt(eNode.getTextContent()));
                } break;
                case TilesXML.H : {
                    _sprite.setSpriteH(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case TilesXML.GLOBALW : {
                    //System.out.println(eNode.getTextContent());
                    _globalW = Integer.parseInt(eNode.getTextContent());
                } break;
                case TilesXML.GLOBALH : {
                    //System.out.println(eNode.getTextContent());
                    _globalH = Integer.parseInt(eNode.getTextContent());
                } break;
                default : break;
            }
        }
    }                
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati mapu s obrazkami pre dlazdice podla meta dat. Kluc v tejto mape
     * je meta data.
     * @return Mapa so vsetkymi obrazkami pre dlazdice.
     */
    public HashMap<Integer, Sprite> getTileSprites() {
        return _sprites;
    }        
    
    /**
     * Metoda ktora vrati meno pre dlazdicu
     * @return Meno dlazdice
     */
    public String getName() {
        return name;
    }
    
    /**
     * Metoda ktora vrati zivot dlazdice.
     * @return Zivot dlazdice
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Metoda ktora vrati damage ktory dokaze ustedrit dlazdica
     * @return Damage dlazdice
     */
    public int getDamage() {
        return damage;
    }
    
    /**
     * Metoda ktora vrati silu/odolnost dlazdice. Na znicenie dlazdice musi byt entita
     * minimalne takej sily ako ma dlazdica odolnost.
     * @return Sila dlazdice
     */
    public int getTileStrength() {
        return tileStrength;
    }
    
    /**
     * Metoda ktora vrati predmet ktory je nutno pouzit na rozbitie dlazdice. Na znicenie dlazdice musi byt entita
     * minimalne takej sily ako ma dlazdica odolnost. Predmety su definovane v triede Weapon.
     * Pri kazdom druhe vo WeaponType je definovane cislo ktore predstavuje predmet. V resource je zadane dalsie cislo
     * ktore ked pri bitovej operacii AND vytvori cislo vacsie ako 0 tak je dlazdicu mozne rozbit. <br>
     * <p>
     * napr. WeaponType.ONEHAXE = 4 = 100,WeaponType.TWOHAXE = 8 = 1000 <br>
     * v resource mame zadany item type 12 = 1100 <br>
     * binarna operacia s ONEHAXE aj s TWOHAXE je vacsia ako nula => dlazdicu je mozne rozbit
     * </p>
     * 
     * @return Sila dlazdice
     */
    public int getItemType() {
        return itemType;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    /**
     * Metoda ktora zvaliduje entitu podla danych konceptov.
     */
    private void validate() {
        if (selfDefining) {
            id = firstMatch();
            tileResources.put(id, this);
        } else {
            if (tileResources.containsKey(id)) {
                new CorruptedFile(new RuntimeException(), Color.red, "Xml file has duplicate tile Id "+id).render();
            } else {
                tileResources.put(id, this);
            }
        }
        
        if (tileStrength ==0) {
            tileStrength = 1;  
        }
        if (itemType == 0) {
            itemType = 1;
        }
    }
                            
    /**
     * Vracia prvu volnu hodnotu id ktoru najde medzi rozvrhnutymi idckami.
     * @return Prve volne ID (type:STRING).
     */
    private int firstMatch() {        
        Set<Integer> ids = tileResources.keySet();
        Iterator<Integer> elemIter = ids.iterator();
        int first = elemIter.next() + 1;
        while (tileResources.containsKey(first)) {
            first = elemIter.next() + 1;
        }        
        return first;            
    }  
    
    /**
     * Metoda ktora skopiruje jeden TileResource zadany v parametri <b>res</b>
     * do novo vytvoreneho resource.
     * @param res Resource z ktoreho kopirujeme do nasho resource udaje.
     * @throws Exception Vynimka pri nepodareni kopirovania.
     */
    @Override
    protected void copy(TileResource res) {
        
    }
    
    // </editor-fold>
}
