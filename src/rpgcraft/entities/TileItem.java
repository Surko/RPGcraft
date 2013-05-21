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
    
    private Image itemImg;
    
    public TileItem(String name, AttackedTile tile) {
        this.name = name == null ? tile.getOriginTile().getName() : name;  
        this.res = EntityResource.getResource(TILESTRING + tile.getId());
        if (res == null) {
            this.id = TILESTRING + tile.getId();
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemImg = tile.getOriginTile().getImage(0);
        } else {
            this.id = res.getId();
            this.count = 1;
            this.spriteType = Sprite.Type.ITEM;
            this.itemImg = res.getEntitySprites().get(spriteType).get(0).getSprite();
        } 
        
        //System.out.println("Konstruktor");
    }
    
    @Override
    public Image getTypeImage() {
        return itemImg;        
    }
    
}
