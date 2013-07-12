/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import rpgcraft.graphics.Colors;
import rpgcraft.resource.UiResource;

/**
 * Abstraktna trieda ktora vytvara interface pre vsetky komponentove typy.
 * Kazdy typ v tomto packagi dedi od tejto triedy vsetky metody ktore su pre ne spolocne.
 * Spolocne metody su settery layout typu, vrchnej a spodnej farby v komponente
 * Ostatne podedene triedy specifikuju priamo co udrzuje typ.
 */
public abstract class AbstractType implements Cloneable {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Constraints
    protected Object gc;
    // Typy ui
    protected UiResource.UiType uitype;
    // Typy layoutu
    protected UiResource.LayoutType layoutType;
    // Farby pozadia komponenty
    protected Color bColor,tColor;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktor volaju ostatne podedene triedy ktory nastavuje typ ui.
     * @param type Ui typ komponenty
     */
    public AbstractType(UiResource.UiType type) {
        this.uitype = type;
    }                 
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi layout typ komponenty podla parametru <b>layoutType</b>.
     * @param layoutType Layout typ komponenty
     */
    public void setLayoutType(UiResource.LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    /**
     * Metoda ktora nastavi constraints pre komponentu
     * @param gc Constraints pre komponentu (GridBagConstraints)
     */
    public void setConstraints(Object gc) {
        this.gc = gc;
    }

    /**
     * Metoda ktora nastavi vrchnu farbu pozadia podla textu <b>color</b>
     * @param color Farba pozadia
     * @throws Exception Vynimka pri parsovani farby
     */
    public void setTopColor(String color) throws Exception {
        this.tColor = Colors.parseColor(color);
    }

    /**
     * Metoda ktora nastavi spodnu farbu pozadia podla textu <b>color</b>
     * @param color Farba pozadia
     * @throws Exception Vynimka pri parsovani farby
     */
    public void setBackColor(String color) throws Exception {
        this.bColor = Colors.parseColor(color);
    }

    /**
     * Metoda ktora nastavi spodnu farbu pozadia na farbu <b>color</b>
     * @param color Farba na nastavenie
     */
    public void setBackColor(Color color) {
        this.bColor = color;
    }

    /**
     * Metoda ktora nastavi vrchnu farbu pozadia na farbu <b>color</b>
     * @param color Farba na nastavenie
     */
    public void setTopColor(Color color) {
        this.tColor = color;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora nastavi constraints.
     * @return Omedzenia pre komponentu
     */
    public Object getConstraints() {
        return gc;
    }

    /**
     * Metoda ktora vrati vrchnu farbu pozadia
     * @return Vrchna farba pozadia
     */
    public Color getTopColor() {
        return tColor;
    }

    /**
     * Metoda ktora vrati spodnu farbu pozadia
     * @return Spodna farba pozadia
     */
    public Color getBackColor() {
        return bColor;
    }

    /**
     * Metoda ktora vrati ui typ komponenty
     * @return Ui typ komponenty
     */
    public UiResource.UiType getUiType() {
        return uitype;
    }

    /**
     * Metoda ktora vrati layout typ komponenty
     * @return Layout typ komponenty
     */
    public UiResource.LayoutType getLayoutType() {
        return layoutType;
    }

    /**
     * Metoda ktora vrati elementy nachadzajuce sa v komponente. V tejto metode 
     * vratime null, kedze nevieme o aku komponentu sa jedna (komponenty ako text nemaju ziadne elementy,
     * pricom panel moze mat)
     * @return List s elementmi.
     */
    public ArrayList<UiResource> getElements() {
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Klonovanie ">
    /**
     * Metoda ktora okopiruje/sklonuje tento typ a kopiu vrati.
     * @return Okopirovany typ tohoto typu.
     * @throws CloneNotSupportedException Ked metoda nepodporuje klonovanie
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractType clone = (AbstractType)super.clone();
        if (layoutType != null) {
            switch (layoutType) {
                case GRIDBAGSWING : {
                    clone.gc = ((GridBagConstraints)gc).clone();
                } break;                                    
            }
        }
        clone.bColor = bColor;
        clone.tColor = tColor;
        return clone;
    }
    // </editor-fold>
}
