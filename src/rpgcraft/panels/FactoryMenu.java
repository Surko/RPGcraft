/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.UiResource;


/**
 * FactoryMenu je spolocny nazov pre objekty typu AbstractMenu ktore boli vytvoreni 
 * umelo. Umele vytvorenie moze byt zavolane z listenerov (pomocou akcii). Na vytvorenie
 * umeleho menu nam staci resource z ktoreho vytvorime menu. Inicializacie, update 
 * su podedene od AbstractMenu. Keby chceme do menu pridat inputHandling tak znova jedine 
 * skrz listenery.
 * @author kirrie
 */
public final class FactoryMenu extends AbstractMenu {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private int w,h;
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory "> 
    /**
     * Konstruktor ktory vytvori novu instanciu triedy. Inicializujeme v nej
     * resource a vkladame toto menu do hash mapy menuMap. V tomto konstruktore
     * tiez volame metodu initialize na zinicializovanie resource podla nacitaneho
     * resource.
     * @param res UiResource z ktoreho vznika menu
     */
    public FactoryMenu(UiResource res) {
        this.res = res;
        initialize(Container.mainContainer,InputHandle.getInstance());
        menuMap.put(res.getId(), this);        
    }   
    // </editor-fold>
       
    // <editor-fold defaultstate="collapsed" desc=" Update + handle ">    
    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);        
    }
    
    @Override
    public void inputHandling() {
        
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    @Override
    public void setWidthHeight(int w, int h) {
        this.w = w;
        this.h = h;
    }    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
     @Override
    public String getName() {
        if (res != null) {
            return res.getId();
        }
        return null;
    }

    @Override
    public int getWidth() {
        return w;
    }

    @Override
    public int getHeight() {
        return h;
    }   
    // </editor-fold>               
}
