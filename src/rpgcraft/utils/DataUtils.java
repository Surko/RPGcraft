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
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
import rpgcraft.MainGameFrame;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.entities.Player;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ui.menu.Menu;
import rpgcraft.map.Save;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingBar;
import rpgcraft.panels.components.swing.SwingImage;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.plugins.RenderPlugin;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public class DataUtils {        
    
    public enum Data {
        SAVE,
        PLAYER_ITEMS,
        RENDER,
        ITEM
    }        
    
    public enum DataValues {
        HEALTH,
        MAXHEALTH,
        STRENGTH
    }
    
    private static final Logger LOG = Logger.getLogger(DataUtils.class.getName());    
    
    private volatile static ExecutorService es = Executors.newCachedThreadPool();
    private volatile static int depth; 
    private volatile static int cycleCounter;
    
    public static ConcurrentHashMap<String, Object> variables = new ConcurrentHashMap<>();
    
    public static String[] split(String data, char delim) {
        if (data.equals("")) {
            return null;
        }
        
        ArrayList<String> retList = new ArrayList<>();
        int beg = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == delim) {                
                retList.add(data.substring(beg,i));
                beg = i + 1;
            }
        }
        retList.add(data.substring(beg));
        return retList.toArray(new String[0]);        
        
    }
    
    public synchronized static Object[][] getDataArrays(String data, Object srcObject) {                
        ArrayList<ArrayList<Object>> resultList = new ArrayList<>();
        
        depth = 0;
        cycleCounter = 0;
        
        // Navratenie dat podla stringu data
        getDataFromString(data, srcObject, resultList);   
        
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
    
    private static int getDataFromString(String data, Object srcObject, ArrayList<ArrayList<Object>> result) {
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
                       index += getDataArray(data.substring(index), srcObject, row);
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
                        getInfToList(inf, intParams, srcObject, result, null);                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                    }
                }                                    
            }
            
        }
        return index;
    }
    
    private static int getDataArray(String data, Object srcObject, ArrayList<Object> result) {
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
                       getDataArray(data.substring(index), srcObject, result);
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
                        getInfToList(inf, intParams, srcObject, null, result);                        
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
     * SAVE, PLAYER_ITEMS, etc... . Moznost pridat dalsie tagy je len na programatorovi.
     * Informacie mozme dostat dvoma sposobmi : <br>
     * - v liste <b>result1</b> je list listov. Tato moznost pouzivana ked volame tuto metodu
     * z metody getDataFromString. <br>
     * - v liste <b>result2</b> je list s datami. Tato moznost je pouzivane ked volame tuto metodu
     * z metody getDataArray. <br>
     * Dolezite je vediet ze cele data su vzdy predavane ako 2-dimenzionalne pole (rows, cols) a pri metode getDataFromString
     * mozme pouzivat priamo ziskavanie informacii z tagov. Kedze ale informacie z tagov su tiez uz
     * v 2-dimenzionalnom poli tak musime pouzivat referenciu na list listov aby sme to vedeli rozpoznat.
     * V druhom pripade pri volani z metody getDaraArray uz mame vytvorene pole poli a pracujeme
     * len s jednym riadkom a ked sa tam nachadza nejaky tag tak nevytvarame dalsie pole
     * ale iba naskladame vsetky informacie ako keby do jedneho riadku.
     * List s tymto musi ale vediet pracovat, preto je dolezite aby sa nezabudlo
     * ze keby nahodou list ma podelement dalsi list tak Cursor v liste musi pracovat sekvencne 
     * aby vyplnil ten podelement.    
     * Pocitame s tym ze jeden z listov predanych ako parameter je nenulovy.
     * @param inf Tag podla ktoreho vyberie informacie do listu
     * @param param Parametre podla ktorych vyplni vysledny list.
     * @param result1 List listov v ktorom su ulozene informacie po riadkoch.
     * @param result2 List s informaciami/datami v ktorom su ulozene informacie sekvencne.List sluzi ako jeden riadok
     * @return V jednom z poli vsetky data
     */    
    private static void getInfToList(String inf, ArrayList<String> param, Object srcObject,  
            ArrayList<ArrayList<Object>> result1, ArrayList<Object> result2) {  
        
        ArrayList<ArrayList<Object>> infList = null;
        try {
            switch (Data.valueOf(inf)) {
                case SAVE : {
                    infList = Save.getGameSavesParam(param);                    
                } break;                
                case PLAYER_ITEMS : {
                    // Tolerovany srcObject - Entity alebo Gamemenu z ktoreho si vytiahneme Playera.
                    if (srcObject instanceof Entity) {                                                
                        infList = Player.getInventoryItemsParam((Entity)srcObject, param);
                        break;
                    }
                    if (srcObject instanceof GameMenu) {
                        infList = Player.getInventoryItemsParam(((GameMenu)srcObject).player, param);
                        break;
                    }
                    LOG.log(Level.SEVERE, StringResource.getResource(null));
                    new MultiTypeWrn(null, Color.red, StringResource.getResource(null),
                            null).renderSpecific(StringResource.getResource(null));                    
                } break;
                case RENDER : {
                    if (srcObject instanceof GameMenu) {                        
                        infList = RenderPlugin.getRenderParam(param);
                    }
                } break;
                case ITEM : {
                    if (srcObject instanceof Item) {
                        infList = Item.getItemInfo((Item)srcObject);
                    }
                } break;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, StringResource.getResource("_ndinfo"));
            new MultiTypeWrn(e, Color.red, StringResource.getResource("_ndinfo"),
                null).renderSpecific(StringResource.getResource("_label_parsingerror")); 
            return;                
        }
        
        if (result1 != null) {
            for (ArrayList<Object> list : infList) {
                result1.add(list);
            }
        } else {
            for (ArrayList<Object> list : infList) {
                result2.addAll(list);
            }
        }                       
    }
    
    /**
     * Metoda ktora vraci specificky aktualny cas a datum podla vzoru zadaneho parametrom <b>param</b>
     * Tento vzor dokazu spracovavat objekty dediace od DateFormat. Na zistenie casu
     * pouzivame metodu getTime z instancie Calendar.
     * @param param Format datumu.
     * @return Text s aktualnym datumom
     * @see DateFormat
     */
    public static String getSpecificDate(String param) {
        DateFormat dateFormat = new SimpleDateFormat(param);
        
        Calendar calendar = Calendar.getInstance();
        
        return dateFormat.format(calendar.getTime());
    }
    
    /**
     * Metoda ktora vraci specificky cas a datum podla vzoru zadaneho parametrom <b>param</b>
     * Tento vzor dokazu spracovavat objekty dediace od DateFormat. Na zistenie casu
     * pouzivame metodu getTime z instancie Calendar. Cas ziskavame z parametru <b>time</b>
     * @param time Cas ulozeny v long tvare.
     * @return Text s casom
     * @see DateFormat
     */
    public static String getSpecificDate(long time) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(time);                
        return dateFormat.format(date);
    }
    
    /**
     * Metoda ktora vraci specificky aktualny cas a datum podla vzoru dd/MM/yyyy HH:mm:ss.
     * Tento vzor dokazu spracovavat objekty dediace od DateFormat. Na zistenie casu
     * pouzivame metodu getTime z instancie Calendar.     
     * @return Text s aktualnym datumom
     * @see DateFormat
     */
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        Calendar calendar = Calendar.getInstance();
        
        return dateFormat.format(calendar.getTime());
        
    }
    
    /**
     * Metoda ktora vrati kolekciu kontajnerov nachadzajuce sa v hlavnom game kontajneri.
     * @return Kolekcia kontajnerov
     */
    public static Collection<rpgcraft.panels.components.Container> getPanelContainers() {
        return MainGameFrame.game.getChildContainers();
    }
    
    /**
     * Metoda ktora vrati komponentu ktora sa nachadza v kontajneri s id rovnym zadanemu 
     * parametru <b>id</b>.
     * @param id Id kontajneru v ktorom hladame komponentu.
     * @return Komponenta pod danym id.
     */
    public static Component getComponentOfId(String id) {
        Collection<rpgcraft.panels.components.Container> containers = MainGameFrame.game.getChildContainers();
        for (rpgcraft.panels.components.Container cont : containers) {
            if (cont.getResource().getId().equals(id)) {
                return cont.getComponent();
            }
        }
        return null;
    }
    
    /**
     * Metoda ktora vrati vsetky kontajnery ktore sa nachadzaju v menu zadanom parametrom
     * <b>menu</b>
     * @param menu Menu v ktorom hladame kontajneri.
     * @return Kolekcia kontajnerov v menu.
     */
    public static Collection<rpgcraft.panels.components.Container> getMenuContainers(AbstractMenu menu) {
        return menu.getContainers();
    }
    
    /**
     * 
     * @param resource
     * @param menu
     * @param src
     * @param parent
     * @return 
     */
    public static Container getComponentFromResource(UiResource resource, AbstractMenu menu, Container src, Container parent) {
        Component c = null;
        
        if (src == null) {        
            src = new Container(resource, 0, 0, 0, 0, parent);
        } else {
            if (src.getComponent() != null) {
                parent.getComponent().removeComponent(src.getComponent());
                src.setParentContainer(parent);
            }
        }
        
        switch (resource.getUiType()) {
            case BAR : c = new SwingBar(src, menu);
                break;
            case BUTTON : c = new SwingImageButton(src, menu);
                break;
            case EDITTEXT : c = new SwingInputText(src, menu);
                break;
            case LIST : c = new SwingImageList(src, menu);
                break;
            case PANEL : c = new SwingImagePanel(src, menu);
                break;
            case TEXT : c = new SwingText(src, menu);
                break;  
            case IMAGE : c = new SwingImage(src, menu);
                break;
        }
        
        if (c != null) {            
            c.addActionListeners(resource.getMouseActions());   
            c.addActionListeners(resource.getKeyActions()); 
            src.setComponent(c);                                                
        }

        parent.addChild(src);        
        
        return src;
    }        
    
    public static Object getData(Component c, DataValues dataValue) {
        
        switch (dataValue) {
            case HEALTH : {
                Menu menu = c.getOriginMenu();
                if (menu instanceof GameMenu) {
                    return ((GameMenu)menu).getMap().player.getHealth();
                }
            } break;
            case MAXHEALTH : {
                Menu menu = c.getOriginMenu();
                if (menu instanceof GameMenu) {
                    return ((GameMenu)menu).getMap().player.getMaxHealth();
                }
            } break;    
            default : return null;
                
        }
        return null;
        
        
    }
    
    
    public static Object getValueOfVariable(String var) {
        return variables.get(var);
    }
    
    public static void setValueOfVariable(Object val, Object var) {
        variables.put(var.toString(), val);
    }       
    
    /**
     * 
     * @param parsedOp
     * @param e
     * @return 
     */
    public static Comparable[] getComparableValues(Object[] parsedOp, ActionEvent e) {
        Comparable[] compValues = new Comparable[2];
        Listener fst = ListenerFactory.getListener(parsedOp[0],false);
        if (fst != null) {
            fst.actionPerformed(e);
            compValues[0] = (Comparable) e.getReturnValue();
        } else {                          
            if (parsedOp[0] instanceof Integer) {
                compValues[0] = (Integer)parsedOp[0];        
            }
            if (parsedOp[0] instanceof String) {
                compValues[0] = Integer.parseInt((String)parsedOp[0]);
            }            
        }

        Listener snd = ListenerFactory.getListener(parsedOp[1], false);
        if (snd != null) {
            snd.actionPerformed(e);
            compValues[1] = (Comparable) e.getReturnValue();
        } else {                          
            if (parsedOp[1] instanceof Integer) {
                compValues[1] = (Integer)parsedOp[1];
            }
            if (parsedOp[1] instanceof String) {
                compValues[1] = Integer.parseInt((String)parsedOp[1]);
            }            
        }
        
        return compValues;
        
    }
    
    /**
     * Metoda ktora vykona akciu zadanu parametrom vytvorenim noveho Threadu s touto akciou.
     * Z akcie sa zavola metoda run. Je dolezite aby akcia mala definovany ActionEvent. Inak sa nevykona nic.
     * @param action Akcia na vykonanie
     */
    public static void execute(Action action) {        
        es.execute(new Thread(action));
    }
    
    /**
     * Metoda ktora vykona Thread zadany parametrom t.     
     * @param t Thread ktory sa vykona
     */
    public static void execute(Thread t) {
        es.execute(t);           
    }
    
}
