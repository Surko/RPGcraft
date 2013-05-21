/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.resource.StringResource;

/**
 *
 * @author doma
 */
public class ItemMenu extends AbstractInMenu {
    private static final int hItemBox = 20;
    private static final int wGap = 5, hGap = 5;    
       
    private enum Command {
        USE(StringResource.getResource("_use")),
        EQUIP(StringResource.getResource("_equip")),
        UNEQUIP(StringResource.getResource("_unequip")),
        ACTIVE(StringResource.getResource("_active")),
        UNACTIVE(StringResource.getResource("_unactive")),
        DESTROY(StringResource.getResource("_destroy")),
        CLOSE(StringResource.getResource("_close")),
        DROP(StringResource.getResource("_drop"));
        
        private String sCommand;
        
        private Command(String sCommand) {
            this.sCommand = sCommand;
        }
        
        public String getValue() {
            return sCommand;
        }
    }
    
    private Item item;
    private int selection;
    
    private int selectedPosX,selectedPosY;
    
    private int width = 100;
    private int height = 200;
    
    // Moznosti ktore sa daju robit s predmetom
    private ArrayList<Command> choices;
    
    public ItemMenu() {
        super(null, null);
    }
    
    public ItemMenu(AbstractInMenu source, int x, int y) {
        super(source.getEntity(), source.getInput());
        this.sourceMenu = source;
        this.item = entity.getInventory().get(selection);
        this.activated = false;        
        this.xPos = x - width;
        this.yPos = y;
        
        setItemMenu();
        recalculatePositions();
    }        
    
    private void setItemMenu() {
        choices = new ArrayList<>();
        
        if (item.isUsable()) {
            choices.add(Command.USE);
        }
        if (item.isEquipped()) {
            choices.add(Command.UNEQUIP);
        } else {
            if (item.isEquipable()) {
                choices.add(Command.EQUIP);
            }
        }
        
        if (item.isActive()) {
            choices.add(Command.UNACTIVE);
        } else {
            if (item.isActivable()) {
                choices.add(Command.ACTIVE);
            }
        }
        if (item.isDropable()) {
            choices.add(Command.DROP);
        }
        choices.add(Command.DESTROY);
        choices.add(Command.CLOSE);
        
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda vrati sirku menu pre item menu
     * @return Sirka inventara
     */
    @Override
    public int getWidth() {
        return width;
    }
    
    /**
     * Metoda vrati vysku menu pre item menu
     * @return Vyska inventara
     */
    @Override
    public int getHeight() {
        return height;
    }
    
    // </editor-fold>
    
    @Override
    protected void setGraphics() {
        Graphics g = toDraw.getGraphics();
        g.setColor(Colors.getColor(Colors.invBackColor));
        g.fillRoundRect(0, 0, getWidth() - wGap, getHeight() - hGap, wGap, hGap);
        g.setColor(Colors.getColor(Colors.invOnTopColor));
        g.fillRoundRect(wGap, hGap, getWidth(), getHeight(), wGap, hGap);
        g.setColor(Color.BLACK);  
                
        for (int i = 0; i < choices.size(); i++) {                    
            g.drawString(choices.get(i).getValue(), wGap, (i + 1) * hItemBox);
        }
    }
    
    /**
     * Metoda by mala vratit meno tohoto menu, ale kedze itemMenu moze byt len subMenu tak mu nepriradujem ziadne 
     * meno aby som donutil ukazanie tohoto menu len s doprovodom hlavneho menu.
     * @return null
     */
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public void paintMenu(Graphics g) {
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);        
            
            if (selection >= 0) {
                g.setColor(Colors.getColor(Colors.selectedColor));
                g.fillRoundRect(xPos + wGap, selection * hItemBox + yPos + hGap, getWidth() - wGap, hItemBox, wGap, hGap);
            }
            
            if (!activated) {
                g.setColor(Colors.getColor(Colors.selectedColor));
                g.fillRoundRect(xPos, yPos, getWidth(), getHeight(), wGap, hGap);
            }
        }
    }    
    
    @Override
    public void update() {
        if (changedState) {
            selectedPosY = selection * hItemBox + yPos + hItemBox;
            selectedPosX = xPos;            
            changedState = true;
        }
    }
    
    /**
     * Metoda ktora ma spracovavat eventy/udalosti z mysi. ItemMenu take moznosti nepodporuje ale je ich mozne pridat.
     * Na zistenie spravnych suradnic staci odcitat od suradnic v Evente suradnice tohoto menu a modulovat a predelovat vyskou
     * jedneho elementu v tomto menu.
     * @param e MouseEvent z mysi
     * @see MouseEvent
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    @Override
    public void inputHandling() {
        if (input.clickedKeys.contains(input.escape.getKeyCode()) || input.clickedKeys.contains(InputHandle.right.getKeyCode())) {                                    
            if (sourceMenu != null) {
                exit();
                sourceMenu.activate();                
                return;
            }
        }
                
        if (input.clickedKeys.contains(InputHandle.up.getKeyCode())) {      
            if (selection > 0) {
                selection--;
            }
        }
        
        if (input.clickedKeys.contains(InputHandle.down.getKeyCode())) {      
            if (selection < choices.size() - 1) {
                selection++;
            }           
        }
        
        if (input.clickedKeys.contains(InputHandle.enter.getKeyCode())) {
            if (choices.size() > 0) {
                use();
            }
            exit();
            sourceMenu.activate();
        }
    }
    
    @Override
    public void exit() {
        activated = false;
        visible = false;
    }
    
    private void use() {
        switch (choices.get(selection)) {
            case USE : {
                entity.use(item);                                                
                sourceMenu.setState(true);
            } break;
            case EQUIP : entity.equip(item);
                break;
            case ACTIVE : entity.setActiveItem(item);
                break;
            case UNEQUIP : entity.equip(entity);
                break;
            case UNACTIVE : entity.setActiveItem(entity);
                break;
            case CLOSE : {
                exit();            
                if (sourceMenu != null) {
                    sourceMenu.activate();
                }
            } break;
        }
    }        

    @Override
    public void recalculatePositions() {
        this.height = choices.size() * hItemBox + hGap * 2;
        if (height != 0) {
            this.toDraw = new BufferedImage(getWidth(), getHeight(), BufferedImage.TRANSLUCENT);
            setGraphics();
        }
    }
    
}
