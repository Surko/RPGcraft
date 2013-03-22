/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.logging.*;
import javax.swing.*;
import org.w3c.dom.Element;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.panels.*;   
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.TileResource;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.Framer;
import rpgcraft.xml.EntityXML;
import rpgcraft.xml.TilesXML;
import rpgcraft.xml.ImagesXML;
import rpgcraft.xml.UiXML;
import rpgcraft.xml.XmlReader;
/**
 *
 * @author Kirrie
 */

public class GamePane extends SwingImagePanel implements Runnable {
    
    // <editor-fold defaultstate="collapsed" desc="Premenne">
    // Thread v ktorom bezi hlavna hra, dalsi Thread sa stara o UI prvky atd.
    private Thread t; 
    // JFrame alias hlavny frame s panelom v ktorom je hra
    private JFrame mFrame;    
    // Logger na odlogovanie hry
    private Logger logger = Logger.getLogger(getClass().getName());
    //Bool hodnota ci bolo initializovane GUI s loggerom 
    private boolean initialized = false;
    // Bool hodnota ci boli initializovane xml subory s entitami a dlazdicami
    private boolean xmlInitialized = false;        
    // Premenne urcujuce stav hry
    private volatile static boolean running = true;    
    public volatile static boolean gameOver = false;    
    //
    
    // Sirka a Vyska Panelu s hrou
    private int pWidth;
    private int pHeight;                      
  
