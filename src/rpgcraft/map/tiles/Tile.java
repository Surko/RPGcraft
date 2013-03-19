/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.awt.Image;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.SpriteSheet;
import rpgcraft.resource.TileResource;

/**
 *
 * @author Kirrie
 */
public class Tile {    
    
    protected boolean swimable;
    protected boolean destroyable;
    protected ItemLevelType materialType;
    
    protected ArrayList<Sprite> _sprites;
    protected Integer id;
    protected String name;       
    protected SpriteSheet sheet;
    protected int damage;
    protected int health;
    protected int tileStrength;        
    
    // PUBLIC METHODS
 
    public Tile(Integer id, TileResource res) {
        this.id = id;
        this._sprites = res.getTileSprites();
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
    
    public boolean isDestroyable() {
        return destroyable;
    }
    
    
    public Image getImage() {
        return _sprites.get(0).getSprite();
    }

    public void moveInto(Entity e) {
        
    }    
    
    public boolean equalsTo(Tile tile) {        
        return this.id == tile.id ? true : false;
    }
    
    
    
}
