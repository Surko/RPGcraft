/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.ListType;

/**
 * Utility trieda ktora v sebe zdruzuje rozne metody pre pracu s textom. Trieda je cela staticka =>
 * mozne k nej pristupovat z kazdej inej triedy ci instancie. 
 * Metoda dokaze urcovat dlzky zadaneho textu podla fontu, rozparsovavat text podla dlzok
 * ci ziskat informaciu (stacktrace) pre vynimky.
 */
public class TextUtils { 
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    public static final String DELIM = "[ ]+";
    public static final Font DEFAULT_FONT = Font.decode("Dialog-plain-12");
    // Renderovaci kontext pre ziskanie dlzok textu
    public static FontRenderContext ctx;
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc="Staticke metody nad textom">
    /**
     * Metoda getResourceText ziskava text z resource textu. V resource textu sa moze nachadzat
     * "@" co znamena ze vsetko za tymto znakom je id pre textovy resource zadany v StringResource.
     * Text bez zavinaca nam naznacuje ze pouzivame priamo tento text;
     * @param resText Pravy text ziskany z resource.
     * @return Text z locales pod danym menom
     */
    public static String getResourceText(String resText) {
        String[] _stext = resText.split("@");
        if (_stext.length==1) {
            return resText;
        } else {
            if ("".equals(_stext[0])) { 
                return StringResource.getResource(_stext[1]);
            }
            else {
                Logger.getLogger(TextUtils.class.getName()).log(Level.INFO, StringResource.getResource("_istring"));
                return null;
            }
        }
    }    
    
    /**
     * Metoda ktora ziska vysku textu podla fontu zadaneho parametrom <b>font</b>.
     * Na toto vyuzivame ziskanie metrik a potom ziskanie vysky podla getAscent - getDescent.
     * @param font Font z ktoreho urcujeme vysky
     * @return Vyska testu
     */
    public static int getTextHeight(Font font) {
        LineMetrics fm = font.getLineMetrics(DELIM, ctx);
        return (int)(fm.getAscent() - fm.getDescent() + 2);
    }
    
    /**
     * Metoda ktora ziska siku textu podla fontu zadaneho parametrom <b>font</b>.
     * Na toto vyuzivame metodu getStringBounds.
     * @param font Font z ktoreho urcujeme dlzky
     * @return Dlzka testu
     */
    public static int getTextWidth(Font font, String title) {        
        return (int)font.getStringBounds(title, ctx).getWidth();
    }
    
    /**
     * Metoda ktora ziska vysku aj sirku textu ktoru navrati. Na ziskanie pouzivame
     * metody getStringBound aj getAscent - getDescent.
     * @param font Font podla ktoreho urcime vysku a sirku textu
     * @param title Text z ktoreho urcujeme dlzky
     * @return Sirka a vyska textu
     */
    public static int[] getTextSize(Font font, String title) {
        int[] result = new int[2];
        if ((title == null)||(font == null)) {
            return result;
        }
        
        LineMetrics fm = font.getLineMetrics(title, ctx);
        result[0] = (int)font.getStringBounds(title, ctx).getWidth();
        result[1] = (int)(fm.getAscent() - fm.getDescent()) + 2;
        
        return result;
    }
    
    /**
     * Metoda ktora vrati typ na pocet riadkov podla parametru <b>rows</b> (AUTO, DEFAULTTYPE).      
     * @param rows Text z ktoreho urci pocet riadkov
     * @return Typ poctu riadkov 
     */
    public static int getRowCount(String rows) {
        if ((rows != null)) {
            try {
                ListType.RowType type = ListType.RowType.valueOf(rows);
                if (type.equals(ListType.RowType.AUTO)) {
                    return ListType.AUTOTYPE;
                } else {
                    return ListType.DEFAULTTYPE;
                }
            } catch (Exception e) {
                try {
                    return Integer.parseInt(rows);
                } catch (Exception ex) {
                    Logger.getLogger(TextUtils.class.getName()).log(Level.WARNING, StringResource.getResource("_istring"));
                    return ListType.DEFAULTTYPE;
                }
            }
        } else {
           return ListType.DEFAULTTYPE; 
        }        
    }
    
    /**
     * Metoda ktora rozparsuje text zadany v parametri <b>text</b> podla dlzky <b>size</b>.
     * Text uvazujeme s fontom <b>font</b>. Na ziskanie pouzivame metodu parseToSize so
     * 4 parametrami
     * @param size Dlzka do akej parsujeme text
     * @param text Text ktory parsujeme
     * @param font Font ktory ma text
     * @param maxW Maximalna dlzka textu
     * @return List s rozparsovanymi textmi
     */
    public static ArrayList<Pair<String,Integer>> parseToSize(int size, String text, Font font) {        
        return parseToSize(null, size, text, font);
    }
    
    /**
     * Metoda ktora rozparsuje text zadany v parametri <b>text</b> podla dlzky <b>size</b>.
     * Text uvazujeme s fontom <b>font</b>. Rozparsovane texty pridavame do listu <b>lines</b> .
     * @param lines List do ktoreho pridavame rozparsovane texty.
     * @param size Dlzka podla ktorej parsujeme texty
     * @param text Text na parsovanie
     * @param font Font ktory ma text
     * @param maxW Maximalna dlzka textu
     * @return List s rozparsovanymi textmi.
     */
    public static ArrayList<Pair<String,Integer>> parseToSize(ArrayList<Pair<String,Integer>> lines, int size,
            String text, Font font) {                 
        if (lines == null) {
            lines = new ArrayList<>();
        }
        
        if (text == null) {
            return lines;
        }                
        
        String line = "", tryLine = "";
        int txtSize = 0, _psize = 0;        
        String[] _blankFree = text.split(DELIM);        
        
        //System.out.println(size + " " + lines + " pocet " + _blankFree.length);        
        if (_blankFree.length > 0 && line.equals("")) {
            line = _blankFree[0];            
            txtSize = getTextWidth(font, line);            
        } else {
            return null;
        }
        
        for (int i = 1; i < _blankFree.length; i++) {  
            _psize = txtSize;
            tryLine = line + " " + _blankFree[i];
            txtSize = getTextWidth(font, tryLine);
            if (txtSize  > size) {
                lines.add(new Pair(line, _psize)); 
                txtSize -= _psize;                                               
                line = _blankFree[i];                
            } else {
                line = tryLine;
            }                          
        }
        
        if (!line.equals("")) {            
            lines.add(new Pair(line, txtSize)); 
        }
        
        return lines;
    }
    
    /**
     * Prevadza danu vynimku typu Exception do Stringu. Pri neznamej vynimke vrati String s informaciou
     * o neznamej vynimke
     * @param e Vynimka na prevedenie
     * @return Vynimku prevedenu do Stringu [type:STRING]
     */
    
    public static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
            } catch(Exception e2) {                
                return stack2string(e2);
            }
    }
    
    // </editor-fold>
    
    
}
