/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import rpgcraft.panels.listeners.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.listeners.Action;

/**
 * Interface pre kazdu komponentu ktora sa nachadza v hre. Zatial hu implementuje
 * iba SwingComponent.
 */
public interface Component extends MouseListener, Cloneable {
    
    /**
     * Metoda ktora ma vyvolat udalosti z mysi
     * @param action Akcia ktoru spustame
     * @param event Udalost ktora bola vyvolana
     */
    public void fireMouseEvent(Action action, ActionEvent event);
    
    /**
     * Metoda ktora ma vyvolat udalosti z klavesnice
     * @param action Akcia ktoru spustame
     * @param event Udalost ktora bola vyvolana
     */
    public void fireKeyEvent(Action action, ActionEvent event);    
    
    /**
     * Metoda ktora vykresluje komponentu.
     * @param g Graficky kontext do ktoreho kreslime.
     */
    public void paint(Graphics g);
   
     /**
     * Metoda ktora reaguje na stlacenie mysi.
     * @param e MouseEvent ktory spusta metodu
     */
    @Override
    public void mouseClicked(MouseEvent e);
    
    /**
     * Metoda ktora reaguje na drzanie tlacidla mysi.
     * @param e MouseEvent ktory spusta metodu
     */
    @Override
    public void mousePressed(MouseEvent e);
    
    /**
     * Metoda ktora reaguje na upustenia tlacidla mysi.
     * @param e MouseEvent ktory spusta metodu
     */
    @Override
    public void mouseReleased(MouseEvent e);

    /**
     * Metoda ktora reaguje na vstupenie mysi do priestoru
     * @param e MouseEvent ktory spusta metodu
     */
    @Override
    public void mouseEntered(MouseEvent e);

    /**
     * Metoda ktora reaguje na opustenie mysi z pristoru.
     * @param e MouseEvent ktory spusta metodu
     */
    @Override
    public void mouseExited(MouseEvent e);
    
    /**
     * Metoda ktora spracovava klavesove udalosti
     * @param input InputHandler podla ktoreho spracovavame udalosti
     */
    public void processKeyEvents(InputHandle input);
    
    /**
     * Metoda ktora pridava ActionListener ku komponente
     * @param listener Listener na pridanie
     */
    public void addActionListener(ActionListener listener);
    
    /**
     * Metoda ktora pridava sadu akcii do komponentu
     * @param actions List s akciami na pridanie
     */
    public void addActionListeners(ArrayList<Action> actions);
    
    /**
     * Metoda ktora prida akciu do komponenty
     * @param action Akcia ktoru pridavame
     */
    public void addActionListener(Action action);            
    
    /**
     * Metoda ktora vymazava ActionListener z komponenty.
     * @param listener Listener ktory vymazavame.
     */
    public void removeActionListener(ActionListener listener);        
    
    /**
     * Metoda ktora vrati originalne AbstractMenu v ktorom sa nachadza komponenta.
     * @return AbstractMenu v ktorom je menu
     */
    public AbstractMenu getOriginMenu();    
 
    /**
     * Metoda ktora nastavuje ci je komponenta viditelna.
     * @param aFlag True/false ci je metoda viditelna.
     */
    public void setVisible(boolean aFlag);
    
    /**
     * Metoda ktora aktualizuje komponentu podla danych operacii. 
     */
    public void update();
    
    /**
     * Metoda ktora skopiruje komponentu ako navratovu hodnotu
     * @param cont Kontajner ktory priradime novej komponente
     * @param menu Menu ktore priradime novej komponente
     * @return Novu komponentu podla skopirovanej
     */
    public Component copy(Container cont, AbstractMenu menu);
    
    /**
     * Metoda ktora vrati true/false ci je komponenta vidiet.
     * @return True/false ci ukazujeme komponentu
     */
    public boolean isShowing();
    
    /**
     * Metoda ktora vrati true/false ci je komponenta oznacena.
     * @return True/false ci je oznaceny predmet
     */
    public boolean isSelected();
    
    /**
     * Metoda ktora vrati true/false ci je komponenta viditelna.
     * @return True/false ci je viditelny predmet
     */
    public boolean isVisible();
    
    /**
     * Metoda ktora aktivuje komponentu na spracovanie eventov
     */
    public void activate();
    
    /**
     * Metoda ktora deaktivuje komponentu na spracovanie eventov
     */
    public void deactivate();
    
    /**
     * Metoda ktora oznaci komponentu.
     * @return True/false ci sa podarilo oznacit
     */
    public boolean select();
    
    /**
     * Metoda ktora odoznaci komponentu
     * @return True/false ci sa podarilo odoznacit.
     */
    public boolean unselect();
    
    /**
     * Metoda ktora posle event na spracovanie.
     * @param event Event ktory posielame
     */
    public void dispatchEvent(AWTEvent event);
    
    /**
     * Metoda ktora nastavuje okraje (velkosti) a pozicie komponenty. Pozicie su
     * vzhladom k rodicovskej komponente
     * @param x X-ova pozicia komponenty
     * @param y Y-ova pozicia komponenty
     * @param width Sirka komponenty
     * @param height Vyska komponenty
     */
    public void setBounds(int x, int y, int width, int height);
    
    /**
     * Metoda ktora aktualizuje UI. Vacsinou vyuzita pri Swing komponentach
     * ktore obsahuju layout.
     */
    public void updateUI();
    
    /**
     * Metoda ktora vrati  ci je komponenta bez blizsie urcenych dat.
     * @return True/false ci je s datami alebo bezdatova.
     */
    public boolean isNoData();
    
    /**
     * Metoda ktora refreshne komponentu podla okolia.
     */
    public void refresh();
    
    /**
     * Metoda ktora refreshne pozicie komponenty.
     * @param w Sirka komponenty
     * @param h Vyska komponenty
     * @param pw Sirka rodicovskej komponenty
     * @param ph Vyska rodicovskej komponenty
     */
    public void refreshPositions(int w, int h, int pw, int ph);
    
    /**
     * Metoda ktora vrati rodicovsku komponentu
     * @return Rodicovska komponenta tejto komponenty
     */
    public Component getParentComponent();
    
    /**
     * Metoda ktora prida komponentu do tejto komponenty
     * @param c Komponenta ktoru pridavame
     */
    public void addComponent(Component c);
    
    /**
     * Metoda ktora prida komponentu do tejto komponenty podla urcitych obmedzeni
     * zadane parametrom <b>constraints</b>.
     * @param c Komponenta ktoru pridavame
     * @param constraints Obmedzenia komponenty.
     */
    public void addComponent(Component c, Object constraints);
    
    /**
     * Metoda ktora vymaze komponentu z tejto komponenty
     * @param c Komponenta ktoru vymazavame.
     */
    public void removeComponent(Component c);
    
    /**
     * Metoda ktora vrati preferovanu velkost
     * @return Preferovana velkost
     */
    public Dimension getPreferredSize();
    
    /**
     * Metoda ktora vrati normalnu velkost komponenty
     * @return Normalna velkost komponenty
     */
    public Dimension getSize();
    
    /**
     * Metoda ktora vrati layout ktory bol dany komponente
     * @return LayoutManager vytvoreny/pridany do komponenty
     */
    public LayoutManager getLayout();
    
    /**
     * Metoda ktora vrati kontajner tejto komponenty.
     * @return Kontajner tejto komponenty.
     */
    public Container getContainer();
    
    /**
     * Metoda ktora vymazava vsetky komponenty z tejto komponenty.
     */
    public void removeAll();
}