    // Definovany vstup pre hraca
    public InputHandle input;                
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Konstruktor">
    /**
     * Konstruktor s panelom pre hru. Nastavi menu na prazdne menu bez nejakych vnutornych komponent. 
     * Toto menu sluzi ako prva inicializacia hry na ktorom su postavene dalsie menu.
     * Nasledne inicializuje farbu pozadia.
     */
    public GamePane() {
        // Inicializacia prazdneho menu
        menu = new BlankMenu();     
        /*
         // DEBUGGING
         Object[][] sk = DataUtils.getDataArrays("[#BEST(NAME,IMAGE,DATE),[@newgame,ahoj]]");
         for (int i = 0; i< sk.length; i++) 
           for (int j = 0; j < sk[i].length; j++) {
               System.out.println(sk[i][j]);
               }
         */
        addOwnMouseListener();
        backColor = Colors.getColor(Colors.Black);   
                
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Privatne metody">
    
    // <editor-fold defaultstate="collapsed" desc="Pomocne + Debugging metody">
    private void writeAllComponents(Component c, String parString) {
        if (c instanceof SwingImageList) {
            System.out.println(parString + " -> list:" +c.getLocation() + "," + c.getSize());  
            for (Component _c : ((Container)c).getComponents()) {
                writeAllComponents(_c, parString + " -> list:" +c.getLocation()+ "," + c.getSize());
            }
            return;
        }
        if (c instanceof SwingImageButton) {
            System.out.println(parString + " -> button: " + c.getLocation()+ "," + c.getSize());
        }
        if (c instanceof SwingText) {
            System.out.println(parString + " -> text: " + c.getLocation()+ "," + c.getSize());
        }
        if (c instanceof SwingInputText) {
            System.out.println(parString + " -> edittext: " + c.getLocation()+ "," + c.getSize() + "," + c.isShowing());
        }

        if (c instanceof SwingImagePanel) {  
            System.out.println(parString + " -> panel: " + c.getLocation()+ "," + c.getSize());
            for (Component _c : ((Container)c).getComponents()) {                
                writeAllComponents(_c, parString + " -> panel:" +c.getLocation()+ "," + c.getSize());
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Inicializacie">
    private void init() {
        if (initialized) {
            return;
        }
        initLogger();
        logger.log(Level.INFO, StringResource.getResource("initinfo1"));
        logger.log(Level.INFO, StringResource.getResource("initinfo2"),
                PathManager.getInstance().getRootPath().toString());
        pWidth=MainGameFrame.getContentWidth();
        pHeight=MainGameFrame.getContentHeight();  
        mFrame = MainGameFrame.getFrame();
        setBounds(0, 0, pWidth, pHeight);
        
        setBackground(Color.BLACK);
        setVisible(true);              
        
        input = InputHandle.getInstance();
        menu.initialize(null, input);
        
        logger.log(Level.INFO, StringResource.getResource("initinfo4"));        
        
        logger.log(Level.INFO, StringResource.getResource("initinfo5"));
        initialization();        
        
    }
    
    /**
     * 
     */
    private void initLogger() {
        if (ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0) {
            System.setOut(new PrintStream(System.out) {

                @Override
                public void print(final String message) {
                    Logger.getLogger("").info(message);
                }
            });
            System.setErr(new PrintStream(System.err) {
                @Override
                public void print(final String message) {
                    Logger.getLogger("").severe(message);
                }
            });
        }
        File dirPath = PathManager.getInstance().getLogPath();

        if (!dirPath.exists()) {
            if (!dirPath.mkdirs()) {
                return;
            }
        }

        try {
            FileHandler fh = new FileHandler(new File(dirPath, "RPGcraft.log").getAbsolutePath(), false);
            fh.setLevel(Level.INFO);
            fh.setFormatter(new SimpleFormatter());
            Logger.getLogger("").addHandler(fh);
        } catch (IOException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
        }
    }
    
    /**
      * Inicializuje zaciatocne premenne, xml subory a nastavi zakladne typy menu, ktore sa v hre vyskytuju.
      * Vsetky udalosti su obstarane logovanim pre hladani kde sa stala chyba.  
      * @see GamePane#initializeXmlFiles(java.io.File[]) 
      */     
    private void initialization() {   
            
            if (!xmlInitialized) {                         
                initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());
                xmlInitialized = true;                        
            } else {
            logger.log(Level.INFO, "Xml files were already initialized ---> ABORT");
            }  
            
            componentContainer = new rpgcraft.panels.components.Container(null, this.getWidth(), 
                this.getHeight(), 0, 0, null);
            rpgcraft.panels.components.Container.mainContainer = componentContainer;
            componentContainer.setComponent(this);
            componentContainer.setTop(true);
            
            IntroPanel intro = new IntroPanel(UiResource.getResource("introMenu"));
            intro.initialize(componentContainer, input);
            logger.log(Level.INFO, "Intro panel set");
            MainMenu main = new MainMenu(UiResource.getResource("mainMenu"));
            main.initialize(componentContainer, input);
            logger.log(Level.INFO, "Main panel set");
            AboutMenu about = new AboutMenu(UiResource.getResource("aboutMenu"));
            about.initialize(componentContainer, input);
            logger.log(Level.INFO, "About panel set"); 
            GameMenu game = new GameMenu(UiResource.getResource("gameMenu"));
            game.initialize(componentContainer, input);            
            logger.log(Level.INFO, "Game panel set"); 
            LoadCreateMenu loadcreate = new LoadCreateMenu(UiResource.getResource("loadcreateMenu"));
            loadcreate.initialize(componentContainer, input);
            logger.log(Level.INFO, "LoadCreate panel set"); 

            

    }
     
    /**
     * Metoda inicializuje vsetky entity ktore sa nachadzaju v adresari entities.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory. V if podmienke testujeme
     * ci je adresar xml. Ked je tak ho rozparsujeme s tym ze pozname rootelement/parameter <b>res</b>.
     * Root element tu sluzi taku rolu, ze mame dva typy entit: Predmety a NPC/Hraca. Pre kazdy taky 
     * typ mame roznu stavbu xml dokumentu.
     * (pri zlom formate xml sa vypise chyba do logu ako sa aj objavi hracie error okno)
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s entitami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeEntities(File file, String res) {
        logger.log(Level.INFO, "EntFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles()) { 
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
                for (Element elem : xmlread.parseElements(res)) {
                    EntityResource.newBundledResource(elem);
                }
            }                                    
        }        
    }
    
    /**
     * Metoda inicializuje vsetky obrazky suvisiace s hrou nachadzajuce sa v adresari images.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory. V if podmienke testujeme
     * ci je adresar xml. Ked je tak ho rozparsujeme s rootelementom definovanom v ImagesXML s nazvom
     * <b>IMAGE</b>
     * (pri zlom formate xml sa vypise chyba do logu ako sa aj objavi hracie error okno)
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s obrazkami.
     */
    private void initializeImages(File file) {
        logger.log(Level.INFO, "ImageFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles()) {
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
                for (Element elem : xmlread.parseElements(ImagesXML.IMAGE)) {
                    ImageResource.newBundledResource(elem);
                }   
            }                                    
        }                    
    }
    
    private void initializeTiles(File file) {
        logger.log(Level.INFO, "TileFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles()) {                                     
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
                for (Element elem : xmlread.parseElements(TilesXML.TILE)) {
                    TileResource.newBundledResource(elem);
                }     
            }                               
        }
    }
            
    private void initializeUI(File file) {
        logger.log(Level.INFO, "UIFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles()) {
            // zistovanie ci je subor xml
            System.out.println(xmlfile.getName().substring(xmlfile.getName().length() - 3));
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
                for (Element elem : xmlread.parseRootElements(UiXML.ELEMENT)) {
                    UiResource.newBundledResource(elem);
                }
            }                        

        }                    
    }   
             
