/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft;

import java.awt.Color;
import java.util.logging.Logger;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import javax.swing.JFrame;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.MainUtils;

/**
 * Hlavny hraci ram pre RPGcraft. Trieda v sebe obsahuje vytvorenie hracieho ramu 
 * s inicializaciou loggera, ktory presmeruvava vstup kazdeho definovaneho loggera v inych
 * triedach sem (viac {@link #initLogger()}). Vytvoreny ram ma zaciatocnu fixnu 
 * velkost 800x600 definovanu v premennych tejto triedy. Do ramu pridavame hraci panel
 * v ktorom sa odohrava cela logika hry. Takisto do ramu pridavame reakciu na zmenu komponenty 
 * metodou addComponentListener. <br>
 * Po initializacii zavolame metodu startGame na hraci panel ktora vytvori Thread a spusti
 * instanciu s hrou. 
 * <pre><p>
 * 20.7 1st revision
 * 1.8 2nd revision
 * 20.8 3rd revision
 * 31.8 4rd revision
 * xx.1 - 18.3 5rd revision
 * 19.3 - xx.6 last revision
 * </p></pre>
 * @see GamePane 
 * @author Kirrie
 */
public class MainGameFrame {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Text s neodchytitelnou vynimkou
     */
    private final static String UNCAUGHT = "Missing Locales = Uncaught Exception!";
    /**
     * Text s nazvom okna
     */
    private static final String RPGCRAFT = "RPGcraft!";
    /**
     * Logger pre celu hru
     */
    private final static Logger LOG = Logger.getLogger(MainGameFrame.class.getPackage().getName());       
    /**
     * Definovane velkosti okna
     */
    public static final int Fwidth = 800, Fheight = 600;        
    /**
     * okno v ktorom bude panel s hrou
     */
    private static JFrame mFrame;
    /**
     * Panel s hrou
     */
    public static GamePane game;        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">        
    
    /**
     * Metoda ktora inicializuje logger. Odlisnost od obycajneho vytvorenie instancie loggeru je ta
     * ze definujeme co s loggerom, kam sa bude log ukladat (getLogPath), pod akym meno vystupovat
     * (FileHandler konstruktor). Vsetky to nastavujeme iba v pripade ked sme uz nenacitali 
     * nejaky config file z disku. Ked sme nacitali tak pracujeme s definovanymi vlastnostami
     * podla suboru.
     * Kazdy dalsi definovany logger od zavolania tejto metody bude presmerovavat svoj vystup
     * do suboru definovaneho v tejto metode. To zarucujeme tym ze meno tohoto loggeru
     * je rpgcraft a vsetky ostatne loggery su rpgcraft.xxx => presmeruvavaju vstup do rodica
     * a ked sa dostanu sem tak vypiseme log do suboru a konzole ako treba
     * @see FileHandler
     */
    private static void initLogger() {
        /* Kontrolovanie spustenia hry v debug mode.
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
        */                     
        File dirPath = PathManager.getInstance().getRootPath();      

        if (LOG.getHandlers() == null || LOG.getHandlers().length == 0) {
            try {               
                ConsoleHandler ch = new ConsoleHandler();            
                ch.setLevel(Level.SEVERE);
                ch.setFormatter(new SimpleFormatter());            
                FileHandler fh = new FileHandler(new File(dirPath, "RPGcraft.log").getAbsolutePath(), false);
                fh.setLevel(Level.ALL);
                fh.setFormatter(new SimpleFormatter());                        
                LOG.setUseParentHandlers(false);            
                LOG.addHandler(fh);
                LOG.addHandler(ch);

            } catch (IOException ex) {
                LOG.log(Level.WARNING, ex.toString(), ex);
            }        
        }
    }
       
    /**
     * Metoda ktora nacita Textove retazce zo suboru nachadzajuci sa v adreasi locales
     * na disku. Cestu si vyberie z PathManager
     * @throws Exception Vynimka pri chybe nacitania
     */
    private static void loadStringResource() throws Exception {  
        StringResource.initializeResources();
    }
    
    /**
     * Metoda ktora ukonci hru zavolanim metody endGame v hracom paneli.
     */
    public static void endGame() {
        if (game != null) {
            game.endGame();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
     /**
     * Trieda getFrame ktora pri volani vrati frame v ktorom
     * prebieha hra
     * @return Frame s panelom s hrou.
     */
    public static JFrame getFrame() {
        return mFrame;
    }
    
    /**
     * Metoda ktora vrati sirku content/obsahoveho okna (okno bez vrchnej listy)
     * @return Sirka content okna
     */
    public static int getContentWidth() {
        return mFrame.getContentPane().getWidth();
    }
    
    /**
     * Metoda ktora vrati vysku content/obsahoveho okna (okno bez vrchnej listy)
     * @return Vyska content okna
     */
    public static int getContentHeight() {
        return mFrame.getContentPane().getHeight();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Definovane Listenery ">
    // Triedy        
    /**
     * Staticka trieda implementujuca ComponentListener, 
     * ktora ma za ulohu menit velkost obrazovky pri kazdom
     * zvacsovani ci zmensovani obrazovky (frame) mysou v hre.
     * @see ComponentListener
     * 
     */
    static class UpdateLengths implements ComponentListener {
        /**
         * Metoda reagujuca na zvacsovanie okna. Pri Zmene okna sa meni aj velkost
         * ramu s obsahom.
         * @param e KomponentEvent ktory vyvolal metodu
         */
        @Override
        public void componentResized(ComponentEvent e) {
            game.setChangedSize();          
        }
        
        /**
         * Metoda reagujuca pri pohybe okna. (Prazdna)
         * @param e KomponentEvent ktory vyvolal metodu
         */
        @Override
        public void componentMoved(ComponentEvent e) {            
        }

        /**
         * Metoda reagujuca na objavenie okna. (prazdna)         
         * @param e KomponentEvent ktory vyvolal metodu
         */
        @Override
        public void componentShown(ComponentEvent e) {
        }

        /**
         * Metoda reagujuca na schovani okna. (prazdna
         * @param e KomponentEvent ktory vyvolal metodu
         */
        @Override
        public void componentHidden(ComponentEvent e) {
        }        
    }                    
    
    // </editor-fold>
    
    /**
     * Hlavna main metoda celej hry, ktora initializuje texty, logger,
     * cesty k suborom, hlavny ram a panel s hrou. Parametre args posuvame
     * do PathManageru ktory rozhoduje podla tychto parametrov ako vytvarame cesty.
     * Pri normalnom spusteni nehraju parametre rolu, pri spusteni v IDE je dobre mat v aspon nieco
     * v argumentoch aby dokazal PathManager rozhodnut ze cerpane cesty z adresara build/classes.
     * @param args Parametre s prikazoveho riadku.
     */    
    public static void main(String[] args) {        
        try {   
            PathManager.getInstance(args); 
            initLogger(); 
            mFrame = new JFrame(RPGCRAFT);                                         
            
            mFrame.addWindowListener(null);
            mFrame.setBackground(Color.BLACK);
            mFrame.setBounds(0, 0, Fwidth, Fheight);
            mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mFrame.addComponentListener(new UpdateLengths());

            game = new GamePane();  
            game.setLayout(null);        
            game.add(MainUtils.FPSCOUNTER);                 

            mFrame.getContentPane().add(game);
            mFrame.addKeyListener(InputHandle.getInstance());        
            mFrame.setVisible(true);
            
            loadStringResource();  
            game.startGame();            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, UNCAUGHT, e);
            new MultiTypeWrn(e, Color.red, UNCAUGHT, null).renderSpecific(UNCAUGHT);
        }
    }
    
}
