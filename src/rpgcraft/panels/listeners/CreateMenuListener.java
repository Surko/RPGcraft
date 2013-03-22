/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.FactoryMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Cursor;
import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public class CreateMenuListener extends Listener {
    public enum Operations {
            CREATE
    }
    
    Operations op;
    
    /**
     * Konstruktor tohoto listeneru s menom menu.
     * @param menu 
     */
    public CreateMenuListener(String menu) {
        String[] mainOp = menu.split("[(]");
        this.op = Operations.valueOf(mainOp[0]);
        
        if (mainOp.length > 1) {            
            setParams(mainOp[1].substring(0, mainOp[1].length() - 1));        
        }
    }



    /**
     * Metoda ActionPerformed vykonava operaciu ako je to pri kazdom Listenery.
     * V tomto pripade skontroluje ci bola operacia vykonana v komponente.
     * Nasledne zisti v akej a vytiahne si z nej menu. Ked take menu neexistovalo => 
     * nebolo zatial inicializovane tak vytvori tovarensky vyrobene menu FactoryMenu
     * ktore sa podoba na menu s id=param.
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
        setRunParams(e);

        if (e.getSource() instanceof Component) {
            Component c = (Component)e.getSource();
            if (AbstractMenu.getMenuByName(parsedOp[0])==null) {                
                FactoryMenu factMenu = new FactoryMenu(UiResource.getResource(parsedOp[0]));
                ((Menu)c.getOriginMenu()).setMenu(factMenu);  
            } else {
                ((Menu)c.getOriginMenu()).setMenu(AbstractMenu.getMenuByName(parsedOp[0]));                          
            }
                          
        }

    }
}