    // </editor-fold>         
    
    // </editor-fold>           
    
    // <editor-fold desc="Public metody" defaultstate="collapsed">    
    
    // <editor-fold defaultstate="collapsed" desc="Settery">
    
    @Override
    public void addComponent(rpgcraft.panels.components.Component c) {
        super.addComponent(c);        
    }
    
    /**
     * Metoda ma za ulohu spustit dalsie vlakno s update funkciami.
     */
    public void startGame() {
        if (t==null||running) {
            t= new Thread(this);
            t.start();
        }
    }
    
    /**
     * Threadovo spusti Hru. Nastavi si do premennych FPS pocitadlo. Inicializuje,
     * nastavi vychodzie menu a spusti nekonecny cyklus s updatami a prekreslovanim.
     */    
    @Override
    public void run() {  
        if (!initialized) {
            init();
        }
        Framer.fpsTimer = System.currentTimeMillis();       
        
        // Nastavenie prveho menu.
        setMenu(AbstractMenu.getMenuByName("introMenu"));         
        
        while(running) {
            Framer.tick++;
            //long timeTaken = System.currentTimeMillis();
            //long sleepTime = period - timeTaken;                        
                        
            // !!! Zmenit na prekreslovanie iba ked sa podari update
            update();            
            //Render();
            repaint();
             
            try {
               Thread.sleep(Framer.fpsProhibitor);
            } catch(Exception e) {
               
            }                                                                                  
        }
    }  
         
    /**
      * Metoda nastavi priznak running na false => vykazanie konca hry.
      */
    public void endGame() {
        running = false;
    } 
    
    /**
     * Metoda vymaze vsetky komponenty definovane vnutri v paneli.
     */
    @Override
    public void removeAll() {
        super.removeAll(); 
    }
    
    /**
     * Metoda nastavi vysku a sirku tohoto panelu. Tieto zmeny su volane
     * vacsinou z hlavneho Frame.
     * @param w Nova sirka panelu.
     * @param h Nova vyska panelu.
     */
    @Override
    public void setSize(int w, int h) {
        //mFrame.setSize(w, h);
        super.setSize(w, h);
    } 
    
    /**
     * Dokopy nastavi Width a Height hracieho okna. Hlavne vyuzivane pri aktualizovani
     * komponentu do ktoreho kreslime 
     * @see Class paintComponent,
     * @param x Nastavi velkost dlzky hracieho okna
     * @param y Nastavi velkost sirky hracieho okna
     */    
    public void setWidthHeight(int x,int y) {
        if (menu != null) {
            menu.grChange(true);
            menu.uiChange(true);
            menu.setWidthHeight(x, y);
        }
        
        this.pWidth = x;
        this.pHeight = y;
        this.setSize(x,y);
        
        if (componentContainer != null) {
            componentContainer.set(x, y, 0, 0);
        }                                     
    }
    
