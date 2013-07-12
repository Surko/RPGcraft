/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft;

import rpgcraft.plugins.AbstractMenu;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.*;
import javax.swing.*;
import org.w3c.dom.Element;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.generators.MapGenerator;
import rpgcraft.panels.*;   
import rpgcraft.panels.components.swing.SwingBar;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.plugins.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.plugins.Ai;
import rpgcraft.plugins.DataPlugin;
import rpgcraft.plugins.GeneratorPlugin;
import rpgcraft.plugins.ItemGeneratorPlugin;
import rpgcraft.plugins.ScriptLibraryPlugin;
import rpgcraft.resource.ConversationGroupResource;
import rpgcraft.resource.ConversationResource;
import rpgcraft.resource.EffectResource;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.TileResource;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.QuestsResource;
import rpgcraft.resource.RecipeResource;
import rpgcraft.resource.SoundResource;
import rpgcraft.resource.UiResource;
import rpgcraft.scripting.ScriptFactory;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.DefaultDataPlugin;
import rpgcraft.utils.MainUtils;
import rpgcraft.utils.TextUtils;
import rpgcraft.xml.ConversationGroupXML;
import rpgcraft.xml.ConversationXML;
import rpgcraft.xml.EffectXML;
import rpgcraft.xml.EntityXML;
import rpgcraft.xml.TilesXML;
import rpgcraft.xml.ImagesXML;
import rpgcraft.xml.QuestXML;
import rpgcraft.xml.RecipeXML;
import rpgcraft.xml.SoundXML;
import rpgcraft.xml.UiXML;
import rpgcraft.xml.XmlReader;
import rpgcraft.xml.XmlReader.XmlSavePriority;
/**
 * Trieda s hlavnym hracim panelom dediace od nami vytvoreneho SwingImagePanel a implementujuca Runnable
 * pre beh v dalsom vlakne. Sustredujeme v nej zakladne inicializacne metody (metoda globalInit a initialization
 * spolu s inicializaciami pluginov a xml suborov). <br>
 * Kedze trieda dedi od SwingImagePanel (co znamena ze od JPanel) tak moze byt pridana do JFrame vytvoreny v triede
 * MainGameFrame, takisto to znamena ze mozme donho pridavat dalsie komponenty s tym ze pri volani 
 * paintComponent mame zabezpecene vykreslovanie ako tohoto panelu tak aj jeho "detske" komponenty. <br>
 * Tento panel ako jediny z SwingImagePanelov nema definovany resource a je ako jediny ktory vzdy zostava 
 * v rame (JFrame). Viac vlaknovy pristup nam umoznuje vykonavat aktualizacie komponenty, menu, hlavnej hry a 
 * vykreslovanie udajov oddelene => ziadne usekane pristupy. <br>
 * V triede je taktiez vytvoreny zaklad pre reakciu na vstup (mys a klavesnica). 
 * Spominame to preto, lebo reakcie na vstup sa daju definovat z xml do komponent, no
 * treba zarucit spracovanie vstupu aj natvrdo kodovanych v jednotlivych triedach (ako su napriklad
 * AbstractInMenu, ktore nepredstavuje ziadnu komponentu ale priamo napisany kod 
 * vytvarajuci menu v grafickom kontexte, viac v bloku Eventy). Pri klavesnici by 
 * sa to dalo namiesto naseho pristupu vyriesit pridanym KeyListenera do kazdej komponenty. 
 * @see SwingImagePanel
 */
public class GamePane extends SwingImagePanel implements Runnable {
    
    // <editor-fold defaultstate="collapsed" desc="Premenne">
    // Logger na odlogovanie hry
    private static final Logger LOG = Logger.getLogger(GamePane.class.getName());
    // Thread v ktorom bezi hlavna hra, dalsi Thread sa stara o UI prvky atd.
    private Thread t;
    // JFrame alias hlavny frame s panelom v ktorom je hra
    private JFrame mFrame;        
    //Bool hodnota ci bolo initializovane GUI s loggerom 
    private boolean initialized = false;
    // Bool hodnota ci boli initializovane xml subory s entitami a dlazdicami
    private boolean xmlInitialized = false;   
    // Bool hodnota ci boli initializovane pluginy
    private boolean pluginsInitialized = false;
    // Bool hodnota ci boli zmenene velkosti.
    private boolean changedSize = false;
    /**
     * Premenne urcujuce stav hry
     */
    private volatile static boolean running = true,gameOver = false;    
    //
    
    
    // Premenne na cakanie vlakna
    private long startTime, elapsedTime, waitTime;
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
    
