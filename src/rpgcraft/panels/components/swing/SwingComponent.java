/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JPanel;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.resource.types.AbstractType;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MathUtils;

/**
 * SwingKomponenta dediaca od panelu implementujuca komponentu si zdruzuje zakladne
 * vlastnosti Swingovskych komponent s tym ze mame spolocne metody zaruceny implementaciou Component.
 * Je to zakladna trieda ktora obsahuje zakladne metody pre pracu s komponentami ako je nastavovanie
 * velkosti, zistenie velkosti komponenty ako aj zakladne spracovavanie udalosti a pridavanie
 * akcii/listenerov ku komponente.
 * @author Surko
 */
public abstract class SwingComponent extends JPanel implements Component {  
    // <editor-fold defaultstate="collapsed" desc=" Logger ">
    // Logger
    private static final Logger LOG = Logger.getLogger(SwingComponent.class.getName());
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    
    // Kontajner komponenty
    protected Container componentContainer;
    // Menu kde sa komponenta nachadza
    protected AbstractMenu menu;
    // Boolean ci sa zmenila komponenta
    protected boolean changed;
    // Typ pre komponentu
    protected AbstractType type;   
    // Boolean ci je oznacena komponenta
    protected boolean isSelected;
    // Mouse listenery
    protected ArrayList _mlisteners;
    // Key listenery
    protected ArrayList<Action> _klisteners;
    // Ci je komponenta bez dat a nevyuzitelna.
    protected boolean isNoData = false;
    // Ci je komponenta aktivna pre eventy
    protected boolean active = false;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    protected abstract void reconstructComponent();  
    /**
     * Metoda clone ktora skopiruje obsah Componenty a vrati novu instanciu tohoto objektu.
     * @return Nova instancia objektu SwingComponent
     */
    @Override
    public abstract Component copy(Container cont, AbstractMenu menu);    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    
    /**
     * Prazdny konstruktor pre vytvorenie komponenty.
     */
    protected SwingComponent() { 
    }
    
    /**
     * Konstruktor pre komponentu ktory nastavi zakladne informacie o komponente =>
     * kontajner pre komponentu, menu v ktorom sa komponenta nachadza a typ ktory urcuje
     * comu podlieha komponenta (list, panel, button, atd)
     * @param container Kontajner pre komponentu
     * @param menu Menu v ktorom sa komponenta nachadza
     */
    public SwingComponent(Container container, AbstractMenu menu) {
        this.componentContainer = container;
        this.menu = menu;
        if (componentContainer != null) {
            this.type = componentContainer.getResource().getType();
            this.active = componentContainer.getResource().isEventActive();
        }
        changed = true;
    }                
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Metoda ktora nastavi ci sa zmenila komponenta a potrebuje nejakym sposobom
     * prekreslit.
     * @param changed 
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
    
