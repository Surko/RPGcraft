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

/**
 *
 * @author kirrie
 */
public class TileItem extends Item {
    
    private static final String TILESTRING = "tile:";    
    
    public enum TileType implements TypeofItems{
        TILE;

        @Override
        public Object getValue() {
            return null;
        }
    }
    
    private Image itemImg;
    private Tile tile;
    
    public TileItem(String name, AttackedTile tile) {
        this.tile = tile.getOriginTile();
        this.name = name == null ? tile.getOriginTile().getName() : name;         
        this.placeable = true;
        this.res = EntityResource.getResource(TILESTRING + tile.getId());
        if (res == null) {
            this.id = TILESTRING + this.tile.getId();
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemImg = this.tile.getImage(0);
        } else {
            this.id = res.getId();
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemImg = res.getEntitySprites().get(spriteType).get(0).getSprite();
        } 
        
        //System.out.println("Konstruktor");
    }
    
    public TileItem(String name, EntityResource res) {
        this.name = name;
        this.res = res;        
    }
    
    public TileItem(String name, Tile tile) {
        this.tile = tile;
        this.name = name == null ? tile.getName() : name;
        this.placeable = true;
        this.res = EntityResource.getResource(TILESTRING + tile.getId());
        if (res == null) {
            this.id = TILESTRING + tile.getId();
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
    
    public Tile getTile() {
        return tile;
    }
    
    @Override
    public Image getTypeImage() {
        return itemImg;        
    }
    
    public int getTileId() {
        return tile.getId();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
}
