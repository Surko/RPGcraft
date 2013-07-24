/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import rpgcraft.plugins.AbstractInMenu;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.TextUtils;

/**
 * InventoryMenu je trieda ktora sa stara o vytvorenie a zobrazenie inventara pre hraca.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu.
 * @author kirrie
 */
public class InventoryMenu extends AbstractInMenu {

    // <editor-fold defaultstate="collapsed" desc=" Konstanty ">
    /**
     * Id inventaru
     */
    public static final String INVENTORYID = "_inventory";
    /**
     * Meno tohoto menu
     */
    private static final String INVENTORY = StringResource.getResource(INVENTORYID);    
    /**
     * Sirka, vyska a odsadenie od okrajov
     */
    private static final int invWidth = 200, invHeight = 400, wGap = 5, hGap = 5;
    /**
     * Vyska a sirka predmetov
     */
    private static final int itemsWidth = 160, itemsHeight = 360;
    /**
     * Vyska jedneho predmetu
     */
    private static final int itemHeight = 40;
    /**
     * Pocet predmetov na zobrazenie
     */
    private static final int itemsToShow = 5;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Pocet prvkov v CraftingMenu
     */
    private int gridx = 2, gridy = 2;
    /**
     * Vyska a sirka inventaru
     */
    private int w = -1, h = -1; 
    /**
     * Oznaceny predmet v menu
     */
    private int selection = 0;
    /**
     * x, y pozicie, kde sa bude vykreslovat ItemMenu.
     */
    private int selectPosX, selectPosY;
    /**
     * Obrazovy kontajner pre itemy na vykreslenie
     */
    private Image itemsBox;
    /**
     * List s predmetmi
     */
    private ArrayList<Item> items;

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    
    /**
     * Prazdny konstruktory pre moznost vytvarania novych instancii metodou newInstance.
     */
    public InventoryMenu() {
        super(null, null);
    }
    
    /**
     * Konstruktor menu pre inventar s dvoma parametrami entity a input, ktora
     * urcuje pre ktoru entitu sa bude vykreslovat inventar a ako bude reagovat
     * inventar na vstup z klavesnice od uzivatela.
     *
     * @param entity Entita pre ktoru sa vykresluje inventar
     * @param input Obsluha tlacidiel na klavesnici.
     */
    public InventoryMenu(Entity entity, InputHandle input, AbstractMenu menu) {
        super(entity, input);
        this.menu = menu;
        this.activated = true;
        this.items = entity.getInventory();
        setGraphics();
        recalculatePositions();
        this.changedState = true;
        menuList.put(INVENTORYID, this);
    }
    
    /**
     * Metoda ktora initializuje toto menu s novymi udajmi. 
     * Novymi udajmi je entita zadana parametrom pre ktoru vytvarame menu.
     * Parameter origMenu sluzi ako vzor pre toto menu z ktoreho ziskavame pozadie tohoto menu.
     * @return Initializovany inventar.
     */
    @Override
    public InventoryMenu initialize(AbstractInMenu origMenu, Entity e1, Entity e2) {
        this.entity = e1;
        this.input = origMenu.getInput();
        this.menu = origMenu.getMenu();
        this.items = e1.getInventory();
        this.changedState = true;        
        this.toDraw = origMenu.getDrawImage();        
        return this;
    }

    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    public static InventoryMenu getInventoryMenu() {
        return (InventoryMenu)menuList.get(INVENTORYID);       
    }
    