    /**
     * Metoda ktora nastavi komponentu aby bola viditelna pre uzivatela.
     * @param aFlag 
     */
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);        
    }            
    
    /**
     * Metoda ktora oznaci komponentu.
     * @return True/False ci sa podarilo oznacit
     */
    @Override
    public boolean select() {
        if (!isNoData) {
            isSelected = true;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Metoda aktivuje komponentu a je schopna reagovat na eventy.
     */
    @Override
    public void activate() {
        this.active = true;
    }
    
    /**
     * Metoda deaktivuje komponentu pre vsetky eventy.
     */
    @Override
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Metoda ktora odoznaci komponentu
     * @return True/False ci sa podarilo odoznacit
     */
    @Override
    public boolean unselect() {
        return isSelected = false;
    }        
    
    /**
     * Override metoda ktora nastavi boundaries podla parametrov. 
     * Rovnaka funkcnost metody ako ta ktora je v AWTSwing.
     * @param x Zaciatocna x-ova pozicia 
     * @param y Zaciatonca y-ova pozicia
     * @param w Sirka komponenty
     * @param h Vyska komponenty
     */
    @Override
    public void setBounds(int x, int y, int w, int h) {  
        super.setBounds(x, y, w, h);
    }
    
    /**
     * Metoda ktora prida listener pre mys pre tuto komponentu
     */
    public void addOwnMouseListener() {
        addMouseListener(this);    
    }
    
    /**
     * Metoda ktora prida do mouselistenerov ActionListener zadany parametrom.
     * @param listener Listener pridavany do mouselistenerov.
     */
    @Override
    public void addActionListener(ActionListener listener) {
        if (listener != null) {
            if (_mlisteners == null) {
                _mlisteners = new ArrayList();
            }
            _mlisteners.add(listener);
        }
    }

    
    /**
     * Metoda ktora prida k listenerom (bud mouse alebo key podla typu akcie) 
     * akciu na vykonanie.
     * @param action Akcia na pridanie k listenerom.
     */
    @Override
    public void addActionListener(Action action) {
        if (action != null) {
            
            switch (action.getType()) {
                case MOUSE : {
                    if (_mlisteners == null) {
                        _mlisteners = new ArrayList();
                    }
                    
                    if (this.getMouseListeners().length == 0) {
                        addOwnMouseListener();
                    }
                    
                    _mlisteners.add(action);
                } break;
                case KEY : {
                    if (_klisteners == null) {
                        _klisteners = new ArrayList();                        
                    }
                    _klisteners.add(action);
                } break;
            }                        
        }
    }
    
    /**
     * Metoda ktora prida k listeneru vsetky akcie zadane v liste.
     * @param actions Akcie na pridanie zadane v liste.
     */
    @Override
    public void addActionListeners(ArrayList<Action> actions) {
        if (actions != null) {                       
            
            for (Action action : actions) {
                addActionListener(action);
            }
        }
    }

    /**
     * Metoda ktora vymaze ActionListener z komponenty.
     * @param listener Listener ktory sa vymazava.
     */
    @Override
    public void removeActionListener(ActionListener listener) {
        if (_mlisteners.remove(listener)) {        
            if (_mlisteners.isEmpty()) {
                removeMouseListener(this);
            }
        } else {
            _klisteners.remove(listener);
        }
    }  
      
    /**
     * Metoda ktora ma za ulohu pridat do komponenty novu komponentu zadanu ako parameter c,
     * podla dalsich dodatocnych informacii zadanych v parametre constraints
     * @param c Komponenta na pridanie.
     * @param constraints Constraints podla ktorych sa rozvrhne komponenta
     */
    @Override
    public void addComponent(Component c, Object constraints) {
        if (c instanceof java.awt.Component) {
            this.add((java.awt.Component)c,constraints, 0);
        }
        //this.validate();
    }   
    
    /**
     * Metoda ktora ma za ulohu pridat do komponenty novu komponentu zadanu ako parameter c
     * @param c Komponenta na pridanie.
     */
    @Override
    public void addComponent(Component c) {
        if (c instanceof java.awt.Component) {
            this.add((java.awt.Component)c);
        }
    }
    
    /**
     * Metoda ma za ulohu vymazat zo sameho seba komponentu zadanu ako paramter c.
     * @param c Komponenta na vymazanie
     */
    @Override
    public void removeComponent(Component c) {
        if (c instanceof java.awt.Component) {
            this.remove((java.awt.Component)c);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda ktora vrati s kontajneru preferovane velkosti komponenty.
     * @return 
     */
    @Override
    public Dimension getPreferredSize() {
        if (componentContainer != null) {
            return componentContainer.getPrefDimension();
        } else {
            return super.getPreferredSize();
        }
    }
    
    /**
     * Override metoda ktora vrati velkost komponenty. Rozhoduje sa 
     * podla toho ci je komponenta s kontajnerom alebo bez neho (napriklad keby zmizol kontajner nejakym sposobom tak nedojde k chybe).
     * Ked ma tak vrati velkost z kontajneru inak vrati nastavene velkosti komponenty.
     * @return 
     */
    @Override
    public Dimension getSize() {
        if (componentContainer == null) {
            return super.getSize();
        }
        return componentContainer.getPrefDimension();        
    }
    
    /**
     * Metoda ktora vrati kontajner v ktorom sa komponenta nachadza
     * @return 
     */
    @Override
    public Container getContainer() {
        return componentContainer;
    }
    
    /**
     * Metoda ktora ma vratit LayoutManager tejto komponenty.
     * @return LayoutManager tejto komponenty
     */
    @Override
    public LayoutManager getLayout() {
        return super.getLayout();
    }
    
    /**
     * Metoda ktora vrati menu v ktorom sa komponenta nachadza
     * @return 
     */
    @Override
    public AbstractMenu getOriginMenu() {
        return menu;        
    }
    
    /**
     * Metoda ktora ma za ulohu vratit otcovsku komponentu komponenty ktora metodu vola.
     * @return 
     */
    @Override
    public Component getParentComponent() {
        if (componentContainer != null) {
            return componentContainer.getParentContainer().getComponent();
        }
        return null;
    }
    
    /** Metoda ktora vrati ci je komponenta oznacena     
     * @return True/False ci je komponenta oznacena
     */
    @Override
    public boolean isSelected() {
        return isSelected;
    }
    
    /**
     * Metoda ktora vrati ci je komponenta bez dat.     
     * @return True/False ci je komponenta bezdatova.
     */
    @Override
    public boolean isNoData() {
        return isNoData;
    }
    
    // </editor-fold>  
           
    // <editor-fold defaultstate="collapsed" desc=" Action metody + EventHandlery ">
    /**
     * Metoda ktora skuma ci su splnene podmienky pre zavolanie akcie z listeneru.
     * V tejto komponente je len zakladna implementacia s testovanim poctu klikov, cim sa stava
     * atribut clicks v xml povinny. Pravdaze ked pridavam komponente normalny ActionListener
     * tak tato metoda nie je ani volana. Po splneni podmienok je vybraty Listener
     * a zavolana akcia.
     * @param action Akcia ktora musi byt splnena.
     * @param event Udalost podla ktorej sa rozhodne ci bude akcia splnena.
     */
    @Override
    public void fireMouseEvent(Action action, ActionEvent event) {  
        
        if (event.getClicks() < action.getClicks()) return;

        performAction(action, event);
        
    }
    
    /**
     * Metoda ktora len prenesie vykonanie akcie na metodu performAction.
     * Tato metoda tu je len preto aby sa dodrzala koncepcia volania ako pri spracovavani
     * udalosti z mysi.
     * @param action Akcia na vykonanie.
     * @param event ActionEvent podla ktoreho sa vykona akcia.
     */
    @Override
    public void fireKeyEvent(Action action, ActionEvent event) {
        performAction(action, event);                       
    }
    
    /**
     * Metoda performAction podla nazvu vykona akciu zadanu parametrom action
     * podla udalosti ActionEvent ktoru posielame listeneru, ktory si ziskavame 
     * z triedy ListenerFactory.
     * @param action
     * @param event 
     */
    public void performAction(Action action, ActionEvent event) {
        action.setActionEvent(event);
        DataUtils.execute(action);
        /*
        Listener list = ListenerFactory.getListener(action);
        event.setAction(action);
        if (list != null)
            list.actionPerformed(event);                               
            */
    }
         
    /**
     * Deprecated verzia spracovavania udalosti z mysi. Rovnaka funkcnost prenesena
     * do metody mouseClicked.
     * @param event ActionEvent podla ktoreho urcuje ci sa akcia vykona.
     * @deprecated
     */
    @Deprecated
    public void isMouseSatisfied(ActionEvent event) {
        for (int i = 0;i<_mlisteners.size() ;i++ ){  
            if (_mlisteners.get(i) instanceof ActionListener) {
                ActionListener listener = (ActionListener)_mlisteners.get(i);                
                listener.actionPerformed(event);  
                continue;
            }
            if (_mlisteners.get(i) instanceof Action) {
                Action action = (Action)_mlisteners.get(i);
                
                fireMouseEvent(action, event);
                
                if (action.isTransparent()) {
                    ((SwingComponent)this.getParent()).isMouseSatisfied(event);
                }
                
                continue;
            }            
        }
    }
    
    /**
     * Override metoda ktora spracovava udalost kliknutia z mysi. Ked je to normalny ActionListener
     * tak len vykona akciu (actionPerformed). Ked je akcia typu Action tak spusti metodu fireMouseEvent,
     * ktora podla dalsich vlastnosti rozhodne ci sa akcia vykona. Nakonci skontroluje ci bola akcia transparentna, 
     * co znamena ze je event preneseny na komponentu vyssie <=> otcovsku komponentu.
     * @param e MouseEvent pre mys.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (menu != null) {
            menu.mouseHandling(e);
        }
        
        if (_mlisteners != null && active) {            
            for (int i = 0;i<_mlisteners.size() ;i++ ){  
                ActionEvent event = new ActionEvent(this, 0, e.getClickCount(), null, null);
                if (_mlisteners.get(i) instanceof ActionListener) {
                    ActionListener listener = (ActionListener)_mlisteners.get(i);                
                    listener.actionPerformed(event);  
                    continue;
                }
                if (_mlisteners.get(i) instanceof Action) {
                    Action action = (Action)_mlisteners.get(i);

                    fireMouseEvent(action, event);

                    if (action.isTransparent()) {
                        ((Component)this.getParent()).mouseClicked(e);
                    }

                    continue;
                }            
            }
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
    
    /**
     * Podobne ako metoda mouseClicked ma tato metoda za ulohu spracovavat vstup od uzivatela z klavesnice.
     * Parameter input sluzi ako odkaz na KeyHandler, vdaka ktoremu zisti co za klavesy boli stlacene
     * a podla toho vykona prislusne operacie.
     * @param input InputHandle/Klavesnicovy vstup pre hru.
     */
    @Override
    public void processKeyEvents(InputHandle input) {                        
        if (_klisteners != null && !_klisteners.isEmpty()) {                                   
            for (Action action : _klisteners) {
                if (input.clickedKeys.contains(action.getKey())) {
                    fireKeyEvent(action, new ActionEvent(this, 0, -1, action.getAction(), null));                    
                }
            }
        }        
    }                                         
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update metody ">
    
    /**
     * Prazdna update metoda aby sa zaistilo ze hu nemusim vsade implementovat, ale iba tam
     * kde je potreba.
     */
    @Override
    public void update() {};
    
    /**
     * Override metoda updateUI ktora vykona to iste ako originalna updateUI definovana
     * v AWT.     
     */
    @Override
    public void updateUI() {
        super.updateUI(); 
    }
    /**
     * Metoda refreshPositions je rovnaka pre vsetky zdedene komponenty od tejto.
     * Ma za ulohu nastavit pozicie komponenty podla rozmiestnenie resource (CENTER, LEFT, atd...)
     * naco potrebuje vysky a sirky otcovskej a tejto komponenty. 
     * Ked ma komponenta INGAME layout tak donastavi boundaries.
     * @param w Sirka komponenty
     * @param h Vyska komponenty
     * @param pw Otcovska sirka komponenty
     * @param ph Otcovska vyska komponenty
     */
    @Override
    public void refreshPositions(int w, int h, int pw, int ph) {
        int[] _rpos = MathUtils.getStartPositions(componentContainer.getResource().getPosition(),
                      pw, ph, w , h);
        componentContainer.setPositions(_rpos);
        // Musi byt porovnavanie s null, lebo uplne prva komponenta nie je rozvrhnuta podla xml.
        if (componentContainer.getParentContainer().getComponent().getLayout() == null) {
            setBounds(_rpos[0] + componentContainer.getResource().getX(),
                    _rpos[1] + componentContainer.getResource().getY(), w, h);
        }
    }
    
    /**
     * Metoda refresh zaktualizuje komponentu podla nami zadaneho layoutu =>
     * zoberie sa do uvahy vsetko co sa nachadza v kontajnery a v resource.
     * Vacsiu ulohu nabera pri zdedenych komponentach od tejto. V tomto pripade
     * iba vypise do logu ze ide refreshovat komponentu.
     */
    @Override
    public void refresh() {
        //LOG.log(Level.INFO, StringResource.getResource("_rsh", new String[] {componentContainer.getResource().getId()}));
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    
    /**
     * Prazdna kresliaca metoda. Vykresluje obrazok zadany parametrom dbImage.
     * Kazda komponenta by si hu mala implementovat.
     * @param g Graphics do ktorej vykreslujeme bufferovany obrazok
     * @param dbImage Bufferovany obrazok.
     */
    protected void paintImage(Graphics g, Image dbImage) {}        

    // </editor-fold>        
    
    
    
}
