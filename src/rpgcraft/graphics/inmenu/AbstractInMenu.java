/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.inmenu;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;
import rpgcraft.GamePane;
import rpgcraft.entities.Entity;
import rpgcraft.handlers.InputHandle;
import rpgcraft.map.SaveMap;

/**
 *
 * @author doma
 */
public abstract class AbstractInMenu implements Menu<AbstractInMenu> {
    
    public static HashMap<String, AbstractInMenu> menuList = new HashMap<>();
    
    protected Entity entity;
    protected InputHandle input;
    protected GamePane game;
    
    protected Image toDraw;
    protected AbstractInMenu subMenu;
    
    protected int xPos;
    protected int yPos;    
    
    protected SaveMap map;
    
    public AbstractInMenu(Entity entity, InputHandle input) {
        this.entity = entity;
        this.input = input;
    }
        
    @Override
    public void setMenu(AbstractInMenu menu) {
        
    }
    
    protected abstract void setGraphics();         
    
    @Override
    public abstract void update();
    
    @Override
    public abstract void paintMenu(Graphics g);
    
    @Override
    public abstract void inputHandling();
    
    protected abstract boolean isActivated();
    
    protected abstract void activate();
    
    public void setPositions(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }
    
}
