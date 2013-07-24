/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics;

import java.awt.Color;
import java.text.ParseException;
import java.util.HashMap;
import rpgcraft.resource.StringResource;

/**
 * Trieda Colors ktora definuje vsetky pouzite farby v aplikacii. Farby sa ukladaju 
 * v statickej premennej colors ktora uchovava vsetky farby v HashMape s textovym klucom.
 * Mozne kluce v tejto mape su definovane takisto v tejto triede. Volanie farieb je bez vytvarania
 * novej instancie. Vsetky farby su pridane do mapy statickym initializatorom.
 * @author Kirrie
 */
public class Colors {
    /**
     * Ulozene farby v hashmape s klucom odpovedajucim textu
     */
    private static HashMap<String,Color> colors = new HashMap<>();
    
    /**
     * Premenne na rozparsovanie farby z textu. Napriklad #120,120,120 alebo missColor.
     */
    private static final String RGBSTPOINT = "#", RGBDELIM = ",";
    
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
    /**
     * Farba zobrata z Color.Black
     */
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
     * Transparentna farba vytvarajuca efekt podvecera
     */
    public static final String eveningColor = "eveningColor";
    /**
     * Farba popisujuca zivot hraca
     */
    public static final String healthColor = "healthColor";
    /**
     * Farba popisujuca obranu hraca
     */
    public static final String defenseColor = "defenseColor";
    /**
     * Farba popisujuca staminu hraca
     */
    public static final String staminaColor = "staminaColor";
    /**
     * Priehladna farba na pozadi inventara
     */
    public static final String transInvBackColor = "transInvBackColor";
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
     * Farba pri utoku na dlazdicu.
     */
    public static final String tileAttackColor = "tileAttackColor";
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
        colors.put(eveningColor, new Color(20, 20, 125, 80));
        colors.put(healthColor, Color.RED);
        colors.put(defenseColor, Color.YELLOW);
        colors.put(staminaColor, new Color(7, 194, 17));
        colors.put(transInvBackColor, new Color(224, 224, 224, 100));
        colors.put(invBackColor, new Color(224, 224, 224));
        colors.put(invOnTopColor, new Color(145, 145, 145, 100));
        colors.put(selectedColor, new Color(0, 0, 0, 50));
        colors.put(tileAttackColor, new Color(7, 194, 17));
    }
 
    
    /**
     * Metoda getColor vrati farbu z mapy <b>colors</b>, ked nenajde v nej
     * tak vyhlada pomocou metody getColor definovanej v triede Color, pomocou parametru clrName,
     * ktory ma v sebe vyssie spominane textove hodnoty pre rozne farby.
     * @param colName Kluc v mape ktoremu prislucha farba
     * @return Farbu z mapy <b>colors</b>
     */
    public static Color getColor(String colName) {
        return colors.containsKey(colName) ? colors.get(colName) : Color.getColor(colName);        
    }
    
    /**
     * Metoda parseColor rozparsuje farbu zadanu textovym parametrom colName. 
     * Ked text vyhovuje stavbe ako ma farba v xml vyzerat tak metoda vyda spravnu farbu.
     * Inak hodi ParseException
     * @param colName Text s hodnotou farby na rozparsovanie.
     * @return Farba po rozparsovani textu.
     * @see ParseException
     * @throws ParseException 
     */
    public static Color parseColor(String colName) throws Exception {
        
        if (colName.startsWith(RGBSTPOINT)) {
            String[] rgb = colName.substring(1).split(RGBDELIM);
            
            if (rgb.length != 4) {
                throw new ParseException(StringResource.getResource("_eparse",
                        new String[] { "parseColor("+colName+")"}),1);
            }
            
            int r = Integer.parseInt(rgb[0]);
            int g = Integer.parseInt(rgb[1]);
            int b = Integer.parseInt(rgb[2]);        
            int a = Integer.parseInt(rgb[3]);
            return new Color(r,g,b,a);
            
        } else {
          return colors.containsKey(colName) ? colors.get(colName) : Color.getColor(colName);   
        }      
        
    }

    
    
}
