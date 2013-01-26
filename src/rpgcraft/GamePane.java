/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.logging.*;
import javax.swing.*;
import org.w3c.dom.Element;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.panels.*;   
import rpgcraft.panels.components.swing.SwingComponent;
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
    private volatile static boolean running = false;    
    public volatile static boolean gameOver = false;    
    //
    //
    // Sirka a Vyska Panelu s hrou
    private int pWidth;
    private int pHeight;                      
        Component c;
    private Graphics dbg;    
    private volatile Image dbImage = null;
    public InputHandle input;
            
    @Override
    public void fireEvent(ActionEvent event) {
    }

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

    @Override
    public void addActionListener(ActionListener listener) {
    }

    @Override
    public void removeActionListener(ActionListener listener) {
    }
    
    public GamePane() {
        // Inicializacia prazdneho menu
        menu = new BlankMenu();   
        
        menu.initialize(null, input);
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
        
        input = MainGameFrame.input;
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
        mFrame.setSize(w, h);
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
            componentContainer.setComponent(this);
            componentContainer.setTop(true);
            
            IntroPanel intro = new IntroPanel(UiResource.getResource("introMenu"));
            intro.initialize(componentContainer, input);
            logger.log(Level.INFO, "Intro panel set");
            MainMenu main = new MainMenu(UiResource.getResource("mainMenu"));
            main.initialize(componentContainer, input);
            logger.log(Level.INFO, "Main panel set");
            AboutPanel about = new AboutPanel(UiResource.getResource("aboutMenu"));
            about.initialize(componentContainer, input);
            GameMenu game = new GameMenu(UiResource.getResource("gameMenu"));
            game.initialize(componentContainer, input);
            logger.log(Level.INFO, "About panel set");            

            

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
            xmlread.parseXmlFile(xmlfile);
            
            for (Element elem : xmlread.parseElements(res)) {
                EntityResource.newBundledResource(elem);
            }            
        }        
    }
    
    private void initializeTiles(File file) {
        logger.log(Level.INFO, "TileFile: {0}", file.getName());
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles()) {                                     
            
            xmlread.parseXmlFile(xmlfile);
            
            for (Element elem : xmlread.parseElements(TilesXML.TILE)) {
                TileResource.newBundledResource(elem);
            }            
        }
    }
    
    private void initializeImages(File file) {
        logger.log(Level.INFO, "ImageFile: {0}", file.getName());
        XmlReader xmlreader = new XmlReader();    
        for (File xmlfile : file.listFiles()) {
            
            xmlreader.parseXmlFile(xmlfile);
                        
            for (Element elem : xmlreader.parseElements(ImagesXML.IMAGE)) {
                ImageResource.newBundledResource(elem);
            }
        }                    
    }
    
    private void initializeUI(File file) {
        logger.log(Level.INFO, "UIFile: {0}", file.getName());
        XmlReader xmlreader = new XmlReader();    
        for (File xmlfile : file.listFiles()) {
            
            xmlreader.parseXmlFile(xmlfile);
            for (Element elem : xmlreader.parseRootElements(UiXML.ELEMENT)) {
                UiResource.newBundledResource(elem);
            }

        }                    
    }
    
    public void createNewGame() {
        AbstractMenu.getMenuByName("gamemenu").newMapInstance();
    }
    
    public void startGame() {
        if (t==null||!running) {
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
        running = true;        
        
        setMenu(AbstractMenu.getMenuByName("intro"));         
        
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
        }
        
        this.pWidth = x;
        this.pHeight = y;
        
        if (componentContainer != null)
            componentContainer.set(x, y, 0, 0);
        
        dbImage = null;        
        setImageProperties();        
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
            menu.grChange(true);                   
            menu.recalculate();
            menu.update();                 
            updateUI();            
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
                menu.update();
                menu.inputHandling();
            }            
        }
    }
    
    private void setImageProperties() {
        if (dbImage == null) {
            dbImage = createImage(pWidth, pHeight);
            
            if (dbImage == null) {
                new MultiTypeWrn(null, Colors.getColor(Colors.menuError1), "Null dbImage", null).renderSpecific("DbImage in GamePane is null");
                return;
            } else {
                dbg = dbImage.getGraphics();
            }
            
            dbg.setColor(Color.BLACK);
            dbg.fillRect(0, 0, pWidth, pHeight);
            
            if (menu != null) {
                menu.setWidthHeight(pWidth, pHeight);
            }            
        }
    }
    
    @Override
    protected void paintImage(Graphics g, Image dbImage) {               
        // DEBUG!! -- Kontrola Threadu
        //System.out.println(Thread.currentThread().getName());
        
        if (menu != null) {
            if (dbImage == null) {
                return;
            }
            //super.paintComponent(dbImage.getGraphics()); 
            menu.paintMenu(dbImage.getGraphics());                         
            
            if (dbImage != null) {
                g.drawImage(dbImage, 0, 0, null);                
            }
            
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, pWidth, pHeight);
    }

}
    
    
        


    

