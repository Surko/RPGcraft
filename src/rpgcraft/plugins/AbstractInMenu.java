/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import rpgcraft.GamePane;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.ui.menu.Menu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.map.SaveMap;
import rpgcraft.plugins.AbstractMenu;

/**
 *
 * @author doma
 */
public abstract class AbstractInMenu implements Menu<AbstractInMenu> {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Menu list so vsetkymi definovanymi menu.
    protected static HashMap<String, AbstractInMenu> menuList = new HashMap<>();
    
    // Entita pre ktoru toto menu vytvarame.
    protected Entity entity;
    // Input podla ktoreho spracovavame vstup
    protected InputHandle input;
    // GamePane v ktorom vykreslujeme toto menu
    protected GamePane game;
    // Rodicovske AbstractInMenu v ktorom sa nachadza. Ked je null tak je menu najvyssie postaveny rodic.
    protected AbstractInMenu sourceMenu;
    
    // Obrazok s prichystanym menu na vykreslenie
    protected Image toDraw;
    // Kazde menu moze mat svoje subMenu.
    protected AbstractInMenu subMenu;
    
    // Vyska a sirka menu. Obycajne sa vyuzivaju konstantne definovane dimenzie. Ale ked su tieto hodnoty nenulove tak sa pouziju.
    protected int w = -1, h = -1; 
    // Viditelnost menu
    protected boolean visible;
    
    // x-ova a y-ova pozicia menu v AbstractMenu
    protected int xPos, yPos;
    
    // Ci sa zmenilo nieco v menu
    protected boolean changedState;
    // Ci je menu aktivovane. Aktivovane menu je bledsie ako tie neaktivne.
    protected boolean activated;    
    
    // Hlavne menu do ktoreho pripada vnutorne menu
    protected AbstractMenu menu;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor abstraktneho menu. Programator nemoze vytvorit instanciu tohoto menu kedze menu je abstraktna trieda ale
     * potomkovia uz su normalnymi triedami, ktore musia zavolat tento konstruktor. Konstruktor initializuje entity a input
     * s menu ktore vytvarame.
     * @param entity
     * @param input 
     */
    public AbstractInMenu(Entity entity, InputHandle input) {
        //System.out.println(entity + " " + input);
        this.entity = entity;
        this.input = input;
    }
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    /**
     * Metoda ktora initializuje toto menu s novou entitou zadanou parametrom <b>e</b>.
     * Kazda dediaca trieda moze mat svoju vlastnu implementaciu.      
     * @param origMenu Originalne menu z ktoreho initializujeme nove menu
     * @param e Entita pre ktoru robime menu
     * 
     */
    public abstract AbstractInMenu initialize(AbstractInMenu origMenu, Entity e);
    
    /**
     * Metoda ktora prepocitava pozicie menu v GameMenu alebo mozne doplnit aj na ine AbstractMenu.
     */
    public abstract void recalculatePositions();
    
    /**     
     * Metoda ktora nastavi graficky vyzor pre menu. Kazdy potomok moze byt rozny ked si tuto
     * metodu potomok implementuje, ale v konecnom dosledku by mali mat pozadie spolocne.
     * Telo metody sa sklada z vytvorenia obrazku <b>toDraw</b> ktory vykreslujeme v metode paintMenu.
     * Tento obrazok je vyplneny dvoma vyplnenymi obdlznikmi (jeden od druheho trochu posunuty <=> wGap, hGap)
     * na vytvorenie efektu tienu.
     */
    protected void setGraphics() {
        toDraw = new BufferedImage(getWidth(), getHeight(), BufferedImage.TRANSLUCENT);
        Graphics g = toDraw.getGraphics();
       
        g.setColor(Colors.getColor(Colors.invBackColor));
        g.fillRoundRect(0, 0, getWidth() - getWGap(), getHeight() - getHGap(), getWGap(), getHGap());

        g.setColor(Colors.getColor(Colors.invOnTopColor));
        g.fillRoundRect(getWGap(), getHGap(), getWidth() - getWGap(), getHeight() - getHGap(), getWGap(), getHGap());
        
    }        
    
    /**
     * Metoda ktora vykona update nad menu. Kazdy potomok moze inak aktualizovat menu.
     */
    @Override
    public abstract void update();
    
    /**
     * Metoda ktora vykresli menu do grafickeho kontextu. Kazdy potomok vykresluje svoje menu inak.
     * @param g Graficky kontext do ktoreho vykreslujeme menu
     */
    @Override
    public abstract void paintMenu(Graphics g);
    
    /**
     * Spolocna velkost pre medzeru po dlzke.
     * @return Medzera po dlzke
     */
    public abstract int getWGap();
    
    /**
     * Spolocna velkost pre medzeru po vyske.
     * @return Medzera po vyske
     */
    public abstract int getHGap();
    
    /**
     * Metoda ktora vracia sirku Menu. Kazdy potom moze navratit rozne hodnoty.
     * @return Sirka menu
     */
    @Override
    public abstract int getWidth();
    
    /**
     * Metoda ktora vracia vysku Menu. Kazdy potomok moze navratit rozne hodnoty.
     * @return Vyska menu
     */
    @Override
    public abstract int getHeight();
      
    /**
     * Metoda ktora vrati by mala vratit meno pre toto menu ktore je zaroven klucom
     * k ziskaniu tohoto menu v hashmape <b>menuList</b>
     * @return Meno tohoto menu.
     */
    public abstract String getName();
    
    /**
     * Metoda ktora odpoveda na vstup uzivatela. Implementovana v potomkoch.
     */
    @Override
    public abstract void inputHandling();        
            
