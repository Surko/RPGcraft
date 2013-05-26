/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;
import rpgcraft.GamePane;
import rpgcraft.errors.MissingFile;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ui.menu.Menu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MainUtils;
import rpgcraft.utils.MathUtils;

/**
 * Abstraktna trieda MenuOrig ma za ulohu vytvorit spolocny interface pre vsetky
 * triedy, ktore by chceli vytvorit nove menu. Implementuje interface Menu.
 * Trieda je abstraktna, kedze nechcem aby niekto vytvaral 
 * instancie MenuOrig ale iba potomkov.
 * @author Kirrie
 */

public abstract class AbstractMenu implements Menu<AbstractMenu> {         
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    protected static HashMap<String, AbstractMenu> menuMap = new HashMap<>();
        
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    // ABSTRACT CLASSES        
    public abstract void setWidthHeight(int w, int h);            
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update + Init ">
    
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

    
    public void initialize(Container gameContainer,InputHandle input) {
        if (res == null) {
            new MultiTypeWrn(null, Color.red, "There is no resource associated with this menu", null).renderSpecific("Menu Error");
        }
        
        this.gameContainer = gameContainer;
        this.gamePane = (GamePane)gameContainer.getComponent();
        this.input = input;
        this.containers = new ArrayList<>();
        
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
    
    protected void initializeUI() {        
        if (uiContainers.isEmpty()) {
            return;
        }
        
        if (!initialized) {
            recalculate(res); 
            // Pridanie Frameru - Mozne nahradit aby sa pridaval iba ked sa stlaci nejake tlacidlo                        
            reinitialize(getContentGraphics(), res, gameContainer); 
            gamePane.add(MainUtils.FPSCOUNTER, 0);            
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
        
        // Ked je parent nahodou GamePane tak pri kazdej initializacii v AbstractMenu len pridavame
        // dalsi kontajner => treba sa ich zbavovat po kazdom prehodeni menu => Nastavenie v GamePane#setMenu.
        parent.addChildComponents();        
                        
    }    
    
    public void reinitializeMenu() {        
        initializeMenuResource();
        initialized = false;
        ugChange(true);
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
            if (cont.getComponent()!= null) {
                cont.getComponent().refreshPositions(cont.getWidth(), cont.getHeight(),
                        cont.getParentWidth(), cont.getParentHeight());
            }
        }                
        
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
    
                
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati meno tohoto menu. Meno ziskava z id resource z ktoreho je menu vytvorene.
     * @return Meno pre menu.
     */
    public String getName() {        
        return res.getId();    
    }
    
    /**
     * Metoda ktora vrati sirku menu. V tomto pripade vrati 0 kedze kazdy potomok
     * by si mal tuto metodu podedit ale keby nahodou zabudne tak bude nulova velkost.
     * @return Sirka menu
     */
    @Override
    public int getWidth() {
        return 0;
    }
    
    /**
     * Metoda ktora vrati vysku menu. V tomto pripade vrati 0 kedze kazdy potomok
     * by si mal tuto metodu podedit ale keby nahodou zabudne tak bude nulova velkost.
     * @return Vyska menu
     */
    @Override
    public int getHeight() {
        return 0;
    }    
    
    /**
     * Metoda getUI ziskava informacie o resource a jeho potomkoch a uklada si ich
     * do HashMapy pre lahsi pristup. Pri skrolovacom resource uklada resource do listu scrollingResource.
     * 
     * @param resource 
     */
    private ArrayList<Container> getUI(UiResource resource) {
        if (resource.getType().getElements() != null) {            
            ArrayList<Container> _containers = new ArrayList<>();
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
            // Lokalne kontajnery v metode
            _containers.add(cont);            
            uiContainers.put(_res, cont);
            cont.setChildContainers(getUI(_res));            
            
            }
            return _containers;
        }
        return null;
    }
    
    /**
     * Metoda ktora vrati menu z hashmapy menuMap s menom zadanym ako parameter <b>name</b>
     * @param name Meno menu ktore hladame
     * @return Menu s menom rovnym parametru
     */
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
     * Metoda ktora vrati graficky kontext pre menu. Graficky kontext menu
     * je vlastne graphics obrazku <b>contImage</b>. Graficky kontext bude prazdny a
     * vycisteny ciernou farbou.
     * @return Graficky kontext pre menu
     */
    private Graphics getContentGraphics() {
        if (contImage == null) {
            return null;
        }
        Graphics g = contImage.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, gamePane.getWidth(), gamePane.getHeight());        
        return g;
    }
    
