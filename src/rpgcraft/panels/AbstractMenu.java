/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTextField;
import rpgcraft.GamePane;
import rpgcraft.MainGameFrame;
import rpgcraft.errors.MissingFile;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.ingame.InGameButton;
import rpgcraft.panels.components.ingame.InGameText;
import rpgcraft.panels.components.swing.SwingCustomButton;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.UiResource.UiType;
import rpgcraft.utils.Framer;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.MathUtils;
import rpgcraft.utils.TextUtils;

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
    
    /**
     * Abstraktna metoda s interface Menu zabezpecujuca uzivatelsky vstup
     */
    @Override
    public abstract void inputHandling();                
    public abstract void setWidthHeight(int w, int h);   
    
    
    protected Container gameContainer;
    protected GamePane gamePane;
    protected UiResource res;
    protected InputHandle input;
    protected GridBagConstraints c;
    protected ArrayList<UiResource> scrollingResource;
    // Neutriedena Mapa zadana pomocou linkedHashMap.
    protected Map<String, Container> uiContainers;
    protected boolean changedUi;
    protected boolean changedGr;
    protected BufferedImage contImage;
    protected boolean changedInit;
    
    
    public void initialize(Container gameContainer,InputHandle input) {
        this.gameContainer = gameContainer;
        this.gamePane = (GamePane)gameContainer.getComponent();
        this.input = input;                                       
        
        contImage = new BufferedImage(gameContainer.getWidth(), gameContainer.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        
        uiContainers = Collections.synchronizedMap(new LinkedHashMap<String,Container>());    
        
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
        uiContainers.put(res.getId(), cont);                
        
        cont.setChildContainers(getUI(res));
                                
        initializeImage();        
    }        
    
    
    
    
    /**
     * Metoda getUI ziskava informacie o resource a jeho potomkoch a uklada si ich
     * do HashMapy pre lahsi pristup.
     * @param resource 
     */
    private ArrayList<Container> getUI(UiResource resource) {
        if (resource.getElements() != null) {            
            ArrayList<Container> containers = new ArrayList<>();
            for (UiResource _res : resource.getElements()) {
                int[] lengths = MathUtils.getLengths(_res, uiContainers.get(resource.getId()));
                
                if (_res.isScrolling()) {
                    if (scrollingResource == null) {                        
                        scrollingResource = new ArrayList<>();
                    }
                    scrollingResource.add(_res);
                }        
            Container cont = new Container(_res,
                    lengths[0], lengths[2], lengths[1], lengths[3], uiContainers.get(resource.getId()));
            containers.add(cont);
            uiContainers.put(_res.getId(), cont);
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
        int[] lengths = MathUtils.getLengths(resource, uiContainers.get(resource.getId()).getParentContainer());
        
        Container cont = uiContainers.get(resource.getId());
        cont.set(lengths[0], lengths[2], lengths[1], lengths[3]);
        if (resource.getElements() != null) {
            for (UiResource _res : resource.getElements()) {
                    recalculate(_res);                    
                }
        }
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
            contImage = new BufferedImage(gamePane.getWidth(), gamePane.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            recalculate(res);
        }
        if (changedGr) {
            Graphics g = contImage.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, gamePane.getWidth(), gamePane.getHeight());
            paintElement(g, res, gamePane);  
            gamePane.updateUI();
            changedGr = false;
        }
    }                                             
    
    protected void modePainting(Graphics g, UiResource res, Container cont, int[] pos) {
        Image img = cont.getImage();
        switch (res.getPaintMode()) {
            case NORMAL : {
                if (img != null) {
                    g.drawImage(img, pos[0] + cont.getX(), pos[1] + cont.getY(), null);
                } else {
                    ImageUtils.paintBackgroundImage(res, cont);    
                    g.drawImage(cont.getImage(), pos[0] + cont.getX(), pos[1] + cont.getY(), null);                    
                }
            } break;
            case OVERLAP : {
                ImageUtils.overpaintBackgroundImage(g, res, cont, pos);                
            } break;
            default : {
                return;
            }            
            
        }
    }
    
    /**
     * Ked sa zmenil kontainer alebo ked je potreba preinitializovat tak prekresluje nanovo vsetky
     * komponenty.
     * @param g
     * @param resource 
     */
    protected void paintElement(Graphics g, UiResource resource, JPanel comp) {        
                   
        Container cont = uiContainers.get(resource.getId());
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru
        int[] _rpos = MathUtils.getStartPositions(resource.getPosition(),cont.getParentWidth(), cont.getParentHeight(), cont.getWidth(), cont.getHeight());
        
        // Vykreslit prvok iba ked sa zmenil
        if ((!cont.isChanged())) {
            modePainting(g, resource, cont, _rpos);
        } else {  
            cont.setImage(null);
            cont.setChanged(false);
            switch (resource.getType()) {
                case BUTTON : {                      
                     switch (resource.getLayoutType()) {
                        case INGAME : {
                            // Vytvorenie tlacidla z resource nachadzajuci sa v kontajnery cont a v tomto menu.
                            InGameButton butt = new InGameButton(resource, cont, this);
                            butt.addActionListener(ListenerFactory.getListener(resource.getAction()));
                            cont.setComponent(butt);
                            modePainting(g, resource, cont, _rpos); 
                        } break;
                        default : {                            
                            if (cont.getComponent() != null) {
                                comp.remove((SwingCustomButton)cont.getComponent());
                            }                               
                            SwingImageButton butt = new SwingImageButton(cont, this);
                            butt.addActionListener(ListenerFactory.getListener(resource.getAction()));
                            cont.setComponent(butt);                            
                            comp.add(butt,resource.getConstraints());                                                        
                        }
                    }
                    return;
                }
                case PANEL : {
                    switch (resource.getLayoutType()) {
                        case INGAME : {                              
                            modePainting(g, resource, cont, _rpos);     
                        } break;                    
                        default : {
                            if (cont.getComponent() != null) {
                                comp.remove((SwingImagePanel)cont.getComponent());
                            }                            
                            SwingImagePanel component = new SwingImagePanel(cont, this);  
                            cont.setComponent(component);                             
                            comp.add(component,resource.getConstraints());                            
                        } break;
                    }
                } break;
                case TEXT : {                
                    
                        switch (resource.getLayoutType()) {
                            case INGAME : {                            
                                InGameText text = new InGameText(resource, cont, this); 
                                cont.setComponent(text);                                
                            } break;
                            default : {
                                if (cont.getComponent() != null) {
                                    comp.remove((SwingText)cont.getComponent());
                                }                               
                                SwingText component = new SwingText(cont, this);
                                cont.setComponent(component);
                                comp.add(component,resource.getConstraints()); 
                            } break;
                        }
                    
                    return;
                }
                case IMAGE : {
                
                } break;
                case LIST : {
                    switch (resource.getLayoutType()) {
                        case INGAME : {                              
                                 
                        } break;                    
                        default : {
                            if (cont.getComponent() != null) {
                                comp.remove((SwingImageList)cont.getComponent());
                            }                            
                            SwingImageList component = new SwingImageList(cont, this, new String[][] {new String[] {"ahoj"},new String[] {"ahoj"},new String[] {"ahoj"}});  
                            cont.setComponent(component);                            
                            comp.add(component,resource.getConstraints()); 
                        } break;
                    }
                }
                    
            }
                        
            
        }        
        /* 
         * Rekurzivne prechadzam metodu paintElement s podelementami resource                        
         * Ked je resource typu panel tak metoda je volana s tymto panelom ako 
         * komponentov, inak je volana s parametrom comp. Tymto si zachovavam
         * kazdy resource ako kontainer pre podelementy.
         */
            
            if (resource.getElements() != null) {
                for (UiResource _resource : resource.getElements()) {
                    if (cont.getComponent() instanceof JPanel) {
                        paintElement(g, _resource, (JPanel)cont.getComponent());
                        cont.getComponent().update();
                    } else {
                        paintElement(g, _resource, comp);
                    }
                }
            }
    }              
    
    
    
    /**
     * Metoda loadImage nacita z ImageResource obrazok zadany parametrom iFile.
     * Dodatocne parametre urcuju co za farbu pozadia sa pouzije pri chybe a spravu
     * pre uzivatela.
     * @param iFile Text s imageresource na nacitanie obrazku
     * @param c Farba pri chybe
     * @param msg Sprava pre uzivatela pri chybe
     * @return Obrazok pre menu
     */
    public Image loadImage(String iFile, Color c, String msg) {        
        try {            
        Image out = ImageResource.getResource(iFile).getBackImage();
        return out;
        } catch(Exception e) {                
             new MissingFile(e, c, msg).render();  
             return null;
        }        
    }
    
    /**
     * Metoda loadResourceImage je podobna ako loadImage. V tomto pripade ale obrazok
     * ziskava priamo z resource zadanom parametrom resource. Dodatocne parametre urcuju co za farbu pozadia sa 
     * pouzije pri chybe a spravu pre uzivatela.
     * @param resource ImageResouce na nacitanie obrazku
     * @param c Farba pri chybe
     * @param msg Sprava pre uzivatela pri chybe
     * @return Obrazok pre menu
     */
    public Image loadResourceImage(ImageResource resource, Color c, String msg) {
        try {
            Image out = resource.getBackImage();
            return out;
        } catch(Exception e) {                
             new MissingFile(e, c, msg).render();  
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
    }
    
    public Container getContainer(String resource) {
        return uiContainers.get(resource);
    }
    
    @Override
    public void setMenu(AbstractMenu menu) {
        gamePane.setMenu(menu);        
    }
    
    public void newMapInstance() {}
    
    public void paintFPS(Graphics g) {        
        g.setColor(Colors.getColor(Colors.fpsColor));
        g.drawString(Framer.fShow+ " FPS", 0, 10);
    }
    
    @Override
    public void paintMenu(Graphics g) {        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, gamePane.getWidth(), gamePane.getHeight());                 
        g.drawImage(contImage, 0, 0, null); 
    }
    
    @Override
    public void update() {  
       if (scrollingResource != null) {
            for (UiResource resource : scrollingResource) {
                uiContainers.get(res).increase(res.getScrollX(), res.getScrollY());                
            }
            changedGr = true;
        }
       initializeImage();
    }

    
}