    /**
     * Metoda ktora spracovava event z mysi. Kazde menu moze spracovavat eventy rozne.
     */
    @Override
    public abstract void mouseHandling(MouseEvent e);
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati podla parametru sMenu take AbstractInMenu ktore zodpoveda 
     * tomuto parametru. Tieto menu su ulozene v HashMape menuList.
     * @param sMenu Textovy retazec/kluc z menuList
     * @return AbstractInMenu zodpovedajuce parametru
     */
    public static AbstractInMenu getMenu(String sMenu) {
        return menuList.get(sMenu);
    }
    
    /**
     * Metoda ktora vrati ci je menu aktivovane tym padom interakcie mozne.
     * @return True/false ci je menu aktivovane.
     */
    public boolean isActivated() {
        return activated;
    }
    
    /**
     * Metoda ktora vrati viditelnost menu.
     * @return True/false ci je menu viditelne.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Metoda ktora vrati entitu pre ktoru bolo vytvorene menu.
     * @return Entita pre toto menu
     */
    public Entity getEntity() {
        return entity;        
    }
    
    /**
     * Metoda ktora vrati handler klavesnice pre toto menu.
     * @return Handler tohoto menu
     * @see InputHandle
     */
    public InputHandle getInput() {
        return input;
    }        
    
    /**
     * Metoda ktora vrati x-ovu poziciu menu v paneli.
     * @return x-ova pozicia
     */
    public int getXPos() {
        return xPos;
    }
    
    /**
     * Metoda ktora vrati y-ovu poziciu menu v paneli.
     * @return y-ova pozicia
     */
    public int getYPos() {
        return yPos;
    }
    
    /**
     * Metoda ktora vrati obrazok na vykreslenie.
     * @return 
     */
    public Image getDrawImage() {
        return toDraw;
    }
    
    /**
     * Metoda ktora vrati pre ake menu sme tvorili toto inMenu.
     * @return Menu v ktorom je toto menu
     */
    public AbstractMenu getMenu() {
        return menu;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Metoda ktora nastavi pozicie tohoto menu v domovskom/hracom paneli.
     * Vacsinou je tato metoda zavolana pri aktualizovani velkosti okna.
     * @param x X-ova pozicia tohoto menu
     * @param y Y-ova pozicia tohoto menu
     */
    public void setPositions(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }
        
    /**
     * Metoda ktora nastavi viditelnost tohoto menu. Vyuzite v metode paintComponent.
     * @param visible Viditelnost menu
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }   
    
    /**
     * Metoda ktora nastavi pod menu tohoto menu.
     * @param menu SubMenu tohoto menu
     */
    @Override
    public void setMenu(AbstractInMenu menu) {
        
    }
    
    /**
     * Metoda ktora zmeni stav tohoto menu. Vacsinou si pre zmene stavu donuti v metode
     * update aktualizovat menu podla novych informacii.
     * @param state 
     */
    public void setState(boolean state) {
        this.changedState = state;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora ukonci cinnost menu. Defaultne nastavuje viditelnost a ci je menu aktivne na false.
     * Vacsinou je tato metoda pretazovana v potomkoch. Netreba zabudat ze ked ukoncujeme nejake hlavne menu (nie submenu)
     * tak je dolezite zavolat menu.setInMenu(null) aby bolo menu zrusene. Ked je menu typu submenu tak nemusi dochadzat k takemuto nastaveniu.
     */    
    public void exit() {
        visible = false;
        activated = false;
    }           
    
    /**
     * Metoda ktora bezpecne ukonci pod menu pre toto menu. Po kazdom bezpecnom ukonceni
     * je povinnostou zmenit changedState na true cim sa premaluje toto menu podla novo pridanych predmetov.
     */
    public void safeSubMenuExit() {
        if (subMenu != null) {
            subMenu.exit();
            changedState = true;
            subMenu = null;
        }
    }
    
    /**
     * Metoda ktora deaktivuje menu
     */
    public void deactivate() {
        this.activated = false;
    }
    
    /**
     * Metoda ktora aktivuje menu.
     */
    public void activate() {
        this.activated = true;
    }        
    
    /**
     * Staticka metoda prida do hashmapy <b>menuList</b> nove menu 
     * s menom ziskanym z metody getName (metoda getMenu musi byt implementovana v tomto menu).
     * @param inMenu Nove menu do hashmapy.
     */
    public static void addMenu(AbstractInMenu inMenu) {
        menuList.put(inMenu.getName(), inMenu);
    }
    
    /**
     * Metoda ktora reinicializuje menu. Robi to iste co konstruktor.
     * @param entity Entita pre ktoru vytvarame menu
     * @param input InputHandler ovladanie klavesnice  pre toto menu
     */
    public void reinitialize(Entity entity, InputHandle input) {
        this.entity = entity;
        this.input = input;
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="Collapsed" desc=" Staticke metody ">
    /**
     * Metoda newInstance ktora vrati pravu instanciu AbstractInMenu. Prava v tomto vyzname
     * znamena ze to bude instancia AbstractInMenu alebo jeho potomkov (vsetko si zisti z typu triedy).
     * Po vytvoreni novej instancie zavola initialize co prenastavi entitu pre ktoru vytvarame menu.
     * Vyhoda volani tejto metody pri volani konstruktora je ta ze nevolame metody setGraphics ale vyuzivame spolocny obrazok.
     * @param name Meno predmetu ktory bude instancia vytvarat.
     * @param itemType Typ predmetu ktory bude instancia vytvarat.
     * @return Instanciu typu ItemGeneratorPlugin alebo jeho potomkov.
     */
    public final AbstractInMenu newInstance(Entity e) {
        try {
            System.out.println(this.getClass().newInstance());
            return this.getClass().newInstance().initialize(this, e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    } 
    
    // </editor-fold>
    
}
