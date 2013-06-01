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
 *
 * @author kirrie
 */
public class TileItem extends Item {
    
    private static final String TILESTRING = "tile:";    
    
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
    
    private Image itemImg;
    private Tile tile;
    
    public TileItem() {
        
    }
    
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
            this.itemImg = res.getEntitySprites().get(spriteType).get(0).getSprite();
        } 
        
        //System.out.println("Konstruktor");
    }
            
    public TileItem(String name, EntityResource res) {
        this.name = name;
        this.res = res;
        this.itemType = ItemType.TILE;
        this.itemType.setSubType(TileType.TILE);        
    }
    
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
    
    @Override
    public void reinitialize() {
        this.res = EntityResource.getResource(id);
        if (res == null) {      
            this.tile = Tile.getTile(id);
            this.itemImg = this.tile.getImage(0);
        } else {                     
            this.itemImg = res.getEntitySprites().get(spriteType).get(0).getSprite();
        }         
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
