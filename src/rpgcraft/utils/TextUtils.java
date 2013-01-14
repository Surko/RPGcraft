/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;

/**
 *
 * @author Surko
 */
public class TextUtils {
    
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

    // </editor-fold>
    
    
}
