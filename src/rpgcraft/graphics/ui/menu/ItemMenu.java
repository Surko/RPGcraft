/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.entities.Item.Command;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.handlers.InputHandle;

/**
 * ItemMenu je trieda ktora sa stara o vytvorenie a zobrazenie menu co sa da
 * s predmetom robit.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu. 
 */
public class ItemMenu extends AbstractInMenu {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final int hItemBox = 20;
    private static final int wGap = 5, hGap = 5;                  
    
    private Item item;
    private int selection;
    
    private int selectedPosX,selectedPosY;
    
    private int width = 100;
    private int height = 200;
    
    // Moznosti ktore sa daju robit s predmetom
    private ArrayList<Command> choices;
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Zakladny konstruktor dolezity pri volani newInstance pri vytvarani kopii
     * objektu.
     */
    public ItemMenu() {
        super(null, null);
    }
    
    /**
     * Konstruktor ktory vytvori instanciu/objekt/menu typu ItemMenu. Menu nastavujeme
     * z ktoreho menu bolo vytvorene a nasledne pre aky predmet sme ho vytvorili. Parametre
     * <b>x,y</b> sluzia ako pozicie kde vykreslujeme menu.
     * @param source Zdrojove AbstractInMenu kde sme toto menu vytvorili
     * @param item Predmet pre ktory sme vytvorili menu
     * @param x X-ova pozicia menu
     * @param y Y-ova pozicia menu
     */
    public ItemMenu(AbstractInMenu source, Item item, int x, int y) {
        super(source.getEntity(), source.getInput());
        this.sourceMenu = source;
        this.item = item;
        this.activated = false;        
        this.xPos = x - width;
        this.yPos = y;
        
        setItemMenu();
        setGraphics();
    }        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora initializuje toto menu s novymi udajmi. 
     * Novymi udajmi je entita zadana parametrom pre ktoru vytvarame menu.
     * Parameter origMenu sluzi ako vzor pre toto menu z ktoreho ziskavame pozadie tohoto menu.
     * @return Initializovany journal.
     */
    @Override
    public ItemMenu initialize(AbstractInMenu origMenu, Entity e1, Entity e2) {             
        this.toDraw = origMenu.getDrawImage();        
        return this;
    }
    // </editor-fold>
    
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
    
    /**
     * Metoda by mala vratit meno tohoto menu, ale kedze itemMenu moze byt len subMenu tak mu nepriradujem ziadne 
     * meno aby som donutil ukazanie tohoto menu len s doprovodom hlavneho menu.
     * @return null
     */
    @Override
    public String getName() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int getWGap() {
        return wGap;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int getHGap() {
        return hGap;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi do listu choices vsetky moznosti co sa da s danym predmetom robit.
     * (ked je mozne predmet pouzit tak bude USABLE, atd...)
     */
    private void setItemMenu() {
        choices = new ArrayList<>();
        
        choices.add(Command.INFO);
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
        if (item.isPlaceable()) {
            choices.add(Command.PLACE);
        }
        choices.add(Command.DESTROY);
        choices.add(Command.CLOSE);
                
        this.height = choices.size() * hItemBox + hGap * 2;
    }
        
    /**
     * Metoda ktora nastavuje obrazok toDraw, ktore byva spolocne pre vsetky menu.
     * Preto zaciname volanim super.setGraphics. Po navrate vykreslime nazvy prikazov
     * co mozme s predmetom robit.
     */
    @Override
    protected final void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();
        g.setColor(Color.BLACK);  
                
        for (int i = 0; i < choices.size(); i++) {                    
            g.drawString(choices.get(i).getValue(), wGap, (i + 1) * hItemBox);
        }
    }      
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    /**
     * Metoda ktora vykresli menu ulozene v obrazku toDraw ked je menu viditelne.
     * Ked mame oznaceny nejaky predmet z tohoto menu tak ho prekreslime oznacenou farbou.
     * @param g Graficky kontext do ktoreho kreslime.
     */
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora aktualizuje toto item menu. Aktualizujeme iba vtedy ked sa zmenil stav.
     * Pri zmene menime pozicie selectedPosY a selectedPosX.     
     */
    @Override
    public void update() {
        if (changedState) {
            selectedPosY = selection * hItemBox + yPos + hItemBox;
            selectedPosX = xPos;            
            changedState = false;
        }
    }
    
    /**
     * Metoda ktora spravne ukonci menu volanim super metody.
     */
    @Override
    public void exit() {
        super.exit();
    }
    
    /**
     * Metoda ktora zavola akciu na predmet pre ktory sme vytvorili menu. 
     * Akcia je dana podla toho co bolo oznacene v tomto menu. Prikazy su vykonavane
     * prechadzanim case vetiev.
     */
    private void use() {
        switch (choices.get(selection)) {
            case INFO : {                
                AbstractInMenu newMenu = new ItemInfo(sourceMenu, item);
                this.sourceMenu.setMenu(newMenu);
                newMenu.setVisible(true);
            } break;
            case USE : {
                entity.use(item);   
                sourceMenu.activate();
                sourceMenu.setState(true);
            } break;
            case EQUIP : {
                entity.equip(item);
                sourceMenu.activate();
                sourceMenu.setState(true);
            } break;
            case ACTIVE : {
                entity.setActiveItem(item);
                sourceMenu.activate();
                sourceMenu.setState(true);
            } break;
            case UNEQUIP : {
                entity.equip(item);
                sourceMenu.activate();
                sourceMenu.setState(true);
            } break;
            case UNACTIVE : {
                entity.setActiveItem(null);
                sourceMenu.activate();
                sourceMenu.setState(true);
            } break;
            case DROP : {
                entity.drop(item);
                sourceMenu.activate();
                sourceMenu.setState(true);
            } break;    
            case PLACE : {
                entity.placeItem(item);
                entity.removeItem(item);  
                sourceMenu.activate();
                sourceMenu.setState(true);                
            } break;
            case CLOSE : {                
                sourceMenu.activate();
            } break;
            case DESTROY : {
                entity.removeItem(item);
                sourceMenu.activate();
                sourceMenu.setState(true);                
            } break;
        }
    }        

    /**
     * Metoda ktora rekalkuluje poziciu pozicie pre toto menu.
     */
    @Override
    public void recalculatePositions() {                
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handler ">
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    /**
     * Metoda ktora spracovava vstup. Pri stlaceni ESC ukoncujeme menu. Pri stlaceni UP alebo
     * DOWN menime oznacenie, ENTER potvrdzuje a vykona akciu nad predmetom.
     */
    @Override
    public void inputHandling() {
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())
                || input.clickedKeys.contains(InputHandle.DefinedKey.RIGHT.getKeyCode())) {                                    
            if (sourceMenu != null) {
                exit();
                sourceMenu.activate();                
                return;
            }
        }
                
        if (input.clickedKeys.contains(InputHandle.DefinedKey.UP.getKeyCode())) {      
            if (selection > 0) {
                selection--;
            }
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.DOWN.getKeyCode())) {      
            if (selection < choices.size() - 1) {
                selection++;
            }           
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ENTER.getKeyCode())) {
            if (choices.size() > 0) {
                use();
            }
            exit();      
        }
    }        
    // </editor-fold>
}
