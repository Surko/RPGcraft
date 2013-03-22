/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.*;
import rpgcraft.MainGameFrame;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.map.Save;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingCustomButton;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public class DataUtils {        
    
    public enum Data {
        SAVE
    }
    
    private static final Logger LOG = Logger.getLogger(DataUtils.class.getName());
    
    private volatile static int depth; 
    private volatile static int cycleCounter;
    
    public synchronized static Object[][] getDataArrays(String data) {                
        ArrayList<ArrayList<Object>> resultList = new ArrayList<>();
        
        depth = 0;
        cycleCounter = 0;
        
        // Navratenie dat podla stringu data
        getDataFromString(data, resultList);   
        
        if (depth != 0) {
            LOG.log(Level.SEVERE, StringResource.getResource("_dldepth"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_dldepth"),
                    null).renderSpecific(StringResource.getResource("_label_parsingerror"));           
        }
        
        int height = resultList.size();
        int width = 0;
        if (height > 0) {
            width = resultList.get(0).size();
        }
        Object[][] resultArray = new Object[height][width];
        
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = resultList.get(i).toArray();
        }
        
        depth = 0;
        return resultArray;
    }    
    
    private static int getDataFromString(String data, ArrayList<ArrayList<Object>> result) {
        int index = 0;
        
        cycleCounter++;
        if (cycleCounter > 100) {
            LOG.log(Level.SEVERE, StringResource.getResource("cyclestate"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("cyclestate"),
                    null).renderSpecific(StringResource.getResource("_label_cycle"));
        }
        while (data.length() > index) {                        
            switch (data.charAt(index)) {                
                case '[' : {
                   if (depth == 0) {
                       index++;
                       depth++;
                       break;
                   }
                   if (depth == 1) {                       
                       depth++;
                       index++;
                       ArrayList<Object> row = new ArrayList<>();                       
                       index += getDataArray(data.substring(index), row);
                       result.add(row);
                   }                                       
                } break; 
                case ']' : {
                    depth--;  
                    index++;
                } break;
                case ',' : {
                    index++;
                } break;
                case '#' : {
                    index++;
                    if (depth == 1) {
                        String inf = "";
                        char c = data.charAt(index);
                        while (c != '(') {
                            inf += c;
                            c = data.charAt(++index);
                        }           
                        
                        c = data.charAt(++index);
                        ArrayList<String> intParams = new ArrayList();
                        String param = "";
                        while (c != ')') {
                            if (c == ',') {
                                c = data.charAt(++index);
                                intParams.add(param);
                                param = "";
                            } else {                                
                                param += c;
                                c = data.charAt(++index);
                                if (c == ')') {
                                    index++;
                                    intParams.add(param);
                                    param = "";
                                }
                            }                            
                        }
                        getInfToList(inf, intParams, result, null);                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                    }
                }                                    
            }
            
        }
        return index;
    }
    
    private static int getDataArray(String data, ArrayList<Object> result) {
        int index = 0;
        
        cycleCounter++;
        if (cycleCounter > 100) {
            LOG.log(Level.SEVERE, StringResource.getResource("cyclestate"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("cyclestate"),
                    null).renderSpecific(StringResource.getResource("_label_cycle")); 
            return 0;
        }
        
        while (data.length() > index) {      
            switch (data.charAt(index)) {
                case '@' : {
                        index++;
                        if (depth > 1) {
                            String parsed = "";
                            char c = data.charAt(index);
                            while ((c != ']')&&(c != ',')) {
                                parsed += c;
                                c = data.charAt(++index);
                            }                            
                            result.add(StringResource.getResource(parsed));
                        } else {
                            LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                        }
                    } break;
                case ']' : {
                    depth--;  
                    index++;
                    return index;
                }
                case '[' : {
                    index++;
                    if (depth > 1) {
                        depth++;                    
                       getDataArray(data.substring(index), result);
                    }
                } break;
                case '#' : {
                    index++;
                    if (depth > 1) {
                        String inf = "";
                        char c = data.charAt(index);
                        while (c != '(') {
                            inf += c;
                            c = data.charAt(++index);
                        }           
                        
                        index++;
                        ArrayList<String> intParams = new ArrayList();
                        String param = "";
                        while (c != ')') {
                            if (c == ',') {
                                intParams.add(param);
                            } else {
                                param += c;
                            }                            
                        }
                        getInfToList(inf, intParams, null, result);                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                    }
                } break; 
                case ',' : {
                    index++;
                } break;
                default : {
                    if (depth > 1) {
                        String parsed = "";
                        char c = data.charAt(index);
                        while ((c != ']')&&(c != ',')) {
                            parsed += c;
                            c = data.charAt(++index);
                        }
                        result.add(parsed);                   
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                    }
                }    
            }
        }
        return index;
    }

    
    /**
     * Metoda ma za ulohu poskladat do jedneho ArrayListu result informacie podla daneho tagu
     * SAVE, etc... .
     * @param data
     * @param result
     * @return 
     */
    
    private static void getInfToList(String inf, ArrayList<String> param,ArrayList<ArrayList<Object>> result1, ArrayList<Object> result2) {
        
        if (result1 != null) {
            try {
                switch (Data.valueOf(inf)) {
                    case SAVE : {
                        ArrayList<ArrayList<Object>> saveList = Save.getGameSavesParam(param);
                        for (ArrayList<Object> list : saveList) {
                            result1.add(list);
                        }
                    } break;                
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_ndinfo"));
                new MultiTypeWrn(e, Color.red, StringResource.getResource("_ndinfo"),
                    null).renderSpecific(StringResource.getResource("_label_parsingerror")); 
                return;                
            }
        } else {        
            try {
                switch (Data.valueOf(inf)) {
                    case SAVE : {
                        ArrayList<ArrayList<Object>> saveList = Save.getGameSavesParam(param);
                        for (ArrayList<Object> list : saveList) {
                            result2.addAll(list);
                        }
                    } break;
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, StringResource.getResource("_ndinfo"));
                new MultiTypeWrn(e, Color.red, StringResource.getResource("_ndinfo"),
                    null).renderSpecific(StringResource.getResource("_label_parsingerror")); 
                return; 
            }
        }              
    }
    
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        Calendar calendar = Calendar.getInstance();
        
        return dateFormat.format(calendar.getTime());
        
    }
    
    public static Collection<rpgcraft.panels.components.Container> getPanelContainers() {
        return MainGameFrame.game.getChildContainers();
    }
    
    public static Component getComponentOfId(String id) {
        Collection<rpgcraft.panels.components.Container> containers = MainGameFrame.game.getChildContainers();
        for (rpgcraft.panels.components.Container cont : containers) {
            if (cont.getResource().getId().equals(id)) {
                return cont.getComponent();
            }
        }
        return null;
    }
    
    public static Collection<rpgcraft.panels.components.Container> getMenuContainers(AbstractMenu menu) {
        return menu.getContainers();
    }
    
    public static Container getComponentFromResource(UiResource resource, AbstractMenu menu, Container src, Container parent) {
        Component c = null;
        
        if (src == null) {        
            src = new Container(resource, 0, 0, 0, 0, parent);
        } else {
            if (src.getComponent() != null) {
                parent.getComponent().removeComponent(src.getComponent());
            }
        }
        switch (resource.getUiType()) {
            case BUTTON : {                      
                 switch (resource.getLayoutType()) {
                    case INGAME : {
                        // Vytvorenie tlacidla z resource nachadzajuci sa v kontajnery cont a v tomto menu.
                        c = new SwingImageButton(src, menu);
                        c.addActionListeners(resource.getMouseActions());
                        c.addActionListeners(resource.getKeyActions());
                        src.setComponent(c); 
                    } break;
                    default : {                                                          
                        c = new SwingImageButton(src, menu);
                        c.addActionListeners(resource.getMouseActions());
                        c.addActionListeners(resource.getKeyActions());
                        src.setComponent(c);                             
                        parent.getComponent().addComponent(c,resource.getConstraints());                                                        
                    }
                }
            } break;
            case PANEL : {
                switch (resource.getLayoutType()) {
                    case INGAME : {                                   
                    }                 
                    default : {                           
                        c = new SwingImagePanel(src, menu);
                        c.addActionListeners(resource.getMouseActions());   
                        c.addActionListeners(resource.getKeyActions()); 
                        src.setComponent(c);                             
                        parent.getComponent().addComponent(c,resource.getConstraints());                         
                    } break;
                }
            } break;
            case TEXT : {                
                    switch (resource.getLayoutType()) {
                        case INGAME : {                            
                            c = new SwingText(src, menu); 
                            src.setComponent(c);
                            parent.getComponent().addComponent(c);
                        } break;
                        default : {                           
                            c = new SwingText(src, menu);
                            c.addActionListeners(resource.getMouseActions());   
                            c.addActionListeners(resource.getKeyActions()); 
                            src.setComponent(c);
                            parent.getComponent().addComponent(c,resource.getConstraints()); 
                        } break;
                    }
            } break;
            case EDITTEXT : {                
                    switch (resource.getLayoutType()) {
                        case INGAME : {                            
                            c = new SwingInputText(src, menu); 
                            src.setComponent(c);
                            parent.getComponent().addComponent(c);
                        } break;
                        default : {                           
                            c = new SwingInputText(src, menu);
                            c.addActionListeners(resource.getMouseActions());   
                            c.addActionListeners(resource.getKeyActions()); 
                            src.setComponent(c);
                            parent.getComponent().addComponent(c,resource.getConstraints()); 
                        } break;
                    }
            } break;
            case IMAGE : {

            } break;
            case LIST : {
                switch (resource.getLayoutType()) {
                    case INGAME : {                              

                    } break;                    
                    default : {                        
                        c = new SwingImageList(src, menu); 
                        c.addActionListeners(resource.getMouseActions());   
                        c.addActionListeners(resource.getKeyActions()); 
                        src.setComponent(c);                            
                        parent.getComponent().addComponent(c,resource.getConstraints());                             
                    } break;
                }
            }
        }
        if (src != null) {
            parent.addContainer(src);
        }
        return src;
    }
    
}
