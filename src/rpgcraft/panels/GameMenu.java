/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import rpgcraft.GamePane;
import rpgcraft.MainGameFrame;
import rpgcraft.entities.Item;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.Player;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.inmenu.AbstractInMenu;
import rpgcraft.graphics.inmenu.InventoryMenu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.SaveState;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.Framer;

/**
 *
 * @author Surko
 */
public class GameMenu extends AbstractMenu implements Runnable {

    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/interfacy ">
    
    public interface PaintingTypes {
        public static final int PAINTING_INSIDE_REPAINT = 0;
        public static final int PAINTING_INSIDE_UPDATE = 1;
        public static final int PAINTING_INSIDE_THREAD = 2;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">        
    
    public static final int PAINTING_TYPE = PaintingTypes.PAINTING_INSIDE_REPAINT;
    
    // Objekt pre hraca typu Player zdedeny po Entite    
    public Player player;
    private AbstractInMenu menu;
    private SaveState map;
    private Thread buffThread;
    // Bufferovany obrazok v pamati. 
    private Image buffImage;
    // Volatilna premenna pre pristup jedneho Threadu k obrazku.
    // Pointer na obrazok (buffImage alebo contImage). Urcuje ktory z obrazkov
    // je ten co sa bude vykreslovat.
    private volatile Image screenImage;
    private volatile boolean hasToRepaint;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    
    /**
     * Konstruktor pre GameMenu s parametrom resource. Resource urcuje
     * ako bude vyzerat toto menu.
     * @param res Resource podla ktoreho sa urcuje vyzor Menu.
     */
    public GameMenu(UiResource res) {
        this.res = res;   
    }    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public/Protected metody ">                           
    
    // <editor-fold defaultstate="collapsed" desc=" Metody vlakna ">
    
