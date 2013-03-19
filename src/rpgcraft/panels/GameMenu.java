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
import rpgcraft.map.Save;
import rpgcraft.map.SaveMap;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.Framer;
import rpgcraft.utils.ImageUtils;

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
    
    private static final Logger LOG = Logger.getLogger(GameMenu.class.getName());
    
    // Objekt pre hraca typu Player zdedeny po Entite    
    public Player player;
    private AbstractInMenu menu;
    private Save save;
    private SaveMap saveMap;
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
                if ((saveMap != null)&&(contImage != null)) {
                    if (screenImage == buffImage) {
                       saveMap.paint(contImage.getGraphics());
                       screenImage = contImage;
                    } else {
                        saveMap.paint(buffImage.getGraphics());
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
    public SaveMap getMap() {
        return saveMap;
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Metoda setWidthHeight nastavi velkosti (vysku a sirku) pre toto menu,
     * aj velkost mapy aj velkost bufferovacich obrazkov ktore vykresluje toto menu.     
     * Toto sa sklada z volania dvoch funkcii <b>setWidthHeight</b>, ktora nastavi
     * velkost mapy na pozadovanie velkosti a <b>setImageProperties<b>, ktora nastavi
     * velkosti bufferovacich obrazkov podla dalsich parametrov vykreslovania.
     * @param w Nove sirky pre menu .
     * @param h Nove vysky pre menu.
     */
    @Override
    public void setWidthHeight(int w, int h) {        
        saveMap.setWidthHeight(w, h);             
        setImageProperties(w, h);
    }
    
    public void recalcWidthHeight() {       
        setWidthHeight(gamePane.getWidth(), gamePane.getHeight());
    }
    
    public synchronized void loadMapInstance(String saveName) {
        
        gamePane.setSize(MainGameFrame.Fwidth, MainGameFrame.Fheight); 
        
        if (!gamePane.hasXmlInitialized()) {                         
            gamePane.initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());                                  
        } else {
            LOG.log(Level.INFO, "Xml files were already initialized ---> ABORT");
        }
                        
        this.save = new Save(saveName,gamePane, input); 
        
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Game map set");
        
        if (save.loadAndStart(saveName)) {
        
            saveMap = save.getSaveMap();
            saveMap.loadMapAround(0,0);  
            saveMap.initializeTiles();                
            
            if (saveMap.player == null) {
                LOG.log(Level.WARNING, StringResource.getResource("_mplayer"));
            }
            
            if (PAINTING_TYPE == 2) {
                buffThread = new Thread(this);        
                buffThread.start();
            }
        }
        
    }
    
    /**
     * Tato metoda zatial zdruzuje aj funkcie nacitania mapy aj vytvorenie novej mapy.
     * Obidve funkcie sucasne su vylucitelne. Preto prepinam vzdy iba jednu.
     */
    public synchronized void newMapInstance() {
        
        gamePane.setSize(MainGameFrame.Fwidth, MainGameFrame.Fheight);                
        
        this.save = new Save("skuska",gamePane, input);        
        
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Game map set");
        
        if (!gamePane.hasXmlInitialized()) {                         
            gamePane.initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());                                  
        } else {
            LOG.log(Level.INFO, "Xml files were already initialized ---> ABORT");
        }
        save.createNewSave();
        
        saveMap = save.getSaveMap();
        saveMap.loadMapAround(0,0);  
        saveMap.initializeTiles();
        
        // Testovacie riadky na vytvorenie dvoch entit.
        MovingEntity entity = new MovingEntity("Zombie", saveMap, EntityResource.getResource("Zombie"));        
        entity.initialize();        
        entity.trySpawn();
        MovingEntity entity2 = new MovingEntity("Zombie", saveMap, EntityResource.getResource("Zombie"));        
        entity2.initialize(); 
        entity2.trySpawn();
        //entity.setImpassableTile(1); 
        player = new Player("Surko", saveMap, EntityResource.getResource("_player"));
        player.initialize();
        player.trySpawn();
        player.setHandling(input);
        player.setLightRadius(6);
        player.setSound(128);
        //player.setImpassableTile(1);
        
        Item item = new Item("Healing Potion",EntityResource.getResource("healing1"));
        player.getInventory().add(item);
        
        // DEBUG PRE PRIDANIE HRACA, vylucitelne s nacitanim mapy
        saveMap.addEntity(player);
        //map.addEntity(entity); 
        //map.addEntity(entity2);
        
        InventoryMenu inventory = new InventoryMenu(player, input, saveMap);
        
        setWidthHeight(gamePane.getWidth(), gamePane.getHeight());
        
        if (saveMap.player == null) {
            LOG.log(Level.WARNING, StringResource.getResource("_mplayer"));
        }
        
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
        save.inputHandling();
        
        if (input.escape.click) {
            save.saveAndQuit(ImageUtils.makeThumbnailImage(screenImage));            
            setMenu(AbstractMenu.getMenuByName("mainMenu"));
            save = null;
            saveMap = null;
            input.escape.click = false;
        }
        
        
    }
        
    @Override
    public synchronized void update() {

        if (Framer.tick > 2500) {
            if (saveMap.getGameTime()==24) {
                saveMap.setGameTime(0);
            }
            saveMap.increaseGameTime();
            saveMap.setLightState(true);
            Framer.tick = 0;
        }          
        input.keyUpdates();
        saveMap.update();
        hasToRepaint = true;

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

            if (saveMap != null) {
                saveMap.paint(dbg);
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
        if ((PAINTING_TYPE == 0)&&(screenImage != null)&&(saveMap != null)) {
            saveMap.paint(screenImage.getGraphics());
        }
        
        g.drawImage(screenImage, 0, 0, null);
    }
     
    // </editor-fold>
             
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Privatne metody ">
    
    /**
     * Metoda setImageProperties preinicializuje/vytvori nove obrazky na vykreslenie mapy
     * podla novo zadanej vysky a sirky. Obrazok je definovany v AbstractMenu ako <b>contImage</b>
     * Natoto posluzi metoda createImage v paneli v ktorom sa odohrava hra, v ktorom
     * bolo vytvorene toto menu (vacsinou GamePane). Tato metoda vytvara off-screen
     * obrazok pre dvojite bufferovanie. Po vytvoreni je zvykom otestovat ci nahodou nejake vlakno
     * nezrusilo tento bufferovaci obrazok a ked nie tak ho prichysta na kreslenie.
     * Pri PAINTING_TYPE rovnemu dvojke pouzivame princip page flipping co su 2 bufferovacie obrazky.
     * Vystupuje tu vzdy jeden pointer <b>screenImage</b>, ktory ukazuje na jeden
     * z tychto obrazkov ktory budeme vykreslovat na obrazovku,
     * pricom druhy <b>buffImage</b> je zatial spracovavany druhym Threadom. 
     * @param w Sirka bufferovacich obrazkov
     * @param h Vyska bufferovacich obrazkov
     */
    private void setImageProperties(int w, int h) {
        contImage = gamePane.createImage(w, h);
        if (PAINTING_TYPE == 2) {
            buffImage = gamePane.createImage(w, h);
        }
        Graphics dbg;
        if (contImage == null) {
            new MultiTypeWrn(null, Colors.getColor(Colors.internalError), "Null dbImage", null).renderSpecific("ContImage in GamePane is null");
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
    
    // </editor-fold>   
    
}
