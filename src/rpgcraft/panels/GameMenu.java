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
import rpgcraft.graphics.ui.menu.EndMenu;
import rpgcraft.graphics.ui.menu.InventoryMenu;
import rpgcraft.graphics.ui.menu.Journal;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.Save;
import rpgcraft.map.SaveMap;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.MainUtils;

/**
 * Menu pre hru dediace od AbstractMenu. Vacsina metod zabezpecuje abstraktna trieda AbstractMenu.
 * Najdolezitejsie kusy kodu su v konstruktore, ktory prida intro do definovanych menu, a v inputHandling
 * ktory spracovava vstup uzivatela vzhladom na toto menu. Kedze toto menu je zaklad
 * pre celu hru tak sa menu stara o nacitanie/vytvaranie/vykreslovanie/aktualizovanie map.
 * @author Kirrie
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
    private static final String GAMEMENU = "gameMenu";
    
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
     * @return Meno pre menu
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
    
    /**
     * Metoda ktora vrati aktivny save z tohoto menu.
     * @return Aktivny save v tomto menu
     */
    public Save getSave() {
        return save;
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
    
    /**
     * Metoda ktora nacita ulozenu poziciu z disku podla mena zadaneho parametrom
     * <b>saveName</b>. Z nacitanych dat vytvorime instanciu Save z ktorej ziskame
     * nacitanu mapu ktora je pripravena na zobrazovaniei a interagovanie
     * @param saveName Meno pozicie ktoru nacitavame.
     */
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
    
    /**
     * Metoda ktora nastavi vnutorne menu v tomto GameMenu. Vnutorne menu je zadane v parametri
     * a na zobrazenie ho je treba zresetovat/zviditelnit/aktivovat volanim metod.
     * @param inMenu InMenu ktore chceme zobrazit
     */
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
        if (inMenu != null) {
            MainUtils.stopped = true;
            inMenu.recalculatePositions();
            inMenu.activate();            
            inMenu.setVisible(true);            
        }    
        this.inMenu = inMenu;   
    }    
    
    /**
     * Metoda ktora zobrazi menu, ktore si ziskame metodou getMenu podla mena <b>sMenu</b>.
     * Po ziskani menu vytvorime novu instanciu podla nasich potrieb. Ked je menu null
     * tak nic nezobrazime
     * @param sMenu Text menu ktore chceme zobrazit
     * @param e1 Entita pre ktoru zobrazujeme menu
     * @param e2 Entita s ktorou zdielame menu
     */
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
    
    /**
     * Metoda ktora zobrazi menu. Najprv skusi vybrat menu podla textu <b>sMenu</b>. Ked sa to nepodari
     * tak bude chcet zobrazit menu ktoreho typ ziskame z parametru <b>newClass</b>, co je zadana trieda
     * Menu ziskame volanim newInstance na tuto triedu a volanim reinitialize nastavime
     * toto menu podla urcitych konvencii. Po zinicializovani zavolame na toto menu
     * este raz metodu newInstance s parametrami ktora vytvori a zinicializuje toto menu
     * podla nasich potrieb.
     * @param newClass Trieda podla ktorej vytvarame menu
     * @param sMenu Text ako sa vola menu
     * @param e1 Entita pre ktoru zobrazujeme menu
     * @param e2 Entita s ktorou zdielame menu
     * @return AbstractInMenu podla typu.
     */
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
    
    /**
     * Metoda ktora zobrazi zurnal entity zadanej parametrom.
     * Na vytvorenie zurnalu treba zavolat newInstance na existujuci zurnal
     * ktory vytvorime kontruktorom.     
     * @return Zurnal
     */
    public Journal showJournal() {
        Journal journal = (Journal)Journal.getJournalMenu();
        if (journal == null) {
            journal = new Journal(player, input, this);
        }                
        journal = (Journal)journal.newInstance(player, null);
        setInMenu(journal);  
        return journal;
    }
    
     /**
     * Metoda ktora zobrazi end menu entity zadanej parametrom.
     * Na vytvorenie takehoto menu treba zavolat newInstance na existujuce end menu
     * ktory vytvorime kontruktorom.
     * @param e Entita pre ktoru panel zobrazujeme (Player)    
     * @return EndMenu
     */
    public EndMenu showEndMenu(Entity e) {
        EndMenu endMenu = (EndMenu)EndMenu.getEndMenu();
        if (endMenu == null) {
            endMenu = new EndMenu(player, input, this);
        }                
        endMenu = (EndMenu)endMenu.newInstance(player, null);
        setInMenu(endMenu);  
        return endMenu;        
    }
    
    /**
     * Metoda ktora zobrazi charakterovy panel entity zadanej parametrom.
     * Na vytvorenie charakteroveho panelu treba zavolat newInstance na existujuci panel
     * ktory vytvorime kontruktorom.
     * @param e Entita pre ktoru panel zobrazujeme (Player)    
     * @return Charakterovy panel
     */
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
    
    /**
     * Metoda ktora zobrazi inventar entity zadanej parametrom.
     * Na vytvorenie inventaru treba zavolat newInstance na existujuci inventar
     * ktory vytvorime kontruktorom.
     * @param e Entita pre ktoru panel zobrazujeme (Player)    
     * @return Inventar
     */
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
    
     /**
     * Metoda ktora zobrazi charakterovy panel entity zadanej parametrom.
     * Na vytvorenie charakteroveho panelu treba zavolat newInstance na existujuci panel
     * ktory vytvorime kontruktorom.
     * @param e Entita pre ktoru panel zobrazujeme (Player)    
     * @return Charakterovy panel
     */
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
    
    /**
     * Metoda ktora zobrazi konverzacny panel medzi entitami zadanymi parametrami.
     * Na vytvorenie konverzacneho panelu treba zavolat newInstance na existujucu konverzaciu
     * ktoru vytvorime kontruktorom.
     * @param e1 Entita ktora sa rozprava (Player)
     * @param e2 Entita s ktorou sa rozpravame
     * @return Konverzacny panel
     */
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
     * Tato metoda vytvara novu instanciu Save a SaveMap. Meno ulozenej pozicie je dane
     * parametrom <b>saveName</b>. Metoda vytvori instancie Save aj SaveMap a nasledne donich prida
     * hraca. Po nacitani hraca aktualizujeme okolie v mape podla tohoto hraca.
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
        // Nacitanie mapy okolo hraca
        _saveMap.loadMapAround(player); 
        //player.setImpassableTile(1);
        
        Item item = Item.createItem("Healing Potion",EntityResource.getResource("healing1"));                 
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
    
    /**
     * Metoda ktora zresetuje aktivny save a save mapu v tomto menu
     */
    public void resetSave() {
        save = null;
        saveMap = null;
    }
    
    /**
     * Metoda ktora reaguje na vstup podla toho co je stlacene. Ked existuje inMenu
     * tak aktualizujeme iba inMenu.
     */
    @Override
    public void inputHandling() {
        super.inputHandling();        
        
        if (inMenu != null) {
            inMenu.inputHandling();
        } else {        
            save.inputHandling();
            
            if (MainUtils.DEFAULTMENU) {
                if (input.clickedKeys.contains(InputHandle.DefinedKey.INVENTORY.getKeyCode())) {
                    showInventory(player);
                }

                if (input.clickedKeys.contains(InputHandle.DefinedKey.QUEST.getKeyCode())) {
                    showJournal();
                }

                if (input.clickedKeys.contains(InputHandle.DefinedKey.CHARACTER.getKeyCode())) {
                    showInfo(player);
                }

                // Ulozenie hry pri stlaceni ESC
                if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {
                    showEndMenu(player);
                } 
            }
        }
    }
        
    /**
     * Metoda ktora reaguje na stlacenia mysi. Ked existuje inMenu
     * tak posielame toto spracovavanie do tohoto inMenu.
     * @param e MouseEvent ktory spracovava tato metoda
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        if (inMenu != null) {
            inMenu.mouseHandling(e);
        }
    }
    
    /**
     * Synchronizovana metoda updatektora zavola aktualizovanie rodicovskeho menu.
     * Ked existuje InMenu v tomto menu tak aktualizuje iba toto vnutorne menu.
     * V druhom pripade aktualizujeme mapu s entitami atd...
     */
    @Override
    public synchronized void update() {
        super.update();        
         if (inMenu != null) {
            inMenu.update();
        } else {
             if (saveMap != null) {
                 saveMap.update();      
             }
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
                if (inMenu != null) {
                    inMenu.paintMenu(screenImage.getGraphics());
                }
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