    /**
     * Metoda ktora vrati kolekciu vsetkych kontajnerov radenych k tomuto 
     * menu.
     * @return Kolekcia kontajnerov
     */
    public Collection<Container> getContainers() {
        return uiContainers.values();
    }
    
    /**
     * Metoda ktora vrati Container ktory sa nachadza v hashmape pod klucom
     * typu UiResource zadany parametrom <b>resource</b>
     * @param resource UiResource/kluc podla ktoreho prehladavame mapu.
     * @return Container zodpovedajuci parametru.
     */
    public Container getContainer(UiResource resource) {        
        return uiContainers.get(resource);
    }
    
    public Container getContainer(String name) {
        for (Container cont : containers) {
            if (cont.getResource().getId().equals(name)) {
                return cont;
            }
        }
        return null;
    }
    
    public ArrayList<Container> getMenuContainers() {
        return containers;
    }
    
    public boolean hasContainer(UiResource resource) {
        return uiContainers.containsKey(resource);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
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
    
    /**
     * Metoda ktora nastavi terajsie menu v gamePane na to zadane v parametri <b>menu</b>
     * @param menu Menu ktore sa ma zobrazit
     */
    @Override
    public void setMenu(AbstractMenu menu) {
        gamePane.setMenu(menu);        
    }        
    
    /**
     * Metoda ktora nastavuje ci bolo menu inicializovane. Vyuzitelne pri aktualizovani
     * menu. Ked je menu inicializovane tak nemusime vytvarat nove komponenty ale staci iba pomenit
     * komponenty ktore boli zmenene. Ked nebolo inicializovane tak musime inicializovat.
     * @param init True/False ci je menu uz inicializovane.
     */
    public void setInitialized(boolean init) {
        this.initialized = init;
    }
        
    /**
     * Metoda setInMenu nastavuje submenu pre toto menu. Submenu je typu AbstractInMenu 
     * a su ako takou nahradou za SwingElementy => kazda komponenta definovana v xml by sa dala nahradit takymto submenu.
     * @param inMenu Vnutorne menu na zobrazenie.
     */
    public void setInMenu(AbstractInMenu inMenu) {
        
    }
    
    public static void addMenu(AbstractMenu menu) {
        menuMap.put(menu.getName(), menu);;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InputHandling ">
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
    
    /**
     * Metoda mouseHandling ma spracovavat udalosti z mysi. Vacsinou su tieto metody v menu prazdne. 
     * Dovod je ten ze pouzivame Swing komponenty a tie maju priamo zabezpecene spracovavanie mysi =>
     * vsetky co sa da spravit v menu sa da vymenit za swing elementy dopisanim do layout.xml
     * @param e MouseEvent pre stlacenie mysi.
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody " >
        
    /**
     * Metoda ktora vykresluje menu do grafickeho kontextu. Kazdy potomok abstraktneho menu
     * moze vykreslovat svoje menu inak. Vacsinou tuto metodu nevyuzivame kvoli tomu ze sa daju tieto vykreslenia nahradit komponentami
     * definovanymi v layout.xml. V GameMenu je ale tato metoda dolezita kedze vykreslovanie mapy prebieha tymto sposobom.
     * @param g Graficky kontext do ktoreho vykreslujeme.
     */
    @Override
    public void paintMenu(Graphics g) {
        
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Komponent metody ">
    
    public void deactivateAll() {
        for (Container cont : uiContainers.values()) {
            cont.getComponent().deactivate();
        }
    }
    
    public void activateAll() {
        for (Container cont : uiContainers.values()) {
            cont.getComponent().activate();
        }
    }
    
    public void deactivate(UiResource res) {
        if (uiContainers.contains(res)) {
            uiContainers.get(res).getComponent().deactivate();
        }
    }
    
    public void activate(UiResource res) {
        if (uiContainers.contains(res)) {
            uiContainers.get(res).getComponent().activate();
        }
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
    
    /**
     * Metoda ma za ulohu vymazat vsetky kontajnery a stym aj vsetky komponenty v tomto menu.
     * Parameter sluzi ako prepinac ci sa ma vymazat aj kontajner ktory tvori zaklad pre menu.
     * @param menu True/False podla toho ci sa vymaze aj menu kontajner
     */
    public void removeAllContainers(boolean menu) {
        if (menu) {
            uiContainers.clear();        
            gameContainer.clear();              
        } else {
            Container cont = uiContainers.get(res);
            uiContainers.clear();            
            gameContainer.clear();            
            uiContainers.put(res, cont);
            gameContainer.addContainer(cont);
        }
        
        changedUi = true;
        changedGr = true;
    }
    
    // </editor-fold>    
}
