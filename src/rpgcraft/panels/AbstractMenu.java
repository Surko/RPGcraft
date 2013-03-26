/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;
import rpgcraft.GamePane;
import rpgcraft.errors.MissingFile;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingCustomButton;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.Framer;
import rpgcraft.utils.MathUtils;

/**
 * Abstraktna trieda MenuOrig ma za ulohu vytvorit spolocny interface pre vsetky
 * triedy, ktore by chceli vytvorit nove menu. Implementuje interface Menu.
 * Trieda je abstraktna, kedze nechcem aby niekto vytvaral 
 * instancie MenuOrig ale iba potomkov.
 * @author Kirrie
 */

public abstract class AbstractMenu implements Menu<AbstractMenu> {         
    
    protected static HashMap<String, AbstractMenu> menuMap = new HashMap<>();
    
    // ABSTRACT CLASSES
    
    
    public abstract void setWidthHeight(int w, int h);
    
    protected Container gameContainer;
    protected GamePane gamePane;
    protected UiResource res;
    protected InputHandle input;
    protected GridBagConstraints c;
    protected ArrayList<UiResource> scrollingResource;
    protected ArrayList<Container> containers;
    // Neutriedena Mapa zadana pomocou linkedHashMap.
    protected ConcurrentHashMap<UiResource, Container> uiContainers;
    protected boolean changedUi;
    protected boolean changedGr;
    protected Image contImage;
    protected boolean changedInit;
    protected boolean initialized;
    
    
    public void initialize(Container gameContainer,InputHandle input) {
        if (res == null) {
            new MultiTypeWrn(null, Color.red, "There is no resource associated with this menu", null).renderSpecific("Menu Error");
        }
        
        this.gameContainer = gameContainer;
        this.gamePane = (GamePane)gameContainer.getComponent();
        this.input = input;                                       
        
        //contImage = new BufferedImage(gameContainer.getWidth(), gameContainer.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        
        //uiContainers = Collections.synchronizedMap(new LinkedHashMap<UiResource,Container>());    
        uiContainers = new ConcurrentHashMap<>();
        
        initializeMenuResource();
    }        
         
    private void initializeMenuResource() {
        /*
         * Ked je originalny resource skrolovaci tak ho priradi do pola so 
         * skrolovacimi resource.
         */
        if (res.isScrolling()) {
            if (scrollingResource == null) {                                    
                scrollingResource = new ArrayList<>();
            }            
            scrollingResource.add(res);
        }
                
        /*
         * Do pola sa zhromazdia dlzky kontaineru 
         */
        int[] lengths = MathUtils.getLengths(res, gameContainer);
        
        
        // Pridame poziciu resource z ktoreho vytvarame menu (aj ked je tento resource
        // nejaky z java layout. V takom pripade sa skrolovanie neberie v uvahu.
        Container cont = new Container(res, lengths[0], lengths[2],
                lengths[1], lengths[3], gameContainer); 
        
        uiContainers.put(res, cont);                
        
        cont.setChildContainers(getUI(res));
        
        initializeImage();
        
        containers = gameContainer.getChildContainer();
                
    }
    
    /**
     * Metoda getUI ziskava informacie o resource a jeho potomkoch a uklada si ich
     * do HashMapy pre lahsi pristup.
     * @param resource 
     */
    private ArrayList<Container> getUI(UiResource resource) {
        if (resource.getType().getElements() != null) {            
            ArrayList<Container> containers = new ArrayList<>();
            for (UiResource _res : resource.getType().getElements()) {
                int[] lengths = MathUtils.getLengths(_res, uiContainers.get(resource));
                
                if (_res.isScrolling()) {
                    if (scrollingResource == null) {                        
                        scrollingResource = new ArrayList<>();
                    }
                    scrollingResource.add(_res);
                }        
            Container cont = new Container(_res,
                    lengths[0], lengths[2], lengths[1], lengths[3], uiContainers.get(resource));
            containers.add(cont);
            uiContainers.put(_res, cont);
            cont.setChildContainers(getUI(_res));            
            
            }
            return containers;
        }
        return null;
    }
    
    
    public void recalculate() {
        recalculate(res);        
    }
    /**
     * Metoda recalculate prepocitava pozicie resource pre toto menu, kedze 
     * uzivatel moze zvacsovat okno a bez tejto metody by zostali vnutorne prvky
     * v originalnych velkostiach.
     */
    protected void recalculate(UiResource resource) {
        int[] lengths = MathUtils.getLengths(resource, uiContainers.get(resource).getParentContainer());
        
        Container cont = uiContainers.get(resource);
        cont.set(lengths[0], lengths[2], lengths[1], lengths[3]);
        if (resource.getType().getElements() != null) {
            for (UiResource _res : resource.getType().getElements()) {
                    recalculate(_res);                    
                }
        }
    }
    
