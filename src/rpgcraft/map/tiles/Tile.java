/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import rpgcraft.entities.Entity;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.SpriteSheet;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.TileResource;

/**
 *
 * @author Kirrie
 */
public class Tile {    
    
    public static HashMap<Integer,Tile> tiles;
    private static final String IMPASS = "_cross";
       
    private Image upperImage;
    
    protected boolean swimable;
    protected boolean destroyable;
    
    protected HashMap<Integer,Sprite> _sprites;
    protected Integer id;
    protected String name;       
    protected SpriteSheet sheet;
    protected int damage;
    protected int health;
    protected int tileStrength;        
    
    protected int sprNum;
    protected long waypointTime;
    protected long currentTime;
    protected long maxTime;
    
    // PUBLIC METHODS
 
    public Tile(Integer id, TileResource res) {
        this.id = id;
        this.upperImage = ImageResource.getResource(IMPASS).getBackImage();
        if (res != null) {
            this.tileStrength = res.getTileStrength();
            this._sprites = res.getTileSprites();            
            this.tileStrength = res.getTileStrength();
            this.health = res.getHealth();
            this.damage = res.getDamage();    
            this.name = res.getName();
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
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
    
    public int getHealth() {
        return health;
    }
    
    public int getDamage() {
        return damage;                       
    }
    
    public int getTileStrength() {
        return tileStrength;
    }
    
    public boolean isDestroyable() {
        return destroyable;
    }
    
    
    public Image getImage(int meta) {        
        return _sprites == null ? null : 
                (_sprites.containsKey(meta) ? _sprites.get(meta).getSprite() : _sprites.get(0).getSprite());
    }
    
    
    public Image getUpperImage() {
        return upperImage;
    }

    public void moveInto(Entity e) {
        
    }    
    
    public boolean equalsTo(Tile tile) {        
        return this.id == tile.id ? true : false;
    }
    
    public static void initializeTiles() {        
        tiles = DefaultTiles.getInstance().createDefaultTiles();
        tiles.putAll(LoadedTiles.getInstance().createLoadedTiles(tiles));
    
    }
    
    
}
