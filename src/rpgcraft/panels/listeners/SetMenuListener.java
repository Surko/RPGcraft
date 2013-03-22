/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.Color;
import rpgcraft.panels.listeners.ActionEvent;
import java.awt.event.ActionListener;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Cursor;
import rpgcraft.resource.StringResource;

/**
 * SetMenuListener je implementovany ActionListener, ktory ma za ulohu
 * vykonat operaciu prepnut menu (setMenu).
 * @see ActionListener
 * @author Kirrie
 */
public class SetMenuListener extends Listener {
    
        public enum Operations {
            SETMENU
        }
        
        Operations op;
        
        /**
         * Konstruktor tohoto listeneru s menom menu.
         * @param menu 
         */
        public SetMenuListener(String menu) {
            String[] mainOp = menu.split("[(]");
            this.op = Operations.valueOf(mainOp[0]);

            if (mainOp.length > 1) {            
                setParams(mainOp[1].substring(0, mainOp[1].length() - 1));        
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
            setRunParams(e);
            
            if (e.getSource() instanceof Component) {
                Component c = (Component)e.getSource();
                Menu newMenu = AbstractMenu.getMenuByName(parsedOp[0]);
                
                if (newMenu != null) {                                    
                    ((Menu)c.getOriginMenu()).setMenu(newMenu);                
                } else {
                    new MultiTypeWrn(null, Color.red, StringResource.getResource("_nelistener",
                            new String[] {parsedOp[0]}), null).renderSpecific(StringResource.getResource("_label_resourcerror"));
                }
                
            }
            
        }
        
        
}
