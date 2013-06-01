/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.Color;
import rpgcraft.panels.listeners.ActionEvent;
import java.awt.event.ActionListener;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ui.menu.Menu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.FactoryMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Cursor;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

/**
 * SetMenuListener je implementovany ActionListener, ktory ma za ulohu
 * vykonat operaciu prepnut menu (setMenu).
 * @see ActionListener
 * @author Kirrie
 */
public class MenuListener extends Listener {

        @Override
        public String getName() {
            return ListenerFactory.Commands.MENUOP.toString();
        }
    
        public enum Operations {
            SETMENU,
            CREATEMENU
        }
        
        Operations op;
        
        /**
         * Konstruktor tohoto listeneru s menom menu.
         * @param menu 
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
        
        
        
        /**
         * Metoda ActionPerformed vykonava operaciu ako je to pri kazdom Listenery.
         * V tomto pripade skontroluje ci bola operacia vykonana v komponente.
         * Nasledne zisti v akej a vytiahne si z nej menu v ktorej je komponenta pouzivana.
         * Z tohoto menu potom volame metodu setMenu. 
         * Takisto by sa dala tato metoda nahradit priamym sposobom nacitanie menu metodou
         * setMenu cez hraci panel. Zatial zostava na uvazenie pretoze kazde menu moze nacitavat dalsie
         * menu inak.
         * (GamePane)
         * @param e ActionEvent
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
        
        
}
