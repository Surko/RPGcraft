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
import java.util.logging.*;
import javax.swing.*;
import org.w3c.dom.Element;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.panels.*;   
import rpgcraft.panels.components.swing.SwingImagePanel;
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
    //
    // Sirka a Vyska Panelu s hrou
    private int pWidth;
    private int pHeight;                      
    private Component c;
  
    public InputHandle input;
            

    @Override
    public void mouseClicked(MouseEvent e) {
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
        
        backColor = Colors.getColor(Colors.Black);   
                
    }
    
    private void init() {
        if (initialized) {
            return;
        }
        initLogger();
        logger.log(Level.INFO, StringResource.getResource("initinfo1"));
        logger.log(Level.INFO, StringResource.getResource("initinfo2"),
                PathManager.getInstance().getRootPath().toString());
        pWidth=MainGameFrame.Fwidth;
        pHeight=MainGameFrame.Fheight;  
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
             
    @Override
    public void setSize(int w, int h) {
        //mFrame.setSize(w, h);
        super.setSize(w, h);
    }
    
    public boolean hasXmlInitialized() {
        return xmlInitialized;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();       
    }
    
         
     /**
      * Inicializuje zaciatocne premenne a preposle nastavenia o Menu.
      * 
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
    
    private void initializeEntities(File file, String res) {
        logger.log(Level.INFO, "EntFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles()) { 
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
            }
            
            for (Element elem : xmlread.parseElements(res)) {
                EntityResource.newBundledResource(elem);
            }            
        }        
    }
    
    private void initializeTiles(File file) {
        logger.log(Level.INFO, "TileFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles()) {                                     
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
            }
            
            for (Element elem : xmlread.parseElements(TilesXML.TILE)) {
                TileResource.newBundledResource(elem);
            }            
        }
    }
    
    private void initializeImages(File file) {
        logger.log(Level.INFO, "ImageFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles()) {
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
            }
                        
            for (Element elem : xmlread.parseElements(ImagesXML.IMAGE)) {
                ImageResource.newBundledResource(elem);
            }
        }                    
    }
    
    private void initializeUI(File file) {
        logger.log(Level.INFO, "UIFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles()) {
            System.out.println(xmlfile.getName().substring(xmlfile.getName().length() - 3));
            if (xmlfile.getName().substring(xmlfile.getName().length() - 3).equals("xml")) {
                xmlread.parseXmlFile(xmlfile);
            }
            for (Element elem : xmlread.parseRootElements(UiXML.ELEMENT)) {
                UiResource.newBundledResource(elem);
            }

        }                    
    }    
    
    public void startGame() {
        if (t==null||running) {
            t= new Thread(this);
            t.start();
        }
    }
    
    /**
     * Threadovo spusti Hru. Nastavi si do premennych FPS pocitadlo. Inicializuje,
     * nastavi vychodzie menu a spusti nekonecny cyklus.
     */
    
     @Override
    public void run() {  
        if (!initialized) {
            init();
        }
        Framer.fpsTimer = System.currentTimeMillis();       
        
        setMenu(AbstractMenu.getMenuByName("introMenu"));         
        
        while(running) {
            Framer.tick++;
            //long timeTaken = System.currentTimeMillis();
            //long sleepTime = period - timeTaken;                        
            
            update();            
            //Render();
            repaint();
             
            try {
                Thread.sleep(Framer.fpsProhibitor);
            } catch(Exception e) {
               
            }                                                                                  
        }
    }  
         
    public void endGame() {
        running = false;
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
    
    @Override
    public int getWidth() {
        return pWidth;
    }
    
    @Override
    public int getHeight() {
        return pHeight;
    }

    @Override
    public void removeAll() {
        super.removeAll(); 
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
            removeAll();
            menu.ugChange(true);                   
            //menu.recalculate();
            //menu.update();                 
            //updateUI();            
        }
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
                menu.inputHandling();
            }            
        }
    }               
    
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);
        if (menu != null) {
            menu.paintMenu(g);  
        }
    }

}
    
    
        


    

