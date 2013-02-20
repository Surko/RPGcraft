/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.panels.listeners.ActionEvent;
import java.awt.event.ActionListener;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Cursor;
import rpgcraft.panels.components.swing.SwingCustomButton;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.resource.UiResource;

/**
 * SetMenuListener je implementovany ActionListener, ktory ma za ulohu
 * vykonat operaciu prepnut menu (setMenu).
 * @see ActionListener
 * @author Kirrie
 */
public class SetMenuListener extends Listener {
        // String s menu na nacitanie
        String menu;
        /**
         * Konstruktor tohoto listeneru s menom menu.
         * @param menu 
         */
        public SetMenuListener(String menu) {
            this.menu = menu;
            String[] parts = menu.split("#");
            switch (parts.length) {
                case 2 : {
                    this.param = parts[1];
                    this.type = parts[0];
                } break;                
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
            if (type != null) {
                switch (type) {
                    case "LIST" : {
                        Cursor c = (Cursor)e.getParam();
                        menu = c.getString(c.getColumnIndex(param));                        
                    } break;                    
                }
            }
            
            if (e.getSource() instanceof Component) {
                Component c = (Component)e.getSource();
                ((Menu)c.getOriginMenu()).setMenu(AbstractMenu.getMenuByName(menu));                
            }
            
        }
        
        
}
