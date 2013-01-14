/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.inmenu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;

/**
 *
 * @author doma
 */
public class ItemMenu extends AbstractInMenu {
    
    private boolean activated;
    private Item item;
    private int selection;
    
    private int width = 50;
    private int height = 200;
    
    private ArrayList<String> choices;
    
    public ItemMenu() {
        super(null, null);
    }
    
    public ItemMenu(Entity entity, InputHandle input, Item item, int xPos, int yPos) {
        super(entity, input);
        this.item = item;
        this.activated = false;        
        this.xPos = xPos;
        this.yPos = yPos;
        
        setItemMenu();
        this.height = choices.size() * 20 + 10;
        if (height != 0) {
            this.toDraw = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
            setGraphics();
        }
    }
    
    private void setItemMenu() {
        choices = new ArrayList<>();
        
        if (item.isUsable()) choices.add("Use");
        if (item.isEquipped()) {
            choices.add("Unequip");
        } else {
            if (item.isEquipable()) {
                choices.add("Equip");
            }
        }
        
        if (item.isActive()) {
            choices.add("Make Unactive");
        } else {
            if (item.isActivable()) {
                choices.add("Make Active");
            }
        }
        if (item.isDropable()) choices.add("Drop");
        
    }
    
    @Override
    protected void setGraphics() {
        Graphics g = toDraw.getGraphics();
        g.setColor(Colors.getColor(Colors.invOnTopColor));
        g.fillRoundRect(0, 0, width, height, 5, 5);
        g.setColor(Color.BLACK);  
                
        for (int i = 0; i < choices.size(); i++) {
                    
            g.drawString(choices.get(i), 0, (i + 1) * 20);
        }
    }
    
    @Override
    public void paintMenu(Graphics g) {
        g.drawImage(toDraw, xPos, yPos, null);
        
    }
    
    @Override
    public void update() {
        
    }
    
    @Override
    public void inputHandling() {
        if (input.escape.click) {
            input.freeKeys();
            deactivate();
            return;
        }
        
        if (input.up.click) {      
            if (selection > 0)
                selection--;
            input.up.click = false;
        }
        
        if (input.down.click) {      
            if (selection < choices.size() - 1)
                selection++;
            input.down.click = false;
        }
        
        if (input.attack.click) {
            if (choices.size() > 0)
                use();
            input.freeKeys();
            deactivate();                                    
        }
    }
    
    
    private void use() {
        switch (choices.get(selection)) {
            case "Use" : entity.use(item);
                return;
            case "Equip" : entity.equip(item);
                return;
            case "Make Active" : {                
                entity.setActiveItem(item);
            } 
                return;
            case "Unequip" : entity.equip(entity);
                return;
            case "Make Unactive" : entity.setActiveItem(entity);
                return;
        }
    }
    
    @Override
    protected void activate() {
        this.activated = true;
    }
    
    @Override
    protected boolean isActivated() {
        return activated;
    }
 
    protected void deactivate() {
        this.activated = false;
    }
    
}
