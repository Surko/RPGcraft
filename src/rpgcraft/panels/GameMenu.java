/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.plugins.AbstractMenu;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.Player;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.ui.menu.CharInfo;
import rpgcraft.graphics.ui.menu.ConversationPanel;
import rpgcraft.graphics.ui.menu.InventoryMenu;
import rpgcraft.graphics.ui.menu.Journal;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.Save;
import rpgcraft.map.SaveMap;
import rpgcraft.map.tiles.Tile;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.MainUtils;
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
    public MovingEntity player;
    
    private AbstractInMenu inMenu;
    
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
        menuMap.put(res.getId(), this);
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
    
    /**
     * Metoda vrati nazov tohoto menu. Pod tymto nazvom je toto menu ulozene v 
     * hashmape <b>menuMap</b>
     * @return 
     */
    @Override
    public String getName() {
        return res.getId();
    }
    
    /**
     * Metoda getImage vrati obrazok/contentImage do ktoreho sme vykreslovali hru.
     * @return Obrazok tohoto menu.
     */
    public Image getGameImage() {
        return screenImage;
    }
    
    /**
     * Vrati sirku pre toto menu = sirka screenImage.
     * @return Sirka menu
     */
    @Override
    public int getWidth() {
        return screenImage.getWidth(null);
    }
    
    /**
     * Vrati vysku pre toto menu = vysku screenImage.
     * @return Vyska menu
     */
    @Override
    public int getHeight() {
        return screenImage.getHeight(null);
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
        if (inMenu != null) {
            inMenu.recalculatePositions();
        }              
    }        
    
    /**
     * Metoda ktora nastavi vysku menu a ostatnych casti v tomto menu volanim metody
     * setWidthHeight s velkostami z gamePane.
     */
    public void recalcWidthHeight() {       
        setWidthHeight(gamePane.getWidth(), gamePane.getHeight());
    }
    
    public synchronized void loadMapInstance(String saveName) {                 
        
        if (!gamePane.hasXmlInitialized()) {                         
            try {
                gamePane.initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());                                                       
            } catch (Exception e) {
                new MultiTypeWrn(e, Color.RED, StringResource.getResource("_resourcerror"),
                        null).renderSpecific(StringResource.getResource("_label_resourcerror"));
            }                                                        
        } else {
            LOG.log(Level.INFO, "Xml files were already initialized ---> ABORT");
        }        
                
        this.save = new Save(saveName,gamePane, this, input); 
        Tile.initializeTiles();
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Game map set");
        
        if (save.loadAndStart(saveName)) {
                                    
            saveMap = save.getSaveMap();            
            saveMap.loadMapAround(0,0);              
            player = saveMap.player;
            
            setWidthHeight(gamePane.getWidth(), gamePane.getHeight());            
            
            if (saveMap.player == null) {
                LOG.log(Level.WARNING, StringResource.getResource("_mplayer"));
            }
            
            if (PAINTING_TYPE == 2) {
                buffThread = new Thread(this);        
                buffThread.start();
            }                        
        }
        
    }
    
    @Override
    public void setInMenu(AbstractInMenu inMenu) {        
        /*
        if (jammedMenu > 0) {
            jammedMenu--;
        } else {
        this.menu = menu;
        jammedMenu = 10;                
        }*/
        MainUtils.stopped = false;
        this.inMenu = inMenu;        
        if (inMenu != null) {
            MainUtils.stopped = true;
            inMenu.activate();            
            inMenu.setVisible(true);
            inMenu.recalculatePositions();
        }    
    }
    
    public void showInMenu(String sMenu, Entity e1, Entity e2) {
        AbstractInMenu menu = AbstractInMenu.getMenu(sMenu);
        if (menu == null) {                        
            return;
        }
        
        if (e1 == null) {
            setInMenu(menu.newInstance(player, e2));
        } else {
            setInMenu(menu.newInstance(e1, e2));
        }        
    }
    
    public AbstractInMenu showMenu(Class<? extends AbstractInMenu> newClass, String sMenu, Entity e1, Entity e2) {
        AbstractInMenu menu = AbstractInMenu.getMenu(sMenu);
        if (menu == null) {                        
            try {
                menu = newClass.newInstance();
                menu.reinitialize(e1, input, this);
                AbstractInMenu.addMenu(menu);
            } catch (InstantiationException ex) {
                Logger.getLogger(GameMenu.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(GameMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (e1 == null) {
            menu = menu.newInstance(player, e2);
            setInMenu(menu);
        } else {
            menu = menu.newInstance(e1, e2);
            setInMenu(menu);
        }       
        
        return menu;
        
    }
    
    public Journal showJournal() {
        Journal journal = (Journal)Journal.getJournalMenu();
        if (journal == null) {
            journal = new Journal(player, input, this);
        }                
        journal = (Journal)journal.newInstance(player, null);
        setInMenu(journal);  
        return journal;
    }
    
    public CharInfo showInfo(Entity e) {
        CharInfo info = (CharInfo)CharInfo.getCharacterMenu();
        if (info == null) {
            info = new CharInfo(player, input, this);
        }    
                
        if (e == null) {
            info = (CharInfo)info.newInstance(player, null);
            setInMenu(info);         
        } else {
            info = (CharInfo)info.newInstance(e, null);
            setInMenu(info);
        }
        
        return info;
    }
    
    public InventoryMenu showInventory(Entity e) {
        InventoryMenu inventory = InventoryMenu.getInventoryMenu();
        if (inventory == null) {
            inventory = new InventoryMenu(player, input, this);
        }
        
        if (e == null) {
            inventory = (InventoryMenu)inventory.newInstance(player, null);
            setInMenu(inventory);
        } else {
            inventory = (InventoryMenu)inventory.newInstance(e, null);
            setInMenu(inventory);
        } 
        
        return inventory;
    }
    
    public CharInfo showCharacter(Entity e) {                
        CharInfo charInfo = CharInfo.getCharacterMenu();
        
        if (charInfo == null) {
            charInfo = new CharInfo(player, input, this);
        }
        
        if (e == null) {
            charInfo = (CharInfo)charInfo.newInstance(player, null);
            setInMenu(charInfo);
        } else {
            charInfo = (CharInfo)charInfo.newInstance(e, null);
            setInMenu(charInfo);
        }
             
        return charInfo;
    }
    
    public ConversationPanel showConversation(Entity e1, Entity e2) {                
        ConversationPanel conversation = ConversationPanel.getConversationPanel();
        
        if (conversation == null) {
            conversation = new ConversationPanel(player, e2,input, this);
        }
        
        if (e1 == null) {
            conversation = (ConversationPanel)conversation.newInstance(player, e2);
            setInMenu(conversation);
        } else {
            conversation = (ConversationPanel)conversation.newInstance(e1, e2);
            setInMenu(conversation);
        }
             
        return conversation;
    }
    
    /**
     * Tato metoda zatial zdruzuje aj funkcie nacitania mapy aj vytvorenie novej mapy.
     * Obidve funkcie sucasne su vylucitelne. Preto prepinam vzdy iba jednu.
     */
    public synchronized boolean newMapInstance(String saveName) {             
        
        // Ked existuje save tak nic nevytvori
        if (Save.saveExist(saveName)) {            
            return false;
        }                
        
        this.save = new Save(saveName,gamePane, this, input);        
        
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Game map set");
        
        if (!gamePane.hasXmlInitialized()) {                         
            try {
                gamePane.initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());                                                       
            } catch (Exception e) {
                new MultiTypeWrn(e, Color.RED, StringResource.getResource("_resourcerror"),
                        null).renderSpecific(StringResource.getResource("_label_resourcerror"));
            }                                                        
        } else {
            LOG.log(Level.INFO, StringResource.getResource("_xmlinitialized"));
        } 
        save.createNewSave();
        
        SaveMap _saveMap = save.getSaveMap();
        _saveMap.initializeTiles();                                 
            
        
        // Nacitanie playera 
        Player _player = new Player("Surko", _saveMap, EntityResource.getResource("_player"));
        _player.initialize();        
        _player.trySpawn();
        _player.setHandling(input);
        _player.setLightRadius(6);
        _player.setSound(128);
        _player.addQuest("fetch");        
        player = _player;
        // Testovacie riadky na vytvorenie dvoch entit. Tieto prikazy mozu byt v samostatnej metode na inicializaciu. Napriklad pri update
        // a kontrolovanie ci uz bolo inicializovane. v load map instance taketo prikazy niesu a preto vzdy budu initialized na true pri load.
        MovingEntity entity = new MovingEntity("Zombie", _saveMap, EntityResource.getResource("Zombie"));        
        entity.initialize();        
        entity.trySpawn(player, 64);
        MovingEntity entity2 = new MovingEntity("Zombie", _saveMap, EntityResource.getResource("Zombie"));        
        entity2.initialize();         
        entity2.trySpawn(player, 64);        
                
        // Nacitanie mapy okolo hraca
        _saveMap.loadMapAround(player); 
        //player.setImpassableTile(1);
        
        Item item = Item.createItem("Healing Potion",EntityResource.getResource("healing1"));
        Item item2 = Item.createItem("Healing Potion", EntityResource.getResource("healing1"));                
        player.addItem(item);
        player.addItem(item);
        player.addItem(item);
        player.addItem(item);                        
                
        saveMap = _saveMap;
        setWidthHeight(gamePane.getWidth(), gamePane.getHeight());                 
                                
        if (saveMap.player == null) {
            LOG.log(Level.WARNING, StringResource.getResource("_mplayer"));
        }
        
        if (PAINTING_TYPE == 2) {
            buffThread = new Thread(this);        
            buffThread.start();
        }
                
        return true;
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
        super.inputHandling();        
        
        if (inMenu != null) {
            inMenu.inputHandling();
        } else {        
            save.inputHandling();
            
            if (input.clickedKeys.contains(input.inventory.getKeyCode())) {
                showInventory(player);
            }
            
            if (input.clickedKeys.contains(input.quest.getKeyCode())) {
                showJournal();
            }
            
            if (input.clickedKeys.contains(input.character.getKeyCode())) {
                showInfo(player);
            }

            // Ulozenie hry pri stlaceni ESC
            if (input.clickedKeys.contains(input.escape.getKeyCode())) {
                save.saveAndQuit(ImageUtils.makeThumbnailImage(screenImage));            
                setMenu(AbstractMenu.getMenuByName("mainMenu"));
                save = null;
                saveMap = null;
            } 
        }
    }
        
    @Override
    public void mouseHandling(MouseEvent e) {
        if (inMenu != null) {
            inMenu.mouseHandling(e);
        }
    }
    
    @Override
    public synchronized void update() {
        super.update();        
         if (inMenu != null) {
            inMenu.update();
        } else {
            saveMap.update();               
        }
        
        hasToRepaint = true;
        
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
            if (inMenu != null) {
                inMenu.paintMenu(screenImage.getGraphics());
            }
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