    /**
     * Metoda ktora vypise vsetky komponenty ktore boli pridane do hracieho panelu.
     * Najprv vypiseme o aku komponentu sa jedna a potom ked je to list alebo panel
     * tak volame rekurzivne tuto funkciu na vnutorne komponenty.
     * @param c Komponenta o ktorej vypisujeme informacie.
     * @param parString Textovy parameter pridany k textu
     */
    private void writeAllComponents(Component c, String parString) {
        if (c instanceof SwingImageList) {
            System.out.println(parString + " -> list:" +c.getLocation() + "," + c.getSize() + "," + c.isVisible() + "," + c.isEnabled());  
            for (Component _c : ((Container)c).getComponents()) {
                writeAllComponents(_c, parString + " -> list:" +c.getLocation()+ "," + c.getSize());
            }
            return;
        }
        if (c instanceof SwingImageButton) {
            System.out.println(parString + " -> button: " + c.getLocation()+ "," + c.getSize() + "," + c.isVisible()+ "," + c.isEnabled());
        }
        if (c instanceof SwingText) {
            System.out.println(parString + " -> text: " + c.getLocation()+ "," + c.getSize() + "," + c.isVisible()+ "," + c.isEnabled());
        }
        if (c instanceof SwingInputText) {
            System.out.println(parString + " -> edittext: " + c.getLocation()+ "," + c.getSize() + "," + c.isVisible()+ "," + c.isEnabled());
        }
        
        if (c instanceof SwingBar) {
            System.out.println(parString + " -> bar: " + c.getLocation()+ "," + c.getSize() + "," + c.isVisible()+ "," + c.isEnabled());
        }

        if (c instanceof SwingImagePanel) {  
            System.out.println(parString + " -> panel: " + c.getLocation()+ "," + c.getSize() + "," + c.isVisible()+ "," + c.isEnabled());
            for (Component _c : ((Container)c).getComponents()) {                
                writeAllComponents(_c, parString + " -> panel:" +c.getLocation()+ "," + c.getSize());
            }
        }
    }
    