    /**
     * <i>{@inheritDoc }</i>
     * @return {@inheritDoc }
     */
    @Override
    public String getName() {
        return INVENTORYID;
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
    
    /**
     * Metoda vrati sirku menu pre inventar
     * @return Sirka inventara
     */
    @Override
    public int getWidth() {
        if (w <= 0) {
            return invWidth;
        }
        return w;
    }
    
    /**
     * Metoda vrati vysku menu pre inventar
     * @return Vyska inventara
     */
    @Override
    public int getHeight() {
        if (h <= 0) {
            return invHeight;
        }
        return h;
    }
    
    /**
     * Metoda ktora vrati oznaceny predmet v inventari.
     * @return Predmet ktory je v inventary oznaceny.
     */
    private Item getSelectedItem() {
        return items.get(selection);
    }

    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * {@inheritDoc}
     * 
     * Metoda ktora ma zaulohu vytvori graficke rozhranie pre inventar. Navrchu
     * inventara je vypisany string ze sa jedna o inventar.
     */
    @Override
    public final void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();
        g.setColor(Color.BLACK);
        int[] txtSize = TextUtils.getTextSize(TextUtils.DEFAULT_FONT, INVENTORY);
        g.drawString(INVENTORY, (getWidth() - txtSize[0])/2, txtSize[1] + hGap);
        
    }         
    
    /**
     * Nastavi pocet prvkov  v CraftingMenu. Podla tychto hodnot sa pri stlaceni crafting kluca 
     * nastavi craftingMenu 
     * @param gridx Pocet prvkov po sirke
     * @param gridy Pocet prvkov po vyske
     */
    public void setCraftingBox(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
    }           
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Update metody">
    /**
     * Metoda ktora aktualizuje pozicie vykreslovaneho inventara.     
     */
    @Override
    public void update() {
        if (subMenu != null && subMenu.isVisible()) {
            subMenu.update();
        }
        
        if (changedState) {
           if (updateImage()) {
               changedState = false;
           }
        }
        
    }

    /**
     * Metoda ktora aktualizuje obrazok s predmetmi v inventari. Volana je iba vtedy ked poziadame o prekreslenie
     * takym sposobom ze nastavime premennu changedState na true. Update metoda nasledne zavola tuto metodu.<br>
     * Metoda vytvori box velkosti itemsWidth x itemsHeight. Potom postupne po riadkoch vykresluje predmety
     * ziskane z entity zadanej v konstruktore tohoto menu. Do premennej invenToShow priradi predmety ktorych pocet je
     * rovny alebo mensi premennej <b>itemsToShow</b>. Nasledne prechadzame predmety na zobrazenie a vykreslujeme ich do nasho
     * boxu.<br>
     * Nakoniec nam zostava uz len vykreslenie obdlzniku ktory reprezentuje oznaceny predmet. Suradnice tohoto
     * obdlzniku zistime podla premennej <b>localSelect</b>. <br>
     * Pri neexistencii predmetov vypise text "No Items".
     * @return True/False ci sa podarilo aktualizovat obrazok s predmetmi
     */
    private boolean updateImage() {
        itemsBox = new BufferedImage(itemsWidth, itemsHeight, BufferedImage.TRANSLUCENT);
        Graphics g = itemsBox.getGraphics();       

        if (!items.isEmpty()) {
            if (selection >= items.size()) {
                selection = items.size() - 1;
            }
            ArrayList<Item> invenToShow;
            int localSelect = 0;
                
            if (items.size() > itemsToShow) {
                // sItem = kolko predmetov je za oznacenym predmetom.
                int sItem = items.size() - selection - 1;
                
                invenToShow = new ArrayList<>();

                if (sItem < itemsToShow) {
                    localSelect = itemsToShow - sItem - 1;
                    int diff = items.size() - itemsToShow;
                    for (int i = diff < 0 ? 0 : diff; i < items.size(); i++) {
                        invenToShow.add(items.get(i));
                    }
                } else {
                    for (int i = selection; i < selection + itemsToShow; i++) {
                        invenToShow.add(items.get(i));
                    }
                }
            } else {
                invenToShow = items;
                localSelect = selection;
            }

            for (int i = 0; i < invenToShow.size(); i++) {
                Item item = invenToShow.get(i);
                Image itemImg = item.getTypeImage();
                if (itemImg != null) {
                    g.drawImage(itemImg, 0, i * itemHeight, null);
                    g.drawString(item.toString(), itemImg.getWidth(null) + wGap, (i + 1) * itemHeight - itemHeight / 2);
                } else {
                    g.drawString(item.toString(), 0, (i + 1) * itemHeight - itemHeight / 2);
                }

            }

            g.setColor(Colors.getColor(Colors.selectedColor));
            selectPosY = localSelect * itemHeight + yPos + itemHeight;
            selectPosX = xPos;
            g.fillRoundRect(0, localSelect * itemHeight, itemsWidth, itemHeight, wGap, hGap);
        } else {
            g.drawString("No Items", 0, itemHeight);
        }
        return true;
    }
    