    /**
     * 
     */
    @Override
    public void run() {
        while (true) {
            if (hasToRepaint) {
                if ((map != null)&&(contImage != null)) {
                    if (screenImage == buffImage) {
                       map.paint(contImage.getGraphics());
                       screenImage = contImage;
                    } else {
                        map.paint(buffImage.getGraphics());
                        screenImage = buffImage;
                    }
                }
                hasToRepaint = false;
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda getMap vracia prisluchajucu instanciu mapy k tomuto Menu.
     * @return Mapa prisluchajuca k menu.
     */
    public SaveState getMap() {
        return map;
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    @Override
    public void setWidthHeight(int w, int h) {        
        map.setWidthHeight(w, h);             
        setImageProperties(w, h);
    }
        
    
    public synchronized void newMapInstance() {
        
        gamePane.setSize(MainGameFrame.Fwidth, MainGameFrame.Fheight);                
        
        this.map = new SaveState(gamePane, input);        
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Game map set");
        
        if (!gamePane.hasXmlInitialized()) {                         
            gamePane.initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());                                  
        } else {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Xml files were already initialized ---> ABORT");
        }
        
        map.loadMapAround(0,0);   
        map.initializeTiles();
        
        // Testovacie riadky na vytvorenie dvoch entit.
        MovingEntity entity = new MovingEntity("Zombie", map, EntityResource.getResource("Zombie"));        
        entity.initialize();        
        entity.trySpawn();
        MovingEntity entity2 = new MovingEntity("Zombie", map, EntityResource.getResource("Zombie"));        
        entity2.initialize(); 
        entity2.trySpawn();
        //entity.setImpassableTile(1); 
        player = new Player("Surko", map, EntityResource.getResource("_player"));
        player.initialize();
        player.trySpawn();
        player.setHandling(input);
        player.setLightRadius(96);
        player.setSound(128);
        //player.setImpassableTile(1);
        
        Item item = new Item("Healing Potion",EntityResource.getResource("healing1"));
        player.getInventory().add(item);
        
        //map.addEntity(player);
        //map.addEntity(entity); 
        //map.addEntity(entity2);
        
        InventoryMenu inventory = new InventoryMenu(player, input, map);
        
        setWidthHeight(gamePane.getWidth(), gamePane.getHeight());
        
        if (PAINTING_TYPE == 2) {
            buffThread = new Thread(this);        
            buffThread.start();
        }
        
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update metody ">
    
    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("gameMenu",this);        
    }        
    
    @Override
    public void inputHandling() {
        map.inputHandling();
        
        
    }
        
    @Override
    public synchronized void update() {
        /*
         * Kontrola Threadu. Z istych dovodov AWT-Event-Queue Thread (vlakno vyhradne pre
         * vykreslovanie) tiez vyvolava tuto metodu aj ked nema nasledne hned
         * za Update Threadom.
         * Preto je metoda synchronized. V konecnom dosledku je jedno 
         * kto vyvolava tuto metodu, ale je dolezite aby bola
         * vyvolana iba raz a nie viacero razy (viedlo to k dvojnasobnemu poctu entit)
         * System.out.println(Thread.currentThread().getName());
         * 
         */
        if (map == null) {
            newMapInstance();
        }
        
        if (map.player != null) {
            if (Framer.tick > 2500) {
                if (map.gameTime==24) {
                    map.gameTime = 0;
                }
                map.gameTime++;
                map.setLightState(true);
                Framer.tick = 0;
            }          
            input.keyUpdates();
            map.update();
            hasToRepaint = true;
        }
        super.update();
    }
    
    /**
     * Stara metoda pre vykreslovanie mapy do bufferovaneho obrazku prebiehajuca pri volani
     * update metody => rovnaky Thread ako ostatne Updaty. Preto sa tato metoda dostava do uzadia 
     * a je vhodnejsi viac vlaknovy pristup.
     */
    @Deprecated
    @Override
    protected  void initializeGraphics() {     
        if (contImage != null) {
            Graphics dbg = contImage.getGraphics();
            dbg.setColor(Color.BLACK);
            //dbg.fillRect(0, 0, gamePane.getWidth(), gamePane.getHeight());

            if (map != null) {
                map.paint(dbg);
            }

            //changedGr = false;
        }
    }
    
    /**
     * Metoda initializeImage, tak ako v rodicovskom AbstractMenu kontroluje 
     * ci sa zmenilo Ui => co by viedlo k preinitializovaniu ui komponent (panely, tlacidla, ...)
     * alebo ci sa zmenil graficky kontext => to by viedlo preinitializovaniu grafickeho kontextu =>
     * prekresleniu mapy, no tato metoda je deprecated a vyuziva sa viac vlaknovy pristup.
     */
    @Override
    protected void initializeImage() {
        if (changedUi) {
            initializeUI();
        }
        if (changedGr) {
            if (PAINTING_TYPE == 1) {
                initializeGraphics();                
            }
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    
    /**
     * Metoda paintMenu vykresli nabufferovany obrazok s hrou 
     * do grafickeho kontextu. Vo vacsine pripadov to bude graficky kontext
     * prisluchajuci k GamePane. 
     * @param g Graficky kontext do ktoreho vykreslujeme mapu
     */
    @Override
    public void paintMenu(Graphics g) { 
        if (screenImage == null) {  
            return;
        }
        if ((PAINTING_TYPE == 0)&&(screenImage != null)&&(map != null)) {
            map.paint(screenImage.getGraphics());
        }
        
        g.drawImage(screenImage, 0, 0, null);
    }
     
    // </editor-fold>
             
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Privatne metody ">
    private void setImageProperties(int w, int h) {
        if (contImage == null) {
            contImage = gamePane.createImage(w, h);
            if (PAINTING_TYPE == 2) {
                buffImage = gamePane.createImage(w, h);
            }
            Graphics dbg;
            if (contImage == null) {
                new MultiTypeWrn(null, Colors.getColor(Colors.menuError1), "Null dbImage", null).renderSpecific("ContImage in GamePane is null");
                return;
            } else {
                dbg = contImage.getGraphics();
                dbg.setColor(Color.BLACK);
                dbg.fillRect(0, 0, w, h); 
                if (PAINTING_TYPE == 2) {
                    dbg = buffImage.getGraphics();
                    dbg.setColor(Color.BLACK);
                    dbg.fillRect(0, 0, w, h); 
                }
                screenImage = contImage;
            }
                                                         
        }
    }
    
    // </editor-fold>
   
    
}