    /**
     * Debug metoda ktora spracujeme vstup z mysi. Rekurzivne vola metodu writeAllComponents 
     * a takymto sposobom vypise vsetky komponenty nachadzajuce sa v hracom paneli
     * a v jeho vnutornych elementoch.
     * Po vypisani udajov o komponentach vypise vsetky ulozene premenne s ulozenymi udajmi
     * z DataUtils.variables.
     * @param e Udalost spracovania mysi.
     */
    private void debugHandle(MouseEvent e) {
        System.out.println("GamePane: " + getLocation()+ "," + getSize());
        for (Component c : getComponents()) {
            writeAllComponents(c, "");
        }
        for (String var : DataUtils.variables.keySet()) {
            System.out.println(var + " = " + DataUtils.getValueOfVariable(var));
        }                
        System.out.println(getComponentCount());
        System.out.println(e.getPoint() + " " + this.getComponentAt(e.getPoint()));
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Inicializacie">
    /**
     * Metoda ktora je ako prva volana na inicializaciu resource a menu.
     * Nastavujeme v nej vysky a sirky pre panel ako aj co za ovladanie budeme pouzivat.
     * Dolezita sucast je volanie metody initialization ktora inicializuje vsetky
     * resource (xml subory, pluginy, ...)
     */
    private void globalInit() {
        if (initialized) {
            return;
        }        
        LOG.log(Level.INFO, StringResource.getResource("initinfo1"));
        LOG.log(Level.INFO, StringResource.getResource("initinfo2"),
                PathManager.getInstance().getRootPath().toString());
        pWidth=MainGameFrame.getContentWidth();
        pHeight=MainGameFrame.getContentHeight();  
        mFrame = MainGameFrame.getFrame();
        setBounds(0, 0, pWidth, pHeight);
        
        setBackground(Color.BLACK);
        setVisible(true);              
        
        input = InputHandle.getInstance();
        menu.initialize(null, input);
        
        LOG.log(Level.INFO, StringResource.getResource("initinfo4"));        
        
        LOG.log(Level.INFO, StringResource.getResource("initinfo5"));
        try {
            initialization();        
        } catch (Exception e) {
            new MultiTypeWrn(e, Color.red, StringResource.getResource("_label_init"),
                    null).renderSpecific(StringResource.getResource("_label_init"));
        }
        
    }        
    
    /**
      * Inicializuje zaciatocne premenne, xml subory a nastavi zakladne typy menu, ktore sa v hre vyskytuju.
      * Vsetky udalosti su obstarane logovanim pre hladani kde sa stala chyba.  
      * @see GamePane#initializeXmlFiles(java.io.File[]) 
      */     
    private void initialization() throws Exception{   
            
            if (!xmlInitialized) {                 
                initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());                                                                           
            } else {
                LOG.log(Level.INFO, StringResource.getResource("alreadyinitinfo5"));
            }
            
            if (!pluginsInitialized) {                
                initializePlugins(PathManager.getInstance().getPluginsPath().listFiles());
                pluginsInitialized = true;                
            } else {
                LOG.log(Level.INFO, StringResource.getResource("alreadyinitinfo7"));
            }
            
            // Nastavenie data pluginov
            DataPlugin.addPlugin(DefaultDataPlugin.getInstance());
            
            componentContainer = new rpgcraft.panels.components.Container(null, this.getWidth(), 
                this.getHeight(), 0, 0, null);
            rpgcraft.panels.components.Container.mainContainer = componentContainer;
            componentContainer.setComponent(this);            
            
            LOG.log(Level.INFO, StringResource.getResource("initinfo6"));
            IntroPanel intro = new IntroPanel(UiResource.getResource("introMenu"));
            intro.initialize(componentContainer, input);
            LOG.log(Level.INFO, "Intro panel set");
            MainMenu main = new MainMenu(UiResource.getResource("mainMenu"));
            main.initialize(componentContainer, input);
            LOG.log(Level.INFO, "Main panel set");
            AboutMenu about = new AboutMenu(UiResource.getResource("aboutMenu"));
            about.initialize(componentContainer, input);
            LOG.log(Level.INFO, "About panel set"); 
            GameMenu game = new GameMenu(UiResource.getResource("gameMenu"));
            game.initialize(componentContainer, input);            
            LOG.log(Level.INFO, "Game panel set"); 
            LoadCreateMenu loadcreate = new LoadCreateMenu(UiResource.getResource("loadcreateMenu"));
            loadcreate.initialize(componentContainer, input);
            LOG.log(Level.INFO, "LoadCreate panel set"); 
           
    }
     
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi generatormi inteligencie. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede Ai pomocou metody addAi.
     * 
     * @param file Zlozka v ktorej sa nachadzaju inteligencne pluginy.
     */
    private void initializeAiPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) {            
            
            try {
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();  
                Ai plugin = (Ai) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                Ai.addAi(plugin);  
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"Ai", Integer.toString(plugLoaded)}));
    }
    
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi generatormi itemov. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede ItemGeneratorPlugin pomocou metody addGenerator.
     * 
     * @param file Zlozka v ktorej sa nachadzaju generatory predmetov.
     */
    private void initializeItemGeneratorPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) {            
            
            try {
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();  
                ItemGeneratorPlugin plugin = (ItemGeneratorPlugin) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                ItemGeneratorPlugin.addGenerator(plugin);  
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"ItemGenerator", Integer.toString(plugLoaded)}));
    }
    
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi generatormi map. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede MapGenerator pomocou metody addGenerator.
     * 
     * @param file Zlozka v ktorej sa nachadzaju generatory map.
     */
    private void initializeMapPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) { 
            
            try {
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();  
                GeneratorPlugin plugin = (GeneratorPlugin) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                MapGenerator.addGenerator(plugin);
                plugin.run();
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"Map", Integer.toString(plugLoaded)}));
    }
    
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi skriptovacimi lua pluginmi. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede ScriptFactory pomocou metody addLibrary.
     * 
     * @param file Zlozka v ktorej sa nachadzaju pluginy skriptov
     */
    private void initializeScriptPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) { 
            
            try {                
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });       
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();                
                ScriptLibraryPlugin plugin = (ScriptLibraryPlugin) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                ScriptFactory.addLibrary(plugin);
                plugin.run();
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"Script", Integer.toString(plugLoaded)}));
    }
    
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi menu pluginmi. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede AbstractMenu pomocou metody addMenu.
     * 
     * @param file Zlozka v ktorej sa nachadzaju menu pluginy.
     */
    private void initializeMenuPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) { 
            
            try {
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();  
                AbstractMenu plugin = (AbstractMenu) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                AbstractMenu.addMenu(plugin);
                plugin.initialize(componentContainer, input);
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"Menu", Integer.toString(plugLoaded)}));
    }
    
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi inmenu pluginmi. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede AbstractInMenu pomocou metody addMenu.
     * 
     * @param file Zlozka v ktorej sa nachadzaju inmenu pluginy.
     */
    private void initializeInMenuPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) { 
            
            try {
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();  
                AbstractInMenu plugin = (AbstractInMenu) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                AbstractInMenu.addMenu(plugin);
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"InMenu", Integer.toString(plugLoaded)}));
    }
    
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi inmenu pluginmi. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede AbstractInMenu pomocou metody addMenu.
     * 
     * @param file Zlozka v ktorej sa nachadzaju inmenu pluginy.
     */
    private void initializeDataPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) { 
            
            try {
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();  
                DataPlugin plugin = (DataPlugin) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                DataPlugin.addPlugin(plugin);
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"InMenu", Integer.toString(plugLoaded)}));
    }
        
    /**
     * Metoda ktora ako ostatne inicializacie pluginov dostava ako parameter zlozku s 
     * moznymi listener pluginmi. Zo zlozky si zoberie vsetky subory a skontroluje pomocou definovaneho
     * filtra ci to je jar subor. Pre jar subory vytvori classloader nacita triedu v zadanom
     * packagi z manifestu a vytvori novu instanciu. Nasledne prida nacitany plugin do nacitanych pluginov 
     * v triede ListenerFactory pomocou metody addListener.
     * 
     * @param file Zlozka v ktorej sa nachadzaju listener pluginy.
     */
    private void initializeListenerPlugins(File file) {
        int plugLoaded = 0;
        for (File f : file.listFiles(MainUtils.jarFilter)) { 
            
            try {
                URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });
                URL url = authorizedLoader.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes manifAttributes = manifest.getMainAttributes();  
                Listener plugin = (Listener) authorizedLoader.loadClass(
                        manifAttributes.getValue("MAINCLASS")).newInstance();
                ListenerFactory.addListener(plugin);
                plugLoaded++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_plugSyn", new String[] {file.getName()}));
            }
        }
        LOG.log(Level.INFO, StringResource.getResource("_plugload", new String[] {"InMenu", Integer.toString(plugLoaded)}));
    }
    
    /**
     * Metoda inicializuje vsetky entity ktore sa nachadzaju v adresari entities.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory. V if podmienke testujeme
     * ci je adresar xml. Ked je tak ho rozparsujeme s tym ze pozname rootelement/parameter <b>res</b>.
     * Root element tu sluzi taku rolu, ze mame dva typy entit: Predmety a NPC/Hraca. Pre kazdy taky 
     * typ mame roznu stavbu xml dokumentu.     
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s entitami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeEntities(File file, String res) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"EntityFile :", file.getName()}));
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {             
                xmlread.parseXmlFile(xmlfile);
                for (Element elem : xmlread.parseElements(res)) {
                    EntityResource.newBundledResource(elem);
                }                                              
        }        
    }
    
    /**
     * Metoda inicializuje vsetky efekty ktore sa nachadzaju v adresari effects.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s efektami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeEffects(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"EffectFile :", file.getName()}));
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {             
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseElements(EffectXML.EFFECT)) {
                EffectResource.newBundledResource(elem);
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
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"ImageFile :", file.getName()}));
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
    
    /**
     * Metoda inicializuje vsetky dlazdice ktore sa nachadzaju v adresari tiles.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s dlazdicami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeTiles(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"TileFile :", file.getName()}));
        XmlReader xmlread = new XmlReader();
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {                                                 
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseElements(TilesXML.TILE)) {
                TileResource.newBundledResource(elem);
            }                                               
        }
    }
            
    /**
     * Metoda inicializuje vsetky ui prvky ktore sa nachadzaju v adresari xml v podadreasi ui.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s ui.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeUI(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"UiFile :", file.getName()}));
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {
            // zistovanie ci je subor xml                        
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseRootElements(UiXML.ELEMENT)) {
                UiResource.newBundledResource(elem);
            }
        }                    
    } 
    
    /**
     * Metoda inicializuje vsetky recept prvky ktore sa nachadzaju v adresari recipes.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s receptami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeRecipes(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"RecipeFile :", file.getName()}));
        
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {
            // zistovanie ci je subor xml            
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseRootElements(RecipeXML.ELEMENT)) {
                RecipeResource.newBundledResource(elem);
            }                                  
        }          
    }
    
    /**
     * Metoda inicializuje vsetky quest ktore sa nachadzaju v adresari quests
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s questami/ulohami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeQuests(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"QuestsFile :", file.getName()}));
        
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {
            // zistovanie ci je subor xml
            // System.out.println(xmlfile.getName().substring(xmlfile.getName().length() - 3));            
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseRootElements(QuestXML.ELEMENT)) {
                QuestsResource.newBundledResource(elem);
            }
        }          
    }
    
    /**
     * Metoda inicializuje vsetky konverzacne grupy ktore sa nachadzaju v adresari conversationgroups.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s konverzacnymi grupami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeGroups(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"ConversationGroupFile :", file.getName()}));
        
        XmlReader xmlread = new XmlReader();    
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {            
            // zistovanie ci je subor xml                        
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseRootElements(ConversationGroupXML.ELEMENT)) {
                ConversationGroupResource.newBundledResource(elem);
            }
        }  
    }
    
    /**
     * Metoda inicializuje vsetky konverzacie ktore sa nachadzaju v adresari conversations.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource s konverzaciami.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeConversations(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"ConversationsFile :", file.getName()}));
        
        XmlReader xmlread = new XmlReader();           
        
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {            
            // zistovanie ci je subor xml           
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseRootElements(ConversationXML.ELEMENT)) {
                ConversationResource.newBundledResource(elem);
            }
        }  
        
        for (File dirFile : file.listFiles(MainUtils.dirFilter)) {
            initializeGroups(dirFile);
        }
    }
    
    /**
     * Metoda inicializuje vsetky zvuky ktore sa nachadzaju v adresari sounds.
     * Parameter <b>file</b> sluzi ako adresar z ktoreho cerpame xml subory.
     * Ked mame xml subor tak ho rozparsujeme s tym ze pozname rootelement/parameter.          
     * @param file Adresar z ktoreho cerpame xml subory na rozparsovanie a vytvorenie resource so zvukmi.
     * @param res Textove pole podla ktoreho urcujeme Root element v xml.
     * @see XmlReader
     */
    private void initializeSounds(File file) {
        LOG.log(Level.INFO, StringResource.getResource("_initxml",
                new String[] {"SoundFile :", file.getName()}));
        
        XmlReader xmlread = new XmlReader();           
        
        for (File xmlfile : file.listFiles(MainUtils.xmlFilter)) {            
            // zistovanie ci je subor xml           
            xmlread.parseXmlFile(xmlfile);
            for (Element elem : xmlread.parseRootElements(SoundXML.SOUND)) {
                SoundResource.newBundledResource(elem);
            }
        }          
    }
             
    // </editor-fold>         
    
    // </editor-fold>           
    
    // <editor-fold desc="Public metody" defaultstate="collapsed">    
    
    // <editor-fold defaultstate="collapsed" desc=" Hlavne run metody ">
    
    /**
     * Metoda ma za ulohu spustit dalsie vlakno s metodami na spracovanie hry ako
     * je update funkcia a zdedena metoda repaint ktora donuti prekreslit kontext
     * panelu.
     */
    public void startGame() {
        if (t==null||running) {
            t= new Thread(this);
            t.start();
        }
    }
    
    /**
     * Threadovo spusti Hru. Ako prve sa inicializuju vsetky udaje pre spravny chod hry.
     * Inicializuju sa xml subory, pluginy, a zakladne menu kontajnery.
     * Nastavi si do premennych FPS pocitadlo, nastavime vychodzie menu
     * a spusti nekonecny cyklus s updatami a prekreslovanim (repaint).
     * Nakonci tela metody uspavame Thread na dobu zadanu v MainUtils. Toto uspanie 
     * ulahcuje procesor od nadmernych updatov => nebezi na 100% => znizene performance <=> fps 
     * na 100 FPS.
     */    
    @Override
    public void run() {  
        // Inicializ
        if (!initialized) {
            globalInit();
        }
        // FPS pocitadlo ulozene v MainUtils.
        MainUtils.fpsTimer = System.currentTimeMillis();       
        System.out.println(SoundResource.getResource("menu"));
        //  ake dlhe bude oneskorenie vlakna
        int delayTime = 40;
        // Nastavenie prveho menu.
        setMenu(AbstractMenu.getMenuByName("introMenu"));         
        
        while(running) {                        
            startTime = System.currentTimeMillis();
            //long timeTaken = System.currentTimeMillis();
            //long sleepTime = period - timeTaken;                        
            //System.out.println(Thread.activeCount());
            // !!! Zmenit na prekreslovanie iba ked sa podari update
            update();                   
            //Render();
            MainUtils.rendered = false;
            repaint();
            waitForPaint();            
            elapsedTime = System.currentTimeMillis() - startTime;
            waitTime = Math.max(delayTime - elapsedTime, MainUtils.fpsProhibitor);
            try {
               Thread.sleep(waitTime);
            } catch(Exception e) {
               
            }              
            MainUtils.TICK++;
        }
        
        LOG.log(Level.INFO, StringResource.getResource("pgmExit"));
        
    }  
      
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Settery">
    
    /**
     * Metoda ktora prida komponentu z projektu do hracieho panelu.
     * @param c Komponenta na pridanie
     */
    @Override
    public void addComponent(rpgcraft.panels.components.Component c) {
        super.addComponent(c);        
    }
        
    /**
     * Metoda je volana z hlavneho cyklu hned po metode repaint ktora odosle
     * event aby sa prekreslovalo. Nevieme ale ci bude zavolana skor alebo neskor
     * ako paint metoda ktora bude volana z event dispatch vlakna. V obidvoch pripadoch
     * to je ale jedno a prekreslovanie bude zosynchronizovane.
     */
    public void waitForPaint() {
        synchronized(this) {
            while (!MainUtils.rendered) {
                try {
                    wait();
                } catch (InterruptedException e) {}
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
            menu.setWidthHeight(x, y);
            menu.grChange(true);
            menu.uiChange(true);            
        }
        
        if (componentContainer != null) {
            componentContainer.set(x, y, 0, 0);
        }  
        
        this.pWidth = x;
        this.pHeight = y;                 
                                                                          
    }
    
    /**
     * Nastavi menu v ktorom pracujeme. Nasledne ho inicializuje s ovladanim hry 
     * a s oknom v ktorom pracujeme.
     * @param menu Menu v ktorom sa nachadzame. Mozme pouzit vsetky podedene Menu
     * @see rpgcraft.plugins.AbstractInMenu
     * @see rpgcraft.panels.IntroPanel 
     * @see rpgcraft.panels.MainMenu 
     */    
    public void setMenu(AbstractMenu menu) {                     
        if (this.menu != null) {
            this.menu.stopPlaying();
        }
        if (menu != null) {             
            componentContainer.setChildContainers(menu.getMenuContainers());
            removeAll();
            menu.setInitialized(false);            
            menu.ugChange(true); 
            menu.startPlaying();
            //menu.recalculate();
            //menu.update();                 
            //updateUI();            
        }
        this.menu = menu;
    }
    
    /**
     * Metoda ktora nastavi priznak changedSize na true co donuti 
     * zmenit velkosti.
     */
    public void setChangedSize() {
        this.changedSize = true;
    }
    
    // </editor-fold>
            
    // <editor-fold desc="Update" defaultstate="collapsed">
        
    /** 
     * Aktualizuje hracie okno. V prvom rade kontroluje ci je okno aktivne. Ked je, tak kontroluje uzivatelsky vstup a nasledne
     * aktualizuje okno s obsahom ktory moze byt bud aktualizovanie menu ci aktualizovanie hlavnej hry.
     * V dalsich vetvach <b>update</b> a <b>inputHandling</b> bude update rozdeleny 
     * do nich.
     */         
    @Override
    public void update() {
        if (mFrame.hasFocus()&&!gameOver) {
            if (changedSize) {
                setWidthHeight(MainGameFrame.getContentWidth(), MainGameFrame.getContentHeight());
                changedSize = false;
            }
            if (menu!=null) {
                //System.out.println(Thread.currentThread().getName());
                menu.update();
                try {
                    if (input.clickedKeys.size() != 0 || input.runningKeys.size() != 0) {                        
                        menu.inputHandling();                        
                        input.freeKeys();
                    }
                } catch (Exception e) {
                    
                }
            }            
        }
    }               
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc="Gettery">
    /**
     * Vrati sirku panelu
     * @return Sirka panela
     */
    @Override
    public int getWidth() {
        return pWidth;
    }    
    
    /**
     * Vrati vysku panelu
     * @return Vyska panela
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
     * Metoda ktora vrati vsetky "detske" kontajnery tohoto hracieho panelu.
     */
    public Collection<rpgcraft.panels.components.Container> getChildContainers() {
        return menu.getContainers();
    }
    
    /**
     * Metoda getActiveMenu vrati aktivne menu s ktorym sa pracuje a ktore sa 
     * aktualne vykresluje v tomto paneli.
     * @return Aktivne menu
     */
    public AbstractMenu getActiveMenu() {
        return menu;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Inicializacie">
         
     /**
      * Metoda inicializuje xml subory. Parametrom <b>files</b> ziskavame adresare v ktorych
      * sa nachadzaju xml subory. <br>
      * Najprv prevedieme usporiadanie podla priorit definovanych x XmlReader v enume XmlSavePriority.
      * Prechadzame usporiadane pole a pomocou switch-case zistujeme co za adresar spracovavame
      * a podla mena adresaru volame metodu na inicializaciu toho co sa v adresari nachadza.
      * @param files Adresare odkial sa inicialuzuju xml subory
      * @see GamePane#initializeEntities(java.io.File, java.lang.String) 
      * @see GamePane#initializeImages(java.io.File) 
      * @see GamePane#initializeTiles(java.io.File) 
      * @see GamePane#initializeUI(java.io.File) 
      */
    public void initializeXmlFiles(File[] files) throws Exception { 
        LOG.log(Level.INFO, StringResource.getResource("_xmlinitstart"));            
        
        
        // Najprv preusporiadame xmlka podla priority nacitavania        
        File[] priorFiles = new File[XmlSavePriority.values().length];
        for (File file : files) {
            int priority = XmlSavePriority.valueOf(file.getName()).getPriority();
            priorFiles[priority] = file;            
        }
        
        for (File file : priorFiles) {
            if (file == null) {
                continue;
            }
            
            XmlSavePriority xml = null;
            
            try {
                xml = XmlSavePriority.valueOf(file.getName());            
            } catch (Exception e) {                
                LOG.log(Level.WARNING, StringResource.getResource("_rxml", new String[] {file.getName()}));
                continue;
            }
            
            switch (xml) {
                case effects : initializeEffects(file);
                    break;
                case entities : initializeEntities(file, EntityXML.MOB);
                    break;
                case tiles : initializeTiles(file);
                    break;
                case images : initializeImages(file);
                    break;
                case items: initializeEntities(file, EntityXML.ITEM);    
                    break;
                case ui : initializeUI(file);
                    break;                
                case recipes : initializeRecipes(file);
                    break;
                case quests : initializeQuests(file);
                    break;
                case conversations : initializeConversations(file);
                    break;                  
                case sounds : initializeSounds(file);
                    break;
                default : break;
            }
        }
        
        xmlInitialized = true;  
        
        LOG.log(Level.INFO, StringResource.getResource("_xmlinitdone"));
    }                 
    
    /**
     * Metoda ktora inicializuje pluginy. Medzi pluginy patria generatory od map, menu
     * ale aj skriptovacie moduly do lua, ci listenery.     
     */
    public void initializePlugins(File[] files) {
        // Inicializacia pluginov ako generatory map tak aj skriptovacie pluginy.
        LOG.log(Level.INFO, StringResource.getResource("_pluginitstart"));      
        
        for (File f : files) {
            
            if (!f.isDirectory()) {
                LOG.log(Level.INFO, StringResource.getResource("_ndir", new String[] {f.getName()}));
                return;
            }
            
            switch (f.getName()) {
                case PathManager.AIPLUG : {
                    initializeAiPlugins(f);
                } break;
                case PathManager.MAPLUG : {
                    initializeMapPlugins(f);
                } break;
                case PathManager.SCRIPTPLUG : {
                    initializeScriptPlugins(f);
                } break;
                case PathManager.INMENUPLUG : {
                    initializeInMenuPlugins(f);
                } break;
                case PathManager.MENUPLUG : {
                    initializeMenuPlugins(f);
                } break;
                case PathManager.ITEMGENPLUG : {
                    initializeItemGeneratorPlugins(f);
                } break;
                case PathManager.LISTPLUG : {
                    initializeListenerPlugins(f);
                } break; 
                case PathManager.DATAPLUG : {
                    initializeDataPlugins(f);
                } break;     
                default : break;                    
            }
            
        }
        
        LOG.log(Level.INFO, StringResource.getResource("_pluginitdone"));                         
        
    }
    
    // </editor-fold>
    
    // <editor-fold desc="Kresliace metody" defaultstate="collapsed">
    /**
     * Metoda paint zavolana pri kazdom volani v event dispatch vlakne. Vacsinou vyvolana
     * pri volani repaint. Vykonaju sa v nej kreslenia iba vtedy ked je MainUtils.rendered nastaveny na false,
     * co zabezpeci, ze nebudu vykreslovane take poziadavky ktore boli vyvolane
     * z dispatch vlakna.
     * <p>
     * Synchronizujeme tu na objekte this kde nastavujeme ze sa zrenderovalo takze musime notifikovat
     * hlavne vlakno, ktore uz caka na monitor tohoto objektu aby sa mohol prebudit
     * a pokracovat v metode waitForPaint a dalej v hlavnom cykle.
     * </p>
     * @param g 
     */
    @Override
    public void paint(Graphics g) {
        if (!MainUtils.rendered) {
            super.paint(g);
            
            synchronized (this) {
                MainUtils.rendered = true;
                notify();
            }
        }  
    }
            
    /**
     * Override metoda paintComponent volana prekreslovacim vlaknom AWT-Thread vtedy ked je potreba
     * alebo pri zavolani metody repaint. Taktiez nastavuje fontRenderContext v TextUtils
     * aby sme dokazali urcovat vysky a sirky textov pri neskorsich urcovaniach velkosti.
     */
    @Override
    public void paintComponent(Graphics g) {   
        if (TextUtils.ctx == null) {
            TextUtils.ctx = ((Graphics2D)g).getFontRenderContext();
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, pWidth, pHeight);                            
    }
    // </editor-fold>
        
    //<editor-fold desc="Eventy" defaultstate="collapsed">
    /**
     * Metoda vyvolana pri stlaceni mysi v komponente. Pri Debug mode volame metodu debugHandle s parametrom 
     * Event. Pri existencii menu volame metodu mouseHandling s eventom.
     * @param e Udalost z mysi
     */
    @Override
    public void mouseClicked(MouseEvent e) {                  
        
        if (MainUtils.DEBUG) {
            debugHandle(e);
        }
        
        if (menu != null) {
            menu.mouseHandling(e);
        }
    }

    /**
     * Prazdna metoda vyvolavana celu dobu stlacenia mysi na komponente
     * @param e Udalost z mysi
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Prazdna metoda vyvolana pri uvolneni tlacidla mysi na komponnente.
     * @param e Udalost z mysi
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Prazdna metoda vyvolana pri pohybe mysi do komponenty.
     * @param e Udalost z mysi
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Prazdna metoda vyvolana pri pohybe mysi von z komponenty.
     * @param e Udalost z mysi
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    // </editor-fold>
    
    // </editor-fold>    
    
}                
