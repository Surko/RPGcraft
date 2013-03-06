/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics;

import java.awt.Color;
import java.util.HashMap;

/**
 * Trieda Colors ktora definuje vsetky pouzite farby v aplikacii. Farby sa ukladaju 
 * v statickej premennej colors ktora uchovava vsetky farby v HashMape s textovym klucom.
 * Mozne kluce v tejto mape su definovane takisto v tejto triede. Volanie farieb je bez vytvarania
 * novej instancie. Vsetky farby su pridane do mapy statickym initializatorom.
 * @author Kirrie
 */
public class Colors {    
    private static HashMap<String,Color> colors = new HashMap<>();
    
    /**
     * Modra farba indikujuca Error suvisiaci s chybajucim suborom.
     */
    public static final String missError = "missColor";
    /**
     * Zlta farba pre vypis FPS
     */
    public static final String fpsColor = "fpsColor";
    /**
     * Cervena farba indikujuca Error pri nejakej vnutornej chybe
     */
    public static final String  internalError = "internalError";
    /**
     * Transparentna farba s ciernym zakladom.
     */
    public static final String transparentColor = "transColor";
    /**
     * Nepriehladna cierna farba
     */
    public static final String fullBlack = "fullBlack";
    public static final String Black = "Black";
    /**
     * Transparentna farba vytvarajuca efekt vychadzajuceho slnka
     */
    public static final String morningColor = "mornColor";
    /**
     * Transparentna farba vytvarajuca efekt noci
     */
    public static final String nightColor = "nightColor";
    /**
     * Farba popisujuca zivot hraca
     */
    public static final String healthColor = "healthColor";
    /**
     * Farba popisujuca obranu hraca
     */
    public static final String defenseColor = "defenseColor";
    /**
     * Farba na pozadi inventara
     */
    public static final String invBackColor = "invBackColor";
    /**
     * Farba v popredi inventara
     */
    public static final String invOnTopColor = "invOnTopColor";
    /**
     * Farba pri oznaceni elementu v liste
     */
    public static final String selectedColor = "selectedColor";
    /**
     * Staticky initializator ktory do mapy <b>colors</b> vlozi vsetky farby
     */
    static {
        colors.put(fpsColor, Color.YELLOW);
        colors.put(missError, Color.getHSBColor(0.56f, 1.0f, 0.8f));
        colors.put(internalError, Color.RED);
        colors.put(transparentColor, new Color(0, 0, 0, 0));
        colors.put(fullBlack, Color.BLACK);
        colors.put(Black, Color.BLACK);
        colors.put(morningColor, new Color(211,175,90,25));
        colors.put(nightColor, new Color(20, 20, 125, 150));
        colors.put(healthColor, Color.RED);
        colors.put(defenseColor, Color.YELLOW);
        colors.put(invBackColor, new Color(224, 224, 224));
        colors.put(invOnTopColor, new Color(145, 145, 145, 100));
        colors.put(selectedColor, new Color(0, 0, 0, 50));
    }
 
    
    /**
     * Metoda getColor vrati farbu z mapy <b>colors</b>, ked nenajde v nej
     * tak vyhlada pomocou metody getColor definovanej v triede Color, pomocou parametru clrName,
     * ktory ma v sebe vyssie spominane textove hodnoty pre rozne farby.
     * @param clrName Kluc v mape ktoremu prislucha farba
     * @return Farbu z mapy <b>colors</b>
     */
    public static Color getColor(String clrName) {
        return colors.containsKey(clrName) ? colors.get(clrName) : Color.getColor(clrName);        
    }

    
    
}