    protected void recalculate(Container cont) {
        int[] lengths = MathUtils.getLengths(cont.getResource(), cont.getParentContainer());
                
        cont.set(lengths[0], lengths[2], lengths[1], lengths[3]);
        if (cont.getChildContainer() != null) {
            for (Container _cont : cont.getChildContainer()) {
                    recalculate(_cont);                    
                }
        }
    }
    
    protected void initializeUI() {        
        if (uiContainers.isEmpty()) {
            return;
        }
        
        if (!initialized) {
            recalculate(res); 
            gamePane.add(Framer.frameLabel);
            reinitialize(getContentGraphics(), res, gameContainer);
            initialized = true;
        } else {        
            for (Container contToCalc : gameContainer.getChildContainer()) {
                recalculate(contToCalc);
                refreshElements(getContentGraphics(), contToCalc, gamePane);
            }
        }
        gamePane.updateUI();
        changedUi = false;
    }
    
    private Graphics getContentGraphics() {
        if (contImage == null) {
            return null;
        }
        Graphics g = contImage.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, gamePane.getWidth(), gamePane.getHeight());        
        return g;
    }
    
    protected void initializeGraphics() {                                
        changedGr = false;   
        
    }
    
    /**
     * Metoda initializeImage initializuje obrazok este predtyn nez sa vykresluje. 
     * Tymto sposobom nam opada inicializacia pri kazdom vykreslovani co by spomalovalo
     * aplikaciu. Initializuje sa iba ked je <b>changedGr</b> = True, co znamena ze
     * treba prekreslit graficky kontext.
     * Pri zhode <b>changedGr</b> = True je treba prepocitat vsetky prvky v tomto menu
     * aby zodpovedali novym velkostiam a rozostaveniu ostatnych komponent.
     */
    protected void initializeImage() {
        if (changedUi) {
            initializeUI();            
        }
        if (changedGr) {
            initializeGraphics();            
        }
    }                                           
    
    /**
     * Ked sa zmenil kontainer alebo ked je potreba preinitializovat tak prekresluje nanovo vsetky
     * komponenty.
     * @param g
     * @param resource 
     */    
    protected void reinitialize(Graphics g, UiResource resource, Container parent) {
        Container cont = uiContainers.get(resource);    
        cont.setChanged(false);
                        
        DataUtils.getComponentFromResource(resource, this, cont, parent);        
                        
        /**        
        Rekurzivne prechadzam metodu paintElement s podelementami resource                        
        Ked je resource typu panel tak metoda je volana s tymto panelom ako 
        komponentov, inak je volana s parametrom comp. Tymto si zachovavam
        kazdy resource ako kontainer pre podelementy.
        
        if (resource.getType().getElements() != null) {
            for (UiResource _resource : resource.getType().getElements()) {
                if (cont.getComponent() instanceof JPanel) {
                    reinitialize(g, _resource, (JPanel)cont.getComponent());
                    cont.getComponent().update();
                } else {
                    reinitialize(g, _resource, comp);
                }
            }
        }
        */
        
        if (resource.getType().getElements() != null) {
            for (UiResource _resource : resource.getType().getElements()) {
                if (cont.getComponent() instanceof JPanel) {
                    reinitialize(g, _resource, cont);
                    cont.getComponent().update();
                } else {
                    reinitialize(g, _resource, parent);
                }
            }
        }
        
        /*
         * Refresh komponent <=> urcenie vysok a sirok podla ostatnych elementov.
         * Prebieha postorder aby sme zarucili ze ostatne podelementy tohoto elementu
         * uz boli refreshnute a tym ziskali spravne velkosti.
         */
        if (cont.getComponent() != null) {
            cont.getComponent().refresh();
        }                      
                        
    }
    
    protected void refreshElements(Graphics g, Container cont, JPanel comp) {                
        if ((cont.isChanged())) {             
            cont.setChanged(false);
            if (cont.getChildContainer() != null) {
                for (Container _cont : cont.getChildContainer()) {
                    if (cont.getComponent() instanceof JPanel) {
                        refreshElements(g, _cont, (JPanel)cont.getComponent());
                        cont.getComponent().update();
                    } else {
                        refreshElements(g, _cont, comp);
                    }
                }
            }            
            /*
             * Refresh komponent <=> urcenie vysok a sirok podla ostatnych elementov.
             * Prebieha postorder aby sme zarucili ze ostatne podelementy tohoto elementu
             * uz boli refreshnute a tym ziskali spravne velkosti.
             */
            if (cont.getComponent() != null) {
                cont.getComponent().refresh();
            }
        } else {
            if (cont.getComponent()!= null)
            cont.getComponent().refreshPositions(cont.getWidth(), cont.getHeight(),
                    cont.getParentWidth(), cont.getParentHeight());
        }                
        
    }

    
    /**
     * Metoda loadImage nacita z ImageResource obrazok zadany parametrom iFile.
     * Dodatocne parametre urcuju co za farbu pozadia sa pouzije pri chybe a spravu
     * pre uzivatela.
     * @param iFile Text s imageresource na nacitanie obrazku
     * @param msg Sprava pre uzivatela pri chybe
     * @return Obrazok pre menu
     */
    public Image loadImage(String iFile, String msg) {        
        try {            
        Image out = ImageResource.getResource(iFile).getBackImage();
        return out;
        } catch(Exception e) {                
             new MissingFile(e, msg).render();  
             return null;
        }        
    }
    
    /**
     * Metoda loadResourceImage je podobna ako loadImage. V tomto pripade ale obrazok
     * ziskava priamo z resource zadanom parametrom resource. Dodatocne parametre urcuju co za farbu pozadia sa 
     * pouzije pri chybe a spravu pre uzivatela.
     * @param resource ImageResouce na nacitanie obrazku
     * @param msg Sprava pre uzivatela pri chybe
     * @return Obrazok pre menu
     */
    public Image loadResourceImage(ImageResource resource, String msg) {
        try {
            Image out = resource.getBackImage();
            return out;
        } catch(Exception e) {                
             new MissingFile(e, msg).render();  
             return null;
        }   
    }    

    public static AbstractMenu getMenuByName(String name) {
        return menuMap.get(name);
    }
    
    /**
     * Metoda getGamePanel vracia panel v ktorom je toto menu vytvorene
     * @return Panel s hrou
     */
    public GamePane getGamePanel() {
        return gamePane;
    }
    
    /**
     * Metoda ktora indikuje stav ci sa bude menit graficky kontext
     * pri dalsom update.
     * @param state True/False podla toho aky bude stav
     */
    public void grChange(boolean state) {
        changedGr = state;
    }
    
    /**
     * Metoda ktora indikuje stav ci sa zmenilo GUI (zvacsenie okna, alebo dalsie 
     * operacie s oknom)
     * @param state True/False podla toho aky bude stav
     */
    public void uiChange(boolean state) {
        changedUi = state;
    }
    
    /**
     * Metoda ktora spaja funkcnost metod uiChange a grChange.
     * @param state True/False podla toho aky bude stav
     */
    public void ugChange(boolean state) {
        changedGr = state;
        changedUi = state;
    }
    
    public void reinitializeMenu() {        
        initializeMenuResource();
        initialized = false;
        ugChange(true);
    }
    
    public Collection<Container> getContainers() {
        return uiContainers.values();
    }
    
    public Container getContainer(UiResource resource) {        
        return uiContainers.get(resource);
    }
    
    public ArrayList<Container> getMenuContainers() {
        return containers;
    }
    
    public boolean hasContainer(UiResource resource) {
        return uiContainers.containsKey(resource);
    }
    
    public void addContainer(Container cont) {
        if (!uiContainers.containsKey(cont.getResource())) {
            uiContainers.put(cont.getResource(), cont);
            changedUi = true;
            changedGr = true;
        }
    }
    
    public void removeContainer(UiResource res) {
        Container cont = uiContainers.get(res);
        if (cont != null) {
            cont.getParentContainer().removeContainer(cont);
            uiContainers.remove(res);
            changedUi = true;
            changedGr = true;
        }
    }
    
    public void removeContainerTemp(UiResource res) {
        Container cont = uiContainers.get(res);
        if (cont != null) {
            cont.getParentContainer().removeContainer(cont);
            changedUi = true;
            changedGr = true;
        }
    }
    
    public void removeAllContainers() {
        uiContainers.clear();
        gameContainer.clear(); 
        changedUi = true;
        changedGr = true;
    }
    
    @Override
    public void setMenu(AbstractMenu menu) {
        gamePane.setMenu(menu);        
    }        
    
    public void setInitialized(boolean init) {
        this.initialized = init;
    }
    
    @Override
    public void update() {  
       if (scrollingResource != null) {
            for (UiResource resource : scrollingResource) {
                uiContainers.get(resource).increase(resource.getScrollX(), resource.getScrollY());                
            }            
        }
       this.initializeImage();
    }      
    
    
    
    /**
     * Metoda s interfacu Menu zabezpecujuca uzivatelsky vstup.
     */
    @Override
    public void inputHandling() {
        for (Container cont : uiContainers.values()) {
            if (cont.getComponent().isVisible()) {
                cont.getComponent().processKeyEvents(input);
            }
        }
    }

    @Override
    public void paintMenu(Graphics g) {
        
    }
    
}
