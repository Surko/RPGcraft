/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.Color;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ui.menu.Menu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.plugins.Listener;
import rpgcraft.panels.FactoryMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

/**
 * Trieda dediaca od Listeneru je dalsi typ listeneru mozny vygenerovat v ListenerFactory,
 * ktory ma za ulohu vykonavat Menu akcie => akcie ktore su vseobecne pre ovladanie menu.
 */
public class MenuListener extends Listener {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Enum s moznymi operaciami v tomto listenery. V metode actionPerform sa
     * podla tychto operacii vykonavaju prislusne metody
     */
    public enum Operations {
        SETMENU,
        CREATEMENU
    }

    /**
     * Operacia na vykonanie
     */
    Operations op;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
    * Vytvorenie instancie listeneru pomocou textu zadaneho v parametri <b>data</b>.
    * Konstruktor rozparsuje text, urci operaciu aka sa bude vykonavat a parametre
    * pre tuto operaciu pomocou metody setParams
    * @param menu Text s funkciou ktoru vykonavame
    */
    public MenuListener(String menu) {
        int fstBracket = menu.indexOf('(');

        String params = null;

        if (fstBracket == -1) {
            this.op = MenuListener.Operations.valueOf(menu);
        } else {
            this.op = MenuListener.Operations.valueOf(menu.substring(0, fstBracket));
            params = menu.substring(fstBracket);
        }

        if (params != null) {            
            setParams(params.substring(1, params.length() - 1));        
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Vykonavanie + pomocne metody ">
    /**
    * {@inheritDoc }
    * @param e {@inheritDoc }
    * @see ActionEvent
    */
    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println(Thread.currentThread());
        super.actionPerformed(e);


        switch (op) {
            case SETMENU : {
               if (e.getSource() instanceof Component) {
                    Component c = (Component)e.getSource();
                    Menu newMenu = null;
                    if (parsedObjects[0] instanceof String) {
                        newMenu = AbstractMenu.getMenuByName((String)parsedObjects[0]);
                    }
                    if (parsedObjects[0] instanceof AbstractMenu) {
                        newMenu = (AbstractMenu)parsedObjects[0];
                    }

                    if (newMenu != null) {                                    
                        ((Menu)c.getOriginMenu()).setMenu(newMenu);                
                    } else {
                        new MultiTypeWrn(null, Color.red, StringResource.getResource("_nelistener",
                                new String[] {parsedObjects[0].toString()}), null).renderSpecific(StringResource.getResource("_label_resourcerror"));
                    }

                } 
            } break;
            case CREATEMENU : {
                if (e.getSource() instanceof Component) {
                    Component c = (Component)e.getSource();
                    if (parsedObjects[0] instanceof String) {
                        if (AbstractMenu.getMenuByName((String)parsedObjects[0])==null) {                
                            FactoryMenu factMenu = new FactoryMenu(UiResource.getResource((String)parsedObjects[0]));
                            ((Menu)c.getOriginMenu()).setMenu(factMenu);  
                        } else {
                            ((Menu)c.getOriginMenu()).setMenu(AbstractMenu.getMenuByName((String)parsedObjects[0]));                          
                        }
                    }

                }
            } break;                
        }                                                

    }
    // </editor-fold>
       
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * {@inheritDoc }
     * return Meno listeneru
     */
    @Override
    public String getName() {
        return ListenerFactory.Commands.MENUOP.toString();        
    }    
    // </editor-fold>
}