    /**
     * Metoda ktora zmeni vlastnosti inventaru podla novo zadanych parametrov.
     * @param entity Entita pre ktoru tvorime inventar
     * @param input Vstup ktory pouzivame
     */
    public void changeProperties(Entity entity, InputHandle input) {
        this.entity = entity;
        this.input = input;
    }
    
    /**
     * Metoda ktora prepocita pozicie menu v paneli s hrou. Inventar sa vzdy vyskytuje uplne vpravo.
     */
    @Override
    public final void recalculatePositions() {
        this.xPos = menu.getWidth() - getWidth() - wGap;
        this.yPos = menu.getHeight() - getHeight() - hGap;
        if (subMenu != null) {            
            subMenu.recalculatePositions();
        }
    }
    
    /**
     * <i>{@inheritDoc}</i> <br>
     * Overrida metoda ktora ma za ulohu spravne ukoncit menu.
     * 
     */
    @Override
    public void exit() {         
        if (subMenu != null) { 
            subMenu.exit();
        }
        subMenu = null;
        activated = false;
        visible = false;
        menu.setInMenu(null);  
        input.freeKeys();
    }        
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Input/Mouse Handling " >
    
    /**
     * Metoda ktora spracovava vstup ked je submenu typu craftinmenu.
     * Hlavna uloha je priradit do jednotlivych boxov v craftingMenu oznacene predmety.
     * Priradenie do boxu zabezpecuje spracovavanie vstupu. Cisla v tomto pripade znamenaju policka v craftingMenu
     * kam sa oznaceny predmet prida.
     */
    public void processCraftingKeys() {
        CraftingMenu cftMenu = (CraftingMenu)subMenu;
        
        Item item = entity.getItem(selection);
        
        boolean removed = false;
        
        if (item != null && item.getCount() > 0) {
            if (input.clickedKeys.contains(KeyEvent.VK_1)) { 
                removed = cftMenu.addItemToCell(item, 0) ? true : false;                
            }
            if (input.clickedKeys.contains(KeyEvent.VK_2)) {
                removed = cftMenu.addItemToCell(item, 1) ? true : false;
            }
            if (input.clickedKeys.contains(KeyEvent.VK_3)) {
                removed = cftMenu.addItemToCell(item, 2) ? true : false;
            }
            if (input.clickedKeys.contains(KeyEvent.VK_4)) {
                removed = cftMenu.addItemToCell(item, 3) ? true : false;
            }
            if (input.clickedKeys.contains(KeyEvent.VK_5)) {
                removed = cftMenu.addItemToCell(item, 4) ? true : false;                
            }
            if (input.clickedKeys.contains(KeyEvent.VK_6)) {
                removed = cftMenu.addItemToCell(item, 5) ? true : false;
            }
            if (input.clickedKeys.contains(KeyEvent.VK_7)) {
                removed = cftMenu.addItemToCell(item, 6) ? true : false;
            }
            if (input.clickedKeys.contains(KeyEvent.VK_8)) {
                removed = cftMenu.addItemToCell(item, 7) ? true : false;
            }
            if (input.clickedKeys.contains(KeyEvent.VK_9)) {
                removed = cftMenu.addItemToCell(item, 8) ? true : false;
            }
            
            if (removed) {
                entity.removeItem(selection);
                changedState = true;
            }
            
        }
        
    }
    
