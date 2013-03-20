/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.inmenu;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.map.SaveMap;

/**
 *
 * @author doma
 */
public class InventoryMenu extends AbstractInMenu {
    
    private int selection = 0;
    private int selectPosX;
    private int selectPosY;
    
    private static final int itemsToShow = 5;
    
    private Image itemImage;
    private boolean changedState;
    
    /**
     * Konstruktor menu pre inventar s dvoma parametrami entity a input, ktora urcuje 
     * pre ktoru entitu sa bude vykreslovat inventar a ako bude reagovat inventar
     * na vstup z klavesnice od uzivatela.
     * @param entity Entita pre ktoru sa vykresluje inventar
     * @param input Obsluha tlacidiel na klavesnici.
     */
    public InventoryMenu(Entity entity, InputHandle input, SaveMap map) {
        super(entity, input);
        setGraphics();
        this.map = map;
        this.xPos = 550;
        this.yPos = 5;
        this.subMenu = new ItemMenu();
        this.changedState = true;
        menuList.put("inventory", this);
    }
    
    /**
     * Metoda ktora ma zaulohu vytvori graficke rozhranie pre inventar.
     */
    @Override
    public void setGraphics() {
        toDraw = new BufferedImage(200, 400, BufferedImage.TRANSLUCENT);        
        Graphics g = toDraw.getGraphics();
        g.setColor(Colors.getColor(Colors.invBackColor));        
        g.fillRoundRect(0, 0, 195, 395, 5, 10);
        g.setColor(Colors.getColor(Colors.invOnTopColor)); 
        g.fillRoundRect(5, 5, 195, 395, 5, 10);
    }
    
    /**
     * Metoda paint vykresli do grafickeho kontextu g nami pripraveny
     * inventar z metody setGraphics volanej pri vytvarani instancie tohoto objektu 
     * a predmety vnutri inventara entity.     
     * @param g Graficky kontext do ktoreho sa kresli
     */
    @Override
    public void paintMenu(Graphics g) {
        g.drawImage(toDraw, xPos, yPos, null);
                
        g.drawImage(itemImage, xPos + 20 ,yPos + 20, game);
        
        if (subMenu.isActivated()) {
            subMenu.paintMenu(g);
        }
        
    }
    
    public void changeProperties(Entity entity, InputHandle input) {
        this.entity = entity;
        this.input = input;
    }
    
    /**
     * Metoda ktora aktualizuje pozicie vykreslovaneho inventara.
     * @param xPos Pozicia x inventara na obrazovke
     * @param yPos Pozicia y inventara na obrazovke
     */
    @Override
    public void update() { 
        if (!subMenu.isActivated()) {
            if (changedState) {            
                
                if (updateImage()) {
                    changedState = false;
                }
            }
        } else {
            subMenu.update();
        }
    }
    
    /**
     * 
     * @return True/False ci sa podarilo aktualizovat obrazok s predmetmi
     */
    private boolean updateImage() {
        itemImage = new BufferedImage(160, 360, BufferedImage.TRANSLUCENT);
        Graphics g = itemImage.getGraphics();
                
        ArrayList<Item> inventory = entity.getInventory();
        
        if (!inventory.isEmpty()) {
            ArrayList<Item> invenToShow ; 
            int localSelect = 0;
            
            if (inventory.size() > itemsToShow) {
                // sItem = kolko predmetov je za oznacenym predmetom.
                int sItem = inventory.size() - selection - 1;          
                
                invenToShow = new ArrayList<>();                       
                                
                if (sItem < itemsToShow) { 
                    localSelect = itemsToShow - sItem - 1;
                    int diff = inventory.size() - itemsToShow;
                    for (int i = diff < 0 ? 0 : diff; i < inventory.size(); i++) {
                        invenToShow.add(inventory.get(i));
                        }
                } else {
                    for (int i = selection; i < selection + itemsToShow; i++) {
                       invenToShow.add(inventory.get(i)); 
                    }
                }
            } else {
                invenToShow = inventory;
                localSelect = selection;
            }
            
            for (int i = 0; i < invenToShow.size(); i++) {
                g.drawString(invenToShow.get(i).getName(), 0, (i+1) * 40);                                  
            }
            g.setColor(Colors.getColor(Colors.invOnTopColor));
            selectPosY = localSelect * 40 + yPos + 40;
            selectPosX = xPos + 120;
            g.fillRoundRect(0, localSelect * 40, 160, 40, 5, 5);
        } else {
            g.drawString("No Items", 0, 40);
        }
        return true;
    }
    
    private Item selectedItem() {        
        return entity.getInventory().get(selection);
    }
    
    @Override
    public void inputHandling() {
        if (subMenu.isActivated()) {
            subMenu.inputHandling();
            return;
        } 
                            
        if (input.clickedKeys.contains(input.inventory.getKeyCode())) {
            map.setMenu(null);
            input.freeKeys();
        }
        
        if (input.clickedKeys.contains(input.up.getKeyCode())) {
            if (selection > 0) {
                selection--;
                changedState= true;
            }      
            input.clickedKeys.remove(input.up.getKeyCode());
        }
                
        if (input.clickedKeys.contains(input.down.getKeyCode())) {
            if (selection < entity.getInventory().size() - 1) {
                selection++;
                changedState = true;
            }            
            input.clickedKeys.remove(input.down.getKeyCode());
        }
        
        if (input.clickedKeys.contains(input.attack.getKeyCode())) {
            System.out.println("DONE!!");
            subMenu = new ItemMenu(entity, input, selectedItem(), selectPosX, selectPosY);
            subMenu.activate();
            input.clickedKeys.remove(input.attack.getKeyCode());
        }
        
        
    }

    @Override
    protected boolean isActivated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void activate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