    /**
     * Nastavi menu v ktorom pracujeme. Nasledne ho inicializuje s ovladanim hry 
     * a s oknom v ktorom pracujeme.
     * @param menu Menu v ktorom sa nachadzame. Mozme pouzit vsetky podedene Menu
     * @see rpgcraft.panels.Menu 
     * @see rpgcraft.panels.IntroPanel 
     * @see rpgcraft.panels.MainMenu 
     */    
    public void setMenu(AbstractMenu menu) {             
        this.menu = menu;
        if (menu != null) {
            menu.setInitialized(false);
            removeAll();
            menu.ugChange(true);                   
            //menu.recalculate();
            //menu.update();                 
            //updateUI();            
        }
    }
    
    // </editor-fold>
            
    // <editor-fold desc="Update" defaultstate="collapsed">
    
    @Override
    public void addNotify() {
        super.addNotify();       
    }
    
    /** 
     * Aktualizuje hracie okno. V prvom rade kontroluje ci je okno aktivne. Ked je, tak kontroluje uzivatelsky vstup a nasledne
     * aktualizuje okno s obsahom ktory moze byt bud aktualizovanie menu ci aktualizovanie hlavnej hry.
     * V dalsich vetvach <b>update</b> a <b>inputHandling</b> bude update rozdeleny 
     * do nich.
     */         
    @Override
    public void update() {
        if (mFrame.hasFocus()&&!gameOver) {
            if (menu!=null) {
                //System.out.println(Thread.currentThread().getName());
                menu.update();
                if (input.clickedKeys.size() != 0 || input.runningKeys.size() != 0) {
                    System.out.println("Handling Input for Menu");
                    menu.inputHandling();
                    input.freeKeys();
                }
            }            
        }
    }               
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc="Gettery">
    /**
     * Vrati sirku panelu
     * @return 
     */
    @Override
    public int getWidth() {
        return pWidth;
    }    
    
    /**
     * Vrati vysku panelu
     * @return 
     */
    @Override
    public int getHeight() {
        return pHeight;
    }
    
    /**
     * Metoda vrati true/false podla toho ci boli xml subory uz inicializovane.    
     * @return true - prebehla inicializacia, false - neprebehla 
     */
    public boolean hasXmlInitialized() {
        return xmlInitialized;
    } 
    
    /**
     * 
     * @param files 
     */
    public Collection<rpgcraft.panels.components.Container> getChildContainers() {
        return menu.getContainers();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Inicializacie">
         
     /**
      * Metoda inicializuje xml subory. Parametrom <b>files</b> ziskavame adresare v ktorych
      * sa nachadzaju xml subory. Pomocou switch-case zistujeme co za adresar spracovavame
      * a podla mena adresaru volame metodu na inicializaciu toho co sa v adresari nachadza.
      * @param files Adresare odkial sa inicialuzuju xml subory
      * @see GamePane#initializeEntities(java.io.File, java.lang.String) 
      * @see GamePane#initializeImages(java.io.File) 
      * @see GamePane#initializeTiles(java.io.File) 
      * @see GamePane#initializeUI(java.io.File) 
      */
    public void initializeXmlFiles(File[] files) { 
        logger.log(Level.INFO, "Xml files started to initialize...");            
        
        for (File file : files) {            
            switch (file.getName()) {
                case "entities" : initializeEntities(file, EntityXML.MOB);
                    break;
                case "tiles" : initializeTiles(file);
                    break;
                case "items" : initializeEntities(file, EntityXML.ITEM);    
                    break;
                case "ui" : initializeUI(file);
                    break;
                case "images" : initializeImages(file);
                    break;
            }
        }
        
        xmlInitialized = true;  
        
        logger.log(Level.INFO, "Xml files are initialized and ready to use");
    }                 
    
    // </editor-fold>
    
    // <editor-fold desc="Kresliace metody" defaultstate="collapsed">
    
    /**
     * Override metoda paintComponent volana prekreslovacim vlaknom AWT-Thread vtedy ked je potreba
     * alebo pri zavolani metody repaint.
     * Ma za ulohu prekreslit panel podla menu co je vnom aktivovane.      
     */
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);
        if (menu != null) {
            menu.paintMenu(g);  
        }
    }
    // </editor-fold>
        
    //<editor-fold desc="Eventy" defaultstate="collapsed">
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("GamePane: " + getLocation()+ "," + getSize());
        for (Component c : getComponents()) {
            writeAllComponents(c, "");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    // </editor-fold>
    
    // </editor-fold>    
    
}                