    /**
     * Metoda ktora spracovava vstup uzivatela. <br>
     * <p> V prvom rade skontroluje ci je submenu inicializovane. Mozu nastat situacie : <br>
     * - Ked mame stale aktivovany inventar ale zaroven submenu je craftinMenu. Vtedy sa spracovavaju crafting vstup.
     * Crafting vstup reaguje na vstup podla metody processCraftingKeys(). <br>
     * - Ked mame aktivovane subMenu tak spracovavame vstup tohoto subMenu. Neberieme do uvahy ine moznosti <br>
     * - Ked stlacime lave tlacidlo klavesnice tak menime aktivovane okno na subMenu.
     * </p>
     * 
     * <p> Ked nebolo subMenu aktivovane tak nastavuju dalsie moznosti : <br>
     * - Stlacenie ESC alebo inventory kluca. Vtedy sa inventar zrusi a vsetky predmety sa vratia do inventara. <br>
     * - Ked stlacime crafting kluc tak sa aktivuje craftingmenu. Ked uz v kontajnery pre menu existuje craftingMenu tak pri stlaceni
     * craftin kluca sa vsetky predmety vratia naspat do inventara. Ked neexistovalo ziadne menu tak vytvori nove podla hodnot gridx a gridy. <br>
     * - Stlacenie hore/dole tlacidiel posuva oznaceny predmet v inventary. <br>
     * - Stlacenie enteru vytvori nove subMenu s moznostami co sa da s oznacenym predmetom spravit. <br>     
     * </p>
     * 
     * Vsetky operacie su bezpecne aby nedoslo k strate predmetov entity pri roznych zmenach menu na ine menu.
     */
    @Override
    public void inputHandling() {
        if (subMenu != null) {
            
            if (subMenu.isActivated()) {
                subMenu.inputHandling();
                return;
            }  
            
            if (activated && subMenu instanceof CraftingMenu) {
                processCraftingKeys();
            }
                                             
            if (input.clickedKeys.contains(InputHandle.DefinedKey.LEFT.getKeyCode())) {
                if (subMenu.isVisible()) {
                    activated = false;
                    subMenu.activate();
                }
            }
        }

        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())
                || input.clickedKeys.contains(InputHandle.DefinedKey.INVENTORY.getKeyCode())) {       
            exit();
            changedState = true;
            return;
        }

        if (input.clickedKeys.contains(InputHandle.DefinedKey.CRAFTING.getKeyCode())) {
            if (subMenu instanceof CraftingMenu && subMenu.isVisible()) {                                
                subMenu.exit();
                subMenu = null;
                changedState = true;
                return;
            } else {
                safeSubMenuExit();
                subMenu = new CraftingMenu(this, gridx, gridy);
                subMenu.setVisible(true);                
            }
        }

        if (input.clickedKeys.contains(InputHandle.DefinedKey.UP.getKeyCode())) {
            if (selection > 0) {
                selection--;
                changedState = true;
            }
        }

        if (input.clickedKeys.contains(InputHandle.DefinedKey.DOWN.getKeyCode())) {
            if (selection < entity.getInventory().size() - 1) {
                selection++;
                changedState = true;
            }
        }

        if (input.clickedKeys.contains(InputHandle.DefinedKey.ENTER.getKeyCode())) {             
            if (selection >= 0 && entity.getInventory().size() != 0) {
                activated = false;
                safeSubMenuExit();
                subMenu = new ItemMenu(this, getSelectedItem(), selectPosX, selectPosY);
                subMenu.setVisible(true);
                subMenu.activate();
            }
        }

    }

    /**
     * <i>{@inheritDoc }</i>
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
        
    /**
     * Metoda paint vykresli do grafickeho kontextu g nami pripraveny inventar z
     * metody setGraphics volanej pri vytvarani instancie tohoto objektu a
     * predmety vnutri inventara entity.
     *
     * @param g Graficky kontext do ktoreho sa kresli
     */
    @Override
    public void paintMenu(Graphics g) {
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);

            g.drawImage(itemsBox, xPos + 20, yPos + 20, null);

            if (!activated) {
                g.setColor(Colors.getColor(Colors.selectedColor));
                g.fillRoundRect(xPos, yPos, getWidth() - wGap, getHeight() - hGap, wGap, hGap);
            }            
        }
        // Testovanie na submenu. Kedze moze prekryvat toto menu tak je v metode ako posledne.
        if (subMenu != null) {
            subMenu.paintMenu(g);
        }

    }
    
    // </editor-fold>
    
    
    
}
