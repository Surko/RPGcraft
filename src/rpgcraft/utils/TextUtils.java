/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.ListType;
import sun.font.FontDesignMetrics;

/**
 *
 * @author Surko
 */
public class TextUtils { 
    public static final String DELIM = "[ ]+";
    public static final Font DEFAULT_FONT = Font.decode("Dialog-plain-12");
    
    // <editor-fold defaultstate="collapsed" desc="Staticke metody nad textom">
    /**
     * Metoda getResourceText ziskava text z resource textu. V resource textu sa moze nachadzat
     * "@" co znamena ze vsetko za tymto znakom je id pre textovy resource zadany v StringResource.
     * Text bez zavinaca nam naznacuje ze pouzivame priamo tento text;
     * @param resText Pravy text ziskany z resource.
     * @return 
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
    
    public static int getTextHeight(Font font) {
        FontMetrics fm = FontDesignMetrics.getMetrics(font);
        return (fm.getAscent() - fm.getDescent());
    }
    
    public static int getTextWidth(Font font, String title) {
        FontMetrics fm = FontDesignMetrics.getMetrics(font);
        return fm.stringWidth(title);
    }
    
    public static int[] getTextSize(Font font, String title) {
        int[] result = new int[2];
        if ((title == null)||(font == null)) return result;
        
        FontMetrics fm = FontDesignMetrics.getMetrics(font);
        result[0] = fm.stringWidth(title);
        result[1] = (fm.getAscent() - fm.getDescent());
        
        return result;
    }
    
    public static int getRowCount(String rows) {
        if ((rows != null)) {
            try {
            switch (ListType.RowType.valueOf(rows)) {
                case AUTO : {
                    return ListType.AUTOTYPE;
                }   
                default : return ListType.DEFAULTTYPE;
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
    
    public static ArrayList<String> parseToSize(int size, String text, Font font) {        
        return parseToSize(null, size, text, font);
    }
    
    public static ArrayList<String> parseToSize(ArrayList<String> lines, int size, String text, Font font) {   
        if (lines == null) {
            lines = new ArrayList<>();
        }
        
        String line = "", tryLine = "";
        int txtSize = 0, _psize = 0;
               
        String[] _blankFree = text.split(DELIM);        
        System.out.println(size + " " + lines + " pocet " + _blankFree.length);
        
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
                txtSize = _psize;                
                lines.add(line);                
                line = _blankFree[i];                
            } else {
                line = tryLine;
            }              
            
        }
        
        if (!line.equals("")) {
            lines.add(line);
        }
            System.out.println(lines.get(0) + " " + lines.get(1));
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
