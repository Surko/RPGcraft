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
import java.util.logging.*;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.map.Save;
import rpgcraft.resource.StringResource;

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
    
}
