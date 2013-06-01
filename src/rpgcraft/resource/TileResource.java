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
 *
 * @author doma
 */
public class TileResource extends AbstractResource<TileResource> {
 
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
    private int tileStrength;
    
    private static HashMap<Integer, TileResource> tileResources = new HashMap<>();
    private static final boolean selfDefining = false;
    
    public static TileResource getResource(Integer id) {
        return tileResources.get(id);
    }
    
    public static HashMap<Integer, TileResource> getResources() {
        return tileResources;       
    }
    
    public static TileResource getResource(String id) {
        return tileResources.get(Integer.parseInt(id));
    }
    
    private TileResource(Element elem) {
        _sprites = new HashMap();
        parse(elem);
        validate();
    }
    
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
        
        this.tileStrength = 1;
        
    }
    
    public static TileResource newBundledResource(Element elem) {
        return new TileResource(elem);                
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
                    System.out.println("Loading sprite" + ++iSprite );

                    // Globalne nastavene vysky a sirky sprite 

                    parse((Element)eNode);
                    _sprite.setImagefromSheet(sheet, _globalW, _globalH);
                    _sprites.put(type.getValue(), _sprite);
                    System.out.println("Done Loading");
                    
                    
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
                    System.out.println(eNode.getTextContent());
                    id = Integer.parseInt(eNode.getTextContent());                    
                } break;
                case TilesXML.DURATION : {
                    System.out.println(eNode.getTextContent());
                    _sprite.setDuration(Integer.parseInt(eNode.getTextContent()));
                } break;
                case TilesXML.X : {                    
                    System.out.println(eNode.getTextContent());
                    _sprite.setSpriteX(Integer.parseInt(eNode.getTextContent()));
                } break;
                case TilesXML.Y : {
                    System.out.println(eNode.getTextContent());
                    _sprite.setSpriteY(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case TilesXML.W : {                    
                    _sprite.setSpriteW(Integer.parseInt(eNode.getTextContent()));
                } break;
                case TilesXML.H : {
                    _sprite.setSpriteH(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case TilesXML.GLOBALW : {
                    System.out.println(eNode.getTextContent());
                    _globalW = Integer.parseInt(eNode.getTextContent());
                } break;
                case TilesXML.GLOBALH : {
                    System.out.println(eNode.getTextContent());
                    _globalH = Integer.parseInt(eNode.getTextContent());
                } break;
                default : break;
            }
        }
    }        
    
    @Override
    protected void copy(TileResource res) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public HashMap<Integer, Sprite> getTileSprites() {
        return _sprites;
    }        
    
    public String getName() {
        return name;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public int getTileStrength() {
        return tileStrength;
    }

    
    
}
